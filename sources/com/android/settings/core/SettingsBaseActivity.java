package com.android.settings.core;

import android.R;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0018R$style;
import com.android.settings.SubSettings;
import com.android.settings.dashboard.CategoryManager;
import com.google.android.setupcompat.util.WizardManagerHelper;
import com.oneplus.settings.BaseAppCompatActivity;
import com.oneplus.settings.OPOnBackPressedListener;
import java.util.ArrayList;
import java.util.List;

public class SettingsBaseActivity extends BaseAppCompatActivity {
    private static ArraySet<ComponentName> sTileBlacklist = new ArraySet<>();
    private final List<CategoryListener> mCategoryListeners = new ArrayList();
    protected boolean mNeedShowAppBar = true;
    private final PackageReceiver mPackageReceiver = new PackageReceiver();

    public interface CategoryListener {
        void onCategoriesChanged();
    }

    /* access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, com.oneplus.settings.BaseAppCompatActivity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (isLockTaskModePinned() && !isSettingsRunOnTop()) {
            Log.w("SettingsBaseActivity", "Devices lock task mode pinned.");
            finish();
        }
        System.currentTimeMillis();
        TypedArray obtainStyledAttributes = getTheme().obtainStyledAttributes(R.styleable.Theme);
        if (!obtainStyledAttributes.getBoolean(38, false)) {
            requestWindowFeature(1);
        }
        if (WizardManagerHelper.isAnySetupWizard(getIntent()) && (this instanceof SubSettings)) {
            setTheme(C0018R$style.LightTheme_SubSettings_SetupWizard);
        }
        super.setContentView(C0012R$layout.settings_base_layout);
        Toolbar toolbar = (Toolbar) findViewById(C0010R$id.action_bar);
        obtainStyledAttributes.getBoolean(38, false);
        toolbar.setTitle(getTitle());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            /* class com.android.settings.core.SettingsBaseActivity.AnonymousClass1 */

            public void onClick(View view) {
                SettingsBaseActivity.this.onBackPressed();
            }
        });
        if (!this.mNeedShowAppBar) {
            toolbar.setVisibility(8);
            ViewGroup viewGroup = (ViewGroup) findViewById(C0010R$id.content_frame);
            if (viewGroup != null) {
                viewGroup.setPadding(0, 0, 0, 0);
            }
        }
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

    @Override // androidx.activity.ComponentActivity
    public void onBackPressed() {
        Fragment findFragmentById = getSupportFragmentManager().findFragmentById(C0010R$id.main_content);
        if (findFragmentById instanceof OPOnBackPressedListener) {
            ((OPOnBackPressedListener) findFragmentById).doBack();
        } else {
            super.onBackPressed();
        }
    }

    public boolean onNavigateUp() {
        if (super.onNavigateUp()) {
            return true;
        }
        finish();
        return true;
    }

    /* access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter("android.intent.action.PACKAGE_ADDED");
        intentFilter.addAction("android.intent.action.PACKAGE_REMOVED");
        intentFilter.addAction("android.intent.action.PACKAGE_CHANGED");
        intentFilter.addAction("android.intent.action.PACKAGE_REPLACED");
        intentFilter.addDataScheme("package");
        registerReceiver(this.mPackageReceiver, intentFilter);
        updateCategories();
    }

    /* access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity
    public void onPause() {
        unregisterReceiver(this.mPackageReceiver);
        super.onPause();
    }

    public void addCategoryListener(CategoryListener categoryListener) {
        this.mCategoryListeners.add(categoryListener);
    }

    public void remCategoryListener(CategoryListener categoryListener) {
        this.mCategoryListeners.remove(categoryListener);
    }

    @Override // androidx.activity.ComponentActivity, androidx.appcompat.app.AppCompatActivity, android.app.Activity
    public void setContentView(int i) {
        ViewGroup viewGroup = (ViewGroup) findViewById(C0010R$id.content_frame);
        if (viewGroup != null) {
            viewGroup.removeAllViews();
        }
        LayoutInflater.from(this).inflate(i, viewGroup);
    }

    @Override // androidx.activity.ComponentActivity, androidx.appcompat.app.AppCompatActivity, android.app.Activity
    public void setContentView(View view) {
        ((ViewGroup) findViewById(C0010R$id.content_frame)).addView(view);
    }

    @Override // androidx.activity.ComponentActivity, androidx.appcompat.app.AppCompatActivity
    public void setContentView(View view, ViewGroup.LayoutParams layoutParams) {
        ((ViewGroup) findViewById(C0010R$id.content_frame)).addView(view, layoutParams);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void onCategoriesChanged() {
        int size = this.mCategoryListeners.size();
        for (int i = 0; i < size; i++) {
            this.mCategoryListeners.get(i).onCategoriesChanged();
        }
    }

    private boolean isLockTaskModePinned() {
        return ((ActivityManager) getApplicationContext().getSystemService(ActivityManager.class)).getLockTaskModeState() == 2;
    }

    private boolean isSettingsRunOnTop() {
        return TextUtils.equals(getPackageName(), ((ActivityManager) getApplicationContext().getSystemService(ActivityManager.class)).getRunningTasks(1).get(0).baseActivity.getPackageName());
    }

    public boolean setTileEnabled(ComponentName componentName, boolean z) {
        PackageManager packageManager = getPackageManager();
        int componentEnabledSetting = packageManager.getComponentEnabledSetting(componentName);
        if ((componentEnabledSetting == 1) == z && componentEnabledSetting != 0) {
            return false;
        }
        if (z) {
            sTileBlacklist.remove(componentName);
        } else {
            sTileBlacklist.add(componentName);
        }
        packageManager.setComponentEnabledSetting(componentName, z ? 1 : 2, 1);
        return true;
    }

    public void updateCategories() {
        new CategoriesUpdateTask().execute(new Void[0]);
    }

    /* access modifiers changed from: private */
    public class CategoriesUpdateTask extends AsyncTask<Void, Void, Void> {
        private final CategoryManager mCategoryManager;

        public CategoriesUpdateTask() {
            this.mCategoryManager = CategoryManager.get(SettingsBaseActivity.this);
        }

        /* access modifiers changed from: protected */
        public Void doInBackground(Void... voidArr) {
            this.mCategoryManager.reloadAllCategories(SettingsBaseActivity.this);
            return null;
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(Void r2) {
            this.mCategoryManager.updateCategoryFromBlacklist(SettingsBaseActivity.sTileBlacklist);
            SettingsBaseActivity.this.onCategoriesChanged();
        }
    }

    /* access modifiers changed from: private */
    public class PackageReceiver extends BroadcastReceiver {
        private PackageReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            SettingsBaseActivity.this.updateCategories();
        }
    }
}
