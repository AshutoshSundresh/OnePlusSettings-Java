package com.oneplus.accountsdk;

import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface a extends IInterface {

    /* renamed from: com.oneplus.accountsdk.a$a  reason: collision with other inner class name */
    public static abstract class AbstractBinderC0000a extends Binder implements a {

        /* renamed from: com.oneplus.accountsdk.a$a$a  reason: collision with other inner class name */
        static class C0001a implements a {
            private IBinder a;

            C0001a(IBinder iBinder) {
                this.a = iBinder;
            }

            @Override // com.oneplus.accountsdk.a
            public final void a() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.oneplus.accountsdk.IOPAuthResponse");
                    this.a.transact(2, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.oneplus.accountsdk.a
            public final void a(Bundle bundle) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.oneplus.accountsdk.IOPAuthResponse");
                    if (bundle != null) {
                        obtain.writeInt(1);
                        bundle.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    this.a.transact(1, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public final IBinder asBinder() {
                return this.a;
            }
        }

        public static a a(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.oneplus.accountsdk.IOPAuthResponse");
            return (queryLocalInterface == null || !(queryLocalInterface instanceof a)) ? new C0001a(iBinder) : (a) queryLocalInterface;
        }
    }

    void a() throws RemoteException;

    void a(Bundle bundle) throws RemoteException;
}
