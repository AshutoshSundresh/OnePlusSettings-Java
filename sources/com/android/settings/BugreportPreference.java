package com.android.settings;

import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.RemoteException;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.CustomDialogPreferenceCompat;

public class BugreportPreference extends CustomDialogPreferenceCompat {
    private TextView mFullSummary;
    private CheckedTextView mFullTitle;
    private TextView mInteractiveSummary;
    private CheckedTextView mInteractiveTitle;

    public BugreportPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settingslib.CustomDialogPreferenceCompat
    public void onPrepareDialogBuilder(AlertDialog.Builder builder, DialogInterface.OnClickListener onClickListener) {
        super.onPrepareDialogBuilder(builder, onClickListener);
        View inflate = View.inflate(getContext(), C0012R$layout.bugreport_options_dialog, null);
        this.mInteractiveTitle = (CheckedTextView) inflate.findViewById(C0010R$id.bugreport_option_interactive_title);
        this.mInteractiveSummary = (TextView) inflate.findViewById(C0010R$id.bugreport_option_interactive_summary);
        this.mFullTitle = (CheckedTextView) inflate.findViewById(C0010R$id.bugreport_option_full_title);
        this.mFullSummary = (TextView) inflate.findViewById(C0010R$id.bugreport_option_full_summary);
        AnonymousClass1 r1 = new View.OnClickListener() {
            /* class com.android.settings.BugreportPreference.AnonymousClass1 */

            public void onClick(View view) {
                if (view == BugreportPreference.this.mFullTitle || view == BugreportPreference.this.mFullSummary) {
                    BugreportPreference.this.mInteractiveTitle.setChecked(false);
                    BugreportPreference.this.mFullTitle.setChecked(true);
                }
                if (view == BugreportPreference.this.mInteractiveTitle || view == BugreportPreference.this.mInteractiveSummary) {
                    BugreportPreference.this.mInteractiveTitle.setChecked(true);
                    BugreportPreference.this.mFullTitle.setChecked(false);
                }
            }
        };
        this.mInteractiveTitle.setOnClickListener(r1);
        this.mFullTitle.setOnClickListener(r1);
        this.mInteractiveSummary.setOnClickListener(r1);
        this.mFullSummary.setOnClickListener(r1);
        builder.setPositiveButton(17041142, onClickListener);
        builder.setView(inflate);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settingslib.CustomDialogPreferenceCompat
    public void onClick(DialogInterface dialogInterface, int i) {
        if (i == -1) {
            Context context = getContext();
            if (this.mFullTitle.isChecked()) {
                Log.v("BugreportPreference", "Taking full bugreport right away");
                FeatureFactory.getFactory(context).getMetricsFeatureProvider().action(context, 295, new Pair[0]);
                try {
                    ActivityManager.getService().requestFullBugReport();
                } catch (RemoteException e) {
                    Log.e("BugreportPreference", "error taking bugreport (bugreportType=Full)", e);
                }
            } else {
                Log.v("BugreportPreference", "Taking interactive bugreport right away");
                FeatureFactory.getFactory(context).getMetricsFeatureProvider().action(context, 294, new Pair[0]);
                try {
                    ActivityManager.getService().requestInteractiveBugReport();
                } catch (RemoteException e2) {
                    Log.e("BugreportPreference", "error taking bugreport (bugreportType=Interactive)", e2);
                }
            }
        }
    }
}
