package dz.eadn.sig.exceptions;

import java.io.Serializable;

public class UnauthorizedException extends RuntimeException implements Serializable {
	private static final long serialVersionUID = 1L;

	public UnauthorizedException(String message) {
		super(message);
	}
}
