package com.google.android.setupdesign.items;

import android.content.Context;
import android.util.AttributeSet;
import android.view.InflateException;
import java.lang.reflect.Constructor;
import java.util.HashMap;

public abstract class ReflectionInflater<T> extends SimpleInflater<T> {
    private static final Class<?>[] CONSTRUCTOR_SIGNATURE = {Context.class, AttributeSet.class};
    private static final HashMap<String, Constructor<?>> constructorMap = new HashMap<>();
    private final Context context;
    private String defaultPackage;
    private final Object[] tempConstructorArgs = new Object[2];

    protected ReflectionInflater(Context context2) {
        super(context2.getResources());
        this.context = context2;
    }

    public final T createItem(String str, String str2, AttributeSet attributeSet) {
        String concat = (str2 == null || str.indexOf(46) != -1) ? str : str2.concat(str);
        Constructor<?> constructor = constructorMap.get(concat);
        if (constructor == null) {
            try {
                constructor = this.context.getClassLoader().loadClass(concat).getConstructor(CONSTRUCTOR_SIGNATURE);
                constructor.setAccessible(true);
                constructorMap.put(str, constructor);
            } catch (Exception e) {
                throw new InflateException(attributeSet.getPositionDescription() + ": Error inflating class " + concat, e);
            }
        }
        this.tempConstructorArgs[0] = this.context;
        this.tempConstructorArgs[1] = attributeSet;
        T t = (T) constructor.newInstance(this.tempConstructorArgs);
        this.tempConstructorArgs[0] = null;
        this.tempConstructorArgs[1] = null;
        return t;
    }

    /* access modifiers changed from: protected */
    @Override // com.google.android.setupdesign.items.SimpleInflater
    public T onCreateItem(String str, AttributeSet attributeSet) {
        return createItem(str, this.defaultPackage, attributeSet);
    }

    public void setDefaultPackage(String str) {
        this.defaultPackage = str;
    }
}
