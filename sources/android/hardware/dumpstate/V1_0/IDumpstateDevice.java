package android.hardware.dumpstate.V1_0;

import android.os.HidlSupport;
import android.os.HwBinder;
import android.os.HwParcel;
import android.os.IHwBinder;
import android.os.IHwInterface;
import android.os.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

public interface IDumpstateDevice extends IHwInterface {
    ArrayList<String> interfaceChain() throws RemoteException;

    static default IDumpstateDevice asInterface(IHwBinder iHwBinder) {
        if (iHwBinder == null) {
            return null;
        }
        IDumpstateDevice queryLocalInterface = iHwBinder.queryLocalInterface("android.hardware.dumpstate@1.0::IDumpstateDevice");
        if (queryLocalInterface != null && (queryLocalInterface instanceof IDumpstateDevice)) {
            return queryLocalInterface;
        }
        Proxy proxy = new Proxy(iHwBinder);
        try {
            Iterator<String> it = proxy.interfaceChain().iterator();
            while (it.hasNext()) {
                if (it.next().equals("android.hardware.dumpstate@1.0::IDumpstateDevice")) {
                    return proxy;
                }
            }
        } catch (RemoteException unused) {
        }
        return null;
    }

    static default IDumpstateDevice getService(String str, boolean z) throws RemoteException {
        return asInterface(HwBinder.getService("android.hardware.dumpstate@1.0::IDumpstateDevice", str, z));
    }

    static default IDumpstateDevice getService(boolean z) throws RemoteException {
        return getService("default", z);
    }

    public static final class Proxy implements IDumpstateDevice {
        private IHwBinder mRemote;

        public Proxy(IHwBinder iHwBinder) {
            Objects.requireNonNull(iHwBinder);
            this.mRemote = iHwBinder;
        }

        public IHwBinder asBinder() {
            return this.mRemote;
        }

        public String toString() {
            try {
                return interfaceDescriptor() + "@Proxy";
            } catch (RemoteException unused) {
                return "[class or subclass of android.hardware.dumpstate@1.0::IDumpstateDevice]@Proxy";
            }
        }

        public final boolean equals(Object obj) {
            return HidlSupport.interfacesEqual(this, obj);
        }

        public final int hashCode() {
            return asBinder().hashCode();
        }

        @Override // android.hardware.dumpstate.V1_0.IDumpstateDevice
        public ArrayList<String> interfaceChain() throws RemoteException {
            HwParcel hwParcel = new HwParcel();
            hwParcel.writeInterfaceToken("android.hidl.base@1.0::IBase");
            HwParcel hwParcel2 = new HwParcel();
            try {
                this.mRemote.transact(256067662, hwParcel, hwParcel2, 0);
                hwParcel2.verifySuccess();
                hwParcel.releaseTemporaryStorage();
                return hwParcel2.readStringVector();
            } finally {
                hwParcel2.release();
            }
        }

        public String interfaceDescriptor() throws RemoteException {
            HwParcel hwParcel = new HwParcel();
            hwParcel.writeInterfaceToken("android.hidl.base@1.0::IBase");
            HwParcel hwParcel2 = new HwParcel();
            try {
                this.mRemote.transact(256136003, hwParcel, hwParcel2, 0);
                hwParcel2.verifySuccess();
                hwParcel.releaseTemporaryStorage();
                return hwParcel2.readString();
            } finally {
                hwParcel2.release();
            }
        }
    }
}
