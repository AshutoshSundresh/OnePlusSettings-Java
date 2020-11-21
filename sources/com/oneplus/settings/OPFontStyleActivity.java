package com.oneplus.settings;

import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.settings.C0007R$dimen;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.oneplus.compat.util.OpThemeNative;
import com.oneplus.settings.OPFontStyleActivity;
import com.oneplus.settings.ui.OPSpaceItemDecoration;
import com.oneplus.settings.utils.OPUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OPFontStyleActivity extends BaseActivity implements View.OnClickListener, OnPressListener {
    private static int mLastFontValue = -1;
    private FontViewHolder mCurrentVH;
    private int mFontValue = 1;
    private List<ItemEntity> mItemEntities = new ArrayList();
    private TextView mPreviewText1;
    private TextView mPreviewText2;
    private TextView mPreviewText3;
    private TextView mPreviewText4;
    protected int mSelectedIndex;

    /* access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, com.oneplus.settings.BaseAppCompatActivity, com.oneplus.settings.BaseActivity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        mLastFontValue = Settings.System.getIntForUser(getContentResolver(), "oem_font_mode", 1, 0);
        Log.d("OPFontStyleActivity", "onCreate mLastFontValue = " + mLastFontValue);
        setTitle(C0017R$string.oneplus_font_style);
        setContentView(C0012R$layout.op_font_style_newdisplay);
        setOnPressListener(this);
        for (Integer num : Typeface.opGetFontIDsForUser(UserHandle.myUserId())) {
            Typeface opGetIsolatedTypeface = Typeface.opGetIsolatedTypeface(num.intValue(), UserHandle.myUserId(), "sans-serif-medium");
            Typeface opGetIsolatedTypeface2 = Typeface.opGetIsolatedTypeface(num.intValue(), UserHandle.myUserId(), "sans-serif-book");
            Resources resources = getResources();
            String otherPackageString = OPUtils.getOtherPackageString(resources, "com.oneplus", "oneplus_oem_font_name_" + num);
            Log.d("OPFontStyleActivity", "fontId = " + num);
            Log.d("OPFontStyleActivity", "name = " + otherPackageString);
            ItemEntity itemEntity = new ItemEntity(num.intValue(), opGetIsolatedTypeface, opGetIsolatedTypeface2, otherPackageString);
            if (mLastFontValue == num.intValue()) {
                itemEntity.selected = true;
                this.mSelectedIndex = this.mItemEntities.size();
                Log.d("OPFontStyleActivity", "mSelectedIndex = " + this.mSelectedIndex);
            }
            this.mItemEntities.add(itemEntity);
        }
        this.mFontValue = mLastFontValue;
        this.mPreviewText1 = (TextView) findViewById(C0010R$id.preview_text1);
        this.mPreviewText2 = (TextView) findViewById(C0010R$id.preview_text2);
        this.mPreviewText3 = (TextView) findViewById(C0010R$id.preview_text3);
        this.mPreviewText4 = (TextView) findViewById(C0010R$id.preview_text4);
        modifyPreviewText();
        RecyclerView recyclerView = (RecyclerView) findViewById(C0010R$id.font_style_recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(0);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new OPSpaceItemDecoration(this, this.mItemEntities.size(), (int) getResources().getDimension(C0007R$dimen.op_control_margin_space4)));
        recyclerView.setAdapter(new ChooseFontStyleAdapter());
        int i = this.mSelectedIndex;
        if (i >= 0 && i < this.mItemEntities.size()) {
            Log.d("OPFontStyleActivity", "scrollToPosition");
            linearLayoutManager.scrollToPosition(this.mSelectedIndex);
        }
        ((Button) findViewById(C0010R$id.save_button)).setOnClickListener(this);
    }

    /* access modifiers changed from: protected */
    @Override // com.oneplus.settings.BaseActivity
    public boolean needShowWarningDialog() {
        return mLastFontValue != this.mFontValue;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    /* access modifiers changed from: package-private */
    public class ChooseFontStyleAdapter extends RecyclerView.Adapter<FontViewHolder> {
        ChooseFontStyleAdapter() {
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public FontViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View inflate = LayoutInflater.from(OPFontStyleActivity.this).inflate(C0012R$layout.op_font_style_choose_item, (ViewGroup) null);
            if (OPFontStyleActivity.this.mItemEntities.size() == 2) {
                inflate.setPadding(20, 0, 20, 0);
            }
            return new FontViewHolder(inflate);
        }

        public void onBindViewHolder(FontViewHolder fontViewHolder, int i) {
            ItemEntity itemEntity = (ItemEntity) OPFontStyleActivity.this.mItemEntities.get(i);
            fontViewHolder.fontTitle.setTypeface(itemEntity.title);
            fontViewHolder.fontFlag.setTypeface(itemEntity.content);
            fontViewHolder.fontFlag.setText(itemEntity.name);
            if (itemEntity.selected) {
                fontViewHolder.fontMask.setVisibility(0);
                fontViewHolder.fontTitle.setSelected(true);
                fontViewHolder.fontFlag.setSelected(true);
                OPFontStyleActivity.this.mCurrentVH = fontViewHolder;
            } else {
                fontViewHolder.fontMask.setVisibility(8);
                fontViewHolder.fontTitle.setSelected(false);
                fontViewHolder.fontFlag.setSelected(false);
            }
            fontViewHolder.itemView.setOnClickListener(new View.OnClickListener(i, fontViewHolder) {
                /* class com.oneplus.settings.$$Lambda$OPFontStyleActivity$ChooseFontStyleAdapter$2bQmhusvAh2JmeWOZtZJI1e1XK0 */
                public final /* synthetic */ int f$1;
                public final /* synthetic */ OPFontStyleActivity.FontViewHolder f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void onClick(View view) {
                    OPFontStyleActivity.ChooseFontStyleAdapter.this.lambda$onBindViewHolder$0$OPFontStyleActivity$ChooseFontStyleAdapter(this.f$1, this.f$2, view);
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$onBindViewHolder$0 */
        public /* synthetic */ void lambda$onBindViewHolder$0$OPFontStyleActivity$ChooseFontStyleAdapter(int i, FontViewHolder fontViewHolder, View view) {
            OPFontStyleActivity oPFontStyleActivity = OPFontStyleActivity.this;
            if (oPFontStyleActivity.mSelectedIndex != i) {
                if (oPFontStyleActivity.mCurrentVH != null) {
                    if (OPFontStyleActivity.this.mCurrentVH.fontMask != null) {
                        OPFontStyleActivity.this.mCurrentVH.fontMask.setVisibility(8);
                    }
                    if (OPFontStyleActivity.this.mCurrentVH.fontTitle != null) {
                        OPFontStyleActivity.this.mCurrentVH.fontTitle.setSelected(false);
                    }
                    if (OPFontStyleActivity.this.mCurrentVH.fontFlag != null) {
                        OPFontStyleActivity.this.mCurrentVH.fontFlag.setSelected(false);
                    }
                }
                fontViewHolder.fontMask.setVisibility(0);
                fontViewHolder.fontTitle.setSelected(true);
                fontViewHolder.fontFlag.setSelected(true);
                OPFontStyleActivity oPFontStyleActivity2 = OPFontStyleActivity.this;
                oPFontStyleActivity2.mSelectedIndex = i;
                oPFontStyleActivity2.mCurrentVH = fontViewHolder;
                for (int i2 = 0; i2 < OPFontStyleActivity.this.mItemEntities.size(); i2++) {
                    if (i == i2) {
                        ((ItemEntity) OPFontStyleActivity.this.mItemEntities.get(i2)).selected = true;
                    } else {
                        ((ItemEntity) OPFontStyleActivity.this.mItemEntities.get(i2)).selected = false;
                    }
                }
                OPFontStyleActivity oPFontStyleActivity3 = OPFontStyleActivity.this;
                oPFontStyleActivity3.mFontValue = ((ItemEntity) oPFontStyleActivity3.mItemEntities.get(i)).fontType;
                OPFontStyleActivity.this.modifyPreviewText();
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return OPFontStyleActivity.this.mItemEntities.size();
        }
    }

    /* access modifiers changed from: package-private */
    public static class FontViewHolder extends RecyclerView.ViewHolder {
        TextView fontFlag;
        ImageView fontMask;
        TextView fontTitle;

        FontViewHolder(View view) {
            super(view);
            this.fontTitle = (TextView) view.findViewById(C0010R$id.font_title);
            this.fontMask = (ImageView) view.findViewById(C0010R$id.font_mask);
            this.fontFlag = (TextView) view.findViewById(C0010R$id.font_flag);
        }
    }

    /* access modifiers changed from: package-private */
    public static class ItemEntity {
        Typeface content;
        int fontType;
        String name;
        boolean selected = false;
        Typeface title;

        ItemEntity(int i, Typeface typeface, Typeface typeface2, String str) {
            this.fontType = i;
            this.title = typeface;
            this.content = typeface2;
            this.name = str;
        }
    }

    @Override // androidx.fragment.app.FragmentActivity
    public void onResume() {
        super.onResume();
        this.mFontValue = Settings.System.getIntForUser(getContentResolver(), "oem_font_mode", 1, 0);
    }

    @Override // com.oneplus.settings.OnPressListener
    public void onCancelPressed() {
        finish();
    }

    @Override // com.oneplus.settings.BaseActivity
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != 0) {
            return super.onOptionsItemSelected(menuItem);
        }
        int i = this.mFontValue;
        if (i == 1) {
            OPUtils.sendAnalytics("font", "status", "noto");
        } else if (i == 2) {
            OPUtils.sendAnalytics("font", "status", "slate");
        } else if (i == 100) {
            OPUtils.sendAnalytics("font", "status", "yuan");
        } else if (i == 101) {
            OPUtils.sendAnalytics("font", "status", "kai");
        }
        finish();
        new Handler().postDelayed(new Runnable() {
            /* class com.oneplus.settings.OPFontStyleActivity.AnonymousClass1 */

            public void run() {
                OPFontStyleActivity oPFontStyleActivity = OPFontStyleActivity.this;
                oPFontStyleActivity.setFontStyle(oPFontStyleActivity.mFontValue);
            }
        }, 200);
        return true;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void setFontStyle(int i) {
        Settings.System.putInt(getContentResolver(), "oem_font_mode", i);
        HashMap hashMap = new HashMap();
        hashMap.put("oneplus_dynamicfont", String.valueOf(i));
        OpThemeNative.enableTheme(this, hashMap);
    }

    public void onClick(View view) {
        if (view.getId() == C0010R$id.save_button) {
            int i = this.mFontValue;
            if (i == 1) {
                OPUtils.sendAnalytics("font", "status", "noto");
            } else if (i == 2) {
                OPUtils.sendAnalytics("font", "status", "slate");
            } else if (i == 100) {
                OPUtils.sendAnalytics("font", "status", "yuan");
            } else if (i == 101) {
                OPUtils.sendAnalytics("font", "status", "kai");
            }
            finish();
            new Handler().postDelayed(new Runnable() {
                /* class com.oneplus.settings.OPFontStyleActivity.AnonymousClass2 */

                public void run() {
                    OPFontStyleActivity oPFontStyleActivity = OPFontStyleActivity.this;
                    oPFontStyleActivity.setFontStyle(oPFontStyleActivity.mFontValue);
                }
            }, 200);
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void modifyPreviewText() {
        TextView textView = this.mPreviewText1;
        if (textView != null) {
            textView.setTypeface(Typeface.getTypeface(this.mFontValue, "sans-serif-medium"));
        }
        TextView textView2 = this.mPreviewText2;
        if (textView2 != null) {
            textView2.setTypeface(Typeface.getTypeface(this.mFontValue, "sans-serif-medium"));
        }
        TextView textView3 = this.mPreviewText3;
        if (textView3 != null) {
            textView3.setTypeface(Typeface.getTypeface(this.mFontValue, "sans-serif-medium"));
        }
        TextView textView4 = this.mPreviewText4;
        if (textView4 != null) {
            textView4.setTypeface(Typeface.getTypeface(this.mFontValue, "sans-serif-medium"));
        }
    }

    @Override // androidx.activity.ComponentActivity
    public void onBackPressed() {
        performBackEvent();
    }
}
