package androidx.leanback.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.leanback.R$id;
import androidx.leanback.R$layout;

/* access modifiers changed from: package-private */
public final class RowContainerView extends LinearLayout {
    private Drawable mForeground;
    private boolean mForegroundBoundsChanged;
    private ViewGroup mHeaderDock;

    public boolean hasOverlappingRendering() {
        return false;
    }

    public RowContainerView(Context context) {
        this(context, null, 0);
    }

    public RowContainerView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public RowContainerView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mForegroundBoundsChanged = true;
        setOrientation(1);
        LayoutInflater.from(context).inflate(R$layout.lb_row_container, this);
        this.mHeaderDock = (ViewGroup) findViewById(R$id.lb_row_container_header_dock);
        setLayoutParams(new LinearLayout.LayoutParams(-2, -2));
    }

    public void addHeaderView(View view) {
        if (this.mHeaderDock.indexOfChild(view) < 0) {
            this.mHeaderDock.addView(view, 0);
        }
    }

    public void addRowView(View view) {
        addView(view);
    }

    public void showHeader(boolean z) {
        this.mHeaderDock.setVisibility(z ? 0 : 8);
    }

    public void setForeground(Drawable drawable) {
        this.mForeground = drawable;
        setWillNotDraw(drawable == null);
        invalidate();
    }

    public void setForegroundColor(int i) {
        Drawable drawable = this.mForeground;
        if (drawable instanceof ColorDrawable) {
            ((ColorDrawable) drawable.mutate()).setColor(i);
            invalidate();
            return;
        }
        setForeground(new ColorDrawable(i));
    }

    public Drawable getForeground() {
        return this.mForeground;
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        this.mForegroundBoundsChanged = true;
    }

    public void draw(Canvas canvas) {
        super.draw(canvas);
        Drawable drawable = this.mForeground;
        if (drawable != null) {
            if (this.mForegroundBoundsChanged) {
                this.mForegroundBoundsChanged = false;
                drawable.setBounds(0, 0, getWidth(), getHeight());
            }
            this.mForeground.draw(canvas);
        }
    }
}
