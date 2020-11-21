package com.google.protobuf;

/* access modifiers changed from: package-private */
public interface SchemaFactory {
    <T> Schema<T> createSchema(Class<T> cls);
}
