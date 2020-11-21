package androidx.slice;

import androidx.versionedparcelable.VersionedParcel;

public final class SliceSpecParcelizer {
    public static SliceSpec read(VersionedParcel versionedParcel) {
        SliceSpec sliceSpec = new SliceSpec();
        sliceSpec.mType = versionedParcel.readString(sliceSpec.mType, 1);
        sliceSpec.mRevision = versionedParcel.readInt(sliceSpec.mRevision, 2);
        return sliceSpec;
    }

    public static void write(SliceSpec sliceSpec, VersionedParcel versionedParcel) {
        versionedParcel.setSerializationFlags(true, false);
        versionedParcel.writeString(sliceSpec.mType, 1);
        int i = sliceSpec.mRevision;
        if (1 != i) {
            versionedParcel.writeInt(i, 2);
        }
    }
}
