package androidx.mediarouter.app;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.View;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.content.ContextCompat;
import androidx.mediarouter.R$drawable;
import androidx.mediarouter.R$string;

class MediaRouteExpandCollapseButton extends AppCompatImageButton {
    final AnimationDrawable mCollapseAnimationDrawable;
    final String mCollapseGroupDescription;
    final AnimationDrawable mExpandAnimationDrawable;
    final String mExpandGroupDescription;
    boolean mIsGroupExpanded;
    View.OnClickListener mListener;

    public MediaRouteExpandCollapseButton(Context context) {
        this(context, null);
    }

    public MediaRouteExpandCollapseButton(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public MediaRouteExpandCollapseButton(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mExpandAnimationDrawable = (AnimationDrawable) ContextCompat.getDrawable(context, R$drawable.mr_group_expand);
        this.mCollapseAnimationDrawable = (AnimationDrawable) ContextCompat.getDrawable(context, R$drawable.mr_group_collapse);
        PorterDuffColorFilter porterDuffColorFilter = new PorterDuffColorFilter(MediaRouterThemeHelper.getControllerColor(context, i), PorterDuff.Mode.SRC_IN);
        this.mExpandAnimationDrawable.setColorFilter(porterDuffColorFilter);
        this.mCollapseAnimationDrawable.setColorFilter(porterDuffColorFilter);
        this.mExpandGroupDescription = context.getString(R$string.mr_controller_expand_group);
        this.mCollapseGroupDescription = context.getString(R$string.mr_controller_collapse_group);
        setImageDrawable(this.mExpandAnimationDrawable.getFrame(0));
        setContentDescription(this.mExpandGroupDescription);
        super.setOnClickListener(new View.OnClickListener() {
            /* class androidx.mediarouter.app.MediaRouteExpandCollapseButton.AnonymousClass1 */

            public void onClick(View view) {
                MediaRouteExpandCollapseButton mediaRouteExpandCollapseButton = MediaRouteExpandCollapseButton.this;
                boolean z = !mediaRouteExpandCollapseButton.mIsGroupExpanded;
                mediaRouteExpandCollapseButton.mIsGroupExpanded = z;
                if (z) {
                    mediaRouteExpandCollapseButton.setImageDrawable(mediaRouteExpandCollapseButton.mExpandAnimationDrawable);
                    MediaRouteExpandCollapseButton.this.mExpandAnimationDrawable.start();
                    MediaRouteExpandCollapseButton mediaRouteExpandCollapseButton2 = MediaRouteExpandCollapseButton.this;
                    mediaRouteExpandCollapseButton2.setContentDescription(mediaRouteExpandCollapseButton2.mCollapseGroupDescription);
                } else {
                    mediaRouteExpandCollapseButton.setImageDrawable(mediaRouteExpandCollapseButton.mCollapseAnimationDrawable);
                    MediaRouteExpandCollapseButton.this.mCollapseAnimationDrawable.start();
                    MediaRouteExpandCollapseButton mediaRouteExpandCollapseButton3 = MediaRouteExpandCollapseButton.this;
                    mediaRouteExpandCollapseButton3.setContentDescription(mediaRouteExpandCollapseButton3.mExpandGroupDescription);
                }
                View.OnClickListener onClickListener = MediaRouteExpandCollapseButton.this.mListener;
                if (onClickListener != null) {
                    onClickListener.onClick(view);
                }
            }
        });
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.mListener = onClickListener;
    }
}
