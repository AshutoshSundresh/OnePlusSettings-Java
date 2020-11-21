package com.android.settings.utils;

import android.content.Context;
import androidx.preference.PreferenceViewHolder;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.setupdesign.DividerItemDecoration;

public class SettingsDividerItemDecoration extends DividerItemDecoration {
    public SettingsDividerItemDecoration(Context context) {
        super(context);
    }

    /* access modifiers changed from: protected */
    @Override // com.google.android.setupdesign.DividerItemDecoration
    public boolean isDividerAllowedAbove(RecyclerView.ViewHolder viewHolder) {
        if (viewHolder instanceof PreferenceViewHolder) {
            return ((PreferenceViewHolder) viewHolder).isDividerAllowedAbove();
        }
        return super.isDividerAllowedAbove(viewHolder);
    }

    /* access modifiers changed from: protected */
    @Override // com.google.android.setupdesign.DividerItemDecoration
    public boolean isDividerAllowedBelow(RecyclerView.ViewHolder viewHolder) {
        if (viewHolder instanceof PreferenceViewHolder) {
            return ((PreferenceViewHolder) viewHolder).isDividerAllowedBelow();
        }
        return super.isDividerAllowedBelow(viewHolder);
    }
}
