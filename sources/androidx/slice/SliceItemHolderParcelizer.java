package androidx.slice;

import android.os.Parcelable;
import androidx.slice.SliceItemHolder;
import androidx.versionedparcelable.VersionedParcel;
import androidx.versionedparcelable.VersionedParcelable;

public final class SliceItemHolderParcelizer {
    private static SliceItemHolder.SliceItemPool sBuilder = new SliceItemHolder.SliceItemPool();

    public static SliceItemHolder read(VersionedParcel versionedParcel) {
        SliceItemHolder sliceItemHolder = sBuilder.get();
        sliceItemHolder.mVersionedParcelable = versionedParcel.readVersionedParcelable(sliceItemHolder.mVersionedParcelable, 1);
        sliceItemHolder.mParcelable = versionedParcel.readParcelable(sliceItemHolder.mParcelable, 2);
        sliceItemHolder.mStr = versionedParcel.readString(sliceItemHolder.mStr, 3);
        sliceItemHolder.mInt = versionedParcel.readInt(sliceItemHolder.mInt, 4);
        sliceItemHolder.mLong = versionedParcel.readLong(sliceItemHolder.mLong, 5);
        return sliceItemHolder;
    }

    public static void write(SliceItemHolder sliceItemHolder, VersionedParcel versionedParcel) {
        versionedParcel.setSerializationFlags(true, true);
        VersionedParcelable versionedParcelable = sliceItemHolder.mVersionedParcelable;
        if (versionedParcelable != null) {
            versionedParcel.writeVersionedParcelable(versionedParcelable, 1);
        }
        Parcelable parcelable = sliceItemHolder.mParcelable;
        if (parcelable != null) {
            versionedParcel.writeParcelable(parcelable, 2);
        }
        String str = sliceItemHolder.mStr;
        if (str != null) {
            versionedParcel.writeString(str, 3);
        }
        int i = sliceItemHolder.mInt;
        if (i != 0) {
            versionedParcel.writeInt(i, 4);
        }
        long j = sliceItemHolder.mLong;
        if (0 != j) {
            versionedParcel.writeLong(j, 5);
        }
    }
}
