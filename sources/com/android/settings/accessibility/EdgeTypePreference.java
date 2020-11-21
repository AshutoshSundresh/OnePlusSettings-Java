package com.android.settings.accessibility;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import com.android.internal.widget.SubtitleView;
import com.android.settings.C0003R$array;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;

public class EdgeTypePreference extends ListDialogPreference {
    public EdgeTypePreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        Resources resources = context.getResources();
        setValues(resources.getIntArray(C0003R$array.captioning_edge_type_selector_values));
        setTitles(resources.getStringArray(C0003R$array.captioning_edge_type_selector_titles));
        setDialogLayoutResource(C0012R$layout.grid_picker_dialog);
        setListItemLayoutResource(C0012R$layout.preset_picker_item);
    }

    @Override // androidx.preference.Preference
    public boolean shouldDisableDependents() {
        return getValue() == 0 || super.shouldDisableDependents();
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.accessibility.ListDialogPreference
    public void onBindListItem(View view, int i) {
        SubtitleView findViewById = view.findViewById(C0010R$id.preview);
        findViewById.setForegroundColor(-1);
        findViewById.setBackgroundColor(0);
        findViewById.setTextSize(getContext().getResources().getDisplayMetrics().density * 32.0f);
        findViewById.setEdgeType(getValueAt(i));
        findViewById.setEdgeColor(-16777216);
        CharSequence titleAt = getTitleAt(i);
        if (titleAt != null) {
            ((TextView) view.findViewById(C0010R$id.summary)).setText(titleAt);
        }
    }
}
