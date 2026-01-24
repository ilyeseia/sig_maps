package dz.eadn.sig.service;

import dz.eadn.sig.dto.UserLoggedActionsDto;
import dz.eadn.sig.model.UserLoggedActions;
import dz.eadn.sig.service.common.CommonService;
import dz.eadn.sig.util.WITHUUID;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public interface UserLoggedActionsService extends CommonService<UserLoggedActions, UserLoggedActionsDto> {

    public void createAudit(List<Map<String, String>> object, Serializable id, String entityName);
}
