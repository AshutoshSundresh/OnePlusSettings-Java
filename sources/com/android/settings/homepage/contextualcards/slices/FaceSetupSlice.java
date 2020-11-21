package com.android.settings.homepage.contextualcards.slices;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.face.FaceManager;
import android.net.Uri;
import android.os.UserHandle;
import android.provider.Settings;
import androidx.core.graphics.drawable.IconCompat;
import androidx.slice.Slice;
import androidx.slice.builders.ListBuilder;
import androidx.slice.builders.SliceAction;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0017R$string;
import com.android.settings.SubSettings;
import com.android.settings.Utils;
import com.android.settings.biometrics.face.FaceStatusPreferenceController;
import com.android.settings.homepage.contextualcards.FaceReEnrollDialog;
import com.android.settings.security.SecuritySettings;
import com.android.settings.slices.CustomSliceRegistry;
import com.android.settings.slices.CustomSliceable;
import com.android.settings.slices.SliceBuilderUtils;

public class FaceSetupSlice implements CustomSliceable {
    private final Context mContext;
    private FaceManager mFaceManager;

    public FaceSetupSlice(Context context) {
        this.mContext = context;
    }

    @Override // com.android.settings.slices.CustomSliceable
    public Slice getSlice() {
        CharSequence charSequence;
        CharSequence charSequence2;
        FaceManager faceManagerOrNull = Utils.getFaceManagerOrNull(this.mContext);
        this.mFaceManager = faceManagerOrNull;
        if (faceManagerOrNull == null) {
            ListBuilder listBuilder = new ListBuilder(this.mContext, CustomSliceRegistry.FACE_ENROLL_SLICE_URI, -1);
            listBuilder.setIsError(true);
            return listBuilder.build();
        }
        int myUserId = UserHandle.myUserId();
        boolean hasEnrolledTemplates = this.mFaceManager.hasEnrolledTemplates(myUserId);
        int reEnrollSetting = getReEnrollSetting(this.mContext, myUserId);
        if (!hasEnrolledTemplates) {
            charSequence2 = this.mContext.getText(C0017R$string.security_settings_face_settings_enroll);
            charSequence = this.mContext.getText(C0017R$string.security_settings_face_settings_context_subtitle);
        } else if (reEnrollSetting == 1) {
            charSequence2 = this.mContext.getText(C0017R$string.security_settings_face_enroll_should_re_enroll_title);
            charSequence = this.mContext.getText(C0017R$string.security_settings_face_enroll_should_re_enroll_subtitle);
        } else if (reEnrollSetting == 3) {
            charSequence2 = this.mContext.getText(C0017R$string.security_settings_face_enroll_must_re_enroll_title);
            charSequence = this.mContext.getText(C0017R$string.security_settings_face_enroll_must_re_enroll_subtitle);
        } else {
            ListBuilder listBuilder2 = new ListBuilder(this.mContext, CustomSliceRegistry.FACE_ENROLL_SLICE_URI, -1);
            listBuilder2.setIsError(true);
            return listBuilder2.build();
        }
        ListBuilder listBuilder3 = new ListBuilder(this.mContext, CustomSliceRegistry.FACE_ENROLL_SLICE_URI, -1);
        listBuilder3.setAccentColor(com.android.settingslib.Utils.getColorAccentDefaultColor(this.mContext));
        listBuilder3.addRow(buildRowBuilder(charSequence2, charSequence, IconCompat.createWithResource(this.mContext, C0008R$drawable.ic_face_24dp), this.mContext, getIntent()));
        return listBuilder3.build();
    }

    @Override // com.android.settings.slices.CustomSliceable
    public Uri getUri() {
        return CustomSliceRegistry.FACE_ENROLL_SLICE_URI;
    }

    @Override // com.android.settings.slices.CustomSliceable
    public Intent getIntent() {
        if (!this.mFaceManager.hasEnrolledTemplates(UserHandle.myUserId())) {
            return SliceBuilderUtils.buildSearchResultPageIntent(this.mContext, SecuritySettings.class.getName(), FaceStatusPreferenceController.KEY_FACE_SETTINGS, this.mContext.getText(C0017R$string.security_settings_face_settings_enroll).toString(), 1401).setClassName(this.mContext.getPackageName(), SubSettings.class.getName());
        }
        return new Intent(this.mContext, FaceReEnrollDialog.class);
    }

    private static ListBuilder.RowBuilder buildRowBuilder(CharSequence charSequence, CharSequence charSequence2, IconCompat iconCompat, Context context, Intent intent) {
        SliceAction createDeeplink = SliceAction.createDeeplink(PendingIntent.getActivity(context, 0, intent, 0), iconCompat, 0, charSequence);
        ListBuilder.RowBuilder rowBuilder = new ListBuilder.RowBuilder();
        rowBuilder.setTitleItem(iconCompat, 0);
        rowBuilder.setTitle(charSequence);
        rowBuilder.setSubtitle(charSequence2);
        rowBuilder.setPrimaryAction(createDeeplink);
        return rowBuilder;
    }

    public static int getReEnrollSetting(Context context, int i) {
        return Settings.Secure.getIntForUser(context.getContentResolver(), "face_unlock_re_enroll", 0, i);
    }
}
