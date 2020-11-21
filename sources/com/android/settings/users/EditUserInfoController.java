package com.android.settings.users;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.UserInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settingslib.drawable.CircleFramedDrawable;
import java.io.File;

public class EditUserInfoController {
    private Dialog mEditUserInfoDialog;
    private EditUserPhotoController mEditUserPhotoController;
    private Bitmap mSavedPhoto;
    private UserHandle mUser;
    private UserManager mUserManager;
    private boolean mWaitingForActivityResult = false;

    public interface OnContentChangedCallback {
        void onLabelChanged(UserHandle userHandle, CharSequence charSequence);

        void onPhotoChanged(UserHandle userHandle, Drawable drawable);
    }

    public interface OnDialogCompleteCallback {
        void onNegativeOrCancel();

        void onPositive();
    }

    public void clear() {
        EditUserPhotoController editUserPhotoController = this.mEditUserPhotoController;
        if (editUserPhotoController != null) {
            editUserPhotoController.removeNewUserPhotoBitmapFile();
        }
        this.mEditUserInfoDialog = null;
        this.mSavedPhoto = null;
    }

    public void onRestoreInstanceState(Bundle bundle) {
        String string = bundle.getString("pending_photo");
        if (string != null) {
            this.mSavedPhoto = EditUserPhotoController.loadNewUserPhotoBitmap(new File(string));
        }
        this.mWaitingForActivityResult = bundle.getBoolean("awaiting_result", false);
    }

    public void onSaveInstanceState(Bundle bundle) {
        EditUserPhotoController editUserPhotoController;
        File saveNewUserPhotoBitmap;
        Dialog dialog = this.mEditUserInfoDialog;
        if (!(dialog == null || !dialog.isShowing() || (editUserPhotoController = this.mEditUserPhotoController) == null || (saveNewUserPhotoBitmap = editUserPhotoController.saveNewUserPhotoBitmap()) == null)) {
            bundle.putString("pending_photo", saveNewUserPhotoBitmap.getPath());
        }
        boolean z = this.mWaitingForActivityResult;
        if (z) {
            bundle.putBoolean("awaiting_result", z);
        }
    }

    public void startingActivityForResult() {
        this.mWaitingForActivityResult = true;
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        this.mWaitingForActivityResult = false;
        EditUserPhotoController editUserPhotoController = this.mEditUserPhotoController;
        if (editUserPhotoController != null && this.mEditUserInfoDialog != null) {
            editUserPhotoController.onActivityResult(i, i2, intent);
        }
    }

    public Dialog createDialog(Fragment fragment, final Drawable drawable, final CharSequence charSequence, String str, final OnContentChangedCallback onContentChangedCallback, UserHandle userHandle, final OnDialogCompleteCallback onDialogCompleteCallback) {
        FragmentActivity activity = fragment.getActivity();
        this.mUser = userHandle;
        if (this.mUserManager == null) {
            this.mUserManager = (UserManager) activity.getSystemService(UserManager.class);
        }
        View inflate = activity.getLayoutInflater().inflate(C0012R$layout.edit_user_info_dialog_content, (ViewGroup) null);
        final EditText editText = (EditText) inflate.findViewById(C0010R$id.user_name);
        editText.setText(charSequence);
        ImageView imageView = (ImageView) inflate.findViewById(C0010R$id.user_photo);
        UserManager userManager = this.mUserManager;
        boolean z = userManager != null && canChangePhoto(activity, userManager.getUserInfo(userHandle.getIdentifier()));
        if (!z) {
            imageView.setBackground(null);
        }
        Bitmap bitmap = this.mSavedPhoto;
        Drawable instance = bitmap != null ? CircleFramedDrawable.getInstance(activity, bitmap) : drawable;
        imageView.setImageDrawable(instance);
        if (z) {
            this.mEditUserPhotoController = createEditUserPhotoController(fragment, imageView, instance);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(str);
        builder.setView(inflate);
        builder.setCancelable(true);
        builder.setPositiveButton(17039370, new DialogInterface.OnClickListener() {
            /* class com.android.settings.users.EditUserInfoController.AnonymousClass3 */

            public void onClick(DialogInterface dialogInterface, int i) {
                Drawable newUserPhotoDrawable;
                OnContentChangedCallback onContentChangedCallback;
                OnContentChangedCallback onContentChangedCallback2;
                if (i == -1) {
                    Editable text = editText.getText();
                    if (!TextUtils.isEmpty(text) && ((charSequence == null || !text.toString().equals(charSequence.toString())) && (onContentChangedCallback2 = onContentChangedCallback) != null)) {
                        onContentChangedCallback2.onLabelChanged(EditUserInfoController.this.mUser, text.toString());
                    }
                    if (!(EditUserInfoController.this.mEditUserPhotoController == null || (newUserPhotoDrawable = EditUserInfoController.this.mEditUserPhotoController.getNewUserPhotoDrawable()) == null || newUserPhotoDrawable.equals(drawable) || (onContentChangedCallback = onContentChangedCallback) == null)) {
                        onContentChangedCallback.onPhotoChanged(EditUserInfoController.this.mUser, newUserPhotoDrawable);
                    }
                }
                EditUserInfoController.this.clear();
                OnDialogCompleteCallback onDialogCompleteCallback = onDialogCompleteCallback;
                if (onDialogCompleteCallback != null) {
                    onDialogCompleteCallback.onPositive();
                }
            }
        });
        builder.setNegativeButton(17039360, new DialogInterface.OnClickListener() {
            /* class com.android.settings.users.EditUserInfoController.AnonymousClass2 */

            public void onClick(DialogInterface dialogInterface, int i) {
                EditUserInfoController.this.clear();
                OnDialogCompleteCallback onDialogCompleteCallback = onDialogCompleteCallback;
                if (onDialogCompleteCallback != null) {
                    onDialogCompleteCallback.onNegativeOrCancel();
                }
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            /* class com.android.settings.users.EditUserInfoController.AnonymousClass1 */

            public void onCancel(DialogInterface dialogInterface) {
                EditUserInfoController.this.clear();
                OnDialogCompleteCallback onDialogCompleteCallback = onDialogCompleteCallback;
                if (onDialogCompleteCallback != null) {
                    onDialogCompleteCallback.onNegativeOrCancel();
                }
            }
        });
        AlertDialog create = builder.create();
        this.mEditUserInfoDialog = create;
        create.getWindow().setSoftInputMode(4);
        return this.mEditUserInfoDialog;
    }

    /* access modifiers changed from: package-private */
    public boolean canChangePhoto(Context context, UserInfo userInfo) {
        return PhotoCapabilityUtils.canCropPhoto(context) && (PhotoCapabilityUtils.canChoosePhoto(context) || PhotoCapabilityUtils.canTakePhoto(context));
    }

    /* access modifiers changed from: package-private */
    public EditUserPhotoController createEditUserPhotoController(Fragment fragment, ImageView imageView, Drawable drawable) {
        return new EditUserPhotoController(fragment, imageView, this.mSavedPhoto, drawable, this.mWaitingForActivityResult);
    }
}
