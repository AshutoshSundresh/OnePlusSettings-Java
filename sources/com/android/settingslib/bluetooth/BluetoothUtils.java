package com.android.settingslib.bluetooth;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Pair;
import com.android.settingslib.R$array;
import com.android.settingslib.R$dimen;
import com.android.settingslib.R$drawable;
import com.android.settingslib.R$string;
import com.android.settingslib.widget.AdaptiveIcon;
import com.android.settingslib.widget.AdaptiveOutlineDrawable;
import java.io.IOException;

public class BluetoothUtils {
    private static ErrorListener sErrorListener;

    public interface ErrorListener {
        void onShowError(Context context, String str, int i);
    }

    public static int getConnectionStateSummary(int i) {
        if (i == 0) {
            return R$string.bluetooth_disconnected;
        }
        if (i == 1) {
            return R$string.bluetooth_connecting;
        }
        if (i == 2) {
            return R$string.bluetooth_connected;
        }
        if (i != 3) {
            return 0;
        }
        return R$string.bluetooth_disconnecting;
    }

    static void showError(Context context, String str, int i) {
        ErrorListener errorListener = sErrorListener;
        if (errorListener != null) {
            errorListener.onShowError(context, str, i);
        }
    }

    public static void setErrorListener(ErrorListener errorListener) {
        sErrorListener = errorListener;
    }

    public static Pair<Drawable, String> getBtClassDrawableWithDescription(Context context, CachedBluetoothDevice cachedBluetoothDevice) {
        BluetoothClass btClass = cachedBluetoothDevice.getBtClass();
        if (btClass != null) {
            int majorDeviceClass = btClass.getMajorDeviceClass();
            if (majorDeviceClass == 256) {
                return new Pair<>(getBluetoothDrawable(context, R$drawable.ic_bt_laptop), context.getString(R$string.bluetooth_talkback_computer));
            }
            if (majorDeviceClass == 512) {
                return new Pair<>(getBluetoothDrawable(context, R$drawable.ic_bt_cellphone), context.getString(R$string.bluetooth_talkback_phone));
            }
            if (majorDeviceClass == 1280) {
                return new Pair<>(getBluetoothDrawable(context, HidProfile.getHidClassDrawable(btClass)), context.getString(R$string.bluetooth_talkback_input_peripheral));
            }
            if (majorDeviceClass == 1536) {
                return new Pair<>(getBluetoothDrawable(context, 17302822), context.getString(R$string.bluetooth_talkback_imaging));
            }
        }
        for (LocalBluetoothProfile localBluetoothProfile : cachedBluetoothDevice.getProfiles()) {
            int drawableResource = localBluetoothProfile.getDrawableResource(btClass);
            if (drawableResource != 0) {
                return new Pair<>(getBluetoothDrawable(context, drawableResource), null);
            }
        }
        if (btClass != null) {
            if (btClass.doesClassMatch(0)) {
                return new Pair<>(getBluetoothDrawable(context, R$drawable.ic_bt_headset_hfp), context.getString(R$string.bluetooth_talkback_headset));
            }
            if (btClass.doesClassMatch(1)) {
                return new Pair<>(getBluetoothDrawable(context, R$drawable.ic_bt_headphones_a2dp), context.getString(R$string.bluetooth_talkback_headphone));
            }
        }
        return new Pair<>(getBluetoothDrawable(context, R$drawable.ic_settings_bluetooth).mutate(), context.getString(R$string.bluetooth_talkback_bluetooth));
    }

    public static Drawable getBluetoothDrawable(Context context, int i) {
        return context.getDrawable(i);
    }

    public static Pair<Drawable, String> getBtRainbowDrawableWithDescription(Context context, CachedBluetoothDevice cachedBluetoothDevice) {
        Resources resources = context.getResources();
        Pair<Drawable, String> btDrawableWithDescription = getBtDrawableWithDescription(context, cachedBluetoothDevice);
        if (btDrawableWithDescription.first instanceof BitmapDrawable) {
            return new Pair<>(new AdaptiveOutlineDrawable(resources, ((BitmapDrawable) btDrawableWithDescription.first).getBitmap()), (String) btDrawableWithDescription.second);
        }
        return new Pair<>(buildBtRainbowDrawable(context, (Drawable) btDrawableWithDescription.first, cachedBluetoothDevice.getAddress().hashCode()), (String) btDrawableWithDescription.second);
    }

    public static Drawable buildBtRainbowDrawable(Context context, Drawable drawable, int i) {
        Resources resources = context.getResources();
        int[] intArray = resources.getIntArray(R$array.bt_icon_fg_colors);
        int[] intArray2 = resources.getIntArray(R$array.bt_icon_bg_colors);
        int abs = Math.abs(i % intArray2.length);
        drawable.setTint(intArray[abs]);
        AdaptiveIcon adaptiveIcon = new AdaptiveIcon(context, drawable);
        adaptiveIcon.setBackgroundColor(intArray2[abs]);
        return adaptiveIcon;
    }

    public static Pair<Drawable, String> getBtDrawableWithDescription(Context context, CachedBluetoothDevice cachedBluetoothDevice) {
        Uri uriMetaData;
        Pair<Drawable, String> btClassDrawableWithDescription = getBtClassDrawableWithDescription(context, cachedBluetoothDevice);
        BluetoothDevice device = cachedBluetoothDevice.getDevice();
        boolean booleanMetaData = getBooleanMetaData(device, 6);
        int dimensionPixelSize = context.getResources().getDimensionPixelSize(R$dimen.bt_nearby_icon_size);
        Resources resources = context.getResources();
        if (booleanMetaData && (uriMetaData = getUriMetaData(device, 5)) != null) {
            try {
                context.getContentResolver().takePersistableUriPermission(uriMetaData, 1);
            } catch (SecurityException e) {
                Log.e("BluetoothUtils", "Failed to take persistable permission for: " + uriMetaData, e);
            }
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uriMetaData);
                if (bitmap != null) {
                    Bitmap createScaledBitmap = Bitmap.createScaledBitmap(bitmap, dimensionPixelSize, dimensionPixelSize, false);
                    bitmap.recycle();
                    return new Pair<>(new BitmapDrawable(resources, createScaledBitmap), (String) btClassDrawableWithDescription.second);
                }
            } catch (IOException e2) {
                Log.e("BluetoothUtils", "Failed to get drawable for: " + uriMetaData, e2);
            } catch (SecurityException e3) {
                Log.e("BluetoothUtils", "Failed to get permission for: " + uriMetaData, e3);
            }
        }
        return new Pair<>((Drawable) btClassDrawableWithDescription.first, (String) btClassDrawableWithDescription.second);
    }

    public static Drawable buildAdvancedDrawable(Context context, Drawable drawable) {
        Bitmap bitmap;
        int dimensionPixelSize = context.getResources().getDimensionPixelSize(R$dimen.advanced_icon_size);
        Resources resources = context.getResources();
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
        if (bitmap == null) {
            return drawable;
        }
        Bitmap createScaledBitmap = Bitmap.createScaledBitmap(bitmap, dimensionPixelSize, dimensionPixelSize, false);
        bitmap.recycle();
        return new AdaptiveOutlineDrawable(resources, createScaledBitmap, 1);
    }

    public static Bitmap createBitmap(Drawable drawable, int i, int i2) {
        Bitmap createBitmap = Bitmap.createBitmap(i, i2, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return createBitmap;
    }

    public static boolean getBooleanMetaData(BluetoothDevice bluetoothDevice, int i) {
        byte[] metadata;
        if (bluetoothDevice == null || (metadata = bluetoothDevice.getMetadata(i)) == null) {
            return false;
        }
        return Boolean.parseBoolean(new String(metadata));
    }

    public static String getStringMetaData(BluetoothDevice bluetoothDevice, int i) {
        byte[] metadata;
        if (bluetoothDevice == null || (metadata = bluetoothDevice.getMetadata(i)) == null) {
            return null;
        }
        return new String(metadata);
    }

    public static int getIntMetaData(BluetoothDevice bluetoothDevice, int i) {
        byte[] metadata;
        if (bluetoothDevice == null || (metadata = bluetoothDevice.getMetadata(i)) == null) {
            return -1;
        }
        try {
            return Integer.parseInt(new String(metadata));
        } catch (NumberFormatException unused) {
            return -1;
        }
    }

    public static Uri getUriMetaData(BluetoothDevice bluetoothDevice, int i) {
        String stringMetaData = getStringMetaData(bluetoothDevice, i);
        if (stringMetaData == null) {
            return null;
        }
        return Uri.parse(stringMetaData);
    }
}
