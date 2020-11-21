package com.oneplus.utils.reflection.utils;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;

public class ConcurrentReferenceHashMap<K, V> extends AbstractMap<K, V> implements ConcurrentMap<K, V> {
    private static final ReferenceType DEFAULT_REFERENCE_TYPE = ReferenceType.SOFT;
    private Set<Map.Entry<K, V>> entrySet;
    private final float loadFactor;
    private final ReferenceType referenceType;
    private final ConcurrentReferenceHashMap<K, V>.Segment[] segments;
    private final int shift;

    /* access modifiers changed from: protected */
    public interface Reference<K, V> {
        Entry<K, V> get();

        int getHash();

        Reference<K, V> getNext();

        void release();
    }

    public enum ReferenceType {
        SOFT,
        WEAK
    }

    /* access modifiers changed from: protected */
    public enum Restructure {
        WHEN_NECESSARY,
        NEVER
    }

    /* access modifiers changed from: private */
    public enum TaskOption {
        RESTRUCTURE_BEFORE,
        RESTRUCTURE_AFTER,
        SKIP_IF_EMPTY,
        RESIZE
    }

    protected static int calculateShift(int i, int i2) {
        int i3 = 1;
        int i4 = 0;
        while (i3 < i && i3 < i2) {
            i3 <<= 1;
            i4++;
        }
        return i4;
    }

    public ConcurrentReferenceHashMap(int i) {
        this(i, 0.75f, 16, DEFAULT_REFERENCE_TYPE);
    }

    public ConcurrentReferenceHashMap(int i, float f, int i2, ReferenceType referenceType2) {
        int i3 = 0;
        Assert.isTrue(i >= 0, "Initial capacity must not be negative");
        Assert.isTrue(f > 0.0f, "Load factor must be positive");
        Assert.isTrue(i2 > 0, "Concurrency level must be positive");
        Assert.notNull(referenceType2, "Reference type must not be null");
        this.loadFactor = f;
        int calculateShift = calculateShift(i2, 65536);
        this.shift = calculateShift;
        int i4 = 1 << calculateShift;
        this.referenceType = referenceType2;
        int i5 = (int) ((((long) (i + i4)) - 1) / ((long) i4));
        this.segments = (Segment[]) Array.newInstance(Segment.class, i4);
        while (true) {
            ConcurrentReferenceHashMap<K, V>.Segment[] segmentArr = this.segments;
            if (i3 < segmentArr.length) {
                segmentArr[i3] = new Segment(i5);
                i3++;
            } else {
                return;
            }
        }
    }

    /* access modifiers changed from: protected */
    public final float getLoadFactor() {
        return this.loadFactor;
    }

    /* access modifiers changed from: protected */
    public ConcurrentReferenceHashMap<K, V>.ReferenceManager createReferenceManager() {
        return new ReferenceManager();
    }

    /* access modifiers changed from: protected */
    public int getHash(Object obj) {
        int hashCode = obj == null ? 0 : obj.hashCode();
        int i = hashCode + ((hashCode << 15) ^ -12931);
        int i2 = i ^ (i >>> 10);
        int i3 = i2 + (i2 << 3);
        int i4 = i3 ^ (i3 >>> 6);
        int i5 = i4 + (i4 << 2) + (i4 << 14);
        return i5 ^ (i5 >>> 16);
    }

    @Override // java.util.AbstractMap, java.util.Map
    public V get(Object obj) {
        Reference<K, V> reference = getReference(obj, Restructure.WHEN_NECESSARY);
        Entry<K, V> entry = reference != null ? reference.get() : null;
        if (entry != null) {
            return entry.getValue();
        }
        return null;
    }

    public boolean containsKey(Object obj) {
        Reference<K, V> reference = getReference(obj, Restructure.WHEN_NECESSARY);
        Entry<K, V> entry = reference != null ? reference.get() : null;
        return entry != null && ObjectUtils.nullSafeEquals(entry.getKey(), obj);
    }

    /* access modifiers changed from: protected */
    public final Reference<K, V> getReference(Object obj, Restructure restructure) {
        int hash = getHash(obj);
        return getSegmentForHash(hash).getReference(obj, hash, restructure);
    }

    @Override // java.util.AbstractMap, java.util.Map
    public V put(K k, V v) {
        return put(k, v, true);
    }

    @Override // java.util.Map, java.util.concurrent.ConcurrentMap
    public V putIfAbsent(K k, V v) {
        return put(k, v, false);
    }

    private V put(K k, final V v, final boolean z) {
        return (V) doTask(k, new ConcurrentReferenceHashMap<K, V>.Task(this, new TaskOption[]{TaskOption.RESTRUCTURE_BEFORE, TaskOption.RESIZE}) {
            /* class com.oneplus.utils.reflection.utils.ConcurrentReferenceHashMap.AnonymousClass1 */

            /* JADX DEBUG: Multi-variable search result rejected for r2v0, resolved type: com.oneplus.utils.reflection.utils.ConcurrentReferenceHashMap$Entry<K, V> */
            /* JADX WARN: Multi-variable type inference failed */
            /* access modifiers changed from: protected */
            @Override // com.oneplus.utils.reflection.utils.ConcurrentReferenceHashMap.Task
            public V execute(Reference<K, V> reference, Entry<K, V> entry, ConcurrentReferenceHashMap<K, V>.Entries entries) {
                if (entry != 0) {
                    V v = (V) entry.getValue();
                    if (z) {
                        entry.setValue(v);
                    }
                    return v;
                }
                entries.add(v);
                return null;
            }
        });
    }

    @Override // java.util.AbstractMap, java.util.Map
    public V remove(Object obj) {
        return (V) doTask(obj, new ConcurrentReferenceHashMap<K, V>.Task(this, TaskOption.RESTRUCTURE_AFTER, TaskOption.SKIP_IF_EMPTY) {
            /* class com.oneplus.utils.reflection.utils.ConcurrentReferenceHashMap.AnonymousClass2 */

            /* access modifiers changed from: protected */
            @Override // com.oneplus.utils.reflection.utils.ConcurrentReferenceHashMap.Task
            public V execute(Reference<K, V> reference, Entry<K, V> entry) {
                if (entry == null) {
                    return null;
                }
                reference.release();
                return (V) ((Entry) entry).value;
            }
        });
    }

    public boolean remove(Object obj, final Object obj2) {
        return ((Boolean) doTask(obj, new ConcurrentReferenceHashMap<K, V>.Task(this, new TaskOption[]{TaskOption.RESTRUCTURE_AFTER, TaskOption.SKIP_IF_EMPTY}) {
            /* class com.oneplus.utils.reflection.utils.ConcurrentReferenceHashMap.AnonymousClass3 */

            /* access modifiers changed from: protected */
            @Override // com.oneplus.utils.reflection.utils.ConcurrentReferenceHashMap.Task
            public Boolean execute(Reference<K, V> reference, Entry<K, V> entry) {
                if (entry == null || !ObjectUtils.nullSafeEquals(entry.getValue(), obj2)) {
                    return Boolean.FALSE;
                }
                reference.release();
                return Boolean.TRUE;
            }
        })).booleanValue();
    }

    @Override // java.util.Map, java.util.concurrent.ConcurrentMap
    public boolean replace(K k, final V v, final V v2) {
        return ((Boolean) doTask(k, new ConcurrentReferenceHashMap<K, V>.Task(this, new TaskOption[]{TaskOption.RESTRUCTURE_BEFORE, TaskOption.SKIP_IF_EMPTY}) {
            /* class com.oneplus.utils.reflection.utils.ConcurrentReferenceHashMap.AnonymousClass4 */

            /* JADX DEBUG: Multi-variable search result rejected for r3v0, resolved type: com.oneplus.utils.reflection.utils.ConcurrentReferenceHashMap$Entry<K, V> */
            /* JADX WARN: Multi-variable type inference failed */
            /* access modifiers changed from: protected */
            @Override // com.oneplus.utils.reflection.utils.ConcurrentReferenceHashMap.Task
            public Boolean execute(Reference<K, V> reference, Entry<K, V> entry) {
                if (entry == 0 || !ObjectUtils.nullSafeEquals(entry.getValue(), v)) {
                    return Boolean.FALSE;
                }
                entry.setValue(v2);
                return Boolean.TRUE;
            }
        })).booleanValue();
    }

    @Override // java.util.Map, java.util.concurrent.ConcurrentMap
    public V replace(K k, final V v) {
        return (V) doTask(k, new ConcurrentReferenceHashMap<K, V>.Task(this, new TaskOption[]{TaskOption.RESTRUCTURE_BEFORE, TaskOption.SKIP_IF_EMPTY}) {
            /* class com.oneplus.utils.reflection.utils.ConcurrentReferenceHashMap.AnonymousClass5 */

            /* JADX DEBUG: Multi-variable search result rejected for r2v0, resolved type: com.oneplus.utils.reflection.utils.ConcurrentReferenceHashMap$Entry<K, V> */
            /* JADX WARN: Multi-variable type inference failed */
            /* access modifiers changed from: protected */
            @Override // com.oneplus.utils.reflection.utils.ConcurrentReferenceHashMap.Task
            public V execute(Reference<K, V> reference, Entry<K, V> entry) {
                if (entry == 0) {
                    return null;
                }
                V v = (V) entry.getValue();
                entry.setValue(v);
                return v;
            }
        });
    }

    public void clear() {
        for (ConcurrentReferenceHashMap<K, V>.Segment segment : this.segments) {
            segment.clear();
        }
    }

    public int size() {
        int i = 0;
        for (ConcurrentReferenceHashMap<K, V>.Segment segment : this.segments) {
            i += segment.getCount();
        }
        return i;
    }

    @Override // java.util.AbstractMap, java.util.Map
    public Set<Map.Entry<K, V>> entrySet() {
        if (this.entrySet == null) {
            this.entrySet = new EntrySet();
        }
        return this.entrySet;
    }

    private <T> T doTask(Object obj, ConcurrentReferenceHashMap<K, V>.Task task) {
        int hash = getHash(obj);
        return (T) getSegmentForHash(hash).doTask(hash, obj, task);
    }

    private ConcurrentReferenceHashMap<K, V>.Segment getSegmentForHash(int i) {
        ConcurrentReferenceHashMap<K, V>.Segment[] segmentArr = this.segments;
        return segmentArr[(i >>> (32 - this.shift)) & (segmentArr.length - 1)];
    }

    /* access modifiers changed from: protected */
    public final class Segment extends ReentrantLock {
        private volatile int count = 0;
        private final int initialSize;
        private final ConcurrentReferenceHashMap<K, V>.ReferenceManager referenceManager;
        private volatile Reference<K, V>[] references;
        private int resizeThreshold;

        static /* synthetic */ int access$508(Segment segment) {
            int i = segment.count;
            segment.count = i + 1;
            return i;
        }

        public Segment(int i) {
            this.referenceManager = ConcurrentReferenceHashMap.this.createReferenceManager();
            int calculateShift = 1 << ConcurrentReferenceHashMap.calculateShift(i, 1073741824);
            this.initialSize = calculateShift;
            setReferences(createReferenceArray(calculateShift));
        }

        public Reference<K, V> getReference(Object obj, int i, Restructure restructure) {
            if (restructure == Restructure.WHEN_NECESSARY) {
                restructureIfNecessary(false);
            }
            if (this.count == 0) {
                return null;
            }
            Reference<K, V>[] referenceArr = this.references;
            return findInChain(referenceArr[getIndex(i, referenceArr)], obj, i);
        }

        public <T> T doTask(final int i, final Object obj, ConcurrentReferenceHashMap<K, V>.Task task) {
            boolean hasOption = task.hasOption(TaskOption.RESIZE);
            if (task.hasOption(TaskOption.RESTRUCTURE_BEFORE)) {
                restructureIfNecessary(hasOption);
            }
            Entry<K, V> entry = null;
            if (task.hasOption(TaskOption.SKIP_IF_EMPTY) && this.count == 0) {
                return task.execute(null, null, null);
            }
            lock();
            try {
                final int index = getIndex(i, this.references);
                final Reference<K, V> reference = this.references[index];
                Reference<K, V> findInChain = findInChain(reference, obj, i);
                if (findInChain != null) {
                    entry = findInChain.get();
                }
                return task.execute(findInChain, entry, new ConcurrentReferenceHashMap<K, V>.Entries() {
                    /* class com.oneplus.utils.reflection.utils.ConcurrentReferenceHashMap.Segment.AnonymousClass1 */

                    @Override // com.oneplus.utils.reflection.utils.ConcurrentReferenceHashMap.Entries
                    public void add(V v) {
                        Segment.this.references[index] = Segment.this.referenceManager.createReference(new Entry<>(obj, v), i, reference);
                        Segment.access$508(Segment.this);
                    }
                });
            } finally {
                unlock();
                if (task.hasOption(TaskOption.RESTRUCTURE_AFTER)) {
                    restructureIfNecessary(hasOption);
                }
            }
        }

        public void clear() {
            if (this.count != 0) {
                lock();
                try {
                    setReferences(createReferenceArray(this.initialSize));
                    this.count = 0;
                } finally {
                    unlock();
                }
            }
        }

        /* access modifiers changed from: protected */
        public final void restructureIfNecessary(boolean z) {
            boolean z2 = true;
            boolean z3 = this.count > 0 && this.count >= this.resizeThreshold;
            Reference<K, V> pollForPurge = this.referenceManager.pollForPurge();
            if (pollForPurge != null || (z3 && z)) {
                lock();
                try {
                    int i = this.count;
                    Set emptySet = Collections.emptySet();
                    if (pollForPurge != null) {
                        emptySet = new HashSet();
                        while (pollForPurge != null) {
                            emptySet.add(pollForPurge);
                            pollForPurge = this.referenceManager.pollForPurge();
                        }
                    }
                    int size = i - emptySet.size();
                    boolean z4 = size > 0 && size >= this.resizeThreshold;
                    int length = this.references.length;
                    if (!z || !z4 || length >= 1073741824) {
                        z2 = false;
                    } else {
                        length <<= 1;
                    }
                    Reference<K, V>[] createReferenceArray = z2 ? createReferenceArray(length) : this.references;
                    for (int i2 = 0; i2 < this.references.length; i2++) {
                        if (!z2) {
                            createReferenceArray[i2] = null;
                        }
                        for (Reference<K, V> reference = this.references[i2]; reference != null; reference = reference.getNext()) {
                            if (!emptySet.contains(reference) && reference.get() != null) {
                                int index = getIndex(reference.getHash(), createReferenceArray);
                                createReferenceArray[index] = this.referenceManager.createReference(reference.get(), reference.getHash(), createReferenceArray[index]);
                            }
                        }
                    }
                    if (z2) {
                        setReferences(createReferenceArray);
                    }
                    this.count = Math.max(size, 0);
                } finally {
                    unlock();
                }
            }
        }

        private Reference<K, V> findInChain(Reference<K, V> reference, Object obj, int i) {
            Entry<K, V> entry;
            K key;
            while (reference != null) {
                if (reference.getHash() == i && (entry = reference.get()) != null && ((key = entry.getKey()) == obj || key.equals(obj))) {
                    return reference;
                }
                reference = reference.getNext();
            }
            return null;
        }

        private Reference<K, V>[] createReferenceArray(int i) {
            return (Reference[]) Array.newInstance(Reference.class, i);
        }

        private int getIndex(int i, Reference<K, V>[] referenceArr) {
            return (referenceArr.length - 1) & i;
        }

        private void setReferences(Reference<K, V>[] referenceArr) {
            this.references = referenceArr;
            this.resizeThreshold = (int) (((float) referenceArr.length) * ConcurrentReferenceHashMap.this.getLoadFactor());
        }

        public final int getCount() {
            return this.count;
        }
    }

    /* access modifiers changed from: protected */
    public static final class Entry<K, V> implements Map.Entry<K, V> {
        private final K key;
        private volatile V value;

        public Entry(K k, V v) {
            this.key = k;
            this.value = v;
        }

        @Override // java.util.Map.Entry
        public K getKey() {
            return this.key;
        }

        @Override // java.util.Map.Entry
        public V getValue() {
            return this.value;
        }

        @Override // java.util.Map.Entry
        public V setValue(V v) {
            V v2 = this.value;
            this.value = v;
            return v2;
        }

        public String toString() {
            return ((Object) this.key) + "=" + ((Object) this.value);
        }

        public final boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof Map.Entry)) {
                return false;
            }
            Map.Entry entry = (Map.Entry) obj;
            return ObjectUtils.nullSafeEquals(getKey(), entry.getKey()) && ObjectUtils.nullSafeEquals(getValue(), entry.getValue());
        }

        public final int hashCode() {
            return ObjectUtils.nullSafeHashCode(this.value) ^ ObjectUtils.nullSafeHashCode(this.key);
        }
    }

    /* access modifiers changed from: private */
    public abstract class Task<T> {
        private final EnumSet<TaskOption> options;

        /* access modifiers changed from: protected */
        public T execute(Reference<K, V> reference, Entry<K, V> entry) {
            return null;
        }

        public Task(ConcurrentReferenceHashMap concurrentReferenceHashMap, TaskOption... taskOptionArr) {
            this.options = taskOptionArr.length == 0 ? EnumSet.noneOf(TaskOption.class) : EnumSet.of(taskOptionArr[0], taskOptionArr);
        }

        public boolean hasOption(TaskOption taskOption) {
            return this.options.contains(taskOption);
        }

        /* access modifiers changed from: protected */
        public T execute(Reference<K, V> reference, Entry<K, V> entry, ConcurrentReferenceHashMap<K, V>.Entries entries) {
            return execute(reference, entry);
        }
    }

    /* access modifiers changed from: private */
    public abstract class Entries {
        /* JADX WARN: Failed to parse method signature: (TV)V */
        public abstract void add(Object obj);

        private Entries(ConcurrentReferenceHashMap concurrentReferenceHashMap) {
        }
    }

    private class EntrySet extends AbstractSet<Map.Entry<K, V>> {
        private EntrySet() {
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set, java.lang.Iterable
        public Iterator<Map.Entry<K, V>> iterator() {
            return new EntryIterator();
        }

        public boolean contains(Object obj) {
            if (obj == null || !(obj instanceof Map.Entry)) {
                return false;
            }
            Map.Entry entry = (Map.Entry) obj;
            Reference<K, V> reference = ConcurrentReferenceHashMap.this.getReference(entry.getKey(), Restructure.NEVER);
            Entry<K, V> entry2 = reference != null ? reference.get() : null;
            if (entry2 != null) {
                return ObjectUtils.nullSafeEquals(entry.getValue(), entry2.getValue());
            }
            return false;
        }

        public boolean remove(Object obj) {
            if (!(obj instanceof Map.Entry)) {
                return false;
            }
            Map.Entry entry = (Map.Entry) obj;
            return ConcurrentReferenceHashMap.this.remove(entry.getKey(), entry.getValue());
        }

        public int size() {
            return ConcurrentReferenceHashMap.this.size();
        }

        public void clear() {
            ConcurrentReferenceHashMap.this.clear();
        }
    }

    private class EntryIterator implements Iterator<Map.Entry<K, V>> {
        private Entry<K, V> last;
        private Entry<K, V> next;
        private Reference<K, V> reference;
        private int referenceIndex;
        private Reference<K, V>[] references;
        private int segmentIndex;

        public EntryIterator() {
            moveToNextSegment();
        }

        public boolean hasNext() {
            getNextIfNecessary();
            return this.next != null;
        }

        @Override // java.util.Iterator
        public Entry<K, V> next() {
            getNextIfNecessary();
            Entry<K, V> entry = this.next;
            if (entry != null) {
                this.last = entry;
                this.next = null;
                return entry;
            }
            throw new NoSuchElementException();
        }

        private void getNextIfNecessary() {
            while (this.next == null) {
                moveToNextReference();
                Reference<K, V> reference2 = this.reference;
                if (reference2 != null) {
                    this.next = reference2.get();
                } else {
                    return;
                }
            }
        }

        private void moveToNextReference() {
            Reference<K, V>[] referenceArr;
            Reference<K, V> reference2 = this.reference;
            if (reference2 != null) {
                this.reference = reference2.getNext();
            }
            while (this.reference == null && (referenceArr = this.references) != null) {
                int i = this.referenceIndex;
                if (i >= referenceArr.length) {
                    moveToNextSegment();
                    this.referenceIndex = 0;
                } else {
                    this.reference = referenceArr[i];
                    this.referenceIndex = i + 1;
                }
            }
        }

        private void moveToNextSegment() {
            this.reference = null;
            this.references = null;
            if (this.segmentIndex < ConcurrentReferenceHashMap.this.segments.length) {
                this.references = ConcurrentReferenceHashMap.this.segments[this.segmentIndex].references;
                this.segmentIndex++;
            }
        }

        public void remove() {
            Assert.state(this.last != null);
            ConcurrentReferenceHashMap.this.remove(this.last.getKey());
        }
    }

    /* access modifiers changed from: protected */
    public class ReferenceManager {
        private final ReferenceQueue<Entry<K, V>> queue = new ReferenceQueue<>();

        protected ReferenceManager() {
        }

        public Reference<K, V> createReference(Entry<K, V> entry, int i, Reference<K, V> reference) {
            if (ConcurrentReferenceHashMap.this.referenceType == ReferenceType.WEAK) {
                return new WeakEntryReference(entry, i, reference, this.queue);
            }
            return new SoftEntryReference(entry, i, reference, this.queue);
        }

        public Reference<K, V> pollForPurge() {
            return (Reference) this.queue.poll();
        }
    }

    /* access modifiers changed from: private */
    public static final class SoftEntryReference<K, V> extends SoftReference<Entry<K, V>> implements Reference<K, V> {
        private final int hash;
        private final Reference<K, V> nextReference;

        @Override // java.lang.ref.Reference, com.oneplus.utils.reflection.utils.ConcurrentReferenceHashMap.Reference, java.lang.ref.SoftReference
        public /* bridge */ /* synthetic */ Entry get() {
            return (Entry) super.get();
        }

        public SoftEntryReference(Entry<K, V> entry, int i, Reference<K, V> reference, ReferenceQueue<Entry<K, V>> referenceQueue) {
            super(entry, referenceQueue);
            this.hash = i;
            this.nextReference = reference;
        }

        @Override // com.oneplus.utils.reflection.utils.ConcurrentReferenceHashMap.Reference
        public int getHash() {
            return this.hash;
        }

        @Override // com.oneplus.utils.reflection.utils.ConcurrentReferenceHashMap.Reference
        public Reference<K, V> getNext() {
            return this.nextReference;
        }

        @Override // com.oneplus.utils.reflection.utils.ConcurrentReferenceHashMap.Reference
        public void release() {
            enqueue();
            clear();
        }
    }

    /* access modifiers changed from: private */
    public static final class WeakEntryReference<K, V> extends WeakReference<Entry<K, V>> implements Reference<K, V> {
        private final int hash;
        private final Reference<K, V> nextReference;

        @Override // java.lang.ref.Reference, com.oneplus.utils.reflection.utils.ConcurrentReferenceHashMap.Reference
        public /* bridge */ /* synthetic */ Entry get() {
            return (Entry) super.get();
        }

        public WeakEntryReference(Entry<K, V> entry, int i, Reference<K, V> reference, ReferenceQueue<Entry<K, V>> referenceQueue) {
            super(entry, referenceQueue);
            this.hash = i;
            this.nextReference = reference;
        }

        @Override // com.oneplus.utils.reflection.utils.ConcurrentReferenceHashMap.Reference
        public int getHash() {
            return this.hash;
        }

        @Override // com.oneplus.utils.reflection.utils.ConcurrentReferenceHashMap.Reference
        public Reference<K, V> getNext() {
            return this.nextReference;
        }

        @Override // com.oneplus.utils.reflection.utils.ConcurrentReferenceHashMap.Reference
        public void release() {
            enqueue();
            clear();
        }
    }
}
