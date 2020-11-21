package com.oneplus.settings.displaysizeadaption;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Checkable;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.android.settingslib.applications.ApplicationsState;
import com.oneplus.settings.SettingsBaseApplication;
import com.oneplus.settings.utils.OPUtils;

public class DisplaySizeAdaptionDetail extends InstrumentedDialogFragment implements DialogInterface.OnClickListener, View.OnClickListener {
    private static DisplaySizeAdaptiongeManager mManager;
    private Context mContext;
    private Checkable mDefault;
    private Checkable mFullScreen;
    private CharSequence mLabel;
    private int mOriginValue;
    private Checkable mOriginalSize;
    private String mPackageName;
    private int mSelectedValue;
    private int mUid;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 9999;
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservableDialogFragment, androidx.fragment.app.DialogFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mPackageName = getArguments().getString("package");
        PackageManager packageManager = getContext().getPackageManager();
        this.mContext = getContext();
        try {
            this.mLabel = packageManager.getApplicationInfo(this.mPackageName, 0).loadLabel(packageManager);
            this.mUid = packageManager.getApplicationInfo(this.mPackageName, 0).uid;
        } catch (Exception unused) {
            this.mLabel = this.mPackageName;
        }
        DisplaySizeAdaptiongeManager instance = DisplaySizeAdaptiongeManager.getInstance(this.mContext);
        mManager = instance;
        int appTypeValue = instance.getAppTypeValue(this.mPackageName);
        this.mSelectedValue = appTypeValue;
        this.mOriginValue = appTypeValue;
    }

    public Checkable setup(View view, int i) {
        if (i == 1) {
            if (!OPUtils.isSupportScreenCutting()) {
                ((TextView) view.findViewById(16908310)).setText(C0017R$string.oneplus_display_size_adaption_full_screen);
            } else {
                ((TextView) view.findViewById(16908310)).setText(C0017R$string.default_keyboard_layout);
            }
        } else if (i == 0) {
            ((TextView) view.findViewById(16908310)).setText(C0017R$string.oneplus_display_size_adaption_original_size);
        } else {
            ((TextView) view.findViewById(16908310)).setText(C0017R$string.oneplus_app_display_fullscreen);
        }
        view.setClickable(true);
        view.setOnClickListener(this);
        return (Checkable) view;
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(this.mLabel);
        builder.setBottomShow(true);
        builder.setNegativeButton(C0017R$string.cancel, (DialogInterface.OnClickListener) null);
        builder.setPositiveButton(C0017R$string.done, this);
        builder.setView(C0012R$layout.op_display_size_content);
        return builder.create();
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservableDialogFragment, androidx.fragment.app.DialogFragment
    public void onStart() {
        super.onStart();
        this.mDefault = setup(getDialog().findViewById(C0010R$id.ignore_default), 3);
        if (OPUtils.isSupportScreenCutting()) {
            getDialog().findViewById(C0010R$id.ignore_default).setVisibility(0);
        } else {
            getDialog().findViewById(C0010R$id.ignore_on).setVisibility(0);
        }
        this.mOriginalSize = setup(getDialog().findViewById(C0010R$id.ignore_on), 0);
        this.mFullScreen = setup(getDialog().findViewById(C0010R$id.ignore_off), 1);
        updateViews();
    }

    private void updateViews() {
        boolean z = false;
        this.mFullScreen.setChecked(this.mSelectedValue == 1);
        this.mOriginalSize.setChecked(this.mSelectedValue == 0);
        Checkable checkable = this.mDefault;
        int i = this.mSelectedValue;
        if (i == 3 || i == 2) {
            z = true;
        }
        checkable.setChecked(z);
        if (this.mSelectedValue == 0 && OPUtils.isSupportScreenCutting()) {
            this.mFullScreen.setChecked(true);
        }
    }

    public void onClick(View view) {
        if (view == this.mFullScreen) {
            this.mSelectedValue = 1;
        } else if (view == this.mOriginalSize) {
            this.mSelectedValue = 0;
        } else if (view == this.mDefault) {
            this.mSelectedValue = 2;
        }
        updateViews();
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        if (i == -1) {
            if (this.mSelectedValue == 3) {
                this.mSelectedValue = 2;
            }
            mManager.setClassApp(this.mUid, this.mPackageName, this.mSelectedValue);
            if (OPUtils.isSupportScreenCutting() && this.mOriginValue != this.mSelectedValue) {
                Context context = this.mContext;
                Toast.makeText(context, context.getResources().getString(C0017R$string.exception_hints), 1).show();
            }
        }
    }

    @Override // androidx.fragment.app.DialogFragment
    public void onDismiss(DialogInterface dialogInterface) {
        super.onDismiss(dialogInterface);
        Fragment targetFragment = getTargetFragment();
        if (targetFragment != null) {
            targetFragment.onActivityResult(getTargetRequestCode(), 0, new Intent());
        }
    }

    public static CharSequence getSummary(Context context, ApplicationsState.AppEntry appEntry) {
        return getSummary(context, appEntry.info.packageName);
    }

    public static CharSequence getSummary(Context context, String str) {
        int appTypeValue = DisplaySizeAdaptiongeManager.getInstance(context).getAppTypeValue(str);
        if (appTypeValue == 1) {
            if (!OPUtils.isSupportScreenCutting()) {
                return SettingsBaseApplication.mApplication.getString(C0017R$string.oneplus_display_size_adaption_full_screen);
            }
            return SettingsBaseApplication.mApplication.getString(C0017R$string.default_keyboard_layout);
        } else if (appTypeValue != 0) {
            return SettingsBaseApplication.mApplication.getString(C0017R$string.oneplus_app_display_fullscreen);
        } else {
            if (!OPUtils.isSupportScreenCutting()) {
                return SettingsBaseApplication.mApplication.getString(C0017R$string.oneplus_display_size_adaption_original_size);
            }
            return SettingsBaseApplication.mApplication.getString(C0017R$string.default_keyboard_layout);
        }
    }

    public static void show(Fragment fragment, int i, String str, int i2) {
        DisplaySizeAdaptionDetail displaySizeAdaptionDetail = new DisplaySizeAdaptionDetail();
        Bundle bundle = new Bundle();
        bundle.putString("package", str);
        bundle.putInt("uid", i);
        displaySizeAdaptionDetail.setArguments(bundle);
        displaySizeAdaptionDetail.setTargetFragment(fragment, i2);
        displaySizeAdaptionDetail.show(fragment.getFragmentManager(), DisplaySizeAdaptionDetail.class.getSimpleName());
    }
}
