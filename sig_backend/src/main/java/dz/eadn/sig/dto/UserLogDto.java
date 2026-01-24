package dz.eadn.sig.dto;

import java.util.Date;

import dz.eadn.sig.util.WITHUUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author Achrouf Abdenour
 *
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserLogDto extends WITHUUID {
	private String username;
	private Date loginDate;
	private Date logoutDate;
	private String userIp;
	private String browserName;
	private String browserVersion;
	private String token;
	private String clientOS;
}
