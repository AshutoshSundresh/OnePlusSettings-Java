package com.android.settingslib.notification;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.service.notification.Condition;
import android.service.notification.ZenModeConfig;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.Slog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.policy.PhoneWindow;
import com.android.settingslib.R$id;
import com.android.settingslib.R$layout;
import com.android.settingslib.R$string;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Objects;

public class EnableZenModeDialog {
    @VisibleForTesting
    protected static final int COUNTDOWN_ALARM_CONDITION_INDEX = 2;
    @VisibleForTesting
    protected static final int COUNTDOWN_CONDITION_INDEX = 1;
    private static final boolean DEBUG = Log.isLoggable("EnableZenModeDialog", 3);
    private static final int DEFAULT_BUCKET_INDEX;
    @VisibleForTesting
    protected static final int FOREVER_CONDITION_INDEX = 0;
    private static final int MAX_BUCKET_MINUTES;
    private static final int[] MINUTE_BUCKETS;
    private static final int MIN_BUCKET_MINUTES;
    private int MAX_MANUAL_DND_OPTIONS = 3;
    private AlarmManager mAlarmManager;
    private boolean mAttached;
    private int mBucketIndex = -1;
    @VisibleForTesting
    protected Context mContext;
    @VisibleForTesting
    protected Uri mForeverId;
    @VisibleForTesting
    protected LayoutInflater mLayoutInflater;
    @VisibleForTesting
    protected NotificationManager mNotificationManager;
    private int mUserId;
    @VisibleForTesting
    protected TextView mZenAlarmWarning;
    private RadioGroup mZenRadioGroup;
    @VisibleForTesting
    protected LinearLayout mZenRadioGroupContent;

    static {
        int[] iArr = ZenModeConfig.MINUTE_BUCKETS;
        MINUTE_BUCKETS = iArr;
        MIN_BUCKET_MINUTES = iArr[0];
        MAX_BUCKET_MINUTES = iArr[iArr.length - 1];
        DEFAULT_BUCKET_INDEX = Arrays.binarySearch(iArr, 60);
    }

    public EnableZenModeDialog(Context context) {
        this.mContext = context;
    }

    public Dialog createDialog() {
        this.mNotificationManager = (NotificationManager) this.mContext.getSystemService("notification");
        this.mForeverId = Condition.newId(this.mContext).appendPath("forever").build();
        this.mAlarmManager = (AlarmManager) this.mContext.getSystemService("alarm");
        this.mUserId = this.mContext.getUserId();
        this.mAttached = false;
        AlertDialog.Builder positiveButton = new AlertDialog.Builder(this.mContext).setTitle(R$string.zen_mode_settings_turn_on_dialog_title).setNegativeButton(R$string.cancel, (DialogInterface.OnClickListener) null).setPositiveButton(R$string.zen_mode_enable_dialog_turn_on, new DialogInterface.OnClickListener() {
            /* class com.android.settingslib.notification.EnableZenModeDialog.AnonymousClass1 */

            public void onClick(DialogInterface dialogInterface, int i) {
                ConditionTag conditionTagAt = EnableZenModeDialog.this.getConditionTagAt(EnableZenModeDialog.this.mZenRadioGroup.getCheckedRadioButtonId());
                if (EnableZenModeDialog.this.isForever(conditionTagAt.condition)) {
                    MetricsLogger.action(EnableZenModeDialog.this.mContext, 1259);
                } else if (EnableZenModeDialog.this.isAlarm(conditionTagAt.condition)) {
                    MetricsLogger.action(EnableZenModeDialog.this.mContext, 1261);
                } else if (EnableZenModeDialog.this.isCountdown(conditionTagAt.condition)) {
                    MetricsLogger.action(EnableZenModeDialog.this.mContext, 1260);
                } else {
                    Slog.d("EnableZenModeDialog", "Invalid manual condition: " + conditionTagAt.condition);
                }
                EnableZenModeDialog enableZenModeDialog = EnableZenModeDialog.this;
                enableZenModeDialog.mNotificationManager.setZenMode(1, enableZenModeDialog.getRealConditionId(conditionTagAt.condition), "EnableZenModeDialog");
            }
        });
        View contentView = getContentView();
        bindConditions(forever());
        positiveButton.setView(contentView);
        return positiveButton.create();
    }

    private void hideAllConditions() {
        int childCount = this.mZenRadioGroupContent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            this.mZenRadioGroupContent.getChildAt(i).setVisibility(8);
        }
        this.mZenAlarmWarning.setVisibility(8);
    }

    /* access modifiers changed from: protected */
    public View getContentView() {
        if (this.mLayoutInflater == null) {
            this.mLayoutInflater = new PhoneWindow(this.mContext).getLayoutInflater();
        }
        View inflate = this.mLayoutInflater.inflate(R$layout.zen_mode_turn_on_dialog_container, (ViewGroup) null);
        ScrollView scrollView = (ScrollView) inflate.findViewById(R$id.container);
        this.mZenRadioGroup = (RadioGroup) scrollView.findViewById(R$id.zen_radio_buttons);
        this.mZenRadioGroupContent = (LinearLayout) scrollView.findViewById(R$id.zen_radio_buttons_content);
        this.mZenAlarmWarning = (TextView) scrollView.findViewById(R$id.zen_alarm_warning);
        for (int i = 0; i < this.MAX_MANUAL_DND_OPTIONS; i++) {
            View inflate2 = this.mLayoutInflater.inflate(R$layout.zen_mode_radio_button, (ViewGroup) this.mZenRadioGroup, false);
            this.mZenRadioGroup.addView(inflate2);
            inflate2.setId(i);
            View inflate3 = this.mLayoutInflater.inflate(R$layout.zen_mode_condition, (ViewGroup) this.mZenRadioGroupContent, false);
            inflate3.setId(this.MAX_MANUAL_DND_OPTIONS + i);
            this.mZenRadioGroupContent.addView(inflate3);
        }
        hideAllConditions();
        return inflate;
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public void bind(Condition condition, View view, int i) {
        final ConditionTag conditionTag;
        if (condition != null) {
            boolean z = true;
            boolean z2 = condition.state == 1;
            if (view.getTag() != null) {
                conditionTag = (ConditionTag) view.getTag();
            } else {
                conditionTag = new ConditionTag();
            }
            view.setTag(conditionTag);
            if (conditionTag.rb != null) {
                z = false;
            }
            if (conditionTag.rb == null) {
                conditionTag.rb = (RadioButton) this.mZenRadioGroup.getChildAt(i);
            }
            conditionTag.condition = condition;
            final Uri conditionId = getConditionId(condition);
            if (DEBUG) {
                Log.d("EnableZenModeDialog", "bind i=" + this.mZenRadioGroupContent.indexOfChild(view) + " first=" + z + " condition=" + conditionId);
            }
            conditionTag.rb.setEnabled(z2);
            conditionTag.rb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                /* class com.android.settingslib.notification.EnableZenModeDialog.AnonymousClass2 */

                public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                    if (z) {
                        conditionTag.rb.setChecked(true);
                        if (EnableZenModeDialog.DEBUG) {
                            Log.d("EnableZenModeDialog", "onCheckedChanged " + conditionId);
                        }
                        MetricsLogger.action(EnableZenModeDialog.this.mContext, 164);
                        EnableZenModeDialog.this.updateAlarmWarningText(conditionTag.condition);
                    }
                }
            });
            updateUi(conditionTag, view, condition, z2, i, conditionId);
            view.setVisibility(0);
            return;
        }
        throw new IllegalArgumentException("condition must not be null");
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public ConditionTag getConditionTagAt(int i) {
        return (ConditionTag) this.mZenRadioGroupContent.getChildAt(i).getTag();
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public void bindConditions(Condition condition) {
        bind(forever(), this.mZenRadioGroupContent.getChildAt(0), 0);
        if (condition == null) {
            bindGenericCountdown();
            bindNextAlarm(getTimeUntilNextAlarmCondition());
        } else if (isForever(condition)) {
            getConditionTagAt(0).rb.setChecked(true);
            bindGenericCountdown();
            bindNextAlarm(getTimeUntilNextAlarmCondition());
        } else if (isAlarm(condition)) {
            bindGenericCountdown();
            bindNextAlarm(condition);
            getConditionTagAt(2).rb.setChecked(true);
        } else if (isCountdown(condition)) {
            bindNextAlarm(getTimeUntilNextAlarmCondition());
            bind(condition, this.mZenRadioGroupContent.getChildAt(1), 1);
            getConditionTagAt(1).rb.setChecked(true);
        } else {
            Slog.d("EnableZenModeDialog", "Invalid manual condition: " + condition);
        }
    }

    public static Uri getConditionId(Condition condition) {
        if (condition != null) {
            return condition.id;
        }
        return null;
    }

    public Condition forever() {
        return new Condition(Condition.newId(this.mContext).appendPath("forever").build(), foreverSummary(this.mContext), "", "", 0, 1, 0);
    }

    public long getNextAlarm() {
        AlarmManager.AlarmClockInfo nextAlarmClock = this.mAlarmManager.getNextAlarmClock(this.mUserId);
        if (nextAlarmClock != null) {
            return nextAlarmClock.getTriggerTime();
        }
        return 0;
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public boolean isAlarm(Condition condition) {
        return condition != null && ZenModeConfig.isValidCountdownToAlarmConditionId(condition.id);
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public boolean isCountdown(Condition condition) {
        return condition != null && ZenModeConfig.isValidCountdownConditionId(condition.id);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private boolean isForever(Condition condition) {
        return condition != null && this.mForeverId.equals(condition.id);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private Uri getRealConditionId(Condition condition) {
        if (isForever(condition)) {
            return null;
        }
        return getConditionId(condition);
    }

    private String foreverSummary(Context context) {
        return context.getString(17041530);
    }

    private static void setToMidnight(Calendar calendar) {
        calendar.set(11, 0);
        calendar.set(12, 0);
        calendar.set(13, 0);
        calendar.set(14, 0);
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public Condition getTimeUntilNextAlarmCondition() {
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        setToMidnight(gregorianCalendar);
        gregorianCalendar.add(5, 6);
        long nextAlarm = getNextAlarm();
        if (nextAlarm <= 0) {
            return null;
        }
        GregorianCalendar gregorianCalendar2 = new GregorianCalendar();
        gregorianCalendar2.setTimeInMillis(nextAlarm);
        setToMidnight(gregorianCalendar2);
        if (gregorianCalendar.compareTo((Calendar) gregorianCalendar2) >= 0) {
            return ZenModeConfig.toNextAlarmCondition(this.mContext, nextAlarm, ActivityManager.getCurrentUser());
        }
        return null;
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public void bindGenericCountdown() {
        int i = DEFAULT_BUCKET_INDEX;
        this.mBucketIndex = i;
        Condition timeCondition = ZenModeConfig.toTimeCondition(this.mContext, MINUTE_BUCKETS[i], ActivityManager.getCurrentUser());
        if (!this.mAttached || getConditionTagAt(1).condition == null) {
            bind(timeCondition, this.mZenRadioGroupContent.getChildAt(1), 1);
        }
    }

    private void updateUi(final ConditionTag conditionTag, final View view, Condition condition, boolean z, final int i, Uri uri) {
        String str;
        if (conditionTag.lines == null) {
            conditionTag.lines = view.findViewById(16908290);
        }
        if (conditionTag.line1 == null) {
            conditionTag.line1 = (TextView) view.findViewById(16908308);
        }
        if (conditionTag.line2 == null) {
            conditionTag.line2 = (TextView) view.findViewById(16908309);
        }
        if (!TextUtils.isEmpty(condition.line1)) {
            str = condition.line1;
        } else {
            str = condition.summary;
        }
        String str2 = condition.line2;
        conditionTag.line1.setText(str);
        boolean z2 = false;
        if (TextUtils.isEmpty(str2)) {
            conditionTag.line2.setVisibility(8);
        } else {
            conditionTag.line2.setVisibility(0);
            conditionTag.line2.setText(str2);
        }
        conditionTag.lines.setEnabled(z);
        float f = 1.0f;
        conditionTag.lines.setAlpha(z ? 1.0f : 0.4f);
        conditionTag.lines.setOnClickListener(new View.OnClickListener(this) {
            /* class com.android.settingslib.notification.EnableZenModeDialog.AnonymousClass3 */

            public void onClick(View view) {
                conditionTag.rb.setChecked(true);
            }
        });
        long tryParseCountdownConditionId = ZenModeConfig.tryParseCountdownConditionId(uri);
        ImageView imageView = (ImageView) view.findViewById(16908313);
        ImageView imageView2 = (ImageView) view.findViewById(16908314);
        if (i != 1 || tryParseCountdownConditionId <= 0) {
            if (imageView != null) {
                ((ViewGroup) view).removeView(imageView);
            }
            if (imageView2 != null) {
                ((ViewGroup) view).removeView(imageView2);
                return;
            }
            return;
        }
        imageView.setOnClickListener(new View.OnClickListener() {
            /* class com.android.settingslib.notification.EnableZenModeDialog.AnonymousClass4 */

            public void onClick(View view) {
                EnableZenModeDialog.this.onClickTimeButton(view, conditionTag, false, i);
                conditionTag.lines.setAccessibilityLiveRegion(1);
            }
        });
        imageView2.setOnClickListener(new View.OnClickListener() {
            /* class com.android.settingslib.notification.EnableZenModeDialog.AnonymousClass5 */

            public void onClick(View view) {
                EnableZenModeDialog.this.onClickTimeButton(view, conditionTag, true, i);
                conditionTag.lines.setAccessibilityLiveRegion(1);
            }
        });
        int i2 = this.mBucketIndex;
        if (i2 > -1) {
            imageView.setEnabled(i2 > 0);
            if (this.mBucketIndex < MINUTE_BUCKETS.length - 1) {
                z2 = true;
            }
            imageView2.setEnabled(z2);
        } else {
            if (tryParseCountdownConditionId - System.currentTimeMillis() > ((long) (MIN_BUCKET_MINUTES * 60000))) {
                z2 = true;
            }
            imageView.setEnabled(z2);
            imageView2.setEnabled(!Objects.equals(condition.summary, ZenModeConfig.toTimeCondition(this.mContext, MAX_BUCKET_MINUTES, ActivityManager.getCurrentUser()).summary));
        }
        imageView.setAlpha(imageView.isEnabled() ? 1.0f : 0.5f);
        if (!imageView2.isEnabled()) {
            f = 0.5f;
        }
        imageView2.setAlpha(f);
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public void bindNextAlarm(Condition condition) {
        View childAt = this.mZenRadioGroupContent.getChildAt(2);
        ConditionTag conditionTag = (ConditionTag) childAt.getTag();
        if (condition != null && (!this.mAttached || conditionTag == null || conditionTag.condition == null)) {
            bind(condition, childAt, 2);
        }
        ConditionTag conditionTag2 = (ConditionTag) childAt.getTag();
        int i = 0;
        boolean z = (conditionTag2 == null || conditionTag2.condition == null) ? false : true;
        this.mZenRadioGroup.getChildAt(2).setVisibility(z ? 0 : 8);
        if (!z) {
            i = 8;
        }
        childAt.setVisibility(i);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void onClickTimeButton(View view, ConditionTag conditionTag, boolean z, int i) {
        Condition condition;
        int i2;
        int i3;
        long j;
        MetricsLogger.action(this.mContext, 163, z);
        int length = MINUTE_BUCKETS.length;
        int i4 = this.mBucketIndex;
        int i5 = 0;
        int i6 = -1;
        if (i4 == -1) {
            long tryParseCountdownConditionId = ZenModeConfig.tryParseCountdownConditionId(getConditionId(conditionTag.condition));
            long currentTimeMillis = System.currentTimeMillis();
            while (true) {
                if (i5 >= length) {
                    condition = null;
                    break;
                }
                i2 = z ? i5 : (length - 1) - i5;
                i3 = MINUTE_BUCKETS[i2];
                j = currentTimeMillis + ((long) (60000 * i3));
                if ((!z || j <= tryParseCountdownConditionId) && (z || j >= tryParseCountdownConditionId)) {
                    i5++;
                }
            }
            this.mBucketIndex = i2;
            condition = ZenModeConfig.toTimeCondition(this.mContext, j, i3, ActivityManager.getCurrentUser(), false);
            if (condition == null) {
                int i7 = DEFAULT_BUCKET_INDEX;
                this.mBucketIndex = i7;
                condition = ZenModeConfig.toTimeCondition(this.mContext, MINUTE_BUCKETS[i7], ActivityManager.getCurrentUser());
            }
        } else {
            int i8 = length - 1;
            if (z) {
                i6 = 1;
            }
            int max = Math.max(0, Math.min(i8, i4 + i6));
            this.mBucketIndex = max;
            condition = ZenModeConfig.toTimeCondition(this.mContext, MINUTE_BUCKETS[max], ActivityManager.getCurrentUser());
        }
        bind(condition, view, i);
        updateAlarmWarningText(conditionTag.condition);
        conditionTag.rb.setChecked(true);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void updateAlarmWarningText(Condition condition) {
        String computeAlarmWarningText = computeAlarmWarningText(condition);
        this.mZenAlarmWarning.setText(computeAlarmWarningText);
        this.mZenAlarmWarning.setVisibility(computeAlarmWarningText == null ? 8 : 0);
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public String computeAlarmWarningText(Condition condition) {
        int i;
        if ((this.mNotificationManager.getNotificationPolicy().priorityCategories & 32) != 0) {
            return null;
        }
        long currentTimeMillis = System.currentTimeMillis();
        long nextAlarm = getNextAlarm();
        if (nextAlarm < currentTimeMillis) {
            return null;
        }
        if (condition == null || isForever(condition)) {
            i = R$string.zen_alarm_warning_indef;
        } else {
            long tryParseCountdownConditionId = ZenModeConfig.tryParseCountdownConditionId(condition.id);
            i = (tryParseCountdownConditionId <= currentTimeMillis || nextAlarm >= tryParseCountdownConditionId) ? 0 : R$string.zen_alarm_warning;
        }
        if (i == 0) {
            return null;
        }
        return this.mContext.getResources().getString(i, getTime(nextAlarm, currentTimeMillis));
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public String getTime(long j, long j2) {
        boolean z = j - j2 < 86400000;
        boolean is24HourFormat = DateFormat.is24HourFormat(this.mContext, ActivityManager.getCurrentUser());
        return this.mContext.getResources().getString(z ? R$string.alarm_template : R$string.alarm_template_far, DateFormat.format(DateFormat.getBestDateTimePattern(Locale.getDefault(), z ? is24HourFormat ? "Hm" : "hma" : is24HourFormat ? "EEEHm" : "EEEhma"), j));
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public static class ConditionTag {
        public Condition condition;
        public TextView line1;
        public TextView line2;
        public View lines;
        public RadioButton rb;

        protected ConditionTag() {
        }
    }
}
