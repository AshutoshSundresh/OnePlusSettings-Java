package com.qualcomm.qti.remoteSimlock;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface IUimRemoteSimlockService extends IInterface {
    int deregisterCallback(IUimRemoteSimlockServiceCallback iUimRemoteSimlockServiceCallback) throws RemoteException;

    int registerCallback(IUimRemoteSimlockServiceCallback iUimRemoteSimlockServiceCallback) throws RemoteException;

    int uimRemoteSimlockGetSimlockStatus(int i) throws RemoteException;

    public static abstract class Stub extends Binder implements IUimRemoteSimlockService {
        public static IUimRemoteSimlockService asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.qualcomm.qti.remoteSimlock.IUimRemoteSimlockService");
            if (queryLocalInterface == null || !(queryLocalInterface instanceof IUimRemoteSimlockService)) {
                return new Proxy(iBinder);
            }
            return (IUimRemoteSimlockService) queryLocalInterface;
        }

        /* access modifiers changed from: private */
        public static class Proxy implements IUimRemoteSimlockService {
            public static IUimRemoteSimlockService sDefaultImpl;
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override // com.qualcomm.qti.remoteSimlock.IUimRemoteSimlockService
            public int registerCallback(IUimRemoteSimlockServiceCallback iUimRemoteSimlockServiceCallback) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.qualcomm.qti.remoteSimlock.IUimRemoteSimlockService");
                    obtain.writeStrongBinder(iUimRemoteSimlockServiceCallback != null ? iUimRemoteSimlockServiceCallback.asBinder() : null);
                    if (!this.mRemote.transact(1, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().registerCallback(iUimRemoteSimlockServiceCallback);
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

            @Override // com.qualcomm.qti.remoteSimlock.IUimRemoteSimlockService
            public int deregisterCallback(IUimRemoteSimlockServiceCallback iUimRemoteSimlockServiceCallback) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.qualcomm.qti.remoteSimlock.IUimRemoteSimlockService");
                    obtain.writeStrongBinder(iUimRemoteSimlockServiceCallback != null ? iUimRemoteSimlockServiceCallback.asBinder() : null);
                    if (!this.mRemote.transact(2, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().deregisterCallback(iUimRemoteSimlockServiceCallback);
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

            @Override // com.qualcomm.qti.remoteSimlock.IUimRemoteSimlockService
            public int uimRemoteSimlockGetSimlockStatus(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.qualcomm.qti.remoteSimlock.IUimRemoteSimlockService");
                    obtain.writeInt(i);
                    if (!this.mRemote.transact(7, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().uimRemoteSimlockGetSimlockStatus(i);
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
        }

        public static IUimRemoteSimlockService getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}
