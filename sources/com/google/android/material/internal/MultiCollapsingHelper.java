package com.google.android.material.internal;

import android.animation.TimeInterpolator;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Build;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import androidx.animation.AnimatorUtils;
import androidx.core.math.MathUtils;
import androidx.core.text.TextDirectionHeuristicsCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import com.google.android.material.R$color;
import com.google.android.material.R$dimen;
import com.google.android.material.animation.AnimationUtils;
import com.google.android.material.appbar.CollapsingAppbarLayout;
import com.google.android.material.resources.TextAppearance;

public final class MultiCollapsingHelper {
    private static final Paint DEBUG_DRAW_PAINT = null;
    private static final boolean USE_SCALING_TEXTURE = (Build.VERSION.SDK_INT < 18);
    private ColorStateList collapsedSubtitleColor;
    private ColorStateList collapsedSubtitleShadowColor;
    private float collapsedSubtitleShadowDx;
    private float collapsedSubtitleShadowDy;
    private float collapsedSubtitleShadowRadius;
    private float collapsedSubtitleSize = 15.0f;
    private Typeface collapsedSubtitleTypeface;
    private float collapsedSubtitleX;
    private float collapsedSubtitleY;
    private float currentSubtitleSize;
    private Typeface currentSubtitleTypeface;
    private float currentSubtitleX;
    private float currentSubtitleY;
    private ColorStateList expandedSubtitleColor;
    private ColorStateList expandedSubtitleShadowColor;
    private float expandedSubtitleShadowDx;
    private float expandedSubtitleShadowDy;
    private float expandedSubtitleShadowRadius;
    private float expandedSubtitleSize = 15.0f;
    private Bitmap expandedSubtitleTexture;
    private Typeface expandedSubtitleTypeface;
    private float expandedSubtitleX;
    private float expandedSubtitleY;
    private float lineSpacingExtra = 0.0f;
    private float lineSpacingMultiplier = 1.0f;
    private int mAppbarMarginBottom;
    private int mAppbarMarginLeft;
    private int mAppbarMarginRight;
    private boolean mBoundsChanged;
    private final Rect mCollapsedBounds;
    private float mCollapsedDrawX;
    private float mCollapsedDrawY;
    private float mCollapsedTextBlend;
    private ColorStateList mCollapsedTextColor;
    private int mCollapsedTextGravity = 16;
    private float mCollapsedTextSize;
    private ColorStateList mCollapsedTitleShadowColor;
    private float mCollapsedTitleShadowDx;
    private float mCollapsedTitleShadowDy;
    private float mCollapsedTitleShadowRadius;
    private Bitmap mCollapsedTitleTexture;
    private Typeface mCollapsedTypeface;
    private Bitmap mCrossSectionTitleTexture;
    private final RectF mCurrentBounds;
    private float mCurrentDrawX;
    private float mCurrentDrawY;
    private float mCurrentTitleSize;
    private Typeface mCurrentTypeface;
    private Bitmap mDrawBitmap;
    private boolean mDrawLine = true;
    private boolean mDrawTitle;
    private final Rect mExpandedBounds;
    private float mExpandedDrawX;
    private float mExpandedDrawY;
    private float mExpandedFirstLineDrawX;
    private float mExpandedFraction;
    private float mExpandedTextBlend;
    private ColorStateList mExpandedTextColor;
    private int mExpandedTextGravity = 16;
    private float mExpandedTextSize;
    private ColorStateList mExpandedTitleShadowColor;
    private float mExpandedTitleShadowDx;
    private float mExpandedTitleShadowDy;
    private float mExpandedTitleShadowRadius;
    private Bitmap mExpandedTitleTexture;
    private Typeface mExpandedTypeface;
    private boolean mInsetSubtitleImage = false;
    private boolean mIsRtl;
    private TimeInterpolator mPositionInterpolator;
    private float mScale;
    private int[] mState;
    private float mSyncBottomY;
    private float mSyncLeftX;
    private float mSyncRightX;
    private CharSequence mSyncText;
    private float mSyncTopY;
    private float mTempY;
    private StaticLayout mTextLayout;
    private final TextPaint mTextPaint;
    private TimeInterpolator mTextSizeInterpolator;
    private CharSequence mTextToDraw;
    private CharSequence mTextToDrawCollapsed;
    private Paint mTexturePaint;
    private CharSequence mTitle;
    private boolean mUseTexture;
    private final CollapsingAppbarLayout mView;
    private int maxLines = 2;
    private CharSequence subtitle;
    private final TextPaint subtitlePaint;
    private float subtitleScale;
    private Paint subtitleTexturePaint;
    private CharSequence subtitleToDraw;
    private ColorStateList syncColorList;
    private final TextPaint syncTextPaint;

    static {
        Paint paint = null;
        if (0 != 0) {
            paint.setAntiAlias(true);
            DEBUG_DRAW_PAINT.setColor(-65281);
        }
    }

    public MultiCollapsingHelper(CollapsingAppbarLayout collapsingAppbarLayout) {
        this.mView = collapsingAppbarLayout;
        this.mTextPaint = new TextPaint(129);
        this.subtitlePaint = new TextPaint(129);
        this.syncTextPaint = new TextPaint(129);
        this.syncColorList = this.mView.getResources().getColorStateList(R$color.oneplus_accent_color);
        this.mCollapsedBounds = new Rect();
        this.mExpandedBounds = new Rect();
        this.mCurrentBounds = new RectF();
    }

    public void setTextSizeInterpolator(TimeInterpolator timeInterpolator) {
        this.mTextSizeInterpolator = timeInterpolator;
        recalculate();
    }

    public void setCollapsedTitleColor(ColorStateList colorStateList) {
        if (this.mCollapsedTextColor != colorStateList) {
            this.mCollapsedTextColor = colorStateList;
            recalculate();
        }
    }

    public void setExpandedTitleColor(ColorStateList colorStateList) {
        if (this.mExpandedTextColor != colorStateList) {
            this.mExpandedTextColor = colorStateList;
            recalculate();
        }
    }

    public void setSyncTextColor(ColorStateList colorStateList) {
        if (this.syncColorList != colorStateList) {
            this.syncColorList = colorStateList;
        }
    }

    public void setExpandedBounds(int i, int i2, int i3, int i4) {
        if (!rectEquals(this.mExpandedBounds, i, i2, i3, i4)) {
            this.mExpandedBounds.set(i, i2, i3, i4);
            this.mBoundsChanged = true;
            onBoundsChanged();
        }
    }

    public void setCollapsedBounds(int i, int i2, int i3, int i4) {
        if (!rectEquals(this.mCollapsedBounds, i, i2, i3, i4)) {
            this.mCollapsedBounds.set(i, i2, i3, i4);
            this.mBoundsChanged = true;
            onBoundsChanged();
        }
    }

    public void onBoundsChanged() {
        this.mDrawTitle = this.mCollapsedBounds.width() > 0 && this.mCollapsedBounds.height() > 0 && this.mExpandedBounds.width() > 0 && this.mExpandedBounds.height() > 0;
    }

    public void setExpandedTextGravity(int i) {
        if (this.mExpandedTextGravity != i) {
            this.mExpandedTextGravity = i;
            recalculate();
        }
    }

    public int getExpandedTextGravity() {
        return this.mExpandedTextGravity;
    }

    public void setCollapsedTextGravity(int i) {
        if (this.mCollapsedTextGravity != i) {
            this.mCollapsedTextGravity = i;
            recalculate();
        }
    }

    public int getCollapsedTextGravity() {
        return this.mCollapsedTextGravity;
    }

    public void setCollapsedTitleAppearance(int i) {
        TextAppearance textAppearance = new TextAppearance(this.mView.getContext(), i);
        ColorStateList colorStateList = textAppearance.textColor;
        if (colorStateList != null) {
            this.mCollapsedTextColor = colorStateList;
        }
        float f = textAppearance.textSize;
        if (f != 0.0f) {
            this.mCollapsedTextSize = f;
        }
        ColorStateList colorStateList2 = textAppearance.shadowColor;
        if (colorStateList2 != null) {
            this.mCollapsedTitleShadowColor = colorStateList2;
        }
        this.mCollapsedTitleShadowDx = textAppearance.shadowDx;
        this.mCollapsedTitleShadowDy = textAppearance.shadowDy;
        this.mCollapsedTitleShadowRadius = textAppearance.shadowRadius;
        if (Build.VERSION.SDK_INT >= 16) {
            this.mCollapsedTypeface = readFontFamilyTypeface(i);
        }
        recalculate();
    }

    public void setExpandedTitleAppearance(int i) {
        TextAppearance textAppearance = new TextAppearance(this.mView.getContext(), i);
        ColorStateList colorStateList = textAppearance.textColor;
        if (colorStateList != null) {
            this.mExpandedTextColor = colorStateList;
        }
        float f = textAppearance.textSize;
        if (f != 0.0f) {
            this.mExpandedTextSize = f;
        }
        ColorStateList colorStateList2 = textAppearance.shadowColor;
        if (colorStateList2 != null) {
            this.mExpandedTitleShadowColor = colorStateList2;
        }
        this.mExpandedTitleShadowDx = textAppearance.shadowDx;
        this.mExpandedTitleShadowDy = textAppearance.shadowDy;
        this.mExpandedTitleShadowRadius = textAppearance.shadowRadius;
        if (Build.VERSION.SDK_INT >= 16) {
            this.mExpandedTypeface = readFontFamilyTypeface(i);
        }
        recalculate();
    }

    private Typeface readFontFamilyTypeface(int i) {
        TypedArray obtainStyledAttributes = this.mView.getContext().obtainStyledAttributes(i, new int[]{16843692});
        try {
            String string = obtainStyledAttributes.getString(0);
            if (string != null) {
                return Typeface.create(string, 0);
            }
            obtainStyledAttributes.recycle();
            return null;
        } finally {
            obtainStyledAttributes.recycle();
        }
    }

    public void setCollapsedTitleTypeface(Typeface typeface) {
        if (areTypefacesDifferent(this.mCollapsedTypeface, typeface)) {
            this.mCollapsedTypeface = typeface;
            recalculate();
        }
    }

    public void setExpandedTitleTypeface(Typeface typeface) {
        if (areTypefacesDifferent(this.mExpandedTypeface, typeface)) {
            this.mExpandedTypeface = typeface;
            recalculate();
        }
    }

    public Typeface getCollapsedTitleTypeface() {
        Typeface typeface = this.mCollapsedTypeface;
        return typeface != null ? typeface : Typeface.DEFAULT;
    }

    public void setCollapsedSubtitleTypeface(Typeface typeface) {
        if (setCollapsedSubtitleTypefaceInternal(typeface)) {
            recalculate();
        }
    }

    public void setExpandedSubtitleTypeface(Typeface typeface) {
        if (setExpandedSubtitleTypefaceInternal(typeface)) {
            recalculate();
        }
    }

    public Typeface getCollapsedSubtitleTypeface() {
        Typeface typeface = this.collapsedSubtitleTypeface;
        return typeface != null ? typeface : Typeface.DEFAULT;
    }

    public Typeface getExpandedSubtitleTypeface() {
        Typeface typeface = this.expandedSubtitleTypeface;
        return typeface != null ? typeface : Typeface.DEFAULT;
    }

    private boolean setExpandedSubtitleTypefaceInternal(Typeface typeface) {
        if (this.expandedSubtitleTypeface == typeface) {
            return false;
        }
        this.expandedSubtitleTypeface = typeface;
        return true;
    }

    private boolean setCollapsedSubtitleTypefaceInternal(Typeface typeface) {
        if (this.collapsedSubtitleTypeface == typeface) {
            return false;
        }
        this.collapsedSubtitleTypeface = typeface;
        return true;
    }

    public Typeface getExpandedTitleTypeface() {
        Typeface typeface = this.mExpandedTypeface;
        return typeface != null ? typeface : Typeface.DEFAULT;
    }

    public void setExpansionFraction(float f) {
        float clamp = MathUtils.clamp(f, 0.0f, 1.0f);
        if (clamp != this.mExpandedFraction) {
            this.mExpandedFraction = clamp;
            calculateCurrentOffsets();
        }
    }

    public final boolean setState(int[] iArr) {
        this.mState = iArr;
        if (!isStateful()) {
            return false;
        }
        recalculate();
        return true;
    }

    /* access modifiers changed from: package-private */
    public final boolean isStateful() {
        ColorStateList colorStateList;
        ColorStateList colorStateList2 = this.mCollapsedTextColor;
        return (colorStateList2 != null && colorStateList2.isStateful()) || ((colorStateList = this.mExpandedTextColor) != null && colorStateList.isStateful());
    }

    public float getExpansionFraction() {
        return this.mExpandedFraction;
    }

    public void calculateCurrentOffsets() {
        calculateOffsets(this.mExpandedFraction);
    }

    private void calculateOffsets(float f) {
        interpolateBounds(f);
        this.mCurrentDrawX = lerp(this.mExpandedDrawX, this.mCollapsedDrawX, f, this.mPositionInterpolator);
        this.mCurrentDrawY = lerp(this.mExpandedDrawY, this.mCollapsedDrawY, f, this.mPositionInterpolator);
        this.currentSubtitleX = lerp(this.expandedSubtitleX, this.collapsedSubtitleX, f, this.mPositionInterpolator);
        this.currentSubtitleY = lerp(this.expandedSubtitleY, this.collapsedSubtitleY, f, this.mPositionInterpolator);
        setInterpolatedTextSize(lerp(this.mExpandedTextSize, this.mCollapsedTextSize, f, this.mTextSizeInterpolator));
        setInterpolatedSubtitleSize(lerp(this.expandedSubtitleSize, this.collapsedSubtitleSize, f, this.mTextSizeInterpolator));
        setCollapsedTextBlend(1.0f - lerp(0.0f, 1.0f, 1.0f - f, AnimatorUtils.op_control_interpolator_linear_out_slow_in));
        setExpandedTextBlend(lerp(1.0f, 0.0f, f, AnimatorUtils.op_control_interpolator_linear_out_slow_in));
        if (this.mCollapsedTextColor != this.mExpandedTextColor) {
            this.mTextPaint.setColor(blendColors(getCurrentExpandedTextColor(), getCurrentCollapsedTextColor(), f));
        } else {
            this.mTextPaint.setColor(getCurrentCollapsedTextColor());
        }
        this.mTextPaint.setShadowLayer(lerp(this.mExpandedTitleShadowRadius, this.mCollapsedTitleShadowRadius, f, null), lerp(this.mExpandedTitleShadowDx, this.mCollapsedTitleShadowDx, f, null), lerp(this.mExpandedTitleShadowDy, this.mCollapsedTitleShadowDy, f, null), blendColors(getCurrentColor(this.mExpandedTitleShadowColor), getCurrentColor(this.mCollapsedTitleShadowColor), f));
        if (this.collapsedSubtitleColor != this.expandedSubtitleColor) {
            this.subtitlePaint.setColor(blendColors(getCurrentExpandedSubtitleColor(), getCurrentCollapsedSubtitleColor(), f));
            this.syncTextPaint.setColor(getCurrentSyncTextColor());
        } else {
            this.subtitlePaint.setColor(getCurrentCollapsedSubtitleColor());
        }
        this.subtitlePaint.setShadowLayer(lerp(this.expandedSubtitleShadowRadius, this.collapsedSubtitleShadowRadius, f, null), lerp(this.expandedSubtitleShadowDx, this.collapsedSubtitleShadowDx, f, null), lerp(this.expandedSubtitleShadowDy, this.collapsedSubtitleShadowDy, f, null), blendColors(getCurrentColor(this.expandedSubtitleShadowColor), getCurrentColor(this.collapsedSubtitleShadowColor), f));
        ViewCompat.postInvalidateOnAnimation(this.mView);
    }

    private void setInterpolatedSubtitleSize(float f) {
        calculateUsingSubtitleSize(f);
        boolean z = USE_SCALING_TEXTURE && this.subtitleScale != 1.0f;
        this.mUseTexture = z;
        if (z) {
            ensureExpandedSubtitleTexture();
        }
        ViewCompat.postInvalidateOnAnimation(this.mView);
    }

    private void ensureExpandedSubtitleTexture() {
        if (this.expandedSubtitleTexture == null && !this.mExpandedBounds.isEmpty() && !TextUtils.isEmpty(this.subtitleToDraw)) {
            calculateOffsets(0.0f);
            TextPaint textPaint = this.subtitlePaint;
            CharSequence charSequence = this.subtitleToDraw;
            int round = Math.round(textPaint.measureText(charSequence, 0, charSequence.length()));
            int round2 = Math.round(this.subtitlePaint.descent() - this.subtitlePaint.ascent());
            if (round > 0 && round2 >= 0) {
                this.expandedSubtitleTexture = Bitmap.createBitmap(round, round2, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(this.expandedSubtitleTexture);
                CharSequence charSequence2 = this.subtitleToDraw;
                canvas.drawText(charSequence2, 0, charSequence2.length(), 0.0f, ((float) round2) - this.subtitlePaint.descent(), this.subtitlePaint);
                if (this.subtitleTexturePaint == null) {
                    this.subtitleTexturePaint = new Paint(3);
                }
            }
        }
    }

    private int getCurrentExpandedSubtitleColor() {
        return getCurrentColor(this.expandedSubtitleColor);
    }

    private int getCurrentSyncTextColor() {
        return getCurrentColor(this.syncColorList);
    }

    public int getCurrentCollapsedSubtitleColor() {
        return getCurrentColor(this.collapsedSubtitleColor);
    }

    public void setCollapsedSubtitleColor(ColorStateList colorStateList) {
        if (this.collapsedSubtitleColor != colorStateList) {
            this.collapsedSubtitleColor = colorStateList;
            recalculate();
        }
    }

    public void setExpandedSubtitleColor(ColorStateList colorStateList) {
        if (this.expandedSubtitleColor != colorStateList) {
            this.expandedSubtitleColor = colorStateList;
            recalculate();
        }
    }

    private void calculateUsingSubtitleSize(float f) {
        float f2;
        boolean z;
        if (this.subtitle != null) {
            float width = (float) this.mCollapsedBounds.width();
            float width2 = (float) this.mExpandedBounds.width();
            boolean z2 = true;
            if (isClose(f, this.collapsedSubtitleSize)) {
                f2 = this.collapsedSubtitleSize;
                this.subtitleScale = 1.0f;
                Typeface typeface = this.currentSubtitleTypeface;
                Typeface typeface2 = this.collapsedSubtitleTypeface;
                if (typeface != typeface2) {
                    this.currentSubtitleTypeface = typeface2;
                }
            } else {
                float f3 = this.expandedSubtitleSize;
                Typeface typeface3 = this.currentSubtitleTypeface;
                Typeface typeface4 = this.expandedSubtitleTypeface;
                if (typeface3 != typeface4) {
                    this.currentSubtitleTypeface = typeface4;
                    z = true;
                } else {
                    z = false;
                }
                if (isClose(f, this.expandedSubtitleSize)) {
                    this.subtitleScale = 1.0f;
                } else {
                    this.subtitleScale = f / this.expandedSubtitleSize;
                }
                float f4 = this.collapsedSubtitleSize / this.expandedSubtitleSize;
                width = width2 * f4 > width ? Math.min(width / f4, width2) : width2;
                f2 = f3;
            }
            if (width > 0.0f) {
                if (this.currentSubtitleSize == f2) {
                    boolean z3 = this.mBoundsChanged;
                }
                this.currentSubtitleSize = f2;
                this.mBoundsChanged = false;
            }
            this.subtitlePaint.setTextSize(this.currentSubtitleSize);
            this.subtitlePaint.setTypeface(this.currentSubtitleTypeface);
            this.subtitlePaint.setLinearText(this.subtitleScale != 1.0f);
            this.syncTextPaint.setTextSize(this.currentSubtitleSize);
            this.syncTextPaint.setColor(this.mView.getResources().getColor(R$color.oneplus_accent_color));
            this.syncTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
            TextPaint textPaint = this.syncTextPaint;
            if (this.subtitleScale == 1.0f) {
                z2 = false;
            }
            textPaint.setLinearText(z2);
            CharSequence ellipsize = TextUtils.ellipsize(this.subtitle, this.subtitlePaint, width, TextUtils.TruncateAt.END);
            if (!TextUtils.equals(ellipsize, this.subtitleToDraw)) {
                this.subtitleToDraw = ellipsize;
                this.mIsRtl = calculateIsRtl(ellipsize);
            }
        }
    }

    private int getCurrentColor(ColorStateList colorStateList) {
        if (colorStateList == null) {
            return 0;
        }
        int[] iArr = this.mState;
        if (iArr != null) {
            return colorStateList.getColorForState(iArr, 0);
        }
        return colorStateList.getDefaultColor();
    }

    public int getCurrentExpandedTextColor() {
        int[] iArr = this.mState;
        if (iArr != null) {
            return this.mExpandedTextColor.getColorForState(iArr, 0);
        }
        return this.mExpandedTextColor.getDefaultColor();
    }

    private int getCurrentCollapsedTextColor() {
        int[] iArr = this.mState;
        if (iArr != null) {
            return this.mCollapsedTextColor.getColorForState(iArr, 0);
        }
        return this.mCollapsedTextColor.getDefaultColor();
    }

    private void calculateBaseOffsets() {
        float f;
        float f2 = this.mCurrentTitleSize;
        float f3 = this.currentSubtitleSize;
        boolean isEmpty = TextUtils.isEmpty(this.subtitle);
        calculateUsingTextSize(this.mCollapsedTextSize);
        calculateUsingSubtitleSize(this.collapsedSubtitleSize);
        CharSequence charSequence = this.mTextToDraw;
        this.mTextToDrawCollapsed = charSequence;
        float measureText = charSequence != null ? this.mTextPaint.measureText(charSequence, 0, charSequence.length()) : 0.0f;
        CharSequence charSequence2 = this.subtitleToDraw;
        float measureText2 = charSequence2 != null ? this.subtitlePaint.measureText(charSequence2, 0, charSequence2.length()) : 0.0f;
        int absoluteGravity = GravityCompat.getAbsoluteGravity(this.mCollapsedTextGravity, this.mIsRtl ? 1 : 0);
        StaticLayout staticLayout = this.mTextLayout;
        float height = staticLayout != null ? (float) staticLayout.getHeight() : 0.0f;
        float descent = this.subtitlePaint.descent() - this.subtitlePaint.ascent();
        if (isEmpty) {
            int i = absoluteGravity & 112;
            if (i == 48) {
                this.mCollapsedDrawY = (float) this.mCollapsedBounds.top;
            } else if (i != 80) {
                this.mCollapsedDrawY = ((float) this.mCollapsedBounds.centerY()) - ((height / 2.0f) + ViewUtils.dpToPx(this.mView.getContext(), 3));
            } else {
                this.mCollapsedDrawY = ((float) this.mCollapsedBounds.bottom) - height;
            }
        } else {
            float height2 = (((float) this.mCollapsedBounds.height()) - (descent + height)) / 3.0f;
            float f4 = (float) this.mCollapsedBounds.top;
            if (height2 > ((float) this.mView.getResources().getDimensionPixelOffset(R$dimen.op_control_margin_space2)) + 2.2f) {
                f = height2;
            } else {
                f = (float) this.mView.getResources().getDimensionPixelOffset(R$dimen.op_app_bar_margin_top);
            }
            this.mCollapsedDrawY = ((f4 + f) - this.mTextPaint.ascent()) + this.subtitlePaint.ascent();
            this.collapsedSubtitleY = (((((float) this.mCollapsedBounds.top) + (height2 * 2.0f)) + height) - this.subtitlePaint.ascent()) - ((float) this.mView.getResources().getDimensionPixelOffset(R$dimen.op_app_bar_collapsed_top_subtitle_margin));
            if (this.mExpandedBounds.top < 100) {
                this.mCollapsedDrawY += this.mTextPaint.ascent() / 4.0f;
            }
        }
        int dimensionPixelOffset = this.mView.getContext().getResources().getDimensionPixelOffset(R$dimen.op_control_margin_space4);
        int i2 = absoluteGravity & 8388615;
        if (i2 == 1) {
            this.mCollapsedDrawX = ((float) this.mCollapsedBounds.centerX()) - (measureText / 2.0f);
            this.collapsedSubtitleX = (((float) this.mCollapsedBounds.centerX()) - (measureText2 / 2.0f)) + ((float) (this.mInsetSubtitleImage ? dimensionPixelOffset : 0));
        } else if (i2 != 5) {
            int i3 = this.mCollapsedBounds.left;
            this.mCollapsedDrawX = (float) i3;
            this.collapsedSubtitleX = (float) (i3 + (this.mInsetSubtitleImage ? dimensionPixelOffset : 0));
        } else {
            int i4 = this.mCollapsedBounds.right;
            this.mCollapsedDrawX = ((float) i4) - measureText;
            this.collapsedSubtitleX = (((float) i4) - measureText2) + ((float) (this.mInsetSubtitleImage ? dimensionPixelOffset : 0));
        }
        calculateUsingTextSize(this.mExpandedTextSize);
        calculateUsingSubtitleSize(this.expandedSubtitleSize);
        StaticLayout staticLayout2 = this.mTextLayout;
        float lineWidth = staticLayout2 != null ? staticLayout2.getLineWidth(0) : 0.0f;
        CharSequence charSequence3 = this.subtitleToDraw;
        float measureText3 = charSequence3 != null ? this.subtitlePaint.measureText(charSequence3, 0, charSequence3.length()) : 0.0f;
        StaticLayout staticLayout3 = this.mTextLayout;
        this.mExpandedFirstLineDrawX = staticLayout3 != null ? staticLayout3.getLineLeft(0) : 0.0f;
        int absoluteGravity2 = GravityCompat.getAbsoluteGravity(this.mExpandedTextGravity, this.mIsRtl ? 1 : 0);
        StaticLayout staticLayout4 = this.mTextLayout;
        float height3 = staticLayout4 != null ? (float) staticLayout4.getHeight() : 0.0f;
        float descent2 = this.subtitlePaint.descent() - this.subtitlePaint.ascent();
        float f5 = height3 / 2.0f;
        float descent3 = f5 - this.mTextPaint.descent();
        if (isEmpty) {
            int i5 = absoluteGravity2 & 112;
            if (i5 == 48) {
                this.mExpandedDrawY = (float) this.mExpandedBounds.top;
            } else if (i5 != 80) {
                this.mExpandedDrawY = ((float) this.mExpandedBounds.centerY()) - f5;
            } else {
                this.mExpandedDrawY = ((float) this.mExpandedBounds.bottom) - height3;
            }
        } else {
            int i6 = absoluteGravity2 & 112;
            if (i6 == 48) {
                float f6 = (float) this.mExpandedBounds.top;
                this.mExpandedDrawY = f6;
                this.expandedSubtitleY = f6 + descent2 + height3;
            } else if (i6 != 80) {
                float centerY = ((float) this.mExpandedBounds.centerY()) - f5;
                this.mExpandedDrawY = centerY;
                this.expandedSubtitleY = centerY + descent2 + descent3;
            } else {
                int i7 = this.mExpandedBounds.bottom;
                this.mExpandedDrawY = (((float) i7) - descent2) - height3;
                this.expandedSubtitleY = ((float) i7) - height3;
            }
        }
        int i8 = absoluteGravity2 & 8388615;
        if (i8 == 1) {
            this.mExpandedDrawX = ((float) this.mExpandedBounds.centerX()) - (lineWidth / 2.0f);
            this.expandedSubtitleX = (((float) this.mExpandedBounds.centerX()) - (measureText3 / 2.0f)) + ((float) (this.mInsetSubtitleImage ? dimensionPixelOffset : 0));
        } else if (i8 != 5) {
            int i9 = this.mExpandedBounds.left;
            this.mExpandedDrawX = (float) i9;
            this.expandedSubtitleX = (float) (i9 + (this.mInsetSubtitleImage ? dimensionPixelOffset : 0));
        } else {
            int i10 = this.mExpandedBounds.right;
            this.mExpandedDrawX = ((float) i10) - lineWidth;
            this.expandedSubtitleX = (((float) i10) - measureText3) + ((float) (this.mInsetSubtitleImage ? dimensionPixelOffset : 0));
        }
        clearTexture();
        setInterpolatedTextSize(f2);
        setInterpolatedSubtitleSize(f3);
    }

    private void interpolateBounds(float f) {
        this.mCurrentBounds.left = lerp((float) this.mExpandedBounds.left, (float) this.mCollapsedBounds.left, f, this.mPositionInterpolator);
        this.mCurrentBounds.top = lerp(this.mExpandedDrawY, this.mCollapsedDrawY, f, this.mPositionInterpolator);
        this.mCurrentBounds.right = lerp((float) this.mExpandedBounds.right, (float) this.mCollapsedBounds.right, f, this.mPositionInterpolator);
        this.mCurrentBounds.bottom = lerp((float) this.mExpandedBounds.bottom, (float) this.mCollapsedBounds.bottom, f, this.mPositionInterpolator);
    }

    public void draw(Canvas canvas) {
        int i;
        float f;
        float f2;
        float f3;
        float f4;
        int i2;
        float f5;
        float f6;
        float f7;
        float f8;
        float f9;
        float f10;
        float f11;
        int i3;
        float f12;
        float f13;
        float f14;
        int i4;
        Bitmap bitmap;
        int i5;
        float f15;
        int save = canvas.save();
        if (this.mTextToDraw == null || !this.mDrawTitle) {
            i = save;
        } else {
            float f16 = this.mCurrentDrawX;
            float f17 = this.mCurrentDrawY;
            float f18 = this.currentSubtitleX;
            float f19 = this.currentSubtitleY;
            boolean z = ViewCompat.getLayoutDirection(this.mView) == 1;
            if (z) {
                Rect rect = this.mExpandedBounds;
                f = ((float) (rect.right - rect.left)) - getSubtitleLocationX();
            } else {
                f = getSubtitleLocationX();
            }
            Paint.FontMetricsInt fontMetricsInt = this.subtitlePaint.getFontMetricsInt();
            int i6 = fontMetricsInt.top;
            float ascent = ((this.currentSubtitleY + ((float) (((~i6) - ((~i6) - (~fontMetricsInt.ascent))) - (fontMetricsInt.bottom - fontMetricsInt.descent)))) + (this.subtitlePaint.ascent() * this.subtitleScale)) - ((float) this.mView.getResources().getDimensionPixelOffset(R$dimen.op_control_margin_list_top2));
            boolean z2 = this.mUseTexture && this.mExpandedTitleTexture != null;
            this.mTextPaint.setTextSize(this.mCurrentTitleSize);
            if (z2) {
                f3 = 0.0f;
                f2 = 0.0f;
            } else {
                float ascent2 = this.mTextPaint.ascent() * this.mScale;
                f3 = this.subtitlePaint.ascent() * this.subtitleScale;
                f2 = ascent2;
            }
            if (z2) {
                f17 += f2;
                f19 += f3;
            }
            int save2 = canvas.save();
            if (this.mExpandedFraction != 1.0f || !this.mDrawLine) {
                f7 = f17;
                f5 = f2;
                f4 = ascent;
                i2 = save;
                f6 = 0.0f;
                f8 = f19;
            } else {
                this.subtitlePaint.setStrokeWidth((float) this.mView.getResources().getDimensionPixelSize(R$dimen.op_control_divider_height_standard));
                this.subtitlePaint.setColor(this.mView.getResources().getColor(R$color.op_control_divider_color_default));
                f4 = ascent;
                f7 = f17;
                i2 = save;
                f5 = f2;
                f6 = 0.0f;
                f8 = f19;
                canvas.drawLine((float) this.mAppbarMarginLeft, (float) (this.mView.getMeasuredHeight() - this.mAppbarMarginBottom), (float) ((this.mView.getMeasuredWidth() - this.mAppbarMarginLeft) - this.mAppbarMarginRight), (float) (this.mView.getMeasuredHeight() - this.mAppbarMarginBottom), this.subtitlePaint);
                this.subtitlePaint.setColor(getCurrentCollapsedSubtitleColor());
                canvas.restoreToCount(save2);
            }
            int save3 = canvas.save();
            if (!TextUtils.isEmpty(this.subtitle)) {
                float f20 = this.subtitleScale;
                if (f20 != 1.0f) {
                    canvas.scale(f20, f20, f18, f8);
                }
                if (z2) {
                    canvas.drawBitmap(this.expandedSubtitleTexture, f18, f8, this.subtitleTexturePaint);
                    i4 = save3;
                    f14 = f4;
                } else {
                    if (!TextUtils.isEmpty(this.mSyncText)) {
                        Rect rect2 = new Rect();
                        TextPaint textPaint = this.syncTextPaint;
                        CharSequence charSequence = this.mSyncText;
                        textPaint.getTextBounds(charSequence, 0, charSequence.length(), rect2);
                        if (z) {
                            f15 = Math.max(((float) this.mExpandedBounds.right) - this.syncTextPaint.measureText(String.valueOf(this.mSyncText)), f6);
                        } else {
                            f15 = this.subtitlePaint.measureText(String.valueOf(this.subtitleToDraw)) + f18 + 24.0f;
                        }
                        this.mSyncLeftX = f15;
                        this.mSyncRightX = f15 + this.syncTextPaint.measureText(String.valueOf(this.mSyncText));
                        float f21 = this.expandedSubtitleY;
                        int i7 = this.mExpandedBounds.top;
                        float abs = (((float) i7) + f21) - (Math.abs((f21 + ((float) i7)) - 330.0f) * this.mExpandedFraction);
                        this.mSyncTopY = abs;
                        this.mSyncBottomY = abs + ((float) rect2.height());
                        if (this.mSyncRightX < ((float) this.mView.getResources().getDisplayMetrics().widthPixels)) {
                            CharSequence charSequence2 = this.mSyncText;
                            i5 = save3;
                            f14 = f4;
                            canvas.drawText(charSequence2, 0, charSequence2.length(), this.mSyncLeftX, f8, this.syncTextPaint);
                        } else {
                            i5 = save3;
                            f14 = f4;
                            CharSequence charSequence3 = this.mSyncText;
                            canvas.drawText(charSequence3, 0, charSequence3.length(), f18, (this.subtitlePaint.descent() + f8) - this.subtitlePaint.ascent(), this.syncTextPaint);
                        }
                        CharSequence charSequence4 = this.subtitleToDraw;
                        canvas.drawText(charSequence4, 0, charSequence4.length(), z ? Math.max((((float) this.mExpandedBounds.right) - this.subtitlePaint.measureText(String.valueOf(this.subtitleToDraw))) - this.syncTextPaint.measureText(String.valueOf(this.mSyncText)), f6) : f18, f8, this.subtitlePaint);
                    } else {
                        i5 = save3;
                        f14 = f4;
                        CharSequence charSequence5 = this.subtitleToDraw;
                        canvas.drawText(charSequence5, 0, charSequence5.length(), z ? ((float) this.mExpandedBounds.right) - this.subtitlePaint.measureText(String.valueOf(this.subtitleToDraw)) : f18, f8, this.subtitlePaint);
                    }
                    i4 = i5;
                }
                canvas.restoreToCount(i4);
                if (this.mInsetSubtitleImage && (bitmap = this.mDrawBitmap) != null) {
                    canvas.drawBitmap(bitmap, f, f14, this.subtitlePaint);
                }
            }
            TextPaint textPaint2 = this.mTextPaint;
            CharSequence charSequence6 = this.mTextToDraw;
            float measureText = textPaint2.measureText(charSequence6, 0, charSequence6.length());
            Rect rect3 = this.mExpandedBounds;
            if (measureText >= ((float) (rect3.right - rect3.left))) {
                z = false;
            }
            float f22 = this.mScale;
            if (f22 != 1.0f) {
                canvas.scale(f22, f22, z ? (float) this.mExpandedBounds.right : f16, f7);
            }
            float lineLeft = (this.mCurrentDrawX + this.mTextLayout.getLineLeft(0)) - (this.mExpandedFirstLineDrawX * 2.0f);
            if (z2) {
                this.mTexturePaint.setAlpha((int) (this.mExpandedTextBlend * 255.0f));
                canvas.drawBitmap(this.mExpandedTitleTexture, lineLeft, f7, this.mTexturePaint);
                this.mTexturePaint.setAlpha((int) (this.mCollapsedTextBlend * 255.0f));
                canvas.drawBitmap(this.mCollapsedTitleTexture, f16, f7, this.mTexturePaint);
                this.mTexturePaint.setAlpha(255);
                canvas.drawBitmap(this.mCrossSectionTitleTexture, f16, f7, this.mTexturePaint);
            } else {
                Rect rect4 = this.mExpandedBounds;
                int i8 = rect4.right;
                int i9 = rect4.left;
                if (measureText < ((float) (i8 - i9)) && z) {
                    measureText = (float) ((i8 - i9) - this.mView.getResources().getDimensionPixelOffset(R$dimen.control_app_bar_ar_margin));
                }
                if (z) {
                    Rect rect5 = this.mExpandedBounds;
                    f9 = ((float) (rect5.right - rect5.left)) - measureText;
                } else {
                    f9 = lineLeft;
                }
                canvas.translate(f9, f7);
                this.mTempY = f7;
                this.mTextPaint.setAlpha((int) (this.mExpandedTextBlend * ((float) Color.alpha(getCurrentExpandedTextColor()))));
                if (!z) {
                    this.mTextLayout.draw(canvas);
                }
                if (z) {
                    float f23 = this.mExpandedDrawX;
                    Rect rect6 = this.mExpandedBounds;
                    f10 = (f23 - ((float) rect6.right)) + measureText + ((float) rect6.left);
                } else {
                    f10 = f16 - lineLeft;
                }
                canvas.translate(f10, f6);
                Rect rect7 = this.mExpandedBounds;
                if (measureText >= ((float) (rect7.right - rect7.left)) || !z) {
                    this.mTextPaint.setAlpha((int) (this.mCollapsedTextBlend * ((float) Color.alpha(getCurrentCollapsedTextColor()))));
                    CharSequence charSequence7 = this.mTextToDrawCollapsed;
                    int length = charSequence7.length();
                    if (z) {
                        Rect rect8 = this.mExpandedBounds;
                        f13 = ((float) (rect8.right - rect8.left)) - measureText;
                    } else {
                        f13 = f6;
                    }
                    f11 = f5;
                    canvas.drawText(charSequence7, 0, length, f13, (-f11) / this.mScale, this.mTextPaint);
                } else {
                    f11 = f5;
                }
                String trim = this.mTextToDrawCollapsed.toString().trim();
                if (trim.endsWith("…")) {
                    i3 = 0;
                    trim = trim.substring(0, trim.length() - 1);
                } else {
                    i3 = 0;
                }
                this.mTextPaint.setAlpha(Color.alpha(getCurrentExpandedTextColor()));
                int lineEnd = this.mTextLayout.getLineEnd(i3) <= trim.length() ? this.mTextLayout.getLineEnd(i3) : trim.length();
                if (z) {
                    Rect rect9 = this.mExpandedBounds;
                    f12 = ((float) (rect9.right - rect9.left)) - this.mTextPaint.measureText(trim, 0, trim.length());
                } else {
                    f12 = f6;
                }
                canvas.drawText(trim, 0, lineEnd, f12, (-f11) / this.mScale, (Paint) this.mTextPaint);
            }
            i = i2;
        }
        canvas.restoreToCount(i);
    }

    public void setAppbarMargin(int i, int i2, int i3, int i4) {
        this.mAppbarMarginLeft = i;
        this.mAppbarMarginRight = i2;
        this.mAppbarMarginBottom = i4;
    }

    private boolean calculateIsRtl(CharSequence charSequence) {
        boolean z = true;
        if (ViewCompat.getLayoutDirection(this.mView) != 1) {
            z = false;
        }
        return (z ? TextDirectionHeuristicsCompat.FIRSTSTRONG_RTL : TextDirectionHeuristicsCompat.FIRSTSTRONG_LTR).isRtl(charSequence, 0, charSequence.length());
    }

    private void setInterpolatedTextSize(float f) {
        calculateUsingTextSize(f);
        boolean z = USE_SCALING_TEXTURE && this.mScale != 1.0f;
        this.mUseTexture = z;
        if (z) {
            ensureExpandedTexture();
            ensureCollapsedTexture();
            ensureCrossSectionTexture();
        }
        ViewCompat.postInvalidateOnAnimation(this.mView);
    }

    private void setCollapsedTextBlend(float f) {
        this.mCollapsedTextBlend = f;
        ViewCompat.postInvalidateOnAnimation(this.mView);
    }

    private void setExpandedTextBlend(float f) {
        this.mExpandedTextBlend = f;
        ViewCompat.postInvalidateOnAnimation(this.mView);
    }

    public void setDrawLine(boolean z) {
        this.mDrawLine = z;
    }

    private boolean areTypefacesDifferent(Typeface typeface, Typeface typeface2) {
        return (typeface != null && !typeface.equals(typeface2)) || (typeface == null && typeface2 != null);
    }

    private void calculateUsingTextSize(float f) {
        boolean z;
        int i;
        float f2;
        CharSequence charSequence;
        Layout.Alignment alignment;
        boolean z2;
        if (this.mTitle != null) {
            float width = (float) this.mCollapsedBounds.width();
            float width2 = (float) this.mExpandedBounds.width();
            if (isClose(f, this.mCollapsedTextSize)) {
                f2 = this.mCollapsedTextSize;
                this.mScale = 1.0f;
                if (areTypefacesDifferent(this.mCurrentTypeface, this.mCollapsedTypeface)) {
                    this.mCurrentTypeface = this.mCollapsedTypeface;
                    z2 = true;
                } else {
                    z2 = false;
                }
                z = z2;
                width2 = width;
                i = 1;
            } else {
                float f3 = this.mExpandedTextSize;
                if (areTypefacesDifferent(this.mCurrentTypeface, this.mExpandedTypeface)) {
                    this.mCurrentTypeface = this.mExpandedTypeface;
                    z = true;
                } else {
                    z = false;
                }
                if (isClose(f, this.mExpandedTextSize)) {
                    this.mScale = 1.0f;
                } else {
                    this.mScale = f / this.mExpandedTextSize;
                }
                int i2 = (((this.mCollapsedTextSize / this.mExpandedTextSize) * width2) > width ? 1 : (((this.mCollapsedTextSize / this.mExpandedTextSize) * width2) == width ? 0 : -1));
                i = this.maxLines;
                f2 = f3;
            }
            if (width2 > 0.0f) {
                z = this.mCurrentTitleSize != f2 || this.mBoundsChanged || z;
                this.mCurrentTitleSize = f2;
                this.mBoundsChanged = false;
            }
            if (this.mTextToDraw == null || z) {
                this.mTextPaint.setTextSize(this.mCurrentTitleSize);
                this.mTextPaint.setTypeface(this.mCurrentTypeface);
                int i3 = (int) width2;
                StaticLayout staticLayout = new StaticLayout(this.mTitle, this.mTextPaint, i3, Layout.Alignment.ALIGN_NORMAL, this.lineSpacingMultiplier, this.lineSpacingExtra, false);
                if (staticLayout.getLineCount() > i) {
                    int i4 = i - 1;
                    CharSequence charSequence2 = "";
                    CharSequence subSequence = i4 > 0 ? this.mTitle.subSequence(0, staticLayout.getLineEnd(i4 - 1)) : charSequence2;
                    CharSequence subSequence2 = this.mTitle.subSequence(staticLayout.getLineStart(i4), staticLayout.getLineEnd(i4));
                    if (subSequence2.charAt(subSequence2.length() - 1) == ' ') {
                        charSequence2 = subSequence2.subSequence(subSequence2.length() - 1, subSequence2.length());
                        subSequence2 = subSequence2.subSequence(0, subSequence2.length() - 1);
                    }
                    charSequence = TextUtils.concat(subSequence, TextUtils.ellipsize(TextUtils.concat(subSequence2, "…", charSequence2), this.mTextPaint, width2, TextUtils.TruncateAt.END));
                } else {
                    charSequence = this.mTitle;
                }
                if (!TextUtils.equals(charSequence, this.mTextToDraw)) {
                    this.mTextToDraw = charSequence;
                    this.mIsRtl = calculateIsRtl(charSequence);
                }
                int i5 = this.mExpandedTextGravity & 8388615;
                if (i5 == 1) {
                    alignment = Layout.Alignment.ALIGN_CENTER;
                } else if (i5 == 5 || i5 == 8388613) {
                    alignment = Layout.Alignment.ALIGN_OPPOSITE;
                } else {
                    alignment = Layout.Alignment.ALIGN_NORMAL;
                }
                this.mTextLayout = new StaticLayout(this.mTextToDraw, this.mTextPaint, i3, alignment, this.lineSpacingMultiplier, this.lineSpacingExtra, false);
            }
        }
    }

    private void ensureExpandedTexture() {
        if (this.mExpandedTitleTexture == null && !this.mExpandedBounds.isEmpty() && !TextUtils.isEmpty(this.mTextToDraw)) {
            calculateOffsets(0.0f);
            int width = this.mTextLayout.getWidth();
            int height = this.mTextLayout.getHeight();
            if (width > 0 && height > 0) {
                this.mExpandedTitleTexture = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                this.mTextLayout.draw(new Canvas(this.mExpandedTitleTexture));
                if (this.mTexturePaint == null) {
                    this.mTexturePaint = new Paint(3);
                }
            }
        }
    }

    private void ensureCollapsedTexture() {
        if (this.mCollapsedTitleTexture == null && !this.mCollapsedBounds.isEmpty() && !TextUtils.isEmpty(this.mTextToDraw)) {
            calculateOffsets(0.0f);
            TextPaint textPaint = this.mTextPaint;
            CharSequence charSequence = this.mTextToDraw;
            int round = Math.round(textPaint.measureText(charSequence, 0, charSequence.length()));
            int round2 = Math.round(this.mTextPaint.descent() - this.mTextPaint.ascent());
            if (round > 0 || round2 > 0) {
                this.mCollapsedTitleTexture = Bitmap.createBitmap(round, round2, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(this.mCollapsedTitleTexture);
                CharSequence charSequence2 = this.mTextToDrawCollapsed;
                canvas.drawText(charSequence2, 0, charSequence2.length(), 0.0f, (-this.mTextPaint.ascent()) / this.mScale, this.mTextPaint);
                if (this.mTexturePaint == null) {
                    this.mTexturePaint = new Paint(3);
                }
            }
        }
    }

    private void ensureCrossSectionTexture() {
        if (this.mCrossSectionTitleTexture == null && !this.mCollapsedBounds.isEmpty() && !TextUtils.isEmpty(this.mTextToDraw)) {
            calculateOffsets(0.0f);
            int round = Math.round(this.mTextPaint.measureText(this.mTextToDraw, this.mTextLayout.getLineStart(0), this.mTextLayout.getLineEnd(0)));
            int round2 = Math.round(this.mTextPaint.descent() - this.mTextPaint.ascent());
            if (round > 0 || round2 > 0) {
                this.mCrossSectionTitleTexture = Bitmap.createBitmap(round, round2, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(this.mCrossSectionTitleTexture);
                String trim = this.mTextToDrawCollapsed.toString().trim();
                if (trim.endsWith("…")) {
                    trim = trim.substring(0, trim.length() - 1);
                }
                canvas.drawText(trim, 0, this.mTextLayout.getLineEnd(0) <= trim.length() ? this.mTextLayout.getLineEnd(0) : trim.length(), 0.0f, (-this.mTextPaint.ascent()) / this.mScale, (Paint) this.mTextPaint);
                if (this.mTexturePaint == null) {
                    this.mTexturePaint = new Paint(3);
                }
            }
        }
    }

    public void recalculate() {
        if (this.mView.getHeight() > 0 && this.mView.getWidth() > 0) {
            calculateBaseOffsets();
            calculateCurrentOffsets();
        }
    }

    public void setTitle(CharSequence charSequence) {
        if (charSequence == null || !charSequence.equals(this.mTitle)) {
            this.mTitle = charSequence;
            this.mTextToDraw = null;
            clearTexture();
            recalculate();
        }
    }

    public CharSequence getTitle() {
        return this.mTitle;
    }

    public ColorStateList getExpandedSubtitleColor() {
        return this.expandedSubtitleColor;
    }

    public ColorStateList getSyncColor() {
        return this.syncColorList;
    }

    public void setExpandedSubtitleAppearance(int i) {
        TextAppearance textAppearance = new TextAppearance(this.mView.getContext(), i);
        ColorStateList colorStateList = textAppearance.textColor;
        if (colorStateList != null) {
            this.expandedSubtitleColor = colorStateList;
        }
        float f = textAppearance.textSize;
        if (f != 0.0f) {
            this.expandedSubtitleSize = f;
        }
        ColorStateList colorStateList2 = textAppearance.shadowColor;
        if (colorStateList2 != null) {
            this.expandedSubtitleShadowColor = colorStateList2;
        }
        this.expandedSubtitleShadowDx = textAppearance.shadowDx;
        this.expandedSubtitleShadowDy = textAppearance.shadowDy;
        this.expandedSubtitleShadowRadius = textAppearance.shadowRadius;
        if (Build.VERSION.SDK_INT >= 16) {
            this.expandedSubtitleTypeface = readFontFamilyTypeface(i);
        }
        recalculate();
    }

    public void setCollapsedSubtitleAppearance(int i) {
        TextAppearance textAppearance = new TextAppearance(this.mView.getContext(), i);
        ColorStateList colorStateList = textAppearance.textColor;
        if (colorStateList != null) {
            this.collapsedSubtitleColor = colorStateList;
        }
        float f = textAppearance.textSize;
        if (f != 0.0f) {
            this.collapsedSubtitleSize = f;
        }
        ColorStateList colorStateList2 = textAppearance.shadowColor;
        if (colorStateList2 != null) {
            this.collapsedSubtitleShadowColor = colorStateList2;
        }
        this.collapsedSubtitleShadowDx = textAppearance.shadowDx;
        this.collapsedSubtitleShadowDy = textAppearance.shadowDy;
        this.collapsedSubtitleShadowRadius = textAppearance.shadowRadius;
        if (Build.VERSION.SDK_INT >= 16) {
            this.collapsedSubtitleTypeface = readFontFamilyTypeface(i);
        }
        recalculate();
    }

    public void setSubtitle(CharSequence charSequence) {
        if (charSequence == null || !charSequence.equals(this.subtitle)) {
            this.subtitle = charSequence;
            this.subtitleToDraw = null;
            clearTexture();
            recalculate();
        }
    }

    public CharSequence getSubtitle() {
        return this.subtitle;
    }

    private void clearTexture() {
        Bitmap bitmap = this.mExpandedTitleTexture;
        if (bitmap != null) {
            bitmap.recycle();
            this.mExpandedTitleTexture = null;
        }
        Bitmap bitmap2 = this.mCollapsedTitleTexture;
        if (bitmap2 != null) {
            bitmap2.recycle();
            this.mCollapsedTitleTexture = null;
        }
        Bitmap bitmap3 = this.mCrossSectionTitleTexture;
        if (bitmap3 != null) {
            bitmap3.recycle();
            this.mCrossSectionTitleTexture = null;
        }
        Bitmap bitmap4 = this.expandedSubtitleTexture;
        if (bitmap4 != null) {
            bitmap4.recycle();
            this.expandedSubtitleTexture = null;
        }
    }

    private static boolean isClose(float f, float f2) {
        return Math.abs(f - f2) < 0.001f;
    }

    public ColorStateList getExpandedTextColor() {
        return this.mExpandedTextColor;
    }

    private static int blendColors(int i, int i2, float f) {
        float f2 = 1.0f - f;
        return Color.argb((int) ((((float) Color.alpha(i)) * f2) + (((float) Color.alpha(i2)) * f)), (int) ((((float) Color.red(i)) * f2) + (((float) Color.red(i2)) * f)), (int) ((((float) Color.green(i)) * f2) + (((float) Color.green(i2)) * f)), (int) ((((float) Color.blue(i)) * f2) + (((float) Color.blue(i2)) * f)));
    }

    private static float lerp(float f, float f2, float f3, TimeInterpolator timeInterpolator) {
        if (timeInterpolator != null) {
            f3 = timeInterpolator.getInterpolation(f3);
        }
        return AnimationUtils.lerp(f, f2, f3);
    }

    private static boolean rectEquals(Rect rect, int i, int i2, int i3, int i4) {
        return rect.left == i && rect.top == i2 && rect.right == i3 && rect.bottom == i4;
    }

    public void setInsetImage(boolean z) {
        this.mInsetSubtitleImage = z;
    }

    public float getSubtitleLocationX() {
        return this.expandedSubtitleX - ((float) (this.mInsetSubtitleImage ? this.mView.getContext().getResources().getDimensionPixelOffset(R$dimen.op_control_margin_space4) : 0));
    }

    public void setImageDrawable(Bitmap bitmap) {
        this.mInsetSubtitleImage = true;
        this.mDrawBitmap = bitmap;
        recalculate();
    }

    public float getSyncLeftLocation() {
        return this.mSyncLeftX;
    }

    public float getSyncRightLocation() {
        return this.mSyncRightX;
    }

    public float getSyncTopLocation() {
        return this.mSyncTopY;
    }

    public float getSyncBottomLocation() {
        return this.mSyncBottomY;
    }
}
