package com.caverock.androidsvg;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;

public class SVGImageView extends ImageView {
    private static Method setLayerTypeMethod;

    public SVGImageView(Context context) {
        super(context);
        try {
            setLayerTypeMethod = View.class.getMethod("setLayerType", Integer.TYPE, Paint.class);
        } catch (NoSuchMethodException unused) {
        }
    }

    public SVGImageView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet, 0);
        try {
            setLayerTypeMethod = View.class.getMethod("setLayerType", Integer.TYPE, Paint.class);
        } catch (NoSuchMethodException unused) {
        }
        init(attributeSet, 0);
    }

    public SVGImageView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        try {
            setLayerTypeMethod = View.class.getMethod("setLayerType", Integer.TYPE, Paint.class);
        } catch (NoSuchMethodException unused) {
        }
        init(attributeSet, i);
    }

    private void init(AttributeSet attributeSet, int i) {
        TypedArray obtainStyledAttributes = getContext().getTheme().obtainStyledAttributes(attributeSet, R.styleable.SVGImageView, i, 0);
        try {
            int resourceId = obtainStyledAttributes.getResourceId(0, -1);
            if (resourceId != -1) {
                setImageResource(resourceId);
                return;
            }
            String string = obtainStyledAttributes.getString(0);
            if (internalSetImageURI(Uri.parse(string))) {
                obtainStyledAttributes.recycle();
                return;
            }
            setImageAsset(string);
            obtainStyledAttributes.recycle();
        } finally {
            obtainStyledAttributes.recycle();
        }
    }

    public void setImageResource(int i) {
        try {
            SVG fromResource = SVG.getFromResource(getContext(), i);
            setSoftwareLayerType();
            setImageDrawable(new PictureDrawable(fromResource.renderToPicture()));
        } catch (SVGParseException e) {
            Log.w("SVGImageView", "Unable to find resource: " + i, e);
        }
    }

    public void setImageURI(Uri uri) {
        internalSetImageURI(uri);
    }

    public void setImageAsset(String str) {
        try {
            SVG fromAsset = SVG.getFromAsset(getContext().getAssets(), str);
            setSoftwareLayerType();
            setImageDrawable(new PictureDrawable(fromAsset.renderToPicture()));
        } catch (Exception e) {
            Log.w("SVGImageView", "Unable to find asset file: " + str, e);
        }
    }

    private boolean internalSetImageURI(Uri uri) {
        InputStream inputStream = null;
        try {
            inputStream = getContext().getContentResolver().openInputStream(uri);
            SVG fromInputStream = SVG.getFromInputStream(inputStream);
            setSoftwareLayerType();
            setImageDrawable(new PictureDrawable(fromInputStream.renderToPicture()));
            if (inputStream == null) {
                return true;
            }
            try {
                inputStream.close();
                return true;
            } catch (IOException unused) {
                return true;
            }
        } catch (Exception e) {
            Log.w("ImageView", "Unable to open content: " + uri, e);
            if (inputStream == null) {
                return false;
            }
            try {
                inputStream.close();
                return false;
            } catch (IOException unused2) {
                return false;
            }
        } catch (Throwable th) {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException unused3) {
                }
            }
            throw th;
        }
    }

    private void setSoftwareLayerType() {
        Method method = setLayerTypeMethod;
        if (method != null) {
            try {
                method.invoke(this, 1, null);
            } catch (Exception e) {
                Log.w("SVGImageView", "Unexpected failure calling setLayerType", e);
            }
        }
    }
}
