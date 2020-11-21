package com.google.common.eventbus;

import com.google.common.base.Preconditions;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class Subscriber {
    private final Method method;
    final Object target;

    /* access modifiers changed from: package-private */
    public void invokeSubscriberMethod(Object obj) throws InvocationTargetException {
        try {
            Method method2 = this.method;
            Object obj2 = this.target;
            Preconditions.checkNotNull(obj);
            method2.invoke(obj2, obj);
        } catch (IllegalArgumentException e) {
            throw new Error("Method rejected target/argument: " + obj, e);
        } catch (IllegalAccessException e2) {
            throw new Error("Method became inaccessible: " + obj, e2);
        } catch (InvocationTargetException e3) {
            if (e3.getCause() instanceof Error) {
                throw ((Error) e3.getCause());
            }
            throw e3;
        }
    }

    public final int hashCode() {
        return ((this.method.hashCode() + 31) * 31) + System.identityHashCode(this.target);
    }

    public final boolean equals(Object obj) {
        if (!(obj instanceof Subscriber)) {
            return false;
        }
        Subscriber subscriber = (Subscriber) obj;
        if (this.target != subscriber.target || !this.method.equals(subscriber.method)) {
            return false;
        }
        return true;
    }

    static final class SynchronizedSubscriber extends Subscriber {
        /* access modifiers changed from: package-private */
        @Override // com.google.common.eventbus.Subscriber
        public void invokeSubscriberMethod(Object obj) throws InvocationTargetException {
            synchronized (this) {
                Subscriber.super.invokeSubscriberMethod(obj);
            }
        }
    }
}
