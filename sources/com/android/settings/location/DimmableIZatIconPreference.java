package com.android.settings.location;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.constraintlayout.widget.R$styleable;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.C0017R$string;
import com.android.settings.widget.RestrictedAppPreference;
import com.android.settingslib.location.InjectedSetting;
import com.android.settingslib.widget.apppreference.AppPreference;
import dalvik.system.DexClassLoader;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class DimmableIZatIconPreference {
    private static Method mGetConsentMethod;
    private static Method mGetXtProxyMethod;
    private static String mIzatPackage;
    private static DexClassLoader mLoader;
    private static Class mNotifierClz;

    private static boolean isIzatPackage(Context context, InjectedSetting injectedSetting) {
        String str = mIzatPackage;
        return str != null && str.equals(injectedSetting.packageName);
    }

    /* access modifiers changed from: private */
    public static void dimIcon(AppPreference appPreference, boolean z) {
        Drawable icon = appPreference.getIcon();
        if (icon != null) {
            icon.mutate().setAlpha(z ? R$styleable.Constraint_layout_goneMarginStart : 255);
            appPreference.setIcon(icon);
        }
    }

    private static class IZatAppPreference extends AppPreference {
        private boolean mChecked;
        private Context mContext;

        private IZatAppPreference(Context context) {
            super(context);
            this.mContext = context;
            Object newProxyInstance = Proxy.newProxyInstance(DimmableIZatIconPreference.mLoader, new Class[]{DimmableIZatIconPreference.mNotifierClz}, new InvocationHandler() {
                /* class com.android.settings.location.DimmableIZatIconPreference.IZatAppPreference.AnonymousClass1 */

                @Override // java.lang.reflect.InvocationHandler
                public Object invoke(Object obj, Method method, Object[] objArr) throws Throwable {
                    boolean booleanValue;
                    if (!method.getName().equals("userConsentNotify")) {
                        return null;
                    }
                    boolean z = false;
                    if (objArr[0] == null || !(objArr[0] instanceof Boolean) || IZatAppPreference.this.mChecked == (booleanValue = ((Boolean) objArr[0]).booleanValue())) {
                        return null;
                    }
                    IZatAppPreference.this.mChecked = booleanValue;
                    IZatAppPreference iZatAppPreference = IZatAppPreference.this;
                    if (!iZatAppPreference.isEnabled() || !IZatAppPreference.this.mChecked) {
                        z = true;
                    }
                    DimmableIZatIconPreference.dimIcon(iZatAppPreference, z);
                    return null;
                }
            });
            try {
                this.mChecked = ((Boolean) DimmableIZatIconPreference.mGetConsentMethod.invoke(DimmableIZatIconPreference.mGetXtProxyMethod.invoke(null, context, newProxyInstance), new Object[0])).booleanValue();
            } catch (ExceptionInInitializerError | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        @Override // androidx.preference.Preference
        public CharSequence getSummary() {
            int i;
            if (!isEnabled() || !this.mChecked) {
                i = C0017R$string.notification_toggle_off;
            } else {
                i = C0017R$string.notification_toggle_on;
            }
            return this.mContext.getString(i);
        }

        @Override // com.android.settingslib.widget.apppreference.AppPreference, androidx.preference.Preference
        public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
            super.onBindViewHolder(preferenceViewHolder);
            DimmableIZatIconPreference.dimIcon(this, !isEnabled() || !this.mChecked);
        }
    }

    private static class IZatRestrictedAppPreference extends RestrictedAppPreference {
        private boolean mChecked;

        private IZatRestrictedAppPreference(Context context, String str) {
            super(context, str);
            Object newProxyInstance = Proxy.newProxyInstance(DimmableIZatIconPreference.mLoader, new Class[]{DimmableIZatIconPreference.mNotifierClz}, new InvocationHandler() {
                /* class com.android.settings.location.DimmableIZatIconPreference.IZatRestrictedAppPreference.AnonymousClass1 */

                @Override // java.lang.reflect.InvocationHandler
                public Object invoke(Object obj, Method method, Object[] objArr) throws Throwable {
                    boolean booleanValue;
                    if (!method.getName().equals("userConsentNotify")) {
                        return null;
                    }
                    boolean z = false;
                    if (objArr[0] == null || !(objArr[0] instanceof Boolean) || IZatRestrictedAppPreference.this.mChecked == (booleanValue = ((Boolean) objArr[0]).booleanValue())) {
                        return null;
                    }
                    IZatRestrictedAppPreference.this.mChecked = booleanValue;
                    IZatRestrictedAppPreference iZatRestrictedAppPreference = IZatRestrictedAppPreference.this;
                    if (!iZatRestrictedAppPreference.isEnabled() || !IZatRestrictedAppPreference.this.mChecked) {
                        z = true;
                    }
                    DimmableIZatIconPreference.dimIcon(iZatRestrictedAppPreference, z);
                    return null;
                }
            });
            try {
                this.mChecked = ((Boolean) DimmableIZatIconPreference.mGetConsentMethod.invoke(DimmableIZatIconPreference.mGetXtProxyMethod.invoke(null, context, newProxyInstance), new Object[0])).booleanValue();
            } catch (ExceptionInInitializerError | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        @Override // com.android.settingslib.widget.apppreference.AppPreference, com.android.settings.widget.RestrictedAppPreference, androidx.preference.Preference
        public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
            super.onBindViewHolder(preferenceViewHolder);
            DimmableIZatIconPreference.dimIcon(this, !isEnabled() || !this.mChecked);
        }
    }

    static AppPreference getAppPreference(Context context, InjectedSetting injectedSetting) {
        if (isIzatPackage(context, injectedSetting)) {
            return new IZatAppPreference(context);
        }
        return new AppPreference(context);
    }

    static RestrictedAppPreference getRestrictedAppPreference(Context context, InjectedSetting injectedSetting) {
        if (isIzatPackage(context, injectedSetting)) {
            return new IZatRestrictedAppPreference(context, injectedSetting.userRestriction);
        }
        return new RestrictedAppPreference(context, injectedSetting.userRestriction);
    }
}
