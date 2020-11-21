package com.android.settings.inputmethod;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.input.InputDeviceIdentifier;
import android.hardware.input.InputManager;
import android.hardware.input.KeyboardLayout;
import android.os.Bundle;
import android.view.InputDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.RadioButton;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import java.util.ArrayList;
import java.util.Collections;

public class KeyboardLayoutDialogFragment extends InstrumentedDialogFragment implements InputManager.InputDeviceListener, LoaderManager.LoaderCallbacks<Keyboards> {
    private KeyboardLayoutAdapter mAdapter;
    private InputManager mIm;
    private int mInputDeviceId = -1;
    private InputDeviceIdentifier mInputDeviceIdentifier;

    public static final class Keyboards {
        public int current = -1;
        public final ArrayList<KeyboardLayout> keyboardLayouts = new ArrayList<>();
    }

    public interface OnSetupKeyboardLayoutsListener {
        void onSetupKeyboardLayouts(InputDeviceIdentifier inputDeviceIdentifier);
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 541;
    }

    public void onInputDeviceAdded(int i) {
    }

    public KeyboardLayoutDialogFragment() {
    }

    public KeyboardLayoutDialogFragment(InputDeviceIdentifier inputDeviceIdentifier) {
        this.mInputDeviceIdentifier = inputDeviceIdentifier;
    }

    @Override // androidx.fragment.app.Fragment
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Context baseContext = activity.getBaseContext();
        this.mIm = (InputManager) baseContext.getSystemService("input");
        this.mAdapter = new KeyboardLayoutAdapter(baseContext);
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservableDialogFragment, androidx.fragment.app.DialogFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (bundle != null) {
            this.mInputDeviceIdentifier = bundle.getParcelable("inputDeviceIdentifier");
        }
        getLoaderManager().initLoader(0, null, this);
    }

    @Override // androidx.fragment.app.Fragment, androidx.fragment.app.DialogFragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putParcelable("inputDeviceIdentifier", this.mInputDeviceIdentifier);
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        FragmentActivity activity = getActivity();
        LayoutInflater from = LayoutInflater.from(activity);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(C0017R$string.keyboard_layout_dialog_title);
        builder.setPositiveButton(C0017R$string.keyboard_layout_dialog_setup_button, new DialogInterface.OnClickListener() {
            /* class com.android.settings.inputmethod.KeyboardLayoutDialogFragment.AnonymousClass2 */

            public void onClick(DialogInterface dialogInterface, int i) {
                KeyboardLayoutDialogFragment.this.onSetupLayoutsButtonClicked();
            }
        });
        builder.setSingleChoiceItems(this.mAdapter, -1, new DialogInterface.OnClickListener() {
            /* class com.android.settings.inputmethod.KeyboardLayoutDialogFragment.AnonymousClass1 */

            public void onClick(DialogInterface dialogInterface, int i) {
                KeyboardLayoutDialogFragment.this.onKeyboardLayoutClicked(i);
            }
        });
        builder.setView(from.inflate(C0012R$layout.keyboard_layout_dialog_switch_hint, (ViewGroup) null));
        updateSwitchHintVisibility();
        return builder.create();
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservableDialogFragment
    public void onResume() {
        super.onResume();
        this.mIm.registerInputDeviceListener(this, null);
        InputDevice inputDeviceByDescriptor = this.mIm.getInputDeviceByDescriptor(this.mInputDeviceIdentifier.getDescriptor());
        if (inputDeviceByDescriptor == null) {
            dismiss();
        } else {
            this.mInputDeviceId = inputDeviceByDescriptor.getId();
        }
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservableDialogFragment
    public void onPause() {
        this.mIm.unregisterInputDeviceListener(this);
        this.mInputDeviceId = -1;
        super.onPause();
    }

    @Override // androidx.fragment.app.DialogFragment
    public void onCancel(DialogInterface dialogInterface) {
        super.onCancel(dialogInterface);
        dismiss();
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void onSetupLayoutsButtonClicked() {
        ((OnSetupKeyboardLayoutsListener) getTargetFragment()).onSetupKeyboardLayouts(this.mInputDeviceIdentifier);
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        show(getActivity().getSupportFragmentManager(), "layout");
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void onKeyboardLayoutClicked(int i) {
        if (i >= 0 && i < this.mAdapter.getCount()) {
            KeyboardLayout keyboardLayout = (KeyboardLayout) this.mAdapter.getItem(i);
            if (keyboardLayout != null) {
                this.mIm.setCurrentKeyboardLayoutForInputDevice(this.mInputDeviceIdentifier, keyboardLayout.getDescriptor());
            }
            dismiss();
        }
    }

    @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
    public Loader<Keyboards> onCreateLoader(int i, Bundle bundle) {
        return new KeyboardLayoutLoader(getActivity().getBaseContext(), this.mInputDeviceIdentifier);
    }

    public void onLoadFinished(Loader<Keyboards> loader, Keyboards keyboards) {
        this.mAdapter.clear();
        this.mAdapter.addAll(keyboards.keyboardLayouts);
        this.mAdapter.setCheckedItem(keyboards.current);
        AlertDialog alertDialog = (AlertDialog) getDialog();
        if (alertDialog != null) {
            alertDialog.getListView().setItemChecked(keyboards.current, true);
        }
        updateSwitchHintVisibility();
    }

    @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
    public void onLoaderReset(Loader<Keyboards> loader) {
        this.mAdapter.clear();
        updateSwitchHintVisibility();
    }

    public void onInputDeviceChanged(int i) {
        int i2 = this.mInputDeviceId;
        if (i2 >= 0 && i == i2) {
            getLoaderManager().restartLoader(0, null, this);
        }
    }

    public void onInputDeviceRemoved(int i) {
        int i2 = this.mInputDeviceId;
        if (i2 >= 0 && i == i2) {
            dismiss();
        }
    }

    private void updateSwitchHintVisibility() {
        AlertDialog alertDialog = (AlertDialog) getDialog();
        if (alertDialog != null) {
            alertDialog.findViewById(C0010R$id.customPanel).setVisibility(this.mAdapter.getCount() > 1 ? 0 : 8);
        }
    }

    /* access modifiers changed from: private */
    public static final class KeyboardLayoutAdapter extends ArrayAdapter<KeyboardLayout> {
        private int mCheckedItem = -1;
        private final LayoutInflater mInflater;

        public KeyboardLayoutAdapter(Context context) {
            super(context, 17367306);
            this.mInflater = (LayoutInflater) context.getSystemService("layout_inflater");
        }

        public void setCheckedItem(int i) {
            this.mCheckedItem = i;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            String str;
            String str2;
            KeyboardLayout keyboardLayout = (KeyboardLayout) getItem(i);
            if (keyboardLayout != null) {
                str = keyboardLayout.getLabel();
                str2 = keyboardLayout.getCollection();
            } else {
                str = getContext().getString(C0017R$string.keyboard_layout_default_label);
                str2 = "";
            }
            boolean z = i == this.mCheckedItem;
            if (str2.isEmpty()) {
                return inflateOneLine(view, viewGroup, str, z);
            }
            return inflateTwoLine(view, viewGroup, str, str2, z);
        }

        private View inflateOneLine(View view, ViewGroup viewGroup, String str, boolean z) {
            if (view == null || isTwoLine(view)) {
                view = this.mInflater.inflate(17367055, viewGroup, false);
                setTwoLine(view, false);
            }
            CheckedTextView checkedTextView = (CheckedTextView) view.findViewById(16908308);
            checkedTextView.setText(str);
            checkedTextView.setChecked(z);
            return view;
        }

        private View inflateTwoLine(View view, ViewGroup viewGroup, String str, String str2, boolean z) {
            if (view == null || !isTwoLine(view)) {
                view = this.mInflater.inflate(17367306, viewGroup, false);
                setTwoLine(view, true);
            }
            ((TextView) view.findViewById(16908308)).setText(str);
            ((TextView) view.findViewById(16908309)).setText(str2);
            ((RadioButton) view.findViewById(16909333)).setChecked(z);
            return view;
        }

        private static boolean isTwoLine(View view) {
            return view.getTag() == Boolean.TRUE;
        }

        private static void setTwoLine(View view, boolean z) {
            view.setTag(Boolean.valueOf(z));
        }
    }

    private static final class KeyboardLayoutLoader extends AsyncTaskLoader<Keyboards> {
        private final InputDeviceIdentifier mInputDeviceIdentifier;

        public KeyboardLayoutLoader(Context context, InputDeviceIdentifier inputDeviceIdentifier) {
            super(context);
            this.mInputDeviceIdentifier = inputDeviceIdentifier;
        }

        @Override // androidx.loader.content.AsyncTaskLoader
        public Keyboards loadInBackground() {
            Keyboards keyboards = new Keyboards();
            InputManager inputManager = (InputManager) getContext().getSystemService("input");
            for (String str : inputManager.getEnabledKeyboardLayoutsForInputDevice(this.mInputDeviceIdentifier)) {
                KeyboardLayout keyboardLayout = inputManager.getKeyboardLayout(str);
                if (keyboardLayout != null) {
                    keyboards.keyboardLayouts.add(keyboardLayout);
                }
            }
            Collections.sort(keyboards.keyboardLayouts);
            String currentKeyboardLayoutForInputDevice = inputManager.getCurrentKeyboardLayoutForInputDevice(this.mInputDeviceIdentifier);
            if (currentKeyboardLayoutForInputDevice != null) {
                int size = keyboards.keyboardLayouts.size();
                int i = 0;
                while (true) {
                    if (i >= size) {
                        break;
                    } else if (keyboards.keyboardLayouts.get(i).getDescriptor().equals(currentKeyboardLayoutForInputDevice)) {
                        keyboards.current = i;
                        break;
                    } else {
                        i++;
                    }
                }
            }
            if (keyboards.keyboardLayouts.isEmpty()) {
                keyboards.keyboardLayouts.add(null);
                keyboards.current = 0;
            }
            return keyboards;
        }

        /* access modifiers changed from: protected */
        @Override // androidx.loader.content.Loader
        public void onStartLoading() {
            super.onStartLoading();
            forceLoad();
        }

        /* access modifiers changed from: protected */
        @Override // androidx.loader.content.Loader
        public void onStopLoading() {
            super.onStopLoading();
            cancelLoad();
        }
    }
}
