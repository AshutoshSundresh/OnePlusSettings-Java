package com.oneplus.settings.aboutphone;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.settings.C0003R$array;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0013R$menu;
import com.android.settings.C0017R$string;
import com.android.settings.overlay.FeatureFactory;
import com.oneplus.settings.BaseActivity;
import com.oneplus.settings.utils.OPUtils;

public class OPForumContributors extends BaseActivity {
    private int REQUEST_CODE = 501;
    private TextView mForumContributorsTextView;
    private float mForumContributorsTextViewWidth;
    private ImageView mForumImageview;

    /* access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, com.oneplus.settings.BaseAppCompatActivity, com.oneplus.settings.BaseActivity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        OPUtils.sendAppTracker("about_phone_settings", "click_award");
        setContentView(C0012R$layout.op_forum_award);
        if (OPUtils.isO2()) {
            setTitle(C0017R$string.oneplus_o2_contributors);
        } else {
            setTitle(C0017R$string.oneplus_h2_contributors);
        }
        this.mForumImageview = (ImageView) findViewById(C0010R$id.forum_imageview);
        TextView textView = (TextView) findViewById(C0010R$id.forum_contributors);
        this.mForumContributorsTextView = textView;
        textView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            /* class com.oneplus.settings.aboutphone.OPForumContributors.AnonymousClass1 */

            public void onGlobalLayout() {
                OPForumContributors.this.mForumContributorsTextView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                OPForumContributors oPForumContributors = OPForumContributors.this;
                oPForumContributors.mForumContributorsTextViewWidth = (float) oPForumContributors.mForumContributorsTextView.getWidth();
                OPForumContributors.this.initData();
            }
        });
        if (OPUtils.isWhiteModeOn(getContentResolver())) {
            getWindow().getDecorView().setSystemUiVisibility(8192);
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void initData() {
        String[] strArr;
        int i;
        String str;
        String str2;
        if (OPUtils.isO2()) {
            if (OPUtils.isBlackModeOn(getContentResolver())) {
                this.mForumImageview.setImageResource(C0008R$drawable.op_forum_o2_dark);
            } else {
                this.mForumImageview.setImageResource(C0008R$drawable.op_forum_o2_light);
            }
            strArr = getResources().getStringArray(C0003R$array.forum_contributors_o2);
        } else {
            if (OPUtils.isBlackModeOn(getContentResolver())) {
                this.mForumImageview.setImageResource(C0008R$drawable.op_forum_h2_dark);
            } else {
                this.mForumImageview.setImageResource(C0008R$drawable.op_forum_h2_light);
            }
            strArr = getResources().getStringArray(C0003R$array.forum_contributors_h2);
        }
        String str3 = strArr[0];
        String str4 = "";
        for (int i2 = 0; i2 < strArr.length; i2++) {
            String str5 = new String(" / " + strArr[i2]);
            String str6 = new String(str3);
            if (i2 == 0) {
                str = str4 + strArr[i2];
                str2 = strArr[i2];
            } else if (!needSplitText(str3)) {
                if (needSplitText(str6 + str5)) {
                    str = str4 + "\n" + strArr[i2];
                    str2 = strArr[i2];
                } else {
                    str4 = str4 + str5;
                    str3 = str3 + str5;
                }
            } else {
                str = str4 + "\n" + strArr[i2];
                str2 = strArr[i2];
            }
            str4 = str;
            str3 = str2;
        }
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(str4);
        new RelativeSizeSpan(0.1f);
        for (int i3 = 0; i3 < str4.length(); i3++) {
            if (Character.valueOf(str4.charAt(i3)).equals('/') && (i = i3 + 1) < str4.length()) {
                if (OPUtils.isBlackModeOn(getContentResolver())) {
                    spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.parseColor("#4db2b2b2")), i3 - 1, i, 34);
                } else {
                    spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.parseColor("#4d121212")), i3 - 1, i, 34);
                }
            }
        }
        this.mForumContributorsTextView.setText(spannableStringBuilder);
    }

    public float getFontWidth(Paint paint, String str) {
        return paint.measureText(str);
    }

    public boolean needSplitText(String str) {
        return getFontWidth(this.mForumContributorsTextView.getPaint(), str) >= this.mForumContributorsTextViewWidth;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(C0013R$menu.op_search_settings, menu);
        MenuItem findItem = menu.findItem(C0010R$id.action_search);
        findItem.setVisible(true);
        findItem.setIcon(C0008R$drawable.ic_menu_search_material);
        findItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            /* class com.oneplus.settings.aboutphone.$$Lambda$OPForumContributors$Fgq3BTVklCn5TzLTlkK9M4s9qko */

            public final boolean onMenuItemClick(MenuItem menuItem) {
                return OPForumContributors.this.lambda$onCreateOptionsMenu$0$OPForumContributors(menuItem);
            }
        });
        return true;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onCreateOptionsMenu$0 */
    public /* synthetic */ boolean lambda$onCreateOptionsMenu$0$OPForumContributors(MenuItem menuItem) {
        Intent buildSearchIntent = buildSearchIntent(this, 1502);
        if (getPackageManager().queryIntentActivities(buildSearchIntent, 65536).isEmpty()) {
            return false;
        }
        FeatureFactory.getFactory(this).getSlicesFeatureProvider().indexSliceDataAsync(this);
        FeatureFactory.getFactory(this).getMetricsFeatureProvider().action(this, 226, new Pair[0]);
        startActivityForResult(buildSearchIntent, this.REQUEST_CODE);
        return true;
    }

    private Intent buildSearchIntent(Context context, int i) {
        return new Intent("android.settings.APP_SEARCH_SETTINGS").setPackage(getSettingsIntelligencePkgName(context)).putExtra("android.intent.extra.REFERRER", buildReferrer(context, i));
    }

    private String getSettingsIntelligencePkgName(Context context) {
        return context.getString(C0017R$string.config_settingsintelligence_package_name);
    }

    private static Uri buildReferrer(Context context, int i) {
        return new Uri.Builder().scheme("android-app").authority(context.getPackageName()).path(String.valueOf(i)).build();
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
