package androidx.mediarouter.media;

import java.util.Objects;
import java.util.function.Predicate;

/* renamed from: androidx.mediarouter.media.-$$Lambda$jMO9OfSzscMxGho8zZuPtPiQlPo  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$jMO9OfSzscMxGho8zZuPtPiQlPo implements Predicate {
    public static final /* synthetic */ $$Lambda$jMO9OfSzscMxGho8zZuPtPiQlPo INSTANCE = new $$Lambda$jMO9OfSzscMxGho8zZuPtPiQlPo();

    private /* synthetic */ $$Lambda$jMO9OfSzscMxGho8zZuPtPiQlPo() {
    }

    @Override // java.util.function.Predicate
    public final boolean test(Object obj) {
        return Objects.nonNull((MediaRouteDescriptor) obj);
    }
}
