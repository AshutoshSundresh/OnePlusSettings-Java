package com.oneplus.settings.ui;

import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.fingerprint.FingerprintManager;
import android.os.SystemProperties;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.settings.C0006R$color;
import com.android.settings.C0007R$dimen;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;
import com.oneplus.settings.gestures.OPGestureUtils;
import com.oneplus.settings.ui.OPCustomClockPreference;
import com.oneplus.settings.utils.OPThemeUtils;
import com.oneplus.settings.utils.OPUtils;
import java.util.ArrayList;
import java.util.List;

public class OPCustomClockPreference extends Preference {
    private static final boolean SUPPORT_FOD = OPUtils.isSupportCustomFingerprint();
    private OPCustomItemEntityViewHolder mCurrentVH;
    private FingerprintManager mFingerprintManager;
    private final List<ItemEntity> mItemEntities;
    private int mLastIndex;
    private ImageView mPreView;
    private int mSelectedIndex;
    private SettingsPreferenceFragment mSettingsPreferenceFragment;
    private ImageView mShowInfo;

    public void setSettingsPreferenceFragment(SettingsPreferenceFragment settingsPreferenceFragment) {
        this.mSettingsPreferenceFragment = settingsPreferenceFragment;
    }

    public OPCustomClockPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mItemEntities = new ArrayList();
        this.mLastIndex = Settings.Secure.getIntForUser(getContext().getContentResolver(), "aod_clock_style", 0, ActivityManager.getCurrentUser());
        setLayoutResource(C0012R$layout.op_custom_clock_choose_layout);
        this.mFingerprintManager = Utils.getFingerprintManagerOrNull(getContext());
    }

    public OPCustomClockPreference(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public OPCustomClockPreference(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public OPCustomClockPreference(Context context) {
        this(context, null);
    }

    public void saveSelectedClock() {
        int currentUser = ActivityManager.getCurrentUser();
        Settings.Secure.putIntForUser(getContext().getContentResolver(), "aod_clock_style", this.mItemEntities.get(this.mSelectedIndex).type, currentUser);
        boolean z = false;
        if (this.mItemEntities.get(this.mSelectedIndex).type == 1) {
            Settings.System.putIntForUser(getContext().getContentResolver(), "aod_smart_display_enabled", 0, currentUser);
        } else {
            boolean z2 = 1 == Settings.Secure.getIntForUser(getContext().getContentResolver(), "doze_enabled", 0, currentUser);
            boolean z3 = 1 == Settings.System.getIntForUser(getContext().getContentResolver(), "prox_wake_enabled", 0, currentUser);
            if (SUPPORT_FOD) {
                if (OPGestureUtils.get(Settings.System.getInt(getContext().getContentResolver(), "oem_acc_blackscreen_gestrue_enable", 0), 11) == 1) {
                    z = true;
                }
                if (z2 && (z3 || z)) {
                    Settings.System.putIntForUser(getContext().getContentResolver(), "aod_smart_display_enabled", 1, currentUser);
                }
            } else if (z2 && z3) {
                Settings.System.putIntForUser(getContext().getContentResolver(), "aod_smart_display_enabled", 1, currentUser);
            }
        }
        OPUtils.sendAppTrackerForClockStyle();
    }

    public boolean needShowWarningDialog() {
        for (int i = 0; i < this.mItemEntities.size(); i++) {
            if (this.mItemEntities.get(i).selected) {
                if (this.mLastIndex != this.mItemEntities.get(i).type) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        ItemEntity itemEntity;
        ItemEntity itemEntity2;
        super.onBindViewHolder(preferenceViewHolder);
        ((ScrollView) preferenceViewHolder.findViewById(C0010R$id.video_container_scrollview)).setOnTouchListener($$Lambda$OPCustomClockPreference$R0hYGgoHJXoeOM0AzCeYqbFHm_4.INSTANCE);
        this.mPreView = (ImageView) preferenceViewHolder.findViewById(C0010R$id.preview);
        this.mShowInfo = (ImageView) preferenceViewHolder.findViewById(C0010R$id.showInfo);
        if (OPUtils.isBlackModeOn(getContext().getContentResolver())) {
            this.mShowInfo.setBackgroundTintList(getContext().getResources().getColorStateList(C0006R$color.op_aod_parsons_show_info_bg_dark));
            this.mShowInfo.setImageTintList(getContext().getResources().getColorStateList(C0006R$color.op_control_icon_color_active_light));
        } else {
            this.mShowInfo.setBackgroundTintList(getContext().getResources().getColorStateList(C0006R$color.op_aod_parsons_show_info_bg_light));
            this.mShowInfo.setImageTintList(getContext().getResources().getColorStateList(C0006R$color.op_control_icon_color_active_dark));
        }
        this.mShowInfo.setOnClickListener(new View.OnClickListener() {
            /* class com.oneplus.settings.ui.OPCustomClockPreference.AnonymousClass1 */

            public void onClick(View view) {
                int i;
                int i2;
                int i3;
                ItemEntity itemEntity = (ItemEntity) OPCustomClockPreference.this.mItemEntities.get(OPCustomClockPreference.this.mSelectedIndex);
                if (itemEntity != null && itemEntity.hasInfo) {
                    int i4 = -1;
                    if (itemEntity.type != 11) {
                        i = -1;
                        i3 = -1;
                        i2 = -1;
                    } else {
                        int i5 = C0017R$string.op_aod_parsons_info_title;
                        int i6 = C0017R$string.op_aod_parsons_info_message;
                        int i7 = C0017R$string.op_aod_parsons_info_button;
                        i3 = i6;
                        i = i5;
                        i4 = C0008R$drawable.op_parsons_info_image;
                        i2 = i7;
                    }
                    OPCustomClockPreference.this.showInfoDialog(i4, i, i3, i2);
                }
            }
        });
        RecyclerView recyclerView = (RecyclerView) preferenceViewHolder.findViewById(C0010R$id.custom_style_recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(0);
        recyclerView.setLayoutManager(linearLayoutManager);
        ItemEntity itemEntity3 = new ItemEntity(getContext().getString(C0017R$string.aod_clock_default), C0008R$drawable.aod_clock_smart_space_default, 0);
        ItemEntity itemEntity4 = new ItemEntity(getContext().getString(C0017R$string.aod_clock_digital), C0008R$drawable.aod_clock_default, 100);
        ItemEntity itemEntity5 = new ItemEntity(getContext().getString(C0017R$string.aod_clock_none), C0008R$drawable.aod_clock_none, 1);
        ItemEntity itemEntity6 = new ItemEntity(getContext().getString(C0017R$string.aod_clock_digital_1), C0008R$drawable.aod_clock_digital_1, 2);
        ItemEntity itemEntity7 = new ItemEntity(getContext().getString(C0017R$string.aod_clock_digital_2), C0008R$drawable.aod_clock_digital_2, 3);
        ItemEntity itemEntity8 = new ItemEntity(getContext().getString(C0017R$string.aod_clock_text_clock), C0008R$drawable.aod_clock_text_clock, 4);
        ItemEntity itemEntity9 = new ItemEntity(getContext().getString(C0017R$string.aod_clock_bold), C0008R$drawable.aod_clock_bold, 5);
        ItemEntity itemEntity10 = new ItemEntity(getContext().getString(C0017R$string.aod_clock_analog), C0008R$drawable.aod_clock_analog_1, 6);
        ItemEntity itemEntity11 = new ItemEntity(getContext().getString(C0017R$string.aod_clock_analog_1), C0008R$drawable.aod_clock_analog_2, 7);
        ItemEntity itemEntity12 = new ItemEntity(getContext().getString(C0017R$string.aod_clock_analog_2), C0008R$drawable.aod_clock_analog_3, 8);
        ItemEntity itemEntity13 = new ItemEntity(getContext().getString(C0017R$string.aod_clock_minimalism), C0008R$drawable.aod_clock_minimalism_1, 9);
        ItemEntity itemEntity14 = new ItemEntity(getContext().getString(C0017R$string.aod_clock_minimalism_1), C0008R$drawable.aod_clock_minimalism_2, 10);
        ItemEntity itemEntity15 = new ItemEntity(getContext().getString(C0017R$string.aod_clock_mcl), C0008R$drawable.aod_clock_mcl, 40);
        ItemEntity itemEntity16 = new ItemEntity(getContext().getString(C0017R$string.aod_clock_parsons), C0008R$drawable.aod_clock_parsons, 11, true);
        if (OPThemeUtils.isSupportMclTheme() && !TextUtils.equals("18801", SystemProperties.get("ro.boot.project_name"))) {
            this.mItemEntities.add(itemEntity15);
        }
        this.mItemEntities.add(itemEntity3);
        this.mItemEntities.add(itemEntity16);
        this.mItemEntities.add(itemEntity8);
        this.mItemEntities.add(itemEntity9);
        this.mItemEntities.add(itemEntity4);
        this.mItemEntities.add(itemEntity6);
        this.mItemEntities.add(itemEntity7);
        this.mItemEntities.add(itemEntity10);
        this.mItemEntities.add(itemEntity11);
        this.mItemEntities.add(itemEntity12);
        this.mItemEntities.add(itemEntity13);
        this.mItemEntities.add(itemEntity14);
        if (needShowNoneClockStyle()) {
            itemEntity = itemEntity16;
            itemEntity2 = itemEntity5;
            this.mItemEntities.add(itemEntity2);
        } else {
            itemEntity = itemEntity16;
            itemEntity2 = itemEntity5;
        }
        int intForUser = Settings.Secure.getIntForUser(getContext().getContentResolver(), "aod_clock_style", 0, ActivityManager.getCurrentUser());
        Log.v("OPCustomClockPreference", "onBindViewHolder  clock curType = " + intForUser);
        int i = (!OPThemeUtils.isSupportMclTheme() || TextUtils.equals("18801", SystemProperties.get("ro.boot.project_name"))) ? 0 : 1;
        if (intForUser == 0) {
            itemEntity3.selected = true;
            this.mSelectedIndex = i + 0;
        } else if (intForUser == 100) {
            itemEntity4.selected = true;
            this.mSelectedIndex = i + 4;
        } else if (intForUser == 2) {
            itemEntity6.selected = true;
            this.mSelectedIndex = i + 5;
        } else if (intForUser == 3) {
            itemEntity7.selected = true;
            this.mSelectedIndex = i + 6;
        } else if (intForUser == 4) {
            itemEntity8.selected = true;
            this.mSelectedIndex = i + 2;
        } else if (intForUser == 5) {
            itemEntity9.selected = true;
            this.mSelectedIndex = i + 3;
        } else if (intForUser == 6) {
            itemEntity10.selected = true;
            this.mSelectedIndex = i + 7;
        } else if (intForUser == 7) {
            itemEntity11.selected = true;
            this.mSelectedIndex = i + 8;
        } else if (intForUser == 8) {
            itemEntity12.selected = true;
            this.mSelectedIndex = i + 9;
        } else if (intForUser == 9) {
            itemEntity13.selected = true;
            this.mSelectedIndex = i + 10;
        } else if (intForUser == 10) {
            itemEntity14.selected = true;
            this.mSelectedIndex = i + 11;
        } else if (intForUser == 11) {
            itemEntity.selected = true;
            this.mSelectedIndex = i + 1;
        } else if (intForUser == 1) {
            itemEntity2.selected = true;
            this.mSelectedIndex = i + 12;
        } else if (intForUser == 40) {
            itemEntity15.selected = true;
            this.mSelectedIndex = 0;
        }
        changeAdaptive(intForUser);
        recyclerView.addItemDecoration(new OPSpaceItemDecoration(getContext(), this.mItemEntities.size(), (int) getContext().getResources().getDimension(C0007R$dimen.op_control_margin_space4)));
        recyclerView.setAdapter(new ChooseStyleAdapter());
        int i2 = this.mSelectedIndex;
        if (i2 >= 0 && i2 < this.mItemEntities.size()) {
            recyclerView.smoothScrollToPosition(this.mSelectedIndex);
        }
        Button button = (Button) preferenceViewHolder.findViewById(C0010R$id.save_button);
        if (button != null) {
            button.setOnClickListener(new View.OnClickListener() {
                /* class com.oneplus.settings.ui.OPCustomClockPreference.AnonymousClass2 */

                public void onClick(View view) {
                    OPCustomClockPreference.this.saveSelectedClock();
                    if (OPCustomClockPreference.this.mSettingsPreferenceFragment != null) {
                        OPCustomClockPreference.this.mSettingsPreferenceFragment.finish();
                    }
                }
            });
        }
    }

    private boolean needShowNoneClockStyle() {
        FingerprintManager fingerprintManager = this.mFingerprintManager;
        return fingerprintManager != null && fingerprintManager.hasEnrolledFingerprints() && OPUtils.isSupportCustomFingerprint();
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void changeAdaptive(int i) {
        if (i == 40) {
            this.mPreView.setBackgroundResource(C0008R$drawable.op_custom_aodpreview_mcl);
        } else if (i != 100) {
            switch (i) {
                case 0:
                    this.mPreView.setBackgroundResource(C0008R$drawable.op_custom_aodpreview_smart_space_default);
                    break;
                case 1:
                    this.mPreView.setBackgroundResource(C0008R$drawable.op_custom_aodpreview_none);
                    break;
                case 2:
                    this.mPreView.setBackgroundResource(C0008R$drawable.op_custom_aodpreview_digital_1);
                    break;
                case 3:
                    this.mPreView.setBackgroundResource(C0008R$drawable.op_custom_aodpreview_digital_2);
                    break;
                case 4:
                    this.mPreView.setBackgroundResource(C0008R$drawable.op_custom_aodpreview_text_clock);
                    break;
                case 5:
                    this.mPreView.setBackgroundResource(C0008R$drawable.op_custom_aodpreview_bold);
                    break;
                case 6:
                    this.mPreView.setBackgroundResource(C0008R$drawable.op_custom_aodpreview_analog_1);
                    break;
                case 7:
                    this.mPreView.setBackgroundResource(C0008R$drawable.op_custom_aodpreview_analog_2);
                    break;
                case 8:
                    this.mPreView.setBackgroundResource(C0008R$drawable.op_custom_aodpreview_analog_3);
                    break;
                case 9:
                    this.mPreView.setBackgroundResource(C0008R$drawable.op_custom_aodpreview_minimalism_1);
                    break;
                case 10:
                    this.mPreView.setBackgroundResource(C0008R$drawable.op_custom_aodpreview_minimalism_2);
                    break;
                case 11:
                    this.mPreView.setBackgroundResource(C0008R$drawable.op_custom_aodpreview_parsons);
                    break;
            }
        } else {
            this.mPreView.setBackgroundResource(C0008R$drawable.op_custom_aodpreview_default);
        }
        ItemEntity itemEntity = this.mItemEntities.get(this.mSelectedIndex);
        this.mShowInfo.setVisibility((itemEntity == null || !itemEntity.hasInfo) ? 4 : 0);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void showInfoDialog(int i, int i2, int i3, int i4) {
        if (i2 != -1 || i3 != -1 || i4 != -1 || i != -1) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            if (i2 != -1) {
                builder.setTitle(i2);
            }
            if (i3 != -1) {
                builder.setMessage(i3);
            }
            if (i != -1) {
                builder.setCustomImage(i);
            }
            if (i4 != -1) {
                builder.setNegativeButton(i4, (DialogInterface.OnClickListener) null);
            }
            builder.create().show();
        }
    }

    class ChooseStyleAdapter extends RecyclerView.Adapter<OPCustomItemEntityViewHolder> {
        ChooseStyleAdapter() {
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public OPCustomItemEntityViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new OPCustomItemEntityViewHolder(LayoutInflater.from(OPCustomClockPreference.this.getContext()).inflate(C0012R$layout.op_custom_clock_choose_item, (ViewGroup) null));
        }

        public void onBindViewHolder(OPCustomItemEntityViewHolder oPCustomItemEntityViewHolder, int i) {
            ItemEntity itemEntity = (ItemEntity) OPCustomClockPreference.this.mItemEntities.get(i);
            oPCustomItemEntityViewHolder.textView.setText(itemEntity.name);
            oPCustomItemEntityViewHolder.imageView.setImageResource(itemEntity.resId);
            if (itemEntity.selected) {
                oPCustomItemEntityViewHolder.imageViewMask.setVisibility(0);
                oPCustomItemEntityViewHolder.imageView.setSelected(true);
                oPCustomItemEntityViewHolder.textView.setSelected(true);
                OPCustomClockPreference.this.mCurrentVH = oPCustomItemEntityViewHolder;
            } else {
                oPCustomItemEntityViewHolder.imageViewMask.setVisibility(4);
                oPCustomItemEntityViewHolder.imageView.setSelected(false);
                oPCustomItemEntityViewHolder.textView.setSelected(false);
            }
            oPCustomItemEntityViewHolder.itemView.setOnClickListener(new View.OnClickListener(i, itemEntity, oPCustomItemEntityViewHolder) {
                /* class com.oneplus.settings.ui.$$Lambda$OPCustomClockPreference$ChooseStyleAdapter$bsCHOKBpJvwXSJzNoxB5To7ci3A */
                public final /* synthetic */ int f$1;
                public final /* synthetic */ OPCustomClockPreference.ItemEntity f$2;
                public final /* synthetic */ OPCustomItemEntityViewHolder f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void onClick(View view) {
                    OPCustomClockPreference.ChooseStyleAdapter.this.lambda$onBindViewHolder$0$OPCustomClockPreference$ChooseStyleAdapter(this.f$1, this.f$2, this.f$3, view);
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$onBindViewHolder$0 */
        public /* synthetic */ void lambda$onBindViewHolder$0$OPCustomClockPreference$ChooseStyleAdapter(int i, ItemEntity itemEntity, OPCustomItemEntityViewHolder oPCustomItemEntityViewHolder, View view) {
            OPCustomClockPreference.this.mSelectedIndex = i;
            OPCustomClockPreference.this.changeAdaptive(itemEntity.type);
            if (OPCustomClockPreference.this.mCurrentVH != null) {
                if (OPCustomClockPreference.this.mCurrentVH.imageViewMask != null) {
                    OPCustomClockPreference.this.mCurrentVH.imageViewMask.setVisibility(4);
                }
                if (OPCustomClockPreference.this.mCurrentVH.imageView != null) {
                    OPCustomClockPreference.this.mCurrentVH.imageView.setSelected(false);
                }
                if (OPCustomClockPreference.this.mCurrentVH.textView != null) {
                    OPCustomClockPreference.this.mCurrentVH.textView.setSelected(false);
                }
            }
            oPCustomItemEntityViewHolder.imageViewMask.setVisibility(0);
            oPCustomItemEntityViewHolder.imageView.setSelected(true);
            oPCustomItemEntityViewHolder.textView.setSelected(true);
            OPCustomClockPreference.this.mCurrentVH = oPCustomItemEntityViewHolder;
            int i2 = 0;
            while (i2 < OPCustomClockPreference.this.mItemEntities.size()) {
                ((ItemEntity) OPCustomClockPreference.this.mItemEntities.get(i2)).selected = i == i2;
                i2++;
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return OPCustomClockPreference.this.mItemEntities.size();
        }
    }

    /* access modifiers changed from: package-private */
    public static class ItemEntity {
        boolean hasInfo;
        String name;
        int resId;
        boolean selected;
        int type;

        public ItemEntity(String str, int i, int i2) {
            this.selected = false;
            this.hasInfo = false;
            this.name = str;
            this.resId = i;
            this.type = i2;
        }

        public ItemEntity(String str, int i, int i2, boolean z) {
            this(str, i, i2);
            this.hasInfo = z;
        }
    }
}
