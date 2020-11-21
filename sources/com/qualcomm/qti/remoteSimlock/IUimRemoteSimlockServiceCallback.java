package com.qualcomm.qti.remoteSimlock;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface IUimRemoteSimlockServiceCallback extends IInterface {
    void uimRemoteSimlockGenerateHMACResponse(int i, int i2, byte[] bArr) throws RemoteException;

    void uimRemoteSimlockGetSharedKeyResponse(int i, int i2, byte[] bArr) throws RemoteException;

    void uimRemoteSimlockGetSimlockStatusResponse(int i, int i2, int i3, long j) throws RemoteException;

    void uimRemoteSimlockGetVersionResponse(int i, int i2, int i3, int i4) throws RemoteException;

    void uimRemoteSimlockProcessSimlockDataResponse(int i, int i2, byte[] bArr) throws RemoteException;

    public static abstract class Stub extends Binder implements IUimRemoteSimlockServiceCallback {
        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, "com.qualcomm.qti.remoteSimlock.IUimRemoteSimlockServiceCallback");
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i == 1) {
                parcel.enforceInterface("com.qualcomm.qti.remoteSimlock.IUimRemoteSimlockServiceCallback");
                uimRemoteSimlockProcessSimlockDataResponse(parcel.readInt(), parcel.readInt(), parcel.createByteArray());
                parcel2.writeNoException();
                return true;
            } else if (i == 2) {
                parcel.enforceInterface("com.qualcomm.qti.remoteSimlock.IUimRemoteSimlockServiceCallback");
                uimRemoteSimlockGetSharedKeyResponse(parcel.readInt(), parcel.readInt(), parcel.createByteArray());
                parcel2.writeNoException();
                return true;
            } else if (i == 3) {
                parcel.enforceInterface("com.qualcomm.qti.remoteSimlock.IUimRemoteSimlockServiceCallback");
                uimRemoteSimlockGenerateHMACResponse(parcel.readInt(), parcel.readInt(), parcel.createByteArray());
                parcel2.writeNoException();
                return true;
            } else if (i == 4) {
                parcel.enforceInterface("com.qualcomm.qti.remoteSimlock.IUimRemoteSimlockServiceCallback");
                uimRemoteSimlockGetVersionResponse(parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readInt());
                parcel2.writeNoException();
                return true;
            } else if (i == 5) {
                parcel.enforceInterface("com.qualcomm.qti.remoteSimlock.IUimRemoteSimlockServiceCallback");
                uimRemoteSimlockGetSimlockStatusResponse(parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readLong());
                parcel2.writeNoException();
                return true;
            } else if (i != 1598968902) {
                return super.onTransact(i, parcel, parcel2, i2);
            } else {
                parcel2.writeString("com.qualcomm.qti.remoteSimlock.IUimRemoteSimlockServiceCallback");
                return true;
            }
        }
    }
}
