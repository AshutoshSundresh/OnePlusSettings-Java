package com.oneplus.settings;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.oneplus.settings.utils.OPUtils;

public class BaseActivity extends BaseAppCompatActivity {
    public OnPressListener mOnPressListener;
    private AlertDialog mWarnDialog;

    /* access modifiers changed from: protected */
    public boolean needShowWarningDialog() {
        return false;
    }

    public void setOnPressListener(OnPressListener onPressListener) {
        this.mOnPressListener = onPressListener;
    }

    /* access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, com.oneplus.settings.BaseAppCompatActivity
    public void onCreate(Bundle bundle) {
        OPUtils.setLightNavigationBar(getWindow(), OPUtils.getThemeMode(getContentResolver()));
        super.onCreate(bundle);
        super.setContentView(C0012R$layout.settings_base_layout);
        Toolbar toolbar = (Toolbar) findViewById(C0010R$id.action_bar);
        toolbar.setTitle(getTitle());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            /* class com.oneplus.settings.BaseActivity.AnonymousClass1 */

            public void onClick(View view) {
                BaseActivity.this.onBackPressed();
            }
        });
    }

    /* access modifiers changed from: protected */
    @Override // androidx.appcompat.app.AppCompatActivity
    public void onTitleChanged(CharSequence charSequence, int i) {
        super.onTitleChanged(charSequence, i);
        Toolbar toolbar = (Toolbar) findViewById(C0010R$id.action_bar);
        if (toolbar != null) {
            toolbar.setTitle(getTitle());
        }
    }

    @Override // androidx.activity.ComponentActivity, androidx.appcompat.app.AppCompatActivity, android.app.Activity
    public void setContentView(int i) {
        getLayoutInflater().inflate(i, (ViewGroup) ((FrameLayout) findViewById(C0010R$id.content_frame)), true);
    }

    public void performBackEvent() {
        if (needShowWarningDialog()) {
            showWarningDialog(C0017R$string.oneplus_custom_drop_title, C0017R$string.menu_cancel, C0017R$string.cancel);
        } else {
            finish();
        }
    }

    @Override // androidx.appcompat.app.AppCompatActivity
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (i != 4) {
            return super.onKeyDown(i, keyEvent);
        }
        performBackEvent();
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != 16908332) {
            return super.onOptionsItemSelected(menuItem);
        }
        performBackEvent();
        return true;
    }

    public void showWarningDialog(int i, int i2, int i3) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(i);
        builder.setPositiveButton(i2, new DialogInterface.OnClickListener() {
            /* class com.oneplus.settings.BaseActivity.AnonymousClass3 */

            public void onClick(DialogInterface dialogInterface, int i) {
                OnPressListener onPressListener = BaseActivity.this.mOnPressListener;
                if (onPressListener != null) {
                    onPressListener.onCancelPressed();
                }
            }
        });
        builder.setNegativeButton(i3, new DialogInterface.OnClickListener() {
            /* class com.oneplus.settings.BaseActivity.AnonymousClass2 */

            public void onClick(DialogInterface dialogInterface, int i) {
                if (BaseActivity.this.mWarnDialog != null) {
                    BaseActivity.this.mWarnDialog.dismiss();
                }
            }
        });
        AlertDialog create = builder.create();
        this.mWarnDialog = create;
        create.show();
    }
}
