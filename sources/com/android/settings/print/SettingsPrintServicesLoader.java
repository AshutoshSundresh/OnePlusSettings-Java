package com.android.settings.print;

import android.content.Context;
import android.print.PrintManager;
import android.print.PrintServicesLoader;
import android.printservice.PrintServiceInfo;
import androidx.loader.content.Loader;
import com.android.internal.util.Preconditions;
import java.util.List;

public class SettingsPrintServicesLoader extends Loader<List<PrintServiceInfo>> {
    private PrintServicesLoader mLoader;

    public SettingsPrintServicesLoader(PrintManager printManager, Context context, int i) {
        super((Context) Preconditions.checkNotNull(context));
        this.mLoader = new PrintServicesLoader(printManager, context, i) {
            /* class com.android.settings.print.SettingsPrintServicesLoader.AnonymousClass1 */

            public void deliverResult(List<PrintServiceInfo> list) {
                SettingsPrintServicesLoader.super.deliverResult(list);
                SettingsPrintServicesLoader.this.deliverResult(list);
            }
        };
    }

    /* access modifiers changed from: protected */
    @Override // androidx.loader.content.Loader
    public void onForceLoad() {
        this.mLoader.forceLoad();
    }

    /* access modifiers changed from: protected */
    @Override // androidx.loader.content.Loader
    public void onStartLoading() {
        this.mLoader.startLoading();
    }

    /* access modifiers changed from: protected */
    @Override // androidx.loader.content.Loader
    public void onStopLoading() {
        this.mLoader.stopLoading();
    }

    /* access modifiers changed from: protected */
    @Override // androidx.loader.content.Loader
    public boolean onCancelLoad() {
        return this.mLoader.cancelLoad();
    }

    /* access modifiers changed from: protected */
    @Override // androidx.loader.content.Loader
    public void onAbandon() {
        this.mLoader.abandon();
    }

    /* access modifiers changed from: protected */
    @Override // androidx.loader.content.Loader
    public void onReset() {
        this.mLoader.reset();
    }
}
