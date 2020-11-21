package com.android.settings.accessibility;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.accessibility.CaptioningManager;
import android.widget.TextView;
import com.android.internal.widget.SubtitleView;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;

public class PresetPreference extends ListDialogPreference {
    private final CaptioningManager mCaptioningManager;

    public PresetPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setDialogLayoutResource(C0012R$layout.grid_picker_dialog);
        setListItemLayoutResource(C0012R$layout.preset_picker_item);
        this.mCaptioningManager = (CaptioningManager) context.getSystemService("captioning");
    }

    @Override // androidx.preference.Preference
    public boolean shouldDisableDependents() {
        return getValue() != -1 || super.shouldDisableDependents();
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.accessibility.ListDialogPreference
    public void onBindListItem(View view, int i) {
        View findViewById = view.findViewById(C0010R$id.preview_viewport);
        SubtitleView findViewById2 = view.findViewById(C0010R$id.preview);
        CaptionAppearanceFragment.applyCaptionProperties(this.mCaptioningManager, findViewById2, findViewById, getValueAt(i));
        findViewById2.setTextSize(getContext().getResources().getDisplayMetrics().density * 32.0f);
        CharSequence titleAt = getTitleAt(i);
        if (titleAt != null) {
            ((TextView) view.findViewById(C0010R$id.summary)).setText(titleAt);
        }
    }
}
