package com.oneplus.settings.aboutphone;

public class SoftwareInfoEntity {
    private String intent;
    private int resIcon;
    private CharSequence summary;
    private String title;

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String str) {
        this.title = str;
    }

    public CharSequence getSummary() {
        return this.summary;
    }

    public void setSummary(CharSequence charSequence) {
        this.summary = charSequence;
    }

    public int getResIcon() {
        return this.resIcon;
    }

    public void setResIcon(int i) {
        this.resIcon = i;
    }

    public String getIntent() {
        return this.intent;
    }

    public void setIntent(String str) {
        this.intent = str;
    }
}
