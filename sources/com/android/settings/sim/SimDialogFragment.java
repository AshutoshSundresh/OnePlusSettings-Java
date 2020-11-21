package com.android.settings.sim;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.android.settings.network.SubscriptionsChangeListener;

public abstract class SimDialogFragment extends InstrumentedDialogFragment implements SubscriptionsChangeListener.SubscriptionsChangeListenerClient {
    private SubscriptionsChangeListener mChangeListener;

    @Override // com.android.settings.network.SubscriptionsChangeListener.SubscriptionsChangeListenerClient
    public void onAirplaneModeChanged(boolean z) {
    }

    public abstract void updateDialog();

    protected static Bundle initArguments(int i, int i2) {
        Bundle bundle = new Bundle();
        bundle.putInt("dialog_type", i);
        bundle.putInt("title_id", i2);
        return bundle;
    }

    public int getDialogType() {
        return getArguments().getInt("dialog_type");
    }

    public int getTitleResId() {
        return getArguments().getInt("title_id");
    }

    @Override // androidx.fragment.app.Fragment, com.android.settings.core.instrumentation.InstrumentedDialogFragment, com.android.settingslib.core.lifecycle.ObservableDialogFragment, androidx.fragment.app.DialogFragment
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mChangeListener = new SubscriptionsChangeListener(context, this);
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservableDialogFragment
    public void onPause() {
        super.onPause();
        this.mChangeListener.stop();
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservableDialogFragment
    public void onResume() {
        super.onResume();
        this.mChangeListener.start();
    }

    @Override // androidx.fragment.app.DialogFragment
    public void onDismiss(DialogInterface dialogInterface) {
        super.onDismiss(dialogInterface);
        SimDialogActivity simDialogActivity = (SimDialogActivity) getActivity();
        if (simDialogActivity != null && !simDialogActivity.isFinishing()) {
            simDialogActivity.onFragmentDismissed(this);
        }
    }

    @Override // com.android.settings.network.SubscriptionsChangeListener.SubscriptionsChangeListenerClient
    public void onSubscriptionsChanged() {
        updateDialog();
    }
}
