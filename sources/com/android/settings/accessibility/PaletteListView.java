package com.android.settings.accessibility;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.android.settings.C0003R$array;
import com.android.settings.C0006R$color;
import com.android.settings.C0007R$dimen;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.google.common.collect.Iterables;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PaletteListView extends ListView {
    private final Context mContext;
    private final int mDefaultGradientColor;
    private final String mDefaultGradientColorCodeString;
    private final DisplayAdapter mDisplayAdapter;
    private final LayoutInflater mLayoutInflater;
    private float mTextBound;

    public PaletteListView(Context context) {
        this(context, null);
    }

    public PaletteListView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public PaletteListView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mContext = context;
        this.mDisplayAdapter = new DisplayAdapter();
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mDefaultGradientColorCodeString = getResources().getString(C0006R$color.palette_list_gradient_background);
        this.mDefaultGradientColor = getResources().getColor(C0006R$color.palette_list_gradient_background, null);
        this.mTextBound = 0.0f;
        init();
    }

    private static int getScreenWidth(WindowManager windowManager) {
        Display defaultDisplay = windowManager.getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        defaultDisplay.getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    private void init() {
        TypedArray obtainTypedArray = getResources().obtainTypedArray(C0003R$array.setting_palette_colors);
        TypedArray obtainTypedArray2 = getResources().obtainTypedArray(C0003R$array.setting_palette_data);
        int length = obtainTypedArray.length();
        ArrayList arrayList = new ArrayList();
        computeTextWidthBounds(obtainTypedArray);
        for (int i = 0; i < length; i++) {
            arrayList.add(new ColorAttributes(obtainTypedArray.getString(i), obtainTypedArray2.getColor(i, this.mDefaultGradientColor), this.mTextBound, new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, null)));
        }
        this.mDisplayAdapter.setColorList(arrayList);
        setAdapter((ListAdapter) this.mDisplayAdapter);
        setDividerHeight(0);
    }

    /* access modifiers changed from: package-private */
    public boolean setPaletteListColors(String[] strArr, String[] strArr2) {
        if (strArr == null || strArr2 == null) {
            return false;
        }
        int length = strArr.length;
        int length2 = strArr2.length;
        ArrayList arrayList = new ArrayList();
        String[] fillColorCodeArray = fillColorCodeArray(strArr2, length, length2);
        computeTextWidthBounds(strArr);
        for (int i = 0; i < length; i++) {
            arrayList.add(new ColorAttributes(strArr[i], Color.parseColor(fillColorCodeArray[i]), this.mTextBound, new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, null)));
        }
        this.mDisplayAdapter.setColorList(arrayList);
        this.mDisplayAdapter.notifyDataSetChanged();
        return true;
    }

    private String[] fillColorCodeArray(String[] strArr, int i, int i2) {
        if (i == i2 || i < i2) {
            return strArr;
        }
        String[] strArr2 = new String[i];
        for (int i3 = 0; i3 < i; i3++) {
            if (i3 < i2) {
                strArr2[i3] = strArr[i3];
            } else {
                strArr2[i3] = this.mDefaultGradientColorCodeString;
            }
        }
        return strArr2;
    }

    private void computeTextWidthBounds(TypedArray typedArray) {
        int length = typedArray.length();
        String[] strArr = new String[length];
        for (int i = 0; i < length; i++) {
            strArr[i] = typedArray.getString(i);
        }
        measureBound(strArr);
    }

    private void computeTextWidthBounds(String[] strArr) {
        int length = strArr.length;
        String[] strArr2 = new String[length];
        for (int i = 0; i < length; i++) {
            strArr2[i] = strArr[i];
        }
        measureBound(strArr2);
    }

    private void measureBound(String[] strArr) {
        WindowManager windowManager = (WindowManager) this.mContext.getSystemService("window");
        TextView textView = (TextView) this.mLayoutInflater.inflate(C0012R$layout.palette_listview_item, (ViewGroup) null).findViewById(C0010R$id.item_textview);
        ArrayList arrayList = new ArrayList(Arrays.asList(strArr));
        Collections.sort(arrayList, Comparator.comparing($$Lambda$GVSGuO0C5SbPLkB7734vFiO79E.INSTANCE));
        textView.setText((CharSequence) Iterables.getLast(arrayList));
        float round = ((float) Math.round((getResources().getDimension(C0007R$dimen.accessibility_layout_margin_start_end) / ((float) getScreenWidth(windowManager))) * 100.0f)) / 100.0f;
        this.mTextBound = (((float) Math.round((textView.getPaint().measureText(textView.getText().toString()) / ((float) getScreenWidth(windowManager))) * 100.0f)) / 100.0f) + round + round;
    }

    private static class ViewHolder {
        public TextView textView;

        private ViewHolder() {
        }
    }

    /* access modifiers changed from: private */
    public final class DisplayAdapter extends BaseAdapter {
        private List<ColorAttributes> mColorList;

        public long getItemId(int i) {
            return (long) i;
        }

        private DisplayAdapter() {
        }

        public int getCount() {
            return this.mColorList.size();
        }

        public Object getItem(int i) {
            return this.mColorList.get(i);
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            ColorAttributes colorAttributes = this.mColorList.get(i);
            String colorName = colorAttributes.getColorName();
            GradientDrawable gradientDrawable = colorAttributes.getGradientDrawable();
            if (view == null) {
                view = PaletteListView.this.mLayoutInflater.inflate(C0012R$layout.palette_listview_item, (ViewGroup) null);
                viewHolder = new ViewHolder();
                viewHolder.textView = (TextView) view.findViewById(C0010R$id.item_textview);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            viewHolder.textView.setText(colorName);
            viewHolder.textView.setBackground(gradientDrawable);
            return view;
        }

        /* access modifiers changed from: protected */
        public void setColorList(List<ColorAttributes> list) {
            this.mColorList = list;
        }
    }

    /* access modifiers changed from: private */
    public final class ColorAttributes {
        private final String mColorName;
        private final int[] mGradientColors;
        private final GradientDrawable mGradientDrawable;
        private final float[] mGradientOffsets;

        ColorAttributes(String str, int i, float f, GradientDrawable gradientDrawable) {
            int[] iArr = {PaletteListView.this.mDefaultGradientColor, PaletteListView.this.mDefaultGradientColor, 0};
            this.mGradientColors = iArr;
            float[] fArr = {0.0f, 0.5f, 1.0f};
            this.mGradientOffsets = fArr;
            iArr[2] = i;
            fArr[1] = f;
            gradientDrawable.setColors(iArr, fArr);
            this.mColorName = str;
            this.mGradientDrawable = gradientDrawable;
        }

        public String getColorName() {
            return this.mColorName;
        }

        public GradientDrawable getGradientDrawable() {
            return this.mGradientDrawable;
        }
    }
}
