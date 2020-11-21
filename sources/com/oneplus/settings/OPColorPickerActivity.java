package com.oneplus.settings;

import android.app.ActionBar;
import android.app.ActivityManager;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemProperties;
import android.provider.Settings;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.EditTextDialog;
import com.android.settings.C0003R$array;
import com.android.settings.C0006R$color;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.oneplus.compat.util.OpThemeNative;
import com.oneplus.settings.ui.ColorPickerView;
import com.oneplus.settings.utils.OPApplicationUtils;
import com.oneplus.settings.utils.OPThemeUtils;
import com.oneplus.settings.utils.OPUtils;
import java.util.HashMap;
import java.util.regex.Pattern;

public class OPColorPickerActivity extends BaseActivity implements View.OnClickListener, ColorPickerView.OnColorChangedListener, OnPressListener {
    private ActivityManager mAm;
    private View mColorEditButton;
    private EditText mColorEditView;
    private String[] mColors;
    private String mCurrentColor;
    private String mCurrentTempColor;
    private int mDisabledCellColor;
    private EditTextDialog mEditColorDialog;
    private boolean mIsCustomColor = false;
    private String[] mLightTextColors;
    private TextView mModifyColorPicker;
    private ColorPickerView mPickerView;
    private View mPresetView;
    private ImageView mPreviewImg;
    private int mRippleEffectColor;
    private int mSelectIndex = -1;
    private View[] mViews;

    /* access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, com.oneplus.settings.BaseAppCompatActivity, com.oneplus.settings.BaseActivity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(C0012R$layout.op_color_picker_layout);
        if (OPUtils.isAndroidModeOn(getContentResolver())) {
            Toast.makeText(this, C0017R$string.oneplus_colorful_mode_cannot_change_color_accent, 1).show();
            finish();
            return;
        }
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setTitle(C0017R$string.theme_accent_color_title);
        }
        this.mAm = (ActivityManager) getSystemService("activity");
        setOnPressListener(this);
        this.mPreviewImg = (ImageView) findViewById(C0010R$id.preview);
        ColorPickerView colorPickerView = (ColorPickerView) findViewById(C0010R$id.oneplus_color_picker_view);
        this.mPickerView = colorPickerView;
        colorPickerView.setOnColorChangedListener(this);
        this.mPresetView = findViewById(C0010R$id.oneplus_color_preset_view);
        this.mRippleEffectColor = getColor(C0006R$color.colorpicker_ripple_effect_color);
        this.mDisabledCellColor = getColor(C0006R$color.colorpicker_disabled_cell_color);
        TextView textView = (TextView) findViewById(C0010R$id.colorpick_modify);
        this.mModifyColorPicker = textView;
        textView.setOnClickListener(this);
        View findViewById = findViewById(C0010R$id.colorvalue_pick);
        this.mColorEditButton = findViewById;
        findViewById.setOnClickListener(this);
        ((Button) findViewById(C0010R$id.save_button)).setOnClickListener(this);
        initAccentColors(getResources());
        initAccentColorView(getWindow().getDecorView());
        this.mPresetView.setVisibility(0);
        this.mPickerView.setVisibility(8);
        this.mColorEditButton.setVisibility(8);
        this.mModifyColorPicker.setText(C0017R$string.oneplus_theme_accent_color_customization);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void refreshUI(String str) {
        ImageView imageView;
        if (this.mSelectIndex == 0) {
            this.mPreviewImg.setImageResource(C0008R$drawable.op_default_accent_color_preview_bg);
        } else {
            this.mPreviewImg.setImageResource(C0008R$drawable.op_custom_accent_color_preview_upper_layer);
        }
        if (this.mSelectIndex != 0 && (imageView = this.mPreviewImg) != null && imageView.getDrawable() != null) {
            this.mPreviewImg.getDrawable().mutate().setTintList(ColorStateList.valueOf(OPUtils.parseColor(str)));
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override // com.oneplus.settings.OnPressListener
    public void onCancelPressed() {
        finish();
    }

    @Override // com.oneplus.settings.BaseActivity
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != 0) {
            return super.onOptionsItemSelected(menuItem);
        }
        sendTheme();
        finish();
        return true;
    }

    @Override // androidx.activity.ComponentActivity
    public void onBackPressed() {
        performBackEvent();
    }

    @Override // com.oneplus.settings.ui.ColorPickerView.OnColorChangedListener
    public void onColorChanged(int i) {
        StringBuffer stringBuffer = new StringBuffer("#");
        stringBuffer.append(Integer.toHexString(Color.red(i)));
        stringBuffer.append(Integer.toHexString(Color.green(i)));
        stringBuffer.append(Integer.toHexString(Color.blue(i)));
        this.mCurrentTempColor = "#" + OPUtils.convertToRGB(i);
        setSelectionVisible(false);
        this.mSelectIndex = -1;
        refreshUI(this.mCurrentTempColor);
    }

    public void onClick(View view) {
        int id = view.getId();
        if (id == C0010R$id.colorpick_modify) {
            if (this.mIsCustomColor) {
                this.mIsCustomColor = false;
                this.mPresetView.setVisibility(0);
                this.mPickerView.setVisibility(8);
                this.mColorEditButton.setVisibility(8);
                this.mModifyColorPicker.setText(C0017R$string.oneplus_theme_accent_color_customization);
                return;
            }
            this.mIsCustomColor = true;
            this.mPresetView.setVisibility(8);
            this.mPickerView.setVisibility(0);
            this.mColorEditButton.setVisibility(0);
            this.mModifyColorPicker.setText(C0017R$string.oneplus_theme_accent_color_presetcolor);
        } else if (id == C0010R$id.colorvalue_pick) {
            showColotEditDialog();
        } else if (id == C0010R$id.save_button) {
            sendTheme();
            finish();
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private boolean isColorCodeValid(String str) {
        return Pattern.compile("^#([0-9a-fA-F]{6})").matcher(str).matches();
    }

    private void initAccentColors(Resources resources) {
        TypedArray obtainTypedArray = resources.obtainTypedArray(C0003R$array.op_custom_accent_color_values);
        int length = obtainTypedArray.length();
        this.mColors = new String[length];
        for (int i = 0; i < length; i++) {
            this.mColors[i] = resources.getString(obtainTypedArray.getResourceId(i, -1));
        }
        obtainTypedArray.recycle();
        TypedArray obtainTypedArray2 = resources.obtainTypedArray(C0003R$array.op_custom_accent_text_color_values_light);
        int length2 = obtainTypedArray2.length();
        this.mLightTextColors = new String[length2];
        for (int i2 = 0; i2 < length2; i2++) {
            this.mLightTextColors[i2] = resources.getString(obtainTypedArray2.getResourceId(i2, -1));
        }
        obtainTypedArray2.recycle();
    }

    private void initAccentColorView(View view) {
        final String[] strArr = this.mColors;
        int[] iArr = {C0010R$id.color_0, C0010R$id.color_1, C0010R$id.color_2, C0010R$id.color_3, C0010R$id.color_4, C0010R$id.color_5, C0010R$id.color_6, C0010R$id.color_7, C0010R$id.color_8, C0010R$id.color_9, C0010R$id.color_10, C0010R$id.color_11};
        this.mViews = new View[strArr.length];
        for (int i = 0; i < strArr.length; i++) {
            this.mViews[i] = view.findViewById(iArr[i]);
            if (i != 0) {
                this.mViews[i].setBackground(createRippleDrawable(strArr[i]));
            }
            this.mViews[i].setTag(Integer.valueOf(i));
            this.mViews[i].setOnClickListener(new View.OnClickListener() {
                /* class com.oneplus.settings.OPColorPickerActivity.AnonymousClass1 */

                public void onClick(View view) {
                    int intValue = ((Integer) view.getTag()).intValue();
                    if (!strArr[intValue].equals(OPColorPickerActivity.this.mCurrentTempColor)) {
                        OPColorPickerActivity.this.setSelectionVisible(false);
                        OPColorPickerActivity.this.mSelectIndex = intValue;
                        OPColorPickerActivity.this.mIsCustomColor = false;
                        OPColorPickerActivity.this.setSelectionVisible(true);
                        OPColorPickerActivity.this.mCurrentTempColor = strArr[intValue];
                        OPColorPickerActivity oPColorPickerActivity = OPColorPickerActivity.this;
                        oPColorPickerActivity.refreshUI(oPColorPickerActivity.mCurrentTempColor);
                    }
                }
            });
        }
        init();
    }

    public RippleDrawable createRippleDrawable(String str) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(TextUtils.isEmpty(str) ? this.mDisabledCellColor : OPUtils.parseColor(str));
        gradientDrawable.setCornerRadius(6.0f);
        return new RippleDrawable(new ColorStateList(new int[][]{new int[0]}, new int[]{this.mRippleEffectColor}), gradientDrawable, null);
    }

    public void setSelectionVisible(boolean z) {
        View[] viewArr;
        int i = 0;
        int[] iArr = {C0010R$id.check_0, C0010R$id.check_1, C0010R$id.check_2, C0010R$id.check_3, C0010R$id.check_4, C0010R$id.check_5, C0010R$id.check_6, C0010R$id.check_7, C0010R$id.check_8, C0010R$id.check_9, C0010R$id.check_10, C0010R$id.check_11};
        int i2 = this.mSelectIndex;
        if (i2 >= 0 && (viewArr = this.mViews) != null) {
            View findViewById = viewArr[i2].findViewById(iArr[i2]);
            if (!z) {
                i = 8;
            }
            findViewById.setVisibility(i);
        }
    }

    public void init() {
        String string = Settings.System.getString(getContentResolver(), "oneplus_accent_color");
        this.mCurrentColor = string;
        if (TextUtils.isEmpty(string)) {
            this.mCurrentColor = getResources().getString(C0006R$color.op_control_accent_color_red_default);
        }
        if (this.mColors != null) {
            int i = 0;
            while (true) {
                if (i >= this.mColors.length) {
                    break;
                }
                Log.d("OPFullScreenGestureGuidePage", " mCurrentColor:" + this.mCurrentColor + " mColors:" + this.mColors[i]);
                if (this.mColors[i].equalsIgnoreCase(this.mCurrentColor) || this.mColors[i].equalsIgnoreCase(this.mCurrentColor.replace("#", "#FF"))) {
                    this.mSelectIndex = i;
                } else {
                    i++;
                }
            }
            this.mSelectIndex = i;
        }
        int i2 = this.mSelectIndex;
        this.mCurrentTempColor = this.mCurrentColor;
        if (i2 >= 0) {
            setSelectionVisible(true);
            View view = this.mPresetView;
            if (view != null) {
                view.setVisibility(0);
            }
            ColorPickerView colorPickerView = this.mPickerView;
            if (colorPickerView != null) {
                colorPickerView.setVisibility(8);
            }
            View view2 = this.mColorEditButton;
            if (view2 != null) {
                view2.setVisibility(8);
            }
            TextView textView = this.mModifyColorPicker;
            if (textView != null) {
                textView.setText(C0017R$string.oneplus_theme_accent_color_customization);
            }
            this.mIsCustomColor = false;
        } else {
            ColorPickerView colorPickerView2 = this.mPickerView;
            if (colorPickerView2 != null) {
                colorPickerView2.setVisibility(0);
                this.mPickerView.setColor(OPUtils.parseColor(this.mCurrentColor));
            }
            View view3 = this.mPresetView;
            if (view3 != null) {
                view3.setVisibility(8);
            }
            View view4 = this.mColorEditButton;
            if (view4 != null) {
                view4.setVisibility(0);
            }
            TextView textView2 = this.mModifyColorPicker;
            if (textView2 != null) {
                textView2.setText(C0017R$string.oneplus_theme_accent_color_presetcolor);
            }
            this.mIsCustomColor = true;
        }
        refreshUI(this.mCurrentColor);
    }

    /* access modifiers changed from: protected */
    @Override // com.oneplus.settings.BaseActivity
    public boolean needShowWarningDialog() {
        if (TextUtils.isEmpty(this.mCurrentColor) || TextUtils.isEmpty(this.mCurrentTempColor)) {
            return false;
        }
        return !this.mCurrentColor.equalsIgnoreCase(this.mCurrentTempColor);
    }

    private void sendTheme() {
        String str;
        String str2;
        saveColorInfo();
        String str3 = this.mCurrentTempColor;
        getResources().getString(C0006R$color.op_control_text_color_primary_default);
        if (this.mSelectIndex == 0) {
            String string = getResources().getString(C0006R$color.op_control_icon_color_active);
            Settings.System.putStringForUser(getContentResolver(), "oneplus_accent_color", str3, -2);
            Settings.System.putStringForUser(getContentResolver(), "oneplus_sub_accent_color", string, -2);
            String textAccentColor = OPUtils.getTextAccentColor(str3);
            if (OPUtils.isBlackModeOn(getContentResolver())) {
                Settings.System.putStringForUser(getContentResolver(), "oneplus_accent_text_color", textAccentColor, -2);
            } else {
                Settings.System.putStringForUser(getContentResolver(), "oneplus_accent_text_color", str3, -2);
            }
            if (OPUtils.isBlackModeOn(getContentResolver())) {
                str2 = getResources().getString(C0006R$color.op_control_text_color_primary_light);
            } else {
                str2 = getResources().getString(C0006R$color.op_control_text_color_primary_dark);
            }
            Settings.System.putStringForUser(getContentResolver(), "oneplus_accent_button_text_color", str2, -2);
            if (!TextUtils.isEmpty(str2)) {
                str2 = str2.replace("#", "");
            }
            SystemProperties.set("persist.sys.theme.oneplus_accent_button_text_color", str2);
            if (!TextUtils.isEmpty(str3)) {
                str3 = str3.replace("#", "");
            }
            SystemProperties.set("persist.sys.theme.accentcolor", str3);
            if (!TextUtils.isEmpty(string)) {
                string = string.replace("#", "");
            }
            SystemProperties.set("persist.sys.theme.sub_accentcolor", string);
            if (!TextUtils.isEmpty(textAccentColor)) {
                textAccentColor = textAccentColor.replace("#", "");
            }
            if (OPUtils.isBlackModeOn(getContentResolver())) {
                SystemProperties.set("persist.sys.theme.accent_text_color", textAccentColor);
            } else {
                SystemProperties.set("persist.sys.theme.accent_text_color", str3);
            }
        } else {
            Settings.System.putStringForUser(getContentResolver(), "oneplus_accent_color", str3, -2);
            Settings.System.putStringForUser(getContentResolver(), "oneplus_sub_accent_color", str3, -2);
            int i = this.mSelectIndex;
            if (i != -1) {
                str = OPUtils.getTextAccentColor(this.mLightTextColors[i]);
            } else {
                str = OPUtils.getTextAccentColor(str3);
            }
            if (OPUtils.isBlackModeOn(getContentResolver())) {
                Settings.System.putStringForUser(getContentResolver(), "oneplus_accent_text_color", str, -2);
            } else {
                Settings.System.putStringForUser(getContentResolver(), "oneplus_accent_text_color", str3, -2);
            }
            String string2 = getResources().getString(C0006R$color.op_control_text_color_primary_dark);
            Settings.System.putStringForUser(getContentResolver(), "oneplus_accent_button_text_color", string2, -2);
            if (!TextUtils.isEmpty(string2)) {
                string2 = string2.replace("#", "");
            }
            SystemProperties.set("persist.sys.theme.oneplus_accent_button_text_color", string2);
            if (!TextUtils.isEmpty(str3)) {
                str3 = str3.replace("#", "");
            }
            SystemProperties.set("persist.sys.theme.accentcolor", str3);
            SystemProperties.set("persist.sys.theme.sub_accentcolor", str3);
            if (!TextUtils.isEmpty(str)) {
                str = str.replace("#", "");
            }
            if (OPUtils.isBlackModeOn(getContentResolver())) {
                SystemProperties.set("persist.sys.theme.accent_text_color", str);
            } else {
                SystemProperties.set("persist.sys.theme.accent_text_color", str3);
            }
        }
        new Handler().postDelayed(new Runnable() {
            /* class com.oneplus.settings.OPColorPickerActivity.AnonymousClass2 */

            public void run() {
                OPApplicationUtils.killProcess(OPColorPickerActivity.this.mAm, true);
                HashMap hashMap = new HashMap();
                hashMap.put("oneplus_accentcolor", "");
                OpThemeNative.enableTheme(OPColorPickerActivity.this, hashMap);
                OPThemeUtils.enableTheme("oneplus_shape", OPThemeUtils.getCurrentShapeByIndex(OPThemeUtils.getCurrentShape(OPColorPickerActivity.this)), OPColorPickerActivity.this);
            }
        }, 200);
    }

    private void saveColorInfo() {
        if (OPUtils.isBlackModeOn(getContentResolver())) {
            Settings.System.putString(getContentResolver(), "oem_black_mode_accent_color", this.mCurrentTempColor);
            if (this.mIsCustomColor) {
                Settings.System.putInt(getContentResolver(), "oem_black_mode_accent_color_index", -1);
            } else {
                Settings.System.putInt(getContentResolver(), "oem_black_mode_accent_color_index", this.mSelectIndex);
            }
        } else {
            Settings.System.putString(getContentResolver(), "oem_white_mode_accent_color", this.mCurrentTempColor);
            if (this.mIsCustomColor) {
                Settings.System.putInt(getContentResolver(), "oem_white_mode_accent_color_index", -1);
            } else {
                Settings.System.putInt(getContentResolver(), "oem_white_mode_accent_color_index", this.mSelectIndex);
            }
        }
        OPUtils.sendAppTrackerForAccentColor();
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void setEditTextAtLastLocation(EditText editText) {
        Editable text = editText.getText();
        if (text instanceof Spannable) {
            Selection.setSelection(text, text.length());
        }
    }

    public void showColotEditDialog() {
        EditTextDialog editTextDialog = new EditTextDialog(this);
        this.mEditColorDialog = editTextDialog;
        editTextDialog.setTitle(C0017R$string.op_custom_color_value_edit);
        this.mEditColorDialog.setCancelable(true);
        this.mEditColorDialog.setButton(-2, getResources().getText(C0017R$string.alert_dialog_cancel), new DialogInterface.OnClickListener(this) {
            /* class com.oneplus.settings.OPColorPickerActivity.AnonymousClass3 */

            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        this.mEditColorDialog.setButton(-1, getResources().getText(C0017R$string.okay), null);
        this.mEditColorDialog.show();
        EditText editText = this.mEditColorDialog.getEditText();
        this.mColorEditView = editText;
        editText.requestFocus();
        this.mColorEditView.setSingleLine(true);
        if (!TextUtils.isEmpty(this.mCurrentTempColor)) {
            String str = this.mCurrentTempColor;
            if (str.length() == 9) {
                str = "#" + str.substring(3);
            }
            this.mColorEditView.setText(str);
            this.mColorEditView.setSelection(str.length());
        }
        this.mColorEditView.addTextChangedListener(new TextWatcher() {
            /* class com.oneplus.settings.OPColorPickerActivity.AnonymousClass4 */

            public void afterTextChanged(Editable editable) {
            }

            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                Editable text = OPColorPickerActivity.this.mColorEditView.getText();
                if (text != null && text.length() == 0) {
                    OPColorPickerActivity.this.mColorEditView.setText("#");
                    OPColorPickerActivity oPColorPickerActivity = OPColorPickerActivity.this;
                    oPColorPickerActivity.setEditTextAtLastLocation(oPColorPickerActivity.mColorEditView);
                }
                if (text != null && text.length() == 8) {
                    OPColorPickerActivity.this.mColorEditView.setText(text.subSequence(0, 7));
                    OPColorPickerActivity oPColorPickerActivity2 = OPColorPickerActivity.this;
                    oPColorPickerActivity2.setEditTextAtLastLocation(oPColorPickerActivity2.mColorEditView);
                }
            }
        });
        this.mEditColorDialog.getButton(-1).setOnClickListener(new View.OnClickListener() {
            /* class com.oneplus.settings.OPColorPickerActivity.AnonymousClass5 */

            public void onClick(View view) {
                String obj = OPColorPickerActivity.this.mColorEditView.getText().toString();
                if (!OPColorPickerActivity.this.isColorCodeValid(obj)) {
                    Toast.makeText(OPColorPickerActivity.this, C0017R$string.op_custom_color_value_invalid, 0).show();
                    return;
                }
                OPColorPickerActivity.this.setSelectionVisible(false);
                OPColorPickerActivity.this.mSelectIndex = -1;
                String str = "#FF" + obj.substring(1);
                OPColorPickerActivity.this.mCurrentTempColor = str;
                OPColorPickerActivity.this.mPickerView.setColor(OPUtils.convertToColorInt(str));
                OPColorPickerActivity.this.refreshUI(str);
                OPColorPickerActivity.this.mEditColorDialog.dismiss();
            }
        });
    }
}
