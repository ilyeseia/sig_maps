package dz.eadn.sig.dto;

import dz.eadn.sig.model.ActionType;
import dz.eadn.sig.util.WITHUUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserLoggedActionsDto extends WITHUUID {

    private String ipAddress;

    private String data;

    private String action;

    private String object;

    private String objectId;

    private String url;

    private String sqlQuery;

    private String userName;
}
