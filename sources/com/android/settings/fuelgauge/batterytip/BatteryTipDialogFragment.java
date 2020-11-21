package com.android.settings.fuelgauge.batterytip;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0015R$plurals;
import com.android.settings.C0017R$string;
import com.android.settings.SettingsActivity;
import com.android.settings.Utils;
import com.android.settings.core.InstrumentedPreferenceFragment;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.android.settings.fuelgauge.batterytip.BatteryTipPreferenceController;
import com.android.settings.fuelgauge.batterytip.actions.BatteryTipAction;
import com.android.settings.fuelgauge.batterytip.tips.BatteryTip;
import com.android.settings.fuelgauge.batterytip.tips.HighUsageTip;
import com.android.settings.fuelgauge.batterytip.tips.RestrictAppTip;
import com.android.settings.fuelgauge.batterytip.tips.UnrestrictAppTip;
import java.util.List;

public class BatteryTipDialogFragment extends InstrumentedDialogFragment implements DialogInterface.OnClickListener {
    BatteryTip mBatteryTip;
    int mMetricsKey;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1323;
    }

    public static BatteryTipDialogFragment newInstance(BatteryTip batteryTip, int i) {
        BatteryTipDialogFragment batteryTipDialogFragment = new BatteryTipDialogFragment();
        Bundle bundle = new Bundle(1);
        bundle.putParcelable(BatteryTipPreferenceController.PREF_NAME, batteryTip);
        bundle.putInt("metrics_key", i);
        batteryTipDialogFragment.setArguments(bundle);
        return batteryTipDialogFragment;
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        Bundle arguments = getArguments();
        Context context = getContext();
        this.mBatteryTip = (BatteryTip) arguments.getParcelable(BatteryTipPreferenceController.PREF_NAME);
        this.mMetricsKey = arguments.getInt("metrics_key");
        int type = this.mBatteryTip.getType();
        if (type == 1) {
            RestrictAppTip restrictAppTip = (RestrictAppTip) this.mBatteryTip;
            List<AppInfo> restrictAppList = restrictAppTip.getRestrictAppList();
            int size = restrictAppList.size();
            CharSequence applicationLabel = Utils.getApplicationLabel(context, restrictAppList.get(0).packageName);
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(context.getResources().getQuantityString(C0015R$plurals.battery_tip_restrict_app_dialog_title, size, Integer.valueOf(size)));
            builder.setPositiveButton(C0017R$string.battery_tip_restrict_app_dialog_ok, this);
            builder.setNegativeButton(17039360, (DialogInterface.OnClickListener) null);
            if (size == 1) {
                builder.setMessage(getString(C0017R$string.battery_tip_restrict_app_dialog_message, applicationLabel));
            } else if (size <= 5) {
                builder.setMessage(getString(C0017R$string.battery_tip_restrict_apps_less_than_5_dialog_message));
                RecyclerView recyclerView = (RecyclerView) LayoutInflater.from(context).inflate(C0012R$layout.recycler_view, (ViewGroup) null);
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                recyclerView.setAdapter(new HighUsageAdapter(context, restrictAppList));
                builder.setView(recyclerView);
            } else {
                builder.setMessage(context.getString(C0017R$string.battery_tip_restrict_apps_more_than_5_dialog_message, restrictAppTip.getRestrictAppsString(context)));
            }
            return builder.create();
        } else if (type == 2) {
            HighUsageTip highUsageTip = (HighUsageTip) this.mBatteryTip;
            View inflate = LayoutInflater.from(context).inflate(C0012R$layout.recycler_view, (ViewGroup) null);
            RecyclerView recyclerView2 = (RecyclerView) inflate.findViewById(C0010R$id.recycler_view);
            recyclerView2.setLayoutManager(new LinearLayoutManager(context));
            recyclerView2.setAdapter(new HighUsageAdapter(context, highUsageTip.getHighUsageAppList()));
            AlertDialog.Builder builder2 = new AlertDialog.Builder(context);
            builder2.setTitle(getString(C0017R$string.battery_tip_dialog_message, Integer.valueOf(highUsageTip.getHighUsageAppList().size())));
            builder2.setView(inflate);
            builder2.setPositiveButton(17039370, (DialogInterface.OnClickListener) null);
            return builder2.create();
        } else if (type == 6) {
            AlertDialog.Builder builder3 = new AlertDialog.Builder(context);
            builder3.setTitle(C0017R$string.battery_tip_dialog_summary_message);
            builder3.setPositiveButton(17039370, (DialogInterface.OnClickListener) null);
            return builder3.create();
        } else if (type == 7) {
            Utils.getApplicationLabel(context, ((UnrestrictAppTip) this.mBatteryTip).getPackageName());
            AlertDialog.Builder builder4 = new AlertDialog.Builder(context);
            builder4.setTitle(getString(C0017R$string.battery_tip_unrestrict_app_dialog_title));
            builder4.setMessage(C0017R$string.battery_tip_unrestrict_app_dialog_message);
            builder4.setPositiveButton(C0017R$string.battery_tip_unrestrict_app_dialog_ok, this);
            builder4.setNegativeButton(C0017R$string.battery_tip_unrestrict_app_dialog_cancel, (DialogInterface.OnClickListener) null);
            return builder4.create();
        } else {
            throw new IllegalArgumentException("unknown type " + this.mBatteryTip.getType());
        }
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        BatteryTipPreferenceController.BatteryTipListener batteryTipListener = (BatteryTipPreferenceController.BatteryTipListener) getTargetFragment();
        if (batteryTipListener != null) {
            BatteryTipAction actionForBatteryTip = BatteryTipUtils.getActionForBatteryTip(this.mBatteryTip, (SettingsActivity) getActivity(), (InstrumentedPreferenceFragment) getTargetFragment());
            if (actionForBatteryTip != null) {
                actionForBatteryTip.handlePositiveAction(this.mMetricsKey);
            }
            batteryTipListener.onBatteryTipHandled(this.mBatteryTip);
        }
    }
}
