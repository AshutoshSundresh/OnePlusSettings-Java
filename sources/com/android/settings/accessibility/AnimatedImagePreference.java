package com.android.settings.accessibility;

import android.content.Context;
import android.graphics.drawable.AnimatedImageDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;

public class AnimatedImagePreference extends Preference {
    private Uri mImageUri;
    private int mMaxHeight = -1;

    AnimatedImagePreference(Context context) {
        super(context);
        setLayoutResource(C0012R$layout.preference_animated_image);
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        ImageView imageView = (ImageView) preferenceViewHolder.itemView.findViewById(C0010R$id.animated_img);
        if (imageView != null) {
            Uri uri = this.mImageUri;
            if (uri != null) {
                imageView.setImageURI(uri);
                Drawable drawable = imageView.getDrawable();
                if (drawable instanceof AnimatedImageDrawable) {
                    ((AnimatedImageDrawable) drawable).start();
                }
            }
            int i = this.mMaxHeight;
            if (i > -1) {
                imageView.setMaxHeight(i);
            }
        }
    }

    public void setImageUri(Uri uri) {
        if (uri != null && !uri.equals(this.mImageUri)) {
            this.mImageUri = uri;
            notifyChanged();
        }
    }

    public void setMaxHeight(int i) {
        if (i != this.mMaxHeight) {
            this.mMaxHeight = i;
            notifyChanged();
        }
    }
}
