package dz.eadn.sig.service.impl;

import dz.eadn.sig.dto.*;
import dz.eadn.sig.exceptions.EntityNotFoundException;
import dz.eadn.sig.exceptions.GlobalException;
import dz.eadn.sig.mapper.CommonModelMapper;
import dz.eadn.sig.mapper.LayerStylesMapper;
import dz.eadn.sig.model.*;
import dz.eadn.sig.repository.LayerStylesRepository;
import dz.eadn.sig.repository.MapLayerRepository;
import dz.eadn.sig.service.*;
import dz.eadn.sig.service.common.impl.CommonServiceImpl;
import dz.eadn.sig.util.GeoToolsService;
import dz.eadn.sig.util.SLDGeneratorImpl;
import dz.eadn.sig.util.Utils;
import feign.FeignException;
import org.geotools.styling.StyledLayerDescriptor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Service
public class LayerStylesServiceImpl extends CommonServiceImpl<Style, LayerStyleDto> implements LayerStylesService {

    public LayerStylesServiceImpl() {
        super(Style.class);
    }

    @Autowired
    private GeoserverService geoserverService;

    @Autowired
    private LayerStylesRepository layerStylesRepository;

    @Autowired
    private GeoToolsService geoToolsService;

    @Autowired
    private LayerStylesMapper layerStylesMapper;

    @Autowired
    private MapLayerService mapLayerService;

    @Autowired
    private MapLayerRepository mapLayerRepository;

    @Autowired
    private ThemeService themeService;

    @Autowired
    private CommonModelMapper<?, ?> cModelMapper;

    @Autowired
    private LayerService layerService;

    @Autowired
    private NotificationMessageService notificationMessageService;

    @Autowired
    private NotificationMessagesDto messages;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public SystemNotification createSystemNotification(Transaction transaction, Object object) {
        SystemNotification systemNotification = new SystemNotification();

        systemNotification.setType("layers");
        systemNotification.setTransaction(transaction);
        systemNotification.setObject(object);

        return systemNotification;
    }

    public NotificationSimpleDto createNotification(NotificationLevel level, Operation operation, String message,
                                                    SystemNotification systemNotification, List<User> users) {

        NotificationSimpleDto notification = new NotificationSimpleDto();

        notification.setObject(NotificationObject.Couche);
        notification.setLevel(level);
        notification.setOperation(operation);
        notification.setDestination("layer");
        notification.setMessage(message);
        notification.setSystemNotification(systemNotification);
        notification.setUsers(users);

        return notification;
    }

    @Override
    public LayerStyleDto save(LayerStyleDto layerStyleDto){
        if(layerStyleDto != null){
            if(layerStyleDto.getLayer().getId() == null || layerStyleDto.getMapId() == null){
                throw new GlobalException("les données fournies ne suffisent pas !");
            }else{
                if(layerStyleDto.getId() != null){
                    LayerStyleDto l = modelMapper.map(updateLayerStyleName(layerStyleDto), LayerStyleDto.class);
                    l.setLayer(null);
                    return l;
                }else{
                    LayerStyleDto l = modelMapper.map(createLayerStyle(layerStyleDto, null, null), LayerStyleDto.class);
                    l.setLayer(null);
                    return l;
                }
            }
        }
        return null;
    }

    @Override
    public void delete(UUID id) {
        deleteLayerStyle(id, true, true);
    }

    @Override
    public LayerStyleSimpleDto createLayerStyle(LayerStyleDto layerStyleDto, MapLayer mapLayer, Theme theme) {
        layerService.CheckIfUserHasPrivilegeOnLayer(layerStyleDto.getLayer().getSlug(), null, "CONFIGURE_LAYER_STYLE_AUTHORITY", "write");
        Style layerStyle = layerStylesMapper.dtoToEntity(layerStyleDto);
        MapLayer mapLayer1 = null;
        if(mapLayer != null){
            layerStyle.setMapLayer(mapLayer);
        }else{
            mapLayer1 = mapLayerRepository.findByMapLayerId(layerStyleDto.getMapLayer());
            if(mapLayer1 != null){
                layerStyle.setMapLayer(mapLayer1);
            }else{
                throw new GlobalException("Une erreur inattendue s'est produite");
            }
        }
        layerStyle = layerStylesRepository.save(layerStyle);
        layerStyle.setTheme(theme != null ? theme :  themeService.getDefaultMapTheme(layerStyleDto.getMapId()));
        String styleName = generateStyleName(layerStyleDto.getName(), layerStyleDto.getMapId().toString(), layerStyleDto.getLayer().getId().toString(), layerStyle.getId().toString());
        if(mapLayer == null && layerStyle.getIsDefault()){
            setDefaultLayerStyle(layerStyleDto.getMapLayer(), layerStyle.getId());
        }
        try {
            geoserverService.addStyle(
                    geoToolsService.createStyle(styleName,
                            layerStyleDto.getLayer().getTopo(),
                            layerStyleDto.getStyleConfig().getSymbologyType(),
                            layerStyleDto.getStyleConfig()));
        } catch (Exception e) {
            layerStylesRepository.delete(layerStyle);
            if(mapLayer != null){
                mapLayerRepository.delete(mapLayer);
            }
            throw new GlobalException("l'opération d'ajout de style a échoué !");
        }
        layerStyle.setName(styleName);
        layerStyle = layerStylesRepository.save(layerStyle);

        if(mapLayer != null || mapLayer1 != null) {
            List<User> users = new ArrayList<>();
            users.addAll(mapLayer != null ? mapLayer.getLayer().getUsers() : mapLayer1.getLayer().getUsers());

            for (Group group : mapLayer != null ? mapLayer.getLayer().getGroups() : mapLayer1.getLayer().getGroups()) {
                users.addAll(group.getUsers());
            }


            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            String message = String.format(messages.getMessages().get("STYLE_CREATE"), layerStyleDto.getDisplayName(),
                    authentication.getName(), layerStyleDto.getLayer().getName());

            SystemNotification systemNotification = createSystemNotification(Transaction.ADD, null);

            NotificationSimpleDto notification = createNotification(NotificationLevel.INFO, Operation.CREATION,
                    message, systemNotification, users);

            notificationMessageService.sendNotificationMessage(notification);
        }

        return modelMapper.map(layerStyle, LayerStyleSimpleDto.class);
    }

    @Override
    public ResponseEntity updateLayerStyle(LayerStyleDto layerStyleDto) {
		layerService.CheckIfUserHasPrivilegeOnLayer(layerStyleDto.getLayer().getSlug(), null, "CONFIGURE_LAYER_STYLE_AUTHORITY", "write");
        Style style = null;
	
		try {
		    style = layerStylesMapper.dtoToEntity(layerStyleDto);
            Layer layer = layerService.findBySlug(layerStyleDto.getLayer().getSlug());
            geoserverService.updateStyle(style.getName(),
                    geoToolsService.createStyle(style.getName(), layerStyleDto.getLayer().getTopo(), style.getSymbologyType(), layerStyleDto.getStyleConfig()));

			layerStylesRepository.save(style);

			// prepare notifications

			List<User> users = new ArrayList<>();

			if (layer != null) {

				users.addAll(layer.getUsers());

				for (Group group : layer.getGroups()) {
					users.addAll(group.getUsers());
				}

			}

			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

			String message = String.format(messages.getMessages().get("NM_LAYER_CHANGE_STYLE"), layerStyleDto.getDisplayName(), layerStyleDto.getLayer().getName(),
					authentication.getName());

			SystemNotification systemNotification = createSystemNotification(Transaction.UPDATE, null);

			NotificationSimpleDto notification = createNotification(NotificationLevel.INFO, Operation.CHANGEMENT_STYLE,
					message, systemNotification, users);

			notificationMessageService.sendNotificationMessage(notification);

		}catch (EntityNotFoundException e){
            throw new GlobalException("le style avec le nom "+ layerStyleDto.getName() +" n'existe plus");
        }
		catch (Exception e) {
			throw new GlobalException("L'opération de mise à jour du style a échoué");
		}
		return new ResponseEntity(modelMapper.map(style, LayerStyleSimpleDto.class), HttpStatus.ACCEPTED);
    }

    @Override
    public LayerStyleSimpleDto updateLayerStyleName(LayerStyleDto layerStyleDto) {
        layerService.CheckIfUserHasPrivilegeOnLayer(layerStyleDto.getLayer().getSlug(), null, "CONFIGURE_LAYER_STYLE_AUTHORITY", "write");
        Style style = findById(layerStyleDto.getId());
        if(style != null) {
            String oldName = style.getDisplayName();
            String newName = generateStyleName(layerStyleDto.getName(), layerStyleDto.getMapId().toString(), layerStyleDto.getLayer().getId().toString(), style.getId().toString());
            if (!style.getName().equals(newName)) {
                Document document = null;
                try {
                    StyledLayerDescriptor sld = geoToolsService.parseSld(geoserverService.getStyle(style.getName()));
                    document = SLDGeneratorImpl.convertStringToDocument(geoToolsService.generateSLD(sld));
                    NodeList nodes = document.getElementsByTagName("sld:Name");
                    if (nodes != null) {
                        for (int j = 0; j < nodes.getLength(); j++) {
                            Node elem = nodes.item(j);
                            elem.setTextContent(newName);
                        }
                    } else {
                        geoserverService.addStyle(geoToolsService.createStyle(newName,
                                layerStyleDto.getLayer().getTopo(), style.getSymbologyType(), null));
                    }
                } catch (FeignException e) {
                    if (e.status() == 404) {
                        geoserverService.addStyle(geoToolsService.createStyle(newName,
                                layerStyleDto.getLayer().getTopo(), style.getSymbologyType(), null));
                    } else {
                        throw new GlobalException("l'opération de la modification a échoué !");
                    }
                }
                try {
                    if (document != null) {
                        geoserverService.addStyle(SLDGeneratorImpl.convertDocumentToString(document));
                        geoserverService.deleteStyle(style.getName());
                    }
                } catch (FeignException e) {
                    throw new GlobalException("l'opération de la modification a échoué !");
                }
            }
            style.setName(newName);
            style.setDisplayName(layerStyleDto.getName());
            style.setIsDefault(layerStyleDto.getIsDefault());
            style = layerStylesRepository.save(style);

            List<User> users = new ArrayList<>();
            users.addAll(style.getMapLayer().getLayer().getUsers());
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            String message = String.format(messages.getMessages().get("STYLE_UPDATE"), oldName, layerStyleDto.getLayer().getName(), authentication.getName());

            SystemNotification systemNotification = createSystemNotification(Transaction.UPDATE, null);

            NotificationSimpleDto notification = createNotification(NotificationLevel.INFO, Operation.MODIFICATION,
                    message, systemNotification, users);

            notificationMessageService.sendNotificationMessage(notification);
            return modelMapper.map(style, LayerStyleSimpleDto.class);
        }else{
            throw new GlobalException("Ce style n'existe déjà");
        }
    }


    @Override
    public void deleteLayerStyle(UUID layerStyleId, Boolean deleteFromDB) {
        try {
            Style layerStyle = null;
            try{
                layerStyle = findById(layerStyleId);
            }catch (EntityNotFoundException e){
                e.printStackTrace();

            }
            if(layerStyle != null){
                geoserverService.deleteStyle(layerStyle.getName());
            }
            if(deleteFromDB){
                jdbcTemplate.execute("DELETE FROM sig.style\n" +
                        "WHERE id = '" +  layerStyleId + "'");
            }
        } catch (FeignException e) {
            // If geoserver not available
            if (e.status() == -1) {
                throw new GlobalException(
                        "Une erreur inattendue s'est produite !");
            }else if(e.status() == 404){
                if(deleteFromDB){
                    jdbcTemplate.execute("DELETE FROM sig.style\n" +
                            "WHERE id = '" + layerStyleId  + "'");
                }
            }
        }catch (Exception e){
            throw new GlobalException("Une erreur inattendue s'est produite !");
        }

    }

    @Override
    public ResponseEntity<UUID> deleteLayerStyle(UUID layerStyleId, Boolean deleteFromDB, Boolean sendNotifications) {
        Style layerStyle = findById(layerStyleId);
        if(layerStyle != null) {
            LayerStyleSimpleDto layerStyleSimpleDto = modelMapper.map(layerStyle, LayerStyleSimpleDto.class);
            List<User> users = new ArrayList<>();
            users.addAll(layerStyle.getMapLayer().getLayer().getUsers());
            String styleName = layerStyle.getDisplayName();
            String layerName = layerStyle.getMapLayer().getLayer().getName();
            for (Group group : layerStyle.getMapLayer().getLayer().getGroups()) {
                users.addAll(group.getUsers());
            }
            try {
                geoserverService.deleteStyle(layerStyle.getName());
            } catch (FeignException e) {
                // If geoserver not available
                if (e.status() == -1) {
                    throw new GlobalException(
                            "Une erreur inattendue s'est produite !");
                }
            }
            if (deleteFromDB) {
                layerStylesRepository.delete(layerStyle);
            }
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            String message = String.format(messages.getMessages().get("STYLE_DELETE"), styleName, layerName, authentication.getName());

            SystemNotification systemNotification = createSystemNotification(Transaction.DELETE, layerStyleSimpleDto);

            NotificationSimpleDto notification = createNotification(NotificationLevel.SEVERE, Operation.SUPPRISSION,
                    message, systemNotification, users);

            notificationMessageService.sendNotificationMessage(notification);
            return new ResponseEntity<UUID>(layerStyleId, HttpStatus.OK);
        }else{
            throw new GlobalException("Ce style n'existe déjà");
        }
    }

    @Override
    public void deleteLayerStyle(Style style, Boolean deleteFromDB) {
        try {
            geoserverService.deleteStyle(style.getName());
        } catch (FeignException e) {
            // If geoserver not available
            if (e.status() == -1) {
                throw new GlobalException(
                        "Une erreur inattendue s'est produite !");
            }
        }
        if(deleteFromDB){
            layerStylesRepository.delete(style);
        }
    }

    @Override
    public void deleteStylesByLayer(Layer layer) {
        mapLayerService.getAllLayerStyles(layer).forEach(s ->
                deleteLayerStyle(s.getId(), false));
    }

    @Override
    public void deleteStylesByMap(Map map) {
        mapLayerService.getAllMapStyles(map).forEach(s -> deleteLayerStyle(s.getId(), false));
    }

    @Override
    public void setDefaultLayerStyle(UUID mapLayerId, UUID styleId) {
        try{
            String query = "UPDATE sig.style\n" +
                    "\tSET  is_default=false\n" +
                    "\tWHERE id <> '" + styleId + "' and layer_map_style = '" + mapLayerId + "'";
            jdbcTemplate.execute(query);
            jdbcTemplate.execute("UPDATE sig.style\n" +
                    "SET  is_default=true\n" +
                    "WHERE id = '" + styleId + "'");
        }catch (Exception e){
            throw  new RuntimeException("Une erreur inattendue s'est produite !");
         }
    }

    @Override
    public List<LayerStyleSimpleDto> getDefaultStyleInLayerMap(List<UUID> mapLayers) {
        return cModelMapper.mapList(layerStylesRepository.findAllByMapLayer_MapLayerIdInAndIsDefaultTrue(mapLayers), LayerStyleSimpleDto.class);
    }

    @Override
    public List<LayerStyleSimpleDto> getLayerStylesInTheme(String layerSlug, UUID mapLayerId) {
//        layerService.CheckIfUserHasPrivilegeOnLayer(layerSlug, null, "CONFIGURE_LAYER_STYLE_AUTHORITY", "write");
        return cModelMapper.mapList(layerStylesRepository.findAllByMapLayer_MapLayerId(mapLayerId), LayerStyleSimpleDto.class);
    }

    @Override
    public List<LayerStyleDto> getLayerStylesInTheme(UUID mapLayerId) {
        return cModelMapper.mapList(layerStylesRepository.findAllByMapLayer_MapLayerId(mapLayerId), LayerStyleDto.class);
    }


    @Override
    public String generateStyleName(String name, String mapId, String layerId, String styleId) {
        return mapId + "_" + layerId + "_" + styleId + "__" +  Utils.toSlug(name);
    }

    @Override
    public LayerStyleSimpleDto cloneLayerStyle(LayerStyleDto layerStyleDto) {
        Style s = layerStylesMapper.dtoToEntity(layerStyleDto);
        s.setMapLayer(mapLayerRepository.findByMapLayerId(layerStyleDto.getMapLayer()));
        s.setDisplayName(layerStyleDto.getDisplayName());
        s.setStyle(layerStyleDto.getStyle());
        Style style1 = layerStylesRepository.save(s);
        String newName = generateStyleName(layerStyleDto.getDisplayName(), layerStyleDto.getMapId().toString(), layerStyleDto.getLayer().getId().toString(), s.getId().toString());
        Document document = null;
        try {
            StyledLayerDescriptor sld = geoToolsService.parseSld(geoserverService.getStyle(layerStyleDto.getName()));
            document = SLDGeneratorImpl.convertStringToDocument(geoToolsService.generateSLD(sld));
            NodeList nodes = document.getElementsByTagName("sld:Name");
            if (nodes != null) {
                for (int j = 0; j < nodes.getLength(); j++) {
                    Node elem = nodes.item(j);
                    elem.setTextContent(newName);
                }
            }
            if (document != null) {
                geoserverService.addStyle(SLDGeneratorImpl.convertDocumentToString(document));
            }
            style1.setName(newName);
            layerStylesRepository.save(style1);
            return modelMapper.map(style1, LayerStyleSimpleDto.class);
        } catch (FeignException e) {
            layerStylesRepository.delete(style1);
            throw new GlobalException("l'opération de la modification a échoué !");
        }
    }
}