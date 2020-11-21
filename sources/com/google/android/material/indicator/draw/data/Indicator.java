package com.google.android.material.indicator.draw.data;

import com.google.android.material.indicator.animation.type.AnimationType;

public class Indicator {
    private long animationDuration;
    private AnimationType animationType;
    private boolean autoVisibility;
    private int count = 3;
    private boolean dynamicCount;
    private boolean fadeOnIdle;
    private int height;
    private long idleDuration;
    private boolean interactiveAnimation;
    private int lastSelectedPosition;
    private Orientation orientation;
    private int padding;
    private int paddingBottom;
    private int paddingLeft;
    private int paddingRight;
    private int paddingTop;
    private int radius;
    private RtlMode rtlMode;
    private int selectedColor;
    private int selectedPosition;
    private int selectingPosition;
    private int stroke;
    private int unselectedColor;
    private int width;

    public void setIdle(boolean z) {
    }

    public void setViewPagerId(int i) {
    }

    public int getHeight() {
        return this.height;
    }

    public void setHeight(int i) {
        this.height = i;
    }

    public int getWidth() {
        return this.width;
    }

    public void setWidth(int i) {
        this.width = i;
    }

    public int getRadius() {
        return this.radius;
    }

    public void setRadius(int i) {
        this.radius = i;
    }

    public int getPadding() {
        return this.padding;
    }

    public void setPadding(int i) {
        this.padding = i;
    }

    public int getPaddingLeft() {
        return this.paddingLeft;
    }

    public void setPaddingLeft(int i) {
        this.paddingLeft = i;
    }

    public int getPaddingTop() {
        return this.paddingTop;
    }

    public void setPaddingTop(int i) {
        this.paddingTop = i;
    }

    public int getPaddingRight() {
        return this.paddingRight;
    }

    public void setPaddingRight(int i) {
        this.paddingRight = i;
    }

    public int getPaddingBottom() {
        return this.paddingBottom;
    }

    public void setPaddingBottom(int i) {
        this.paddingBottom = i;
    }

    public int getStroke() {
        return this.stroke;
    }

    public void setStroke(int i) {
        this.stroke = i;
    }

    public int getUnselectedColor() {
        return this.unselectedColor;
    }

    public void setUnselectedColor(int i) {
        this.unselectedColor = i;
    }

    public int getSelectedColor() {
        return this.selectedColor;
    }

    public void setSelectedColor(int i) {
        this.selectedColor = i;
    }

    public boolean isInteractiveAnimation() {
        return this.interactiveAnimation;
    }

    public void setInteractiveAnimation(boolean z) {
        this.interactiveAnimation = z;
    }

    public boolean isAutoVisibility() {
        return this.autoVisibility;
    }

    public void setAutoVisibility(boolean z) {
        this.autoVisibility = z;
    }

    public boolean isDynamicCount() {
        return this.dynamicCount;
    }

    public void setDynamicCount(boolean z) {
        this.dynamicCount = z;
    }

    public boolean isFadeOnIdle() {
        return this.fadeOnIdle;
    }

    public void setFadeOnIdle(boolean z) {
        this.fadeOnIdle = z;
    }

    public long getIdleDuration() {
        return this.idleDuration;
    }

    public void setIdleDuration(long j) {
        this.idleDuration = j;
    }

    public long getAnimationDuration() {
        return this.animationDuration;
    }

    public void setAnimationDuration(long j) {
        this.animationDuration = j;
    }

    public int getCount() {
        return this.count;
    }

    public void setCount(int i) {
        this.count = i;
    }

    public int getSelectedPosition() {
        return this.selectedPosition;
    }

    public void setSelectedPosition(int i) {
        this.selectedPosition = i;
    }

    public int getSelectingPosition() {
        return this.selectingPosition;
    }

    public void setSelectingPosition(int i) {
        this.selectingPosition = i;
    }

    public int getLastSelectedPosition() {
        return this.lastSelectedPosition;
    }

    public void setLastSelectedPosition(int i) {
        this.lastSelectedPosition = i;
    }

    public Orientation getOrientation() {
        if (this.orientation == null) {
            this.orientation = Orientation.HORIZONTAL;
        }
        return this.orientation;
    }

    public void setOrientation(Orientation orientation2) {
        this.orientation = orientation2;
    }

    public AnimationType getAnimationType() {
        if (this.animationType == null) {
            this.animationType = AnimationType.NONE;
        }
        return this.animationType;
    }

    public void setAnimationType(AnimationType animationType2) {
        this.animationType = animationType2;
    }

    public RtlMode getRtlMode() {
        if (this.rtlMode == null) {
            this.rtlMode = RtlMode.Off;
        }
        return this.rtlMode;
    }

    public void setRtlMode(RtlMode rtlMode2) {
        this.rtlMode = rtlMode2;
    }
}
