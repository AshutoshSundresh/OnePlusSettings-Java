package com.google.protobuf;

import com.google.protobuf.Internal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

abstract class ListFieldSchema {
    private static final ListFieldSchema FULL_INSTANCE = new ListFieldSchemaFull();
    private static final ListFieldSchema LITE_INSTANCE = new ListFieldSchemaLite();

    /* access modifiers changed from: package-private */
    public abstract void makeImmutableListAt(Object obj, long j);

    /* access modifiers changed from: package-private */
    public abstract <L> void mergeListsAt(Object obj, Object obj2, long j);

    /* access modifiers changed from: package-private */
    public abstract <L> List<L> mutableListAt(Object obj, long j);

    private ListFieldSchema() {
    }

    static ListFieldSchema full() {
        return FULL_INSTANCE;
    }

    static ListFieldSchema lite() {
        return LITE_INSTANCE;
    }

    private static final class ListFieldSchemaFull extends ListFieldSchema {
        private static final Class<?> UNMODIFIABLE_LIST_CLASS = Collections.unmodifiableList(Collections.emptyList()).getClass();

        private ListFieldSchemaFull() {
            super();
        }

        /* access modifiers changed from: package-private */
        @Override // com.google.protobuf.ListFieldSchema
        public <L> List<L> mutableListAt(Object obj, long j) {
            return mutableListAt(obj, j, 10);
        }

        /* access modifiers changed from: package-private */
        @Override // com.google.protobuf.ListFieldSchema
        public void makeImmutableListAt(Object obj, long j) {
            Object obj2;
            List list = (List) UnsafeUtil.getObject(obj, j);
            if (list instanceof LazyStringList) {
                obj2 = ((LazyStringList) list).getUnmodifiableView();
            } else if (!UNMODIFIABLE_LIST_CLASS.isAssignableFrom(list.getClass())) {
                if (!(list instanceof PrimitiveNonBoxingCollection) || !(list instanceof Internal.ProtobufList)) {
                    obj2 = Collections.unmodifiableList(list);
                } else {
                    Internal.ProtobufList protobufList = (Internal.ProtobufList) list;
                    if (protobufList.isModifiable()) {
                        protobufList.makeImmutable();
                        return;
                    }
                    return;
                }
            } else {
                return;
            }
            UnsafeUtil.putObject(obj, j, obj2);
        }

        /* JADX DEBUG: Multi-variable search result rejected for r1v10, resolved type: java.util.ArrayList */
        /* JADX WARN: Multi-variable type inference failed */
        private static <L> List<L> mutableListAt(Object obj, long j, int i) {
            LazyStringArrayList lazyStringArrayList;
            List<L> list;
            List<L> list2 = getList(obj, j);
            if (list2.isEmpty()) {
                if (list2 instanceof LazyStringList) {
                    list = new LazyStringArrayList(i);
                } else if (!(list2 instanceof PrimitiveNonBoxingCollection) || !(list2 instanceof Internal.ProtobufList)) {
                    list = new ArrayList<>(i);
                } else {
                    list = ((Internal.ProtobufList) list2).mutableCopyWithCapacity(i);
                }
                UnsafeUtil.putObject(obj, j, list);
                return list;
            }
            if (UNMODIFIABLE_LIST_CLASS.isAssignableFrom(list2.getClass())) {
                ArrayList arrayList = new ArrayList(list2.size() + i);
                arrayList.addAll(list2);
                UnsafeUtil.putObject(obj, j, arrayList);
                lazyStringArrayList = arrayList;
            } else if (list2 instanceof UnmodifiableLazyStringList) {
                LazyStringArrayList lazyStringArrayList2 = new LazyStringArrayList(list2.size() + i);
                lazyStringArrayList2.addAll((UnmodifiableLazyStringList) list2);
                UnsafeUtil.putObject(obj, j, lazyStringArrayList2);
                lazyStringArrayList = lazyStringArrayList2;
            } else if (!(list2 instanceof PrimitiveNonBoxingCollection) || !(list2 instanceof Internal.ProtobufList)) {
                return list2;
            } else {
                Internal.ProtobufList protobufList = (Internal.ProtobufList) list2;
                if (protobufList.isModifiable()) {
                    return list2;
                }
                Internal.ProtobufList mutableCopyWithCapacity = protobufList.mutableCopyWithCapacity(list2.size() + i);
                UnsafeUtil.putObject(obj, j, mutableCopyWithCapacity);
                return mutableCopyWithCapacity;
            }
            return lazyStringArrayList;
        }

        /* access modifiers changed from: package-private */
        @Override // com.google.protobuf.ListFieldSchema
        public <E> void mergeListsAt(Object obj, Object obj2, long j) {
            List list = getList(obj2, j);
            List mutableListAt = mutableListAt(obj, j, list.size());
            int size = mutableListAt.size();
            int size2 = list.size();
            if (size > 0 && size2 > 0) {
                mutableListAt.addAll(list);
            }
            if (size > 0) {
                list = mutableListAt;
            }
            UnsafeUtil.putObject(obj, j, list);
        }

        static <E> List<E> getList(Object obj, long j) {
            return (List) UnsafeUtil.getObject(obj, j);
        }
    }

    private static final class ListFieldSchemaLite extends ListFieldSchema {
        private ListFieldSchemaLite() {
            super();
        }

        /* access modifiers changed from: package-private */
        @Override // com.google.protobuf.ListFieldSchema
        public <L> List<L> mutableListAt(Object obj, long j) {
            Internal.ProtobufList protobufList = getProtobufList(obj, j);
            if (protobufList.isModifiable()) {
                return protobufList;
            }
            int size = protobufList.size();
            Internal.ProtobufList mutableCopyWithCapacity = protobufList.mutableCopyWithCapacity(size == 0 ? 10 : size * 2);
            UnsafeUtil.putObject(obj, j, mutableCopyWithCapacity);
            return mutableCopyWithCapacity;
        }

        /* access modifiers changed from: package-private */
        @Override // com.google.protobuf.ListFieldSchema
        public void makeImmutableListAt(Object obj, long j) {
            getProtobufList(obj, j).makeImmutable();
        }

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference failed for: r3v3, types: [java.util.List] */
        /* access modifiers changed from: package-private */
        @Override // com.google.protobuf.ListFieldSchema
        public <E> void mergeListsAt(Object obj, Object obj2, long j) {
            Internal.ProtobufList<E> protobufList = getProtobufList(obj, j);
            Internal.ProtobufList<E> protobufList2 = getProtobufList(obj2, j);
            int size = protobufList.size();
            int size2 = protobufList2.size();
            Internal.ProtobufList<E> protobufList3 = protobufList;
            protobufList3 = protobufList;
            if (size > 0 && size2 > 0) {
                boolean isModifiable = protobufList.isModifiable();
                Internal.ProtobufList<E> protobufList4 = protobufList;
                if (!isModifiable) {
                    protobufList4 = protobufList.mutableCopyWithCapacity(size2 + size);
                }
                protobufList4.addAll(protobufList2);
                protobufList3 = protobufList4;
            }
            if (size > 0) {
                protobufList2 = protobufList3;
            }
            UnsafeUtil.putObject(obj, j, protobufList2);
        }

        static <E> Internal.ProtobufList<E> getProtobufList(Object obj, long j) {
            return (Internal.ProtobufList) UnsafeUtil.getObject(obj, j);
        }
    }
}
