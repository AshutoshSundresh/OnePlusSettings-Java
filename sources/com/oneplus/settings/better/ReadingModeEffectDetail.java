package com.oneplus.settings.better;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.android.settings.C0003R$array;
import com.android.settings.C0017R$string;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.android.settingslib.applications.ApplicationsState;
import com.oneplus.settings.utils.OPUtils;

public class ReadingModeEffectDetail extends InstrumentedDialogFragment {
    private static ReadingModeEffectManager mManager;
    private static int mUid;
    private Context mContext;
    private CharSequence mLabel;
    private String mPackageName;
    private int mSelectedValue;

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
            mUid = getArguments().getInt("uid");
        } catch (Exception unused) {
            this.mLabel = this.mPackageName;
        }
        ReadingModeEffectManager instance = ReadingModeEffectManager.getInstance(this.mContext);
        mManager = instance;
        int appEffectSelectValue = instance.getAppEffectSelectValue(mUid + this.mPackageName);
        if (appEffectSelectValue == 2) {
            this.mSelectedValue = 0;
        } else if (appEffectSelectValue == 0) {
            this.mSelectedValue = 1;
        } else if (appEffectSelectValue == 3) {
            this.mSelectedValue = 2;
        }
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(this.mContext.getString(C0017R$string.oneplus_reading_mode_select_effect_for_app, this.mLabel));
        builder.setSingleChoiceItems(C0003R$array.oneplus_reading_mode_effec_select, this.mSelectedValue, new DialogInterface.OnClickListener() {
            /* class com.oneplus.settings.better.ReadingModeEffectDetail.AnonymousClass2 */

            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    ReadingModeEffectDetail.mManager.setAppEffectSelect(ReadingModeEffectDetail.mUid, ReadingModeEffectDetail.this.mPackageName, 2);
                    OPUtils.sendAnalytics("read_app", "clr", ReadingModeEffectDetail.this.mPackageName);
                    dialogInterface.dismiss();
                } else if (i != 1) {
                    dialogInterface.dismiss();
                } else {
                    ReadingModeEffectDetail.mManager.setAppEffectSelect(ReadingModeEffectDetail.mUid, ReadingModeEffectDetail.this.mPackageName, 0);
                    OPUtils.sendAnalytics("read_app", "bw", ReadingModeEffectDetail.this.mPackageName);
                    dialogInterface.dismiss();
                }
            }
        });
        builder.setNegativeButton(C0017R$string.alert_dialog_cancel, new DialogInterface.OnClickListener(this) {
            /* class com.oneplus.settings.better.ReadingModeEffectDetail.AnonymousClass1 */

            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        return builder.create();
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
        return getSummary(context, appEntry.info.uid + appEntry.info.packageName);
    }

    private static CharSequence getSummary(Context context, String str) {
        int appEffectSelectValue = ReadingModeEffectManager.getInstance(context).getAppEffectSelectValue(str);
        if (appEffectSelectValue == 2) {
            return context.getString(C0017R$string.oneplus_reading_mode_chromatic);
        }
        if (appEffectSelectValue == 0) {
            return context.getString(C0017R$string.oneplus_reading_mode_mono);
        }
        return context.getString(C0017R$string.oneplus_reading_mode_available);
    }

    public static void show(Fragment fragment, int i, String str, int i2) {
        ReadingModeEffectDetail readingModeEffectDetail = new ReadingModeEffectDetail();
        Bundle bundle = new Bundle();
        bundle.putString("package", str);
        bundle.putInt("uid", i);
        readingModeEffectDetail.setArguments(bundle);
        readingModeEffectDetail.setTargetFragment(fragment, i2);
        readingModeEffectDetail.show(fragment.getFragmentManager(), ReadingModeEffectDetail.class.getSimpleName());
    }
}
