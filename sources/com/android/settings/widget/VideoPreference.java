package com.android.settings.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.R$styleable;

public class VideoPreference extends Preference {
    boolean mAnimationAvailable;
    AnimationController mAnimationController;
    private int mAnimationId;
    private float mAspectRatio = 1.0f;
    private final Context mContext;
    private int mHeight = -2;
    private ImageView mPlayButton;
    private int mPreviewId;
    private ImageView mPreviewImage;
    private int mVectorAnimationId;
    private TextureView mVideo;

    /* access modifiers changed from: package-private */
    public interface AnimationController {
        void attachView(TextureView textureView, View view, View view2);

        int getDuration();

        int getVideoHeight();

        int getVideoWidth();

        void release();
    }

    public VideoPreference(Context context) {
        super(context);
        this.mContext = context;
        initialize(context, null);
    }

    public VideoPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mContext = context;
        initialize(context, attributeSet);
    }

    private void initialize(Context context, AttributeSet attributeSet) {
        int i;
        int i2;
        TypedArray obtainStyledAttributes = context.getTheme().obtainStyledAttributes(attributeSet, R$styleable.VideoPreference, 0, 0);
        try {
            this.mAnimationAvailable = false;
            if (this.mAnimationId == 0) {
                i = obtainStyledAttributes.getResourceId(R$styleable.VideoPreference_animation, 0);
            } else {
                i = this.mAnimationId;
            }
            this.mAnimationId = i;
            if (this.mPreviewId == 0) {
                i2 = obtainStyledAttributes.getResourceId(R$styleable.VideoPreference_preview, 0);
            } else {
                i2 = this.mPreviewId;
            }
            this.mPreviewId = i2;
            int resourceId = obtainStyledAttributes.getResourceId(R$styleable.VideoPreference_vectorAnimation, 0);
            this.mVectorAnimationId = resourceId;
            if (this.mPreviewId == 0 && this.mAnimationId == 0 && resourceId == 0) {
                setVisible(false);
                obtainStyledAttributes.recycle();
                return;
            }
            initAnimationController();
            if (this.mAnimationController == null || this.mAnimationController.getDuration() <= 0) {
                setVisible(false);
            } else {
                setVisible(true);
                setLayoutResource(C0012R$layout.video_preference);
                this.mAnimationAvailable = true;
                updateAspectRatio();
            }
            obtainStyledAttributes.recycle();
        } catch (Exception unused) {
            Log.w("VideoPreference", "Animation resource not found. Will not show animation.");
        } catch (Throwable th) {
            obtainStyledAttributes.recycle();
            throw th;
        }
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        if (this.mAnimationAvailable) {
            this.mVideo = (TextureView) preferenceViewHolder.findViewById(C0010R$id.video_texture_view);
            this.mPreviewImage = (ImageView) preferenceViewHolder.findViewById(C0010R$id.video_preview_image);
            this.mPlayButton = (ImageView) preferenceViewHolder.findViewById(C0010R$id.video_play_button);
            AspectRatioFrameLayout aspectRatioFrameLayout = (AspectRatioFrameLayout) preferenceViewHolder.findViewById(C0010R$id.video_container);
            this.mPreviewImage.setImageResource(this.mPreviewId);
            aspectRatioFrameLayout.setAspectRatio(this.mAspectRatio);
            if (this.mHeight >= -1) {
                aspectRatioFrameLayout.setLayoutParams(new LinearLayout.LayoutParams(-1, this.mHeight));
            }
            AnimationController animationController = this.mAnimationController;
            if (animationController != null) {
                animationController.attachView(this.mVideo, this.mPreviewImage, this.mPlayButton);
            }
        }
    }

    @Override // androidx.preference.Preference
    public void onDetached() {
        releaseAnimationController();
        super.onDetached();
    }

    public void onViewVisible() {
        initAnimationController();
    }

    public void onViewInvisible() {
        releaseAnimationController();
    }

    public void setVideo(int i, int i2) {
        this.mAnimationId = i;
        this.mPreviewId = i2;
        releaseAnimationController();
        initialize(this.mContext, null);
    }

    private void initAnimationController() {
        int i = this.mVectorAnimationId;
        if (i != 0) {
            this.mAnimationController = new VectorAnimationController(this.mContext, i);
            return;
        }
        int i2 = this.mAnimationId;
        if (i2 != 0) {
            MediaAnimationController mediaAnimationController = new MediaAnimationController(this.mContext, i2);
            this.mAnimationController = mediaAnimationController;
            TextureView textureView = this.mVideo;
            if (textureView != null) {
                mediaAnimationController.attachView(textureView, this.mPreviewImage, this.mPlayButton);
            }
        }
    }

    private void releaseAnimationController() {
        AnimationController animationController = this.mAnimationController;
        if (animationController != null) {
            animationController.release();
            this.mAnimationController = null;
        }
    }

    public boolean isAnimationAvailable() {
        return this.mAnimationAvailable;
    }

    public void setHeight(float f) {
        this.mHeight = (int) TypedValue.applyDimension(1, f, this.mContext.getResources().getDisplayMetrics());
    }

    /* access modifiers changed from: package-private */
    public void updateAspectRatio() {
        this.mAspectRatio = ((float) this.mAnimationController.getVideoWidth()) / ((float) this.mAnimationController.getVideoHeight());
    }
}
