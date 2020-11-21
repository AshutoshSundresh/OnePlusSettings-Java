package com.android.settings.users;

import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.os.UserHandle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListPopupWindow;
import android.widget.TextView;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import com.android.settings.C0007R$dimen;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.drawable.CircleFramedDrawable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import libcore.io.Streams;

public class EditUserPhotoController {
    private final Context mContext;
    private final Uri mCropPictureUri;
    private final Fragment mFragment;
    private final ImageView mImageView;
    private Bitmap mNewUserPhotoBitmap;
    private Drawable mNewUserPhotoDrawable;
    private final int mPhotoSize = getPhotoSize(this.mContext);
    private final Uri mTakePictureUri;

    public EditUserPhotoController(Fragment fragment, ImageView imageView, Bitmap bitmap, Drawable drawable, boolean z) {
        Context context = imageView.getContext();
        this.mContext = context;
        this.mFragment = fragment;
        this.mImageView = imageView;
        this.mCropPictureUri = createTempImageUri(context, "CropEditUserPhoto.jpg", !z);
        this.mTakePictureUri = createTempImageUri(this.mContext, "TakeEditUserPhoto2.jpg", !z);
        this.mImageView.setOnClickListener(new View.OnClickListener() {
            /* class com.android.settings.users.EditUserPhotoController.AnonymousClass1 */

            public void onClick(View view) {
                EditUserPhotoController.this.showUpdatePhotoPopup();
            }
        });
        this.mNewUserPhotoBitmap = bitmap;
        this.mNewUserPhotoDrawable = drawable;
    }

    public boolean onActivityResult(int i, int i2, Intent intent) {
        if (i2 != -1) {
            return false;
        }
        Uri data = (intent == null || intent.getData() == null) ? this.mTakePictureUri : intent.getData();
        switch (i) {
            case 1001:
            case 1002:
                if (this.mTakePictureUri.equals(data)) {
                    cropPhoto();
                } else {
                    copyAndCropPhoto(data);
                }
                return true;
            case 1003:
                onPhotoCropped(data, true);
                return true;
            default:
                return false;
        }
    }

    public Drawable getNewUserPhotoDrawable() {
        return this.mNewUserPhotoDrawable;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void showUpdatePhotoPopup() {
        Context context = this.mImageView.getContext();
        boolean canTakePhoto = PhotoCapabilityUtils.canTakePhoto(context);
        boolean canChoosePhoto = PhotoCapabilityUtils.canChoosePhoto(context);
        if (canTakePhoto || canChoosePhoto) {
            ArrayList arrayList = new ArrayList();
            if (canTakePhoto) {
                arrayList.add(new RestrictedMenuItem(context, context.getString(C0017R$string.user_image_take_photo), "no_set_user_icon", new Runnable() {
                    /* class com.android.settings.users.EditUserPhotoController.AnonymousClass2 */

                    public void run() {
                        EditUserPhotoController.this.takePhoto();
                    }
                }));
            }
            if (canChoosePhoto) {
                arrayList.add(new RestrictedMenuItem(context, context.getString(C0017R$string.user_image_choose_photo), "no_set_user_icon", new Runnable() {
                    /* class com.android.settings.users.EditUserPhotoController.AnonymousClass3 */

                    public void run() {
                        EditUserPhotoController.this.choosePhoto();
                    }
                }));
            }
            final ListPopupWindow listPopupWindow = new ListPopupWindow(context);
            listPopupWindow.setAnchorView(this.mImageView);
            listPopupWindow.setModal(true);
            listPopupWindow.setInputMethodMode(2);
            listPopupWindow.setAdapter(new RestrictedPopupMenuAdapter(context, arrayList));
            listPopupWindow.setWidth(Math.max(this.mImageView.getWidth(), context.getResources().getDimensionPixelSize(C0007R$dimen.update_user_photo_popup_min_width)));
            listPopupWindow.setDropDownGravity(8388611);
            listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener(this) {
                /* class com.android.settings.users.EditUserPhotoController.AnonymousClass4 */

                @Override // android.widget.AdapterView.OnItemClickListener
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                    listPopupWindow.dismiss();
                    ((RestrictedMenuItem) adapterView.getAdapter().getItem(i)).doAction();
                }
            });
            listPopupWindow.show();
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void takePhoto() {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        appendOutputExtra(intent, this.mTakePictureUri);
        this.mFragment.startActivityForResult(intent, 1002);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void choosePhoto() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT", (Uri) null);
        intent.setType("image/*");
        appendOutputExtra(intent, this.mTakePictureUri);
        this.mFragment.startActivityForResult(intent, 1001);
    }

    private void copyAndCropPhoto(final Uri uri) {
        new AsyncTask<Void, Void, Void>() {
            /* class com.android.settings.users.EditUserPhotoController.AnonymousClass5 */

            /* access modifiers changed from: protected */
            public Void doInBackground(Void... voidArr) {
                ContentResolver contentResolver = EditUserPhotoController.this.mContext.getContentResolver();
                try {
                    InputStream openInputStream = contentResolver.openInputStream(uri);
                    try {
                        OutputStream openOutputStream = contentResolver.openOutputStream(EditUserPhotoController.this.mTakePictureUri);
                        try {
                            Streams.copy(openInputStream, openOutputStream);
                            if (openOutputStream != null) {
                                openOutputStream.close();
                            }
                            if (openInputStream == null) {
                                return null;
                            }
                            openInputStream.close();
                            return null;
                        } catch (Throwable th) {
                            th.addSuppressed(th);
                        }
                        throw th;
                        throw th;
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                } catch (IOException e) {
                    Log.w("EditUserPhotoController", "Failed to copy photo", e);
                    return null;
                }
            }

            /* access modifiers changed from: protected */
            public void onPostExecute(Void r1) {
                if (EditUserPhotoController.this.mFragment.isAdded()) {
                    EditUserPhotoController.this.cropPhoto();
                }
            }
        }.execute(new Void[0]);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void cropPhoto() {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(this.mTakePictureUri, "image/*");
        appendOutputExtra(intent, this.mCropPictureUri);
        appendCropExtras(intent);
        if (intent.resolveActivity(this.mContext.getPackageManager()) != null) {
            try {
                StrictMode.disableDeathOnFileUriExposure();
                this.mFragment.startActivityForResult(intent, 1003);
            } finally {
                StrictMode.enableDeathOnFileUriExposure();
            }
        } else {
            onPhotoCropped(this.mTakePictureUri, false);
        }
    }

    private void appendOutputExtra(Intent intent, Uri uri) {
        intent.putExtra("output", uri);
        intent.addFlags(3);
        intent.setClipData(ClipData.newRawUri("output", uri));
    }

    private void appendCropExtras(Intent intent) {
        intent.putExtra("crop", "true");
        intent.putExtra("scale", true);
        intent.putExtra("scaleUpIfNeeded", true);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", this.mPhotoSize);
        intent.putExtra("outputY", this.mPhotoSize);
    }

    private void onPhotoCropped(final Uri uri, final boolean z) {
        new AsyncTask<Void, Void, Bitmap>() {
            /* class com.android.settings.users.EditUserPhotoController.AnonymousClass6 */

            /* access modifiers changed from: protected */
            /* JADX WARNING: Removed duplicated region for block: B:20:0x0035 A[SYNTHETIC, Splitter:B:20:0x0035] */
            /* JADX WARNING: Removed duplicated region for block: B:28:0x0042 A[SYNTHETIC, Splitter:B:28:0x0042] */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public android.graphics.Bitmap doInBackground(java.lang.Void... r8) {
                /*
                // Method dump skipped, instructions count: 182
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.settings.users.EditUserPhotoController.AnonymousClass6.doInBackground(java.lang.Void[]):android.graphics.Bitmap");
            }

            /* access modifiers changed from: protected */
            public void onPostExecute(Bitmap bitmap) {
                if (bitmap != null) {
                    EditUserPhotoController.this.mNewUserPhotoBitmap = bitmap;
                    EditUserPhotoController editUserPhotoController = EditUserPhotoController.this;
                    editUserPhotoController.mNewUserPhotoDrawable = CircleFramedDrawable.getInstance(editUserPhotoController.mImageView.getContext(), EditUserPhotoController.this.mNewUserPhotoBitmap);
                    EditUserPhotoController.this.mImageView.setImageDrawable(EditUserPhotoController.this.mNewUserPhotoDrawable);
                }
                new File(EditUserPhotoController.this.mContext.getCacheDir(), "TakeEditUserPhoto2.jpg").delete();
                new File(EditUserPhotoController.this.mContext.getCacheDir(), "CropEditUserPhoto.jpg").delete();
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
    }

    private static int getPhotoSize(Context context) {
        Cursor query = context.getContentResolver().query(ContactsContract.DisplayPhoto.CONTENT_MAX_DIMENSIONS_URI, new String[]{"display_max_dim"}, null, null, null);
        try {
            query.moveToFirst();
            return query.getInt(0);
        } finally {
            query.close();
        }
    }

    private Uri createTempImageUri(Context context, String str, boolean z) {
        File cacheDir = context.getCacheDir();
        cacheDir.mkdirs();
        File file = new File(cacheDir, str);
        if (z) {
            file.delete();
        }
        return FileProvider.getUriForFile(context, "com.android.settings.files", file);
    }

    /* access modifiers changed from: package-private */
    public File saveNewUserPhotoBitmap() {
        if (this.mNewUserPhotoBitmap == null) {
            return null;
        }
        try {
            File file = new File(this.mContext.getCacheDir(), "NewUserPhoto.png");
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            this.mNewUserPhotoBitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            return file;
        } catch (IOException e) {
            Log.e("EditUserPhotoController", "Cannot create temp file", e);
            return null;
        }
    }

    static Bitmap loadNewUserPhotoBitmap(File file) {
        return BitmapFactory.decodeFile(file.getAbsolutePath());
    }

    /* access modifiers changed from: package-private */
    public void removeNewUserPhotoBitmapFile() {
        new File(this.mContext.getCacheDir(), "NewUserPhoto.png").delete();
    }

    /* access modifiers changed from: private */
    public static final class RestrictedMenuItem {
        private final Runnable mAction;
        private final RestrictedLockUtils.EnforcedAdmin mAdmin;
        private final Context mContext;
        private final boolean mIsRestrictedByBase;
        private final String mTitle;

        public RestrictedMenuItem(Context context, String str, String str2, Runnable runnable) {
            this.mContext = context;
            this.mTitle = str;
            this.mAction = runnable;
            int myUserId = UserHandle.myUserId();
            this.mAdmin = RestrictedLockUtilsInternal.checkIfRestrictionEnforced(context, str2, myUserId);
            this.mIsRestrictedByBase = RestrictedLockUtilsInternal.hasBaseUserRestriction(this.mContext, str2, myUserId);
        }

        public String toString() {
            return this.mTitle;
        }

        /* access modifiers changed from: package-private */
        public final void doAction() {
            if (!isRestrictedByBase()) {
                if (isRestrictedByAdmin()) {
                    RestrictedLockUtils.sendShowAdminSupportDetailsIntent(this.mContext, this.mAdmin);
                } else {
                    this.mAction.run();
                }
            }
        }

        /* access modifiers changed from: package-private */
        public final boolean isRestrictedByAdmin() {
            return this.mAdmin != null;
        }

        /* access modifiers changed from: package-private */
        public final boolean isRestrictedByBase() {
            return this.mIsRestrictedByBase;
        }
    }

    /* access modifiers changed from: private */
    public static final class RestrictedPopupMenuAdapter extends ArrayAdapter<RestrictedMenuItem> {
        public RestrictedPopupMenuAdapter(Context context, List<RestrictedMenuItem> list) {
            super(context, C0012R$layout.restricted_popup_menu_item, C0010R$id.text, list);
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            View view2 = super.getView(i, view, viewGroup);
            RestrictedMenuItem restrictedMenuItem = (RestrictedMenuItem) getItem(i);
            TextView textView = (TextView) view2.findViewById(C0010R$id.text);
            ImageView imageView = (ImageView) view2.findViewById(C0010R$id.restricted_icon);
            int i2 = 0;
            textView.setEnabled(!restrictedMenuItem.isRestrictedByAdmin() && !restrictedMenuItem.isRestrictedByBase());
            if (!restrictedMenuItem.isRestrictedByAdmin() || restrictedMenuItem.isRestrictedByBase()) {
                i2 = 8;
            }
            imageView.setVisibility(i2);
            return view2;
        }
    }
}
