package dz.eadn.sig.model;

import dz.eadn.sig.util.WITHUUID;
import lombok.*;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * @author LOKBANI Chouaib
 *
 */
@Entity
@Table(schema = "sig", name = "user_logged_actions")
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserLoggedActions extends WITHUUID {

    private String ipAddress;

    @Column(name = "data", columnDefinition = "TEXT")
    private String data;

    @Column(name = "action")
    private String action;

    @Column(name = "object")
    private String object;

    private String objectId;

    private String url;

    @Column(name = "sql_query", columnDefinition = "TEXT")
    private String sqlQuery;

    private String userName;

}
