package com.oneplus.sprint.callingplus.interfaces;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface IWifiCallingService extends IInterface {
    boolean isWifiCallingSwitchChecked() throws RemoteException;

    boolean isWifiCallingSwitchEnable() throws RemoteException;

    boolean isWifiCallingSwitchNormal() throws RemoteException;

    void setWifiCallingSwitchState(boolean z) throws RemoteException;

    public static abstract class Stub extends Binder implements IWifiCallingService {
        public static IWifiCallingService asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.oneplus.sprint.callingplus.interfaces.IWifiCallingService");
            if (queryLocalInterface == null || !(queryLocalInterface instanceof IWifiCallingService)) {
                return new Proxy(iBinder);
            }
            return (IWifiCallingService) queryLocalInterface;
        }

        /* access modifiers changed from: private */
        public static class Proxy implements IWifiCallingService {
            public static IWifiCallingService sDefaultImpl;
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override // com.oneplus.sprint.callingplus.interfaces.IWifiCallingService
            public boolean isWifiCallingSwitchNormal() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.oneplus.sprint.callingplus.interfaces.IWifiCallingService");
                    boolean z = false;
                    if (!this.mRemote.transact(1, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().isWifiCallingSwitchNormal();
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

            @Override // com.oneplus.sprint.callingplus.interfaces.IWifiCallingService
            public boolean isWifiCallingSwitchChecked() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.oneplus.sprint.callingplus.interfaces.IWifiCallingService");
                    boolean z = false;
                    if (!this.mRemote.transact(2, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().isWifiCallingSwitchChecked();
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

            @Override // com.oneplus.sprint.callingplus.interfaces.IWifiCallingService
            public boolean isWifiCallingSwitchEnable() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.oneplus.sprint.callingplus.interfaces.IWifiCallingService");
                    boolean z = false;
                    if (!this.mRemote.transact(3, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().isWifiCallingSwitchEnable();
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

            @Override // com.oneplus.sprint.callingplus.interfaces.IWifiCallingService
            public void setWifiCallingSwitchState(boolean z) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.oneplus.sprint.callingplus.interfaces.IWifiCallingService");
                    obtain.writeInt(z ? 1 : 0);
                    if (this.mRemote.transact(4, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().setWifiCallingSwitchState(z);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }

        public static IWifiCallingService getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}
