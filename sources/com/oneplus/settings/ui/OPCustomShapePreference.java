package com.oneplus.settings.ui;

import android.app.ActivityManager;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.settings.C0007R$dimen;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.android.settings.SettingsPreferenceFragment;
import com.oneplus.settings.ui.OPCustomShapePreference;
import com.oneplus.settings.utils.OPApplicationUtils;
import com.oneplus.settings.utils.OPThemeUtils;
import com.oneplus.settings.utils.OPUtils;
import java.util.ArrayList;
import java.util.List;

public class OPCustomShapePreference extends Preference {
    private ActivityManager mAm;
    private OPCustomItemEntityViewHolder mCurrentVH;
    private final List<OPCustomItemEntity> mItemEntities;
    private int mLastIndex;
    private ImageView mPreviewImageView;
    private int mSelectedIndex;
    private SettingsPreferenceFragment mSettingsPreferenceFragment;

    public void setSettingsPreferenceFragment(SettingsPreferenceFragment settingsPreferenceFragment) {
        this.mSettingsPreferenceFragment = settingsPreferenceFragment;
    }

    public OPCustomShapePreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mItemEntities = new ArrayList();
        setLayoutResource(C0012R$layout.op_custom_shape_choose_layout);
        this.mAm = (ActivityManager) context.getSystemService("activity");
    }

    public OPCustomShapePreference(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public OPCustomShapePreference(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public OPCustomShapePreference(Context context) {
        this(context, null);
    }

    public void saveSelectedShape() {
        OPApplicationUtils.killProcess(this.mAm, true);
        OPThemeUtils.enableTheme("oneplus_shape", OPThemeUtils.getCurrentShapeByIndex(this.mItemEntities.get(this.mSelectedIndex).index), getContext());
        OPThemeUtils.setCurrentShape(getContext(), this.mItemEntities.get(this.mSelectedIndex).index);
        if (this.mItemEntities.get(this.mSelectedIndex).index == 1) {
            OPUtils.sendAnalytics("shape", "status", "round");
        } else if (this.mItemEntities.get(this.mSelectedIndex).index == 2) {
            OPUtils.sendAnalytics("shape", "status", "square");
        } else if (this.mItemEntities.get(this.mSelectedIndex).index == 3) {
            OPUtils.sendAnalytics("shape", "status", "teardrop");
        } else if (this.mItemEntities.get(this.mSelectedIndex).index == 4) {
            OPUtils.sendAnalytics("shape", "status", "rectangle");
        }
    }

    public boolean needShowWarningDialog() {
        return this.mLastIndex != this.mSelectedIndex;
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        ((ScrollView) preferenceViewHolder.findViewById(C0010R$id.video_container_scrollview)).setOnTouchListener($$Lambda$OPCustomShapePreference$pmKhtjUdx55CAR1Sm4gVOgYr3Q.INSTANCE);
        this.mPreviewImageView = (ImageView) preferenceViewHolder.findViewById(C0010R$id.preview);
        RecyclerView recyclerView = (RecyclerView) preferenceViewHolder.findViewById(C0010R$id.custom_style_recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(0);
        recyclerView.setLayoutManager(linearLayoutManager);
        OPCustomItemEntity oPCustomItemEntity = new OPCustomItemEntity(getContext().getString(C0017R$string.oneplus_theme_shape_effect_round), C0008R$drawable.op_custom_shape_round_wifi, 1);
        OPCustomItemEntity oPCustomItemEntity2 = new OPCustomItemEntity(getContext().getString(C0017R$string.oneplus_theme_shape_effect_square), C0008R$drawable.op_custom_shape_square_wifi, 2);
        OPCustomItemEntity oPCustomItemEntity3 = new OPCustomItemEntity(getContext().getString(C0017R$string.oneplus_theme_shape_effect_teardrop), C0008R$drawable.op_custom_shape_teardrop_wifi, 3);
        OPCustomItemEntity oPCustomItemEntity4 = new OPCustomItemEntity(getContext().getString(C0017R$string.oneplus_theme_shape_effect_round_rectangle), C0008R$drawable.op_custom_shape_round_rectangle_wifi, 4);
        this.mItemEntities.add(oPCustomItemEntity);
        this.mItemEntities.add(oPCustomItemEntity2);
        this.mItemEntities.add(oPCustomItemEntity3);
        this.mItemEntities.add(oPCustomItemEntity4);
        int currentShape = OPThemeUtils.getCurrentShape(getContext());
        if (currentShape == 1) {
            oPCustomItemEntity.selected = true;
            this.mLastIndex = 0;
            this.mSelectedIndex = 0;
        } else if (currentShape == 2) {
            oPCustomItemEntity2.selected = true;
            this.mLastIndex = 1;
            this.mSelectedIndex = 1;
        } else if (currentShape == 3) {
            oPCustomItemEntity3.selected = true;
            this.mLastIndex = 2;
            this.mSelectedIndex = 2;
        } else if (currentShape == 4) {
            oPCustomItemEntity4.selected = true;
            this.mLastIndex = 3;
            this.mSelectedIndex = 3;
        }
        changeAdaptive(currentShape);
        recyclerView.addItemDecoration(new OPSpaceItemDecoration(getContext(), this.mItemEntities.size(), (int) getContext().getResources().getDimension(C0007R$dimen.op_custom_shape_item_space)));
        recyclerView.setAdapter(new ChooseStyleAdapter());
        int i = this.mSelectedIndex;
        if (i >= 0 && i < this.mItemEntities.size()) {
            linearLayoutManager.scrollToPosition(this.mSelectedIndex);
        }
        Button button = (Button) preferenceViewHolder.findViewById(C0010R$id.save_button);
        if (button != null) {
            button.setOnClickListener(new View.OnClickListener() {
                /* class com.oneplus.settings.ui.OPCustomShapePreference.AnonymousClass1 */

                public void onClick(View view) {
                    OPCustomShapePreference.this.saveSelectedShape();
                    if (OPCustomShapePreference.this.mSettingsPreferenceFragment != null) {
                        OPCustomShapePreference.this.mSettingsPreferenceFragment.finish();
                    }
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void changeAdaptive(int i) {
        if (i == 1) {
            this.mPreviewImageView.setImageResource(C0008R$drawable.op_custom_shape_preview_round);
        } else if (i == 2) {
            this.mPreviewImageView.setImageResource(C0008R$drawable.op_custom_shape_preview_square);
        } else if (i == 3) {
            this.mPreviewImageView.setImageResource(C0008R$drawable.op_custom_shape_preview_teardrop);
        } else if (i == 4) {
            this.mPreviewImageView.setImageResource(C0008R$drawable.op_custom_shape_preview_rectangle);
        }
    }

    class ChooseStyleAdapter extends RecyclerView.Adapter<OPCustomItemEntityViewHolder> {
        ChooseStyleAdapter() {
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public OPCustomItemEntityViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new OPCustomItemEntityViewHolder(LayoutInflater.from(OPCustomShapePreference.this.getContext()).inflate(C0012R$layout.op_custom_shape_choose_item, (ViewGroup) null));
        }

        public void onBindViewHolder(OPCustomItemEntityViewHolder oPCustomItemEntityViewHolder, int i) {
            OPCustomItemEntity oPCustomItemEntity = (OPCustomItemEntity) OPCustomShapePreference.this.mItemEntities.get(i);
            oPCustomItemEntityViewHolder.textView.setText(oPCustomItemEntity.name);
            oPCustomItemEntityViewHolder.imageView.setImageResource(oPCustomItemEntity.resId);
            if (oPCustomItemEntity.selected) {
                oPCustomItemEntityViewHolder.imageViewMask.setVisibility(0);
                oPCustomItemEntityViewHolder.imageView.setSelected(true);
                oPCustomItemEntityViewHolder.textView.setSelected(true);
                OPCustomShapePreference.this.mCurrentVH = oPCustomItemEntityViewHolder;
            } else {
                oPCustomItemEntityViewHolder.imageViewMask.setVisibility(4);
                oPCustomItemEntityViewHolder.imageView.setSelected(false);
                oPCustomItemEntityViewHolder.textView.setSelected(false);
            }
            oPCustomItemEntityViewHolder.itemView.setOnClickListener(new View.OnClickListener(i, oPCustomItemEntity, oPCustomItemEntityViewHolder) {
                /* class com.oneplus.settings.ui.$$Lambda$OPCustomShapePreference$ChooseStyleAdapter$z7P9eI1Y5QKVeB4mcsuHrEuqtF8 */
                public final /* synthetic */ int f$1;
                public final /* synthetic */ OPCustomItemEntity f$2;
                public final /* synthetic */ OPCustomItemEntityViewHolder f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void onClick(View view) {
                    OPCustomShapePreference.ChooseStyleAdapter.this.lambda$onBindViewHolder$0$OPCustomShapePreference$ChooseStyleAdapter(this.f$1, this.f$2, this.f$3, view);
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$onBindViewHolder$0 */
        public /* synthetic */ void lambda$onBindViewHolder$0$OPCustomShapePreference$ChooseStyleAdapter(int i, OPCustomItemEntity oPCustomItemEntity, OPCustomItemEntityViewHolder oPCustomItemEntityViewHolder, View view) {
            OPCustomShapePreference.this.mSelectedIndex = i;
            OPCustomShapePreference.this.changeAdaptive(oPCustomItemEntity.index);
            if (OPCustomShapePreference.this.mCurrentVH != null) {
                if (OPCustomShapePreference.this.mCurrentVH.imageViewMask != null) {
                    OPCustomShapePreference.this.mCurrentVH.imageViewMask.setVisibility(4);
                }
                if (OPCustomShapePreference.this.mCurrentVH.imageView != null) {
                    OPCustomShapePreference.this.mCurrentVH.imageView.setSelected(false);
                }
                if (OPCustomShapePreference.this.mCurrentVH.textView != null) {
                    OPCustomShapePreference.this.mCurrentVH.textView.setSelected(false);
                }
            }
            oPCustomItemEntityViewHolder.imageViewMask.setVisibility(0);
            oPCustomItemEntityViewHolder.imageView.setSelected(true);
            oPCustomItemEntityViewHolder.textView.setSelected(true);
            OPCustomShapePreference.this.mCurrentVH = oPCustomItemEntityViewHolder;
            int i2 = 0;
            while (i2 < OPCustomShapePreference.this.mItemEntities.size()) {
                ((OPCustomItemEntity) OPCustomShapePreference.this.mItemEntities.get(i2)).selected = i == i2;
                i2++;
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return OPCustomShapePreference.this.mItemEntities.size();
        }
    }
}
