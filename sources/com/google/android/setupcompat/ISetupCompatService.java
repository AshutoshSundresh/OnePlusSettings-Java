package com.google.android.setupcompat;

import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface ISetupCompatService extends IInterface {
    void logMetric(int i, Bundle bundle, Bundle bundle2) throws RemoteException;

    void validateActivity(String str, Bundle bundle) throws RemoteException;

    public static abstract class Stub extends Binder implements ISetupCompatService {
        public static ISetupCompatService asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.google.android.setupcompat.ISetupCompatService");
            if (queryLocalInterface == null || !(queryLocalInterface instanceof ISetupCompatService)) {
                return new Proxy(iBinder);
            }
            return (ISetupCompatService) queryLocalInterface;
        }

        /* access modifiers changed from: private */
        public static class Proxy implements ISetupCompatService {
            public static ISetupCompatService sDefaultImpl;
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override // com.google.android.setupcompat.ISetupCompatService
            public void validateActivity(String str, Bundle bundle) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.android.setupcompat.ISetupCompatService");
                    obtain.writeString(str);
                    if (bundle != null) {
                        obtain.writeInt(1);
                        bundle.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    if (this.mRemote.transact(1, obtain, null, 1) || Stub.getDefaultImpl() == null) {
                        obtain.recycle();
                    } else {
                        Stub.getDefaultImpl().validateActivity(str, bundle);
                    }
                } finally {
                    obtain.recycle();
                }
            }

            @Override // com.google.android.setupcompat.ISetupCompatService
            public void logMetric(int i, Bundle bundle, Bundle bundle2) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.android.setupcompat.ISetupCompatService");
                    obtain.writeInt(i);
                    if (bundle != null) {
                        obtain.writeInt(1);
                        bundle.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    if (bundle2 != null) {
                        obtain.writeInt(1);
                        bundle2.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    if (this.mRemote.transact(2, obtain, null, 1) || Stub.getDefaultImpl() == null) {
                        obtain.recycle();
                    } else {
                        Stub.getDefaultImpl().logMetric(i, bundle, bundle2);
                    }
                } finally {
                    obtain.recycle();
                }
            }
        }

        public static ISetupCompatService getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}
