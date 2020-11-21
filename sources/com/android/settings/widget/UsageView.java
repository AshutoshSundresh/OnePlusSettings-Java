package com.android.settings.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.settingslib.R$id;
import com.android.settingslib.R$layout;
import com.android.settingslib.R$styleable;
import java.util.Locale;

public class UsageView extends FrameLayout {
    private final TextView[] mBottomLabels = {(TextView) findViewById(R$id.label_start), (TextView) findViewById(R$id.label_end)};
    private final TextView[] mLabels = {(TextView) findViewById(R$id.label_bottom), (TextView) findViewById(R$id.label_middle), (TextView) findViewById(R$id.label_top)};
    private final UsageGraph mUsageGraph = ((UsageGraph) findViewById(R$id.usage_graph));

    public UsageView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        LayoutInflater.from(context).inflate(R$layout.usage_view, this);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.UsageView, 0, 0);
        if (obtainStyledAttributes.hasValue(R$styleable.UsageView_sideLabels)) {
            setSideLabels(obtainStyledAttributes.getTextArray(R$styleable.UsageView_sideLabels));
        }
        if (obtainStyledAttributes.hasValue(R$styleable.UsageView_bottomLabels)) {
            setBottomLabels(obtainStyledAttributes.getTextArray(R$styleable.UsageView_bottomLabels));
        }
        if (obtainStyledAttributes.hasValue(R$styleable.UsageView_textColor)) {
            int color = obtainStyledAttributes.getColor(R$styleable.UsageView_textColor, 0);
            for (TextView textView : this.mLabels) {
                textView.setTextColor(color);
            }
            for (TextView textView2 : this.mBottomLabels) {
                textView2.setTextColor(color);
            }
        }
        if (obtainStyledAttributes.hasValue(R$styleable.UsageView_android_gravity)) {
            int i = obtainStyledAttributes.getInt(R$styleable.UsageView_android_gravity, 0);
            if (i == 8388613) {
                LinearLayout linearLayout = (LinearLayout) findViewById(R$id.graph_label_group);
                LinearLayout linearLayout2 = (LinearLayout) findViewById(R$id.label_group);
                linearLayout.removeView(linearLayout2);
                linearLayout.addView(linearLayout2);
                linearLayout2.setGravity(8388613);
                LinearLayout linearLayout3 = (LinearLayout) findViewById(R$id.bottom_label_group);
                View findViewById = linearLayout3.findViewById(R$id.bottom_label_space);
                linearLayout3.removeView(findViewById);
                linearLayout3.addView(findViewById);
            } else if (i != 8388611) {
                throw new IllegalArgumentException("Unsupported gravity " + i);
            }
        }
        this.mUsageGraph.setAccentColor(obtainStyledAttributes.getColor(R$styleable.UsageView_android_colorAccent, 0));
        obtainStyledAttributes.recycle();
        String language = Locale.getDefault().getLanguage();
        if (TextUtils.equals(language, new Locale("fa").getLanguage()) || TextUtils.equals(language, new Locale("ur").getLanguage())) {
            findViewById(R$id.graph_label_group).setLayoutDirection(0);
            findViewById(R$id.bottom_label_group).setLayoutDirection(0);
        }
    }

    public void clearPaths() {
        this.mUsageGraph.clearPaths();
    }

    public void addPath(SparseIntArray sparseIntArray) {
        this.mUsageGraph.addPath(sparseIntArray);
    }

    public void addProjectedPath(SparseIntArray sparseIntArray) {
        this.mUsageGraph.addProjectedPath(sparseIntArray);
    }

    public void configureGraph(int i, int i2) {
        this.mUsageGraph.setMax(i, i2);
    }

    public void setAccentColor(int i) {
        this.mUsageGraph.setAccentColor(i);
    }

    public void setDividerLoc(int i) {
        this.mUsageGraph.setDividerLoc(i);
    }

    public void setDividerColors(int i, int i2) {
        this.mUsageGraph.setDividerColors(i, i2);
    }

    public void setSideLabelWeights(float f, float f2) {
        setWeight(R$id.space1, f);
        setWeight(R$id.space2, f2);
    }

    private void setWeight(int i, float f) {
        View findViewById = findViewById(i);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) findViewById.getLayoutParams();
        layoutParams.weight = f;
        findViewById.setLayoutParams(layoutParams);
    }

    public void setSideLabels(CharSequence[] charSequenceArr) {
        if (charSequenceArr.length == this.mLabels.length) {
            int i = 0;
            while (true) {
                TextView[] textViewArr = this.mLabels;
                if (i < textViewArr.length) {
                    textViewArr[i].setText(charSequenceArr[i]);
                    i++;
                } else {
                    return;
                }
            }
        } else {
            throw new IllegalArgumentException("Invalid number of labels");
        }
    }

    public void setBottomLabels(CharSequence[] charSequenceArr) {
        if (charSequenceArr.length == this.mBottomLabels.length) {
            int i = 0;
            while (true) {
                TextView[] textViewArr = this.mBottomLabels;
                if (i < textViewArr.length) {
                    textViewArr[i].setText(charSequenceArr[i]);
                    i++;
                } else {
                    return;
                }
            }
        } else {
            throw new IllegalArgumentException("Invalid number of labels");
        }
    }
}
