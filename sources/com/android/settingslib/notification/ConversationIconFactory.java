package com.android.settingslib.notification;

import android.content.Context;
import android.content.pm.LauncherApps;
import android.content.pm.PackageManager;
import android.content.pm.ShortcutInfo;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.UserHandle;
import android.util.IconDrawableFactory;
import android.util.Log;
import com.android.launcher3.icons.BaseIconFactory;
import com.android.settingslib.R$color;

public class ConversationIconFactory extends BaseIconFactory {
    private static final float CIRCLE_RADIUS;
    private static final float INN_CIRCLE_DIA;
    private static final float INN_CIRCLE_RAD;
    private static final float RING_STROKE_WIDTH;
    final IconDrawableFactory mIconDrawableFactory;
    private int mImportantConversationColor;
    final LauncherApps mLauncherApps;
    final PackageManager mPackageManager;

    static {
        float sqrt = (float) Math.sqrt(288.0d);
        INN_CIRCLE_DIA = sqrt;
        float f = sqrt / 2.0f;
        INN_CIRCLE_RAD = f;
        CIRCLE_RADIUS = f + ((10.0f - f) / 2.0f);
        RING_STROKE_WIDTH = (20.0f - sqrt) / 2.0f;
    }

    public ConversationIconFactory(Context context, LauncherApps launcherApps, PackageManager packageManager, IconDrawableFactory iconDrawableFactory, int i) {
        super(context, context.getResources().getConfiguration().densityDpi, i);
        this.mLauncherApps = launcherApps;
        this.mPackageManager = packageManager;
        this.mIconDrawableFactory = iconDrawableFactory;
        this.mImportantConversationColor = context.getResources().getColor(R$color.important_conversation, null);
    }

    public Drawable getBaseIconDrawable(ShortcutInfo shortcutInfo) {
        return this.mLauncherApps.getShortcutIconDrawable(shortcutInfo, this.mFillResIconDpi);
    }

    public Drawable getAppBadge(String str, int i) {
        try {
            return this.mIconDrawableFactory.getBadgedIcon(this.mPackageManager.getApplicationInfoAsUser(str, 128, i), i);
        } catch (PackageManager.NameNotFoundException unused) {
            return this.mPackageManager.getDefaultActivityIcon();
        }
    }

    public Drawable getConversationDrawable(ShortcutInfo shortcutInfo, String str, int i, boolean z) {
        return getConversationDrawable(getBaseIconDrawable(shortcutInfo), str, i, z);
    }

    public Drawable getConversationDrawable(Drawable drawable, String str, int i, boolean z) {
        return new ConversationIconDrawable(drawable, getAppBadge(str, UserHandle.getUserId(i)), this.mIconBitmapSize, this.mImportantConversationColor, z);
    }

    public static class ConversationIconDrawable extends Drawable {
        private Drawable mBadgeIcon;
        private Drawable mBaseIcon;
        private int mIconSize;
        private Paint mPaddingPaint;
        private Paint mRingPaint;
        private boolean mShowRing;

        public int getOpacity() {
            return 0;
        }

        public void setAlpha(int i) {
        }

        public void setColorFilter(ColorFilter colorFilter) {
        }

        public ConversationIconDrawable(Drawable drawable, Drawable drawable2, int i, int i2, boolean z) {
            this.mBaseIcon = drawable;
            this.mBadgeIcon = drawable2;
            this.mIconSize = i;
            this.mShowRing = z;
            Paint paint = new Paint();
            this.mRingPaint = paint;
            paint.setStyle(Paint.Style.STROKE);
            this.mRingPaint.setColor(i2);
            Paint paint2 = new Paint();
            this.mPaddingPaint = paint2;
            paint2.setStyle(Paint.Style.FILL_AND_STROKE);
            this.mPaddingPaint.setColor(-1);
        }

        public void setImportant(boolean z) {
            if (z != this.mShowRing) {
                this.mShowRing = z;
                invalidateSelf();
            }
        }

        public int getIntrinsicWidth() {
            return this.mIconSize;
        }

        public int getIntrinsicHeight() {
            return this.mIconSize;
        }

        public void draw(Canvas canvas) {
            float width = ((float) getBounds().width()) / 56.0f;
            int i = (int) (52.0f * width);
            int i2 = (int) (40.0f * width);
            int i3 = (int) (46.0f * width);
            float f = (float) ((int) (ConversationIconFactory.RING_STROKE_WIDTH * width));
            this.mPaddingPaint.setStrokeWidth(f);
            float f2 = (float) ((int) (ConversationIconFactory.CIRCLE_RADIUS * width));
            Drawable drawable = this.mBaseIcon;
            if (drawable != null) {
                drawable.setBounds(0, 0, i, i);
                this.mBaseIcon.draw(canvas);
            } else {
                Log.w("ConversationIconFactory", "ConversationIconDrawable has null base icon");
            }
            if (this.mBadgeIcon != null) {
                float f3 = (float) i3;
                canvas.drawCircle(f3, f3, f2, this.mPaddingPaint);
                this.mBadgeIcon.setBounds(i2, i2, i, i);
                this.mBadgeIcon.draw(canvas);
            } else {
                Log.w("ConversationIconFactory", "ConversationIconDrawable has null badge icon");
            }
            if (this.mShowRing) {
                this.mRingPaint.setStrokeWidth(f);
                float f4 = (float) i3;
                canvas.drawCircle(f4, f4, f2, this.mRingPaint);
            }
        }
    }
}
