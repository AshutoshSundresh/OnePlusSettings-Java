package androidx.slice;

import android.app.slice.Slice;
import android.app.slice.SliceItem;
import android.app.slice.SliceSpec;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import androidx.collection.ArraySet;
import androidx.core.graphics.drawable.IconCompat;
import androidx.slice.Slice;
import java.util.Set;

public class SliceConvert {
    public static Slice unwrap(Slice slice) {
        if (slice == null || slice.getUri() == null) {
            return null;
        }
        Slice.Builder builder = new Slice.Builder(slice.getUri(), unwrap(slice.getSpec()));
        builder.addHints(slice.getHints());
        SliceItem[] itemArray = slice.getItemArray();
        for (SliceItem sliceItem : itemArray) {
            String format = sliceItem.getFormat();
            char c = 65535;
            switch (format.hashCode()) {
                case -1422950858:
                    if (format.equals("action")) {
                        c = 3;
                        break;
                    }
                    break;
                case 104431:
                    if (format.equals("int")) {
                        c = 5;
                        break;
                    }
                    break;
                case 3327612:
                    if (format.equals("long")) {
                        c = 6;
                        break;
                    }
                    break;
                case 3556653:
                    if (format.equals("text")) {
                        c = 4;
                        break;
                    }
                    break;
                case 100313435:
                    if (format.equals("image")) {
                        c = 1;
                        break;
                    }
                    break;
                case 100358090:
                    if (format.equals("input")) {
                        c = 2;
                        break;
                    }
                    break;
                case 109526418:
                    if (format.equals("slice")) {
                        c = 0;
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                    builder.addSubSlice(unwrap(sliceItem.getSlice()), sliceItem.getSubType());
                    break;
                case 1:
                    builder.addIcon(sliceItem.getIcon().toIcon(), sliceItem.getSubType(), sliceItem.getHints());
                    break;
                case 2:
                    builder.addRemoteInput(sliceItem.getRemoteInput(), sliceItem.getSubType(), sliceItem.getHints());
                    break;
                case 3:
                    builder.addAction(sliceItem.getAction(), unwrap(sliceItem.getSlice()), sliceItem.getSubType());
                    break;
                case 4:
                    builder.addText(sliceItem.getText(), sliceItem.getSubType(), sliceItem.getHints());
                    break;
                case 5:
                    builder.addInt(sliceItem.getInt(), sliceItem.getSubType(), sliceItem.getHints());
                    break;
                case 6:
                    builder.addLong(sliceItem.getLong(), sliceItem.getSubType(), sliceItem.getHints());
                    break;
            }
        }
        return builder.build();
    }

    private static SliceSpec unwrap(SliceSpec sliceSpec) {
        if (sliceSpec == null) {
            return null;
        }
        return new SliceSpec(sliceSpec.getType(), sliceSpec.getRevision());
    }

    static Set<SliceSpec> unwrap(Set<SliceSpec> set) {
        ArraySet arraySet = new ArraySet();
        if (set != null) {
            for (SliceSpec sliceSpec : set) {
                arraySet.add(unwrap(sliceSpec));
            }
        }
        return arraySet;
    }

    public static Slice wrap(Slice slice, Context context) {
        if (slice == null || slice.getUri() == null) {
            return null;
        }
        Slice.Builder builder = new Slice.Builder(slice.getUri());
        builder.addHints(slice.getHints());
        builder.setSpec(wrap(slice.getSpec()));
        for (SliceItem sliceItem : slice.getItems()) {
            String format = sliceItem.getFormat();
            char c = 65535;
            switch (format.hashCode()) {
                case -1422950858:
                    if (format.equals("action")) {
                        c = 3;
                        break;
                    }
                    break;
                case 104431:
                    if (format.equals("int")) {
                        c = 5;
                        break;
                    }
                    break;
                case 3327612:
                    if (format.equals("long")) {
                        c = 6;
                        break;
                    }
                    break;
                case 3556653:
                    if (format.equals("text")) {
                        c = 4;
                        break;
                    }
                    break;
                case 100313435:
                    if (format.equals("image")) {
                        c = 1;
                        break;
                    }
                    break;
                case 100358090:
                    if (format.equals("input")) {
                        c = 2;
                        break;
                    }
                    break;
                case 109526418:
                    if (format.equals("slice")) {
                        c = 0;
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                    builder.addSubSlice(wrap(sliceItem.getSlice(), context), sliceItem.getSubType());
                    break;
                case 1:
                    try {
                        builder.addIcon(IconCompat.createFromIcon(context, sliceItem.getIcon()), sliceItem.getSubType(), sliceItem.getHints());
                        break;
                    } catch (IllegalArgumentException e) {
                        Log.w("SliceConvert", "The icon resource isn't available.", e);
                        break;
                    } catch (Resources.NotFoundException e2) {
                        Log.w("SliceConvert", "The icon resource isn't available.", e2);
                        break;
                    }
                case 2:
                    builder.addRemoteInput(sliceItem.getRemoteInput(), sliceItem.getSubType(), sliceItem.getHints());
                    break;
                case 3:
                    builder.addAction(sliceItem.getAction(), wrap(sliceItem.getSlice(), context), sliceItem.getSubType());
                    break;
                case 4:
                    builder.addText(sliceItem.getText(), sliceItem.getSubType(), sliceItem.getHints());
                    break;
                case 5:
                    builder.addInt(sliceItem.getInt(), sliceItem.getSubType(), sliceItem.getHints());
                    break;
                case 6:
                    builder.addLong(sliceItem.getLong(), sliceItem.getSubType(), sliceItem.getHints());
                    break;
            }
        }
        return builder.build();
    }

    private static SliceSpec wrap(SliceSpec sliceSpec) {
        if (sliceSpec == null) {
            return null;
        }
        return new SliceSpec(sliceSpec.getType(), sliceSpec.getRevision());
    }

    public static Set<SliceSpec> wrap(Set<SliceSpec> set) {
        ArraySet arraySet = new ArraySet();
        if (set != null) {
            for (SliceSpec sliceSpec : set) {
                arraySet.add(wrap(sliceSpec));
            }
        }
        return arraySet;
    }
}
