package androidx.mediarouter.media;

import android.media.MediaRoute2Info;
import java.util.function.Function;

/* renamed from: androidx.mediarouter.media.-$$Lambda$853YVfaGw0-G4oNUYI6Z1ujaq6k  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$853YVfaGw0G4oNUYI6Z1ujaq6k implements Function {
    public static final /* synthetic */ $$Lambda$853YVfaGw0G4oNUYI6Z1ujaq6k INSTANCE = new $$Lambda$853YVfaGw0G4oNUYI6Z1ujaq6k();

    private /* synthetic */ $$Lambda$853YVfaGw0G4oNUYI6Z1ujaq6k() {
    }

    @Override // java.util.function.Function
    public final Object apply(Object obj) {
        return MediaRouter2Utils.toMediaRouteDescriptor((MediaRoute2Info) obj);
    }
}
