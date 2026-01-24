package dz.eadn.sig.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import dz.eadn.sig.mapper.UserLoggedActionsMapper;
import dz.eadn.sig.model.*;
import dz.eadn.sig.service.UserLogService;
import dz.eadn.sig.service.UserLoggedActionsService;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.*;
import org.hibernate.type.Type;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.io.Serializable;
import java.util.*;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class LoggingInterceptor extends EmptyInterceptor {

    private static final String ATTRIBUTE = "attribute";
    private static final String OLD_VALUE = "oldValue";
    private static final String NEW_VALUE = "newValue";
    private static final String ADDED_VALUES = "addedValues";
    private static final String REMOVED_VALUES = "removedValues";
    private static final String SYSTEM = "SYSTEM";


    @Override
    public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) {
        if(!((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getMethod().equals("DELETE") &&  !(entity instanceof UserLoggedActions) && !(entity instanceof UserLog) && !(entity instanceof UserNotification)){
            List<HashMap<String, String>> log = new ArrayList<>();
            UserLoggedActions userLoggedActions = new UserLoggedActions();
            for (int i = 0; i < propertyNames.length; i++) {
                if (currentState[i] == null || previousState[i] == null) {
                    continue;
                } else {
                    if (!currentState[i].equals(previousState[i])) {
                        HashMap<String, String> logAttr = new LinkedHashMap<>();
                        logAttr.put(ATTRIBUTE, propertyNames[i]);
                        logAttr.put(OLD_VALUE, previousState[i].toString());
                        logAttr.put(NEW_VALUE, currentState[i].toString());
                        log.add(logAttr);
                    }
                }
            }
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            try {
                userLoggedActions.setObject(entity.getClass().getSimpleName());
                userLoggedActions.setData(ow.writeValueAsString(log));
                BeanUtil.getBean(UserLoggedActionsService.class).save(BeanUtil.getBean(UserLoggedActionsMapper.class).entityToDto(createLoggedAction(userLoggedActions, id, ActionType.EDIT)));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    @Override
    public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
        if(checking(entity)){
            BeanUtil.getBean(UserLoggedActionsService.class).save(BeanUtil.getBean(UserLoggedActionsMapper.class).entityToDto(createLoggedAction(setData(entity), id, ActionType.CREATE)));
        }
        return super.onSave(entity, id, state, propertyNames, types);
    }

    @Override
    public void onDelete(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
        if(checking(entity)){
            BeanUtil.getBean(UserLoggedActionsService.class).save(BeanUtil.getBean(UserLoggedActionsMapper.class).entityToDto(createLoggedAction(setData(entity), id, ActionType.DELETE)));
        }
    }

    public boolean checking(Object entity){
        if(!(entity instanceof UserLoggedActions) && (!(entity instanceof UserLayerFilter))  && !(entity instanceof UserLog) && !(entity instanceof UserNotification)){
            if((((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getRequestURI().contains("entityelements/import") && entity instanceof EntityElement ||
                    (((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getRequestURI().startsWith("/api/v1.0/layers") && entity instanceof EntityElement) ) ){
                return false;
            }
            return true;
        }
        return false;
    }

    public UserLoggedActions setData(Object entity) {
        UserLoggedActions userLoggedActions = new UserLoggedActions();
        userLoggedActions.setObject(entity.getClass().getSimpleName());
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        Map<String, String> properties = null;
        try {
            properties = convertComplexObject(entity);
            userLoggedActions.setData(ow.writeValueAsString(properties));

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
         catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return userLoggedActions;
    }

    private Map<String, String> convertComplexObject(Object object) throws IllegalAccessException {
        Map<String, String> properties = new LinkedHashMap<>();
        //to get the list of private fields from the object
        List<Field> privateFields = Arrays
                .stream(object.getClass().getDeclaredFields())
                .filter(field -> Modifier.isPrivate(field.getModifiers()))
                .collect(Collectors.toList());

        for (Field field : privateFields) {
            field.setAccessible(true); // to make the field public instead of private .
            Object value = field.get(object);
            if (value != null) {
                if (value instanceof Collection) {
                    List<String> ids = new ArrayList<>();
                    ((Collection) value)
                            .stream(    )
                            .forEach(obj -> Arrays
                                    .stream(obj.getClass().getSuperclass().getDeclaredFields())
                                    .findFirst().ifPresent(field1 -> {
                                        field1.setAccessible(true);
                                        try {
                                            Optional.ofNullable(field1.get(obj))
                                                    .map(v1 -> v1.toString())
                                                    .ifPresent(ids::add);
                                        } catch (IllegalAccessException e) {
                                            e.printStackTrace();
                                        }
                                    }));
                    properties.put(field.getName(), ids.toString());
                } else {
                    properties.put(field.getName(), value.toString());
                }
            }
        }
        return properties;
    }

    public UserLoggedActions createLoggedAction(UserLoggedActions userLoggedActions, Serializable id, ActionType type) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        userLoggedActions.setUserName(authentication.getName());
        userLoggedActions.setAction(type.toString());
        userLoggedActions.setObjectId(id.toString());
        userLoggedActions.setIpAddress(BeanUtil.getBean(UserLogService.class).getClientIpAddr(((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest()));
        userLoggedActions.setUrl(((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getRequestURI());
        return userLoggedActions;
    }
    @Override
    public void onCollectionRemove(Object collection, Serializable key) throws CallbackException {
        System.out.println(collection);
    }

    @Override
    public void onCollectionRecreate(Object collection, Serializable key) throws CallbackException {
        System.out.println(collection);

    }

    @Override
    public void onCollectionUpdate(Object collection, Serializable key) throws CallbackException {

        System.out.println(collection);

    }

}