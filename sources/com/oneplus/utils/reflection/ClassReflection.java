package com.oneplus.utils.reflection;

import com.oneplus.utils.reflection.utils.ConcurrentReferenceHashMap;
import com.oneplus.utils.reflection.utils.ExceptionUtil;
import java.util.Map;

public class ClassReflection {
    private static final Map<String, Class> DECLARED_CLASS_CACHE = new ConcurrentReferenceHashMap(32);

    static {
        new ConcurrentReferenceHashMap(32);
    }

    public static Class findClass(String str) {
        Class<?> cls = DECLARED_CLASS_CACHE.get(str);
        if (cls != null) {
            return cls;
        }
        try {
            cls = Class.forName(str);
            DECLARED_CLASS_CACHE.put(str, cls);
            return cls;
        } catch (ReflectiveOperationException e) {
            ExceptionUtil.handleReflectionException(e);
            return cls;
        }
    }
}
