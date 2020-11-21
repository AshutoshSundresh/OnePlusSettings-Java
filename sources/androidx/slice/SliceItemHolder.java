package androidx.slice;

import android.app.PendingIntent;
import android.os.Parcelable;
import android.text.Spanned;
import androidx.core.text.HtmlCompat;
import androidx.core.util.Pair;
import androidx.versionedparcelable.VersionedParcelable;
import java.util.ArrayList;

public class SliceItemHolder implements VersionedParcelable {
    public static HolderHandler sHandler;
    public static final Object sSerializeLock = new Object();
    Object mCallback;
    int mInt = 0;
    long mLong = 0;
    Parcelable mParcelable = null;
    private SliceItemPool mPool;
    String mStr = null;
    public VersionedParcelable mVersionedParcelable = null;

    public interface HolderHandler {
        void handle(SliceItemHolder sliceItemHolder, String str);
    }

    SliceItemHolder(SliceItemPool sliceItemPool) {
        this.mPool = sliceItemPool;
    }

    public void release() {
        SliceItemPool sliceItemPool = this.mPool;
        if (sliceItemPool != null) {
            sliceItemPool.release(this);
        }
    }

    /* JADX INFO: Can't fix incorrect switch cases order, some code will duplicate */
    public SliceItemHolder(String str, Object obj, boolean z) {
        char c;
        String str2;
        switch (str.hashCode()) {
            case -1422950858:
                if (str.equals("action")) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case 104431:
                if (str.equals("int")) {
                    c = 5;
                    break;
                }
                c = 65535;
                break;
            case 3327612:
                if (str.equals("long")) {
                    c = 6;
                    break;
                }
                c = 65535;
                break;
            case 3556653:
                if (str.equals("text")) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            case 100313435:
                if (str.equals("image")) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 100358090:
                if (str.equals("input")) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case 109526418:
                if (str.equals("slice")) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        switch (c) {
            case 0:
                Pair pair = (Pair) obj;
                F f = pair.first;
                if (f instanceof PendingIntent) {
                    this.mParcelable = f;
                } else if (!z) {
                    throw new IllegalArgumentException("Cannot write callback to parcel");
                }
                this.mVersionedParcelable = pair.second;
                break;
            case 1:
            case 2:
                this.mVersionedParcelable = (VersionedParcelable) obj;
                break;
            case 3:
                this.mParcelable = (Parcelable) obj;
                break;
            case 4:
                if (obj instanceof Spanned) {
                    str2 = HtmlCompat.toHtml((Spanned) obj, 0);
                } else {
                    str2 = (String) obj;
                }
                this.mStr = str2;
                break;
            case 5:
                this.mInt = ((Integer) obj).intValue();
                break;
            case 6:
                this.mLong = ((Long) obj).longValue();
                break;
        }
        HolderHandler holderHandler = sHandler;
        if (holderHandler != null) {
            holderHandler.handle(this, str);
        }
    }

    public Object getObj(String str) {
        HolderHandler holderHandler = sHandler;
        if (holderHandler != null) {
            holderHandler.handle(this, str);
        }
        char c = 65535;
        switch (str.hashCode()) {
            case -1422950858:
                if (str.equals("action")) {
                    c = 0;
                    break;
                }
                break;
            case 104431:
                if (str.equals("int")) {
                    c = 5;
                    break;
                }
                break;
            case 3327612:
                if (str.equals("long")) {
                    c = 6;
                    break;
                }
                break;
            case 3556653:
                if (str.equals("text")) {
                    c = 4;
                    break;
                }
                break;
            case 100313435:
                if (str.equals("image")) {
                    c = 1;
                    break;
                }
                break;
            case 100358090:
                if (str.equals("input")) {
                    c = 3;
                    break;
                }
                break;
            case 109526418:
                if (str.equals("slice")) {
                    c = 2;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                if (this.mParcelable == null && this.mVersionedParcelable == null) {
                    return null;
                }
                Object obj = this.mParcelable;
                if (obj == null) {
                    obj = this.mCallback;
                }
                return new Pair(obj, (Slice) this.mVersionedParcelable);
            case 1:
            case 2:
                return this.mVersionedParcelable;
            case 3:
                return this.mParcelable;
            case 4:
                String str2 = this.mStr;
                return (str2 == null || str2.length() == 0) ? "" : HtmlCompat.fromHtml(this.mStr, 0);
            case 5:
                return Integer.valueOf(this.mInt);
            case 6:
                return Long.valueOf(this.mLong);
            default:
                throw new IllegalArgumentException("Unrecognized format " + str);
        }
    }

    public static class SliceItemPool {
        private final ArrayList<SliceItemHolder> mCached = new ArrayList<>();

        public SliceItemHolder get() {
            if (this.mCached.size() <= 0) {
                return new SliceItemHolder(this);
            }
            ArrayList<SliceItemHolder> arrayList = this.mCached;
            return arrayList.remove(arrayList.size() - 1);
        }

        public void release(SliceItemHolder sliceItemHolder) {
            sliceItemHolder.mParcelable = null;
            sliceItemHolder.mCallback = null;
            sliceItemHolder.mVersionedParcelable = null;
            sliceItemHolder.mInt = 0;
            sliceItemHolder.mLong = 0;
            sliceItemHolder.mStr = null;
            this.mCached.add(sliceItemHolder);
        }
    }
}
