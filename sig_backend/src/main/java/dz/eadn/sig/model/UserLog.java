/**
 * 
 */
package dz.eadn.sig.model;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import dz.eadn.sig.util.WITHUUID;
import lombok.*;
import net.minidev.json.annotate.JsonIgnore;

/**
 * @author Achrouf Abdenour
 *
 */

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@Entity
@Table(schema = "sig", name = "user_log", uniqueConstraints = {})
public class UserLog extends WITHUUID {

	private String username;
	private Date loginDate;
	private Date logoutDate;
	private String userIp;
	@Column(name = "browser_name")
	private String browserName;
	@Column(name = "browser_version")
	private String browserVersion;
	@JsonIgnore
	private String token;
	@Column(name = "client_os")
	private String clientOS;
}
