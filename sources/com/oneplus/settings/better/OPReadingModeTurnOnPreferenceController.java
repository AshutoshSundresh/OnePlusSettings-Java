package com.oneplus.settings.better;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0003R$array;
import com.android.settings.C0017R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settings.widget.MasterSwitchController;
import com.android.settings.widget.MasterSwitchPreference;
import com.android.settings.widget.SwitchWidgetController;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnPause;
import com.android.settingslib.core.lifecycle.events.OnResume;

public class OPReadingModeTurnOnPreferenceController extends BasePreferenceController implements LifecycleObserver, SwitchWidgetController.OnSwitchChangeListener, OnResume, OnPause {
    private static final int ASK_VALUE = 0;
    private static final int CHROMATIC_VALUE = 2;
    private static final int DIALOG_SELECTED_CHROMATIC = 0;
    private static final int DIALOG_SELECTED_MONO = 1;
    private static final String KEY_READING_MODE_TURN_ON = "reading_mode_turn_on";
    private static final int MONO_VALUE = 1;
    public static final String READING_MODE_STATUS = "reading_mode_status";
    public static final String READING_MODE_STATUS_MANUAL = "reading_mode_status_manual";
    private static final String TAG = "com.oneplus.settings.better.OPReadingModeTurnOnPreferenceController";
    private Lifecycle mLifecycle;
    private SettingObserver mSettingObserver;
    private MasterSwitchPreference mSwitch;
    private MasterSwitchController mSwitchController;
    private int selectvalue;

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 0;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public String getPreferenceKey() {
        return KEY_READING_MODE_TURN_ON;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public OPReadingModeTurnOnPreferenceController(Context context, Lifecycle lifecycle) {
        super(context, KEY_READING_MODE_TURN_ON);
        if (lifecycle != null) {
            lifecycle.addObserver(this);
            this.mLifecycle = lifecycle;
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mSwitch = (MasterSwitchPreference) preferenceScreen.findPreference(KEY_READING_MODE_TURN_ON);
        if (this.mLifecycle != null) {
            this.mSettingObserver = new SettingObserver(this.mSwitch);
        }
    }

    @Override // com.android.settings.widget.SwitchWidgetController.OnSwitchChangeListener
    public boolean onSwitchToggled(boolean z) {
        int i = Settings.System.getInt(this.mContext.getContentResolver(), "reading_mode_option_manual", 0);
        if (!z) {
            Settings.System.putStringForUser(this.mContext.getContentResolver(), READING_MODE_STATUS_MANUAL, "force-off", -2);
        } else if (i == 0) {
            showSelectEffectDialog();
        } else if (i == 1) {
            Settings.System.putStringForUser(this.mContext.getContentResolver(), READING_MODE_STATUS_MANUAL, "force-on", -2);
        } else if (i == 2) {
            Settings.System.putStringForUser(this.mContext.getContentResolver(), READING_MODE_STATUS_MANUAL, "force-on-color", -2);
        }
        return true;
    }

    private void showSelectEffectDialog() {
        this.selectvalue = 2;
        AlertDialog.Builder builder = new AlertDialog.Builder(this.mContext);
        builder.setTitle(C0017R$string.oneplus_reading_mode_select_effect);
        builder.setSingleChoiceItems(C0003R$array.oneplus_reading_mode_effec_select, 3, new DialogInterface.OnClickListener() {
            /* class com.oneplus.settings.better.OPReadingModeTurnOnPreferenceController.AnonymousClass3 */

            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    OPReadingModeTurnOnPreferenceController.this.selectvalue = 2;
                    Settings.System.putStringForUser(((AbstractPreferenceController) OPReadingModeTurnOnPreferenceController.this).mContext.getContentResolver(), OPReadingModeTurnOnPreferenceController.READING_MODE_STATUS_MANUAL, "force-on-color", -2);
                    dialogInterface.dismiss();
                } else if (i != 1) {
                    dialogInterface.dismiss();
                } else {
                    OPReadingModeTurnOnPreferenceController.this.selectvalue = 1;
                    Settings.System.putStringForUser(((AbstractPreferenceController) OPReadingModeTurnOnPreferenceController.this).mContext.getContentResolver(), OPReadingModeTurnOnPreferenceController.READING_MODE_STATUS_MANUAL, "force-on", -2);
                    dialogInterface.dismiss();
                }
            }
        });
        builder.setNegativeButton(C0017R$string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
            /* class com.oneplus.settings.better.OPReadingModeTurnOnPreferenceController.AnonymousClass2 */

            public void onClick(DialogInterface dialogInterface, int i) {
                Settings.System.putStringForUser(((AbstractPreferenceController) OPReadingModeTurnOnPreferenceController.this).mContext.getContentResolver(), OPReadingModeTurnOnPreferenceController.READING_MODE_STATUS_MANUAL, "force-off", -2);
                OPReadingModeTurnOnPreferenceController.this.mSwitch.setChecked(false);
                dialogInterface.dismiss();
            }
        });
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            /* class com.oneplus.settings.better.OPReadingModeTurnOnPreferenceController.AnonymousClass1 */

            public void onDismiss(DialogInterface dialogInterface) {
                boolean z = false;
                int intForUser = Settings.System.getIntForUser(((AbstractPreferenceController) OPReadingModeTurnOnPreferenceController.this).mContext.getContentResolver(), OPReadingModeTurnOnPreferenceController.READING_MODE_STATUS, 0, -2);
                MasterSwitchPreference masterSwitchPreference = OPReadingModeTurnOnPreferenceController.this.mSwitch;
                if (intForUser != 0) {
                    z = true;
                }
                masterSwitchPreference.setChecked(z);
            }
        });
        builder.show();
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnResume
    public void onResume() {
        if (isAvailable()) {
            if (this.mSwitch != null) {
                this.mSwitch.setChecked(Settings.System.getIntForUser(this.mContext.getContentResolver(), READING_MODE_STATUS, 0, -2) != 0);
                MasterSwitchController masterSwitchController = new MasterSwitchController(this.mSwitch);
                this.mSwitchController = masterSwitchController;
                masterSwitchController.setListener(this);
                this.mSwitchController.startListening();
                if (this.mSwitch != null) {
                    int i = Settings.System.getInt(this.mContext.getContentResolver(), "reading_mode_option_manual", 0);
                    if (i == 0) {
                        this.mSwitch.setSummary(C0017R$string.oneplus_reading_mode_ask);
                    } else if (i == 1) {
                        this.mSwitch.setSummary(C0017R$string.oneplus_reading_mode_turn_on_mono);
                    } else if (i == 2) {
                        this.mSwitch.setSummary(C0017R$string.oneplus_reading_mode_turn_on_chromatic);
                    }
                }
            }
            SettingObserver settingObserver = this.mSettingObserver;
            if (settingObserver != null) {
                settingObserver.register(this.mContext.getContentResolver(), true);
            }
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnPause
    public void onPause() {
        SettingObserver settingObserver = this.mSettingObserver;
        if (settingObserver != null) {
            settingObserver.register(this.mContext.getContentResolver(), false);
        }
    }

    class SettingObserver extends ContentObserver {
        private final Uri readingModeStatusUri = Settings.System.getUriFor(OPReadingModeTurnOnPreferenceController.READING_MODE_STATUS);

        public SettingObserver(MasterSwitchPreference masterSwitchPreference) {
            super(new Handler());
        }

        public void register(ContentResolver contentResolver, boolean z) {
            if (z) {
                contentResolver.registerContentObserver(this.readingModeStatusUri, false, this);
            } else {
                contentResolver.unregisterContentObserver(this);
            }
        }

        public void onChange(boolean z, Uri uri) {
            super.onChange(z, uri);
            if (this.readingModeStatusUri.equals(uri)) {
                boolean z2 = false;
                int intForUser = Settings.System.getIntForUser(((AbstractPreferenceController) OPReadingModeTurnOnPreferenceController.this).mContext.getContentResolver(), OPReadingModeTurnOnPreferenceController.READING_MODE_STATUS, 0, -2);
                MasterSwitchPreference masterSwitchPreference = OPReadingModeTurnOnPreferenceController.this.mSwitch;
                if (intForUser != 0) {
                    z2 = true;
                }
                masterSwitchPreference.setChecked(z2);
            }
        }
    }
}
