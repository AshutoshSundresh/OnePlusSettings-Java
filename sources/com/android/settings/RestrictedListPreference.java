package com.android.settings;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.UserManager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.ListPreferenceDialogFragmentCompat;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.CustomListPreference;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedPreferenceHelper;
import java.util.ArrayList;
import java.util.List;

public class RestrictedListPreference extends CustomListPreference {
    private final RestrictedPreferenceHelper mHelper;
    private int mProfileUserId;
    private boolean mRequiresActiveUnlockedProfile = false;
    private final List<RestrictedItem> mRestrictedItems = new ArrayList();

    public RestrictedListPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setWidgetLayoutResource(C0012R$layout.restricted_icon);
        this.mHelper = new RestrictedPreferenceHelper(context, this, attributeSet);
    }

    public RestrictedListPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mHelper = new RestrictedPreferenceHelper(context, this, attributeSet);
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        this.mHelper.onBindViewHolder(preferenceViewHolder);
        View findViewById = preferenceViewHolder.findViewById(C0010R$id.restricted_icon);
        if (findViewById != null) {
            findViewById.setVisibility(isDisabledByAdmin() ? 0 : 8);
        }
    }

    @Override // androidx.preference.Preference
    public void performClick() {
        if (this.mRequiresActiveUnlockedProfile) {
            if (!Utils.startQuietModeDialogIfNecessary(getContext(), UserManager.get(getContext()), this.mProfileUserId)) {
                KeyguardManager keyguardManager = (KeyguardManager) getContext().getSystemService("keyguard");
                if (keyguardManager.isDeviceLocked(this.mProfileUserId)) {
                    getContext().startActivity(keyguardManager.createConfirmDeviceCredentialIntent(null, null, this.mProfileUserId));
                    return;
                }
            } else {
                return;
            }
        }
        if (!this.mHelper.performClick()) {
            super.performClick();
        }
    }

    @Override // androidx.preference.Preference
    public void setEnabled(boolean z) {
        if (!z || !isDisabledByAdmin()) {
            super.setEnabled(z);
        } else {
            this.mHelper.setDisabledByAdmin(null);
        }
    }

    public void setDisabledByAdmin(RestrictedLockUtils.EnforcedAdmin enforcedAdmin) {
        if (this.mHelper.setDisabledByAdmin(enforcedAdmin)) {
            notifyChanged();
        }
    }

    public boolean isDisabledByAdmin() {
        return this.mHelper.isDisabledByAdmin();
    }

    public void setRequiresActiveUnlockedProfile(boolean z) {
        this.mRequiresActiveUnlockedProfile = z;
    }

    public void setProfileUserId(int i) {
        this.mProfileUserId = i;
    }

    public boolean isRestrictedForEntry(CharSequence charSequence) {
        if (charSequence == null) {
            return false;
        }
        for (RestrictedItem restrictedItem : this.mRestrictedItems) {
            if (charSequence.equals(restrictedItem.entry)) {
                return true;
            }
        }
        return false;
    }

    public void addRestrictedItem(RestrictedItem restrictedItem) {
        this.mRestrictedItems.add(restrictedItem);
    }

    public void clearRestrictedItems() {
        this.mRestrictedItems.clear();
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private RestrictedItem getRestrictedItemForEntryValue(CharSequence charSequence) {
        if (charSequence == null) {
            return null;
        }
        for (RestrictedItem restrictedItem : this.mRestrictedItems) {
            if (charSequence.equals(restrictedItem.entryValue)) {
                return restrictedItem;
            }
        }
        return null;
    }

    /* access modifiers changed from: protected */
    public ListAdapter createListAdapter(Context context) {
        return new RestrictedArrayAdapter(context, getEntries(), getSelectedValuePos());
    }

    public int getSelectedValuePos() {
        String value = getValue();
        if (value == null) {
            return -1;
        }
        return findIndexOfValue(value);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.CustomListPreference
    public void onPrepareDialogBuilder(AlertDialog.Builder builder, DialogInterface.OnClickListener onClickListener) {
        builder.setAdapter(createListAdapter(builder.getContext()), onClickListener);
    }

    public class RestrictedArrayAdapter extends ArrayAdapter<CharSequence> {
        private final int mSelectedIndex;

        public long getItemId(int i) {
            return (long) i;
        }

        public boolean hasStableIds() {
            return true;
        }

        public RestrictedArrayAdapter(Context context, CharSequence[] charSequenceArr, int i) {
            super(context, C0012R$layout.restricted_dialog_singlechoice, C0010R$id.text1, charSequenceArr);
            this.mSelectedIndex = i;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            View view2 = super.getView(i, view, viewGroup);
            CheckedTextView checkedTextView = (CheckedTextView) view2.findViewById(C0010R$id.text1);
            ImageView imageView = (ImageView) view2.findViewById(C0010R$id.restricted_lock_icon);
            boolean z = false;
            if (RestrictedListPreference.this.isRestrictedForEntry((CharSequence) getItem(i))) {
                checkedTextView.setEnabled(false);
                checkedTextView.setChecked(false);
                imageView.setVisibility(0);
            } else {
                int i2 = this.mSelectedIndex;
                if (i2 != -1) {
                    if (i == i2) {
                        z = true;
                    }
                    checkedTextView.setChecked(z);
                }
                if (!checkedTextView.isEnabled()) {
                    checkedTextView.setEnabled(true);
                }
                imageView.setVisibility(8);
            }
            return view2;
        }
    }

    public static class RestrictedListPreferenceDialogFragment extends CustomListPreference.CustomListPreferenceDialogFragment {
        private int mLastCheckedPosition = -1;

        public static ListPreferenceDialogFragmentCompat newInstance(String str) {
            RestrictedListPreferenceDialogFragment restrictedListPreferenceDialogFragment = new RestrictedListPreferenceDialogFragment();
            Bundle bundle = new Bundle(1);
            bundle.putString("key", str);
            restrictedListPreferenceDialogFragment.setArguments(bundle);
            return restrictedListPreferenceDialogFragment;
        }

        /* access modifiers changed from: private */
        /* access modifiers changed from: public */
        private RestrictedListPreference getCustomizablePreference() {
            return (RestrictedListPreference) getPreference();
        }

        /* access modifiers changed from: protected */
        @Override // com.android.settings.CustomListPreference.CustomListPreferenceDialogFragment
        public DialogInterface.OnClickListener getOnItemClickListener() {
            return new DialogInterface.OnClickListener() {
                /* class com.android.settings.RestrictedListPreference.RestrictedListPreferenceDialogFragment.AnonymousClass1 */

                public void onClick(DialogInterface dialogInterface, int i) {
                    RestrictedListPreference customizablePreference = RestrictedListPreferenceDialogFragment.this.getCustomizablePreference();
                    if (i >= 0 && i < customizablePreference.getEntryValues().length) {
                        RestrictedItem restrictedItemForEntryValue = customizablePreference.getRestrictedItemForEntryValue(customizablePreference.getEntryValues()[i].toString());
                        if (restrictedItemForEntryValue != null) {
                            ((AlertDialog) dialogInterface).getListView().setItemChecked(RestrictedListPreferenceDialogFragment.this.getLastCheckedPosition(), true);
                            RestrictedLockUtils.sendShowAdminSupportDetailsIntent(RestrictedListPreferenceDialogFragment.this.getContext(), restrictedItemForEntryValue.enforcedAdmin);
                        } else {
                            RestrictedListPreferenceDialogFragment.this.setClickedDialogEntryIndex(i);
                        }
                        if (RestrictedListPreferenceDialogFragment.this.getCustomizablePreference().isAutoClosePreference()) {
                            RestrictedListPreferenceDialogFragment.this.onClick(dialogInterface, -1);
                            dialogInterface.dismiss();
                        }
                    }
                }
            };
        }

        /* access modifiers changed from: private */
        /* access modifiers changed from: public */
        private int getLastCheckedPosition() {
            if (this.mLastCheckedPosition == -1) {
                this.mLastCheckedPosition = getCustomizablePreference().getSelectedValuePos();
            }
            return this.mLastCheckedPosition;
        }

        /* access modifiers changed from: protected */
        @Override // com.android.settings.CustomListPreference.CustomListPreferenceDialogFragment
        public void setClickedDialogEntryIndex(int i) {
            super.setClickedDialogEntryIndex(i);
            this.mLastCheckedPosition = i;
        }
    }

    public static class RestrictedItem {
        public final RestrictedLockUtils.EnforcedAdmin enforcedAdmin;
        public final CharSequence entry;
        public final CharSequence entryValue;

        public RestrictedItem(CharSequence charSequence, CharSequence charSequence2, RestrictedLockUtils.EnforcedAdmin enforcedAdmin2) {
            this.entry = charSequence;
            this.entryValue = charSequence2;
            this.enforcedAdmin = enforcedAdmin2;
        }
    }
}
