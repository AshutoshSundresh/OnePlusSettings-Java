package androidx.preference;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import androidx.preference.PreferenceManager;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class RingtonePreference extends Preference implements PreferenceManager.OnActivityResultListener {
    private static Method getDefaultRingtoneUriBySubId;
    private int mRequestCode;
    private int mRingtoneType;
    private boolean mShowDefault;
    private boolean mShowSilent;
    private int mSubscriptionID;

    public RingtonePreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mSubscriptionID = 0;
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.RingtonePreference, i, i2);
        this.mRingtoneType = 1;
        this.mShowDefault = obtainStyledAttributes.getBoolean(R$styleable.RingtonePreference_android_showDefault, true);
        this.mShowSilent = obtainStyledAttributes.getBoolean(R$styleable.RingtonePreference_android_showSilent, true);
        obtainStyledAttributes.recycle();
    }

    public RingtonePreference(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public RingtonePreference(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R$attr.ringtonePreferenceStyle);
    }

    public int getRingtoneType() {
        return this.mRingtoneType;
    }

    public int getSubId() {
        return this.mSubscriptionID;
    }

    /* access modifiers changed from: protected */
    @Override // androidx.preference.Preference
    public void onClick() {
        Intent intent;
        if (Build.VERSION.SDK_INT >= 26) {
            intent = new Intent("oneplus.intent.action.RINGTONE_PICKER");
        } else {
            intent = new Intent("android.intent.action.oneplus.RINGTONE_PICKER");
        }
        onPrepareRingtonePickerIntent(intent);
        PreferenceFragment fragment = getPreferenceManager().getFragment();
        if (fragment != null) {
            fragment.startActivityForResult(intent, this.mRequestCode);
        } else {
            getPreferenceManager().getActivity().startActivityForResult(intent, this.mRequestCode);
        }
    }

    /* access modifiers changed from: protected */
    public void onPrepareRingtonePickerIntent(Intent intent) {
        intent.putExtra("android.intent.extra.ringtone.EXISTING_URI", onRestoreRingtone());
        intent.putExtra("android.intent.extra.ringtone.SHOW_DEFAULT", this.mShowDefault);
        if (this.mShowDefault) {
            if (getRingtoneType() == 1) {
                try {
                    if (getDefaultRingtoneUriBySubId == null) {
                        getDefaultRingtoneUriBySubId = RingtoneManager.class.getDeclaredMethod("getDefaultRingtoneUriBySubId", Integer.TYPE);
                    }
                    if (getDefaultRingtoneUriBySubId != null) {
                        intent.putExtra("android.intent.extra.ringtone.DEFAULT_URI", (Uri) getDefaultRingtoneUriBySubId.invoke(null, Integer.valueOf(getSubId())));
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e2) {
                    e2.printStackTrace();
                } catch (InvocationTargetException e3) {
                    e3.printStackTrace();
                } catch (NoSuchMethodException e4) {
                    e4.printStackTrace();
                }
            } else {
                intent.putExtra("android.intent.extra.ringtone.DEFAULT_URI", RingtoneManager.getDefaultUri(getRingtoneType()));
            }
        }
        intent.putExtra("android.intent.extra.ringtone.SHOW_SILENT", this.mShowSilent);
        intent.putExtra("android.intent.extra.ringtone.TYPE", this.mRingtoneType);
        intent.putExtra("android.intent.extra.ringtone.TITLE", getTitle());
        intent.putExtra("android.intent.extra.ringtone.AUDIO_ATTRIBUTES_FLAGS", 64);
    }

    /* access modifiers changed from: protected */
    public void onSaveRingtone(Uri uri) {
        persistString(uri != null ? uri.toString() : "");
    }

    /* access modifiers changed from: protected */
    public Uri onRestoreRingtone() {
        String persistedString = getPersistedString(null);
        if (!TextUtils.isEmpty(persistedString)) {
            return Uri.parse(persistedString);
        }
        return null;
    }

    /* access modifiers changed from: protected */
    @Override // androidx.preference.Preference
    public Object onGetDefaultValue(TypedArray typedArray, int i) {
        return typedArray.getString(i);
    }

    /* access modifiers changed from: protected */
    @Override // androidx.preference.Preference
    public void onSetInitialValue(boolean z, Object obj) {
        String str = (String) obj;
        if (!z && !TextUtils.isEmpty(str)) {
            onSaveRingtone(Uri.parse(str));
        }
    }

    /* access modifiers changed from: protected */
    @Override // androidx.preference.Preference
    public void onAttachedToHierarchy(PreferenceManager preferenceManager) {
        super.onAttachedToHierarchy(preferenceManager);
        preferenceManager.registerOnActivityResultListener(this);
        this.mRequestCode = preferenceManager.getNextRequestCode();
    }
}
