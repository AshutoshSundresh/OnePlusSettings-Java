package com.oneplus.settings.laboratory;

public class OPLabPluginModel {
    private String action;
    private int featureIconId;
    private String featureKey;
    private String featureSummary;
    private String featureTitle;
    private String[] multiToggleName;
    private int toggleCount;

    public String getAction() {
        return this.action;
    }

    public void setAction(String str) {
        this.action = str;
    }

    public void setFeatureIconId(int i) {
        this.featureIconId = i;
    }

    public int geFeatureIconId() {
        return this.featureIconId;
    }

    public String[] getMultiToggleName() {
        return this.multiToggleName;
    }

    public void setMultiToggleName(String[] strArr) {
        this.multiToggleName = strArr;
    }

    public int getToggleCount() {
        return this.toggleCount;
    }

    public void setToggleCount(int i) {
        this.toggleCount = i;
    }

    public String getFeatureTitle() {
        return this.featureTitle;
    }

    public void setFeatureTitle(String str) {
        this.featureTitle = str;
    }

    public String getFeatureSummary() {
        return this.featureSummary;
    }

    public void setFeatureSummary(String str) {
        this.featureSummary = str;
    }

    public String getFeatureKey() {
        return this.featureKey;
    }

    public void setFeatureKey(String str) {
        this.featureKey = str;
    }
}
