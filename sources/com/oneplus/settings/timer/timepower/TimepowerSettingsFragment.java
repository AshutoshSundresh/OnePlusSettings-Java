package com.oneplus.settings.timer.timepower;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.settings.C0017R$string;
import com.android.settings.C0018R$style;
import com.android.settings.C0019R$xml;
import com.android.settings.SettingsPreferenceFragment;
import com.google.android.material.picker.TimePicker;
import com.google.android.material.picker.TimePickerDialog;
import com.oneplus.settings.utils.OPUtils;
import java.lang.reflect.Array;

public class TimepowerSettingsFragment extends SettingsPreferenceFragment implements TimePickerDialog.OnTimeSetListener, Preference.OnPreferenceChangeListener {
    private int mCode;
    private boolean mDlgVisible = false;
    private TimepowerPreference mPowerOffPref;
    private Preference mPowerOffPreference;
    private SwitchPreference mPowerOffStatePref;
    private TimepowerPreference mPowerOnPref;
    private Preference mPowerOnPreference;
    private SwitchPreference mPowerOnStatePref;
    private boolean mPowerState;
    private boolean[][] mStateArray = ((boolean[][]) Array.newInstance(boolean.class, 2, 2));
    private int[][] mTimeArray = ((int[][]) Array.newInstance(int.class, 2, 2));
    private TimePicker mTimePicker;
    DialogInterface.OnDismissListener onDismissListener = new DialogInterface.OnDismissListener() {
        /* class com.oneplus.settings.timer.timepower.TimepowerSettingsFragment.AnonymousClass5 */

        public void onDismiss(DialogInterface dialogInterface) {
            TimepowerSettingsFragment.this.mDlgVisible = false;
        }
    };

    private static int boolToInt(boolean z) {
        return z ? 1 : 0;
    }

    private static boolean intToBool(int i) {
        return i != 0;
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 9999;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(C0019R$xml.op_time_power_preference);
        init();
    }

    private void init() {
        readData();
        boolean z = this.mStateArray[0][1];
        int[][] iArr = this.mTimeArray;
        String formatTime = formatTime(iArr[0][0], iArr[0][1]);
        boolean z2 = this.mStateArray[1][1];
        int[][] iArr2 = this.mTimeArray;
        String formatTime2 = formatTime(iArr2[1][0], iArr2[1][1]);
        SwitchPreference switchPreference = (SwitchPreference) findPreference("power_on_switch");
        this.mPowerOnStatePref = switchPreference;
        switchPreference.setChecked(z);
        this.mPowerOnStatePref.setOnPreferenceChangeListener(this);
        SwitchPreference switchPreference2 = (SwitchPreference) findPreference("power_off_switch");
        this.mPowerOffStatePref = switchPreference2;
        switchPreference2.setChecked(z2);
        this.mPowerOffStatePref.setOnPreferenceChangeListener(this);
        TimepowerPreference timepowerPreference = (TimepowerPreference) findPreference("power_on_settings");
        this.mPowerOnPref = timepowerPreference;
        timepowerPreference.setTitle(formatTime);
        this.mPowerOnPref.setViewClickListener(new View.OnClickListener() {
            /* class com.oneplus.settings.timer.timepower.TimepowerSettingsFragment.AnonymousClass1 */

            public void onClick(View view) {
                if (!TimepowerSettingsFragment.this.mDlgVisible) {
                    TimepowerSettingsFragment timepowerSettingsFragment = TimepowerSettingsFragment.this;
                    timepowerSettingsFragment.startDialogForResult(timepowerSettingsFragment.getTimeSettingsIntent(0), 0);
                }
            }
        });
        Preference findPreference = findPreference("oneplus_power_on_settings");
        this.mPowerOnPreference = findPreference;
        findPreference.setSummary(formatTime);
        this.mPowerOnPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            /* class com.oneplus.settings.timer.timepower.TimepowerSettingsFragment.AnonymousClass2 */

            @Override // androidx.preference.Preference.OnPreferenceClickListener
            public boolean onPreferenceClick(Preference preference) {
                if (TimepowerSettingsFragment.this.mDlgVisible) {
                    return true;
                }
                TimepowerSettingsFragment timepowerSettingsFragment = TimepowerSettingsFragment.this;
                timepowerSettingsFragment.startDialogForResult(timepowerSettingsFragment.getTimeSettingsIntent(0), 0);
                return false;
            }
        });
        TimepowerPreference timepowerPreference2 = (TimepowerPreference) findPreference("power_off_settings");
        this.mPowerOffPref = timepowerPreference2;
        timepowerPreference2.setTitle(formatTime2);
        this.mPowerOffPref.setViewClickListener(new View.OnClickListener() {
            /* class com.oneplus.settings.timer.timepower.TimepowerSettingsFragment.AnonymousClass3 */

            public void onClick(View view) {
                if (!TimepowerSettingsFragment.this.mDlgVisible) {
                    TimepowerSettingsFragment timepowerSettingsFragment = TimepowerSettingsFragment.this;
                    timepowerSettingsFragment.startDialogForResult(timepowerSettingsFragment.getTimeSettingsIntent(1), 1);
                }
            }
        });
        Preference findPreference2 = findPreference("oneplus_power_off_settings");
        this.mPowerOffPreference = findPreference2;
        findPreference2.setSummary(formatTime2);
        this.mPowerOffPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            /* class com.oneplus.settings.timer.timepower.TimepowerSettingsFragment.AnonymousClass4 */

            @Override // androidx.preference.Preference.OnPreferenceClickListener
            public boolean onPreferenceClick(Preference preference) {
                if (TimepowerSettingsFragment.this.mDlgVisible) {
                    return true;
                }
                TimepowerSettingsFragment timepowerSettingsFragment = TimepowerSettingsFragment.this;
                timepowerSettingsFragment.startDialogForResult(timepowerSettingsFragment.getTimeSettingsIntent(1), 1);
                return false;
            }
        });
        removePreference("power_on_settings");
        removePreference("power_off_settings");
    }

    private void readData() {
        String string = Settings.System.getString(getContentResolver(), "def_timepower_config");
        if (string != null) {
            int i = 0;
            int i2 = 0;
            while (i <= 6) {
                int i3 = i + 6;
                String substring = string.substring(i, i3);
                this.mTimeArray[i2][0] = Integer.parseInt(substring.substring(0, 2));
                this.mTimeArray[i2][1] = Integer.parseInt(substring.substring(2, 4));
                this.mStateArray[i2][0] = intToBool(Integer.parseInt(substring.substring(4, 5)));
                this.mStateArray[i2][1] = intToBool(Integer.parseInt(substring.substring(5, 6)));
                i2++;
                i = i3;
            }
        }
    }

    private String formatTime(int i, int i2) {
        if (is24Hour()) {
            return String.format("%1$02d", Integer.valueOf(i)) + ":" + String.format("%1$02d", Integer.valueOf(i2));
        }
        String string = getString(C0017R$string.android_am);
        if (i >= 12) {
            string = getString(C0017R$string.android_pm);
            if (i > 12) {
                i -= 12;
            }
        } else if (i == 0) {
            i = 12;
        }
        return string + String.format("%1$02d", Integer.valueOf(i)) + ":" + String.format("%1$02d", Integer.valueOf(i2));
    }

    private boolean is24Hour() {
        return DateFormat.is24HourFormat(getActivity());
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private Intent getTimeSettingsIntent(int i) {
        if (i != 0 && i != 1) {
            return null;
        }
        boolean z = this.mStateArray[0][0];
        Log.i("TIMER", this.mPowerOnPreference.getSummary().toString());
        String charSequence = this.mPowerOnPreference.getSummary().toString();
        int[][] iArr = this.mTimeArray;
        int i2 = iArr[0][0];
        int i3 = iArr[0][1];
        if (i == 1) {
            z = this.mStateArray[1][0];
            this.mPowerOffPref.getTitle().toString();
            Log.i("TIMER", this.mPowerOffPreference.getSummary().toString());
            charSequence = this.mPowerOffPreference.getSummary().toString();
            int[][] iArr2 = this.mTimeArray;
            i2 = iArr2[1][0];
            i3 = iArr2[1][1];
        }
        return getEditIntent(i, i2, i3, z, charSequence);
    }

    private Intent getEditIntent(int i, int i2, int i3, boolean z, String str) {
        if (i != 0 && i != 1) {
            return null;
        }
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putBoolean("24hour", is24Hour());
        bundle.putBoolean("power_state", z);
        bundle.putString("display_time", str);
        bundle.putInt("hour", i2);
        bundle.putInt("minute", i3);
        bundle.putInt("power_type", i);
        intent.putExtras(bundle);
        return intent;
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        boolean booleanValue = preference instanceof SwitchPreference ? ((Boolean) obj).booleanValue() : false;
        String key = preference.getKey();
        if ("power_on_switch".equals(key)) {
            updateState(0, booleanValue);
            return true;
        } else if (!"power_off_switch".equals(key)) {
            return false;
        } else {
            updateState(1, booleanValue);
            return true;
        }
    }

    private void updateState(int i, boolean z) {
        if (i == 0 || i == 1) {
            this.mStateArray[i][1] = z;
            if (i == 0 && z) {
                cancleNewPlanLastPowerOn();
                writeData();
                Intent intent = new Intent("com.android.settings.POWER_OP_ON");
                intent.addFlags(285212672);
                getActivity().sendBroadcast(intent);
                if (OPUtils.isSupportNewPlanPowerOffAlarm()) {
                    setPowerOn();
                }
            } else if (i == 1 && z) {
                writeData();
                new Bundle().putLong("trigger_time", new long[2][1]);
                Intent intent2 = new Intent("com.android.settings.action.REQUEST_POWER_OFF");
                intent2.addFlags(285212672);
                getActivity().sendBroadcast(new Intent(intent2));
            } else if (i == 1 && !z) {
                writeData();
                Intent intent3 = new Intent("com.android.settings.POWER_CANCEL_OP_OFF");
                intent3.addFlags(285212672);
                getActivity().sendBroadcast(new Intent(intent3));
            } else if (i == 0 && !z) {
                writeData();
                Intent intent4 = new Intent("com.android.settings.POWER_OP_ON");
                intent4.addFlags(285212672);
                getActivity().sendBroadcast(intent4);
                if (OPUtils.isSupportNewPlanPowerOffAlarm()) {
                    long[] nearestTime = SettingsUtil.getNearestTime(Settings.System.getString(getActivity().getContentResolver(), "def_timepower_config"));
                    Intent intent5 = new Intent("org.codeaurora.poweroffalarm.action.CANCEL_ALARM");
                    intent5.putExtra("time", nearestTime[0]);
                    intent5.setPackage("com.qualcomm.qti.poweroffalarm");
                    intent5.addFlags(285212672);
                    getActivity().sendBroadcast(intent5);
                }
            }
        }
    }

    private void writeData() {
        String str = new String("");
        for (int i = 0; i < 2; i++) {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("%1$02d", Integer.valueOf(this.mTimeArray[i][0])));
            sb.append(String.format("%1$02d", Integer.valueOf(this.mTimeArray[i][1])));
            boolean z = this.mStateArray[i][0];
            boolToInt(z);
            sb.append(String.format("%1$01d", Integer.valueOf(z ? 1 : 0)));
            boolean z2 = this.mStateArray[i][1];
            boolToInt(z2);
            sb.append(String.format("%1$01d", Integer.valueOf(z2 ? 1 : 0)));
            str = str + sb.toString();
        }
        Log.d("TimepowerSettingsFragment", "writeData: " + str);
        Settings.System.putString(getContentResolver(), "def_timepower_config", str);
    }

    private void cancleNewPlanLastPowerOn() {
        if (OPUtils.isSupportNewPlanPowerOffAlarm()) {
            long[] nearestTime = SettingsUtil.getNearestTime(Settings.System.getString(getActivity().getContentResolver(), "def_timepower_config"));
            Intent intent = new Intent("org.codeaurora.poweroffalarm.action.CANCEL_ALARM");
            intent.putExtra("time", nearestTime[0]);
            intent.setPackage("com.qualcomm.qti.poweroffalarm");
            intent.addFlags(285212672);
            getActivity().sendBroadcast(intent);
        }
    }

    private void setPowerOn() {
        long[] nearestTime = SettingsUtil.getNearestTime(Settings.System.getString(getActivity().getContentResolver(), "def_timepower_config"));
        Log.d("TimepowerSettingsFragment", "setPowerOn writeData: " + nearestTime[0]);
        ((AlarmManager) getActivity().getSystemService("alarm")).setExact(0, nearestTime[0], PendingIntent.getBroadcast(getActivity(), 0, new Intent("com.android.settings.POWER_OP_ON"), 134217728));
    }

    private void returnNewTimeSetResult(int i, Intent intent) {
        char c;
        int i2;
        Bundle extras = intent.getExtras();
        if (extras != null) {
            int i3 = extras.getInt("hour");
            int i4 = extras.getInt("minute");
            if (i == 1) {
                i2 = 0;
                c = 1;
            } else {
                c = 0;
                i2 = 1;
            }
            Log.d("TimepowerSettingsFragment", "hour : " + i3 + "  mTimeArray[" + i2 + "][0]" + this.mTimeArray[i2][0]);
            Log.d("TimepowerSettingsFragment", "minute : " + i4 + "  mTimeArray[" + i2 + "][1]" + this.mTimeArray[i2][1]);
            int[][] iArr = this.mTimeArray;
            if (i3 == iArr[i2][0] && i4 == iArr[i2][1]) {
                Toast.makeText(getActivity(), getString(C0017R$string.timepower_time_duplicate), 0).show();
                return;
            }
            this.mTimeArray[c][0] = extras.getInt("hour");
            this.mTimeArray[c][1] = extras.getInt("minute");
            int[][] iArr2 = this.mTimeArray;
            String formatTime = formatTime(iArr2[c][0], iArr2[c][1]);
            this.mStateArray[c][0] = extras.getBoolean("power_state");
            boolean z = this.mStateArray[c][1];
            if (i == 0) {
                this.mPowerOnPref.setTitle(formatTime);
                this.mPowerOnPreference.setSummary(formatTime);
            } else if (i == 1) {
                this.mPowerOffPref.setTitle(formatTime);
                this.mPowerOffPreference.setSummary(formatTime);
            }
            if (z) {
                int i5 = this.mCode;
                if (i5 == 0) {
                    cancleNewPlanLastPowerOn();
                    writeData();
                    Intent intent2 = new Intent("com.android.settings.POWER_OP_ON");
                    intent2.addFlags(285212672);
                    getActivity().sendBroadcast(intent2);
                    setPowerOn();
                } else if (i5 == 1) {
                    writeData();
                    Intent intent3 = new Intent("com.android.settings.action.REQUEST_POWER_OFF");
                    intent3.addFlags(285212672);
                    getActivity().sendBroadcast(intent3);
                } else {
                    writeData();
                }
            } else if (i == 0) {
                updateState(0, true);
                this.mPowerOnStatePref.setChecked(true);
            } else {
                updateState(1, true);
                this.mPowerOffStatePref.setChecked(true);
            }
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void startDialogForResult(Intent intent, int i) {
        if (intent != null) {
            this.mCode = i;
            this.mTimePicker = new TimePicker(getActivity());
            Bundle extras = intent.getExtras();
            this.mPowerState = extras.getBoolean("power_state");
            boolean z = extras.getBoolean("24hour");
            int i2 = extras.getInt("hour");
            int i3 = extras.getInt("minute");
            extras.getInt("power_type");
            this.mTimePicker.setIs24HourView(Boolean.valueOf(z));
            this.mTimePicker.setCurrentHour(Integer.valueOf(i2));
            this.mTimePicker.setCurrentMinute(Integer.valueOf(i3));
            TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), C0018R$style.OnePlus_Theme_Dialog_Picker, this, i2, i3, z);
            timePickerDialog.setOnDismissListener(this.onDismissListener);
            timePickerDialog.show();
            this.mDlgVisible = true;
        }
    }

    @Override // com.google.android.material.picker.TimePickerDialog.OnTimeSetListener
    public void onTimeSet(TimePicker timePicker, int i, int i2) {
        returnData(i, i2);
        this.mDlgVisible = false;
    }

    private void returnData(int i, int i2) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("power_state", this.mPowerState);
        bundle.putInt("hour", i);
        bundle.putInt("minute", i2);
        Intent intent = new Intent();
        intent.putExtras(bundle);
        returnNewTimeSetResult(this.mCode, intent);
    }
}
