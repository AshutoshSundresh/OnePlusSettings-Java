package com.oneplus.settings;

import android.app.ActivityManager;
import android.content.Context;
import android.content.om.IOverlayManager;
import android.content.om.OverlayInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.oneplus.settings.utils.OPApplicationUtils;
import com.oneplus.settings.utils.OPUtils;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class OPNotchDisplayGuideActivity extends BaseActivity implements View.OnClickListener {
    private static final Comparator<OverlayInfo> OVERLAY_INFO_COMPARATOR = Comparator.comparingInt($$Lambda$OPNotchDisplayGuideActivity$lBTk3aV1tGVERBb37MROjQ8ddA.INSTANCE);
    private ActivityManager mAm;
    private Context mContext;
    private TextView mFullScreenModeGuideTitle;
    private Handler mHandler;
    private ImageView mHideNotch;
    private RadioButton mHideNotchBtn;
    private View mHideNotchMode;
    private TextView mHideNotchModeTitle;
    private TextView mHideNotchModeTitleSummary;
    private IOverlayManager mOverlayManager;
    private ImageView mShowNotch;
    private RadioButton mShowNotchBtn;
    private View mShowNotchMode;
    private TextView mShowNotchModeTitle;
    private TextView mShowNotchModeTitleSummary;

    /* access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, com.oneplus.settings.BaseAppCompatActivity, com.oneplus.settings.BaseActivity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(C0012R$layout.op_fullscreen_app_guide_layout);
        this.mContext = getBaseContext();
        this.mHandler = new Handler();
        this.mAm = (ActivityManager) getSystemService("activity");
        this.mContext.getPackageManager();
        ActivityManager.getService();
        this.mOverlayManager = IOverlayManager.Stub.asInterface(ServiceManager.getService("overlay"));
        View findViewById = findViewById(C0010R$id.op_show_notch_mode);
        this.mShowNotchMode = findViewById;
        findViewById.setOnClickListener(this);
        View findViewById2 = findViewById(C0010R$id.op_hide_notch_mode);
        this.mHideNotchMode = findViewById2;
        findViewById2.setOnClickListener(this);
        this.mShowNotchBtn = (RadioButton) findViewById(C0010R$id.op_show_notch_btn);
        this.mHideNotchBtn = (RadioButton) findViewById(C0010R$id.op_hide_notch_btn);
        this.mFullScreenModeGuideTitle = (TextView) findViewById(C0010R$id.fullscreen_mode_guide_title);
        this.mShowNotchModeTitle = (TextView) findViewById(C0010R$id.op_show_notch_mode_title);
        this.mShowNotchModeTitleSummary = (TextView) findViewById(C0010R$id.op_fullscreen_mode_title_summary);
        this.mHideNotchModeTitle = (TextView) findViewById(C0010R$id.op_hide_notch_mode_title);
        this.mHideNotchModeTitleSummary = (TextView) findViewById(C0010R$id.op_hide_notch_mode_title_summary);
        if (OPUtils.isSupportHolePunchFrontCam()) {
            setTitle(getString(C0017R$string.oneplus_front_camera_display_title));
            this.mFullScreenModeGuideTitle.setText(getString(C0017R$string.oneplus_front_camera_display_choose_title));
            this.mShowNotchModeTitle.setText(getString(C0017R$string.oneplus_show_front_camera_display_title));
            this.mShowNotchModeTitleSummary.setText(getString(C0017R$string.oneplus_show_front_camera_display_summary));
            this.mHideNotchModeTitle.setText(getString(C0017R$string.oneplus_hide_front_camera_display_title));
            this.mHideNotchModeTitleSummary.setText(getString(C0017R$string.oneplus_hide_front_camera_display_summary));
        }
        this.mShowNotch = (ImageView) findViewById(C0010R$id.op_show_notch_image);
        this.mHideNotch = (ImageView) findViewById(C0010R$id.op_hide_notch_image);
        if (OPUtils.isBlackModeOn(SettingsBaseApplication.mApplication.getContentResolver())) {
            if (OPUtils.isSM8250Products()) {
                this.mShowNotch.setImageResource(C0008R$drawable.op_front_camera_mode_guide_dark_8_series);
            } else {
                this.mShowNotch.setImageResource(C0008R$drawable.op_fullscreen_mode_guide_dark);
            }
            this.mHideNotch.setImageResource(C0008R$drawable.op_compatibility_mode_guide_dark);
        } else {
            if (OPUtils.isSM8250Products()) {
                this.mShowNotch.setImageResource(C0008R$drawable.op_front_camera_mode_guide_light_8_series);
            } else {
                this.mShowNotch.setImageResource(C0008R$drawable.op_fullscreen_mode_guide_light);
            }
            this.mHideNotch.setImageResource(C0008R$drawable.op_compatibility_mode_guide_light);
        }
        setCurrentMode();
    }

    public void onClick(View view) {
        if (isInMultiWindowMode()) {
            Toast.makeText(this, getString(C0017R$string.feature_not_support_split_screen), 0).show();
        } else if (view == this.mShowNotchMode) {
            this.mShowNotchBtn.setChecked(true);
            this.mHideNotchBtn.setChecked(false);
            this.mShowNotchBtn.setEnabled(false);
            this.mHideNotchBtn.setEnabled(false);
            this.mHandler.postDelayed(new Runnable() {
                /* class com.oneplus.settings.$$Lambda$OPNotchDisplayGuideActivity$I7MhZGV2MxvtCUd5vNy8iL55Lo */

                public final void run() {
                    OPNotchDisplayGuideActivity.this.lambda$onClick$1$OPNotchDisplayGuideActivity();
                }
            }, 2000);
            setOverlay("package_device_default");
            OPUtils.sendAppTracker("notch_display", 1);
        } else if (view == this.mHideNotchMode) {
            this.mShowNotchBtn.setChecked(false);
            this.mHideNotchBtn.setChecked(true);
            this.mShowNotchBtn.setEnabled(false);
            this.mHideNotchBtn.setEnabled(false);
            setOverlay("com.android.internal.display.cutout.emulation.noCutout");
            this.mHandler.postDelayed(new Runnable() {
                /* class com.oneplus.settings.$$Lambda$OPNotchDisplayGuideActivity$FCz1Ru0CGbBUDAMHGoCZNJO4F4 */

                public final void run() {
                    OPNotchDisplayGuideActivity.this.lambda$onClick$2$OPNotchDisplayGuideActivity();
                }
            }, 2000);
            OPUtils.sendAppTracker("notch_display", 0);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onClick$1 */
    public /* synthetic */ void lambda$onClick$1$OPNotchDisplayGuideActivity() {
        this.mShowNotchBtn.setEnabled(true);
        this.mHideNotchBtn.setEnabled(true);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onClick$2 */
    public /* synthetic */ void lambda$onClick$2$OPNotchDisplayGuideActivity() {
        this.mShowNotchBtn.setEnabled(true);
        this.mHideNotchBtn.setEnabled(true);
    }

    private void setCurrentMode() {
        this.mShowNotchBtn.setChecked("package_device_default".equals(getCurrenMode()));
        this.mHideNotchBtn.setChecked("com.android.internal.display.cutout.emulation.noCutout".equals(getCurrenMode()));
    }

    private String getCurrenMode() {
        ArrayList arrayList = new ArrayList();
        String str = "package_device_default";
        arrayList.add(str);
        for (OverlayInfo overlayInfo : getOverlayInfos()) {
            arrayList.add(overlayInfo.packageName);
            if (overlayInfo.isEnabled()) {
                str = (String) arrayList.get(arrayList.size() - 1);
            }
        }
        return str;
    }

    private boolean setOverlay(final String str) {
        OPApplicationUtils.killProcess(this.mAm, true);
        final R orElse = getOverlayInfos().stream().filter($$Lambda$OPNotchDisplayGuideActivity$Mp47syz7Wy6NPVXcC2gvwQMUNKk.INSTANCE).map($$Lambda$OPNotchDisplayGuideActivity$AJTRBoq9BKBRy3dQIWZ1S86ZMY.INSTANCE).findFirst().orElse(null);
        if (("package_device_default".equals(str) && TextUtils.isEmpty(orElse)) || TextUtils.equals(str, orElse)) {
            return true;
        }
        new AsyncTask<Void, Void, Boolean>() {
            /* class com.oneplus.settings.OPNotchDisplayGuideActivity.AnonymousClass1 */

            /* access modifiers changed from: protected */
            public Boolean doInBackground(Void... voidArr) {
                try {
                    if ("package_device_default".equals(str)) {
                        String name = OPNotchDisplayGuideActivity.class.getName();
                        Log.d(name, "set overlay currentPackageName " + orElse);
                        return Boolean.valueOf(OPNotchDisplayGuideActivity.this.mOverlayManager.setEnabled(orElse, false, 0));
                    }
                    String name2 = OPNotchDisplayGuideActivity.class.getName();
                    Log.d(name2, "set overlay packageName " + str);
                    return Boolean.valueOf(OPNotchDisplayGuideActivity.this.mOverlayManager.setEnabledExclusiveInCategory(str, 0));
                } catch (RemoteException e) {
                    Log.w(OPNotchDisplayGuideActivity.class.getName(), "Error enabling overlay.", e);
                    return Boolean.FALSE;
                }
            }

            /* access modifiers changed from: protected */
            public void onPostExecute(Boolean bool) {
                if (!bool.booleanValue()) {
                    Toast.makeText(OPNotchDisplayGuideActivity.this.mContext, C0017R$string.overlay_toast_failed_to_apply, 1).show();
                }
            }
        }.execute(new Void[0]);
        return true;
    }

    private List<OverlayInfo> getOverlayInfos() {
        ArrayList arrayList = new ArrayList();
        try {
            for (OverlayInfo overlayInfo : this.mOverlayManager.getOverlayInfosForTarget("android", 0)) {
                if ("com.android.internal.display_cutout_emulation".equals(overlayInfo.category)) {
                    arrayList.add(overlayInfo);
                }
            }
            arrayList.sort(OVERLAY_INFO_COMPARATOR);
            return arrayList;
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }
}
