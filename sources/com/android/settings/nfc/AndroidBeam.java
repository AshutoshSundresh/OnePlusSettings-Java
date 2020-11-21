package com.android.settings.nfc;

import android.content.pm.PackageManager;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;
import androidx.fragment.app.FragmentActivity;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.android.settings.SettingsActivity;
import com.android.settings.core.InstrumentedFragment;
import com.android.settings.enterprise.ActionDisabledByAdminDialogHelper;
import com.android.settings.widget.SwitchBar;
import com.android.settingslib.HelpUtils;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;

public class AndroidBeam extends InstrumentedFragment implements SwitchBar.OnSwitchChangeListener {
    private boolean mBeamDisallowedByBase;
    private boolean mBeamDisallowedByOnlyAdmin;
    private NfcAdapter mNfcAdapter;
    private CharSequence mOldActivityTitle;
    private SwitchBar mSwitchBar;
    private View mView;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 69;
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        FragmentActivity activity = getActivity();
        this.mNfcAdapter = NfcAdapter.getDefaultAdapter(activity);
        PackageManager packageManager = activity.getPackageManager();
        if (this.mNfcAdapter == null || !packageManager.hasSystemFeature("android.sofware.nfc.beam")) {
            getActivity().finish();
        }
        setHasOptionsMenu(true);
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        HelpUtils.prepareHelpMenuItem(getActivity(), menu, C0017R$string.help_uri_beam, AndroidBeam.class.getName());
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        RestrictedLockUtils.EnforcedAdmin checkIfRestrictionEnforced = RestrictedLockUtilsInternal.checkIfRestrictionEnforced(getActivity(), "no_outgoing_beam", UserHandle.myUserId());
        UserManager.get(getActivity());
        boolean hasBaseUserRestriction = RestrictedLockUtilsInternal.hasBaseUserRestriction(getActivity(), "no_outgoing_beam", UserHandle.myUserId());
        this.mBeamDisallowedByBase = hasBaseUserRestriction;
        if (hasBaseUserRestriction || checkIfRestrictionEnforced == null) {
            View inflate = layoutInflater.inflate(C0012R$layout.op_preference_footer, viewGroup, false);
            this.mView = inflate;
            ((TextView) inflate.findViewById(16908310)).setText(C0017R$string.android_beam_explained);
            return this.mView;
        }
        new ActionDisabledByAdminDialogHelper(getActivity()).prepareDialogBuilder("no_outgoing_beam", checkIfRestrictionEnforced).show();
        this.mBeamDisallowedByOnlyAdmin = true;
        return new View(getContext());
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        SettingsActivity settingsActivity = (SettingsActivity) getActivity();
        this.mOldActivityTitle = settingsActivity.getActionBar().getTitle();
        SwitchBar switchBar = settingsActivity.getSwitchBar();
        this.mSwitchBar = switchBar;
        if (this.mBeamDisallowedByOnlyAdmin) {
            switchBar.hide();
        } else {
            switchBar.setChecked(!this.mBeamDisallowedByBase && this.mNfcAdapter.isNdefPushEnabled());
            this.mSwitchBar.addOnSwitchChangeListener(this);
            this.mSwitchBar.setEnabled(!this.mBeamDisallowedByBase);
            this.mSwitchBar.show();
        }
        settingsActivity.setTitle(C0017R$string.android_beam_settings_title);
    }

    @Override // androidx.fragment.app.Fragment
    public void onDestroyView() {
        super.onDestroyView();
        if (this.mOldActivityTitle != null) {
            getActivity().getActionBar().setTitle(this.mOldActivityTitle);
        }
        if (!this.mBeamDisallowedByOnlyAdmin) {
            this.mSwitchBar.removeOnSwitchChangeListener(this);
            this.mSwitchBar.hide();
        }
    }

    @Override // com.android.settings.widget.SwitchBar.OnSwitchChangeListener
    public void onSwitchChanged(Switch r2, boolean z) {
        boolean z2;
        this.mSwitchBar.setEnabled(false);
        if (z) {
            z2 = this.mNfcAdapter.enableNdefPush();
        } else {
            z2 = this.mNfcAdapter.disableNdefPush();
        }
        if (z2) {
            this.mSwitchBar.setChecked(z);
        }
        this.mSwitchBar.setEnabled(true);
    }
}
