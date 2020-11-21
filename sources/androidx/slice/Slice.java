package androidx.slice;

import android.app.PendingIntent;
import android.app.RemoteInput;
import android.app.slice.SliceManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import androidx.core.graphics.drawable.IconCompat;
import androidx.core.util.Preconditions;
import androidx.slice.compat.SliceProviderCompat;
import androidx.versionedparcelable.CustomVersionedParcelable;
import androidx.versionedparcelable.VersionedParcelable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public final class Slice extends CustomVersionedParcelable implements VersionedParcelable {
    static final String[] NO_HINTS = new String[0];
    static final SliceItem[] NO_ITEMS = new SliceItem[0];
    String[] mHints = NO_HINTS;
    SliceItem[] mItems = NO_ITEMS;
    SliceSpec mSpec = null;
    String mUri = null;

    public void onPreParceling(boolean z) {
    }

    Slice(ArrayList<SliceItem> arrayList, String[] strArr, Uri uri, SliceSpec sliceSpec) {
        this.mHints = strArr;
        this.mItems = (SliceItem[]) arrayList.toArray(new SliceItem[arrayList.size()]);
        this.mUri = uri.toString();
        this.mSpec = sliceSpec;
    }

    public Slice() {
    }

    public Slice(Bundle bundle) {
        SliceSpec sliceSpec = null;
        this.mHints = bundle.getStringArray("hints");
        Parcelable[] parcelableArray = bundle.getParcelableArray("items");
        this.mItems = new SliceItem[parcelableArray.length];
        int i = 0;
        while (true) {
            SliceItem[] sliceItemArr = this.mItems;
            if (i >= sliceItemArr.length) {
                break;
            }
            if (parcelableArray[i] instanceof Bundle) {
                sliceItemArr[i] = new SliceItem((Bundle) parcelableArray[i]);
            }
            i++;
        }
        this.mUri = bundle.getParcelable("uri").toString();
        this.mSpec = bundle.containsKey("type") ? new SliceSpec(bundle.getString("type"), bundle.getInt("revision")) : sliceSpec;
    }

    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putStringArray("hints", this.mHints);
        Parcelable[] parcelableArr = new Parcelable[this.mItems.length];
        int i = 0;
        while (true) {
            SliceItem[] sliceItemArr = this.mItems;
            if (i >= sliceItemArr.length) {
                break;
            }
            parcelableArr[i] = sliceItemArr[i].toBundle();
            i++;
        }
        bundle.putParcelableArray("items", parcelableArr);
        bundle.putParcelable("uri", Uri.parse(this.mUri));
        SliceSpec sliceSpec = this.mSpec;
        if (sliceSpec != null) {
            bundle.putString("type", sliceSpec.getType());
            bundle.putInt("revision", this.mSpec.getRevision());
        }
        return bundle;
    }

    public SliceSpec getSpec() {
        return this.mSpec;
    }

    public Uri getUri() {
        return Uri.parse(this.mUri);
    }

    public List<SliceItem> getItems() {
        return Arrays.asList(this.mItems);
    }

    public SliceItem[] getItemArray() {
        return this.mItems;
    }

    public List<String> getHints() {
        return Arrays.asList(this.mHints);
    }

    public String[] getHintArray() {
        return this.mHints;
    }

    public boolean hasHint(String str) {
        return ArrayUtils.contains(this.mHints, str);
    }

    public void onPostParceling() {
        for (int length = this.mItems.length - 1; length >= 0; length--) {
            SliceItem[] sliceItemArr = this.mItems;
            if (sliceItemArr[length].mObj == null) {
                SliceItem[] sliceItemArr2 = (SliceItem[]) ArrayUtils.removeElement(SliceItem.class, sliceItemArr, sliceItemArr[length]);
                this.mItems = sliceItemArr2;
                if (sliceItemArr2 == null) {
                    this.mItems = new SliceItem[0];
                }
            }
        }
    }

    public static class Builder {
        private int mChildId;
        private ArrayList<String> mHints = new ArrayList<>();
        private ArrayList<SliceItem> mItems = new ArrayList<>();
        private SliceSpec mSpec;
        private final Uri mUri;

        public Builder(Uri uri) {
            this.mUri = uri;
        }

        public Builder(Builder builder) {
            this.mUri = builder.getChildUri();
        }

        private Uri getChildUri() {
            Uri.Builder appendPath = this.mUri.buildUpon().appendPath("_gen");
            int i = this.mChildId;
            this.mChildId = i + 1;
            return appendPath.appendPath(String.valueOf(i)).build();
        }

        public Builder setSpec(SliceSpec sliceSpec) {
            this.mSpec = sliceSpec;
            return this;
        }

        public Builder addHints(String... strArr) {
            this.mHints.addAll(Arrays.asList(strArr));
            return this;
        }

        public Builder addHints(List<String> list) {
            addHints((String[]) list.toArray(new String[list.size()]));
            return this;
        }

        public Builder addSubSlice(Slice slice) {
            Preconditions.checkNotNull(slice);
            addSubSlice(slice, null);
            return this;
        }

        public Builder addSubSlice(Slice slice, String str) {
            Preconditions.checkNotNull(slice);
            this.mItems.add(new SliceItem(slice, "slice", str, slice.getHintArray()));
            return this;
        }

        public Builder addAction(PendingIntent pendingIntent, Slice slice, String str) {
            Preconditions.checkNotNull(pendingIntent);
            Preconditions.checkNotNull(slice);
            this.mItems.add(new SliceItem(pendingIntent, slice, "action", str, slice.getHintArray()));
            return this;
        }

        public Builder addText(CharSequence charSequence, String str, String... strArr) {
            this.mItems.add(new SliceItem(charSequence, "text", str, strArr));
            return this;
        }

        public Builder addText(CharSequence charSequence, String str, List<String> list) {
            addText(charSequence, str, (String[]) list.toArray(new String[list.size()]));
            return this;
        }

        public Builder addIcon(IconCompat iconCompat, String str, String... strArr) {
            Preconditions.checkNotNull(iconCompat);
            if (Slice.isValidIcon(iconCompat)) {
                this.mItems.add(new SliceItem(iconCompat, "image", str, strArr));
            }
            return this;
        }

        public Builder addIcon(IconCompat iconCompat, String str, List<String> list) {
            Preconditions.checkNotNull(iconCompat);
            if (Slice.isValidIcon(iconCompat)) {
                addIcon(iconCompat, str, (String[]) list.toArray(new String[list.size()]));
            }
            return this;
        }

        public Builder addRemoteInput(RemoteInput remoteInput, String str, List<String> list) {
            Preconditions.checkNotNull(remoteInput);
            addRemoteInput(remoteInput, str, (String[]) list.toArray(new String[list.size()]));
            return this;
        }

        public Builder addRemoteInput(RemoteInput remoteInput, String str, String... strArr) {
            Preconditions.checkNotNull(remoteInput);
            this.mItems.add(new SliceItem(remoteInput, "input", str, strArr));
            return this;
        }

        public Builder addInt(int i, String str, String... strArr) {
            this.mItems.add(new SliceItem(Integer.valueOf(i), "int", str, strArr));
            return this;
        }

        public Builder addInt(int i, String str, List<String> list) {
            addInt(i, str, (String[]) list.toArray(new String[list.size()]));
            return this;
        }

        public Builder addLong(long j, String str, String... strArr) {
            this.mItems.add(new SliceItem(Long.valueOf(j), "long", str, strArr));
            return this;
        }

        public Builder addLong(long j, String str, List<String> list) {
            addLong(j, str, (String[]) list.toArray(new String[list.size()]));
            return this;
        }

        @Deprecated
        public Builder addTimestamp(long j, String str, String... strArr) {
            this.mItems.add(new SliceItem(Long.valueOf(j), "long", str, strArr));
            return this;
        }

        public Builder addItem(SliceItem sliceItem) {
            this.mItems.add(sliceItem);
            return this;
        }

        public Slice build() {
            ArrayList<SliceItem> arrayList = this.mItems;
            ArrayList<String> arrayList2 = this.mHints;
            return new Slice(arrayList, (String[]) arrayList2.toArray(new String[arrayList2.size()]), this.mUri, this.mSpec);
        }
    }

    public String toString() {
        return toString("");
    }

    public String toString(String str) {
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append("Slice ");
        String[] strArr = this.mHints;
        if (strArr.length > 0) {
            appendHints(sb, strArr);
            sb.append(' ');
        }
        sb.append('[');
        sb.append(this.mUri);
        sb.append("] {\n");
        String str2 = str + "  ";
        int i = 0;
        while (true) {
            SliceItem[] sliceItemArr = this.mItems;
            if (i < sliceItemArr.length) {
                sb.append(sliceItemArr[i].toString(str2));
                i++;
            } else {
                sb.append(str);
                sb.append('}');
                return sb.toString();
            }
        }
    }

    public static void appendHints(StringBuilder sb, String[] strArr) {
        if (!(strArr == null || strArr.length == 0)) {
            sb.append('(');
            int length = strArr.length - 1;
            for (int i = 0; i < length; i++) {
                sb.append(strArr[i]);
                sb.append(", ");
            }
            sb.append(strArr[length]);
            sb.append(")");
        }
    }

    public static Slice bindSlice(Context context, Uri uri, Set<SliceSpec> set) {
        if (Build.VERSION.SDK_INT >= 28) {
            return callBindSlice(context, uri, set);
        }
        return SliceProviderCompat.bindSlice(context, uri, set);
    }

    private static Slice callBindSlice(Context context, Uri uri, Set<SliceSpec> set) {
        return SliceConvert.wrap(((SliceManager) context.getSystemService(SliceManager.class)).bindSlice(uri, SliceConvert.unwrap(set)), context);
    }

    static boolean isValidIcon(IconCompat iconCompat) {
        if (iconCompat == null) {
            return false;
        }
        if (iconCompat.mType != 2 || iconCompat.getResId() != 0) {
            return true;
        }
        throw new IllegalArgumentException("Failed to add icon, invalid resource id: " + iconCompat.getResId());
    }
}
