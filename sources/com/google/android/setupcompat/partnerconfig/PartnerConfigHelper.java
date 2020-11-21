package com.google.android.setupcompat.partnerconfig;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import com.google.android.setupcompat.partnerconfig.PartnerConfig;
import java.util.EnumMap;

public class PartnerConfigHelper {
    public static final String KEY_FALLBACK_CONFIG = "fallbackConfig";
    public static final String SUW_AUTHORITY = "com.google.android.setupwizard.partner";
    public static final String SUW_GET_PARTNER_CONFIG_METHOD = "getOverlayConfig";
    private static final String TAG = "PartnerConfigHelper";
    private static PartnerConfigHelper instance;
    final EnumMap<PartnerConfig, Object> partnerResourceCache = new EnumMap<>(PartnerConfig.class);
    Bundle resultBundle = null;

    public static synchronized PartnerConfigHelper get(Context context) {
        PartnerConfigHelper partnerConfigHelper;
        synchronized (PartnerConfigHelper.class) {
            if (instance == null) {
                instance = new PartnerConfigHelper(context);
            }
            partnerConfigHelper = instance;
        }
        return partnerConfigHelper;
    }

    private PartnerConfigHelper(Context context) {
        getPartnerConfigBundle(context);
    }

    public boolean isAvailable() {
        Bundle bundle = this.resultBundle;
        return bundle != null && !bundle.isEmpty();
    }

    public int getColor(Context context, PartnerConfig partnerConfig) {
        int i;
        if (partnerConfig.getResourceType() != PartnerConfig.ResourceType.COLOR) {
            throw new IllegalArgumentException("Not a color resource");
        } else if (this.partnerResourceCache.containsKey(partnerConfig)) {
            return ((Integer) this.partnerResourceCache.get(partnerConfig)).intValue();
        } else {
            try {
                ResourceEntry resourceEntryFromKey = getResourceEntryFromKey(context, partnerConfig.getResourceName());
                Resources resources = resourceEntryFromKey.getResources();
                int resourceId = resourceEntryFromKey.getResourceId();
                if (Build.VERSION.SDK_INT >= 23) {
                    i = resources.getColor(resourceId, null);
                } else {
                    i = resources.getColor(resourceId);
                }
                this.partnerResourceCache.put(partnerConfig, (Object) Integer.valueOf(i));
                return i;
            } catch (NullPointerException unused) {
                return 0;
            }
        }
    }

    public Drawable getDrawable(Context context, PartnerConfig partnerConfig) {
        Drawable drawable;
        if (partnerConfig.getResourceType() != PartnerConfig.ResourceType.DRAWABLE) {
            throw new IllegalArgumentException("Not a drawable resource");
        } else if (this.partnerResourceCache.containsKey(partnerConfig)) {
            return (Drawable) this.partnerResourceCache.get(partnerConfig);
        } else {
            try {
                ResourceEntry resourceEntryFromKey = getResourceEntryFromKey(context, partnerConfig.getResourceName());
                Resources resources = resourceEntryFromKey.getResources();
                int resourceId = resourceEntryFromKey.getResourceId();
                TypedValue typedValue = new TypedValue();
                resources.getValue(resourceId, typedValue, true);
                if (typedValue.type == 1 && typedValue.data == 0) {
                    return null;
                }
                if (Build.VERSION.SDK_INT >= 21) {
                    drawable = resources.getDrawable(resourceId, null);
                } else {
                    drawable = resources.getDrawable(resourceId);
                }
                this.partnerResourceCache.put(partnerConfig, (Object) drawable);
                return drawable;
            } catch (Resources.NotFoundException | NullPointerException unused) {
                return null;
            }
        }
    }

    public String getString(Context context, PartnerConfig partnerConfig) {
        if (partnerConfig.getResourceType() != PartnerConfig.ResourceType.STRING) {
            throw new IllegalArgumentException("Not a string resource");
        } else if (this.partnerResourceCache.containsKey(partnerConfig)) {
            return (String) this.partnerResourceCache.get(partnerConfig);
        } else {
            String str = null;
            try {
                ResourceEntry resourceEntryFromKey = getResourceEntryFromKey(context, partnerConfig.getResourceName());
                str = resourceEntryFromKey.getResources().getString(resourceEntryFromKey.getResourceId());
                this.partnerResourceCache.put(partnerConfig, (Object) str);
                return str;
            } catch (NullPointerException unused) {
                return str;
            }
        }
    }

    public boolean getBoolean(Context context, PartnerConfig partnerConfig, boolean z) {
        if (partnerConfig.getResourceType() != PartnerConfig.ResourceType.BOOL) {
            throw new IllegalArgumentException("Not a bool resource");
        } else if (this.partnerResourceCache.containsKey(partnerConfig)) {
            return ((Boolean) this.partnerResourceCache.get(partnerConfig)).booleanValue();
        } else {
            try {
                ResourceEntry resourceEntryFromKey = getResourceEntryFromKey(context, partnerConfig.getResourceName());
                z = resourceEntryFromKey.getResources().getBoolean(resourceEntryFromKey.getResourceId());
                this.partnerResourceCache.put(partnerConfig, (Object) Boolean.valueOf(z));
                return z;
            } catch (NullPointerException unused) {
                return z;
            }
        }
    }

    public float getDimension(Context context, PartnerConfig partnerConfig) {
        return getDimension(context, partnerConfig, 0.0f);
    }

    public float getDimension(Context context, PartnerConfig partnerConfig, float f) {
        if (partnerConfig.getResourceType() != PartnerConfig.ResourceType.DIMENSION) {
            throw new IllegalArgumentException("Not a dimension resource");
        } else if (this.partnerResourceCache.containsKey(partnerConfig)) {
            return getDimensionFromTypedValue(context, (TypedValue) this.partnerResourceCache.get(partnerConfig));
        } else {
            try {
                ResourceEntry resourceEntryFromKey = getResourceEntryFromKey(context, partnerConfig.getResourceName());
                Resources resources = resourceEntryFromKey.getResources();
                int resourceId = resourceEntryFromKey.getResourceId();
                f = resources.getDimension(resourceId);
                this.partnerResourceCache.put(partnerConfig, (Object) getTypedValueFromResource(resources, resourceId, 5));
                return getDimensionFromTypedValue(context, (TypedValue) this.partnerResourceCache.get(partnerConfig));
            } catch (NullPointerException unused) {
                return f;
            }
        }
    }

    public float getFraction(Context context, PartnerConfig partnerConfig) {
        return getFraction(context, partnerConfig, 0.0f);
    }

    public float getFraction(Context context, PartnerConfig partnerConfig, float f) {
        if (partnerConfig.getResourceType() != PartnerConfig.ResourceType.FRACTION) {
            throw new IllegalArgumentException("Not a fraction resource");
        } else if (this.partnerResourceCache.containsKey(partnerConfig)) {
            return ((Float) this.partnerResourceCache.get(partnerConfig)).floatValue();
        } else {
            try {
                ResourceEntry resourceEntryFromKey = getResourceEntryFromKey(context, partnerConfig.getResourceName());
                f = resourceEntryFromKey.getResources().getFraction(resourceEntryFromKey.getResourceId(), 1, 1);
                this.partnerResourceCache.put(partnerConfig, (Object) Float.valueOf(f));
                return f;
            } catch (NullPointerException unused) {
                return f;
            }
        }
    }

    private void getPartnerConfigBundle(Context context) {
        Bundle bundle = this.resultBundle;
        if (bundle == null || bundle.isEmpty()) {
            try {
                this.resultBundle = context.getContentResolver().call(new Uri.Builder().scheme("content").authority(SUW_AUTHORITY).appendPath(SUW_GET_PARTNER_CONFIG_METHOD).build(), SUW_GET_PARTNER_CONFIG_METHOD, (String) null, (Bundle) null);
                this.partnerResourceCache.clear();
            } catch (IllegalArgumentException | SecurityException unused) {
                Log.w(TAG, "Fail to get config from suw provider");
            }
        }
    }

    private ResourceEntry getResourceEntryFromKey(Context context, String str) {
        Bundle bundle = this.resultBundle.getBundle(str);
        Bundle bundle2 = this.resultBundle.getBundle(KEY_FALLBACK_CONFIG);
        if (bundle2 != null) {
            bundle.putBundle(KEY_FALLBACK_CONFIG, bundle2.getBundle(str));
        }
        return ResourceEntry.fromBundle(context, bundle);
    }

    public static synchronized void resetForTesting() {
        synchronized (PartnerConfigHelper.class) {
            instance = null;
        }
    }

    private TypedValue getTypedValueFromResource(Resources resources, int i, int i2) {
        TypedValue typedValue = new TypedValue();
        resources.getValue(i, typedValue, true);
        if (typedValue.type == i2) {
            return typedValue;
        }
        throw new Resources.NotFoundException("Resource ID #0x" + Integer.toHexString(i) + " type #0x" + Integer.toHexString(typedValue.type) + " is not valid");
    }

    private float getDimensionFromTypedValue(Context context, TypedValue typedValue) {
        return typedValue.getDimension(context.getResources().getDisplayMetrics());
    }
}
