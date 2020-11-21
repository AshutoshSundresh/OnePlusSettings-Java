package com.oos.onepluspods.service.aidl;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface IOnePlusUpdate extends IInterface {
    void updateView(String str, int i) throws RemoteException;

    public static abstract class Stub extends Binder implements IOnePlusUpdate {
        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, "com.oos.onepluspods.service.aidl.IOnePlusUpdate");
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i == 1) {
                parcel.enforceInterface("com.oos.onepluspods.service.aidl.IOnePlusUpdate");
                updateView(parcel.readString(), parcel.readInt());
                parcel2.writeNoException();
                return true;
            } else if (i != 1598968902) {
                return super.onTransact(i, parcel, parcel2, i2);
            } else {
                parcel2.writeString("com.oos.onepluspods.service.aidl.IOnePlusUpdate");
                return true;
            }
        }
    }
}
