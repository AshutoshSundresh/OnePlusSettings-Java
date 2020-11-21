package com.google.android.material.picker.calendar;

public class OneplusLunarCalendar {
    private boolean leapMonthFlag;
    private int lunarDay;
    private int lunarMonth;
    private int lunarYear;

    public OneplusLunarCalendar(int i, int i2, int i3, boolean z) {
        this.lunarYear = i;
        this.lunarMonth = i2;
        this.lunarDay = i3;
        this.leapMonthFlag = z;
    }

    public String getYYMMDD() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.lunarYear);
        sb.append("年");
        if (this.leapMonthFlag) {
            sb.append("闰");
        }
        sb.append(this.lunarMonth);
        sb.append("月");
        sb.append(this.lunarDay);
        sb.append("日");
        return sb.toString();
    }
}
