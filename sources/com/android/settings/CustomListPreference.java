package com.android.settings;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.AttributeSet;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.ListPreference;
import androidx.preference.ListPreferenceDialogFragmentCompat;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;

public class CustomListPreference extends ListPreference {
    /* access modifiers changed from: protected */
    public CharSequence getConfirmationMessage(String str) {
        return null;
    }

    /* access modifiers changed from: protected */
    public boolean isAutoClosePreference() {
        return true;
    }

    /* access modifiers changed from: protected */
    public void onDialogClosed(boolean z) {
    }

    /* access modifiers changed from: protected */
    public void onDialogCreated(Dialog dialog) {
    }

    /* access modifiers changed from: protected */
    public void onDialogStateRestored(Dialog dialog, Bundle bundle) {
    }

    /* access modifiers changed from: protected */
    public void onPrepareDialogBuilder(AlertDialog.Builder builder, DialogInterface.OnClickListener onClickListener) {
    }

    public CustomListPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public CustomListPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
    }

    public static class CustomListPreferenceDialogFragment extends ListPreferenceDialogFragmentCompat {
        private int mClickedDialogEntryIndex;

        public static ListPreferenceDialogFragmentCompat newInstance(String str) {
            CustomListPreferenceDialogFragment customListPreferenceDialogFragment = new CustomListPreferenceDialogFragment();
            Bundle bundle = new Bundle(1);
            bundle.putString("key", str);
            customListPreferenceDialogFragment.setArguments(bundle);
            return customListPreferenceDialogFragment;
        }

        /* access modifiers changed from: private */
        /* access modifiers changed from: public */
        private CustomListPreference getCustomizablePreference() {
            return (CustomListPreference) getPreference();
        }

        /* access modifiers changed from: protected */
        @Override // androidx.preference.PreferenceDialogFragmentCompat, androidx.preference.ListPreferenceDialogFragmentCompat
        public void onPrepareDialogBuilder(AlertDialog.Builder builder) {
            super.onPrepareDialogBuilder(builder);
            this.mClickedDialogEntryIndex = getCustomizablePreference().findIndexOfValue(getCustomizablePreference().getValue());
            getCustomizablePreference().onPrepareDialogBuilder(builder, getOnItemClickListener());
            if (!getCustomizablePreference().isAutoClosePreference()) {
                builder.setPositiveButton(C0017R$string.okay, new DialogInterface.OnClickListener() {
                    /* class com.android.settings.CustomListPreference.CustomListPreferenceDialogFragment.AnonymousClass1 */

                    public void onClick(DialogInterface dialogInterface, int i) {
                        CustomListPreferenceDialogFragment.this.onItemChosen();
                    }
                });
            }
        }

        @Override // androidx.preference.PreferenceDialogFragmentCompat, androidx.fragment.app.DialogFragment
        public Dialog onCreateDialog(Bundle bundle) {
            Dialog onCreateDialog = super.onCreateDialog(bundle);
            if (bundle != null) {
                this.mClickedDialogEntryIndex = bundle.getInt("settings.CustomListPrefDialog.KEY_CLICKED_ENTRY_INDEX", this.mClickedDialogEntryIndex);
            }
            getCustomizablePreference().onDialogCreated(onCreateDialog);
            return onCreateDialog;
        }

        @Override // androidx.preference.PreferenceDialogFragmentCompat, androidx.preference.ListPreferenceDialogFragmentCompat, androidx.fragment.app.Fragment, androidx.fragment.app.DialogFragment
        public void onSaveInstanceState(Bundle bundle) {
            super.onSaveInstanceState(bundle);
            bundle.putInt("settings.CustomListPrefDialog.KEY_CLICKED_ENTRY_INDEX", this.mClickedDialogEntryIndex);
        }

        @Override // androidx.fragment.app.Fragment
        public void onActivityCreated(Bundle bundle) {
            super.onActivityCreated(bundle);
            getCustomizablePreference().onDialogStateRestored(getDialog(), bundle);
        }

        /* access modifiers changed from: protected */
        public DialogInterface.OnClickListener getOnItemClickListener() {
            return new DialogInterface.OnClickListener() {
                /* class com.android.settings.CustomListPreference.CustomListPreferenceDialogFragment.AnonymousClass2 */

                public void onClick(DialogInterface dialogInterface, int i) {
                    CustomListPreferenceDialogFragment.this.setClickedDialogEntryIndex(i);
                    if (CustomListPreferenceDialogFragment.this.getCustomizablePreference().isAutoClosePreference()) {
                        CustomListPreferenceDialogFragment.this.onItemChosen();
                    }
                }
            };
        }

        /* access modifiers changed from: protected */
        public void setClickedDialogEntryIndex(int i) {
            this.mClickedDialogEntryIndex = i;
        }

        private String getValue() {
            CustomListPreference customizablePreference = getCustomizablePreference();
            if (this.mClickedDialogEntryIndex < 0 || customizablePreference.getEntryValues() == null) {
                return null;
            }
            return customizablePreference.getEntryValues()[this.mClickedDialogEntryIndex].toString();
        }

        /* access modifiers changed from: protected */
        public void onItemChosen() {
            CharSequence confirmationMessage = getCustomizablePreference().getConfirmationMessage(getValue());
            if (confirmationMessage != null) {
                ConfirmDialogFragment confirmDialogFragment = new ConfirmDialogFragment();
                Bundle bundle = new Bundle();
                bundle.putCharSequence("android.intent.extra.TEXT", confirmationMessage);
                confirmDialogFragment.setArguments(bundle);
                confirmDialogFragment.setTargetFragment(this, 0);
                FragmentTransaction beginTransaction = getFragmentManager().beginTransaction();
                beginTransaction.add(confirmDialogFragment, getTag() + "-Confirm");
                beginTransaction.commitAllowingStateLoss();
                return;
            }
            onItemConfirmed();
        }

        /* access modifiers changed from: protected */
        public void onItemConfirmed() {
            onClick(getDialog(), -1);
            getDialog().dismiss();
        }

        @Override // androidx.preference.PreferenceDialogFragmentCompat, androidx.preference.ListPreferenceDialogFragmentCompat
        public void onDialogClosed(boolean z) {
            getCustomizablePreference().onDialogClosed(z);
            CustomListPreference customizablePreference = getCustomizablePreference();
            String value = getValue();
            if (z && value != null && customizablePreference.callChangeListener(value)) {
                customizablePreference.setValue(value);
            }
        }
    }

    public static class ConfirmDialogFragment extends InstrumentedDialogFragment {
        @Override // com.android.settingslib.core.instrumentation.Instrumentable
        public int getMetricsCategory() {
            return 529;
        }

        @Override // androidx.fragment.app.DialogFragment
        public Dialog onCreateDialog(Bundle bundle) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(getArguments().getCharSequence("android.intent.extra.TEXT"));
            builder.setPositiveButton(17039370, new DialogInterface.OnClickListener() {
                /* class com.android.settings.CustomListPreference.ConfirmDialogFragment.AnonymousClass1 */

                public void onClick(DialogInterface dialogInterface, int i) {
                    Fragment targetFragment = ConfirmDialogFragment.this.getTargetFragment();
                    if (targetFragment != null) {
                        ((CustomListPreferenceDialogFragment) targetFragment).onItemConfirmed();
                    }
                }
            });
            builder.setNegativeButton(17039360, (DialogInterface.OnClickListener) null);
            return builder.create();
        }
    }
}
