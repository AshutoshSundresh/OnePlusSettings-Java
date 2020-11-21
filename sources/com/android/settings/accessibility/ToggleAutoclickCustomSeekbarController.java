package com.android.settings.accessibility;

import android.content.ContentResolver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0010R$id;
import com.android.settings.C0015R$plurals;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnPause;
import com.android.settingslib.core.lifecycle.events.OnResume;
import com.android.settingslib.widget.LayoutPreference;

public class ToggleAutoclickCustomSeekbarController extends BasePreferenceController implements LifecycleObserver, OnResume, OnPause, SharedPreferences.OnSharedPreferenceChangeListener {
    private static final int AUTOCLICK_DELAY_STEP = 100;
    private static final String CONTROL_AUTOCLICK_DELAY_SECURE = "accessibility_autoclick_delay";
    private static final String KEY_CUSTOM_DELAY_VALUE = "custom_delay_value";
    static final int MAX_AUTOCLICK_DELAY_MS = 1000;
    static final int MIN_AUTOCLICK_DELAY_MS = 200;
    private final ContentResolver mContentResolver;
    private TextView mDelayLabel;
    private ImageView mLonger;
    private SeekBar mSeekBar;
    private final SeekBar.OnSeekBarChangeListener mSeekBarChangeListener;
    private final SharedPreferences mSharedPreferences;
    private ImageView mShorter;

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private int seekBarProgressToDelay(int i) {
        return (i * 100) + MIN_AUTOCLICK_DELAY_MS;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 0;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public ToggleAutoclickCustomSeekbarController(Context context, String str) {
        super(context, str);
        this.mSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
            /* class com.android.settings.accessibility.ToggleAutoclickCustomSeekbarController.AnonymousClass1 */

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                ToggleAutoclickCustomSeekbarController toggleAutoclickCustomSeekbarController = ToggleAutoclickCustomSeekbarController.this;
                toggleAutoclickCustomSeekbarController.updateCustomDelayValue(toggleAutoclickCustomSeekbarController.seekBarProgressToDelay(i));
            }
        };
        this.mSharedPreferences = context.getSharedPreferences(context.getPackageName(), 0);
        this.mContentResolver = context.getContentResolver();
    }

    public ToggleAutoclickCustomSeekbarController(Context context, Lifecycle lifecycle, String str) {
        this(context, str);
        if (lifecycle != null) {
            lifecycle.addObserver(this);
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnResume
    public void onResume() {
        SharedPreferences sharedPreferences = this.mSharedPreferences;
        if (sharedPreferences != null) {
            sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnPause
    public void onPause() {
        SharedPreferences sharedPreferences = this.mSharedPreferences;
        if (sharedPreferences != null) {
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        LayoutPreference layoutPreference = (LayoutPreference) preferenceScreen.findPreference(getPreferenceKey());
        if (isAvailable()) {
            int sharedPreferenceForDelayValue = getSharedPreferenceForDelayValue();
            SeekBar seekBar = (SeekBar) layoutPreference.findViewById(C0010R$id.autoclick_delay);
            this.mSeekBar = seekBar;
            seekBar.setMax(delayToSeekBarProgress(MAX_AUTOCLICK_DELAY_MS));
            this.mSeekBar.setProgress(delayToSeekBarProgress(sharedPreferenceForDelayValue));
            this.mSeekBar.setOnSeekBarChangeListener(this.mSeekBarChangeListener);
            TextView textView = (TextView) layoutPreference.findViewById(C0010R$id.current_label);
            this.mDelayLabel = textView;
            textView.setText(delayTimeToString(sharedPreferenceForDelayValue));
            ImageView imageView = (ImageView) layoutPreference.findViewById(C0010R$id.shorter);
            this.mShorter = imageView;
            imageView.setOnClickListener(new View.OnClickListener() {
                /* class com.android.settings.accessibility.$$Lambda$ToggleAutoclickCustomSeekbarController$QowIl17V3UqZABcFjHdnd8UsTw */

                public final void onClick(View view) {
                    ToggleAutoclickCustomSeekbarController.this.lambda$displayPreference$0$ToggleAutoclickCustomSeekbarController(view);
                }
            });
            ImageView imageView2 = (ImageView) layoutPreference.findViewById(C0010R$id.longer);
            this.mLonger = imageView2;
            imageView2.setOnClickListener(new View.OnClickListener() {
                /* class com.android.settings.accessibility.$$Lambda$ToggleAutoclickCustomSeekbarController$JcmNWoZFb1znN59JFRNBqCdelK4 */

                public final void onClick(View view) {
                    ToggleAutoclickCustomSeekbarController.this.lambda$displayPreference$1$ToggleAutoclickCustomSeekbarController(view);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$displayPreference$0 */
    public /* synthetic */ void lambda$displayPreference$0$ToggleAutoclickCustomSeekbarController(View view) {
        minusDelayByImageView();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$displayPreference$1 */
    public /* synthetic */ void lambda$displayPreference$1$ToggleAutoclickCustomSeekbarController(View view) {
        plusDelayByImageView();
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String str) {
        if ("delay_mode".equals(str)) {
            updateCustomDelayValue(getSharedPreferenceForDelayValue());
        }
    }

    private int delayToSeekBarProgress(int i) {
        return (i - 200) / 100;
    }

    private int getSharedPreferenceForDelayValue() {
        return this.mSharedPreferences.getInt(KEY_CUSTOM_DELAY_VALUE, Settings.Secure.getInt(this.mContentResolver, CONTROL_AUTOCLICK_DELAY_SECURE, 600));
    }

    private void putSecureInt(String str, int i) {
        Settings.Secure.putInt(this.mContentResolver, str, i);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void updateCustomDelayValue(int i) {
        putSecureInt(CONTROL_AUTOCLICK_DELAY_SECURE, i);
        this.mSharedPreferences.edit().putInt(KEY_CUSTOM_DELAY_VALUE, i).apply();
        this.mSeekBar.setProgress(delayToSeekBarProgress(i));
        this.mDelayLabel.setText(delayTimeToString(i));
    }

    private void minusDelayByImageView() {
        int sharedPreferenceForDelayValue = getSharedPreferenceForDelayValue();
        if (sharedPreferenceForDelayValue > MIN_AUTOCLICK_DELAY_MS) {
            updateCustomDelayValue(sharedPreferenceForDelayValue - 100);
        }
    }

    private void plusDelayByImageView() {
        int sharedPreferenceForDelayValue = getSharedPreferenceForDelayValue();
        if (sharedPreferenceForDelayValue < MAX_AUTOCLICK_DELAY_MS) {
            updateCustomDelayValue(sharedPreferenceForDelayValue + 100);
        }
    }

    private CharSequence delayTimeToString(int i) {
        int i2 = i == MAX_AUTOCLICK_DELAY_MS ? 1 : 3;
        float f = ((float) i) / 1000.0f;
        return this.mContext.getResources().getQuantityString(C0015R$plurals.accessibilty_autoclick_delay_unit_second, i2, String.format(f == 1.0f ? "%.0f" : "%.1f", Float.valueOf(f)));
    }
}
