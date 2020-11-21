package com.android.settingslib.inputmethod;

import android.content.ContentResolver;
import android.content.Context;
import android.util.Log;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class InputMethodSettingValuesWrapper {
    private static final String TAG = "InputMethodSettingValuesWrapper";
    private static volatile InputMethodSettingValuesWrapper sInstance;
    private final ContentResolver mContentResolver;
    private final InputMethodManager mImm;
    private final ArrayList<InputMethodInfo> mMethodList = new ArrayList<>();

    public static InputMethodSettingValuesWrapper getInstance(Context context) {
        if (sInstance == null) {
            synchronized (TAG) {
                if (sInstance == null) {
                    sInstance = new InputMethodSettingValuesWrapper(context);
                }
            }
        }
        return sInstance;
    }

    private InputMethodSettingValuesWrapper(Context context) {
        this.mContentResolver = context.getContentResolver();
        this.mImm = (InputMethodManager) context.getSystemService(InputMethodManager.class);
        refreshAllInputMethodAndSubtypes();
    }

    public void refreshAllInputMethodAndSubtypes() {
        this.mMethodList.clear();
        this.mMethodList.addAll(this.mImm.getInputMethodList());
    }

    public List<InputMethodInfo> getInputMethodList() {
        return new ArrayList(this.mMethodList);
    }

    public boolean isAlwaysCheckedIme(InputMethodInfo inputMethodInfo) {
        boolean isEnabledImi = isEnabledImi(inputMethodInfo);
        if (getEnabledInputMethodList().size() <= 1 && isEnabledImi) {
            return true;
        }
        int enabledValidNonAuxAsciiCapableImeCount = getEnabledValidNonAuxAsciiCapableImeCount();
        if (enabledValidNonAuxAsciiCapableImeCount > 1 || ((enabledValidNonAuxAsciiCapableImeCount == 1 && !isEnabledImi) || !inputMethodInfo.isSystem() || !InputMethodAndSubtypeUtil.isValidNonAuxAsciiCapableIme(inputMethodInfo))) {
            return false;
        }
        return true;
    }

    private int getEnabledValidNonAuxAsciiCapableImeCount() {
        int i = 0;
        for (InputMethodInfo inputMethodInfo : getEnabledInputMethodList()) {
            if (InputMethodAndSubtypeUtil.isValidNonAuxAsciiCapableIme(inputMethodInfo)) {
                i++;
            }
        }
        if (i == 0) {
            Log.w(TAG, "No \"enabledValidNonAuxAsciiCapableIme\"s found.");
        }
        return i;
    }

    public boolean isEnabledImi(InputMethodInfo inputMethodInfo) {
        for (InputMethodInfo inputMethodInfo2 : getEnabledInputMethodList()) {
            if (inputMethodInfo2.getId().equals(inputMethodInfo.getId())) {
                return true;
            }
        }
        return false;
    }

    private ArrayList<InputMethodInfo> getEnabledInputMethodList() {
        HashMap<String, HashSet<String>> enabledInputMethodsAndSubtypeList = InputMethodAndSubtypeUtil.getEnabledInputMethodsAndSubtypeList(this.mContentResolver);
        ArrayList<InputMethodInfo> arrayList = new ArrayList<>();
        Iterator<InputMethodInfo> it = this.mMethodList.iterator();
        while (it.hasNext()) {
            InputMethodInfo next = it.next();
            if (enabledInputMethodsAndSubtypeList.keySet().contains(next.getId())) {
                arrayList.add(next);
            }
        }
        return arrayList;
    }
}
