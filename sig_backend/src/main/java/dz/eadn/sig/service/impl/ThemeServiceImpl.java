package dz.eadn.sig.service.impl;

import dz.eadn.sig.dto.*;
import dz.eadn.sig.exceptions.GlobalException;
import dz.eadn.sig.mapper.CommonModelMapper;
import dz.eadn.sig.mapper.ThemeMapper;
import dz.eadn.sig.model.*;
import dz.eadn.sig.repository.MapLayerRepository;
import dz.eadn.sig.repository.ThemeRepository;
import dz.eadn.sig.service.*;
import dz.eadn.sig.service.common.impl.CommonServiceImpl;
import dz.eadn.sig.util.GeoToolsService;
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

import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ThemeServiceImpl extends CommonServiceImpl<Theme, ThemeDto> implements ThemeService {

    public ThemeServiceImpl() {
        super(Theme.class);
    }

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ThemeMapper themeMapper;

    @Autowired
    private CommonModelMapper<?, ?> cModelMapper;

    @Autowired
    ThemeRepository themeRepository;

    @Autowired
    private MapService mapService;

    @Autowired
    private MapLayerService mapLayerService;

    @Autowired
    private LayerStylesService layerStylesService;

    @Autowired
    private MapLayerRepository mapLayerRepository;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    private NotificationMessagesDto messages;

    @Autowired
    private NotificationMessageService notificationMessageService;

    public SystemNotification createSystemNotification(Transaction transaction, Object object) {
        SystemNotification systemNotification = new SystemNotification();

        systemNotification.setType("themes");
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
        notification.setDestination("themes");
        notification.setMessage(message);
        notification.setSystemNotification(systemNotification);
        notification.setUsers(users);

        return notification;
    }

    @Override
    public ThemeDto save(ThemeDto themeDto) {
        if (themeDto != null) {
            if (themeDto.getId() != null) {
                ThemeDto t = updateTheme(themeDto);
                t.setMap(null);
                return t;
            } else {
                ThemeDto t = createTheme(themeDto);
                t.setMap(null);
                return t;
            }
        }
        return null;
    }

    @Transactional
    @Override
    public ThemeDto createTheme(ThemeDto themeDto) {
        if (themeRepository.countByMap_IdAndName(themeDto.getMap().getId(), themeDto.getName()) == 0) {
            Map map = mapService.findById(themeDto.getMap().getId());
            if (map != null) {
                Theme theme = themeMapper.dtoToEntity(themeDto);
                theme = themeRepository.save(theme);
                if (!themeDto.getSnapshotType().equals("empty")) {
                    try {
                        String query = mapService.buildLayersMapQuery();
                        List<java.util.Map<String, Object>> layersMaps = jdbcTemplate.queryForList(query,
                                themeDto.getMap().getId());
                        List<MapLayerDto> mapLayerDtos = new ArrayList<>();
                        Theme finalTheme = theme;
                        layersMaps.forEach(lm -> {
                            MapLayerDto mapLayerDto = new MapLayerDto();
                            LayerSimpleDto layerSimpleDto = new LayerSimpleDto();
                            layerSimpleDto.setId(UUID.fromString(lm.get("layers_id").toString()));

                            String q = "select l.topo from sig.layer l where l.id  =?";
                            layerSimpleDto.setTopo((String) jdbcTemplate.queryForObject(
                                    q, new Object[] { UUID.fromString(lm.get("layers_id").toString()) }, String.class));
                            mapLayerDto.setLayer(layerSimpleDto);
                            mapLayerDto.setMap(modelMapper.map(map, MapSimpleDto.class));
                            if (themeDto.getSnapshotType().equals("layersAndStyles")) {
                                mapLayerDto.setMapLayerId(UUID.fromString(lm.get("map_layer_id").toString()));
                            }
                            mapLayerDto.setLayerStyle(themeDto.getLayerStyle());
                            mapLayerDto.setMapManipulation(MapManipulation.ATTACH);
                            mapLayerDto.setOrder(Integer.parseInt(lm.get("layer_order").toString()));
                            mapLayerDto.setIsVisible(Boolean.parseBoolean(lm.get("is_visible").toString()));
                            mapLayerDto.setTheme(finalTheme);
                            if (themeDto.getSnapshotType().equals("layers")) {
                                mapLayerDto.setTargetTheme(themeMapper.entityToDto(finalTheme));
                            }
                            mapLayerDtos.add(mapLayerDto);
                        });
                        if (themeDto.getSnapshotType().equals("layers")) {
                            mapLayerService.saveAll(mapLayerDtos, true);
                        } else {
                            mapLayerService.saveAll(mapLayerDtos, false);
                        }
                    } catch (Exception e) {
                        themeRepository.delete(theme);
                        throw new GlobalException("Une erreur inattendue s'est produite !");
                    }
                }

                List<User> users = new ArrayList<>();

                users.addAll(map.getUsers());

                for (Group group : map.getGroups()) {
                    users.addAll(group.getUsers());
                }

                ThemeDto savedThemeDto = modelMapper.map(theme, ThemeDto.class);

                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                String message = String.format(messages.getMessages().get("THEME_CREATE"), themeDto.getName(),
                        authentication.getName(), map.getName());

                SystemNotification systemNotification = createSystemNotification(Transaction.ADD, savedThemeDto);

                NotificationSimpleDto notification = createNotification(NotificationLevel.INFO, Operation.CREATION,
                        message,
                        systemNotification, users);

                notificationMessageService.sendNotificationMessage(notification);

                return savedThemeDto;
            } else {
                throw new GlobalException("Ce thème existe déjà");
            }
        } else {
            throw new GlobalException("l'opération d'ajout de theme a échoué ");
        }
    }

    @Override
    public ThemeDto updateTheme(ThemeDto themeDto) {
        Theme theme = findById(themeDto.getId());
        if (theme != null) {
            theme.setName(themeDto.getName());
            theme.setIsDefault(themeDto.getIsDefault());
            if (themeDto.getIsDefault()) {
                setDefaultMapTheme(theme.getId(), themeDto.getMap().getId());
            }
            List<User> users = new ArrayList<>();
            users.addAll(theme.getMap().getUsers());

            for (Group group : theme.getMap().getGroups()) {
                users.addAll(group.getUsers());
            }

            ThemeDto savedThemeDto = modelMapper.map(theme, ThemeDto.class);

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            String message = String.format(messages.getMessages().get("THEME_UPDATE"), themeDto.getName(),
                    theme.getMap().getName(), authentication.getName());

            SystemNotification systemNotification = createSystemNotification(Transaction.UPDATE, savedThemeDto);

            NotificationSimpleDto notification = createNotification(NotificationLevel.INFO, Operation.MODIFICATION,
                    message,
                    systemNotification, users);

            notificationMessageService.sendNotificationMessage(notification);
            return savedThemeDto;
        } else {
            throw new GlobalException("Ce thème n'existe déjà");
        }
    }

    @Override
    public ResponseEntity<?> deleteTheme(UUID themeId, UUID mapId) {
        Theme theme = findById(themeId);
        ThemeDto themeDto = modelMapper.map(theme, ThemeDto.class);
        List<User> users = new ArrayList<>();
        users.addAll(theme.getMap().getUsers());

        for (Group group : theme.getMap().getGroups()) {
            users.addAll(group.getUsers());
        }
        String themeName = theme.getName();
        String mapName = theme.getMap().getName();
        theme.getStyles().forEach(s -> {
            layerStylesService.deleteLayerStyle(s, true);
        });
        themeRepository.delete(theme);
        theme.getStyles().forEach(s -> {
            mapLayerRepository.delete(s.getMapLayer());
        });

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String message = String.format(messages.getMessages().get("THEME_DELETE"), themeName, mapName,
                authentication.getName());

        SystemNotification systemNotification = createSystemNotification(Transaction.DELETE, themeDto);

        NotificationSimpleDto notification = createNotification(NotificationLevel.SEVERE, Operation.SUPPRISSION,
                message,
                systemNotification, users);

        notificationMessageService.sendNotificationMessage(notification);
        return new ResponseEntity<UUID>(themeId, HttpStatus.OK);
    }

    @Override
    public Theme getDefaultMapTheme(UUID map) {
        return themeRepository.findByMap_IdAndIsDefaultTrue(map);
    }

    public void setDefaultMapTheme(UUID themeId, UUID mapId) {
        try {
            String query = "UPDATE sig.theme\n" +
                    "\tSET  is_default=false\n" +
                    "\tWHERE id <> '" + themeId + "' and theme_map = '" + mapId + "'";
            jdbcTemplate.execute(query);
            jdbcTemplate.execute("UPDATE sig.theme\n" +
                    "SET  is_default=true\n" +
                    "WHERE id = '" + themeId + "'");
        } catch (Exception e) {
            throw new RuntimeException("Une erreur inattendue s'est produite !");
        }
    }

    @Override
    public List<ThemeSimpleDto> findAllMapThemes(UUID mapId) {
        return cModelMapper.mapList(themeRepository.findAllByMap_Id(mapId), ThemeSimpleDto.class);
    }
}
