package com.oneplus.accountsdk.auth;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import com.oneplus.accountsdk.a;

public class OPAuthResponse implements Parcelable {
    public static final Parcelable.Creator<OPAuthResponse> CREATOR = new Parcelable.Creator<OPAuthResponse>() {
        /* class com.oneplus.accountsdk.auth.OPAuthResponse.AnonymousClass1 */

        /* Return type fixed from 'java.lang.Object' to match base method */
        @Override // android.os.Parcelable.Creator
        public final /* synthetic */ OPAuthResponse createFromParcel(Parcel parcel) {
            return new OPAuthResponse(parcel);
        }

        /* Return type fixed from 'java.lang.Object[]' to match base method */
        @Override // android.os.Parcelable.Creator
        public final /* bridge */ /* synthetic */ OPAuthResponse[] newArray(int i) {
            return new OPAuthResponse[i];
        }
    };
    a a;

    protected OPAuthResponse(Parcel parcel) {
        this.a = a.AbstractBinderC0000a.a(parcel.readStrongBinder());
    }

    static void a(a aVar) {
        if (aVar != null) {
            try {
                aVar.a();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private static void a(a aVar, Bundle bundle) {
        if (aVar != null && bundle != null) {
            try {
                aVar.a(bundle);
            } catch (RemoteException unused) {
            }
        }
    }

    public final void a(Bundle bundle) {
        a(this.a, bundle);
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStrongBinder(this.a.asBinder());
    }
}
