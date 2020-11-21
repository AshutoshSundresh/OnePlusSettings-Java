package com.oneplus.settings.aboutphone;

import java.util.List;

/* access modifiers changed from: package-private */
public interface Contract$View {
    void cancelToast();

    void displayHardWarePreference(int i, String str, String str2, String str3, String str4);

    void displaySoftWarePreference(List<SoftwareInfoEntity> list);

    void performHapticFeedback();

    void showLongToast(int i);

    void showLongToast(String str);
}
