package com.google.android.gms.analytics.internal;

import android.os.Parcel;
import android.os.Parcelable;

public class Command implements Parcelable {
    public static final Parcelable.Creator<Command> CREATOR = new Parcelable.Creator<Command>() {
        /* class com.google.android.gms.analytics.internal.Command.AnonymousClass1 */

        @Override // android.os.Parcelable.Creator
        public Command createFromParcel(Parcel parcel) {
            return new Command(parcel);
        }

        @Override // android.os.Parcelable.Creator
        public Command[] newArray(int i) {
            return new Command[i];
        }
    };
    private String id;
    private String urlParam;
    private String value;

    public int describeContents() {
        return 0;
    }

    public Command(String str, String str2, String str3) {
        this.id = str;
        this.urlParam = str2;
        this.value = str3;
    }

    public String getId() {
        return this.id;
    }

    public String getValue() {
        return this.value;
    }

    public Command() {
    }

    Command(Parcel parcel) {
        readFromParcel(parcel);
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.id);
        parcel.writeString(this.urlParam);
        parcel.writeString(this.value);
    }

    private void readFromParcel(Parcel parcel) {
        this.id = parcel.readString();
        this.urlParam = parcel.readString();
        this.value = parcel.readString();
    }
}
