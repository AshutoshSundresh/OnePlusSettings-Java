package com.android.settings.slices;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.IntentFilter;
import android.widget.Toast;
import com.android.settings.C0017R$string;

public interface Sliceable {
    default void copy() {
    }

    default Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return null;
    }

    default IntentFilter getIntentFilter() {
        return null;
    }

    default boolean hasAsyncUpdate() {
        return false;
    }

    default boolean isCopyableSlice() {
        return false;
    }

    default boolean isPublicSlice() {
        return false;
    }

    default boolean isSliceable() {
        return false;
    }

    default boolean useDynamicSliceSummary() {
        return false;
    }

    static default void setCopyContent(Context context, CharSequence charSequence, CharSequence charSequence2) {
        ((ClipboardManager) context.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("text", charSequence));
        Toast.makeText(context, context.getString(C0017R$string.copyable_slice_toast, charSequence2), 0).show();
    }
}
