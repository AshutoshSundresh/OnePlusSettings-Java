package com.oneplus.faceunlock.internal;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface IOPFaceSettingService extends IInterface {
    int checkState(int i) throws RemoteException;

    void removeFace(int i) throws RemoteException;

    public static abstract class Stub extends Binder implements IOPFaceSettingService {
        public static IOPFaceSettingService asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.oneplus.faceunlock.internal.IOPFaceSettingService");
            if (queryLocalInterface == null || !(queryLocalInterface instanceof IOPFaceSettingService)) {
                return new Proxy(iBinder);
            }
            return (IOPFaceSettingService) queryLocalInterface;
        }

        /* access modifiers changed from: private */
        public static class Proxy implements IOPFaceSettingService {
            public static IOPFaceSettingService sDefaultImpl;
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override // com.oneplus.faceunlock.internal.IOPFaceSettingService
            public int checkState(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.oneplus.faceunlock.internal.IOPFaceSettingService");
                    obtain.writeInt(i);
                    if (!this.mRemote.transact(1, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().checkState(i);
                    }
                    obtain2.readException();
                    int readInt = obtain2.readInt();
                    obtain2.recycle();
                    obtain.recycle();
                    return readInt;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.oneplus.faceunlock.internal.IOPFaceSettingService
            public void removeFace(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.oneplus.faceunlock.internal.IOPFaceSettingService");
                    obtain.writeInt(i);
                    if (this.mRemote.transact(2, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().removeFace(i);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }

        public static IOPFaceSettingService getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}
