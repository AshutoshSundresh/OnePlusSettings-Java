package com.oneplus.settings;

import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.oneplus.settings.utils.OPUtils;

public class OPAuthenticationInformationSettings extends BaseActivity {
    private ImageView mAuthenticationImage;
    private TextView mCmiitIdView;
    private TextView mMadeinChina;
    private TextView mModelTextView;

    /* access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, com.oneplus.settings.BaseAppCompatActivity, com.oneplus.settings.BaseActivity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(C0012R$layout.op_authentication_information_settings);
        setTitle(C0017R$string.oneplus_authentication_information);
        this.mModelTextView = (TextView) findViewById(C0010R$id.authentication_information_model);
        this.mCmiitIdView = (TextView) findViewById(C0010R$id.authentication_information_cmiit_id);
        this.mAuthenticationImage = (ImageView) findViewById(C0010R$id.authentication_information_image);
        if (OPUtils.isWhiteModeOn(getContentResolver())) {
            getWindow().getDecorView().setSystemUiVisibility(8192);
        }
        setDeviceType();
        setCmiitID();
        this.mMadeinChina = (TextView) findViewById(C0010R$id.authentication_information_made_in_china);
        if (OPUtils.isSM8250Products()) {
            this.mMadeinChina.setText(C0017R$string.oneplus_authentication_information_made_in_china_5g);
        }
    }

    private void setDeviceType() {
        String string = getResources().getString(C0017R$string.oneplus_authentication_information_model);
        this.mModelTextView.setText(String.format(string, Build.MODEL));
    }

    private void setCmiitID() {
        String string = getResources().getString(C0017R$string.oneplus_authentication_information_cmiit_id);
        if (Build.MODEL.equalsIgnoreCase("oneplus A3000")) {
            this.mCmiitIdView.setText(String.format(string, "2016CP1331"));
        } else if (Build.MODEL.equalsIgnoreCase("oneplus A3010")) {
            this.mCmiitIdView.setText(String.format(string, "2016CP5088"));
        } else if (Build.MODEL.equalsIgnoreCase("ONEPLUS A5000")) {
            this.mCmiitIdView.setText(String.format(string, "2017CP2198"));
        } else if (Build.MODEL.equalsIgnoreCase("ONEPLUS A5010")) {
            this.mCmiitIdView.setText(String.format(string, "2017CP6039"));
        } else if (Build.MODEL.equalsIgnoreCase("ONEPLUS A6000")) {
            this.mCmiitIdView.setText(String.format(string, "2018CP1307"));
        } else if (Build.MODEL.equalsIgnoreCase(getString(C0017R$string.oneplus_model_for_china_and_india)) || Build.MODEL.equalsIgnoreCase(getString(C0017R$string.oneplus_model_for_europe_and_america))) {
            this.mCmiitIdView.setText(String.format(string, getString(C0017R$string.oneplus_cmittid)));
        } else if (Build.MODEL.equalsIgnoreCase(getString(C0017R$string.oneplus_oneplus_model_18821_for_cn))) {
            this.mCmiitIdView.setText(String.format(string, "2018CP7481"));
        } else if (Build.MODEL.equalsIgnoreCase(getString(C0017R$string.oneplus_oneplus_model_18857_for_cn))) {
            this.mCmiitIdView.setText(String.format(string, "2018CP7482"));
        } else if (Build.MODEL.equalsIgnoreCase(getString(C0017R$string.oneplus_model_19801_for_cn))) {
            this.mCmiitIdView.setText(String.format(string, getString(C0017R$string.oneplus_model_19801_for_cn_cmittid)));
        } else if (Build.MODEL.equalsIgnoreCase(getString(C0017R$string.oneplus_model_18865_for_cn))) {
            this.mCmiitIdView.setText(String.format(string, getString(C0017R$string.oneplus_model_18865_for_cn_cmittid)));
        } else if (Build.MODEL.equalsIgnoreCase(getString(C0017R$string.oneplus_model_19821_for_cn))) {
            this.mCmiitIdView.setText(String.format(string, getString(C0017R$string.oneplus_model_19821_for_cn_cmittid)));
        } else if (Build.MODEL.equalsIgnoreCase(getString(C0017R$string.oneplus_model_19811_for_cn))) {
            this.mCmiitIdView.setText(String.format(string, getString(C0017R$string.oneplus_model_19811_for_cn_cmittid)));
        } else {
            this.mCmiitIdView.setText(String.format(string, "XXXXXXXXXX"));
        }
        if (OPUtils.isBlackModeOn(getContentResolver())) {
            this.mAuthenticationImage.setImageResource(C0008R$drawable.op_authentication_information_image_dark);
        } else {
            this.mAuthenticationImage.setImageResource(C0008R$drawable.op_authentication_information_image_light);
        }
    }

    @Override // com.oneplus.settings.BaseActivity
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != 16908332) {
            return super.onOptionsItemSelected(menuItem);
        }
        finish();
        return true;
    }
}
