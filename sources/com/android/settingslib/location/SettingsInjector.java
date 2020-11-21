package com.android.settingslib.location;

import android.R;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.SystemClock;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.AttributeSet;
import android.util.Log;
import androidx.preference.Preference;
import com.android.settingslib.R$string;
import com.android.settingslib.location.InjectedSetting;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.xmlpull.v1.XmlPullParserException;

public class SettingsInjector {
    protected final Context mContext;
    private final Handler mHandler = new StatusLoadingHandler(this.mSettings);
    protected final Set<Setting> mSettings = new HashSet();

    /* access modifiers changed from: protected */
    public abstract Preference createPreference(Context context, InjectedSetting injectedSetting);

    /* access modifiers changed from: protected */
    public abstract void logPreferenceClick(Intent intent);

    public SettingsInjector(Context context) {
        this.mContext = context;
    }

    /* access modifiers changed from: protected */
    public List<InjectedSetting> getSettings(UserHandle userHandle) {
        PackageManager packageManager = this.mContext.getPackageManager();
        Intent intent = new Intent("android.location.SettingInjectorService");
        int identifier = userHandle.getIdentifier();
        List<ResolveInfo> queryIntentServicesAsUser = packageManager.queryIntentServicesAsUser(intent, 128, identifier);
        if (Log.isLoggable("SettingsInjector", 3)) {
            Log.d("SettingsInjector", "Found services for profile id " + identifier + ": " + queryIntentServicesAsUser);
        }
        ArrayList arrayList = new ArrayList(queryIntentServicesAsUser.size());
        for (ResolveInfo resolveInfo : queryIntentServicesAsUser) {
            try {
                InjectedSetting parseServiceInfo = parseServiceInfo(resolveInfo, userHandle, packageManager);
                if (parseServiceInfo == null) {
                    Log.w("SettingsInjector", "Unable to load service info " + resolveInfo);
                } else {
                    arrayList.add(parseServiceInfo);
                }
            } catch (XmlPullParserException e) {
                Log.w("SettingsInjector", "Unable to load service info " + resolveInfo, e);
            } catch (IOException e2) {
                Log.w("SettingsInjector", "Unable to load service info " + resolveInfo, e2);
            }
        }
        if (Log.isLoggable("SettingsInjector", 3)) {
            Log.d("SettingsInjector", "Loaded settings for profile id " + identifier + ": " + arrayList);
        }
        return arrayList;
    }

    private void populatePreference(Preference preference, InjectedSetting injectedSetting) {
        preference.setTitle(injectedSetting.title);
        preference.setSummary(R$string.loading_injected_setting_summary);
        preference.setOnPreferenceClickListener(new ServiceSettingClickedListener(injectedSetting));
    }

    private boolean hasMultipleUsers(Context context) {
        return ((UserManager) context.getSystemService("user")).getUsers().size() > 1;
    }

    public Map<Integer, List<Preference>> getInjectedSettings(Context context, int i) {
        List<UserHandle> userProfiles = ((UserManager) this.mContext.getSystemService("user")).getUserProfiles();
        ArrayMap arrayMap = new ArrayMap();
        this.mSettings.clear();
        for (UserHandle userHandle : userProfiles) {
            if (i == -2 || i == userHandle.getIdentifier()) {
                ArrayList arrayList = new ArrayList();
                for (T t : getSettings(userHandle)) {
                    Preference createPreference = createPreference(context, t);
                    populatePreference(createPreference, t);
                    arrayList.add(createPreference);
                    this.mSettings.add(new Setting(t, createPreference));
                }
                if (!arrayList.isEmpty()) {
                    arrayMap.put(Integer.valueOf(userHandle.getIdentifier()), arrayList);
                }
            }
            if (userProfiles.size() < 3 && hasMultipleUsers(this.mContext)) {
                break;
            }
        }
        reloadStatusMessages();
        return arrayMap;
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x008a, code lost:
        r4 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x00a4, code lost:
        throw new org.xmlpull.v1.XmlPullParserException("Unable to load resources for package " + r4.packageName);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x00a5, code lost:
        if (0 != 0) goto L_0x00a7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x00a7, code lost:
        r2.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x00aa, code lost:
        throw r4;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:27:0x008c */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public com.android.settingslib.location.InjectedSetting parseServiceInfo(android.content.pm.ResolveInfo r5, android.os.UserHandle r6, android.content.pm.PackageManager r7) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
        // Method dump skipped, instructions count: 171
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settingslib.location.SettingsInjector.parseServiceInfo(android.content.pm.ResolveInfo, android.os.UserHandle, android.content.pm.PackageManager):com.android.settingslib.location.InjectedSetting");
    }

    private static InjectedSetting parseAttributes(String str, String str2, UserHandle userHandle, Resources resources, AttributeSet attributeSet) {
        TypedArray obtainAttributes = resources.obtainAttributes(attributeSet, R.styleable.SettingInjectorService);
        try {
            String string = obtainAttributes.getString(1);
            int resourceId = obtainAttributes.getResourceId(0, 0);
            String string2 = obtainAttributes.getString(2);
            String string3 = obtainAttributes.getString(3);
            if (Log.isLoggable("SettingsInjector", 3)) {
                Log.d("SettingsInjector", "parsed title: " + string + ", iconId: " + resourceId + ", settingsActivity: " + string2);
            }
            InjectedSetting.Builder builder = new InjectedSetting.Builder();
            builder.setPackageName(str);
            builder.setClassName(str2);
            builder.setTitle(string);
            builder.setIconId(resourceId);
            builder.setUserHandle(userHandle);
            builder.setSettingsActivity(string2);
            builder.setUserRestriction(string3);
            return builder.build();
        } finally {
            obtainAttributes.recycle();
        }
    }

    public void reloadStatusMessages() {
        if (Log.isLoggable("SettingsInjector", 3)) {
            Log.d("SettingsInjector", "reloadingStatusMessages: " + this.mSettings);
        }
        Handler handler = this.mHandler;
        handler.sendMessage(handler.obtainMessage(1));
    }

    /* access modifiers changed from: protected */
    public class ServiceSettingClickedListener implements Preference.OnPreferenceClickListener {
        private InjectedSetting mInfo;

        public ServiceSettingClickedListener(InjectedSetting injectedSetting) {
            this.mInfo = injectedSetting;
        }

        @Override // androidx.preference.Preference.OnPreferenceClickListener
        public boolean onPreferenceClick(Preference preference) {
            Intent intent = new Intent();
            InjectedSetting injectedSetting = this.mInfo;
            intent.setClassName(injectedSetting.packageName, injectedSetting.settingsActivity);
            SettingsInjector.this.logPreferenceClick(intent);
            intent.setFlags(268468224);
            SettingsInjector.this.mContext.startActivityAsUser(intent, this.mInfo.mUserHandle);
            return true;
        }
    }

    private static final class StatusLoadingHandler extends Handler {
        WeakReference<Set<Setting>> mAllSettings;
        private Set<Setting> mSettingsBeingLoaded = new ArraySet();
        private Deque<Setting> mSettingsToLoad = new ArrayDeque();

        public StatusLoadingHandler(Set<Setting> set) {
            super(Looper.getMainLooper());
            this.mAllSettings = new WeakReference<>(set);
        }

        public void handleMessage(Message message) {
            if (Log.isLoggable("SettingsInjector", 3)) {
                Log.d("SettingsInjector", "handleMessage start: " + message + ", " + this);
            }
            int i = message.what;
            if (i == 1) {
                Set<Setting> set = this.mAllSettings.get();
                if (set != null) {
                    this.mSettingsToLoad.clear();
                    this.mSettingsToLoad.addAll(set);
                }
            } else if (i == 2) {
                Setting setting = (Setting) message.obj;
                setting.maybeLogElapsedTime();
                this.mSettingsBeingLoaded.remove(setting);
                removeMessages(3, setting);
            } else if (i != 3) {
                Log.wtf("SettingsInjector", "Unexpected what: " + message);
            } else {
                Setting setting2 = (Setting) message.obj;
                this.mSettingsBeingLoaded.remove(setting2);
                if (Log.isLoggable("SettingsInjector", 5)) {
                    Log.w("SettingsInjector", "Timed out after " + setting2.getElapsedTime() + " millis trying to get status for: " + setting2);
                }
            }
            if (this.mSettingsBeingLoaded.size() > 0) {
                if (Log.isLoggable("SettingsInjector", 2)) {
                    Log.v("SettingsInjector", "too many services already live for " + message + ", " + this);
                }
            } else if (!this.mSettingsToLoad.isEmpty()) {
                Setting removeFirst = this.mSettingsToLoad.removeFirst();
                removeFirst.startService();
                this.mSettingsBeingLoaded.add(removeFirst);
                sendMessageDelayed(obtainMessage(3, removeFirst), 1000);
                if (Log.isLoggable("SettingsInjector", 3)) {
                    Log.d("SettingsInjector", "handleMessage end " + message + ", " + this + ", started loading " + removeFirst);
                }
            } else if (Log.isLoggable("SettingsInjector", 2)) {
                Log.v("SettingsInjector", "nothing left to do for " + message + ", " + this);
            }
        }

        public String toString() {
            return "StatusLoadingHandler{mSettingsToLoad=" + this.mSettingsToLoad + ", mSettingsBeingLoaded=" + this.mSettingsBeingLoaded + '}';
        }
    }

    /* access modifiers changed from: private */
    public static class MessengerHandler extends Handler {
        private Handler mHandler;
        private WeakReference<Setting> mSettingRef;

        public MessengerHandler(Setting setting, Handler handler) {
            this.mSettingRef = new WeakReference<>(setting);
            this.mHandler = handler;
        }

        public void handleMessage(Message message) {
            Setting setting = this.mSettingRef.get();
            if (setting != null) {
                Preference preference = setting.preference;
                Bundle data = message.getData();
                boolean z = data.getBoolean("enabled", true);
                String string = data.getString("summary", null);
                if (Log.isLoggable("SettingsInjector", 3)) {
                    Log.d("SettingsInjector", setting + ": received " + message + ", bundle: " + data);
                }
                preference.setSummary(string);
                preference.setEnabled(z);
                Handler handler = this.mHandler;
                handler.sendMessage(handler.obtainMessage(2, setting));
            }
        }
    }

    /* access modifiers changed from: protected */
    public final class Setting {
        public final Preference preference;
        public final InjectedSetting setting;
        public long startMillis;

        public Setting(InjectedSetting injectedSetting, Preference preference2) {
            this.setting = injectedSetting;
            this.preference = preference2;
        }

        public String toString() {
            return "Setting{setting=" + this.setting + ", preference=" + this.preference + '}';
        }

        public void startService() {
            if (((ActivityManager) SettingsInjector.this.mContext.getSystemService("activity")).isUserRunning(this.setting.mUserHandle.getIdentifier())) {
                MessengerHandler messengerHandler = new MessengerHandler(this, SettingsInjector.this.mHandler);
                Messenger messenger = new Messenger(messengerHandler);
                Intent serviceIntent = this.setting.getServiceIntent();
                serviceIntent.putExtra("messenger", messenger);
                if (Log.isLoggable("SettingsInjector", 3)) {
                    Log.d("SettingsInjector", this.setting + ": sending update intent: " + serviceIntent + ", handler: " + messengerHandler);
                    this.startMillis = SystemClock.elapsedRealtime();
                } else {
                    this.startMillis = 0;
                }
                SettingsInjector.this.mContext.startServiceAsUser(serviceIntent, this.setting.mUserHandle);
            } else if (Log.isLoggable("SettingsInjector", 2)) {
                Log.v("SettingsInjector", "Cannot start service as user " + this.setting.mUserHandle.getIdentifier() + " is not running");
            }
        }

        public long getElapsedTime() {
            return SystemClock.elapsedRealtime() - this.startMillis;
        }

        public void maybeLogElapsedTime() {
            if (Log.isLoggable("SettingsInjector", 3) && this.startMillis != 0) {
                long elapsedTime = getElapsedTime();
                Log.d("SettingsInjector", this + " update took " + elapsedTime + " millis");
            }
        }
    }
}
