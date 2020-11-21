package com.oneplus.utils.reflection.utils;

import com.oneplus.compat.exception.OPRuntimeException;

public class ExceptionUtil {
    public static void handleReflectionException(Exception exc) {
        if (exc instanceof ReflectiveOperationException) {
            throw new OPRuntimeException(exc.getMessage());
        }
    }
}
