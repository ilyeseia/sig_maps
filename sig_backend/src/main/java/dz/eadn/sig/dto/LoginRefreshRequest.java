package dz.eadn.sig.dto;

import lombok.Data;

@Data
public class LoginRefreshRequest {
	private String username;
	private String refreshToken;
}
