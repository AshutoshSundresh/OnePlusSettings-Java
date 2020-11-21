package com.oneplus.settings;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import androidx.appcompat.app.AlertDialog;
import com.android.settings.C0017R$string;
import com.android.settings.dashboard.DashboardFragment;

public abstract class OPQuitConfirmFragment extends DashboardFragment implements OPOnBackPressedListener {
    public OnPressListener mOnPressListener;
    private AlertDialog mWarnDialog;

    /* access modifiers changed from: protected */
    public abstract boolean needShowWarningDialog();

    public void setOnPressListener(OnPressListener onPressListener) {
        this.mOnPressListener = onPressListener;
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        getActivity().setRequestedOrientation(1);
        if (getListView() != null) {
            getListView().setPadding(getListView().getPaddingLeft(), getListView().getPaddingTop(), getListView().getPaddingRight(), 0);
        }
    }

    public void showWarningDialog(int i, int i2, int i3) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(i);
        builder.setPositiveButton(i2, new DialogInterface.OnClickListener() {
            /* class com.oneplus.settings.OPQuitConfirmFragment.AnonymousClass2 */

            public void onClick(DialogInterface dialogInterface, int i) {
                OnPressListener onPressListener = OPQuitConfirmFragment.this.mOnPressListener;
                if (onPressListener != null) {
                    onPressListener.onCancelPressed();
                }
            }
        });
        builder.setNegativeButton(i3, new DialogInterface.OnClickListener() {
            /* class com.oneplus.settings.OPQuitConfirmFragment.AnonymousClass1 */

            public void onClick(DialogInterface dialogInterface, int i) {
                if (OPQuitConfirmFragment.this.mWarnDialog != null) {
                    OPQuitConfirmFragment.this.mWarnDialog.dismiss();
                }
            }
        });
        AlertDialog create = builder.create();
        this.mWarnDialog = create;
        create.show();
    }

    private void performBackEvent() {
        if (needShowWarningDialog()) {
            showWarningDialog(C0017R$string.oneplus_custom_drop_title, C0017R$string.menu_cancel, C0017R$string.cancel);
        } else {
            finish();
        }
    }

    @Override // com.oneplus.settings.OPOnBackPressedListener
    public void doBack() {
        performBackEvent();
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != 16908332) {
            return super.onOptionsItemSelected(menuItem);
        }
        performBackEvent();
        return true;
    }
}
