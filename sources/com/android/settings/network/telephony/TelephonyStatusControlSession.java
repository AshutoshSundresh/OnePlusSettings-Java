package com.android.settings.network.telephony;

import android.util.Log;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.utils.ThreadUtils;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Function;

public class TelephonyStatusControlSession implements AutoCloseable {
    private Collection<AbstractPreferenceController> mControllers;
    private Future<Boolean> mResult;

    public static class Builder {
        private Collection<AbstractPreferenceController> mControllers;

        public Builder(Collection<AbstractPreferenceController> collection) {
            this.mControllers = collection;
        }

        public TelephonyStatusControlSession build() {
            return new TelephonyStatusControlSession(this.mControllers);
        }
    }

    private TelephonyStatusControlSession(Collection<AbstractPreferenceController> collection) {
        this.mControllers = collection;
        this.mResult = ThreadUtils.postOnBackgroundThread(new Callable(collection) {
            /* class com.android.settings.network.telephony.$$Lambda$TelephonyStatusControlSession$SaO6VGeXkafOF1TmoDIezgH8J1M */
            public final /* synthetic */ Collection f$1;

            {
                this.f$1 = r2;
            }

            @Override // java.util.concurrent.Callable
            public final Object call() {
                return TelephonyStatusControlSession.this.lambda$new$0$TelephonyStatusControlSession(this.f$1);
            }
        });
    }

    @Override // java.lang.AutoCloseable
    public void close() {
        try {
            this.mResult.get();
        } catch (InterruptedException | ExecutionException e) {
            Log.e("TelephonyStatusControlSS", "setup availability status failed!", e);
        }
        unsetAvailabilityStatus(this.mControllers);
    }

    /* access modifiers changed from: private */
    /* renamed from: setupAvailabilityStatus */
    public Boolean lambda$new$0(Collection<AbstractPreferenceController> collection) {
        try {
            collection.stream().filter($$Lambda$TelephonyStatusControlSession$hTz47DPjQh46qdJbcdfkM3vk6bA.INSTANCE).map(new Function(TelephonyAvailabilityHandler.class) {
                /* class com.android.settings.network.telephony.$$Lambda$n09CH4XLMjy9OICF6HhF5SPvJAA */
                public final /* synthetic */ Class f$0;

                {
                    this.f$0 = r1;
                }

                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    return (TelephonyAvailabilityHandler) this.f$0.cast((AbstractPreferenceController) obj);
                }
            }).forEach($$Lambda$TelephonyStatusControlSession$D4xRGPkvaUB546MXkihjw4z3Do.INSTANCE);
            return Boolean.TRUE;
        } catch (Exception e) {
            Log.e("TelephonyStatusControlSS", "Setup availability status failed!", e);
            return Boolean.FALSE;
        }
    }

    static /* synthetic */ boolean lambda$setupAvailabilityStatus$1(AbstractPreferenceController abstractPreferenceController) {
        return abstractPreferenceController instanceof TelephonyAvailabilityHandler;
    }

    private void unsetAvailabilityStatus(Collection<AbstractPreferenceController> collection) {
        collection.stream().filter($$Lambda$TelephonyStatusControlSession$8DmN07xY64lfNswnIgH0ynynvo.INSTANCE).map(new Function(TelephonyAvailabilityHandler.class) {
            /* class com.android.settings.network.telephony.$$Lambda$n09CH4XLMjy9OICF6HhF5SPvJAA */
            public final /* synthetic */ Class f$0;

            {
                this.f$0 = r1;
            }

            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return (TelephonyAvailabilityHandler) this.f$0.cast((AbstractPreferenceController) obj);
            }
        }).forEach($$Lambda$TelephonyStatusControlSession$BhlpH48u78tI_9fZ8bQ9nX48Dg.INSTANCE);
    }

    static /* synthetic */ boolean lambda$unsetAvailabilityStatus$3(AbstractPreferenceController abstractPreferenceController) {
        return abstractPreferenceController instanceof TelephonyAvailabilityHandler;
    }
}
