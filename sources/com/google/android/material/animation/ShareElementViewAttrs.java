package com.google.android.material.animation;

import android.os.Parcel;
import android.os.Parcelable;

public class ShareElementViewAttrs implements Parcelable {
    public static final Parcelable.Creator<ShareElementViewAttrs> CREATOR = new Parcelable.Creator<ShareElementViewAttrs>() {
        /* class com.google.android.material.animation.ShareElementViewAttrs.AnonymousClass1 */

        @Override // android.os.Parcelable.Creator
        public ShareElementViewAttrs createFromParcel(Parcel parcel) {
            return new ShareElementViewAttrs(parcel);
        }

        @Override // android.os.Parcelable.Creator
        public ShareElementViewAttrs[] newArray(int i) {
            return new ShareElementViewAttrs[i];
        }
    };
    public float alpha;
    public float height;
    public int id;
    public float startX;
    public float startY;
    public float width;

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.id);
        parcel.writeFloat(this.startX);
        parcel.writeFloat(this.startY);
        parcel.writeFloat(this.width);
        parcel.writeFloat(this.height);
        parcel.writeFloat(this.alpha);
    }

    protected ShareElementViewAttrs(Parcel parcel) {
        this.id = parcel.readInt();
        this.startX = parcel.readFloat();
        this.startY = parcel.readFloat();
        this.width = parcel.readFloat();
        this.height = parcel.readFloat();
        this.alpha = parcel.readFloat();
    }
}
