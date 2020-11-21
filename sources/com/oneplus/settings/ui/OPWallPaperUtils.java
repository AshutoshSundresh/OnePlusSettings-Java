package com.oneplus.settings.ui;

import android.app.WallpaperInfo;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Process;
import android.util.Log;
import com.android.settings.C0007R$dimen;
import com.oneplus.settings.utils.OPUtils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class OPWallPaperUtils {
    public static final boolean ATLEAST_OREO_MR1 = (Build.VERSION.SDK_INT >= 27);
    public static final ComponentName LEGACY_ONEPLUS_BLUR_WALLPAPER = ComponentName.unflattenFromString("com.oneplus.wallpaper/.BlurWallpaper");
    public static final ComponentName ONEPLUS_BLUR_WALLPAPER = ComponentName.unflattenFromString("net.oneplus.launcher/.wallpaper.BlurWallpaper");
    public static final ComponentName ONEPLUS_H2_BLUR_WALLPAPER = ComponentName.unflattenFromString("net.oneplus.h2launcher/.wallpaper.BlurWallpaper");
    private static final ComponentName ONEPLUS_LIVE_WALLPAPER;
    private static final Uri ONEPLUS_LIVE_WALLPAPER_URI = Uri.parse("content://" + ONEPLUS_LIVE_WALLPAPER.getPackageName() + "/image/wallpaper");

    static {
        ComponentName unflattenFromString = ComponentName.unflattenFromString("com.oneplus.wallpaper/.LiveWallpaper");
        Objects.requireNonNull(unflattenFromString);
        ONEPLUS_LIVE_WALLPAPER = unflattenFromString;
        ComponentName.unflattenFromString("net.oneplus.launcher/.wallpaper.DummyWallpaper");
        Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.JPEG;
    }

    public static boolean isOnePlusLiveWallpaper(Context context) {
        WallpaperInfo wallpaperInfo = WallpaperManager.getInstance(context).getWallpaperInfo();
        return (OPUtils.isSM8250Products() || OPUtils.isSM8750Products()) ? wallpaperInfo != null && wallpaperInfo.getComponent().flattenToString().contains(ONEPLUS_LIVE_WALLPAPER.flattenToString()) : wallpaperInfo != null && wallpaperInfo.getComponent().equals(ONEPLUS_LIVE_WALLPAPER);
    }

    public static boolean isOnePlusBlurWallpaper(Context context) {
        Process.myUserHandle();
        WallpaperInfo wallpaperInfo = WallpaperManager.getInstance(context).getWallpaperInfo();
        return wallpaperInfo != null && (wallpaperInfo.getComponent().equals(ONEPLUS_BLUR_WALLPAPER) || wallpaperInfo.getComponent().equals(LEGACY_ONEPLUS_BLUR_WALLPAPER));
    }

    public static boolean isImageWallpaper(Context context) {
        if (!checkPeekWallpaperPermission(context)) {
            Log.e("OPWallpaperUtils", "[isImageWallpaper] permission is not granted, return false");
            return false;
        }
        WallpaperManager instance = WallpaperManager.getInstance(context);
        if (instance.peekFastDrawable() == null || instance.getWallpaperInfo() != null) {
            return false;
        }
        return true;
    }

    public static Bitmap loadThumbnailFromWallpaperIcon(Context context, Drawable drawable) {
        if (context == null) {
            Log.i("OPWallpaperUtils", "[loadThumbnailFromWallpaperIcon] invalid context");
            return null;
        } else if (drawable == null) {
            Log.i("OPWallpaperUtils", "[loadThumbnailFromWallpaperIcon] invalid icon drawable");
            return null;
        } else {
            Resources resources = context.getResources();
            Bitmap createBitmap = Bitmap.createBitmap(resources.getDimensionPixelSize(C0007R$dimen.picker_preview_item_width), resources.getDimensionPixelSize(C0007R$dimen.picker_preview_item_height), Bitmap.Config.ARGB_8888);
            createBitmap.eraseColor(OPColorUtils.getMainColor(drawable, -16777216));
            return createBitmap;
        }
    }

    public static File getOnePlusBlurWallpaperFile(Context context) {
        return new File(context.getFilesDir(), "wallpaper");
    }

    public static int calculateSampleSize(BitmapFactory.Options options, int i, int i2) {
        int i3 = options.outHeight;
        int i4 = options.outWidth;
        int i5 = 1;
        if (i3 > i2 || i4 > i) {
            int i6 = i3 / 2;
            int i7 = i4 / 2;
            while (true) {
                int i8 = i6 / i5;
                if ((i8 <= i2 || i7 / i5 <= i) && i8 <= 6144 && i7 / i5 <= 6144) {
                    break;
                }
                i5 *= 2;
            }
        }
        return i5;
    }

    public static boolean hasGrantedPermission(Context context, String str) {
        return hasGrantedPermissions(context, str);
    }

    public static boolean hasGrantedPermissions(Context context, String... strArr) {
        int length = strArr.length;
        boolean z = true;
        for (int i = 0; i < length; i++) {
            z &= context.checkSelfPermission(strArr[i]) == 0;
        }
        return z;
    }

    public static boolean checkPeekWallpaperPermission(Context context) {
        if (ATLEAST_OREO_MR1) {
            return hasGrantedPermission(context, "android.permission.READ_EXTERNAL_STORAGE");
        }
        return true;
    }

    public static boolean isNormalLiveWallpaper(Context context) {
        return isNormalLiveWallpaper(WallpaperManager.getInstance(context).getWallpaperInfo());
    }

    public static boolean isNormalLiveWallpaper(WallpaperInfo wallpaperInfo) {
        return wallpaperInfo != null && !wallpaperInfo.getComponent().equals(LEGACY_ONEPLUS_BLUR_WALLPAPER) && !wallpaperInfo.getComponent().equals(ONEPLUS_BLUR_WALLPAPER) && !wallpaperInfo.getComponent().equals(ONEPLUS_H2_BLUR_WALLPAPER) && !wallpaperInfo.getComponent().equals(ONEPLUS_LIVE_WALLPAPER);
    }

    public static Bitmap getOnePlusLiveWallpaperBitmap(Context context) {
        Bitmap bitmap = null;
        try {
            InputStream openInputStream = context.getContentResolver().openInputStream(ONEPLUS_LIVE_WALLPAPER_URI);
            try {
                bitmap = getBitmapResizeWidthHeight(context, openInputStream);
                Log.d("OPWallpaperUtils", "getOnePlusLiveWallpaperBitmap bitmap = " + bitmap + " is = " + openInputStream);
                openInputStream.close();
                if (openInputStream != null) {
                    openInputStream.close();
                }
                return bitmap;
            } catch (Throwable th) {
                th.addSuppressed(th);
            }
            throw th;
        } catch (IOException e) {
            Log.d("OPWallpaperUtils", "getOnePlusLiveWallpaperBitmap# error loading wallpaper image, error: " + e);
        }
    }

    public static Bitmap loadHomeWallpaper(Context context, WallpaperInfo wallpaperInfo) {
        Bitmap bitmap;
        WallpaperManager instance = WallpaperManager.getInstance(context);
        if (isOnePlusLiveWallpaper(context)) {
            bitmap = getOnePlusLiveWallpaperBitmap(context);
            Log.d("OPWallpaperUtils", "OPSettings-isOnePlusLiveWallpapert-bitmap:" + bitmap);
        } else {
            Bitmap bitmap2 = null;
            if (isImageWallpaper(context)) {
                Drawable drawable = instance.getDrawable();
                bitmap = drawable instanceof BitmapDrawable ? ((BitmapDrawable) drawable).getBitmap() : null;
                Log.d("OPWallpaperUtils", "OPSettings-isImageWallpaper-bitmap:" + bitmap);
            } else if (isNormalLiveWallpaper(context) && wallpaperInfo != null) {
                Log.d("OPWallpaperUtils", "OPSettings-isNormalLiveWallpaper-bitmap:" + ((Object) null));
                bitmap = loadThumbnailFromWallpaperIcon(context, wallpaperInfo.loadIcon(context.getPackageManager()));
            } else if (isOnePlusBlurWallpaper(context)) {
                File onePlusBlurWallpaperFile = getOnePlusBlurWallpaperFile(context);
                if (onePlusBlurWallpaperFile.exists()) {
                    try {
                        Resources resources = context.getResources();
                        int dimensionPixelSize = resources.getDimensionPixelSize(C0007R$dimen.picker_preview_item_width);
                        int dimensionPixelSize2 = resources.getDimensionPixelSize(C0007R$dimen.picker_preview_item_height);
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inJustDecodeBounds = true;
                        BitmapFactory.decodeFile(onePlusBlurWallpaperFile.getPath(), options);
                        options.inSampleSize = calculateSampleSize(options, dimensionPixelSize, dimensionPixelSize2);
                        options.inJustDecodeBounds = false;
                        bitmap2 = BitmapFactory.decodeFile(onePlusBlurWallpaperFile.getPath(), options);
                        Log.d("OPWallpaperUtils", "OPSettings-isOnePlusBlurWallpaper-bitmap:" + bitmap2);
                    } catch (OutOfMemoryError unused) {
                        Log.e("OPWallpaperUtils", "failed loading oneplus blur wallpaper file");
                    }
                } else {
                    Log.e("OPWallpaperUtils", "wallpaper for latest blur wallpaper is empty");
                }
                bitmap = bitmap2;
            } else {
                Drawable drawable2 = instance.getDrawable();
                bitmap = drawable2 instanceof BitmapDrawable ? ((BitmapDrawable) drawable2).getBitmap() : null;
                Log.d("OPWallpaperUtils", "OPSettings-else-bitmap:" + bitmap);
            }
        }
        if (bitmap != null) {
            Log.d("OPWallpaperUtils", "OPSettings-loadHomeWallpaper-widht:" + bitmap.getWidth() + " height:" + bitmap.getHeight());
        }
        return bitmap;
    }

    public static Bitmap getBitmapResizeWidthHeight(Context context, InputStream inputStream) {
        try {
            context.getResources();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8;
            return BitmapFactory.decodeStream(inputStream, null, options);
        } catch (OutOfMemoryError e) {
            Log.e("OPWallpaperUtils", "getBitmapResizeWidthHeight error = " + e);
            return null;
        } catch (Exception e2) {
            Log.e("OPWallpaperUtils", "getBitmapResizeWidthHeight e = " + e2);
            return null;
        }
    }
}
