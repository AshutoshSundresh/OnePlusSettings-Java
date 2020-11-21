package com.android.settings.display;

import android.content.Context;
import android.text.BidiFormatter;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Slog;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.R$styleable;
import com.android.settings.C0017R$string;
import com.android.settings.Utils;
import com.android.settingslib.CustomEditTextPreferenceCompat;
import com.android.settingslib.display.DisplayDensityConfiguration;
import java.text.NumberFormat;

public class DensityPreference extends CustomEditTextPreferenceCompat {
    public DensityPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override // androidx.preference.Preference
    public void onAttached() {
        super.onAttached();
        String unicodeWrap = BidiFormatter.getInstance().unicodeWrap(NumberFormat.getInstance().format((long) getCurrentSwDp()));
        setSummary(getContext().getString(C0017R$string.density_pixel_summary, unicodeWrap));
    }

    private int getCurrentSwDp() {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        return (int) (((float) Math.min(displayMetrics.widthPixels, displayMetrics.heightPixels)) / displayMetrics.density);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settingslib.CustomEditTextPreferenceCompat
    public void onBindDialogView(View view) {
        super.onBindDialogView(view);
        EditText editText = (EditText) view.findViewById(16908291);
        if (editText != null) {
            editText.setInputType(2);
            editText.setText(getCurrentSwDp() + "");
            Utils.setEditTextCursorPosition(editText);
        }
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settingslib.CustomEditTextPreferenceCompat
    public void onDialogClosed(boolean z) {
        if (z) {
            try {
                DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
                DisplayDensityConfiguration.setForcedDisplayDensity(0, Math.max((Math.min(displayMetrics.widthPixels, displayMetrics.heightPixels) * 160) / Math.max(Integer.parseInt(getText()), 320), (int) R$styleable.AppCompatTheme_windowFixedHeightMajor));
            } catch (Exception e) {
                Slog.e("DensityPreference", "Couldn't save density", e);
            }
        }
    }
}
