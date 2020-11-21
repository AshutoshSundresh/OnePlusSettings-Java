package com.android.settings.accessibility;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ListView;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;

public class PaletteListPreference extends Preference {
    private ListView mListView;
    private ViewTreeObserver.OnPreDrawListener mPreDrawListener;

    public PaletteListPreference(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public PaletteListPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        setLayoutResource(C0012R$layout.daltonizer_preview);
        initPreDrawListener();
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        ListView listView = (ListView) preferenceViewHolder.itemView.findViewById(C0010R$id.palette_listView);
        this.mListView = listView;
        if (this.mPreDrawListener != null) {
            listView.getViewTreeObserver().addOnPreDrawListener(this.mPreDrawListener);
        }
    }

    private void initPreDrawListener() {
        this.mPreDrawListener = new ViewTreeObserver.OnPreDrawListener() {
            /* class com.android.settings.accessibility.PaletteListPreference.AnonymousClass1 */

            public boolean onPreDraw() {
                if (PaletteListPreference.this.mListView == null) {
                    return false;
                }
                int measuredHeight = PaletteListPreference.this.mListView.getMeasuredHeight();
                int measuredWidth = PaletteListPreference.this.mListView.getMeasuredWidth();
                ViewTreeObserver viewTreeObserver = PaletteListPreference.this.mListView.getViewTreeObserver();
                if (viewTreeObserver.isAlive()) {
                    viewTreeObserver.removeOnPreDrawListener(this);
                }
                PaletteListPreference.this.mPreDrawListener = null;
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) PaletteListPreference.this.mListView.getLayoutParams();
                layoutParams.height = measuredHeight * PaletteListPreference.this.mListView.getAdapter().getCount();
                layoutParams.width = measuredWidth;
                PaletteListPreference.this.mListView.setLayoutParams(layoutParams);
                return true;
            }
        };
    }
}
