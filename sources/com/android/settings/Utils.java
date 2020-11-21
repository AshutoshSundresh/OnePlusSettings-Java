package com.android.settings;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AppGlobals;
import android.app.Dialog;
import android.app.IActivityManager;
import android.app.KeyguardManager;
import android.app.admin.DevicePolicyManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.IntentFilterVerificationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.hardware.face.FaceManager;
import android.hardware.fingerprint.FingerprintManager;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.INetworkManagementService;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.storage.StorageManager;
import android.os.storage.VolumeInfo;
import android.preference.PreferenceFrameLayout;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.provider.Telephony;
import android.telephony.TelephonyManager;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.style.TtsSpan;
import android.util.ArraySet;
import android.util.IconDrawableFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import androidx.appcompat.app.AlertDialog;
import androidx.core.graphics.drawable.IconCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import com.android.internal.R;
import com.android.internal.app.UnlaunchableAppActivity;
import com.android.internal.util.ArrayUtils;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.dashboard.profileselector.ProfileFragmentBridge;
import com.android.settingslib.widget.ActionBarShadowController;
import com.oneplus.settings.OPMemberController;
import com.oneplus.settings.utils.OPUtils;
import java.net.InetAddress;
import java.util.Formatter;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import org.codeaurora.internal.IExtTelephony;

public final class Utils extends com.android.settingslib.Utils {
    private static final StringBuilder sBuilder = new StringBuilder(50);
    private static final Formatter sFormatter = new Formatter(sBuilder, Locale.getDefault());

    public static boolean updatePreferenceToSpecificActivityOrRemove(Context context, PreferenceGroup preferenceGroup, String str, int i) {
        Preference findPreference = preferenceGroup.findPreference(str);
        if (findPreference == null) {
            return false;
        }
        Intent intent = findPreference.getIntent();
        if (intent != null) {
            PackageManager packageManager = context.getPackageManager();
            List<ResolveInfo> queryIntentActivities = packageManager.queryIntentActivities(intent, 0);
            int size = queryIntentActivities.size();
            for (int i2 = 0; i2 < size; i2++) {
                ResolveInfo resolveInfo = queryIntentActivities.get(i2);
                if ((resolveInfo.activityInfo.applicationInfo.flags & 1) != 0) {
                    Intent intent2 = new Intent();
                    ActivityInfo activityInfo = resolveInfo.activityInfo;
                    findPreference.setIntent(intent2.setClassName(activityInfo.packageName, activityInfo.name));
                    if ((i & 1) != 0) {
                        findPreference.setTitle(resolveInfo.loadLabel(packageManager));
                    }
                    return true;
                }
            }
        }
        preferenceGroup.removePreference(findPreference);
        return false;
    }

    public static boolean isMonkeyRunning() {
        return ActivityManager.isUserAMonkey();
    }

    public static boolean isVoiceCapable(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService("phone");
        return telephonyManager != null && telephonyManager.isVoiceCapable();
    }

    public static String getWifiIpAddresses(Context context) {
        Network currentNetwork = ((WifiManager) context.getSystemService(WifiManager.class)).getCurrentNetwork();
        if (currentNetwork != null) {
            return formatIpAddresses(((ConnectivityManager) context.getSystemService("connectivity")).getLinkProperties(currentNetwork));
        }
        return null;
    }

    private static String formatIpAddresses(LinkProperties linkProperties) {
        if (linkProperties == null) {
            return null;
        }
        Iterator it = linkProperties.getAllAddresses().iterator();
        if (!it.hasNext()) {
            return null;
        }
        String str = "";
        while (it.hasNext()) {
            str = str + ((InetAddress) it.next()).getHostAddress();
            if (it.hasNext()) {
                str = str + "\n";
            }
        }
        return str;
    }

    public static Locale createLocaleFromString(String str) {
        if (str == null) {
            return Locale.getDefault();
        }
        String[] split = str.split("_", 3);
        if (1 == split.length) {
            return new Locale(split[0]);
        }
        if (2 == split.length) {
            return new Locale(split[0], split[1]);
        }
        return new Locale(split[0], split[1], split[2]);
    }

    public static boolean isBatteryPresent(Intent intent) {
        return intent.getBooleanExtra("present", true);
    }

    public static String getBatteryPercentage(Intent intent) {
        return com.android.settingslib.Utils.formatPercentage(com.android.settingslib.Utils.getBatteryLevel(intent));
    }

    public static Dialog buildGlobalChangeWarningDialog(Context context, int i, final Runnable runnable) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(i);
        builder.setMessage(C0017R$string.locale_not_translated);
        builder.setPositiveButton(17039370, new DialogInterface.OnClickListener() {
            /* class com.android.settings.Utils.AnonymousClass1 */

            public void onClick(DialogInterface dialogInterface, int i) {
                runnable.run();
            }
        });
        builder.setNegativeButton(17039360, (DialogInterface.OnClickListener) null);
        return builder.create();
    }

    public static void forcePrepareCustomPreferencesList(ViewGroup viewGroup, View view, ListView listView, boolean z) {
        listView.setScrollBarStyle(33554432);
        listView.setClipToPadding(false);
        prepareCustomPreferencesList(viewGroup, view, listView, z);
    }

    public static void prepareCustomPreferencesList(ViewGroup viewGroup, View view, View view2, boolean z) {
        if (view2.getScrollBarStyle() == 33554432) {
            int dimensionPixelSize = view2.getResources().getDimensionPixelSize(17105405);
            if (viewGroup instanceof PreferenceFrameLayout) {
                view.getLayoutParams().removeBorders = true;
            }
            view2.setPaddingRelative(0, 0, 0, dimensionPixelSize);
        }
    }

    public static void forceCustomPadding(View view, boolean z) {
        view.setPaddingRelative(z ? view.getPaddingStart() : 0, 0, z ? view.getPaddingEnd() : 0, view.getResources().getDimensionPixelSize(17105405));
    }

    public static String getMeProfileName(Context context, boolean z) {
        if (z) {
            return getProfileDisplayName(context);
        }
        return getShorterNameIfPossible(context);
    }

    private static String getShorterNameIfPossible(Context context) {
        String localProfileGivenName = getLocalProfileGivenName(context);
        return !TextUtils.isEmpty(localProfileGivenName) ? localProfileGivenName : getProfileDisplayName(context);
    }

    private static String getLocalProfileGivenName(Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        Cursor query = contentResolver.query(ContactsContract.Profile.CONTENT_RAW_CONTACTS_URI, new String[]{"_id"}, "account_type IS NULL AND account_name IS NULL", null, null);
        if (query == null) {
            return null;
        }
        try {
            if (!query.moveToFirst()) {
                return null;
            }
            long j = query.getLong(0);
            query.close();
            Cursor query2 = contentResolver.query(ContactsContract.Profile.CONTENT_URI.buildUpon().appendPath("data").build(), new String[]{"data2", "data3"}, "raw_contact_id=" + j, null, null);
            if (query2 == null) {
                return null;
            }
            try {
                if (!query2.moveToFirst()) {
                    return null;
                }
                String string = query2.getString(0);
                if (TextUtils.isEmpty(string)) {
                    string = query2.getString(1);
                }
                query2.close();
                return string;
            } finally {
                query2.close();
            }
        } finally {
            query.close();
        }
    }

    private static final String getProfileDisplayName(Context context) {
        Cursor query = context.getContentResolver().query(ContactsContract.Profile.CONTENT_URI, new String[]{"display_name"}, null, null, null);
        if (query == null) {
            return null;
        }
        try {
            if (!query.moveToFirst()) {
                return null;
            }
            String string = query.getString(0);
            query.close();
            return string;
        } finally {
            query.close();
        }
    }

    public static boolean hasMultipleUsers(Context context) {
        return ((UserManager) context.getSystemService(UserManager.class)).getUsers().size() > 1;
    }

    public static UserHandle getManagedProfile(UserManager userManager) {
        for (UserHandle userHandle : userManager.getUserProfiles()) {
            if (userHandle.getIdentifier() != userManager.getUserHandle()) {
                UserInfo userInfo = userManager.getUserInfo(userHandle.getIdentifier());
                if (userInfo.isManagedProfile() && userInfo.id != 999) {
                    return userHandle;
                }
            }
        }
        return null;
    }

    public static UserHandle getManagedProfileWithDisabled(UserManager userManager) {
        int myUserId = UserHandle.myUserId();
        List profiles = userManager.getProfiles(myUserId);
        int size = profiles.size();
        for (int i = 0; i < size; i++) {
            UserInfo userInfo = (UserInfo) profiles.get(i);
            if (userInfo.isManagedProfile() && userInfo.getUserHandle().getIdentifier() != myUserId) {
                return userInfo.getUserHandle();
            }
        }
        return null;
    }

    public static int getManagedProfileId(UserManager userManager, int i) {
        int[] profileIdsWithDisabled = userManager.getProfileIdsWithDisabled(i);
        for (int i2 : profileIdsWithDisabled) {
            if (i2 != i) {
                return i2;
            }
        }
        return -10000;
    }

    public static UserHandle getSecureTargetUser(IBinder iBinder, UserManager userManager, Bundle bundle, Bundle bundle2) {
        UserHandle userHandle = new UserHandle(UserHandle.myUserId());
        IActivityManager service = ActivityManager.getService();
        try {
            boolean equals = OPMemberController.PACKAGE_NAME.equals(service.getLaunchedFromPackage(iBinder));
            UserHandle userHandle2 = new UserHandle(UserHandle.getUserId(service.getLaunchedFromUid(iBinder)));
            if (!userHandle2.equals(userHandle) && isProfileOf(userManager, userHandle2)) {
                return userHandle2;
            }
            UserHandle userHandleFromBundle = getUserHandleFromBundle(bundle2);
            if (userHandleFromBundle != null && !userHandleFromBundle.equals(userHandle) && equals && isProfileOf(userManager, userHandleFromBundle)) {
                return userHandleFromBundle;
            }
            UserHandle userHandleFromBundle2 = getUserHandleFromBundle(bundle);
            if (userHandleFromBundle2 == null || userHandleFromBundle2.equals(userHandle) || !equals || !isProfileOf(userManager, userHandleFromBundle2)) {
                return userHandle;
            }
            return userHandleFromBundle2;
        } catch (RemoteException e) {
            Log.v("Settings", "Could not talk to activity manager.", e);
        }
    }

    private static UserHandle getUserHandleFromBundle(Bundle bundle) {
        if (bundle == null) {
            return null;
        }
        UserHandle userHandle = (UserHandle) bundle.getParcelable("android.intent.extra.USER");
        if (userHandle != null) {
            return userHandle;
        }
        int i = bundle.getInt("android.intent.extra.USER_ID", -1);
        if (i != -1) {
            return UserHandle.of(i);
        }
        return null;
    }

    private static boolean isProfileOf(UserManager userManager, UserHandle userHandle) {
        if (userManager == null || userHandle == null) {
            return false;
        }
        if (UserHandle.myUserId() == userHandle.getIdentifier() || userManager.getUserProfiles().contains(userHandle)) {
            return true;
        }
        return false;
    }

    public static UserInfo getExistingUser(UserManager userManager, UserHandle userHandle) {
        List<UserInfo> users = userManager.getUsers(true);
        int identifier = userHandle.getIdentifier();
        for (UserInfo userInfo : users) {
            if (userInfo.id == identifier) {
                return userInfo;
            }
        }
        return null;
    }

    public static View inflateCategoryHeader(LayoutInflater layoutInflater, ViewGroup viewGroup) {
        TypedArray obtainStyledAttributes = layoutInflater.getContext().obtainStyledAttributes(null, R.styleable.Preference, 16842892, 0);
        int resourceId = obtainStyledAttributes.getResourceId(3, 0);
        obtainStyledAttributes.recycle();
        return layoutInflater.inflate(resourceId, viewGroup, false);
    }

    public static ArraySet<String> getHandledDomains(PackageManager packageManager, String str) {
        List<IntentFilterVerificationInfo> intentFilterVerifications = packageManager.getIntentFilterVerifications(str);
        List<IntentFilter> allIntentFilters = packageManager.getAllIntentFilters(str);
        ArraySet<String> arraySet = new ArraySet<>();
        if (intentFilterVerifications != null && intentFilterVerifications.size() > 0) {
            for (IntentFilterVerificationInfo intentFilterVerificationInfo : intentFilterVerifications) {
                for (String str2 : intentFilterVerificationInfo.getDomains()) {
                    arraySet.add(str2);
                }
            }
        }
        if (allIntentFilters != null && allIntentFilters.size() > 0) {
            for (IntentFilter intentFilter : allIntentFilters) {
                if (intentFilter.hasCategory("android.intent.category.BROWSABLE") && (intentFilter.hasDataScheme("http") || intentFilter.hasDataScheme("https"))) {
                    arraySet.addAll(intentFilter.getHostsList());
                }
            }
        }
        return arraySet;
    }

    public static ApplicationInfo getAdminApplicationInfo(Context context, int i) {
        ComponentName profileOwnerAsUser = ((DevicePolicyManager) context.getSystemService("device_policy")).getProfileOwnerAsUser(i);
        if (profileOwnerAsUser == null) {
            return null;
        }
        String packageName = profileOwnerAsUser.getPackageName();
        try {
            return AppGlobals.getPackageManager().getApplicationInfo(packageName, 0, i);
        } catch (RemoteException e) {
            Log.e("Settings", "Error while retrieving application info for package " + packageName + ", userId " + i, e);
            return null;
        }
    }

    public static boolean isBandwidthControlEnabled() {
        try {
            return INetworkManagementService.Stub.asInterface(ServiceManager.getService("network_management")).isBandwidthControlEnabled();
        } catch (RemoteException unused) {
            return false;
        }
    }

    public static SpannableString createAccessibleSequence(CharSequence charSequence, String str) {
        SpannableString spannableString = new SpannableString(charSequence);
        spannableString.setSpan(new TtsSpan.TextBuilder(str).build(), 0, charSequence.length(), 18);
        return spannableString;
    }

    public static int getUserIdFromBundle(Context context, Bundle bundle) {
        return getUserIdFromBundle(context, bundle, false);
    }

    public static int getUserIdFromBundle(Context context, Bundle bundle, boolean z) {
        if (bundle == null) {
            return getCredentialOwnerUserId(context);
        }
        boolean z2 = false;
        if (z && bundle.getBoolean("allow_any_user", false)) {
            z2 = true;
        }
        int i = bundle.getInt("android.intent.extra.USER_ID", UserHandle.myUserId());
        if (i == -9999) {
            if (!z2) {
                enforceSystemUser(context, i);
            }
            return i;
        }
        if (!z2) {
            enforceSameOwner(context, i);
        }
        return i;
    }

    public static int enforceSystemUser(Context context, int i) {
        if (UserHandle.myUserId() == 0) {
            return i;
        }
        throw new SecurityException("Given user id " + i + " must only be used from USER_SYSTEM, but current user is " + UserHandle.myUserId());
    }

    public static int enforceSameOwner(Context context, int i) {
        if (ArrayUtils.contains(((UserManager) context.getSystemService(UserManager.class)).getProfileIdsWithDisabled(UserHandle.myUserId()), i)) {
            return i;
        }
        throw new SecurityException("Given user id " + i + " does not belong to user " + UserHandle.myUserId());
    }

    public static int getCredentialOwnerUserId(Context context) {
        return getCredentialOwnerUserId(context, UserHandle.myUserId());
    }

    public static int getCredentialOwnerUserId(Context context, int i) {
        return ((UserManager) context.getSystemService(UserManager.class)).getCredentialOwnerProfile(i);
    }

    public static int getCredentialType(Context context, int i) {
        return new LockPatternUtils(context).getCredentialTypeForUser(i);
    }

    public static String formatDateRange(Context context, long j, long j2) {
        String formatter;
        StringBuilder sb = sBuilder;
        synchronized (sb) {
            sb.setLength(0);
            formatter = DateUtils.formatDateRange(context, sFormatter, j, j2, 65552, null).toString();
        }
        return formatter;
    }

    public static boolean startQuietModeDialogIfNecessary(Context context, UserManager userManager, int i) {
        if (!userManager.isQuietModeEnabled(UserHandle.of(i))) {
            return false;
        }
        context.startActivity(UnlaunchableAppActivity.createInQuietModeDialogIntent(i));
        return true;
    }

    public static boolean unlockWorkProfileIfNecessary(Context context, int i) {
        try {
            if (ActivityManager.getService().isUserRunning(i, 2) && new LockPatternUtils(context).isSecure(i)) {
                return confirmWorkProfileCredentials(context, i);
            }
            return false;
        } catch (RemoteException unused) {
            return false;
        }
    }

    private static boolean confirmWorkProfileCredentials(Context context, int i) {
        Intent createConfirmDeviceCredentialIntent = ((KeyguardManager) context.getSystemService("keyguard")).createConfirmDeviceCredentialIntent(null, null, i);
        if (createConfirmDeviceCredentialIntent == null) {
            return false;
        }
        context.startActivity(createConfirmDeviceCredentialIntent);
        return true;
    }

    public static CharSequence getApplicationLabel(Context context, String str) {
        try {
            return context.getPackageManager().getApplicationInfo(str, 4194816).loadLabel(context.getPackageManager());
        } catch (PackageManager.NameNotFoundException unused) {
            Log.e("Settings", "Unable to find info for package: " + str);
            return null;
        }
    }

    public static boolean isPackageDirectBootAware(Context context, String str) {
        try {
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(str, 0);
            if (applicationInfo.isDirectBootAware() || applicationInfo.isPartiallyDirectBootAware()) {
                return true;
            }
            return false;
        } catch (PackageManager.NameNotFoundException unused) {
            return false;
        }
    }

    public static Context createPackageContextAsUser(Context context, int i) {
        try {
            return context.createPackageContextAsUser(context.getPackageName(), 0, UserHandle.of(i));
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("Settings", "Failed to create user context", e);
            return null;
        }
    }

    public static FingerprintManager getFingerprintManagerOrNull(Context context) {
        if (context.getPackageManager().hasSystemFeature("android.hardware.fingerprint")) {
            return (FingerprintManager) context.getSystemService("fingerprint");
        }
        return null;
    }

    public static boolean hasFingerprintHardware(Context context) {
        FingerprintManager fingerprintManagerOrNull = getFingerprintManagerOrNull(context);
        return fingerprintManagerOrNull != null && fingerprintManagerOrNull.isHardwareDetected();
    }

    public static FaceManager getFaceManagerOrNull(Context context) {
        if (context.getPackageManager().hasSystemFeature("android.hardware.biometrics.face")) {
            return (FaceManager) context.getSystemService("face");
        }
        return null;
    }

    public static void launchIntent(Fragment fragment, Intent intent) {
        try {
            int intExtra = intent.getIntExtra("android.intent.extra.USER_ID", -1);
            if (intExtra == -1) {
                fragment.startActivity(intent);
            } else {
                fragment.getActivity().startActivityAsUser(intent, new UserHandle(intExtra));
            }
        } catch (ActivityNotFoundException unused) {
            Log.w("Settings", "No activity found for " + intent);
        }
    }

    public static boolean isDemoUser(Context context) {
        return UserManager.isDeviceInDemoMode(context) && ((UserManager) context.getSystemService(UserManager.class)).isDemoUser();
    }

    public static ComponentName getDeviceOwnerComponent(Context context) {
        return ((DevicePolicyManager) context.getSystemService("device_policy")).getDeviceOwnerComponentOnAnyUser();
    }

    public static boolean isProfileOf(UserInfo userInfo, UserInfo userInfo2) {
        int i;
        return userInfo.id == userInfo2.id || ((i = userInfo.profileGroupId) != -10000 && i == userInfo2.profileGroupId);
    }

    public static boolean showSimCardTile(Context context) {
        boolean z = SystemProperties.getBoolean("persist.vendor.radio.primarycard", false);
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService("phone");
        if (!z || telephonyManager.getSimCount() <= 1) {
            return false;
        }
        return true;
    }

    public static VolumeInfo maybeInitializeVolume(StorageManager storageManager, Bundle bundle) {
        VolumeInfo findVolumeById = storageManager.findVolumeById(bundle.getString("android.os.storage.extra.VOLUME_ID", "private"));
        if (isVolumeValid(findVolumeById)) {
            return findVolumeById;
        }
        return null;
    }

    public static boolean isProfileOrDeviceOwner(UserManager userManager, DevicePolicyManager devicePolicyManager, String str) {
        List users = userManager.getUsers();
        if (devicePolicyManager.isDeviceOwnerAppOnAnyUser(str)) {
            return true;
        }
        int size = users.size();
        for (int i = 0; i < size; i++) {
            ComponentName profileOwnerAsUser = devicePolicyManager.getProfileOwnerAsUser(((UserInfo) users.get(i)).id);
            if (profileOwnerAsUser != null && profileOwnerAsUser.getPackageName().equals(str)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isProfileOrDeviceOwner(DevicePolicyManager devicePolicyManager, String str, int i) {
        if (devicePolicyManager.getDeviceOwnerUserId() == i && devicePolicyManager.isDeviceOwnerApp(str)) {
            return true;
        }
        ComponentName profileOwnerAsUser = devicePolicyManager.getProfileOwnerAsUser(i);
        if (profileOwnerAsUser == null || !profileOwnerAsUser.getPackageName().equals(str)) {
            return false;
        }
        return true;
    }

    private static boolean isVolumeValid(VolumeInfo volumeInfo) {
        if (volumeInfo == null || volumeInfo.getType() != 1 || !volumeInfo.isMountedReadable()) {
            return false;
        }
        return true;
    }

    public static void setEditTextCursorPosition(EditText editText) {
        editText.setSelection(editText.getText().length());
    }

    public static void setSafeIcon(Preference preference, Drawable drawable) {
        if (drawable != null && !(drawable instanceof VectorDrawable)) {
            drawable = getSafeDrawable(drawable, 500, 500);
        }
        preference.setIcon(drawable);
    }

    public static Drawable getSafeDrawable(Drawable drawable, int i, int i2) {
        Bitmap bitmap;
        int minimumWidth = drawable.getMinimumWidth();
        int minimumHeight = drawable.getMinimumHeight();
        if (minimumWidth <= i && minimumHeight <= i2) {
            return drawable;
        }
        float f = (float) minimumWidth;
        float f2 = (float) minimumHeight;
        float min = Math.min(((float) i) / f, ((float) i2) / f2);
        int i3 = (int) (f * min);
        int i4 = (int) (f2 * min);
        if (drawable instanceof BitmapDrawable) {
            bitmap = Bitmap.createScaledBitmap(((BitmapDrawable) drawable).getBitmap(), i3, i4, false);
        } else {
            bitmap = createBitmap(drawable, i3, i4);
        }
        return new BitmapDrawable((Resources) null, bitmap);
    }

    public static IconCompat createIconWithDrawable(Drawable drawable) {
        Bitmap bitmap;
        if (drawable instanceof BitmapDrawable) {
            bitmap = ((BitmapDrawable) drawable).getBitmap();
        } else {
            int intrinsicWidth = drawable.getIntrinsicWidth();
            int intrinsicHeight = drawable.getIntrinsicHeight();
            if (intrinsicWidth <= 0) {
                intrinsicWidth = 1;
            }
            if (intrinsicHeight <= 0) {
                intrinsicHeight = 1;
            }
            bitmap = createBitmap(drawable, intrinsicWidth, intrinsicHeight);
        }
        return IconCompat.createWithBitmap(bitmap);
    }

    public static Bitmap createBitmap(Drawable drawable, int i, int i2) {
        Bitmap createBitmap = Bitmap.createBitmap(i, i2, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return createBitmap;
    }

    public static Drawable getBadgedIcon(IconDrawableFactory iconDrawableFactory, PackageManager packageManager, String str, int i) {
        try {
            return iconDrawableFactory.getBadgedIcon(packageManager.getApplicationInfoAsUser(str, 128, i), i);
        } catch (PackageManager.NameNotFoundException unused) {
            return packageManager.getDefaultActivityIcon();
        }
    }

    public static boolean isPackageEnabled(Context context, String str) {
        try {
            return context.getPackageManager().getApplicationInfo(str, 0).enabled;
        } catch (Exception e) {
            Log.e("Settings", "Error while retrieving application info for package " + str, e);
            return false;
        }
    }

    public static String getLocalizedName(Context context, String str) {
        int identifier;
        String str2 = null;
        if (context == null || str == null || str.isEmpty() || (identifier = context.getResources().getIdentifier(str, "string", context.getPackageName())) <= 0) {
            return null;
        }
        try {
            str2 = context.getResources().getString(identifier);
            Log.d("Settings", "Replaced apn name with localized name");
            return str2;
        } catch (Resources.NotFoundException e) {
            Log.e("Settings", "Got execption while getting the localized apn name.", e);
            return str2;
        }
    }

    public static boolean carrierTableFieldValidate(String str) {
        if (str == null) {
            return false;
        }
        if ("authtype".equalsIgnoreCase(str) || "sub_id".equalsIgnoreCase(str)) {
            return true;
        }
        String upperCase = str.toUpperCase();
        try {
            Telephony.Carriers.class.getDeclaredField(upperCase);
            return true;
        } catch (NoSuchFieldException unused) {
            Log.w("Settings", upperCase + "is not a valid field in class Telephony.Carriers");
            return false;
        }
    }

    public static boolean isSimSettingsApkAvailable() {
        IExtTelephony asInterface = IExtTelephony.Stub.asInterface(ServiceManager.getService("qti.radio.extphone"));
        if (asInterface == null) {
            return false;
        }
        try {
            if (asInterface.isVendorApkAvailable("com.qualcomm.qti.simsettings")) {
                return true;
            }
            return false;
        } catch (RemoteException e) {
            Log.e("Settings", "Got exception in isSimSettingsApkAvailable.", e);
            return false;
        }
    }

    public static boolean isAdvancedPlmnScanSupported() {
        try {
            return IExtTelephony.Stub.asInterface(ServiceManager.getService("qti.radio.extphone")).getPropertyValueBool("persist.vendor.radio.enableadvancedscan", true);
        } catch (RemoteException | NullPointerException e) {
            Log.e("Settings", "isAdvancedPlmnScanSupported Exception: ", e);
            return false;
        }
    }

    public static boolean isSupportCTPA(Context context) {
        return context.getApplicationContext().getResources().getBoolean(C0005R$bool.config_support_CT_PA);
    }

    public static String getString(Context context, String str) {
        return Settings.Global.getString(context.getContentResolver(), str);
    }

    public static boolean isSystemAlertWindowEnabled(Context context) {
        return !((ActivityManager) context.getSystemService("activity")).isLowRamDevice() || Build.VERSION.SDK_INT < 29;
    }

    public static void setActionBarShadowAnimation(Activity activity, Lifecycle lifecycle, View view) {
        if (activity == null) {
            Log.w("Settings", "No activity, cannot style actionbar.");
            return;
        }
        ActionBar actionBar = activity.getActionBar();
        if (actionBar == null) {
            Log.w("Settings", "No actionbar, cannot style actionbar.");
            return;
        }
        actionBar.setElevation(0.0f);
        if (lifecycle != null && view != null) {
            ActionBarShadowController.attachToView(activity, lifecycle, view);
        }
    }

    public static Fragment getTargetFragment(Activity activity, String str, Bundle bundle) {
        boolean z = false;
        boolean z2 = bundle != null && bundle.getInt("profile") == 1;
        if (bundle != null && bundle.getInt("profile") == 2) {
            z = true;
        }
        if (OPUtils.hasMultiAppProfiles(UserManager.get(activity)) && ((UserManager) activity.getSystemService(UserManager.class)).getUserProfiles().size() > 2 && ProfileFragmentBridge.FRAGMENT_MAP.get(str) != null && !z && !z2) {
            return Fragment.instantiate(activity, ProfileFragmentBridge.FRAGMENT_MAP.get(str), bundle);
        }
        if (OPUtils.hasMultiAppProfiles(UserManager.get(activity)) || ((UserManager) activity.getSystemService(UserManager.class)).getUserProfiles().size() <= 1 || ProfileFragmentBridge.FRAGMENT_MAP.get(str) == null || z || z2) {
            return Fragment.instantiate(activity, str, bundle);
        }
        return Fragment.instantiate(activity, ProfileFragmentBridge.FRAGMENT_MAP.get(str), bundle);
    }

    public static boolean isSettingsIntelligence(Context context) {
        return TextUtils.equals(context.getPackageManager().getPackagesForUid(Binder.getCallingUid())[0], context.getString(C0017R$string.config_settingsintelligence_package_name));
    }

    public static boolean isNightMode(Context context) {
        return (context.getResources().getConfiguration().uiMode & 48) == 32;
    }
}
