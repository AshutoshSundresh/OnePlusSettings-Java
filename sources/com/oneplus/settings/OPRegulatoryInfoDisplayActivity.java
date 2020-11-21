package com.oneplus.settings;

import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.oneplus.settings.utils.OPUtils;

public class OPRegulatoryInfoDisplayActivity extends BaseActivity {
    private TextView mBuildModel;
    private View mIndiaSarInfo;
    private TextView mIndiaSarInfoBody;
    private TextView mIndiaSarInfoHead;
    private View mNorthAmericaAuthenticationInfo;
    private View mNorthAmericaSaudiArabia;
    private TextView mRegulatoryInfoCanada;
    private TextView mRegulatoryInfoCanadaCan;
    private TextView mRegulatoryInfoCanadaIC;
    private TextView mRegulatoryInfoSaudiArabiaId;
    private TextView mRegulatoryInfoUSFCCID;
    private TextView mRegulatoryInfoUSHacRating;
    private TextView mRegulatoryInfoUSSummary;
    private View mSaudiArabiaAuthenticationInfo;

    /* access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, com.oneplus.settings.BaseAppCompatActivity, com.oneplus.settings.BaseActivity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (OPUtils.isWhiteModeOn(getContentResolver())) {
            getWindow().getDecorView().setSystemUiVisibility(8192);
        }
        noDisplayRegulatory();
        setContentView(C0012R$layout.op_regulatory_info);
        this.mBuildModel = (TextView) findViewById(C0010R$id.oneplus_model);
        this.mRegulatoryInfoUSFCCID = (TextView) findViewById(C0010R$id.regulatory_info_us_fcc_id);
        this.mRegulatoryInfoCanada = (TextView) findViewById(C0010R$id.regulatory_info_canada);
        this.mRegulatoryInfoCanadaIC = (TextView) findViewById(C0010R$id.regulatory_info_canada_ic);
        this.mRegulatoryInfoCanadaCan = (TextView) findViewById(C0010R$id.regulatory_info_canada_can);
        this.mNorthAmericaAuthenticationInfo = findViewById(C0010R$id.north_america_authentication_info);
        this.mNorthAmericaSaudiArabia = findViewById(C0010R$id.north_america_saudi_arabia_authentication_info);
        this.mIndiaSarInfo = findViewById(C0010R$id.india_sar_info);
        this.mIndiaSarInfoHead = (TextView) findViewById(C0010R$id.india_sar_info_head);
        this.mIndiaSarInfoBody = (TextView) findViewById(C0010R$id.india_sar_info_body);
        this.mSaudiArabiaAuthenticationInfo = findViewById(C0010R$id.saudi_arabia_authentication_info);
        this.mRegulatoryInfoSaudiArabiaId = (TextView) findViewById(C0010R$id.regulatory_info_saudi_arabia_id);
        this.mRegulatoryInfoUSHacRating = (TextView) findViewById(C0010R$id.regulatory_info_us_hac_rating);
        this.mRegulatoryInfoUSSummary = (TextView) findViewById(C0010R$id.regulatory_info_us_summary);
        if (isShowIndiaSar()) {
            showIndiaSar();
        } else if (isShowSaudiArabiaInfo()) {
            this.mBuildModel.setText(getString(C0017R$string.oneplus_model) + " " + Build.MODEL);
            showSaudiArabiaInfo();
        } else {
            if (isShowCanadaInfo()) {
                showCanadaInfo();
            }
            this.mBuildModel.setText(getString(C0017R$string.oneplus_model) + " " + Build.MODEL);
            StringBuilder sb = new StringBuilder();
            sb.append(getString(C0017R$string.oneplus_regulatory_info_us_fcc_id));
            sb.append(getUSFccID());
            this.mRegulatoryInfoUSFCCID.setText(sb.toString());
        }
        show7BeforeProjectRegulatoryInfo();
    }

    private void noDisplayRegulatory() {
        if (OPUtils.isnoDisplaySarValueProject() || ((Build.MODEL.equals("GM1913") || Build.MODEL.equals("GM1903") || Build.MODEL.equals("HD1903") || Build.MODEL.equals("HD1913")) && OPUtils.isEUVersion())) {
            finish();
        }
    }

    private void show7BeforeProjectRegulatoryInfo() {
        if (Build.MODEL.equals("ONEPLUS A6003")) {
            this.mRegulatoryInfoUSSummary.setVisibility(8);
            this.mRegulatoryInfoUSHacRating.setText(C0017R$string.oneplus_a6003_hac_rating);
        } else if (Build.MODEL.equals("ONEPLUS A5000")) {
            this.mRegulatoryInfoUSSummary.setVisibility(8);
            this.mRegulatoryInfoUSHacRating.setVisibility(8);
        } else if (Build.MODEL.equals("ONEPLUS A5010")) {
            this.mRegulatoryInfoUSSummary.setVisibility(8);
            this.mRegulatoryInfoUSHacRating.setVisibility(8);
            this.mIndiaSarInfo.setVisibility(0);
            String string = getString(C0017R$string.oneplus_a5010_in_head);
            String string2 = getString(C0017R$string.oneplus_a5010_in_body);
            this.mIndiaSarInfoHead.setText(getString(C0017R$string.oneplus_india_sar_info_head, new Object[]{string}));
            this.mIndiaSarInfoBody.setText(getString(C0017R$string.oneplus_india_sar_info_body, new Object[]{string2}));
        }
        if (Build.MODEL.equals("ONEPLUS A6013") && !OPUtils.isSupportUstMode() && OPUtils.isO2()) {
            showSaudiArabiaInfo();
        }
    }

    private boolean isShowCanadaInfo() {
        return Build.MODEL.equals(getString(C0017R$string.oneplus_model_19811_for_us)) || Build.MODEL.equals(getString(C0017R$string.oneplus_model_19821_for_us)) || Build.MODEL.equalsIgnoreCase("GM1917") || Build.MODEL.equalsIgnoreCase("GM1905") || Build.MODEL.equalsIgnoreCase("HD1905") || Build.MODEL.equals("ONEPLUS A6003") || Build.MODEL.equals("ONEPLUS A6013") || Build.MODEL.equals("ONEPLUS A5000") || Build.MODEL.equals("ONEPLUS A5010");
    }

    private void showCanadaInfo() {
        this.mRegulatoryInfoCanada.setVisibility(0);
        this.mRegulatoryInfoUSFCCID.setVisibility(0);
        this.mRegulatoryInfoCanadaIC.setVisibility(0);
        this.mRegulatoryInfoCanadaCan.setVisibility(0);
        setCanadaIC();
    }

    private boolean isShowIndiaSar() {
        return Build.MODEL.equalsIgnoreCase(getString(C0017R$string.oneplus_model_19811_for_in)) || Build.MODEL.equalsIgnoreCase(getString(C0017R$string.oneplus_model_19821_for_in)) || Build.MODEL.equalsIgnoreCase("GM1911") || Build.MODEL.equalsIgnoreCase("GM1901") || Build.MODEL.equalsIgnoreCase("HD1901") || Build.MODEL.equalsIgnoreCase("HD1911") || Build.MODEL.equalsIgnoreCase("ONEPLUS A6000") || Build.MODEL.equalsIgnoreCase("ONEPLUS A6010");
    }

    private void showIndiaSar() {
        String str;
        this.mNorthAmericaSaudiArabia.setVisibility(8);
        this.mIndiaSarInfo.setVisibility(0);
        String str2 = "XXX";
        if (Build.MODEL.equalsIgnoreCase(getString(C0017R$string.oneplus_model_19811_for_in))) {
            str2 = getString(C0017R$string.oneplus_19811_in_head);
            str = getString(C0017R$string.oneplus_19811_in_body);
        } else if (Build.MODEL.equalsIgnoreCase(getString(C0017R$string.oneplus_model_19821_for_in))) {
            str2 = getString(C0017R$string.oneplus_19821_in_head);
            str = getString(C0017R$string.oneplus_19821_in_body);
        } else if (Build.MODEL.equalsIgnoreCase("GM1911")) {
            str2 = getString(C0017R$string.oneplus_18821_in_head);
            str = getString(C0017R$string.oneplus_18821_in_body);
        } else if (Build.MODEL.equalsIgnoreCase("GM1901")) {
            str2 = getString(C0017R$string.oneplus_18857_in_head);
            str = getString(C0017R$string.oneplus_18857_in_body);
        } else if (Build.MODEL.equalsIgnoreCase("HD1901")) {
            str2 = getString(C0017R$string.oneplus_18865_in_head);
            str = getString(C0017R$string.oneplus_18865_in_body);
        } else if (Build.MODEL.equalsIgnoreCase("HD1911")) {
            str2 = getString(C0017R$string.oneplus_19801_in_head);
            str = getString(C0017R$string.oneplus_19801_in_body);
        } else if (Build.MODEL.equalsIgnoreCase("ONEPLUS A6000")) {
            str2 = getString(C0017R$string.oneplus_a6000_in_head);
            str = getString(C0017R$string.oneplus_a6000_in_body);
        } else if (Build.MODEL.equalsIgnoreCase("ONEPLUS A6010")) {
            str2 = getString(C0017R$string.oneplus_a6010_in_head);
            str = getString(C0017R$string.oneplus_a6010_in_body);
        } else {
            str = str2;
        }
        this.mIndiaSarInfoHead.setText(getString(C0017R$string.oneplus_india_sar_info_head, new Object[]{str2}));
        this.mIndiaSarInfoBody.setText(getString(C0017R$string.oneplus_india_sar_info_body, new Object[]{str}));
    }

    private void setCanadaIC() {
        String string = getString(C0017R$string.oneplus_regulatory_info_canada_ic);
        if (Build.MODEL.equals(getString(C0017R$string.oneplus_model_19811_for_us))) {
            string = string + getString(C0017R$string.oneplus_19811_canada_ic);
        } else if (Build.MODEL.equals(getString(C0017R$string.oneplus_model_19821_for_us))) {
            string = string + getString(C0017R$string.oneplus_19821_canada_ic);
        } else if (Build.MODEL.equalsIgnoreCase("GM1917")) {
            string = string + getString(C0017R$string.oneplus_18821_canada_ic);
        } else if (Build.MODEL.equalsIgnoreCase("GM1905")) {
            string = string + getString(C0017R$string.oneplus_18857_canada_ic);
        } else if (Build.MODEL.equalsIgnoreCase("HD1905")) {
            string = string + getString(C0017R$string.oneplus_18865_canada_ic);
        } else if (Build.MODEL.equals("ONEPLUS A6003")) {
            string = string + getString(C0017R$string.oneplus_a6003_canada_ic);
        } else if (Build.MODEL.equals("ONEPLUS A6013")) {
            string = string + getString(C0017R$string.oneplus_a6013_canada_ic);
        } else if (Build.MODEL.equals("ONEPLUS A5000")) {
            string = string + getString(C0017R$string.oneplus_a5000_canada_ic);
        } else if (Build.MODEL.equals("ONEPLUS A5010")) {
            string = string + getString(C0017R$string.oneplus_a5010_canada_ic);
        }
        this.mRegulatoryInfoCanadaIC.setText(string);
    }

    private String getUSFccID() {
        if (Build.MODEL.equals(getString(C0017R$string.oneplus_model_19811_for_us))) {
            return getString(C0017R$string.oneplus_19811_us_fcc_id);
        }
        if (Build.MODEL.equals(getString(C0017R$string.oneplus_model_19821_for_us))) {
            return getString(C0017R$string.oneplus_19821_us_fcc_id);
        }
        if (Build.MODEL.equals(getString(C0017R$string.oneplus_model_19867_for_vzw))) {
            return getString(C0017R$string.oneplus_19867_vzw_fcc_id);
        }
        if (Build.MODEL.equals("GM1917")) {
            return getString(C0017R$string.oneplus_18821_us_fcc_id);
        }
        if (Build.MODEL.equals("GM1915")) {
            return getString(C0017R$string.oneplus_18831_us_fcc_id);
        }
        if (Build.MODEL.equals("GM1905")) {
            return getString(C0017R$string.oneplus_18857_us_fcc_id);
        }
        if (Build.MODEL.equals("HD1907")) {
            return getString(C0017R$string.oneplus_19863_tmo_fcc_id);
        }
        if (Build.MODEL.equals("HD1905")) {
            return getString(C0017R$string.oneplus_18865_us_fcc_id);
        }
        if (Build.MODEL.equals("HD1925")) {
            return getString(C0017R$string.oneplus_19861_tmo_fcc_id);
        }
        if (Build.MODEL.equals("ONEPLUS A6003")) {
            return getString(C0017R$string.oneplus_a6003_fcc_id);
        }
        if (Build.MODEL.equals("ONEPLUS A6013")) {
            return getString(C0017R$string.oneplus_a6013_fcc_id);
        }
        if (Build.MODEL.equals("ONEPLUS A5000")) {
            return getString(C0017R$string.oneplus_a5000_fcc_id);
        }
        if (Build.MODEL.equals("ONEPLUS A5010")) {
            return getString(C0017R$string.oneplus_a5010_fcc_id);
        }
        return Build.MODEL.equals("IN2017") ? getString(C0017R$string.oneplus_19855_tmo_fcc_id) : "none";
    }

    private boolean isShowSaudiArabiaInfo() {
        return Build.MODEL.equalsIgnoreCase("GM1913") || Build.MODEL.equalsIgnoreCase("GM1903") || Build.MODEL.equalsIgnoreCase("HD1913") || Build.MODEL.equalsIgnoreCase("HD1903");
    }

    private void showSaudiArabiaInfo() {
        String str;
        this.mNorthAmericaAuthenticationInfo.setVisibility(8);
        this.mSaudiArabiaAuthenticationInfo.setVisibility(0);
        if (Build.MODEL.equalsIgnoreCase("HD1913")) {
            str = getString(C0017R$string.oneplus_19801_eu_saudi_arabia_id);
        } else if (Build.MODEL.equalsIgnoreCase("HD1903")) {
            str = getString(C0017R$string.oneplus_18865_eu_saudi_arabia_id);
        } else if (Build.MODEL.equalsIgnoreCase("GM1913")) {
            str = getString(C0017R$string.oneplus_18821_eu_saudi_arabia_id);
        } else if (Build.MODEL.equalsIgnoreCase("GM1903")) {
            str = getString(C0017R$string.oneplus_18857_eu_saudi_arabia_id);
        } else {
            str = Build.MODEL.equalsIgnoreCase("ONEPLUS A6013") ? getString(C0017R$string.oneplus_a6013_eu_saudi_arabia_id) : "none";
        }
        this.mRegulatoryInfoSaudiArabiaId.setText(str);
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
