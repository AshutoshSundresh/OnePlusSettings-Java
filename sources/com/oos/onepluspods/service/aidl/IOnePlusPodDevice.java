package com.oos.onepluspods.service.aidl;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface IOnePlusPodDevice extends IInterface {
    int getBattaryInfo(String str) throws RemoteException;

    int getKeyFunction(String str) throws RemoteException;

    String getVersion(String str) throws RemoteException;

    boolean isOnePlusPods(String str) throws RemoteException;

    void setIOnePlusUpdate(IOnePlusUpdate iOnePlusUpdate) throws RemoteException;

    void setKeyFunction(int i, int i2, String str) throws RemoteException;

    public static abstract class Stub extends Binder implements IOnePlusPodDevice {
        public static IOnePlusPodDevice asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.oos.onepluspods.service.aidl.IOnePlusPodDevice");
            if (queryLocalInterface == null || !(queryLocalInterface instanceof IOnePlusPodDevice)) {
                return new Proxy(iBinder);
            }
            return (IOnePlusPodDevice) queryLocalInterface;
        }

        /* access modifiers changed from: private */
        public static class Proxy implements IOnePlusPodDevice {
            public static IOnePlusPodDevice sDefaultImpl;
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override // com.oos.onepluspods.service.aidl.IOnePlusPodDevice
            public boolean isOnePlusPods(String str) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.oos.onepluspods.service.aidl.IOnePlusPodDevice");
                    obtain.writeString(str);
                    boolean z = false;
                    if (!this.mRemote.transact(1, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().isOnePlusPods(str);
                    }
                    obtain2.readException();
                    if (obtain2.readInt() != 0) {
                        z = true;
                    }
                    obtain2.recycle();
                    obtain.recycle();
                    return z;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.oos.onepluspods.service.aidl.IOnePlusPodDevice
            public void setKeyFunction(int i, int i2, String str) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.oos.onepluspods.service.aidl.IOnePlusPodDevice");
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    obtain.writeString(str);
                    if (this.mRemote.transact(2, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().setKeyFunction(i, i2, str);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.oos.onepluspods.service.aidl.IOnePlusPodDevice
            public int getKeyFunction(String str) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.oos.onepluspods.service.aidl.IOnePlusPodDevice");
                    obtain.writeString(str);
                    if (!this.mRemote.transact(3, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getKeyFunction(str);
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

            @Override // com.oos.onepluspods.service.aidl.IOnePlusPodDevice
            public String getVersion(String str) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.oos.onepluspods.service.aidl.IOnePlusPodDevice");
                    obtain.writeString(str);
                    if (!this.mRemote.transact(4, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getVersion(str);
                    }
                    obtain2.readException();
                    String readString = obtain2.readString();
                    obtain2.recycle();
                    obtain.recycle();
                    return readString;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.oos.onepluspods.service.aidl.IOnePlusPodDevice
            public void setIOnePlusUpdate(IOnePlusUpdate iOnePlusUpdate) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.oos.onepluspods.service.aidl.IOnePlusPodDevice");
                    obtain.writeStrongBinder(iOnePlusUpdate != null ? iOnePlusUpdate.asBinder() : null);
                    if (this.mRemote.transact(5, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().setIOnePlusUpdate(iOnePlusUpdate);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.oos.onepluspods.service.aidl.IOnePlusPodDevice
            public int getBattaryInfo(String str) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.oos.onepluspods.service.aidl.IOnePlusPodDevice");
                    obtain.writeString(str);
                    if (!this.mRemote.transact(6, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getBattaryInfo(str);
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

        public static IOnePlusPodDevice getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}
