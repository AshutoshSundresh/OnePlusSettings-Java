package com.oneplus.settings.ui;

import android.app.ActivityManager;
import android.app.UiModeManager;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.SystemProperties;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.android.settings.C0006R$color;
import com.android.settings.C0007R$dimen;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.android.settings.R$styleable;
import com.oneplus.compat.util.OpThemeNative;
import com.oneplus.settings.OPMemberController;
import com.oneplus.settings.ui.OPCustomTonePreference;
import com.oneplus.settings.utils.OPThemeUtils;
import com.oneplus.settings.utils.OPUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OPCustomTonePreference extends Preference {
    private PagerAdapter adapter;
    private ChooseStyleAdapter mChooseStyleAdapter;
    private Context mContext;
    private ToneViewHolder mCurrentVH;
    private final List<ItemEntity> mItemEntities;
    protected int mLastIndex;
    private final List<Integer> mPreviewList;
    private RecyclerView mRecyclerView;
    protected int mSelectedIndex;
    private ViewPager mViewPager;

    public OPCustomTonePreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mItemEntities = new ArrayList();
        this.mPreviewList = new ArrayList();
        context.getTheme().obtainStyledAttributes(attributeSet, R$styleable.VideoPreference, 0, 0);
        setLayoutResource(C0012R$layout.op_custom_tone_choose_layout);
        this.mContext = context;
    }

    public OPCustomTonePreference(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public OPCustomTonePreference(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public OPCustomTonePreference(Context context) {
        this(context, null);
    }

    public boolean needShowWarningDialog() {
        return this.mLastIndex != this.mSelectedIndex;
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        ViewPager viewPager = (ViewPager) preferenceViewHolder.findViewById(C0010R$id.tone_preview);
        this.mViewPager = viewPager;
        viewPager.setOffscreenPageLimit(3);
        ItemEntity itemEntity = new ItemEntity(this, getContext().getString(C0017R$string.oneplus_theme_tone_effect_color), C0008R$drawable.op_img_tone_color, 1);
        ItemEntity itemEntity2 = new ItemEntity(this, getContext().getString(C0017R$string.oneplus_theme_tone_effect_light), C0008R$drawable.op_img_tone_light, 2);
        ItemEntity itemEntity3 = new ItemEntity(this, getContext().getString(C0017R$string.oneplus_theme_tone_effect_dark), C0008R$drawable.op_img_tone_dark, 3);
        int i = Settings.System.getInt(getContext().getContentResolver(), "oem_black_mode", 2);
        if (i == 2) {
            this.mPreviewList.add(Integer.valueOf(C0008R$drawable.op_img_tone_color1));
            this.mPreviewList.add(Integer.valueOf(C0008R$drawable.op_img_tone_color2));
            this.mLastIndex = 0;
            this.mSelectedIndex = 0;
            itemEntity.selected = true;
        } else if (i == 0) {
            this.mPreviewList.add(Integer.valueOf(C0008R$drawable.op_img_tone_light1));
            this.mPreviewList.add(Integer.valueOf(C0008R$drawable.op_img_tone_light2));
            this.mLastIndex = 1;
            this.mSelectedIndex = 1;
            itemEntity2.selected = true;
        } else if (i == 1) {
            this.mPreviewList.add(Integer.valueOf(C0008R$drawable.op_img_tone_dark1));
            this.mPreviewList.add(Integer.valueOf(C0008R$drawable.op_img_tone_dark2));
            this.mLastIndex = 2;
            this.mSelectedIndex = 2;
            itemEntity3.selected = true;
        }
        this.mItemEntities.add(itemEntity);
        this.mItemEntities.add(itemEntity2);
        this.mItemEntities.add(itemEntity3);
        Resources resources = getContext().getResources();
        Configuration configuration = resources.getConfiguration();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        this.mViewPager.setPageMargin((-(OPUtils.dp2Px(displayMetrics, (float) configuration.screenWidthDp) - OPUtils.dp2Px(displayMetrics, 200.0f))) + OPUtils.dp2Px(displayMetrics, getContext().getResources().getDimension(C0007R$dimen.op_control_margin_space8)));
        AnonymousClass1 r0 = new PagerAdapter() {
            /* class com.oneplus.settings.ui.OPCustomTonePreference.AnonymousClass1 */

            @Override // androidx.viewpager.widget.PagerAdapter
            public int getItemPosition(Object obj) {
                return -2;
            }

            @Override // androidx.viewpager.widget.PagerAdapter
            public boolean isViewFromObject(View view, Object obj) {
                return view == obj;
            }

            @Override // androidx.viewpager.widget.PagerAdapter
            public int getCount() {
                return OPCustomTonePreference.this.mPreviewList.size();
            }

            @Override // androidx.viewpager.widget.PagerAdapter
            public Object instantiateItem(ViewGroup viewGroup, int i) {
                View inflate = LayoutInflater.from(OPCustomTonePreference.this.getContext()).inflate(C0012R$layout.op_custom_tone_preview_item, (ViewGroup) null);
                viewGroup.addView(inflate);
                RadiusImageView radiusImageView = (RadiusImageView) inflate.findViewById(C0010R$id.preview);
                radiusImageView.setImageResource(((Integer) OPCustomTonePreference.this.mPreviewList.get(i)).intValue());
                radiusImageView.setImageResource(((Integer) OPCustomTonePreference.this.mPreviewList.get(i)).intValue());
                return inflate;
            }

            @Override // androidx.viewpager.widget.PagerAdapter
            public void destroyItem(ViewGroup viewGroup, int i, Object obj) {
                viewGroup.removeView((View) obj);
            }
        };
        this.adapter = r0;
        this.mViewPager.setAdapter(r0);
        this.mRecyclerView = (RecyclerView) preferenceViewHolder.findViewById(C0010R$id.custom_style_recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(0);
        this.mRecyclerView.setLayoutManager(linearLayoutManager);
        this.mRecyclerView.addItemDecoration(new SpaceItemDecoration(0));
        ChooseStyleAdapter chooseStyleAdapter = new ChooseStyleAdapter();
        this.mChooseStyleAdapter = chooseStyleAdapter;
        this.mRecyclerView.setAdapter(chooseStyleAdapter);
    }

    class ChooseStyleAdapter extends RecyclerView.Adapter<ToneViewHolder> {
        ChooseStyleAdapter() {
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public ToneViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new ToneViewHolder(OPCustomTonePreference.this, LayoutInflater.from(OPCustomTonePreference.this.getContext()).inflate(C0012R$layout.op_custom_tone_choose_item, (ViewGroup) null));
        }

        public void onBindViewHolder(ToneViewHolder toneViewHolder, int i) {
            ItemEntity itemEntity = (ItemEntity) OPCustomTonePreference.this.mItemEntities.get(i);
            toneViewHolder.textView.setText(itemEntity.name);
            toneViewHolder.imageView.setImageResource(itemEntity.resId);
            int i2 = Settings.System.getInt(OPCustomTonePreference.this.getContext().getContentResolver(), "oem_black_mode", 2);
            if (i2 == 2) {
                if (itemEntity.selected) {
                    toneViewHolder.imageViewMask.setVisibility(0);
                    OPCustomTonePreference.this.mCurrentVH = toneViewHolder;
                } else {
                    toneViewHolder.imageViewMask.setVisibility(8);
                }
            } else if (i2 == 0) {
                if (itemEntity.selected) {
                    toneViewHolder.imageViewMask.setVisibility(0);
                    OPCustomTonePreference.this.mCurrentVH = toneViewHolder;
                } else {
                    toneViewHolder.imageViewMask.setVisibility(8);
                }
            } else if (i2 == 1) {
                if (itemEntity.selected) {
                    toneViewHolder.imageViewMask.setVisibility(0);
                    toneViewHolder.imageViewMask.setImageResource(C0008R$drawable.op_theme_mask_dark);
                    OPCustomTonePreference.this.mCurrentVH = toneViewHolder;
                } else {
                    toneViewHolder.imageViewMask.setVisibility(8);
                }
            }
            toneViewHolder.imageView.setOnClickListener(new View.OnClickListener(i, toneViewHolder) {
                /* class com.oneplus.settings.ui.$$Lambda$OPCustomTonePreference$ChooseStyleAdapter$TQOysjhtGWTNfIFmxiFZJIZ53K4 */
                public final /* synthetic */ int f$1;
                public final /* synthetic */ OPCustomTonePreference.ToneViewHolder f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void onClick(View view) {
                    OPCustomTonePreference.ChooseStyleAdapter.this.lambda$onBindViewHolder$0$OPCustomTonePreference$ChooseStyleAdapter(this.f$1, this.f$2, view);
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$onBindViewHolder$0 */
        public /* synthetic */ void lambda$onBindViewHolder$0$OPCustomTonePreference$ChooseStyleAdapter(int i, ToneViewHolder toneViewHolder, View view) {
            OPCustomTonePreference oPCustomTonePreference = OPCustomTonePreference.this;
            if (oPCustomTonePreference.mSelectedIndex != i) {
                oPCustomTonePreference.mCurrentVH.imageViewMask.setVisibility(8);
                if (i == 2) {
                    toneViewHolder.imageViewMask.setVisibility(0);
                    toneViewHolder.imageViewMask.setImageResource(C0008R$drawable.op_theme_mask_dark);
                } else {
                    toneViewHolder.imageViewMask.setVisibility(0);
                    toneViewHolder.imageViewMask.setImageResource(C0008R$drawable.op_theme_mask_light);
                }
                OPCustomTonePreference oPCustomTonePreference2 = OPCustomTonePreference.this;
                oPCustomTonePreference2.mSelectedIndex = i;
                oPCustomTonePreference2.mCurrentVH = toneViewHolder;
                for (int i2 = 0; i2 < OPCustomTonePreference.this.mItemEntities.size(); i2++) {
                    if (i == i2) {
                        ((ItemEntity) OPCustomTonePreference.this.mItemEntities.get(i2)).selected = true;
                    } else {
                        ((ItemEntity) OPCustomTonePreference.this.mItemEntities.get(i2)).selected = false;
                    }
                }
                OPCustomTonePreference.this.mPreviewList.clear();
                OPCustomTonePreference oPCustomTonePreference3 = OPCustomTonePreference.this;
                int i3 = oPCustomTonePreference3.mSelectedIndex;
                if (i3 == 0) {
                    oPCustomTonePreference3.mPreviewList.add(Integer.valueOf(C0008R$drawable.op_img_tone_color1));
                    OPCustomTonePreference.this.mPreviewList.add(Integer.valueOf(C0008R$drawable.op_img_tone_color2));
                } else if (i3 == 1) {
                    oPCustomTonePreference3.mPreviewList.add(Integer.valueOf(C0008R$drawable.op_img_tone_light1));
                    OPCustomTonePreference.this.mPreviewList.add(Integer.valueOf(C0008R$drawable.op_img_tone_light2));
                } else {
                    oPCustomTonePreference3.mPreviewList.add(Integer.valueOf(C0008R$drawable.op_img_tone_dark1));
                    OPCustomTonePreference.this.mPreviewList.add(Integer.valueOf(C0008R$drawable.op_img_tone_dark2));
                }
                OPCustomTonePreference.this.adapter.notifyDataSetChanged();
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return OPCustomTonePreference.this.mItemEntities.size();
        }
    }

    /* access modifiers changed from: package-private */
    public class ToneViewHolder extends RecyclerView.ViewHolder {
        RadiusImageView imageView;
        RadiusImageView imageViewMask;
        TextView textView;

        ToneViewHolder(OPCustomTonePreference oPCustomTonePreference, View view) {
            super(view);
            this.imageViewMask = (RadiusImageView) view.findViewById(C0010R$id.choose_mask);
            this.imageView = (RadiusImageView) view.findViewById(C0010R$id.choose_image);
            this.textView = (TextView) view.findViewById(C0010R$id.choose_name);
        }
    }

    class SpaceItemDecoration extends RecyclerView.ItemDecoration {
        SpaceItemDecoration(int i) {
        }

        @Override // androidx.recyclerview.widget.RecyclerView.ItemDecoration
        public void getItemOffsets(Rect rect, View view, RecyclerView recyclerView, RecyclerView.State state) {
            super.getItemOffsets(rect, view, recyclerView, state);
            rect.left = (int) OPCustomTonePreference.this.getContext().getResources().getDimension(C0007R$dimen.oneplus_settings_layout_margin_left2);
            if (((RecyclerView.LayoutParams) view.getLayoutParams()).getViewAdapterPosition() == OPCustomTonePreference.this.mItemEntities.size() - 1) {
                rect.right = (int) OPCustomTonePreference.this.getContext().getResources().getDimension(C0007R$dimen.oneplus_settings_layout_margin_left2);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public class ItemEntity {
        String name;
        int resId;
        boolean selected = false;

        ItemEntity(OPCustomTonePreference oPCustomTonePreference, String str, int i, int i2) {
            this.name = str;
            this.resId = i;
        }
    }

    public void saveSelectedTone() {
        HashMap hashMap = new HashMap();
        int i = this.mSelectedIndex;
        if (i == 2) {
            OPUtils.sendAnalytics("theme_theme", "theme_theme", OPMemberController.CLIENT_TYPE);
            OPThemeUtils.setCurrentBasicColorMode(this.mContext, 1);
            enableDarkTheme(true);
            String string = Settings.System.getString(this.mContext.getContentResolver(), "oem_black_mode_accent_color");
            Settings.System.putStringForUser(this.mContext.getContentResolver(), "oneplus_accent_color", string, ActivityManager.getCurrentUser());
            if (!TextUtils.isEmpty(string)) {
                string = string.replace("#", "");
            }
            SystemProperties.set("persist.sys.theme.accentcolor", string);
        } else if (i == 1) {
            OPUtils.sendAnalytics("theme_theme", "theme_theme", "2");
            OPThemeUtils.setCurrentBasicColorMode(this.mContext, 0);
            enableDarkTheme(false);
            String string2 = Settings.System.getString(this.mContext.getContentResolver(), "oem_white_mode_accent_color");
            Settings.System.putStringForUser(this.mContext.getContentResolver(), "oneplus_accent_color", string2, ActivityManager.getCurrentUser());
            if (!TextUtils.isEmpty(string2)) {
                string2 = string2.replace("#", "");
            }
            SystemProperties.set("persist.sys.theme.accentcolor", string2);
        } else {
            OPUtils.sendAnalytics("theme_theme", "theme_theme", "1");
            OPThemeUtils.setCurrentBasicColorMode(this.mContext, 2);
            enableDarkTheme(false);
            String string3 = this.mContext.getString(C0006R$color.op_primary_default_light);
            Settings.System.putStringForUser(this.mContext.getContentResolver(), "oneplus_accent_color", string3, ActivityManager.getCurrentUser());
            if (!TextUtils.isEmpty(string3)) {
                string3 = string3.replace("#", "");
            }
            SystemProperties.set("persist.sys.theme.accentcolor", string3);
        }
        hashMap.put("oneplus_accentcolor", "");
        OpThemeNative.enableTheme(this.mContext, hashMap);
    }

    private void enableDarkTheme(boolean z) {
        ((UiModeManager) this.mContext.getSystemService(UiModeManager.class)).setNightMode(z ? 2 : 1);
    }
}
