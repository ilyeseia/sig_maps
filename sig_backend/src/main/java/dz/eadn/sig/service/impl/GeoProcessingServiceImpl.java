package dz.eadn.sig.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import dz.eadn.sig.dto.*;
import dz.eadn.sig.exceptions.GlobalException;
import dz.eadn.sig.mapper.CommonModelMapper;
import dz.eadn.sig.mapper.FieldMapper;
import dz.eadn.sig.mapper.LayerMapper;
import dz.eadn.sig.mapper.UserMapper;
import dz.eadn.sig.model.*;
import dz.eadn.sig.repository.EntityElementRepository;
import dz.eadn.sig.repository.MapRepository;
import dz.eadn.sig.repository.UserRepository;
import dz.eadn.sig.service.*;
import dz.eadn.sig.util.EntityElementWriter;
import dz.eadn.sig.util.Utils;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.TopologyException;
import org.locationtech.jts.operation.buffer.BufferOp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * @author  LOKBANI Chouaib
 *
 */
@Service
public class GeoProcessingServiceImpl implements  GeoProcessingService {

    @Autowired
    private LayerServiceImpl layerService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MapRepository mapRepository;

    @Autowired
    private CommonModelMapper cModelMapper;

    @Autowired
    private MapLayerService mapLayerService;

    @Autowired
    private LayerMapper layerMapper;

    @Autowired
    private MapService mapService;

    @Autowired
    private EntityElementRepository entityElementRepository;

    @Autowired
    private EntityElementService entityElementService;

    @Autowired
    private FieldService fieldService;


    @Autowired
    private Utils utils;

    @Override
    public List<LayerSimpleWithFieldsDto> spatialOperation(GeoProcessingDto spatialOpConfig, HttpServletResponse response) {
        if((spatialOpConfig.getOperation().equals(SpatialOperation.ST_Buffer.toString()) && spatialOpConfig.getLayerIdA() != "")  ||  (!spatialOpConfig.getOperation().equals(SpatialOperation.ST_Buffer.toString()) && spatialOpConfig.getLayerIdA() != "" && spatialOpConfig.getLayerIdB() != "")){
            LayerDto savedLayer = null;
            if(spatialOpConfig.getSelectedFields() == null || spatialOpConfig.getSelectedFields().isEmpty())
                throw new GlobalException("Vous devez sélectionner au moins un champ !");
            try {
                Layer layerA = layerService.findById(UUID.fromString(spatialOpConfig.getLayerIdA()));
                Layer layerB = null;
                List<Map<String, Object>> entityElements = new ArrayList<>();
                //Perform Spatial query
                String query = "";
                String layerAQuery = "";
                String layerBQuery = "";

                layerService.CheckIfUserHasPrivilegeOnLayer(layerA.getSlug(), spatialOpConfig.getWorkingMap(), "", "write");
                if(spatialOpConfig.getOutputFormat().equals("newLayer")){
                    layerAQuery = "select e.geom, e.properties from sig.entity_element e where e.deleted = false and st_isValid(e.geom) and  e.layer_entity_element = '" + spatialOpConfig.getLayerIdA() + "'";
                }else{
                    layerAQuery = "select e.id from sig.entity_element e where e.deleted = false and st_isValid(e.geom) and e.layer_entity_element = '" + spatialOpConfig.getLayerIdA() + "'";
                }
                if (spatialOpConfig.getLayerFilterA() != null) {
                    layerAQuery += generateCQLFilter(spatialOpConfig.getLayerFilterA(), layerA);
                }

                if(!spatialOpConfig.getOperation().equals(SpatialOperation.ST_Buffer.toString())){
                    layerB = layerService.findById(UUID.fromString(spatialOpConfig.getLayerIdB()));
                    if(layerB != null) layerService.CheckIfUserHasPrivilegeOnLayer(layerB.getSlug(), spatialOpConfig.getWorkingMap(), "", "write");
                    switch (spatialOpConfig.getOperation()){
                        case "ST_Union":
                        case "ST_SymDifference":
                        case "ST_Intersection":
                            layerBQuery = "select e.id, e.geom, e.properties from sig.entity_element e where e.deleted = false and st_isValid(e.geom) and e.layer_entity_element = '" + spatialOpConfig.getLayerIdB() + "'";
                            break;
                        case "ST_Difference":
                        case "ST_Clip":
                            layerBQuery = "select e.id, e.geom from sig.entity_element e where e.deleted = false and  st_isValid(e.geom) and e.layer_entity_element = '" + spatialOpConfig.getLayerIdB() + "'";
                            break;
                        default:
                            layerBQuery = "select e.geom from sig.entity_element e where e.deleted = false  and st_isValid(e.geom) and  st_isValid(e.geom) = 't'  and  e.layer_entity_element = '" + spatialOpConfig.getLayerIdB() + "'";

                    }
                    if (spatialOpConfig.getLayerFilterB() != null) {
                        layerBQuery += generateCQLFilter(spatialOpConfig.getLayerFilterB(), layerB);
                    }
                }

                switch (spatialOpConfig.getOperation()){
                    case "ST_Buffer":
                    case "ST_Difference":
                    case "ST_Clip":
                    case "ST_SymDifference":
                    case "ST_Union":
                    case "ST_Intersection":
                        query = layerAQuery;
                        break;
                    default:
                        query = "with geoms as ( " + layerBQuery + " ) " + layerAQuery + " and " + spatialOpConfig.getOperation() + "(geom, ST_makeValid((select st_collect(geom) from geoms)))";
                        break;
                }

                entityElements = jdbcTemplate.queryForList(query);

                //Replicate Layer
                if (!entityElements.isEmpty()) {
                    LayerDto layerDto = layerMapper.entityToDto(layerA);
                    layerDto.setFieldDtos(spatialOpConfig.getSelectedFields());
                    layerDto.setId(null);
                    String newLayerName = utils.randomString() + "____";
                    newLayerName += !spatialOpConfig.getNewLayerName().equals("") ? spatialOpConfig.getNewLayerName() : spatialOpConfig.getOperation();
                    layerDto.setName(newLayerName);
                    layerDto.getFieldDtos().forEach(f -> {
                        f.setId(null);
                    });

                    if(spatialOpConfig.getOutputFormat().equals("newLayer")){
//                        layerDto.setStyleDto(spatialOpConfig.getStyleDto());
                        if(spatialOpConfig.getOperation().equals(SpatialOperation.ST_Buffer.toString())){
                            layerDto.setTopo("MultiPolygon");
                        }
                        String username = SecurityContextHolder.getContext().getAuthentication().getName();
                        Optional<User> user = userRepository.findByUsername(username);
                        layerDto.setUserDtos(Arrays.asList(userMapper.entityToDto(user.get())));
                        savedLayer = layerService.save(layerDto);
                        //Insert Data
                        List<Map<String, Object>> entityElementLayerB = new ArrayList<>();
                        String subQuery = "";
                        String newGeom = "";
                        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
                        Map<String, Object> layerAEntityElementProperties = new LinkedHashMap<>();
                        Integer insertedElementIndex = 0;
                        List<Integer> insertedElementsList = new ArrayList<>();
                        for (Map<String, Object> e : entityElements) {
                            if(e.get("properties") != null){
                                layerAEntityElementProperties = new Gson().fromJson(
                                        String.valueOf(e.get("properties")), new TypeToken<LinkedHashMap<String, Object>>() {}.getType()
                                );
                            }
                            layerAEntityElementProperties = layerAEntityElementProperties.entrySet().stream().filter(f ->
                                    spatialOpConfig.getSelectedFields().stream().anyMatch(ss -> ss.getLayer().equals("layerA") && f.getKey().equals(ss.getSlug()))
                            ).collect(Collectors.toMap(x -> x.getKey(), x -> x.getValue() != null ? x.getValue() : ""));
                            switch (spatialOpConfig.getOperation()){
                                case "ST_Union":
                                case "ST_SymDifference":
                                case "ST_Intersection":
                                    if(entityElementLayerB.isEmpty()){
                                        entityElementLayerB = jdbcTemplate.queryForList(layerBQuery);
                                    }
                                    newGeom = e.get("geom").toString();
                                    Map<String, Object> layerBEntityElementProperties = new LinkedHashMap<>();
                                    Map<String, Object> properties;
                                    insertedElementIndex = 0;
                                    for(Map<String, Object> f: entityElementLayerB){
                                        if(f.get("properties") != null){
                                            layerBEntityElementProperties = new Gson().fromJson(
                                                    String.valueOf(f.get("properties")), new TypeToken<LinkedHashMap<String, Object>>() {}.getType()
                                            );
                                            layerBEntityElementProperties = layerBEntityElementProperties.entrySet().stream().filter(f1 ->
                                                    spatialOpConfig.getSelectedFields().stream().anyMatch(ss -> ss.getLayer().equals("layerB") && f1.getKey().equals(ss.getSlug()))
                                            ).collect(Collectors.toMap(x -> x.getKey(), x -> x.getValue()));
                                            for (FieldDto ff : spatialOpConfig.getSelectedFields().stream().filter(eee -> eee.getLayer().equals("layerB")).collect(Collectors.toList())) {
                                                if(layerAEntityElementProperties.get(ff.getSlug()) == null){
                                                    layerAEntityElementProperties.put(ff.getSlug(), layerBEntityElementProperties.get(ff.getSlug()));
                                                }else{
                                                    layerAEntityElementProperties.put(Utils.toSlug(ff.getName()), layerBEntityElementProperties.get(ff.getSlug()));
                                                }
                                            }
                                        }
                                        properties = new LinkedHashMap<>();
                                        for (Map.Entry<String, Object> entry : layerAEntityElementProperties.entrySet()) {
                                            properties.put(entry.getKey(), null);
                                        }
                                        subQuery = intersectionQuery(e.get("geom").toString(), f.get("geom").toString());
                                        String intersection = jdbcTemplate.queryForMap(subQuery).get("ST_Intersection").toString();
                                        String difference = "";
                                        //If there is an intersection
                                        if(!intersection.equals("0107000020E610000000000000")){
                                            //Generate the common intersection
                                            subQuery = differenceQuery(newGeom, intersection);
                                            newGeom = jdbcTemplate.queryForMap(subQuery).get("ST_Difference").toString();
                                            if(!spatialOpConfig.getOperation().equals(SpatialOperation.ST_SymDifference.toString())){
                                                insertStatement(savedLayer,  username, intersection, String.valueOf(ow.writeValueAsString(layerAEntityElementProperties)), true);
                                            }
                                            //Generate B without intersection
                                            if(!spatialOpConfig.getOperation().equals(SpatialOperation.ST_Intersection.toString())) {
                                                for (Map.Entry<String, Object> entry : layerBEntityElementProperties.entrySet()) {
                                                    properties.put(entry.getKey(), entry.getValue());
                                                }
                                                difference = jdbcTemplate.queryForMap(differenceQuery(f.get("geom").toString(), intersection)).get("ST_Difference").toString();
                                                if(!difference.equals("0107000020E610000000000000")){
                                                    insertStatement(savedLayer, username, difference, String.valueOf(ow.writeValueAsString(properties)), true);
                                                }
                                            }

                                        }else if(!spatialOpConfig.getOperation().equals(SpatialOperation.ST_Intersection.toString())){
                                            if(!insertedElementsList.contains(insertedElementIndex)){
                                                //Generate B
                                                for (Map.Entry<String, Object> entry : layerBEntityElementProperties.entrySet()) {
                                                    properties.put(entry.getKey(), entry.getValue());
                                                }
                                                insertStatement(savedLayer,  username, f.get("geom").toString(), String.valueOf(ow.writeValueAsString(properties)), true);
                                                insertedElementsList.add(insertedElementIndex);
                                            }

                                        }
                                        properties.clear();
                                        insertedElementIndex++;
                                    }
                                    //Generate A
                                    if(!newGeom.equals("0107000020E610000000000000") && !spatialOpConfig.getOperation().equals(SpatialOperation.ST_Intersection.toString())){
                                        for (FieldDto f: spatialOpConfig.getSelectedFields().stream().filter(ee -> ee.getLayer().equals("layerB")).collect(Collectors.toList())) {
                                            layerAEntityElementProperties.put(Utils.toSlug(f.getName()), null);
                                        }
                                        insertStatement(savedLayer, username, newGeom, String.valueOf(ow.writeValueAsString(layerAEntityElementProperties)), true);
                                        layerAEntityElementProperties.clear();
                                    }
                                    break;
                                case "ST_Difference":
                                case "ST_Clip":
                                    String q = "with geoms as (" + layerBQuery + " ) ";
                                    String geom = "";
                                    if(spatialOpConfig.getOperation().equals(SpatialOperation.ST_Difference.toString()))
                                        geom =  "ST_Difference('"+ e.get("geom") +"',  ST_makeValid((select st_collect(geom) from geoms)))";
                                    else
                                        geom =  "ST_Intersection('"+ e.get("geom") +"',  ST_makeValid((select st_collect(geom) from geoms)))";
                                    insertStatement(savedLayer, q, username,  geom, String.valueOf(ow.writeValueAsString(layerAEntityElementProperties)), false);
                                    break;
                                case "ST_Buffer":
                                    String g =  "ST_Buffer('"+ e.get("geom") +"', "+ getDistance(spatialOpConfig.getBuffer().get("unit"), Double.parseDouble(spatialOpConfig.getBuffer().get("distance"))) +", '"+ spatialOpConfig.getBuffer().get("params") +"')";
                                    insertStatement(savedLayer, username, g, String.valueOf(ow.writeValueAsString(layerAEntityElementProperties)), false);
                                    break;
                                default:
                                    insertStatement(savedLayer,  username, String.valueOf(e.get("geom")), String.valueOf(ow.writeValueAsString(layerAEntityElementProperties)), true);
                            }
                        }
                        //Attach new layer in the working map
                        dz.eadn.sig.model.Map map = mapRepository.findBySlug(spatialOpConfig.getWorkingMap());
                        MapSimpleDto mapSimpleDto = new MapSimpleDto();
                        mapSimpleDto.setId(map.getId());
                        MapLayerDto mapLayerDto = new MapLayerDto();
                        LayerSimpleDto layerSimpleDto = new LayerSimpleDto();
                        layerSimpleDto.setId(savedLayer.getId());
                        layerSimpleDto.setTopo(savedLayer.getTopo());
                        mapLayerDto.setLayer(layerSimpleDto);
                        mapLayerDto.setMap(mapSimpleDto);
                        mapLayerDto.setLayerStyle(spatialOpConfig.getLayerStyle());
                        mapLayerDto.setOrder(spatialOpConfig.getOrder());
                        mapLayerDto.setTargetTheme(spatialOpConfig.getTargetTheme());
                        List<LayerSimpleWithFieldsDto> layerSimpleWithFieldsDtos =  mapLayerService.saveAll(Arrays.asList(mapLayerDto), true);

                        //Update geoserver security policy
                        if (map.getPrivacy().equals(Privacy.PUBLIC) || map.getPrivacy().equals(Privacy.PUBLIC_WITH_LINK)) {
                            mapService.addRule(savedLayer.getSlug());
                        }
                        return layerSimpleWithFieldsDtos;
                    }else if(spatialOpConfig.getOutputFormat().equals("exportData")){
                        List<FieldDto> fields = null;
                        EntityElementWriter entityElementWriter = entityElementService.getWriter(spatialOpConfig.getExt() != null ? spatialOpConfig.getExt() : "csv");
                        response.setContentType(entityElementWriter.mimeType());

                        response.setHeader("Content-Disposition",
                                "attachment; filename=" + newLayerName + entityElementWriter.extension());

                        response.setStatus(200);
                        List<EntityElement> entityElementList = new ArrayList<>();
                        List<EntityElement> overlayerEntityElements = new ArrayList<>();
                        if(spatialOpConfig.getOperation().equals(SpatialOperation.ST_Intersection.toString()) ||
                                spatialOpConfig.getOperation().equals(SpatialOperation.ST_Union.toString()) ||
                                spatialOpConfig.getOperation().equals(SpatialOperation.ST_SymDifference.toString())){
                            List<Map<String, Object>> entityElements1 = jdbcTemplate.queryForList(layerBQuery);
                            if(!entityElements1.isEmpty()){
                                overlayerEntityElements = entityElementRepository.findAllById(entityElements1.stream().map(f -> UUID.fromString(f.get("id").toString())).collect(Collectors.toList()));
                            }
                        }
                        Geometry newGeom = null;
                        Geometry diff = null;
                        List<EntityElement> generatedEntityElements = new ArrayList<>();
                        Map<String, String> prop = new LinkedHashMap<>();
                        List<FieldDto> fieldsB = null;
                        Integer insertedElementIndex = 0;
                        List<Integer> insertedElementsList = new ArrayList<>();
                        for (Map<String, Object>  e: entityElements) {
                            fields = spatialOpConfig.getSelectedFields().stream().filter(field -> field.getVisible() && field.getLayer().equals("layerA"))
                                    .filter(f -> !f.getType().equals(FieldType.IMAGE))
                                    .filter(f -> !f.getType().equals(FieldType.CAROUSEL))
                                    .collect(Collectors.toList());
                            Optional<EntityElement> entityElement = entityElementRepository.findById(UUID.fromString(e.get("id").toString()));
                            switch (spatialOpConfig.getOperation()){
                                case "ST_Union":
                                case "ST_SymDifference":
                                case "ST_Intersection":
                                    EntityElement tmpEntityElement1 = new EntityElement();
                                    tmpEntityElement1.setProperties(new LinkedHashMap<>());
                                    EntityElement tmpEntityElement2 = new EntityElement();
                                    tmpEntityElement2.setProperties(new LinkedHashMap<>());
                                    newGeom = entityElement.get().getGeom();
                                    fieldsB =spatialOpConfig.getSelectedFields().stream().filter(field -> field.getVisible() && field.getLayer().equals("layerB"))
                                            .filter(f -> !f.getType().equals(FieldType.IMAGE))
                                            .filter(f -> !f.getType().equals(FieldType.CAROUSEL))
                                            .collect(Collectors.toList());
                                    insertedElementIndex = 0;
                                    for(EntityElement ee: overlayerEntityElements){
                                        //IF there is an intersection
                                        if(entityElement.get().getGeom().intersects(ee.getGeom())){
                                            diff = entityElement.get().getGeom().intersection(ee.getGeom());
                                            if(!spatialOpConfig.getOperation().equals(SpatialOperation.ST_SymDifference.toString())){
                                                //Generate Common
                                                tmpEntityElement1.setId(UUID.randomUUID());
                                                tmpEntityElement1.setGeom(diff);
                                                if(entityElement.get().getProperties() != null) {
                                                    fields.forEach(f -> {
                                                        if (f.getType().equals(FieldType.SELECT)) {
                                                            tmpEntityElement1.getProperties().put(f.getSlug(), entityElement.get().getProperties().get(f.getSlug()) != null && entityElement.get().getProperties().get(f.getSlug()).split(":").length > 1 ? entityElement.get().getProperties().get(f.getSlug()).split(":")[1] : "");
                                                        } else {
                                                            tmpEntityElement1.getProperties().put(f.getSlug(), entityElement.get().getProperties().get(f.getSlug()));
                                                        }
                                                    });
                                                }
                                                if(ee.getProperties() != null) {
                                                    fieldsB.forEach(f -> {
                                                        if (f.getType().equals(FieldType.SELECT)) {
                                                            tmpEntityElement1.getProperties().put(Utils.toSlug(f.getName()), ee.getProperties().get(f.getSlug()) != null && ee.getProperties().get(f.getSlug()).split(":").length > 1 ? entityElement.get().getProperties().get(f.getSlug()).split(":")[1] : "");
                                                        } else {
                                                            tmpEntityElement1.getProperties().put(Utils.toSlug(f.getName()), ee.getProperties().get(f.getSlug()));
                                                        }
                                                    });
                                                }
                                                generatedEntityElements.add(tmpEntityElement1);
                                            }

                                            if(diff != null) {
                                                newGeom = newGeom.difference(diff);
                                            }

                                            //Generate B without intersection
                                            if(!spatialOpConfig.getOperation().equals(SpatialOperation.ST_Intersection.toString())) {
                                                tmpEntityElement2.setId(UUID.randomUUID());
                                                if(ee.getProperties() != null) {
                                                    fieldsB.forEach(f -> {
                                                        if (f.getType().equals(FieldType.SELECT)) {
                                                            tmpEntityElement2.getProperties().put(Utils.toSlug(f.getName()), ee.getProperties().get(f.getSlug()) != null && ee.getProperties().get(f.getSlug()).split(":").length > 1 ? entityElement.get().getProperties().get(f.getSlug()).split(":")[1] : "");
                                                        } else {
                                                            tmpEntityElement2.getProperties().put(Utils.toSlug(f.getName()), ee.getProperties().get(f.getSlug()));
                                                        }
                                                    });
                                                }
                                                Geometry difference = ee.getGeom().difference(entityElement.get().getGeom().intersection(ee.getGeom()));
                                                if(!difference.isEmpty()){
                                                    tmpEntityElement2.setGeom(difference);
                                                    fields.forEach(f -> tmpEntityElement2.getProperties().put(f.getSlug(), ""));
                                                    generatedEntityElements.add(tmpEntityElement2);
                                                }

                                            }
                                        }else if(!spatialOpConfig.getOperation().equals(SpatialOperation.ST_Intersection.toString())){
                                            if(!insertedElementsList.contains(insertedElementIndex)){
                                                tmpEntityElement1.setId(UUID.randomUUID());
                                                tmpEntityElement1.setGeom(ee.getGeom());
                                                if(ee.getProperties() != null) {
                                                    fieldsB.forEach(f -> {
                                                        if (f.getType().equals(FieldType.SELECT)) {
                                                            tmpEntityElement1.getProperties().put(Utils.toSlug(f.getName()), ee.getProperties().get(f.getSlug()) != null && ee.getProperties().get(f.getSlug()).split(":").length > 1 ? ee.getProperties().get(f.getSlug()).split(":")[1] : "");
                                                        } else {
                                                            tmpEntityElement1.getProperties().put(Utils.toSlug(f.getName()), ee.getProperties().get(f.getSlug()));
                                                        }
                                                    });
                                                }
                                                fields.forEach(f -> tmpEntityElement1.getProperties().put(f.getSlug(), ""));
                                                generatedEntityElements.add(tmpEntityElement1);
                                                insertedElementsList.add(insertedElementIndex);
                                            }
                                        }
                                        insertedElementIndex++;
                                    }
                                    if(!newGeom.isEmpty() && !spatialOpConfig.getOperation().equals(SpatialOperation.ST_Intersection.toString())){
                                            //Generate A
                                            entityElement.get().setGeom(newGeom);
                                            fieldsB.forEach(f -> entityElement.get().getProperties().put(Utils.toSlug(f.getName()), ""));
                                    }
                                    break;
                                case "ST_Difference":
                                    if(!overlayerEntityElements.isEmpty())
                                        entityElement.get().setGeom(entityElement.get().getGeom().difference(combineIntoOneGeometry(overlayerEntityElements.stream().map(f -> f.getGeom()).collect(Collectors.toList()))));
                                    break;
                                case "ST_Clip":
                                    if(!overlayerEntityElements.isEmpty())
                                        entityElement.get().setGeom(entityElement.get().getGeom().intersection(combineIntoOneGeometry(overlayerEntityElements.stream().map(f -> f.getGeom()).collect(Collectors.toList()))));
                                    break;
                                case "ST_Buffer":
                                    switch (layerA.getTopo()){
                                        case "Point":
                                            entityElement.get().setGeom(entityElement.get().getGeom().buffer(getDistance(spatialOpConfig.getBuffer().get("unit"), Double.parseDouble(spatialOpConfig.getBuffer().get("distance"))), Integer.parseInt(spatialOpConfig.getBuffer().get("quadSegs"))));
                                            break;
                                        case "LineString":
                                            entityElement.get().setGeom(entityElement.get().getGeom().buffer(getDistance(spatialOpConfig.getBuffer().get("unit"), Double.parseDouble(spatialOpConfig.getBuffer().get("distance"))),
                                                    0,
                                                    spatialOpConfig.getBuffer().get("endCapStyle").equals("round") ? BufferOp.CAP_ROUND : spatialOpConfig.getBuffer().get("endCapStyle").equals("flat") ? BufferOp.CAP_FLAT : BufferOp.CAP_SQUARE
                                            ));
                                            break;
                                        default:
                                            entityElement.get().setGeom(entityElement.get().getGeom().buffer(getDistance(spatialOpConfig.getBuffer().get("unit"), Double.parseDouble(spatialOpConfig.getBuffer().get("distance")))));
                                    }
                                    layerDto.setTopo("MultiPolygon");
                                    break;
                                default:
                                    fields.forEach(f -> {
                                        if(f.getType().equals(FieldType.SELECT)){
                                            prop.put(f.getSlug(),  entityElement.get().getProperties().get(f.getSlug()) != null && entityElement.get().getProperties().get(f.getSlug()).split(":").length > 1 ? entityElement.get().getProperties().get(f.getSlug()).split(":")[1] : "");
                                        }else{
                                            prop.put(f.getSlug(), entityElement.get().getProperties().get(f.getSlug()));
                                        }
                                    });
                                    break;
                            }
                            if(newGeom == null || (newGeom != null && !newGeom.isEmpty()) && !spatialOpConfig.getOperation().equals(SpatialOperation.ST_Intersection.toString())){
                                entityElement.get().getProperties().putAll(prop);
                                entityElementList.add(entityElement.get());
                            }
                            prop.clear();
                        }
                        entityElementList.addAll(generatedEntityElements);
                        //For resolve fields slug conflicts
                        layerDto.getFieldDtos().forEach(f -> f.setSlug(Utils.toSlug(f.getName())));
                        entityElementWriter.writeEntityElements(layerMapper.dtoToEntity(layerDto), entityElementList, response.getOutputStream());
                    }
                }
            }catch (TopologyException e) {
            }catch (Exception e){
                if(savedLayer != null){
                    layerService.setForceDelete(true);
                    layerService.delete(savedLayer.getId());
                    layerService.setForceDelete(false);
                }
                e.getMessage();
                throw new GlobalException("Une exception de topologie s'est produite ?");
            }

        }
        return null;
    }

    public double getDistance(String unit, double perimeter) {
        switch (unit) {
            case "kilometers":
                return perimeter * 0.01;
            case "miles":
                return perimeter * 0.016;
            case "feet":
                return perimeter * 0.000003048;
            default:
                return perimeter * 0.00001;
        }
    }

    public void insertStatement(LayerDto savedLayer, String insertQuery, String username, String geom, String properties, boolean withCots){
        if(withCots){
            geom = "'" + geom + "'";
        }
        insertQuery += "INSERT INTO sig.entity_element(id, create_date, created_by, deleted, last_modified_date, modified_by, geom, properties, layer_entity_element) " +
                "VALUES ('"+UUID.randomUUID()+"', '"+ new Date() +"', '"+ username +"', "+false+", '"+ new Date() +"', '"+ username +"', " +
                geom + ", " +
                " ' "+ properties.replaceAll("'", "''") +" ', '"+savedLayer.getId()+"')";
        jdbcTemplate.execute(insertQuery);
    }

    public void insertStatement(LayerDto savedLayer, String username, String geom, String properties, boolean withCots){
        if(withCots){
            geom = "'" + geom + "'";
        }
        String insertQuery = "INSERT INTO sig.entity_element(id, create_date, created_by, deleted, last_modified_date, modified_by, geom, properties, layer_entity_element) " +
                "VALUES ('"+UUID.randomUUID()+"', '"+ new Date() +"', '"+ username +"', "+false+", '"+ new Date() +"', '"+ username +"', " +
                geom + ", " +
                " ' "+ properties.replaceAll("'", "''")  +" ', '"+savedLayer.getId()+"')";
        jdbcTemplate.execute(insertQuery);
    }

    @Override
    public String intersectionQuery(String geom1, String geom2) {
        return "SELECT ST_Intersection(ST_makeValid('"+ geom1 + "'), ST_makeValid('"+ geom2 +"'))";
    }

    @Override
    public String differenceQuery(String geom1, String geom2) {
        return "SELECT ST_Difference(ST_makeValid('"+ geom1 + "'), ST_makeValid('"+ geom2 +"'))";
    }

    public String generateCQLFilter(CommonFilter filterCriterias, Layer layer){

        String query = "";

        for(int i = 0; i < filterCriterias.getRules().size(); i++){
            Optional<Field> field = fieldService.findFieldBySlugAndLayer(filterCriterias.getRules().get(i).getField(), layer);
            if(i == 0){
                query += " and (";
            }
            query += layerService.generateQuery(field.get().getType().toString(), filterCriterias.getRules().get(i), false);
            if (filterCriterias.getRules().size() > 1 && i < filterCriterias.getRules().size() - 1) {
                query +=  " " +  filterCriterias.getCondition();
            }
            if(i == filterCriterias.getRules().size() - 1){
                query += " )";
            }
        }

        return query;
    }


    @Override
    public Geometry combineIntoOneGeometry(List<Geometry> geometryCollection ){
        Geometry all = null;
        for( Iterator<Geometry> i = geometryCollection.iterator(); i.hasNext(); ){
            Geometry geometry = i.next();
            if( geometry == null ) continue;
            if( all == null ){
                all = geometry;
            }
            else {
                all = all.union( geometry );
            }
        }
        return all;
    }

}