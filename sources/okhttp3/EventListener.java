package okhttp3;

/* access modifiers changed from: package-private */
public abstract class EventListener {
    public static final EventListener NONE = new EventListener() {
        /* class okhttp3.EventListener.AnonymousClass1 */
    };

    public interface Factory {
        EventListener create(Call call);
    }

    EventListener() {
    }

    static Factory factory(EventListener eventListener) {
        return new Factory() {
            /* class okhttp3.EventListener.AnonymousClass2 */

            @Override // okhttp3.EventListener.Factory
            public EventListener create(Call call) {
                return EventListener.this;
            }
        };
    }
}
