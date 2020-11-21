package com.oneplus.settings.better;

import android.graphics.drawable.Drawable;
import android.text.TextUtils;

public class OPDarkModeAppModel extends OPAppModel {
    private Drawable appIcon;
    private String appLabel;
    private int appopsMode;
    private boolean disable;
    private boolean editMode;
    private boolean isGameAPP;
    private boolean isSelected;
    private String label;
    private String pkgName;
    private Drawable shortCutIcon;
    private String shortCutId;
    private int type;
    private int uid;
    private int versionCode;

    @Override // com.oneplus.settings.better.OPAppModel
    public int getVersionCode() {
        return this.versionCode;
    }

    public void setVersionCode(int i) {
        this.versionCode = i;
    }

    @Override // com.oneplus.settings.better.OPAppModel
    public boolean isDisable() {
        return this.disable;
    }

    @Override // com.oneplus.settings.better.OPAppModel
    public void setDisable(boolean z) {
        this.disable = z;
    }

    @Override // com.oneplus.settings.better.OPAppModel
    public int getAppopsMode() {
        return this.appopsMode;
    }

    @Override // com.oneplus.settings.better.OPAppModel
    public void setAppopsMode(int i) {
        this.appopsMode = i;
    }

    public OPDarkModeAppModel(String str, String str2, String str3, int i, boolean z) {
        super(str, str2, str3, i, z);
        this.pkgName = str;
        this.label = str2;
        this.shortCutId = str3;
        this.uid = i;
        this.isSelected = z;
    }

    @Override // com.oneplus.settings.better.OPAppModel
    public boolean isEditMode() {
        return this.editMode;
    }

    @Override // com.oneplus.settings.better.OPAppModel
    public void setEditMode(boolean z) {
        this.editMode = z;
    }

    @Override // com.oneplus.settings.better.OPAppModel
    public boolean isGameAPP() {
        return this.isGameAPP;
    }

    @Override // com.oneplus.settings.better.OPAppModel
    public void setGameAPP(boolean z) {
        this.isGameAPP = z;
    }

    @Override // com.oneplus.settings.better.OPAppModel
    public boolean isSelected() {
        return this.isSelected;
    }

    @Override // com.oneplus.settings.better.OPAppModel
    public void setSelected(boolean z) {
        this.isSelected = z;
    }

    @Override // com.oneplus.settings.better.OPAppModel
    public String getShortCutId() {
        return this.shortCutId;
    }

    @Override // com.oneplus.settings.better.OPAppModel
    public int getType() {
        return this.type;
    }

    @Override // com.oneplus.settings.better.OPAppModel
    public void setType(int i) {
        this.type = i;
    }

    @Override // com.oneplus.settings.better.OPAppModel
    public String getPkgName() {
        return this.pkgName;
    }

    @Override // com.oneplus.settings.better.OPAppModel
    public String getLabel() {
        return this.label;
    }

    @Override // com.oneplus.settings.better.OPAppModel
    public String getAppLabel() {
        return this.appLabel;
    }

    @Override // com.oneplus.settings.better.OPAppModel
    public void setAppLabel(String str) {
        this.appLabel = str;
    }

    @Override // com.oneplus.settings.better.OPAppModel
    public Drawable getAppIcon() {
        return this.appIcon;
    }

    @Override // com.oneplus.settings.better.OPAppModel
    public void setAppIcon(Drawable drawable) {
        this.appIcon = drawable;
    }

    @Override // com.oneplus.settings.better.OPAppModel
    public Drawable getShortCutIcon() {
        return this.shortCutIcon;
    }

    @Override // com.oneplus.settings.better.OPAppModel
    public void setShortCutIcon(Drawable drawable) {
        this.shortCutIcon = drawable;
    }

    @Override // com.oneplus.settings.better.OPAppModel
    public int getUid() {
        return this.uid;
    }

    @Override // com.oneplus.settings.better.OPAppModel
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || OPDarkModeAppModel.class != obj.getClass()) {
            return false;
        }
        OPDarkModeAppModel oPDarkModeAppModel = (OPDarkModeAppModel) obj;
        return TextUtils.equals(oPDarkModeAppModel.pkgName, this.pkgName) && oPDarkModeAppModel.getUid() == getUid();
    }
}
