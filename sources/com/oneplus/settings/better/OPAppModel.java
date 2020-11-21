package com.oneplus.settings.better;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

public class OPAppModel implements Parcelable {
    public static final Parcelable.Creator<OPAppModel> CREATOR = new Parcelable.Creator<OPAppModel>() {
        /* class com.oneplus.settings.better.OPAppModel.AnonymousClass1 */

        @Override // android.os.Parcelable.Creator
        public OPAppModel createFromParcel(Parcel parcel) {
            return new OPAppModel(parcel);
        }

        @Override // android.os.Parcelable.Creator
        public OPAppModel[] newArray(int i) {
            return new OPAppModel[i];
        }
    };
    private Drawable appIcon;
    private String appLabel;
    private int appopsMode;
    private boolean disable;
    private boolean editMode;
    private boolean isGameAPP;
    private boolean isSelected;
    private String label;
    private int lockMode;
    private String pkgName;
    private Drawable shortCutIcon;
    private String shortCutId;
    private int type;
    private int uid;
    private int versionCode;

    public int describeContents() {
        return 0;
    }

    public int getVersionCode() {
        return this.versionCode;
    }

    public boolean isDisable() {
        return this.disable;
    }

    public void setDisable(boolean z) {
        this.disable = z;
    }

    public int getAppopsMode() {
        return this.appopsMode;
    }

    public void setAppopsMode(int i) {
        this.appopsMode = i;
    }

    public OPAppModel(String str, String str2, String str3, int i, boolean z) {
        this.pkgName = str;
        this.label = str2;
        this.shortCutId = str3;
        this.uid = i;
        this.isSelected = z;
    }

    public boolean isEditMode() {
        return this.editMode;
    }

    public void setEditMode(boolean z) {
        this.editMode = z;
    }

    public boolean isGameAPP() {
        return this.isGameAPP;
    }

    public void setGameAPP(boolean z) {
        this.isGameAPP = z;
    }

    public boolean isSelected() {
        return this.isSelected;
    }

    public void setSelected(boolean z) {
        this.isSelected = z;
    }

    public String getShortCutId() {
        return this.shortCutId;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int i) {
        this.type = i;
    }

    public String getPkgName() {
        return this.pkgName;
    }

    public String getLabel() {
        return this.label;
    }

    public String getAppLabel() {
        return this.appLabel;
    }

    public void setAppLabel(String str) {
        this.appLabel = str;
    }

    public Drawable getAppIcon() {
        return this.appIcon;
    }

    public void setAppIcon(Drawable drawable) {
        this.appIcon = drawable;
    }

    public Drawable getShortCutIcon() {
        return this.shortCutIcon;
    }

    public void setShortCutIcon(Drawable drawable) {
        this.shortCutIcon = drawable;
    }

    public int getUid() {
        return this.uid;
    }

    public OPAppModel(Parcel parcel) {
        readFromParcel(parcel);
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.pkgName);
        parcel.writeString(this.label);
        parcel.writeInt(this.uid);
        parcel.writeInt(this.lockMode);
        parcel.writeInt(this.isSelected ? 1 : 0);
        parcel.writeInt(this.isGameAPP ? 1 : 0);
    }

    public void readFromParcel(Parcel parcel) {
        this.pkgName = parcel.readString();
        this.pkgName = parcel.readString();
        this.uid = parcel.readInt();
        this.lockMode = parcel.readInt();
        boolean z = false;
        this.isSelected = parcel.readInt() == 1;
        if (parcel.readInt() == 1) {
            z = true;
        }
        this.isGameAPP = z;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return obj != null && getClass() == obj.getClass() && TextUtils.equals(((OPAppModel) obj).pkgName, this.pkgName);
    }
}
