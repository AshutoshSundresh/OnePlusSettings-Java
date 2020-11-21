package com.oneplus.settings.aboutphone;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;

public class OPAboutPhoneHardWareController {
    private String mCameraMessage;
    private String mCpuMessage;
    private final View mHardWareInfoView;
    private Drawable mIcon;
    private String mScreenMessage;
    private String mStorageMessage;

    public static OPAboutPhoneHardWareController newInstance(Activity activity, Fragment fragment, View view) {
        return new OPAboutPhoneHardWareController(activity, fragment, view);
    }

    private OPAboutPhoneHardWareController(Activity activity, Fragment fragment, View view) {
        if (view != null) {
            this.mHardWareInfoView = view;
        } else {
            this.mHardWareInfoView = LayoutInflater.from(fragment.getContext()).inflate(C0012R$layout.op_about_phone_hareware_layout, (ViewGroup) null);
        }
    }

    public OPAboutPhoneHardWareController setPhoneImage(Drawable drawable) {
        this.mIcon = drawable;
        return this;
    }

    public OPAboutPhoneHardWareController setCpuMessage(String str) {
        this.mCpuMessage = str;
        return this;
    }

    public OPAboutPhoneHardWareController setStorageMessage(String str) {
        this.mStorageMessage = str;
        return this;
    }

    public OPAboutPhoneHardWareController setCameraMessage(String str) {
        this.mCameraMessage = str;
        return this;
    }

    public OPAboutPhoneHardWareController setScreenMessage(String str) {
        this.mScreenMessage = str;
        return this;
    }

    public View done() {
        TextView textView = (TextView) this.mHardWareInfoView.findViewById(C0010R$id.cpu_message);
        ((ImageView) this.mHardWareInfoView.findViewById(C0010R$id.phone_image)).setImageDrawable(this.mIcon);
        textView.setText(this.mCpuMessage);
        textView.setVisibility(0);
        ((TextView) this.mHardWareInfoView.findViewById(C0010R$id.storage_message)).setText(this.mStorageMessage);
        ((TextView) this.mHardWareInfoView.findViewById(C0010R$id.camera_message)).setText(this.mCameraMessage);
        ((TextView) this.mHardWareInfoView.findViewById(C0010R$id.screen_message)).setText(this.mScreenMessage);
        return this.mHardWareInfoView;
    }
}
