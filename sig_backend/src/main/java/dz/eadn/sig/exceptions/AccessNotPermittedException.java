package dz.eadn.sig.exceptions;

import java.io.Serializable;

public class AccessNotPermittedException extends RuntimeException implements Serializable {
    private static final long serialVersionUID = 1L;

    public AccessNotPermittedException(String message) {
        super(message);
    }
}
