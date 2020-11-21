package androidx.media;

import androidx.versionedparcelable.VersionedParcelable;

public interface AudioAttributesImpl extends VersionedParcelable {

    public interface Builder {
        AudioAttributesImpl build();

        Builder setLegacyStreamType(int i);
    }
}
