package com.caverock.androidsvg;

public class PreserveAspectRatio {
    public static final PreserveAspectRatio LETTERBOX = new PreserveAspectRatio(Alignment.XMidYMid, Scale.Meet);
    public static final PreserveAspectRatio STRETCH = new PreserveAspectRatio(Alignment.None, null);
    public static final PreserveAspectRatio UNSCALED = new PreserveAspectRatio(null, null);
    private Alignment alignment;
    private Scale scale;

    public enum Alignment {
        None,
        XMinYMin,
        XMidYMin,
        XMaxYMin,
        XMinYMid,
        XMidYMid,
        XMaxYMid,
        XMinYMax,
        XMidYMax,
        XMaxYMax
    }

    public enum Scale {
        Meet,
        Slice
    }

    static {
        Alignment alignment2 = Alignment.XMinYMin;
        Scale scale2 = Scale.Meet;
        Alignment alignment3 = Alignment.XMaxYMax;
        Scale scale3 = Scale.Meet;
        Alignment alignment4 = Alignment.XMidYMin;
        Scale scale4 = Scale.Meet;
        Alignment alignment5 = Alignment.XMidYMax;
        Scale scale5 = Scale.Meet;
        Alignment alignment6 = Alignment.XMidYMid;
        Scale scale6 = Scale.Slice;
        Alignment alignment7 = Alignment.XMinYMin;
        Scale scale7 = Scale.Slice;
    }

    public PreserveAspectRatio(Alignment alignment2, Scale scale2) {
        this.alignment = alignment2;
        this.scale = scale2;
    }

    public Alignment getAlignment() {
        return this.alignment;
    }

    public Scale getScale() {
        return this.scale;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || PreserveAspectRatio.class != obj.getClass()) {
            return false;
        }
        PreserveAspectRatio preserveAspectRatio = (PreserveAspectRatio) obj;
        return this.alignment == preserveAspectRatio.alignment && this.scale == preserveAspectRatio.scale;
    }
}
