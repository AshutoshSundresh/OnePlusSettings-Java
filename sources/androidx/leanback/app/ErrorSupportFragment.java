package androidx.leanback.app;

import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.leanback.R$color;
import androidx.leanback.R$dimen;
import androidx.leanback.R$id;
import androidx.leanback.R$layout;

public class ErrorSupportFragment extends BrandedSupportFragment {
    private Drawable mBackgroundDrawable;
    private Button mButton;
    private View.OnClickListener mButtonClickListener;
    private String mButtonText;
    private Drawable mDrawable;
    private ViewGroup mErrorFrame;
    private ImageView mImageView;
    private boolean mIsBackgroundTranslucent = true;
    private CharSequence mMessage;
    private TextView mTextView;

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R$layout.lb_error_fragment, viewGroup, false);
        this.mErrorFrame = (ViewGroup) inflate.findViewById(R$id.error_frame);
        updateBackground();
        installTitleView(layoutInflater, this.mErrorFrame, bundle);
        this.mImageView = (ImageView) inflate.findViewById(R$id.image);
        updateImageDrawable();
        this.mTextView = (TextView) inflate.findViewById(R$id.message);
        updateMessage();
        this.mButton = (Button) inflate.findViewById(R$id.button);
        updateButton();
        Paint.FontMetricsInt fontMetricsInt = getFontMetricsInt(this.mTextView);
        setTopMargin(this.mTextView, viewGroup.getResources().getDimensionPixelSize(R$dimen.lb_error_under_image_baseline_margin) + fontMetricsInt.ascent);
        setTopMargin(this.mButton, viewGroup.getResources().getDimensionPixelSize(R$dimen.lb_error_under_message_baseline_margin) - fontMetricsInt.descent);
        return inflate;
    }

    private void updateBackground() {
        int i;
        ViewGroup viewGroup = this.mErrorFrame;
        if (viewGroup != null) {
            Drawable drawable = this.mBackgroundDrawable;
            if (drawable != null) {
                viewGroup.setBackground(drawable);
                return;
            }
            Resources resources = viewGroup.getResources();
            if (this.mIsBackgroundTranslucent) {
                i = R$color.lb_error_background_color_translucent;
            } else {
                i = R$color.lb_error_background_color_opaque;
            }
            viewGroup.setBackgroundColor(resources.getColor(i));
        }
    }

    private void updateMessage() {
        TextView textView = this.mTextView;
        if (textView != null) {
            textView.setText(this.mMessage);
            this.mTextView.setVisibility(TextUtils.isEmpty(this.mMessage) ? 8 : 0);
        }
    }

    private void updateImageDrawable() {
        ImageView imageView = this.mImageView;
        if (imageView != null) {
            imageView.setImageDrawable(this.mDrawable);
            this.mImageView.setVisibility(this.mDrawable == null ? 8 : 0);
        }
    }

    private void updateButton() {
        Button button = this.mButton;
        if (button != null) {
            button.setText(this.mButtonText);
            this.mButton.setOnClickListener(this.mButtonClickListener);
            this.mButton.setVisibility(TextUtils.isEmpty(this.mButtonText) ? 8 : 0);
            this.mButton.requestFocus();
        }
    }

    @Override // androidx.leanback.app.BrandedSupportFragment, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        this.mErrorFrame.requestFocus();
    }

    private static Paint.FontMetricsInt getFontMetricsInt(TextView textView) {
        Paint paint = new Paint(1);
        paint.setTextSize(textView.getTextSize());
        paint.setTypeface(textView.getTypeface());
        return paint.getFontMetricsInt();
    }

    private static void setTopMargin(TextView textView, int i) {
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) textView.getLayoutParams();
        marginLayoutParams.topMargin = i;
        textView.setLayoutParams(marginLayoutParams);
    }
}
