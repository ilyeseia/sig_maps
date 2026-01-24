package dz.eadn.sig.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.transaction.Transactional;

import dz.eadn.sig.dto.*;
import dz.eadn.sig.exceptions.EntityNotFoundException;
import dz.eadn.sig.exceptions.GlobalException;
import dz.eadn.sig.mapper.*;
import dz.eadn.sig.model.*;
import dz.eadn.sig.service.*;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import dz.eadn.sig.repository.MapLayerRepository;

/**
 * @author A.LAMOUR
 *
 */
@Service
public class MapLayerServiceImpl implements MapLayerService {

	@Value("${geoserver.workspace}")
	private String workspace;

	@Autowired
	private MapLayerRepository mapLayerRepository;

	@Autowired
	private LayerStylesService layerStylesService;

	@Autowired
	private LayerService layerService;

	@Autowired
	private MapService mapService;

	@Autowired
	private CommonModelMapper<?, ?> cModelMapper;

	@Autowired
	private ThemeService themeService;

	@Autowired
	private NotificationMessagesDto messages;

	@Autowired
	private NotificationMessageService notificationMessageService;

	@Autowired
	private GeoserverSecurityService geoserverSecurityService;

	@Autowired
	private JdbcTemplate jdbcTemplate;



	@Override
	public List<LayerSimpleWithFieldsDto> saveAll(List<MapLayerDto> mapLayerDtos, Boolean superficialClone) {
		List<MapLayer> savedML = new ArrayList<>();
		List<LayerSimpleWithFieldsDto> withFieldsDtos = new ArrayList<>();
		List<LayerStyleSimpleDto> layerStyleSimpleDtos = new ArrayList<>();
		if (mapLayerDtos != null) {
			List<MapLayer> mapLayers = cModelMapper.mapList(mapLayerDtos, MapLayer.class);
			for (MapLayer mapLayer : mapLayers) {
				if (mapLayer.getMapLayerId() == null || !superficialClone) {
					MapLayer ml = new MapLayer(mapLayer.getMap(), mapLayer.getLayer(), mapLayer.getOrder(),mapLayer.getIsVisible());
					ml = mapLayerRepository.save(ml);
					try{
						layerStyleSimpleDtos.addAll(initializeStyle(mapLayerDtos, mapLayer.getMap().getId(), mapLayer.getLayer(), ml, superficialClone));
					}catch (EntityNotFoundException e){
						throw new GlobalException("ce thème n'existe plus, veuillez recharger la carte");
					}
					catch (Exception e){
						mapLayerRepository.delete(ml);
						throw new GlobalException("Une erreur inattendue s'est produite !");
					}
					savedML.add(ml);
				}
			}
			if (savedML != null) {
				List<Layer> layers = savedML.stream().map(MapLayer::getLayer).collect(Collectors.toList());
				withFieldsDtos = cModelMapper.mapList(layers, LayerSimpleWithFieldsDto.class);
			}
		}
		if(!layerStyleSimpleDtos.isEmpty()){
			for(int i = 0; i< withFieldsDtos.size(); i++){
				withFieldsDtos.get(i).setStyle(layerStyleSimpleDtos.get(i));
				withFieldsDtos.get(i).setMapLayerId(layerStyleSimpleDtos.get(i).getMapLayer());
				withFieldsDtos.get(i).setOrder(savedML.get(i).getOrder());
			}
		}
		return withFieldsDtos;
	}

	@Override
	public List<LayerStyleSimpleDto> initializeStyle(List<MapLayerDto> mapLayerDtos, UUID mapId, Layer layer, MapLayer mapLayer, Boolean superficialClone) throws EntityNotFoundException {
		List<MapLayerDto> mapLayerDtos1 = mapLayerDtos.stream().filter(m ->
				m.getMap().getId().equals(mapId) && m.getLayer().getId().equals(layer.getId())).collect(Collectors.toList());
		if(superficialClone){
			Theme theme = themeService.findById(mapLayerDtos1.get(0).getTargetTheme().getId());
			if(theme == null) throw new EntityNotFoundException("");
			LayerStyleDto layerStyleDto = null;
			if(mapLayerDtos1 != null && !mapLayerDtos1.isEmpty()){
				layerStyleDto = mapLayerDtos1.get(0).getLayerStyle();
			}
			if(layerStyleDto != null){
				LayerSimpleDto layerSimpleDto = new LayerSimpleDto();
				layerSimpleDto.setId(layer.getId());
				layerSimpleDto.setTopo(layer.getTopo());
				layerStyleDto.setMapId(mapId);
				layerStyleDto.setLayer(layerSimpleDto);
				return Arrays.asList(layerStylesService.createLayerStyle(layerStyleDto, mapLayer,theme));
			}else{
				return null;
			}
		}else{
			List<LayerStyleSimpleDto> layerStyleSimpleDtos = new ArrayList<>();
				layerStylesService.getLayerStylesInTheme(mapLayerDtos1.get(0).getMapLayerId()).forEach(s -> {
				s.setId(null);
				s.setMapLayer(mapLayer.getMapLayerId());
				s.setTheme(mapLayerDtos1.get(0).getTheme());
				layerStyleSimpleDtos.add(layerStylesService.cloneLayerStyle(s));
			});
			return layerStyleSimpleDtos;
		}
	}


	@Override
	public void removeById(UUID mapId, UUID layerId) {

//		mapLayerRepository.deleteById(new MapLayerId(mapId, layerId));
	}

	@Override
	public void updateLayerVisibility(UUID mapLayerId, Boolean visibility) {
		try{
			jdbcTemplate.execute("UPDATE sig.map_layers" +
					"\tSET is_visible='"+visibility+"' " +
					"\tWHERE map_layer_id = '"+mapLayerId+"'");
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void updateLayerOrder(int order, UUID mapLayerId) {
		jdbcTemplate.execute("UPDATE sig.map_layers\n" +
				"\tSET  layer_order='"+order+"'" +
				"\tWHERE map_layer_id='"+mapLayerId+"'");
	}

	@Override
	public List<LayerSimpleWithFieldsDto> attachLayersToMap(List<MapLayerDto> mapLayerDtos, boolean updatePrivacy) {

			List<User> users = new ArrayList<>();
		Map map = null;
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		MapSimpleDto mapSimpleDto = mapLayerDtos.get(0).getMap();
		List<LayerSimpleDto> layers = mapLayerDtos.stream().map(ml -> ml.getLayer()).collect(Collectors.toList());
		String layersName = layers.stream().map(l -> l.getName()).reduce((l1, l2) -> l1 + ',' + l2).get();

		if (mapSimpleDto != null && mapSimpleDto.getId() != null)
			map = mapService.findById(mapSimpleDto.getId());
		if (map != null) {
			users.addAll(map.getUsers());
			for (Group group : map.getGroups()) {
				users.addAll(group.getUsers());
			}
		}
		List<MapLayerDto> mapLayerDtoList = new ArrayList<>();
		mapLayerDtos.forEach(ml -> {
			if(mapService.checkIfThemeContainLayer(ml.getTargetTheme().getId(), ml.getLayer().getId())){
				mapLayerDtoList.add(ml);
			}
		});
		List<LayerSimpleWithFieldsDto> layerSimpleWithFieldsDtos = saveAll(mapLayerDtoList, true);

		if((mapSimpleDto.getPrivacy().equals(Privacy.PUBLIC) || mapSimpleDto.getPrivacy().equals(Privacy.PUBLIC_WITH_LINK)) && updatePrivacy){
			ShareMapWithOthers shareMapWithOthers = new ShareMapWithOthers();
			shareMapWithOthers.setPrivacy(mapSimpleDto.getPrivacy());
			mapService.shareMap(mapSimpleDto.getId(), shareMapWithOthers);
		}

		String message = String.format(messages.getMessages().get("NM_MAP_ATTACH_LAYER"), layersName,
				mapSimpleDto.getName(), authentication.getName());

		SystemNotification systemNotification = createSystemNotification(Transaction.ATTACH, layers);

		NotificationSimpleDto notification = createNotification(NotificationLevel.INFO, Operation.ATTACHEEMENT, message,
				systemNotification, users);

		notificationMessageService.sendNotificationMessage(notification);
		return layerSimpleWithFieldsDtos;
	}

	@Transactional
	@Override
	public List<LayerSimpleWithFieldsDto> detachLayersFromMap(List<MapLayerDto> mapLayerDtos, UUID mapId,
			UUID layerId) {

		String mapName = "";
		String layerName = "";
		List<User> users = new ArrayList<>();
		List<LayerSimpleWithFieldsDto> withFieldsDtos = null;
		// prepare and send the notification
		if (layerId != null && mapId != null) {
			Layer layer = layerService.findById(layerId);
			Map map = mapService.findById(mapId);
			if (layer != null)
				layerName = layer.getName();

			if (layer != null)
				mapName = map.getName();

			users.addAll(map.getUsers());

			for (Group group : map.getGroups()) {
				users.addAll(group.getUsers());

			}
			mapLayerDtos.stream().filter(m -> m.getMapManipulation().toString().equals(MapManipulation.DETACH.toString())).collect(Collectors.toList()).forEach(mp -> {
				layerStylesService.getLayerStylesInTheme(mp.getMapLayerId()).forEach(s -> {
					layerStylesService.deleteLayerStyle(s.getId(), true);
				});
			});
			try {
				mapLayerDtos.stream().filter(m -> m.getMapManipulation().toString().equals(MapManipulation.DETACH.toString())).collect(Collectors.toList()).forEach(ml -> {
					jdbcTemplate.execute("DELETE FROM sig.map_layers\n" +
							"\tWHERE map_layer_id = '" +  ml.getMapLayerId() + "'");
				});
				mapLayerDtos.stream().filter(m -> m.getMapManipulation().toString().equals(MapManipulation.ATTACH.toString())).collect(Collectors.toList()).forEach(ml -> {
					updateLayerOrder(ml.getOrder(), ml.getMapLayerId());
				});
				if ((map.getPrivacy().equals(Privacy.PUBLIC) || map.getPrivacy().equals(Privacy.PUBLIC_WITH_LINK)) && layer.getMaps().stream()
						.filter(m -> m.getMap().getPrivacy().equals(Privacy.PUBLIC)
								|| m.getMap().getPrivacy().equals(Privacy.PUBLIC_WITH_LINK))
						.collect(Collectors.toList()).size() == 1) {
					geoserverSecurityService.deleteLayersRules(workspace + "." + layer.getSlug() + ".r");
				}
			} catch (FeignException e) {
				if (e.status() != 1) {
					e.printStackTrace();
				}
			}
		}

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		String message = String.format(messages.getMessages().get("NM_MAP_DETACH_LAYER"), layerName, mapName,
				authentication.getName());

		SystemNotification systemNotification = createSystemNotification(Transaction.DETACH, withFieldsDtos);

		NotificationSimpleDto notification = createNotification(NotificationLevel.WARNING, Operation.DETACHEMENT,
				message, systemNotification, users);

		notificationMessageService.sendNotificationMessage(notification);

		return withFieldsDtos;
	}

	@Override
	public void sortLayers(List<MapLayerDto> mapLayerDtos) {
		mapLayerDtos.forEach(lm -> {
			updateLayerOrder(lm.getOrder(), lm.getMapLayerId());
		});
	}

	@Override
	public List<Style> getAllLayerStyles(Layer layer) {
		List<Style> styleList = new ArrayList<>();
		mapLayerRepository.findAllByLayer(layer).forEach(m -> {
			m.getStyles().forEach(s -> styleList.add(s));
		});
		return styleList;
	}

	@Override
	public List<Style> getAllMapStyles(Map map) {
		List<Style> styleList = new ArrayList<>();
		mapLayerRepository.findAllByMap(map).forEach(m -> {
			m.getStyles().forEach(s -> styleList.add(s));
		});
		return styleList;

	}

	public NotificationSimpleDto createNotification(NotificationLevel level, Operation operation, String message,
			SystemNotification systemNotification, List<User> users) {

		NotificationSimpleDto notification = new NotificationSimpleDto();

		notification.setObject(NotificationObject.Carte);
		notification.setLevel(level);
		notification.setOperation(operation);
		notification.setDestination("map");
		notification.setMessage(message);
		notification.setSystemNotification(systemNotification);
		notification.setUsers(users);

		return notification;
	}

	public SystemNotification createSystemNotification(Transaction transaction, Object object) {
		SystemNotification systemNotification = new SystemNotification();

		systemNotification.setType("maps");
		systemNotification.setTransaction(transaction);
		systemNotification.setObject(object);

		return systemNotification;
	}

}
