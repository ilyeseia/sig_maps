package dz.eadn.sig.dto;

import lombok.Data;

@Data
public class GeoServerRequest {
	private String url;
	private String contentType;
	private String accept;
	private String body;
	private String method;
}
