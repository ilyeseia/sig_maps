package dz.eadn.sig.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import dz.eadn.sig.dto.UserLoggedActionsDto;
import dz.eadn.sig.mapper.UserLoggedActionsMapper;
import dz.eadn.sig.model.ActionType;
import dz.eadn.sig.model.UserLoggedActions;
import dz.eadn.sig.repository.UserLoggedActionsRepository;
import dz.eadn.sig.service.UserLoggedActionsService;
import dz.eadn.sig.service.common.impl.CommonServiceImpl;
import dz.eadn.sig.util.LoggingInterceptor;
import dz.eadn.sig.util.WITHUUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/***
 * &author : LOKBANI Chouaib
 */

@Service
public class UserLoggedActionsServiceImpl extends CommonServiceImpl<UserLoggedActions, UserLoggedActionsDto> implements UserLoggedActionsService {

    @Autowired
    UserLoggedActionsRepository userLoggedActionsRepository;

    @Autowired
    UserLoggedActionsMapper userLoggedActionsMapper;

    @Autowired
    LoggingInterceptor loggingInterceptor;

    public UserLoggedActionsServiceImpl() {
        super(UserLoggedActions.class);
    }

    public void createAudit() {

    }

    @Override
    public void createAudit(List<Map<String,  String>> object, Serializable id, String entityName) {
        UserLoggedActions userLoggedActions = new UserLoggedActions();
        userLoggedActions.setObject(entityName);
        userLoggedActions = loggingInterceptor.createLoggedAction(userLoggedActions, id, ActionType.EDIT);
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        try {
            userLoggedActions.setData(ow.writeValueAsString(object));
        }
        catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        userLoggedActionsRepository.save(userLoggedActions);
    }


//    @Override
//    public UserLoggedActionsDto save(UserLoggedActionsDto u) {
//        userLoggedActionsRepository.save(userLoggedActionsMapper.dtoToEntity(u));
//        return null;
//    }


}
