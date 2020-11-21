package com.android.settings.datetime;

import android.app.Dialog;
import android.content.Context;
import androidx.fragment.app.FragmentActivity;
import com.android.settings.C0019R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.datetime.DatePreferenceController;
import com.android.settings.datetime.TimePreferenceController;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.core.AbstractPreferenceController;
import com.google.android.setupcompat.util.WizardManagerHelper;
import java.util.ArrayList;
import java.util.List;

public class DateTimeSettings extends DashboardFragment implements TimePreferenceController.TimePreferenceHost, DatePreferenceController.DatePreferenceHost {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C0019R$xml.date_time_prefs);

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.DialogCreatable
    public int getDialogMetricsCategory(int i) {
        if (i != 0) {
            return i != 1 ? 0 : 608;
        }
        return 607;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "DateTimeSettings";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 38;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.date_time_prefs;
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onAttach(Context context) {
        super.onAttach(context);
        getSettingsLifecycle().addObserver(new TimeChangeListenerMixin(context, this));
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        ArrayList arrayList = new ArrayList();
        FragmentActivity activity = getActivity();
        boolean booleanExtra = activity.getIntent().getBooleanExtra(WizardManagerHelper.EXTRA_IS_FIRST_RUN, false);
        AutoTimeZonePreferenceController autoTimeZonePreferenceController = new AutoTimeZonePreferenceController(activity, this, booleanExtra);
        AutoTimePreferenceController autoTimePreferenceController = new AutoTimePreferenceController(activity, this);
        arrayList.add(autoTimeZonePreferenceController);
        arrayList.add(autoTimePreferenceController);
        arrayList.add(new TimeFormatPreferenceController(activity, this, booleanExtra));
        arrayList.add(new TimeZonePreferenceController(activity, autoTimeZonePreferenceController));
        arrayList.add(new TimePreferenceController(activity, this, autoTimePreferenceController));
        arrayList.add(new DatePreferenceController(activity, this, autoTimePreferenceController));
        return arrayList;
    }

    @Override // com.android.settings.datetime.UpdateTimeAndDateCallback
    public void updateTimeAndDateDisplay(Context context) {
        updatePreferenceStates();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.DialogCreatable
    public Dialog onCreateDialog(int i) {
        if (i == 0) {
            return ((DatePreferenceController) use(DatePreferenceController.class)).buildDatePicker(getActivity());
        }
        if (i == 1) {
            return ((TimePreferenceController) use(TimePreferenceController.class)).buildTimePicker(getActivity());
        }
        throw new IllegalArgumentException();
    }

    @Override // com.android.settings.datetime.TimePreferenceController.TimePreferenceHost
    public void showTimePicker() {
        removeDialog(1);
        showDialog(1);
    }

    @Override // com.android.settings.datetime.DatePreferenceController.DatePreferenceHost
    public void showDatePicker() {
        showDialog(0);
    }
}
