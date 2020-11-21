package androidx.slice.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.format.DateUtils;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import androidx.appcompat.R$attr;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.graphics.drawable.IconCompat;
import java.util.Calendar;

public class SliceViewUtil {
    public static int resolveLayoutDirection(int i) {
        if (i == 2 || i == 3 || i == 1 || i == 0) {
            return i;
        }
        return -1;
    }

    public static int getColorAccent(Context context) {
        return getColorAttr(context, 16843829);
    }

    public static int getColorAttr(Context context, int i) {
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(new int[]{i});
        int color = obtainStyledAttributes.getColor(0, 0);
        obtainStyledAttributes.recycle();
        return color;
    }

    public static Drawable getDrawable(Context context, int i) {
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(new int[]{i});
        Drawable drawable = obtainStyledAttributes.getDrawable(0);
        obtainStyledAttributes.recycle();
        return drawable;
    }

    public static IconCompat createIconFromDrawable(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return IconCompat.createWithBitmap(((BitmapDrawable) drawable).getBitmap());
        }
        Bitmap createBitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return IconCompat.createWithBitmap(createBitmap);
    }

    public static void createCircledIcon(Context context, int i, IconCompat iconCompat, boolean z, ViewGroup viewGroup) {
        ImageView.ScaleType scaleType;
        ImageView imageView = new ImageView(context);
        imageView.setImageDrawable(iconCompat.loadDrawable(context));
        if (z) {
            scaleType = ImageView.ScaleType.CENTER_CROP;
        } else {
            scaleType = ImageView.ScaleType.CENTER_INSIDE;
        }
        imageView.setScaleType(scaleType);
        viewGroup.addView(imageView);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) imageView.getLayoutParams();
        if (z) {
            Bitmap createBitmap = Bitmap.createBitmap(i, i, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(createBitmap);
            imageView.layout(0, 0, i, i);
            imageView.draw(canvas);
            imageView.setImageBitmap(getCircularBitmap(createBitmap));
        } else {
            imageView.setColorFilter(-1);
        }
        layoutParams.width = i;
        layoutParams.height = i;
        layoutParams.gravity = 17;
    }

    public static Bitmap getCircularBitmap(Bitmap bitmap) {
        Bitmap createBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawCircle((float) (bitmap.getWidth() / 2), (float) (bitmap.getHeight() / 2), (float) (bitmap.getWidth() / 2), paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return createBitmap;
    }

    public static CharSequence getTimestampString(Context context, long j) {
        if (j < System.currentTimeMillis() || DateUtils.isToday(j)) {
            return DateUtils.getRelativeTimeSpanString(j, Calendar.getInstance().getTimeInMillis(), 60000, 262144);
        }
        return DateUtils.formatDateTime(context, j, 8);
    }

    public static void tintIndeterminateProgressBar(Context context, ProgressBar progressBar) {
        int colorAttr = getColorAttr(context, R$attr.colorControlHighlight);
        Drawable wrap = DrawableCompat.wrap(progressBar.getIndeterminateDrawable());
        if (wrap != null && colorAttr != 0) {
            wrap.setColorFilter(colorAttr, PorterDuff.Mode.MULTIPLY);
            progressBar.setProgressDrawable(wrap);
        }
    }
}
