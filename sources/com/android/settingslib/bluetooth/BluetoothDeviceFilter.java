package com.android.settingslib.bluetooth;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothUuid;
import android.os.ParcelUuid;
import android.util.Log;
import com.android.internal.util.ArrayUtils;

public final class BluetoothDeviceFilter {
    public static final Filter ALL_FILTER = new AllFilter();
    private static final Filter[] FILTERS = {ALL_FILTER, new AudioFilter(), new TransferFilter(), new PanuFilter(), new NapFilter()};
    public static final Filter UNBONDED_DEVICE_FILTER = new UnbondedDeviceFilter();

    public interface Filter {
        boolean matches(BluetoothDevice bluetoothDevice);
    }

    public static Filter getFilter(int i) {
        if (i >= 0) {
            Filter[] filterArr = FILTERS;
            if (i < filterArr.length) {
                return filterArr[i];
            }
        }
        Log.w("BluetoothDeviceFilter", "Invalid filter type " + i + " for device picker");
        return ALL_FILTER;
    }

    private static final class AllFilter implements Filter {
        @Override // com.android.settingslib.bluetooth.BluetoothDeviceFilter.Filter
        public boolean matches(BluetoothDevice bluetoothDevice) {
            return true;
        }

        private AllFilter() {
        }
    }

    private static final class UnbondedDeviceFilter implements Filter {
        private UnbondedDeviceFilter() {
        }

        @Override // com.android.settingslib.bluetooth.BluetoothDeviceFilter.Filter
        public boolean matches(BluetoothDevice bluetoothDevice) {
            return bluetoothDevice.getBondState() != 12;
        }
    }

    private static abstract class ClassUuidFilter implements Filter {
        /* access modifiers changed from: package-private */
        public abstract boolean matches(ParcelUuid[] parcelUuidArr, BluetoothClass bluetoothClass);

        private ClassUuidFilter() {
        }

        @Override // com.android.settingslib.bluetooth.BluetoothDeviceFilter.Filter
        public boolean matches(BluetoothDevice bluetoothDevice) {
            return matches(bluetoothDevice.getUuids(), bluetoothDevice.getBluetoothClass());
        }
    }

    private static final class AudioFilter extends ClassUuidFilter {
        private AudioFilter() {
            super();
        }

        /* access modifiers changed from: package-private */
        /* JADX WARNING: Removed duplicated region for block: B:14:0x0025 A[RETURN] */
        @Override // com.android.settingslib.bluetooth.BluetoothDeviceFilter.ClassUuidFilter
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean matches(android.os.ParcelUuid[] r2, android.bluetooth.BluetoothClass r3) {
            /*
                r1 = this;
                r1 = 0
                r0 = 1
                if (r2 == 0) goto L_0x0016
                android.os.ParcelUuid[] r3 = com.android.settingslib.bluetooth.A2dpProfile.SINK_UUIDS
                boolean r3 = android.bluetooth.BluetoothUuid.containsAnyUuid(r2, r3)
                if (r3 == 0) goto L_0x000d
                return r0
            L_0x000d:
                android.os.ParcelUuid[] r3 = com.android.settingslib.bluetooth.HeadsetProfile.UUIDS
                boolean r2 = android.bluetooth.BluetoothUuid.containsAnyUuid(r2, r3)
                if (r2 == 0) goto L_0x0025
                return r0
            L_0x0016:
                if (r3 == 0) goto L_0x0025
                boolean r2 = r3.doesClassMatch(r0)
                if (r2 != 0) goto L_0x0024
                boolean r2 = r3.doesClassMatch(r1)
                if (r2 == 0) goto L_0x0025
            L_0x0024:
                return r0
            L_0x0025:
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.settingslib.bluetooth.BluetoothDeviceFilter.AudioFilter.matches(android.os.ParcelUuid[], android.bluetooth.BluetoothClass):boolean");
        }
    }

    private static final class TransferFilter extends ClassUuidFilter {
        private TransferFilter() {
            super();
        }

        /* access modifiers changed from: package-private */
        @Override // com.android.settingslib.bluetooth.BluetoothDeviceFilter.ClassUuidFilter
        public boolean matches(ParcelUuid[] parcelUuidArr, BluetoothClass bluetoothClass) {
            if (parcelUuidArr != null && ArrayUtils.contains(parcelUuidArr, BluetoothUuid.OBEX_OBJECT_PUSH)) {
                return true;
            }
            if (bluetoothClass == null || !bluetoothClass.doesClassMatch(2)) {
                return false;
            }
            return true;
        }
    }

    private static final class PanuFilter extends ClassUuidFilter {
        private PanuFilter() {
            super();
        }

        /* access modifiers changed from: package-private */
        @Override // com.android.settingslib.bluetooth.BluetoothDeviceFilter.ClassUuidFilter
        public boolean matches(ParcelUuid[] parcelUuidArr, BluetoothClass bluetoothClass) {
            if (parcelUuidArr != null && ArrayUtils.contains(parcelUuidArr, BluetoothUuid.PANU)) {
                return true;
            }
            if (bluetoothClass == null || !bluetoothClass.doesClassMatch(4)) {
                return false;
            }
            return true;
        }
    }

    private static final class NapFilter extends ClassUuidFilter {
        private NapFilter() {
            super();
        }

        /* access modifiers changed from: package-private */
        @Override // com.android.settingslib.bluetooth.BluetoothDeviceFilter.ClassUuidFilter
        public boolean matches(ParcelUuid[] parcelUuidArr, BluetoothClass bluetoothClass) {
            if (parcelUuidArr != null && ArrayUtils.contains(parcelUuidArr, BluetoothUuid.NAP)) {
                return true;
            }
            if (bluetoothClass == null || !bluetoothClass.doesClassMatch(5)) {
                return false;
            }
            return true;
        }
    }
}
