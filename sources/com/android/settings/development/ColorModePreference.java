package com.android.settings.development;

import android.content.Context;
import android.content.res.Resources;
import android.hardware.display.DisplayManager;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.Display;
import androidx.preference.SwitchPreference;
import com.android.settings.C0003R$array;
import java.util.ArrayList;
import java.util.List;

public class ColorModePreference extends SwitchPreference implements DisplayManager.DisplayListener {
    private int mCurrentIndex;
    private List<ColorModeDescription> mDescriptions;
    private Display mDisplay;
    private DisplayManager mDisplayManager = ((DisplayManager) getContext().getSystemService(DisplayManager.class));

    public void onDisplayRemoved(int i) {
    }

    public static List<ColorModeDescription> getColorModeDescriptions(Context context) {
        ArrayList arrayList = new ArrayList();
        Resources resources = context.getResources();
        int[] intArray = resources.getIntArray(C0003R$array.color_mode_ids);
        String[] stringArray = resources.getStringArray(C0003R$array.color_mode_names);
        String[] stringArray2 = resources.getStringArray(C0003R$array.color_mode_descriptions);
        for (int i = 0; i < intArray.length; i++) {
            if (!(intArray[i] == -1 || i == 1)) {
                ColorModeDescription colorModeDescription = new ColorModeDescription();
                colorModeDescription.colorMode = intArray[i];
                colorModeDescription.title = stringArray[i];
                colorModeDescription.summary = stringArray2[i];
                arrayList.add(colorModeDescription);
            }
        }
        return arrayList;
    }

    public ColorModePreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public void startListening() {
        this.mDisplayManager.registerDisplayListener(this, new Handler(Looper.getMainLooper()));
    }

    public void stopListening() {
        this.mDisplayManager.unregisterDisplayListener(this);
    }

    public void onDisplayAdded(int i) {
        if (i == 0) {
            updateCurrentAndSupported();
        }
    }

    public void onDisplayChanged(int i) {
        if (i == 0) {
            updateCurrentAndSupported();
        }
    }

    public void updateCurrentAndSupported() {
        boolean z = false;
        this.mDisplay = this.mDisplayManager.getDisplay(0);
        this.mDescriptions = getColorModeDescriptions(getContext());
        int colorMode = this.mDisplay.getColorMode();
        this.mCurrentIndex = -1;
        int i = 0;
        while (true) {
            if (i >= this.mDescriptions.size()) {
                break;
            } else if (this.mDescriptions.get(i).colorMode == colorMode) {
                this.mCurrentIndex = i;
                break;
            } else {
                i++;
            }
        }
        if (this.mCurrentIndex == 1) {
            z = true;
        }
        setChecked(z);
    }

    /* access modifiers changed from: protected */
    @Override // androidx.preference.Preference
    public boolean persistBoolean(boolean z) {
        if (this.mDescriptions.size() != 2) {
            return true;
        }
        ColorModeDescription colorModeDescription = this.mDescriptions.get(z ? 1 : 0);
        this.mDisplay.requestColorMode(colorModeDescription.colorMode);
        this.mCurrentIndex = this.mDescriptions.indexOf(colorModeDescription);
        return true;
    }

    /* access modifiers changed from: private */
    public static class ColorModeDescription {
        private int colorMode;
        private String summary;
        private String title;

        private ColorModeDescription() {
        }
    }
}
