package com.oneplus.utils.reflection;

import com.oneplus.utils.reflection.utils.Assert;
import com.oneplus.utils.reflection.utils.ConcurrentReferenceHashMap;
import com.oneplus.utils.reflection.utils.ExceptionUtil;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;

public class MethodReflection {
    private static final Map<String, Method> DECLARED_METHOD_CACHE = new ConcurrentReferenceHashMap(32);

    public static Method findMethod(Class<?> cls, String str, Class<?>... clsArr) {
        Assert.notNull(cls, "Class must not be null");
        Assert.notNull(str, "Method name must not be null");
        Method method = DECLARED_METHOD_CACHE.get(cls.getName() + str + Arrays.toString(clsArr));
        if (method != null) {
            return method;
        }
        for (Class<?> cls2 = cls; cls2 != null; cls2 = cls2.getSuperclass()) {
            Method[] methods = cls2.isInterface() ? cls2.getMethods() : cls2.getDeclaredMethods();
            for (Method method2 : methods) {
                if (str.equals(method2.getName()) && (clsArr == null || Arrays.equals(clsArr, method2.getParameterTypes()))) {
                    DECLARED_METHOD_CACHE.put(cls.getName() + str + Arrays.toString(clsArr), method2);
                    return method2;
                }
            }
        }
        return null;
    }

    public static Object invokeMethod(Method method, Object obj, Object... objArr) {
        try {
            return method.invoke(obj, objArr);
        } catch (ReflectiveOperationException e) {
            ExceptionUtil.handleReflectionException(e);
            return null;
        }
    }
}
