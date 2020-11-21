package androidx.slice;

import android.net.Uri;

public class SliceStructure {
    private final String mStructure;
    private final Uri mUri;

    public SliceStructure(Slice slice) {
        StringBuilder sb = new StringBuilder();
        getStructure(slice, sb);
        this.mStructure = sb.toString();
        this.mUri = slice.getUri();
    }

    public SliceStructure(SliceItem sliceItem) {
        StringBuilder sb = new StringBuilder();
        getStructure(sliceItem, sb);
        this.mStructure = sb.toString();
        if ("action".equals(sliceItem.getFormat()) || "slice".equals(sliceItem.getFormat())) {
            this.mUri = sliceItem.getSlice().getUri();
        } else {
            this.mUri = null;
        }
    }

    public Uri getUri() {
        return this.mUri;
    }

    public int hashCode() {
        return this.mStructure.hashCode();
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof SliceStructure)) {
            return false;
        }
        return this.mStructure.equals(((SliceStructure) obj).mStructure);
    }

    private static void getStructure(Slice slice, StringBuilder sb) {
        sb.append("s{");
        for (SliceItem sliceItem : slice.getItems()) {
            getStructure(sliceItem, sb);
        }
        sb.append("}");
    }

    /* JADX INFO: Can't fix incorrect switch cases order, some code will duplicate */
    private static void getStructure(SliceItem sliceItem, StringBuilder sb) {
        char c;
        String format = sliceItem.getFormat();
        switch (format.hashCode()) {
            case -1422950858:
                if (format.equals("action")) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case -1377881982:
                if (format.equals("bundle")) {
                    c = 7;
                    break;
                }
                c = 65535;
                break;
            case 104431:
                if (format.equals("int")) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            case 3327612:
                if (format.equals("long")) {
                    c = 5;
                    break;
                }
                c = 65535;
                break;
            case 3556653:
                if (format.equals("text")) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case 100313435:
                if (format.equals("image")) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case 100358090:
                if (format.equals("input")) {
                    c = 6;
                    break;
                }
                c = 65535;
                break;
            case 109526418:
                if (format.equals("slice")) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        if (c == 0) {
            getStructure(sliceItem.getSlice(), sb);
        } else if (c == 1) {
            sb.append('a');
            if ("range".equals(sliceItem.getSubType())) {
                sb.append('r');
            }
            getStructure(sliceItem.getSlice(), sb);
        } else if (c == 2) {
            sb.append('t');
        } else if (c == 3) {
            sb.append('i');
        }
    }
}
