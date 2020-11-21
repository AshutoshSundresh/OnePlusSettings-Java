package com.google.android.setupcompat.internal;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import androidx.annotation.Keep;
import com.google.android.setupcompat.R$attr;
import com.google.android.setupcompat.R$styleable;
import com.google.android.setupcompat.template.Mixin;
import java.util.HashMap;
import java.util.Map;

public class TemplateLayout extends FrameLayout {
    private ViewGroup container;
    private final Map<Class<? extends Mixin>, Mixin> mixins = new HashMap();
    private ViewTreeObserver.OnPreDrawListener preDrawListener;
    private float xFraction;

    /* access modifiers changed from: protected */
    public void onBeforeTemplateInflated(AttributeSet attributeSet, int i) {
    }

    /* access modifiers changed from: protected */
    public void onTemplateInflated() {
    }

    public TemplateLayout(Context context, int i, int i2) {
        super(context);
        init(i, i2, null, R$attr.sucLayoutTheme);
    }

    public TemplateLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(0, 0, attributeSet, R$attr.sucLayoutTheme);
    }

    @TargetApi(11)
    public TemplateLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init(0, 0, attributeSet, i);
    }

    private void init(int i, int i2, AttributeSet attributeSet, int i3) {
        TypedArray obtainStyledAttributes = getContext().obtainStyledAttributes(attributeSet, R$styleable.SucTemplateLayout, i3, 0);
        if (i == 0) {
            i = obtainStyledAttributes.getResourceId(R$styleable.SucTemplateLayout_android_layout, 0);
        }
        if (i2 == 0) {
            i2 = obtainStyledAttributes.getResourceId(R$styleable.SucTemplateLayout_sucContainer, 0);
        }
        onBeforeTemplateInflated(attributeSet, i3);
        inflateTemplate(i, i2);
        obtainStyledAttributes.recycle();
    }

    /* access modifiers changed from: protected */
    public <M extends Mixin> void registerMixin(Class<M> cls, M m) {
        this.mixins.put(cls, m);
    }

    public <T extends View> T findManagedViewById(int i) {
        return (T) findViewById(i);
    }

    public <M extends Mixin> M getMixin(Class<M> cls) {
        return (M) this.mixins.get(cls);
    }

    @Override // android.view.ViewGroup
    public void addView(View view, int i, ViewGroup.LayoutParams layoutParams) {
        this.container.addView(view, i, layoutParams);
    }

    private void addViewInternal(View view) {
        super.addView(view, -1, generateDefaultLayoutParams());
    }

    private void inflateTemplate(int i, int i2) {
        addViewInternal(onInflateTemplate(LayoutInflater.from(getContext()), i));
        ViewGroup findContainer = findContainer(i2);
        this.container = findContainer;
        if (findContainer != null) {
            onTemplateInflated();
            return;
        }
        throw new IllegalArgumentException("Container cannot be null in TemplateLayout");
    }

    /* access modifiers changed from: protected */
    public final View inflateTemplate(LayoutInflater layoutInflater, int i, int i2) {
        if (i2 != 0) {
            if (i != 0) {
                layoutInflater = LayoutInflater.from(new FallbackThemeWrapper(layoutInflater.getContext(), i));
            }
            return layoutInflater.inflate(i2, (ViewGroup) this, false);
        }
        throw new IllegalArgumentException("android:layout not specified for TemplateLayout");
    }

    /* access modifiers changed from: protected */
    public View onInflateTemplate(LayoutInflater layoutInflater, int i) {
        return inflateTemplate(layoutInflater, 0, i);
    }

    /* access modifiers changed from: protected */
    public ViewGroup findContainer(int i) {
        return (ViewGroup) findViewById(i);
    }

    @Keep
    @TargetApi(11)
    public void setXFraction(float f) {
        this.xFraction = f;
        int width = getWidth();
        if (width != 0) {
            setTranslationX(((float) width) * f);
        } else if (this.preDrawListener == null) {
            this.preDrawListener = new ViewTreeObserver.OnPreDrawListener() {
                /* class com.google.android.setupcompat.internal.TemplateLayout.AnonymousClass1 */

                public boolean onPreDraw() {
                    TemplateLayout.this.getViewTreeObserver().removeOnPreDrawListener(TemplateLayout.this.preDrawListener);
                    TemplateLayout templateLayout = TemplateLayout.this;
                    templateLayout.setXFraction(templateLayout.xFraction);
                    return true;
                }
            };
            getViewTreeObserver().addOnPreDrawListener(this.preDrawListener);
        }
    }

    @Keep
    @TargetApi(11)
    public float getXFraction() {
        return this.xFraction;
    }
}
