package androidx.mediarouter.media;

import android.media.MediaRoute2Info;
import java.util.function.Predicate;

/* renamed from: androidx.mediarouter.media.-$$Lambda$MediaRoute2Provider$VT-gS4VAy5vSwKN7_lZ6W4L_NPw  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MediaRoute2Provider$VTgS4VAy5vSwKN7_lZ6W4L_NPw implements Predicate {
    public static final /* synthetic */ $$Lambda$MediaRoute2Provider$VTgS4VAy5vSwKN7_lZ6W4L_NPw INSTANCE = new $$Lambda$MediaRoute2Provider$VTgS4VAy5vSwKN7_lZ6W4L_NPw();

    private /* synthetic */ $$Lambda$MediaRoute2Provider$VTgS4VAy5vSwKN7_lZ6W4L_NPw() {
    }

    @Override // java.util.function.Predicate
    public final boolean test(Object obj) {
        return MediaRoute2Provider.lambda$refreshRoutes$0((MediaRoute2Info) obj);
    }
}
