package com.android.settings.notification.zen;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import com.android.settings.C0007R$dimen;
import com.android.settings.C0012R$layout;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;

public class ZenModeScheduleDaysSelection extends ScrollView {
    private final SimpleDateFormat mDayFormat = new SimpleDateFormat("EEEE");
    private final SparseBooleanArray mDays = new SparseBooleanArray();
    private final LinearLayout mLayout = new LinearLayout(((ScrollView) this).mContext);

    /* access modifiers changed from: protected */
    public void onChanged(int[] iArr) {
    }

    public ZenModeScheduleDaysSelection(Context context, int[] iArr) {
        super(context);
        int dimensionPixelSize = context.getResources().getDimensionPixelSize(C0007R$dimen.zen_schedule_day_margin);
        this.mLayout.setPadding(dimensionPixelSize, 0, dimensionPixelSize, 0);
        addView(this.mLayout);
        if (iArr != null) {
            for (int i : iArr) {
                this.mDays.put(i, true);
            }
        }
        this.mLayout.setOrientation(1);
        Calendar instance = Calendar.getInstance();
        int[] daysOfWeekForLocale = getDaysOfWeekForLocale(instance);
        LayoutInflater from = LayoutInflater.from(context);
        for (final int i2 : daysOfWeekForLocale) {
            CheckBox checkBox = (CheckBox) from.inflate(C0012R$layout.zen_schedule_rule_day, (ViewGroup) this, false);
            instance.set(7, i2);
            checkBox.setText(this.mDayFormat.format(instance.getTime()));
            checkBox.setChecked(this.mDays.get(i2));
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                /* class com.android.settings.notification.zen.ZenModeScheduleDaysSelection.AnonymousClass1 */

                public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                    ZenModeScheduleDaysSelection.this.mDays.put(i2, z);
                    ZenModeScheduleDaysSelection zenModeScheduleDaysSelection = ZenModeScheduleDaysSelection.this;
                    zenModeScheduleDaysSelection.onChanged(zenModeScheduleDaysSelection.getDays());
                }
            });
            this.mLayout.addView(checkBox);
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private int[] getDays() {
        SparseBooleanArray sparseBooleanArray = new SparseBooleanArray(this.mDays.size());
        for (int i = 0; i < this.mDays.size(); i++) {
            int keyAt = this.mDays.keyAt(i);
            if (this.mDays.valueAt(i)) {
                sparseBooleanArray.put(keyAt, true);
            }
        }
        int size = sparseBooleanArray.size();
        int[] iArr = new int[size];
        for (int i2 = 0; i2 < size; i2++) {
            iArr[i2] = sparseBooleanArray.keyAt(i2);
        }
        Arrays.sort(iArr);
        return iArr;
    }

    protected static int[] getDaysOfWeekForLocale(Calendar calendar) {
        int[] iArr = new int[7];
        int firstDayOfWeek = calendar.getFirstDayOfWeek();
        for (int i = 0; i < 7; i++) {
            if (firstDayOfWeek > 7) {
                firstDayOfWeek = 1;
            }
            iArr[i] = firstDayOfWeek;
            firstDayOfWeek++;
        }
        return iArr;
    }
}
