package com.oneplus.settings.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import androidx.core.content.ContextCompat;
import com.android.settings.C0003R$array;
import com.android.settings.C0006R$color;
import com.android.settings.C0008R$drawable;
import com.oneplus.settings.utils.OPUtils;

public class OPDefaultAvatarDrawable extends Drawable {
    private static final int[] CIRCLE_TEXTURE_RESOURCE_IDS = {C0008R$drawable.op_member_grid_circle_left_bottom_bg, C0008R$drawable.op_member_grid_circle_left_top_bg, C0008R$drawable.op_member_grid_circle_right_bottom_bg, C0008R$drawable.op_member_grid_circle_right_top_bg, C0008R$drawable.op_member_grid_diagonal_left_bottom_01_bg, C0008R$drawable.op_member_grid_diagonal_left_bottom_02_bg, C0008R$drawable.op_member_grid_diagonal_left_bottom_03_bg, C0008R$drawable.op_member_grid_diagonal_left_top_bg, C0008R$drawable.op_member_grid_horizon_bg, C0008R$drawable.op_member_grid_vertical_bg};
    private String abridgeName;
    private int color;
    private final TypedArray colors;
    private Context mContext;
    private float offset = 0.0f;
    private final Paint paint = new Paint();
    private final Rect rect = new Rect();
    private Drawable texture;

    private boolean isInvalidIndex(int i) {
        return i < 0 || i >= 10;
    }

    public int getOpacity() {
        return -1;
    }

    public OPDefaultAvatarDrawable(Context context, String str, String str2) {
        this.mContext = context;
        this.colors = context.getResources().obtainTypedArray(C0003R$array.texture_bg_color_array);
        this.paint.setTypeface(Typeface.create("sans-serif-medium", 0));
        this.paint.setTextAlign(Paint.Align.CENTER);
        this.paint.setAntiAlias(true);
        this.paint.setFilterBitmap(true);
        this.paint.setDither(true);
        this.abridgeName = str;
        int resIndexByIdentify = getResIndexByIdentify(str2);
        Log.d("OPDefaultAvatarDrawable", "index:" + resIndexByIdentify);
        this.texture = pickTexture(resIndexByIdentify);
        this.color = pickColor(resIndexByIdentify);
    }

    public void draw(Canvas canvas) {
        Rect bounds = getBounds();
        if (isVisible() && !bounds.isEmpty()) {
            drawLetterTile(canvas);
        }
    }

    private void drawLetterTile(Canvas canvas) {
        this.paint.setColor(this.color);
        Rect bounds = getBounds();
        int min = Math.min(bounds.width(), bounds.height());
        canvas.drawCircle((float) bounds.centerX(), (float) bounds.centerY(), (float) (min / 2), this.paint);
        Drawable drawable = this.texture;
        if (drawable != null) {
            drawable.setBounds(bounds);
            this.texture.draw(canvas);
        }
        if (!TextUtils.isEmpty(this.abridgeName)) {
            this.abridgeName = this.abridgeName.substring(0, 1);
            Log.d("LetterTileDrawable", "minDimension:" + min);
            this.paint.setTextSize((float) OPUtils.sp2px(this.mContext, 24.0f));
            this.paint.getTextBounds(this.abridgeName, 0, 1, this.rect);
            this.paint.setTypeface(Typeface.create("sans-serif", 0));
            this.paint.setColor(-1);
            canvas.drawText(this.abridgeName, (float) bounds.centerX(), (((float) bounds.centerY()) + (this.offset * ((float) bounds.height()))) - this.rect.exactCenterY(), this.paint);
        }
    }

    private Drawable pickTexture(int i) {
        if (isInvalidIndex(i)) {
            return null;
        }
        return this.mContext.getResources().getDrawable(CIRCLE_TEXTURE_RESOURCE_IDS[i], null);
    }

    private int pickColor(int i) {
        return this.colors.getColor(i, ContextCompat.getColor(this.mContext, C0006R$color.avatar_bg_red));
    }

    public void setAlpha(int i) {
        this.paint.setAlpha(i);
    }

    public void setColorFilter(ColorFilter colorFilter) {
        this.paint.setColorFilter(colorFilter);
    }

    public void getOutline(Outline outline) {
        outline.setOval(getBounds());
        outline.setAlpha(1.0f);
    }

    private int getResIndexByIdentify(String str) {
        if (TextUtils.isEmpty(str)) {
            return 0;
        }
        return Math.abs(str.hashCode()) % this.colors.length();
    }
}
