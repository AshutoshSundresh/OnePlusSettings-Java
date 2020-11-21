package com.oneplus.compat.exception;

public class OPRuntimeException extends RuntimeException {
    private static final long serialVersionUID = 556655665566556656L;

    public OPRuntimeException() {
    }

    public OPRuntimeException(String str) {
        super(str);
    }
}
