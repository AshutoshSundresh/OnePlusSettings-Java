package com.oneplus.settings.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.oneplus.settings.utils.CircleCrop;
import com.oneplus.settings.utils.OPUtils;
import java.lang.ref.WeakReference;
import java.util.Map;

public class OPMemberPreference extends Preference {
    private Context mContext;
    private Handler mHandler = new MyHandler();
    private OPMemberImageView mIvAvatarBg;
    private OPMemberImageView mIvAvatarTag;
    private LinearLayout mLlClickBg;
    private LinearLayout mLlContent;
    private TextView mTvContent;
    private TextView mTvNew;
    private TextView mTvTitle;
    private double newVersionValue = 0.0d;

    public OPMemberPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        initViews(context);
    }

    public OPMemberPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        initViews(context);
    }

    public OPMemberPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initViews(context);
    }

    public OPMemberPreference(Context context) {
        super(context);
        initViews(context);
    }

    private void initViews(Context context) {
        this.mContext = context;
        setLayoutResource(C0012R$layout.op_preference_member);
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        OPAdjustWidthLayout oPAdjustWidthLayout = (OPAdjustWidthLayout) preferenceViewHolder.findViewById(C0010R$id.op_adjust_width_layout);
        this.mLlContent = (LinearLayout) preferenceViewHolder.findViewById(C0010R$id.ll_content);
        this.mLlClickBg = (LinearLayout) preferenceViewHolder.findViewById(C0010R$id.ll_click_bg);
        this.mTvTitle = (TextView) preferenceViewHolder.findViewById(C0010R$id.tv_title);
        this.mTvContent = (TextView) preferenceViewHolder.findViewById(C0010R$id.tv_content);
        this.mIvAvatarBg = (OPMemberImageView) preferenceViewHolder.findViewById(C0010R$id.iv_avatar_bg);
        this.mIvAvatarTag = (OPMemberImageView) preferenceViewHolder.findViewById(C0010R$id.iv_avatar_tag);
        this.mTvNew = (TextView) preferenceViewHolder.findViewById(C0010R$id.tv_new);
        getMemberInfo();
        setClickBgHeight();
    }

    public void setData(Map<String, String> map) {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.mContext);
        SharedPreferences.Editor edit = defaultSharedPreferences.edit();
        edit.putString("member_title", map.get("member_title"));
        edit.putString("member_content", map.get("member_content"));
        edit.putString("member_avatar", map.get("member_avatar"));
        edit.putString("member_icon", map.get("member_icon"));
        edit.putString("member_new_version", map.get("member_new_version"));
        if (TextUtils.isEmpty(defaultSharedPreferences.getString("token", ""))) {
            edit.putString("token", map.get("token"));
        }
        edit.apply();
        Message obtainMessage = this.mHandler.obtainMessage();
        obtainMessage.what = 0;
        obtainMessage.obj = map;
        this.mHandler.sendMessageDelayed(obtainMessage, 200);
    }

    private void getMemberInfo() {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.mContext);
        String string = this.mContext.getString(isIndia() ? C0017R$string.op_member_title : C0017R$string.op_china_member_title);
        String string2 = this.mContext.getString(isIndia() ? C0017R$string.op_member_description : C0017R$string.op_china_member_description);
        String string3 = defaultSharedPreferences.getString("member_title", string);
        String string4 = defaultSharedPreferences.getString("member_avatar", "");
        String string5 = defaultSharedPreferences.getString("token", "");
        this.mTvContent.setText(defaultSharedPreferences.getString("member_content", string2));
        this.mTvTitle.setText(string3);
        setAvatar(string4, string3, string5);
        setIconState(defaultSharedPreferences.getString("member_icon", ""));
        setNewVersionVis(defaultSharedPreferences.getString("member_new_version", ""));
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void setViewData(Map<String, String> map) {
        String string = PreferenceManager.getDefaultSharedPreferences(this.mContext).getString("token", "");
        if (TextUtils.isEmpty(string)) {
            string = map.get("token");
        }
        if (map != null) {
            TextView textView = this.mTvTitle;
            if (textView != null) {
                textView.setText(map.get("member_title"));
            }
            TextView textView2 = this.mTvContent;
            if (textView2 != null) {
                textView2.setText(map.get("member_content"));
            }
            String str = map.get("member_avatar");
            if (this.mIvAvatarBg != null) {
                setAvatar(str, map.get("member_title"), string);
            }
            setIconState(map.get("member_icon"));
            setNewVersionVis(map.get("member_new_version"));
            setClickBgHeight();
        }
    }

    private void setIconState(String str) {
        try {
            if (this.mIvAvatarTag != null && !TextUtils.isEmpty(str)) {
                this.mIvAvatarTag.setVisibility(0);
                ((RequestBuilder) ((RequestBuilder) ((RequestBuilder) Glide.with(this.mContext).load(str).centerCrop()).transform(new CircleCrop())).placeholder(this.mIvAvatarTag.getDrawable())).into(this.mIvAvatarTag);
            } else if (this.mIvAvatarTag != null && TextUtils.isEmpty(str)) {
                this.mIvAvatarTag.setVisibility(4);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setNewVersionVis(String str) {
        try {
            String string = PreferenceManager.getDefaultSharedPreferences(this.mContext).getString("member_local_version", "0.0");
            if (!TextUtils.isEmpty(str) && !TextUtils.isEmpty(string)) {
                this.newVersionValue = Double.valueOf(str).doubleValue();
                if (this.newVersionValue <= Double.valueOf(string).doubleValue()) {
                    this.mTvNew.setVisibility(8);
                } else if (this.mTvNew.getVisibility() == 8) {
                    this.mTvNew.setVisibility(0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clearNew() {
        this.mTvNew.setVisibility(8);
        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(this.mContext).edit();
        edit.putString("member_local_version", String.valueOf(this.newVersionValue));
        edit.apply();
    }

    private void setAvatar(String str, String str2, String str3) {
        try {
            if (!TextUtils.isEmpty(str)) {
                ((RequestBuilder) ((RequestBuilder) ((RequestBuilder) Glide.with(this.mContext).load(str).centerCrop()).transform(new CircleCrop())).placeholder(this.mIvAvatarBg.getDrawable())).into(this.mIvAvatarBg);
            } else if (!TextUtils.isEmpty(str3)) {
                this.mIvAvatarBg.setImageDrawable(new OPDefaultAvatarDrawable(this.mContext, str2, str3));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setClickBgHeight() {
        LinearLayout linearLayout = this.mLlContent;
        if (linearLayout != null && this.mLlClickBg != null) {
            linearLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                /* class com.oneplus.settings.ui.OPMemberPreference.AnonymousClass1 */

                public void onGlobalLayout() {
                    Log.d("OPMemberPreference", "mLlContent.getHeight():" + OPMemberPreference.this.mLlContent.getHeight());
                    ViewGroup.LayoutParams layoutParams = OPMemberPreference.this.mLlClickBg.getLayoutParams();
                    layoutParams.height = OPMemberPreference.this.mLlContent.getHeight();
                    OPMemberPreference.this.mLlClickBg.setLayoutParams(layoutParams);
                    OPMemberPreference.this.mLlContent.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });
        }
    }

    private boolean isIndia() {
        return OPUtils.isIndia() && OPUtils.isAppExist(this.mContext, "com.oneplus.membership");
    }

    private static class MyHandler extends Handler {
        private final WeakReference<OPMemberPreference> mPreference;

        private MyHandler(OPMemberPreference oPMemberPreference) {
            this.mPreference = new WeakReference<>(oPMemberPreference);
        }

        public void handleMessage(Message message) {
            super.handleMessage(message);
            OPMemberPreference oPMemberPreference = this.mPreference.get();
            if (oPMemberPreference != null && message.what == 0) {
                oPMemberPreference.setViewData((Map) message.obj);
            }
        }
    }
}
