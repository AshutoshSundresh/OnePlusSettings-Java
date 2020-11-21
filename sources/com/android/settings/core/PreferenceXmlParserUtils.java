package com.android.settings.core;

import android.content.Context;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Xml;
import com.android.internal.R;
import com.android.settings.R$styleable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.xmlpull.v1.XmlPullParserException;

public class PreferenceXmlParserUtils {
    static final String PREF_SCREEN_TAG = "PreferenceScreen";
    private static final List<String> SUPPORTED_PREF_TYPES = Arrays.asList("Preference", "PreferenceCategory", PREF_SCREEN_TAG, "com.android.settings.widget.WorkOnlyCategory");

    private static boolean hasFlag(int i, int i2) {
        return (i & i2) != 0;
    }

    @Deprecated
    public static String getDataTitle(Context context, AttributeSet attributeSet) {
        return getStringData(context, attributeSet, R.styleable.Preference, 4);
    }

    public static List<Bundle> extractMetadata(Context context, int i, int i2) throws IOException, XmlPullParserException {
        int next;
        ArrayList arrayList = new ArrayList();
        if (i <= 0) {
            Log.d("PreferenceXmlParserUtil", i + " is invalid.");
            return arrayList;
        }
        XmlResourceParser xml = context.getResources().getXml(i);
        do {
            next = xml.next();
            if (next == 1) {
                break;
            }
        } while (next != 2);
        int depth = xml.getDepth();
        boolean hasFlag = hasFlag(i2, 1);
        while (true) {
            if (next == 2) {
                String name = xml.getName();
                if ((hasFlag || !TextUtils.equals(PREF_SCREEN_TAG, name)) && (SUPPORTED_PREF_TYPES.contains(name) || name.endsWith("Preference"))) {
                    Bundle bundle = new Bundle();
                    AttributeSet asAttributeSet = Xml.asAttributeSet(xml);
                    TypedArray obtainStyledAttributes = context.obtainStyledAttributes(asAttributeSet, R$styleable.Preference);
                    TypedArray typedArray = null;
                    if (hasFlag) {
                        typedArray = context.obtainStyledAttributes(asAttributeSet, R$styleable.PreferenceScreen);
                    }
                    if (hasFlag(i2, 4)) {
                        bundle.putString("type", name);
                    }
                    if (hasFlag(i2, 2)) {
                        bundle.putString("key", getKey(obtainStyledAttributes));
                    }
                    if (hasFlag(i2, 8)) {
                        bundle.putString("controller", getController(obtainStyledAttributes));
                    }
                    if (hasFlag(i2, 16)) {
                        bundle.putString("title", getTitle(obtainStyledAttributes));
                    }
                    if (hasFlag(i2, 32)) {
                        bundle.putString("summary", getSummary(obtainStyledAttributes));
                    }
                    if (hasFlag(i2, 64)) {
                        bundle.putInt("icon", getIcon(obtainStyledAttributes));
                    }
                    if (hasFlag(i2, 256)) {
                        bundle.putString("keywords", getKeywords(obtainStyledAttributes));
                    }
                    if (hasFlag(i2, 512)) {
                        bundle.putBoolean("searchable", isSearchable(obtainStyledAttributes));
                    }
                    if (hasFlag(i2, 1024) && hasFlag) {
                        bundle.putBoolean("staticPreferenceLocation", isAppended(typedArray));
                    }
                    if (hasFlag(i2, 2048)) {
                        bundle.putString("unavailable_slice_subtitle", getUnavailableSliceSubtitle(obtainStyledAttributes));
                    }
                    if (hasFlag(i2, 4096)) {
                        bundle.putBoolean("for_work", isForWork(obtainStyledAttributes));
                    }
                    arrayList.add(bundle);
                    obtainStyledAttributes.recycle();
                }
            }
            next = xml.next();
            if (next == 1 || (next == 3 && xml.getDepth() <= depth)) {
                xml.close();
            }
        }
        xml.close();
        return arrayList;
    }

    @Deprecated
    private static String getStringData(Context context, AttributeSet attributeSet, int[] iArr, int i) {
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, iArr);
        String string = obtainStyledAttributes.getString(i);
        obtainStyledAttributes.recycle();
        return string;
    }

    private static String getKey(TypedArray typedArray) {
        return typedArray.getString(6);
    }

    private static String getTitle(TypedArray typedArray) {
        return typedArray.getString(4);
    }

    private static String getSummary(TypedArray typedArray) {
        return typedArray.getString(7);
    }

    private static String getController(TypedArray typedArray) {
        return typedArray.getString(R$styleable.Preference_controller);
    }

    private static int getIcon(TypedArray typedArray) {
        return typedArray.getResourceId(0, 0);
    }

    private static boolean isSearchable(TypedArray typedArray) {
        return typedArray.getBoolean(R$styleable.Preference_searchable, true);
    }

    private static String getKeywords(TypedArray typedArray) {
        return typedArray.getString(R$styleable.Preference_keywords);
    }

    private static boolean isAppended(TypedArray typedArray) {
        return typedArray.getInt(R$styleable.PreferenceScreen_staticPreferenceLocation, 0) == 1;
    }

    private static String getUnavailableSliceSubtitle(TypedArray typedArray) {
        return typedArray.getString(R$styleable.Preference_unavailableSliceSubtitle);
    }

    private static boolean isForWork(TypedArray typedArray) {
        return typedArray.getBoolean(R$styleable.Preference_forWork, false);
    }
}
