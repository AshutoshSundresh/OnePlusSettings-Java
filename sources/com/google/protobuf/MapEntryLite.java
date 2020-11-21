package com.google.protobuf;

import com.google.protobuf.WireFormat;
import java.io.IOException;

public class MapEntryLite<K, V> {
    private final Metadata<K, V> metadata;

    /* access modifiers changed from: package-private */
    public static class Metadata<K, V> {
        public final K defaultKey;
        public final V defaultValue;
        public final WireFormat.FieldType keyType;
        public final WireFormat.FieldType valueType;
    }

    static <K, V> void writeTo(CodedOutputStream codedOutputStream, Metadata<K, V> metadata2, K k, V v) throws IOException {
        FieldSet.writeElement(codedOutputStream, metadata2.keyType, 1, k);
        FieldSet.writeElement(codedOutputStream, metadata2.valueType, 2, v);
    }

    static <K, V> int computeSerializedSize(Metadata<K, V> metadata2, K k, V v) {
        return FieldSet.computeElementSize(metadata2.keyType, 1, k) + FieldSet.computeElementSize(metadata2.valueType, 2, v);
    }

    public int computeMessageSize(int i, K k, V v) {
        return CodedOutputStream.computeTagSize(i) + CodedOutputStream.computeLengthDelimitedFieldSize(computeSerializedSize(this.metadata, k, v));
    }

    /* access modifiers changed from: package-private */
    public Metadata<K, V> getMetadata() {
        return this.metadata;
    }
}
