package com.android.settingslib.wifi;

import android.net.ScoredNetwork;
import android.os.Parcel;
import android.os.Parcelable;

/* access modifiers changed from: package-private */
public class TimestampedScoredNetwork implements Parcelable {
    public static final Parcelable.Creator<TimestampedScoredNetwork> CREATOR = new Parcelable.Creator<TimestampedScoredNetwork>() {
        /* class com.android.settingslib.wifi.TimestampedScoredNetwork.AnonymousClass1 */

        @Override // android.os.Parcelable.Creator
        public TimestampedScoredNetwork createFromParcel(Parcel parcel) {
            return new TimestampedScoredNetwork(parcel);
        }

        @Override // android.os.Parcelable.Creator
        public TimestampedScoredNetwork[] newArray(int i) {
            return new TimestampedScoredNetwork[i];
        }
    };
    private ScoredNetwork mScore;
    private long mUpdatedTimestampMillis;

    public int describeContents() {
        return 0;
    }

    TimestampedScoredNetwork(ScoredNetwork scoredNetwork, long j) {
        this.mScore = scoredNetwork;
        this.mUpdatedTimestampMillis = j;
    }

    protected TimestampedScoredNetwork(Parcel parcel) {
        this.mScore = parcel.readParcelable(ScoredNetwork.class.getClassLoader());
        this.mUpdatedTimestampMillis = parcel.readLong();
    }

    public void update(ScoredNetwork scoredNetwork, long j) {
        this.mScore = scoredNetwork;
        this.mUpdatedTimestampMillis = j;
    }

    public ScoredNetwork getScore() {
        return this.mScore;
    }

    public long getUpdatedTimestampMillis() {
        return this.mUpdatedTimestampMillis;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(this.mScore, i);
        parcel.writeLong(this.mUpdatedTimestampMillis);
    }
}
