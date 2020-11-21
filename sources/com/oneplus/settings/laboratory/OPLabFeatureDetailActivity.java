package com.oneplus.settings.laboratory;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import com.android.settings.C0006R$color;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.oneplus.settings.BaseActivity;
import com.oneplus.settings.laboratory.OPRadioButtinGroup;
import com.oneplus.settings.utils.OPUtils;
import com.oneplus.settings.utils.VibratorSceneUtils;

public class OPLabFeatureDetailActivity extends BaseActivity implements View.OnClickListener, OPRadioButtinGroup.OnRadioGroupClickListener {
    private static Toast mToast;
    private View mActiviteFeatureToggle;
    private TextView mDescriptionSummary;
    private AlertDialog mDialog;
    private ImageButton mDislikeImageButton;
    private String[] mFeatureToggleNames;
    private Intent mIntent;
    private ImageButton mLikeImageButton;
    private OPRadioButtinGroup mMultiToggleGroup;
    private String mOneplusLabFeatureKey;
    private String mOneplusLabFeatureTitle;
    private int mOneplusLabFeatureToggleCount;
    private SharedPreferences mSharedPreferences;
    private Switch mSwitch;
    private long[] mVibratePattern;
    private Vibrator mVibrator;

    /* access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, com.oneplus.settings.BaseAppCompatActivity, com.oneplus.settings.BaseActivity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(C0012R$layout.op_lab_feature_details_activity);
        if (OPUtils.isSupportXVibrate()) {
            this.mVibrator = (Vibrator) getSystemService("vibrator");
        }
        initIntent();
        initView();
    }

    private void initIntent() {
        Intent intent = getIntent();
        this.mIntent = intent;
        this.mOneplusLabFeatureToggleCount = intent.getIntExtra("oneplus_lab_feature_toggle_count", 2);
        this.mFeatureToggleNames = this.mIntent.getStringArrayExtra("oneplus_lab_feature_toggle_names");
        this.mOneplusLabFeatureTitle = this.mIntent.getStringExtra("oneplus_lab_feature_title");
        this.mOneplusLabFeatureKey = this.mIntent.getStringExtra("oneplus_lab_feature_key");
        this.mIntent.getIntExtra("oneplus_lab_feature_icon_id", 0);
        setTitle(this.mOneplusLabFeatureTitle);
    }

    private void initView() {
        this.mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        this.mDescriptionSummary = (TextView) findViewById(C0010R$id.op_lab_feature_description_summary);
        TextView textView = (TextView) findViewById(C0010R$id.op_lab_feature_communiry_title);
        TextView textView2 = (TextView) findViewById(C0010R$id.op_lab_feature_communiry_summary);
        this.mActiviteFeatureToggle = findViewById(C0010R$id.op_lab_feature_toggle);
        this.mSwitch = (Switch) findViewById(C0010R$id.op_lab_feature_switch);
        boolean z = true;
        if ("show_importance_slider".equals(this.mOneplusLabFeatureKey)) {
            Switch r0 = this.mSwitch;
            if (Settings.Secure.getInt(getContentResolver(), this.mOneplusLabFeatureKey, 0) != 1) {
                z = false;
            }
            r0.setChecked(z);
        } else {
            Switch r02 = this.mSwitch;
            if (Settings.System.getInt(getContentResolver(), this.mOneplusLabFeatureKey, 0) != 1) {
                z = false;
            }
            r02.setChecked(z);
        }
        this.mMultiToggleGroup = (OPRadioButtinGroup) findViewById(C0010R$id.op_lab_feature_multi_toggle_group);
        if (isMultiToggle()) {
            this.mMultiToggleGroup.addChild(this.mOneplusLabFeatureToggleCount, this.mFeatureToggleNames);
            this.mMultiToggleGroup.setOnRadioGroupClickListener(this);
            this.mMultiToggleGroup.setSelect(Settings.System.getInt(getContentResolver(), this.mOneplusLabFeatureKey, 0));
            this.mActiviteFeatureToggle.setVisibility(8);
        } else {
            this.mMultiToggleGroup.setVisibility(8);
        }
        this.mLikeImageButton = (ImageButton) findViewById(C0010R$id.op_lab_feature_communiry_like);
        this.mDislikeImageButton = (ImageButton) findViewById(C0010R$id.op_lab_feature_communiry_dislike);
        this.mActiviteFeatureToggle.setOnClickListener(this);
        this.mLikeImageButton.setOnClickListener(this);
        this.mDislikeImageButton.setOnClickListener(this);
        this.mDescriptionSummary.setText(this.mIntent.getStringExtra("oneplus_lab_feature_Summary"));
        setLikeOrDislike();
    }

    public boolean isMultiToggle() {
        return this.mOneplusLabFeatureToggleCount > 2;
    }

    @Override // com.oneplus.settings.BaseActivity
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != 16908332) {
            return super.onOptionsItemSelected(menuItem);
        }
        finish();
        return true;
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity
    public void onDestroy() {
        AlertDialog alertDialog = this.mDialog;
        if (alertDialog != null) {
            alertDialog.dismiss();
            this.mDialog = null;
        }
        super.onDestroy();
    }

    private void confirmAlertDialog() {
        AnonymousClass1 r0 = new DialogInterface.OnClickListener() {
            /* class com.oneplus.settings.laboratory.OPLabFeatureDetailActivity.AnonymousClass1 */

            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == -1) {
                    OPLabFeatureDetailActivity.this.mSwitch.setChecked(true);
                    Settings.System.putInt(OPLabFeatureDetailActivity.this.getContentResolver(), OPLabFeatureDetailActivity.this.mOneplusLabFeatureKey, 1);
                    OPUtils.sendAnalytics("dc_dimming", "status", "1");
                } else if (i == -2) {
                    OPLabFeatureDetailActivity.this.mSwitch.setChecked(false);
                    OPUtils.sendAnalytics("dc_dimming", "status", "0");
                }
            }
        };
        if (this.mDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(C0017R$string.oneplus_is_turn_on_feature);
            builder.setMessage(C0017R$string.oneplus_is_turn_on_feature_summary);
            builder.setPositiveButton(C0017R$string.oneplus_turn_on_feature_ok, r0);
            builder.setNegativeButton(C0017R$string.oneplus_turn_on_feature_cancel, r0);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                /* class com.oneplus.settings.laboratory.OPLabFeatureDetailActivity.AnonymousClass2 */

                public void onDismiss(DialogInterface dialogInterface) {
                    boolean z = false;
                    int i = Settings.System.getInt(OPLabFeatureDetailActivity.this.getContentResolver(), OPLabFeatureDetailActivity.this.mOneplusLabFeatureKey, 0);
                    Switch r2 = OPLabFeatureDetailActivity.this.mSwitch;
                    if (i != 0) {
                        z = true;
                    }
                    r2.setChecked(z);
                }
            });
            this.mDialog = builder.create();
        }
        this.mDialog.show();
    }

    public void onClick(View view) {
        int id = view.getId();
        int i = 1;
        if (id == C0010R$id.op_lab_feature_toggle) {
            if (!"oneplus_dc_dimming_value".equalsIgnoreCase(this.mOneplusLabFeatureKey) || !OPUtils.isHDProject()) {
                if (this.mSwitch.isChecked()) {
                    this.mSwitch.setChecked(false);
                } else {
                    this.mSwitch.setChecked(true);
                }
                if (VibratorSceneUtils.systemVibrateEnabled(this)) {
                    long[] vibratorScenePattern = VibratorSceneUtils.getVibratorScenePattern(this, this.mVibrator, 1003);
                    this.mVibratePattern = vibratorScenePattern;
                    VibratorSceneUtils.vibrateIfNeeded(vibratorScenePattern, this.mVibrator);
                }
                if (!this.mSwitch.isChecked()) {
                    i = 0;
                }
                if ("show_importance_slider".equals(this.mOneplusLabFeatureKey)) {
                    Settings.Secure.putInt(getContentResolver(), this.mOneplusLabFeatureKey, i);
                } else {
                    Settings.System.putInt(getContentResolver(), this.mOneplusLabFeatureKey, i);
                }
                OPUtils.sendAppTracker(this.mOneplusLabFeatureKey, i);
                return;
            }
            if (this.mSwitch.isChecked()) {
                this.mSwitch.setChecked(false);
                Settings.System.putInt(getContentResolver(), this.mOneplusLabFeatureKey, 0);
                OPUtils.sendAnalytics("dc_dimming", "status", "0");
            } else {
                confirmAlertDialog();
            }
            if (VibratorSceneUtils.systemVibrateEnabled(this)) {
                long[] vibratorScenePattern2 = VibratorSceneUtils.getVibratorScenePattern(this, this.mVibrator, 1003);
                this.mVibratePattern = vibratorScenePattern2;
                VibratorSceneUtils.vibrateIfNeeded(vibratorScenePattern2, this.mVibrator);
            }
        } else if (id == C0010R$id.op_lab_feature_communiry_like) {
            saveActitiveHistory(1);
        } else if (id == C0010R$id.op_lab_feature_communiry_dislike) {
            saveActitiveHistory(-1);
        }
    }

    @Override // com.oneplus.settings.laboratory.OPRadioButtinGroup.OnRadioGroupClickListener
    public void onRadioGroupClick(int i) {
        Settings.System.putInt(getContentResolver(), this.mOneplusLabFeatureKey, i);
        OPUtils.sendAppTracker(this.mOneplusLabFeatureKey, i);
    }

    private void saveActitiveHistory(int i) {
        OPUtils.sendAppTracker(this.mOneplusLabFeatureKey + "_feedback", i);
        SharedPreferences.Editor edit = this.mSharedPreferences.edit();
        edit.putInt(this.mOneplusLabFeatureKey, i);
        edit.commit();
        showToastTip();
        setLikeOrDislike();
    }

    private void showToastTip() {
        Toast toast = mToast;
        if (toast != null) {
            toast.cancel();
        }
        Toast makeText = Toast.makeText(getApplicationContext(), C0017R$string.oneplus_lab_feedback_toast, 3000);
        mToast = makeText;
        makeText.show();
    }

    private void setLikeOrDislike() {
        if (this.mSharedPreferences.contains(this.mOneplusLabFeatureKey)) {
            highlightUserChoose(this.mSharedPreferences.getInt(this.mOneplusLabFeatureKey, 1));
            return;
        }
        this.mLikeImageButton.getBackground().setTint(getColor(C0006R$color.oneplus_laboratory_grey_color));
        this.mDislikeImageButton.getBackground().setTint(getColor(C0006R$color.oneplus_laboratory_grey_color));
        this.mLikeImageButton.setImageResource(C0008R$drawable.op_ic_oneplus_lab_feature_like);
        this.mDislikeImageButton.setImageResource(C0008R$drawable.op_ic_oneplus_lab_feature_dislike);
    }

    private void highlightUserChoose(int i) {
        if (i == 1) {
            this.mLikeImageButton.getBackground().setTint(getColor(C0006R$color.oneplus_accent_color));
            this.mDislikeImageButton.getBackground().setTint(getColor(C0006R$color.oneplus_laboratory_grey_color));
            this.mLikeImageButton.setImageResource(C0008R$drawable.op_ic_oneplus_lab_feature_like_fill);
            this.mDislikeImageButton.setImageResource(C0008R$drawable.op_ic_oneplus_lab_feature_dislike);
        } else if (i == -1) {
            this.mLikeImageButton.getBackground().setTint(getColor(C0006R$color.oneplus_laboratory_grey_color));
            this.mDislikeImageButton.getBackground().setTint(getColor(C0006R$color.oneplus_accent_color));
            this.mLikeImageButton.setImageResource(C0008R$drawable.op_ic_oneplus_lab_feature_like);
            this.mDislikeImageButton.setImageResource(C0008R$drawable.op_ic_oneplus_lab_feature_dislike_fill);
        } else {
            this.mLikeImageButton.setImageResource(C0008R$drawable.op_ic_oneplus_lab_feature_like);
            this.mDislikeImageButton.setImageResource(C0008R$drawable.op_ic_oneplus_lab_feature_dislike);
            this.mLikeImageButton.getBackground().setTint(getColor(C0006R$color.oneplus_laboratory_grey_color));
            this.mDislikeImageButton.getBackground().setTint(getColor(C0006R$color.oneplus_laboratory_grey_color));
        }
    }
}
