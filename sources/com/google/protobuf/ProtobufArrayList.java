package com.google.protobuf;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

/* access modifiers changed from: package-private */
public final class ProtobufArrayList<E> extends AbstractProtobufList<E> {
    private static final ProtobufArrayList<Object> EMPTY_LIST;
    private final List<E> list;

    static {
        ProtobufArrayList<Object> protobufArrayList = new ProtobufArrayList<>(new ArrayList(0));
        EMPTY_LIST = protobufArrayList;
        protobufArrayList.makeImmutable();
    }

    public static <E> ProtobufArrayList<E> emptyList() {
        return (ProtobufArrayList<E>) EMPTY_LIST;
    }

    private ProtobufArrayList(List<E> list2) {
        this.list = list2;
    }

    @Override // com.google.protobuf.Internal.ProtobufList
    public ProtobufArrayList<E> mutableCopyWithCapacity(int i) {
        if (i >= size()) {
            ArrayList arrayList = new ArrayList(i);
            arrayList.addAll(this.list);
            return new ProtobufArrayList<>(arrayList);
        }
        throw new IllegalArgumentException();
    }

    @Override // java.util.List, java.util.AbstractList
    public void add(int i, E e) {
        ensureIsMutable();
        this.list.add(i, e);
        ((AbstractList) this).modCount++;
    }

    @Override // java.util.List, java.util.AbstractList
    public E get(int i) {
        return this.list.get(i);
    }

    @Override // java.util.List, java.util.AbstractList
    public E remove(int i) {
        ensureIsMutable();
        E remove = this.list.remove(i);
        ((AbstractList) this).modCount++;
        return remove;
    }

    @Override // java.util.List, java.util.AbstractList
    public E set(int i, E e) {
        ensureIsMutable();
        E e2 = this.list.set(i, e);
        ((AbstractList) this).modCount++;
        return e2;
    }

    public int size() {
        return this.list.size();
    }
}
