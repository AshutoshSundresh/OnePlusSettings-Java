package com.android.settings.network;

import android.content.Context;
import android.content.IntentFilter;
import android.net.EthernetManager;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.OnLifecycleEvent;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settings.network.EthernetTetherPreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;

public final class EthernetTetherPreferenceController extends TetherBasePreferenceController {
    @VisibleForTesting
    EthernetManager.Listener mEthernetListener;
    private final EthernetManager mEthernetManager;
    private final String mEthernetRegex;

    @Override // com.android.settings.network.TetherBasePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.network.TetherBasePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.network.TetherBasePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.network.TetherBasePreferenceController
    public int getTetherType() {
        return 5;
    }

    @Override // com.android.settings.network.TetherBasePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.network.TetherBasePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.network.TetherBasePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public EthernetTetherPreferenceController(Context context, String str) {
        super(context, str);
        this.mEthernetRegex = context.getString(17039904);
        this.mEthernetManager = (EthernetManager) context.getSystemService("ethernet");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        AnonymousClass1 r0 = new EthernetManager.Listener() {
            /* class com.android.settings.network.EthernetTetherPreferenceController.AnonymousClass1 */

            /* access modifiers changed from: private */
            /* renamed from: lambda$onAvailabilityChanged$0 */
            public /* synthetic */ void lambda$onAvailabilityChanged$0$EthernetTetherPreferenceController$1() {
                EthernetTetherPreferenceController ethernetTetherPreferenceController = EthernetTetherPreferenceController.this;
                ethernetTetherPreferenceController.updateState(ethernetTetherPreferenceController.mPreference);
            }

            public void onAvailabilityChanged(String str, boolean z) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    /* class com.android.settings.network.$$Lambda$EthernetTetherPreferenceController$1$AvOfxbGpa_2vIeD9ljahcYUG4L4 */

                    public final void run() {
                        EthernetTetherPreferenceController.AnonymousClass1.this.lambda$onAvailabilityChanged$0$EthernetTetherPreferenceController$1();
                    }
                });
            }
        };
        this.mEthernetListener = r0;
        this.mEthernetManager.addListener(r0);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {
        this.mEthernetManager.removeListener(this.mEthernetListener);
        this.mEthernetListener = null;
    }

    @Override // com.android.settings.network.TetherBasePreferenceController
    public boolean shouldEnable() {
        for (String str : this.mCm.getTetherableIfaces()) {
            if (str.matches(this.mEthernetRegex)) {
                return true;
            }
        }
        return false;
    }

    @Override // com.android.settings.network.TetherBasePreferenceController
    public boolean shouldShow() {
        return !TextUtils.isEmpty(this.mEthernetRegex);
    }
}
