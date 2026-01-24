package dz.eadn.sig.mapper;

import dz.eadn.sig.dto.UserLoggedActionsDto;
import dz.eadn.sig.model.UserLoggedActions;
import org.springframework.stereotype.Component;


@Component
public class UserLoggedActionsMapper  extends CommonMapper<UserLoggedActions, UserLoggedActionsDto>{

    @Override
    protected UserLoggedActionsDto mapEntityToDto(UserLoggedActions entity) {
        UserLoggedActionsDto userLoggedActionsDto = new UserLoggedActionsDto();
        userLoggedActionsDto.setAction(entity.getAction());
        userLoggedActionsDto.setUrl(entity.getUrl());
        userLoggedActionsDto.setObject(entity.getObject());
        userLoggedActionsDto.setObjectId(entity.getObjectId());
        userLoggedActionsDto.setSqlQuery(entity.getSqlQuery());
        userLoggedActionsDto.setUrl(entity.getUrl());
        userLoggedActionsDto.setData(entity.getData());
        userLoggedActionsDto.setIpAddress(entity.getIpAddress());
        userLoggedActionsDto.setUserName(entity.getUserName());
        return userLoggedActionsDto;
    }

    @Override
    protected UserLoggedActions mapDtoToEntity(UserLoggedActionsDto userLoggedActionsDto) {
        UserLoggedActions userLoggedActions = new UserLoggedActions();
        userLoggedActions.setAction(userLoggedActionsDto.getAction());
        userLoggedActions.setUrl(userLoggedActionsDto.getUrl());
        userLoggedActions.setObject(userLoggedActionsDto.getObject());
        userLoggedActions.setObjectId(userLoggedActionsDto.getObjectId());
        userLoggedActions.setSqlQuery(userLoggedActionsDto.getSqlQuery());
        userLoggedActions.setUrl(userLoggedActionsDto.getUrl());
        userLoggedActions.setData(userLoggedActionsDto.getData());
        userLoggedActions.setIpAddress(userLoggedActionsDto.getIpAddress());
        userLoggedActions.setUserName(userLoggedActionsDto.getUserName());
        return userLoggedActions;
    }
}
