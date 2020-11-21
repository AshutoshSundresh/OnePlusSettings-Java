package androidx.leanback.widget.picker;

public class PickerColumn {
    private int mCurrentValue;
    private String mLabelFormat;
    private int mMaxValue;
    private int mMinValue;
    private CharSequence[] mStaticLabels;

    public void setLabelFormat(String str) {
        this.mLabelFormat = str;
    }

    public void setStaticLabels(CharSequence[] charSequenceArr) {
        this.mStaticLabels = charSequenceArr;
    }

    public CharSequence getLabelFor(int i) {
        CharSequence[] charSequenceArr = this.mStaticLabels;
        if (charSequenceArr != null) {
            return charSequenceArr[i];
        }
        return String.format(this.mLabelFormat, Integer.valueOf(i));
    }

    public int getCurrentValue() {
        return this.mCurrentValue;
    }

    public void setCurrentValue(int i) {
        this.mCurrentValue = i;
    }

    public int getCount() {
        return (this.mMaxValue - this.mMinValue) + 1;
    }

    public int getMinValue() {
        return this.mMinValue;
    }

    public int getMaxValue() {
        return this.mMaxValue;
    }

    public void setMinValue(int i) {
        this.mMinValue = i;
    }

    public void setMaxValue(int i) {
        this.mMaxValue = i;
    }
}
