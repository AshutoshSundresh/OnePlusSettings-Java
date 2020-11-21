package com.oneplus.settings.aboutphone;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.applications.PackageManagerWrapper;
import com.android.settingslib.DeviceInfoUtils;

public class OPSecurityPatchLevel extends Preference {
    private static final Uri INTENT_URI_DATA = Uri.parse("https://source.android.com/security/bulletin/");
    private Context mContext;
    private PackageManagerWrapper mPackageManager;

    public OPSecurityPatchLevel(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        initViews(context);
    }

    public OPSecurityPatchLevel(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initViews(context);
    }

    public OPSecurityPatchLevel(Context context) {
        super(context);
        initViews(context);
    }

    private void initViews(Context context) {
        this.mContext = context;
        this.mPackageManager = new PackageManagerWrapper(context.getPackageManager());
        setLayoutResource(C0012R$layout.op_security_patch_level);
    }

    /* access modifiers changed from: protected */
    @Override // androidx.preference.Preference
    public void onClick() {
        super.onClick();
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.setData(INTENT_URI_DATA);
        if (this.mPackageManager.queryIntentActivities(intent, 0).isEmpty()) {
            Log.w(OPSecurityPatchLevel.class.getName(), "Stop click action on SECURITY_PATCH_VALUE_ID : queryIntentActivities() returns empty");
        } else {
            this.mContext.startActivity(intent);
        }
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        String securityPatch = DeviceInfoUtils.getSecurityPatch();
        LinearLayout linearLayout = (LinearLayout) preferenceViewHolder.findViewById(C0010R$id.security_patch);
        TextView textView = (TextView) preferenceViewHolder.findViewById(C0010R$id.security_patch_level_value);
        if (textView != null && securityPatch != null) {
            textView.setText(securityPatch);
        }
    }
}
