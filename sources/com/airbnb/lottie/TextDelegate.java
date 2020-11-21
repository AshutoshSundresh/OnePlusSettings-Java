package com.airbnb.lottie;

import java.util.HashMap;
import java.util.Map;

public class TextDelegate {
    private boolean cacheText = true;
    private final Map<String, String> stringMap = new HashMap();

    private String getText(String str) {
        return str;
    }

    TextDelegate() {
    }

    public final String getTextInternal(String str) {
        if (this.cacheText && this.stringMap.containsKey(str)) {
            return this.stringMap.get(str);
        }
        getText(str);
        if (this.cacheText) {
            this.stringMap.put(str, str);
        }
        return str;
    }
}
