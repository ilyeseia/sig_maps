package dz.eadn.sig.exceptions;

import java.io.Serializable;

public class EntityAlreadyExistsException extends RuntimeException implements Serializable {
	private static final long serialVersionUID = 1L;

	public EntityAlreadyExistsException(String message) {
		super(message);
	}
}
