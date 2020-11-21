package com.android.settings.homepage.contextualcards;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import androidx.slice.Slice;
import androidx.slice.SliceMetadata;
import androidx.slice.SliceViewManager;
import com.android.settings.homepage.contextualcards.ContextualCard;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import com.android.settingslib.utils.ThreadUtils;
import java.util.concurrent.Callable;

public class EligibleCardChecker implements Callable<ContextualCard> {
    ContextualCard mCard;
    private final Context mContext;

    static /* synthetic */ void lambda$bindSlice$0(Slice slice) {
    }

    EligibleCardChecker(Context context, ContextualCard contextualCard) {
        this.mContext = context;
        this.mCard = contextualCard;
    }

    @Override // java.util.concurrent.Callable
    public ContextualCard call() {
        ContextualCard contextualCard;
        long currentTimeMillis = System.currentTimeMillis();
        MetricsFeatureProvider metricsFeatureProvider = FeatureFactory.getFactory(this.mContext).getMetricsFeatureProvider();
        if (isCardEligibleToDisplay(this.mCard)) {
            metricsFeatureProvider.action(0, 1686, 1502, this.mCard.getTextSliceUri(), 1);
            contextualCard = this.mCard;
        } else {
            metricsFeatureProvider.action(0, 1686, 1502, this.mCard.getTextSliceUri(), 0);
            contextualCard = null;
        }
        metricsFeatureProvider.action(0, 1684, 1502, this.mCard.getTextSliceUri(), (int) (System.currentTimeMillis() - currentTimeMillis));
        return contextualCard;
    }

    /* access modifiers changed from: package-private */
    public boolean isCardEligibleToDisplay(ContextualCard contextualCard) {
        if (contextualCard.getRankingScore() < 0.0d) {
            return false;
        }
        Uri sliceUri = contextualCard.getSliceUri();
        if (!"content".equals(sliceUri.getScheme())) {
            return false;
        }
        Slice bindSlice = bindSlice(sliceUri);
        if (bindSlice == null || bindSlice.hasHint("error")) {
            Log.w("EligibleCardChecker", "Failed to bind slice, not eligible for display " + sliceUri);
            return false;
        }
        ContextualCard.Builder mutate = contextualCard.mutate();
        mutate.setSlice(bindSlice);
        this.mCard = mutate.build();
        if (isSliceToggleable(bindSlice)) {
            ContextualCard.Builder mutate2 = contextualCard.mutate();
            mutate2.setHasInlineAction(true);
            this.mCard = mutate2.build();
        }
        return true;
    }

    /* access modifiers changed from: package-private */
    public Slice bindSlice(Uri uri) {
        SliceViewManager instance = SliceViewManager.getInstance(this.mContext);
        $$Lambda$EligibleCardChecker$j9fDhA9Nn8fnDdaalBclkvyIuI r0 = $$Lambda$EligibleCardChecker$j9fDhA9Nn8fnDdaalBclkvyIuI.INSTANCE;
        instance.registerSliceCallback(uri, r0);
        Slice bindSlice = instance.bindSlice(uri);
        ThreadUtils.postOnMainThread(new Runnable(uri, r0) {
            /* class com.android.settings.homepage.contextualcards.$$Lambda$EligibleCardChecker$_CWOYdrHPrZTKhB6EpeRdX3RAZo */
            public final /* synthetic */ Uri f$1;
            public final /* synthetic */ SliceViewManager.SliceCallback f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                AsyncTask.execute((SliceViewManager) new Runnable(this.f$1, this.f$2) {
                    /* class com.android.settings.homepage.contextualcards.$$Lambda$EligibleCardChecker$K96heP9LNGkcJovKF_x_Oem8lNg */
                    public final /* synthetic */ Uri f$1;
                    public final /* synthetic */ SliceViewManager.SliceCallback f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run() {
                        SliceViewManager.this.unregisterSliceCallback(this.f$1, this.f$2);
                    }
                });
            }
        });
        return bindSlice;
    }

    /* access modifiers changed from: package-private */
    public boolean isSliceToggleable(Slice slice) {
        return !SliceMetadata.from(this.mContext, slice).getToggles().isEmpty();
    }
}
