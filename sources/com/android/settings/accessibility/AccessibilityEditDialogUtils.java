package com.android.settings.accessibility;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;

public class AccessibilityEditDialogUtils {
    public static AlertDialog showEditShortcutDialog(Context context, CharSequence charSequence, DialogInterface.OnClickListener onClickListener) {
        AlertDialog createDialog = createDialog(context, 0, charSequence, onClickListener);
        createDialog.show();
        setScrollIndicators(createDialog);
        return createDialog;
    }

    public static AlertDialog showMagnificationEditShortcutDialog(Context context, CharSequence charSequence, DialogInterface.OnClickListener onClickListener) {
        AlertDialog createDialog = createDialog(context, 1, charSequence, onClickListener);
        createDialog.show();
        setScrollIndicators(createDialog);
        return createDialog;
    }

    public static AlertDialog showMagnificationModeDialog(Context context, CharSequence charSequence, DialogInterface.OnClickListener onClickListener) {
        AlertDialog createDialog = createDialog(context, 2, charSequence, onClickListener);
        createDialog.show();
        setScrollIndicators(createDialog);
        return createDialog;
    }

    private static AlertDialog createDialog(Context context, int i, CharSequence charSequence, DialogInterface.OnClickListener onClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(createEditDialogContentView(context, i));
        builder.setTitle(charSequence);
        builder.setPositiveButton(C0017R$string.save, onClickListener);
        builder.setNegativeButton(C0017R$string.cancel, $$Lambda$AccessibilityEditDialogUtils$qhigQ5S0y7RqsPPbrAv9RcxFPsE.INSTANCE);
        return builder.create();
    }

    private static void setScrollIndicators(AlertDialog alertDialog) {
        ((ScrollView) alertDialog.findViewById(C0010R$id.container_layout)).setScrollIndicators(3, 3);
    }

    private static View createEditDialogContentView(Context context, int i) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService("layout_inflater");
        if (i == 0) {
            View inflate = layoutInflater.inflate(C0012R$layout.accessibility_edit_shortcut, (ViewGroup) null);
            initSoftwareShortcut(context, inflate);
            initHardwareShortcut(context, inflate);
            return inflate;
        } else if (i == 1) {
            View inflate2 = layoutInflater.inflate(C0012R$layout.accessibility_edit_shortcut_magnification, (ViewGroup) null);
            initSoftwareShortcut(context, inflate2);
            initHardwareShortcut(context, inflate2);
            initMagnifyShortcut(context, inflate2);
            initAdvancedWidget(inflate2);
            return inflate2;
        } else if (i == 2) {
            View inflate3 = layoutInflater.inflate(C0012R$layout.accessibility_edit_magnification_mode, (ViewGroup) null);
            initMagnifyFullScreen(context, inflate3);
            initMagnifyWindowScreen(context, inflate3);
            return inflate3;
        } else {
            throw new IllegalArgumentException();
        }
    }

    private static void initMagnifyFullScreen(Context context, View view) {
        setupShortcutWidget(view.findViewById(C0010R$id.magnify_full_screen), context.getText(C0017R$string.accessibility_magnification_area_settings_full_screen), C0008R$drawable.accessibility_magnification_full_screen);
    }

    private static void initMagnifyWindowScreen(Context context, View view) {
        setupShortcutWidget(view.findViewById(C0010R$id.magnify_window_screen), context.getText(C0017R$string.accessibility_magnification_area_settings_window_screen), C0008R$drawable.accessibility_magnification_window_screen);
    }

    private static void setupShortcutWidget(View view, CharSequence charSequence, int i) {
        setupShortcutWidget(view, charSequence, null, i);
    }

    private static void setupShortcutWidget(View view, CharSequence charSequence, CharSequence charSequence2, int i) {
        ((CheckBox) view.findViewById(C0010R$id.checkbox)).setText(charSequence);
        TextView textView = (TextView) view.findViewById(C0010R$id.summary);
        if (TextUtils.isEmpty(charSequence2)) {
            textView.setVisibility(8);
        } else {
            textView.setText(charSequence2);
        }
        ((ImageView) view.findViewById(C0010R$id.image)).setImageResource(i);
    }

    private static void initSoftwareShortcut(Context context, View view) {
        View findViewById = view.findViewById(C0010R$id.software_shortcut);
        setupShortcutWidget(findViewById, retrieveTitle(context), retrieveSummary(context, ((TextView) findViewById.findViewById(C0010R$id.summary)).getLineHeight()), retrieveImageResId(context));
    }

    private static void initHardwareShortcut(Context context, View view) {
        setupShortcutWidget(view.findViewById(C0010R$id.hardware_shortcut), context.getText(C0017R$string.accessibility_shortcut_edit_dialog_title_hardware), context.getText(C0017R$string.accessibility_shortcut_edit_dialog_summary_hardware), C0008R$drawable.accessibility_shortcut_type_hardware);
    }

    private static void initMagnifyShortcut(Context context, View view) {
        setupShortcutWidget(view.findViewById(C0010R$id.triple_tap_shortcut), context.getText(C0017R$string.accessibility_shortcut_edit_dialog_title_triple_tap), context.getText(C0017R$string.accessibility_shortcut_edit_dialog_summary_triple_tap), C0008R$drawable.accessibility_shortcut_type_triple_tap);
    }

    private static void initAdvancedWidget(View view) {
        LinearLayout linearLayout = (LinearLayout) view.findViewById(C0010R$id.advanced_shortcut);
        linearLayout.setOnClickListener(new View.OnClickListener(linearLayout, view.findViewById(C0010R$id.triple_tap_shortcut)) {
            /* class com.android.settings.accessibility.$$Lambda$AccessibilityEditDialogUtils$qZNea3BDK7CGbVgZxt24eIpFx3w */
            public final /* synthetic */ LinearLayout f$0;
            public final /* synthetic */ View f$1;

            {
                this.f$0 = r1;
                this.f$1 = r2;
            }

            public final void onClick(View view) {
                AccessibilityEditDialogUtils.lambda$initAdvancedWidget$1(this.f$0, this.f$1, view);
            }
        });
    }

    static /* synthetic */ void lambda$initAdvancedWidget$1(LinearLayout linearLayout, View view, View view2) {
        linearLayout.setVisibility(8);
        view.setVisibility(0);
    }

    private static CharSequence retrieveTitle(Context context) {
        int i = C0017R$string.accessibility_shortcut_edit_dialog_title_software;
        if (AccessibilityUtil.isGestureNavigateEnabled(context)) {
            if (AccessibilityUtil.isTouchExploreEnabled(context)) {
                i = C0017R$string.accessibility_shortcut_edit_dialog_title_software_gesture_talkback;
            } else {
                i = C0017R$string.accessibility_shortcut_edit_dialog_title_software_gesture;
            }
        }
        return context.getText(i);
    }

    private static CharSequence retrieveSummary(Context context, int i) {
        int i2;
        if (!AccessibilityUtil.isGestureNavigateEnabled(context)) {
            return getSummaryStringWithIcon(context, i);
        }
        if (AccessibilityUtil.isTouchExploreEnabled(context)) {
            i2 = C0017R$string.accessibility_shortcut_edit_dialog_summary_software_gesture_talkback;
        } else {
            i2 = C0017R$string.accessibility_shortcut_edit_dialog_summary_software_gesture;
        }
        return context.getText(i2);
    }

    private static int retrieveImageResId(Context context) {
        int i;
        int i2 = C0008R$drawable.accessibility_shortcut_type_software;
        if (!AccessibilityUtil.isGestureNavigateEnabled(context)) {
            return i2;
        }
        if (AccessibilityUtil.isTouchExploreEnabled(context)) {
            i = C0008R$drawable.accessibility_shortcut_type_software_gesture_talkback;
        } else {
            i = C0008R$drawable.accessibility_shortcut_type_software_gesture;
        }
        return i;
    }

    private static SpannableString getSummaryStringWithIcon(Context context, int i) {
        String string = context.getString(C0017R$string.accessibility_shortcut_edit_dialog_summary_software);
        SpannableString valueOf = SpannableString.valueOf(string);
        int indexOf = string.indexOf("%s");
        Drawable drawable = context.getDrawable(C0008R$drawable.ic_accessibility_new);
        ImageSpan imageSpan = new ImageSpan(drawable);
        imageSpan.setContentDescription("");
        drawable.setBounds(0, 0, i, i);
        valueOf.setSpan(imageSpan, indexOf, indexOf + 2, 33);
        return valueOf;
    }
}
