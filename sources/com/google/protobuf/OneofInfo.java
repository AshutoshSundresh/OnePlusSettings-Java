package com.google.protobuf;

import java.lang.reflect.Field;

final class OneofInfo {
    public abstract Field getCaseField();

    public abstract Field getValueField();
}
