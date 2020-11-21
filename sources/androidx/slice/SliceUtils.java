package androidx.slice;

import androidx.slice.core.SliceActionImpl;

public class SliceUtils {
    public static int parseImageMode(SliceItem sliceItem) {
        return SliceActionImpl.parseImageMode(sliceItem);
    }
}
