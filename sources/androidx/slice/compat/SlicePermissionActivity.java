package androidx.slice.compat;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.core.text.BidiFormatter;
import androidx.slice.core.R$id;
import androidx.slice.core.R$layout;
import androidx.slice.core.R$string;

public class SlicePermissionActivity extends Activity implements DialogInterface.OnClickListener, DialogInterface.OnDismissListener {
    private String mCallingPkg;
    private AlertDialog mDialog;
    private String mProviderPkg;
    private Uri mUri;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mUri = (Uri) getIntent().getParcelableExtra("slice_uri");
        this.mCallingPkg = getIntent().getStringExtra("pkg");
        this.mProviderPkg = getIntent().getStringExtra("provider_pkg");
        try {
            PackageManager packageManager = getPackageManager();
            String unicodeWrap = BidiFormatter.getInstance().unicodeWrap(loadSafeLabel(packageManager, packageManager.getApplicationInfo(this.mCallingPkg, 0)).toString());
            String unicodeWrap2 = BidiFormatter.getInstance().unicodeWrap(loadSafeLabel(packageManager, packageManager.getApplicationInfo(this.mProviderPkg, 0)).toString());
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R$string.abc_slice_permission_title, new Object[]{unicodeWrap, unicodeWrap2}));
            builder.setView(R$layout.abc_slice_permission_request);
            builder.setNegativeButton(R$string.abc_slice_permission_deny, this);
            builder.setPositiveButton(R$string.abc_slice_permission_allow, this);
            builder.setOnDismissListener(this);
            AlertDialog show = builder.show();
            this.mDialog = show;
            ((TextView) show.getWindow().getDecorView().findViewById(R$id.text1)).setText(getString(R$string.abc_slice_permission_text_1, new Object[]{unicodeWrap2}));
            ((TextView) this.mDialog.getWindow().getDecorView().findViewById(R$id.text2)).setText(getString(R$string.abc_slice_permission_text_2, new Object[]{unicodeWrap2}));
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("SlicePermissionActivity", "Couldn't find package", e);
            finish();
        }
    }

    private CharSequence loadSafeLabel(PackageManager packageManager, ApplicationInfo applicationInfo) {
        String obj = Html.fromHtml(applicationInfo.loadLabel(packageManager).toString()).toString();
        int length = obj.length();
        int i = 0;
        while (true) {
            if (i >= length) {
                break;
            }
            int codePointAt = obj.codePointAt(i);
            int type = Character.getType(codePointAt);
            if (type == 13 || type == 15 || type == 14) {
                obj = obj.substring(0, i);
            } else {
                if (type == 12) {
                    obj = obj.substring(0, i) + " " + obj.substring(Character.charCount(codePointAt) + i);
                }
                i += Character.charCount(codePointAt);
            }
        }
        String trim = obj.trim();
        if (trim.isEmpty()) {
            return applicationInfo.packageName;
        }
        TextPaint textPaint = new TextPaint();
        textPaint.setTextSize(42.0f);
        return TextUtils.ellipsize(trim, textPaint, 500.0f, TextUtils.TruncateAt.END);
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        if (i == -1) {
            SliceProviderCompat.grantSlicePermission(this, getPackageName(), this.mCallingPkg, this.mUri.buildUpon().path("").build());
        }
        finish();
    }

    public void onDismiss(DialogInterface dialogInterface) {
        finish();
    }

    public void onDestroy() {
        super.onDestroy();
        AlertDialog alertDialog = this.mDialog;
        if (alertDialog != null && alertDialog.isShowing()) {
            this.mDialog.cancel();
        }
    }
}
