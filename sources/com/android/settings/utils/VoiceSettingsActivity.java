package com.android.settings.utils;

import android.app.Activity;
import android.app.VoiceInteractor;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public abstract class VoiceSettingsActivity extends Activity {
    /* access modifiers changed from: protected */
    public abstract boolean onVoiceSettingInteraction(Intent intent);

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (!isVoiceInteractionRoot()) {
            Log.v("VoiceSettingsActivity", "Cannot modify settings without voice interaction");
            finish();
        } else if (onVoiceSettingInteraction(getIntent())) {
            finish();
        }
    }

    /* access modifiers changed from: protected */
    public void notifySuccess(CharSequence charSequence) {
        if (getVoiceInteractor() != null) {
            getVoiceInteractor().submitRequest(new VoiceInteractor.CompleteVoiceRequest(charSequence, null) {
                /* class com.android.settings.utils.VoiceSettingsActivity.AnonymousClass1 */

                public void onCompleteResult(Bundle bundle) {
                    VoiceSettingsActivity.this.finish();
                }
            });
        }
    }

    /* access modifiers changed from: protected */
    public void notifyFailure(CharSequence charSequence) {
        if (getVoiceInteractor() != null) {
            getVoiceInteractor().submitRequest(new VoiceInteractor.AbortVoiceRequest(charSequence, null));
        }
    }
}
