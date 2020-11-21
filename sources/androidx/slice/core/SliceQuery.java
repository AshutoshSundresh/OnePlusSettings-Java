package androidx.slice.core;

import android.text.TextUtils;
import androidx.slice.Slice;
import androidx.slice.SliceItem;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

public class SliceQuery {

    /* access modifiers changed from: private */
    public interface Filter<T> {
        boolean filter(T t);
    }

    public static boolean hasAnyHints(SliceItem sliceItem, String... strArr) {
        if (strArr == null) {
            return false;
        }
        for (String str : strArr) {
            if (sliceItem.hasHint(str)) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasHints(SliceItem sliceItem, String... strArr) {
        if (strArr == null) {
            return true;
        }
        for (String str : strArr) {
            if (!(TextUtils.isEmpty(str) || sliceItem.hasHint(str))) {
                return false;
            }
        }
        return true;
    }

    public static List<SliceItem> findAll(SliceItem sliceItem, String str) {
        return findAll(sliceItem, str, (String[]) null, (String[]) null);
    }

    public static List<SliceItem> findAll(SliceItem sliceItem, String str, String str2, String str3) {
        return findAll(sliceItem, str, new String[]{str2}, new String[]{str3});
    }

    public static List<SliceItem> findAll(Slice slice, final String str, final String[] strArr, final String[] strArr2) {
        ArrayList arrayList = new ArrayList();
        findAll(toQueue(slice), new Filter<SliceItem>() {
            /* class androidx.slice.core.SliceQuery.AnonymousClass2 */

            public boolean filter(SliceItem sliceItem) {
                return SliceQuery.checkFormat(sliceItem, str) && SliceQuery.hasHints(sliceItem, strArr) && !SliceQuery.hasAnyHints(sliceItem, strArr2);
            }
        }, arrayList);
        return arrayList;
    }

    public static List<SliceItem> findAll(SliceItem sliceItem, final String str, final String[] strArr, final String[] strArr2) {
        ArrayList arrayList = new ArrayList();
        findAll(toQueue(sliceItem), new Filter<SliceItem>() {
            /* class androidx.slice.core.SliceQuery.AnonymousClass3 */

            public boolean filter(SliceItem sliceItem) {
                return SliceQuery.checkFormat(sliceItem, str) && SliceQuery.hasHints(sliceItem, strArr) && !SliceQuery.hasAnyHints(sliceItem, strArr2);
            }
        }, arrayList);
        return arrayList;
    }

    public static SliceItem find(Slice slice, String str, String str2, String str3) {
        return find(slice, str, new String[]{str2}, new String[]{str3});
    }

    public static SliceItem find(Slice slice, String str) {
        return find(slice, str, (String[]) null, (String[]) null);
    }

    public static SliceItem find(SliceItem sliceItem, String str) {
        return find(sliceItem, str, (String[]) null, (String[]) null);
    }

    public static SliceItem find(SliceItem sliceItem, String str, String str2, String str3) {
        return find(sliceItem, str, new String[]{str2}, new String[]{str3});
    }

    public static SliceItem find(Slice slice, final String str, final String[] strArr, final String[] strArr2) {
        if (slice == null) {
            return null;
        }
        return findSliceItem(toQueue(slice), new Filter<SliceItem>() {
            /* class androidx.slice.core.SliceQuery.AnonymousClass4 */

            public boolean filter(SliceItem sliceItem) {
                return SliceQuery.checkFormat(sliceItem, str) && SliceQuery.hasHints(sliceItem, strArr) && !SliceQuery.hasAnyHints(sliceItem, strArr2);
            }
        });
    }

    public static SliceItem findSubtype(Slice slice, final String str, final String str2) {
        if (slice == null) {
            return null;
        }
        return findSliceItem(toQueue(slice), new Filter<SliceItem>() {
            /* class androidx.slice.core.SliceQuery.AnonymousClass5 */

            public boolean filter(SliceItem sliceItem) {
                return SliceQuery.checkFormat(sliceItem, str) && SliceQuery.checkSubtype(sliceItem, str2);
            }
        });
    }

    public static SliceItem findSubtype(SliceItem sliceItem, final String str, final String str2) {
        if (sliceItem == null) {
            return null;
        }
        return findSliceItem(toQueue(sliceItem), new Filter<SliceItem>() {
            /* class androidx.slice.core.SliceQuery.AnonymousClass6 */

            public boolean filter(SliceItem sliceItem) {
                return SliceQuery.checkFormat(sliceItem, str) && SliceQuery.checkSubtype(sliceItem, str2);
            }
        });
    }

    public static SliceItem find(SliceItem sliceItem, final String str, final String[] strArr, final String[] strArr2) {
        if (sliceItem == null) {
            return null;
        }
        return findSliceItem(toQueue(sliceItem), new Filter<SliceItem>() {
            /* class androidx.slice.core.SliceQuery.AnonymousClass7 */

            public boolean filter(SliceItem sliceItem) {
                return SliceQuery.checkFormat(sliceItem, str) && SliceQuery.hasHints(sliceItem, strArr) && !SliceQuery.hasAnyHints(sliceItem, strArr2);
            }
        });
    }

    static boolean checkFormat(SliceItem sliceItem, String str) {
        return str == null || str.equals(sliceItem.getFormat());
    }

    static boolean checkSubtype(SliceItem sliceItem, String str) {
        return str == null || str.equals(sliceItem.getSubType());
    }

    private static Deque<SliceItem> toQueue(Slice slice) {
        ArrayDeque arrayDeque = new ArrayDeque();
        Collections.addAll(arrayDeque, slice.getItemArray());
        return arrayDeque;
    }

    private static Deque<SliceItem> toQueue(SliceItem sliceItem) {
        ArrayDeque arrayDeque = new ArrayDeque();
        arrayDeque.add(sliceItem);
        return arrayDeque;
    }

    private static SliceItem findSliceItem(Deque<SliceItem> deque, Filter<SliceItem> filter) {
        while (!deque.isEmpty()) {
            SliceItem poll = deque.poll();
            if (filter.filter(poll)) {
                return poll;
            }
            if ("slice".equals(poll.getFormat()) || "action".equals(poll.getFormat())) {
                Collections.addAll(deque, poll.getSlice().getItemArray());
            }
        }
        return null;
    }

    private static void findAll(Deque<SliceItem> deque, Filter<SliceItem> filter, List<SliceItem> list) {
        while (!deque.isEmpty()) {
            SliceItem poll = deque.poll();
            if (filter.filter(poll)) {
                list.add(poll);
            }
            if ("slice".equals(poll.getFormat()) || "action".equals(poll.getFormat())) {
                Collections.addAll(deque, poll.getSlice().getItemArray());
            }
        }
    }

    public static SliceItem findTopLevelItem(Slice slice, String str, String str2, String[] strArr, String[] strArr2) {
        SliceItem[] itemArray = slice.getItemArray();
        for (SliceItem sliceItem : itemArray) {
            if (checkFormat(sliceItem, str) && checkSubtype(sliceItem, str2) && hasHints(sliceItem, strArr) && !hasAnyHints(sliceItem, strArr2)) {
                return sliceItem;
            }
        }
        return null;
    }
}
