package com.android.settings.password;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.google.android.setupcompat.util.WizardManagerHelper;
import java.util.List;

public class ChooseLockTypeDialogFragment extends InstrumentedDialogFragment implements DialogInterface.OnClickListener {
    private ScreenLockAdapter mAdapter;
    private ChooseLockGenericController mController;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 990;
    }

    public static ChooseLockTypeDialogFragment newInstance(int i) {
        Bundle bundle = new Bundle();
        bundle.putInt("userId", i);
        ChooseLockTypeDialogFragment chooseLockTypeDialogFragment = new ChooseLockTypeDialogFragment();
        chooseLockTypeDialogFragment.setArguments(bundle);
        return chooseLockTypeDialogFragment;
    }

    public interface OnLockTypeSelectedListener {
        void onLockTypeSelected(ScreenLockType screenLockType);

        default void startChooseLockActivity(ScreenLockType screenLockType, Activity activity) {
            Intent intent = activity.getIntent();
            Intent intent2 = new Intent(activity, SetupChooseLockGeneric.class);
            intent2.addFlags(33554432);
            ChooseLockTypeDialogFragment.copyBooleanExtra(intent, intent2, "has_challenge", false);
            ChooseLockTypeDialogFragment.copyBooleanExtra(intent, intent2, "show_options_button", false);
            if (intent.hasExtra("choose_lock_generic_extras")) {
                intent2.putExtras(intent.getBundleExtra("choose_lock_generic_extras"));
            }
            intent2.putExtra("lockscreen.password_type", screenLockType.defaultQuality);
            intent2.putExtra("challenge", intent.getLongExtra("challenge", 0));
            WizardManagerHelper.copyWizardManagerExtras(intent, intent2);
            activity.startActivity(intent2);
            activity.finish();
        }
    }

    /* access modifiers changed from: private */
    public static void copyBooleanExtra(Intent intent, Intent intent2, String str, boolean z) {
        intent2.putExtra(str, intent.getBooleanExtra(str, z));
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservableDialogFragment, androidx.fragment.app.DialogFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mController = new ChooseLockGenericController(getContext(), getArguments().getInt("userId"));
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        OnLockTypeSelectedListener onLockTypeSelectedListener;
        Fragment parentFragment = getParentFragment();
        if (parentFragment instanceof OnLockTypeSelectedListener) {
            onLockTypeSelectedListener = (OnLockTypeSelectedListener) parentFragment;
        } else {
            Context context = getContext();
            onLockTypeSelectedListener = context instanceof OnLockTypeSelectedListener ? (OnLockTypeSelectedListener) context : null;
        }
        if (onLockTypeSelectedListener != null) {
            onLockTypeSelectedListener.onLockTypeSelected((ScreenLockType) this.mAdapter.getItem(i));
        }
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        Context context = getContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        ScreenLockAdapter screenLockAdapter = new ScreenLockAdapter(context, this.mController.getVisibleScreenLockTypes(65536, false), this.mController);
        this.mAdapter = screenLockAdapter;
        builder.setAdapter(screenLockAdapter, this);
        builder.setTitle(C0017R$string.setup_lock_settings_options_dialog_title);
        return builder.create();
    }

    private static class ScreenLockAdapter extends ArrayAdapter<ScreenLockType> {
        private final ChooseLockGenericController mController;

        ScreenLockAdapter(Context context, List<ScreenLockType> list, ChooseLockGenericController chooseLockGenericController) {
            super(context, C0012R$layout.choose_lock_dialog_item, list);
            this.mController = chooseLockGenericController;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            Context context = viewGroup.getContext();
            if (view == null) {
                view = LayoutInflater.from(context).inflate(C0012R$layout.choose_lock_dialog_item, viewGroup, false);
            }
            ScreenLockType screenLockType = (ScreenLockType) getItem(i);
            TextView textView = (TextView) view;
            textView.setText(this.mController.getTitle(screenLockType));
            textView.setCompoundDrawablesRelativeWithIntrinsicBounds(getIconForScreenLock(context, screenLockType), (Drawable) null, (Drawable) null, (Drawable) null);
            return view;
        }

        private static Drawable getIconForScreenLock(Context context, ScreenLockType screenLockType) {
            int i = AnonymousClass1.$SwitchMap$com$android$settings$password$ScreenLockType[screenLockType.ordinal()];
            if (i == 1) {
                return context.getDrawable(C0008R$drawable.ic_pattern);
            }
            if (i == 2) {
                return context.getDrawable(C0008R$drawable.ic_pin);
            }
            if (i != 3) {
                return null;
            }
            return context.getDrawable(C0008R$drawable.ic_password);
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: com.android.settings.password.ChooseLockTypeDialogFragment$1  reason: invalid class name */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$android$settings$password$ScreenLockType;

        /* JADX WARNING: Can't wrap try/catch for region: R(14:0|1|2|3|4|5|6|7|8|9|10|11|12|14) */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:11:0x003e */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0012 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x001d */
        /* JADX WARNING: Missing exception handler attribute for start block: B:7:0x0028 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:9:0x0033 */
        static {
            /*
                com.android.settings.password.ScreenLockType[] r0 = com.android.settings.password.ScreenLockType.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                com.android.settings.password.ChooseLockTypeDialogFragment.AnonymousClass1.$SwitchMap$com$android$settings$password$ScreenLockType = r0
                com.android.settings.password.ScreenLockType r1 = com.android.settings.password.ScreenLockType.PATTERN     // Catch:{ NoSuchFieldError -> 0x0012 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0012 }
                r2 = 1
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0012 }
            L_0x0012:
                int[] r0 = com.android.settings.password.ChooseLockTypeDialogFragment.AnonymousClass1.$SwitchMap$com$android$settings$password$ScreenLockType     // Catch:{ NoSuchFieldError -> 0x001d }
                com.android.settings.password.ScreenLockType r1 = com.android.settings.password.ScreenLockType.PIN     // Catch:{ NoSuchFieldError -> 0x001d }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001d }
                r2 = 2
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001d }
            L_0x001d:
                int[] r0 = com.android.settings.password.ChooseLockTypeDialogFragment.AnonymousClass1.$SwitchMap$com$android$settings$password$ScreenLockType     // Catch:{ NoSuchFieldError -> 0x0028 }
                com.android.settings.password.ScreenLockType r1 = com.android.settings.password.ScreenLockType.PASSWORD     // Catch:{ NoSuchFieldError -> 0x0028 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0028 }
                r2 = 3
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0028 }
            L_0x0028:
                int[] r0 = com.android.settings.password.ChooseLockTypeDialogFragment.AnonymousClass1.$SwitchMap$com$android$settings$password$ScreenLockType     // Catch:{ NoSuchFieldError -> 0x0033 }
                com.android.settings.password.ScreenLockType r1 = com.android.settings.password.ScreenLockType.NONE     // Catch:{ NoSuchFieldError -> 0x0033 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0033 }
                r2 = 4
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0033 }
            L_0x0033:
                int[] r0 = com.android.settings.password.ChooseLockTypeDialogFragment.AnonymousClass1.$SwitchMap$com$android$settings$password$ScreenLockType     // Catch:{ NoSuchFieldError -> 0x003e }
                com.android.settings.password.ScreenLockType r1 = com.android.settings.password.ScreenLockType.SWIPE     // Catch:{ NoSuchFieldError -> 0x003e }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x003e }
                r2 = 5
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x003e }
            L_0x003e:
                int[] r0 = com.android.settings.password.ChooseLockTypeDialogFragment.AnonymousClass1.$SwitchMap$com$android$settings$password$ScreenLockType     // Catch:{ NoSuchFieldError -> 0x0049 }
                com.android.settings.password.ScreenLockType r1 = com.android.settings.password.ScreenLockType.MANAGED     // Catch:{ NoSuchFieldError -> 0x0049 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0049 }
                r2 = 6
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0049 }
            L_0x0049:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.settings.password.ChooseLockTypeDialogFragment.AnonymousClass1.<clinit>():void");
        }
    }
}
