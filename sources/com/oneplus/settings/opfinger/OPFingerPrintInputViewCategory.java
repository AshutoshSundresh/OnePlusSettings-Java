package com.oneplus.settings.opfinger;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.C0010R$id;

public class OPFingerPrintInputViewCategory extends PreferenceCategory {
    private Button mOPFingerInputCompletedComfirmBtn;
    public OnOPFingerComfirmListener mOnOPFingerComfirmListener;

    public interface OnOPFingerComfirmListener {
        void onOPFingerComfirmClick();
    }

    private void initViews(Context context) {
    }

    public OPFingerPrintInputViewCategory(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        new Handler();
        initViews(context);
    }

    public OPFingerPrintInputViewCategory(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        new Handler();
        initViews(context);
    }

    @Override // androidx.preference.PreferenceCategory, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        OPFingerPrintRecognitionView oPFingerPrintRecognitionView = (OPFingerPrintRecognitionView) preferenceViewHolder.findViewById(C0010R$id.op_finger_recognition_view);
        OPFingerPrintRecognitionContinueView oPFingerPrintRecognitionContinueView = (OPFingerPrintRecognitionContinueView) preferenceViewHolder.findViewById(C0010R$id.op_finger_recognition_continue_view);
        TextView textView = (TextView) preferenceViewHolder.findViewById(C0010R$id.opfinger_input_tips_title_tv);
        TextView textView2 = (TextView) preferenceViewHolder.findViewById(C0010R$id.opfinger_input_tips_subtitle_tv);
        Button button = (Button) preferenceViewHolder.findViewById(C0010R$id.opfinger_input_completed_comfirm_btn);
        this.mOPFingerInputCompletedComfirmBtn = button;
        button.setOnClickListener(new View.OnClickListener() {
            /* class com.oneplus.settings.opfinger.OPFingerPrintInputViewCategory.AnonymousClass1 */

            public void onClick(View view) {
                OnOPFingerComfirmListener onOPFingerComfirmListener = OPFingerPrintInputViewCategory.this.mOnOPFingerComfirmListener;
                if (onOPFingerComfirmListener != null) {
                    onOPFingerComfirmListener.onOPFingerComfirmClick();
                }
            }
        });
    }
}
