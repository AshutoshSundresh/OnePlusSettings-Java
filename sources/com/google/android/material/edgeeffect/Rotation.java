package com.google.android.material.edgeeffect;

public enum Rotation {
    LANDSCAPE(270),
    INVERSE_LANDSCAPE(90),
    PORTRAIT(0),
    INVERSE_PORTRAIT(180);
    
    private final int m_DeviceOrientation;

    private Rotation(int i) {
        this.m_DeviceOrientation = i;
    }

    public static Rotation fromScreenOrientation(int i) {
        Rotation rotation = PORTRAIT;
        if (i == 0) {
            return LANDSCAPE;
        }
        if (i == 1) {
            return rotation;
        }
        if (i == 8) {
            return INVERSE_LANDSCAPE;
        }
        if (i != 9) {
            return rotation;
        }
        return INVERSE_PORTRAIT;
    }

    public int getDeviceOrientation() {
        return this.m_DeviceOrientation;
    }

    /* renamed from: com.google.android.material.edgeeffect.Rotation$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$google$android$material$edgeeffect$Rotation;

        /* JADX WARNING: Can't wrap try/catch for region: R(8:0|1|2|3|4|5|6|(3:7|8|10)) */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0012 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x001d */
        /* JADX WARNING: Missing exception handler attribute for start block: B:7:0x0028 */
        static {
            /*
                com.google.android.material.edgeeffect.Rotation[] r0 = com.google.android.material.edgeeffect.Rotation.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                com.google.android.material.edgeeffect.Rotation.AnonymousClass1.$SwitchMap$com$google$android$material$edgeeffect$Rotation = r0
                com.google.android.material.edgeeffect.Rotation r1 = com.google.android.material.edgeeffect.Rotation.INVERSE_PORTRAIT     // Catch:{ NoSuchFieldError -> 0x0012 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0012 }
                r2 = 1
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0012 }
            L_0x0012:
                int[] r0 = com.google.android.material.edgeeffect.Rotation.AnonymousClass1.$SwitchMap$com$google$android$material$edgeeffect$Rotation     // Catch:{ NoSuchFieldError -> 0x001d }
                com.google.android.material.edgeeffect.Rotation r1 = com.google.android.material.edgeeffect.Rotation.INVERSE_LANDSCAPE     // Catch:{ NoSuchFieldError -> 0x001d }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001d }
                r2 = 2
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001d }
            L_0x001d:
                int[] r0 = com.google.android.material.edgeeffect.Rotation.AnonymousClass1.$SwitchMap$com$google$android$material$edgeeffect$Rotation     // Catch:{ NoSuchFieldError -> 0x0028 }
                com.google.android.material.edgeeffect.Rotation r1 = com.google.android.material.edgeeffect.Rotation.LANDSCAPE     // Catch:{ NoSuchFieldError -> 0x0028 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0028 }
                r2 = 3
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0028 }
            L_0x0028:
                int[] r0 = com.google.android.material.edgeeffect.Rotation.AnonymousClass1.$SwitchMap$com$google$android$material$edgeeffect$Rotation     // Catch:{ NoSuchFieldError -> 0x0033 }
                com.google.android.material.edgeeffect.Rotation r1 = com.google.android.material.edgeeffect.Rotation.PORTRAIT     // Catch:{ NoSuchFieldError -> 0x0033 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0033 }
                r2 = 4
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0033 }
            L_0x0033:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.android.material.edgeeffect.Rotation.AnonymousClass1.<clinit>():void");
        }
    }

    public boolean isLandscape() {
        int i = AnonymousClass1.$SwitchMap$com$google$android$material$edgeeffect$Rotation[ordinal()];
        return i == 2 || i == 3;
    }
}
