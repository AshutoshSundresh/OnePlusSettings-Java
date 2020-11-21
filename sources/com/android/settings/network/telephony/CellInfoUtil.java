package com.android.settings.network.telephony;

import android.telephony.CellIdentity;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellIdentityNr;
import android.telephony.CellIdentityTdscdma;
import android.telephony.CellIdentityWcdma;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoNr;
import android.telephony.CellInfoTdscdma;
import android.telephony.CellInfoWcdma;
import android.text.BidiFormatter;
import android.text.TextDirectionHeuristics;
import android.text.TextUtils;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class CellInfoUtil {
    public static String getNetworkTitle(CellIdentity cellIdentity, String str) {
        if (cellIdentity != null) {
            String objects = Objects.toString(cellIdentity.getOperatorAlphaLong(), "");
            if (TextUtils.isEmpty(objects)) {
                objects = Objects.toString(cellIdentity.getOperatorAlphaShort(), "");
            }
            if (!TextUtils.isEmpty(objects)) {
                return objects;
            }
        }
        if (TextUtils.isEmpty(str)) {
            return "";
        }
        return BidiFormatter.getInstance().unicodeWrap(str, TextDirectionHeuristics.LTR);
    }

    public static CellIdentity getCellIdentity(CellInfo cellInfo) {
        if (cellInfo == null) {
            return null;
        }
        if (cellInfo instanceof CellInfoGsm) {
            return ((CellInfoGsm) cellInfo).getCellIdentity();
        }
        if (cellInfo instanceof CellInfoCdma) {
            return ((CellInfoCdma) cellInfo).getCellIdentity();
        }
        if (cellInfo instanceof CellInfoWcdma) {
            return ((CellInfoWcdma) cellInfo).getCellIdentity();
        }
        if (cellInfo instanceof CellInfoTdscdma) {
            return ((CellInfoTdscdma) cellInfo).getCellIdentity();
        }
        if (cellInfo instanceof CellInfoLte) {
            return ((CellInfoLte) cellInfo).getCellIdentity();
        }
        if (cellInfo instanceof CellInfoNr) {
            return ((CellInfoNr) cellInfo).getCellIdentity();
        }
        return null;
    }

    /* JADX DEBUG: Multi-variable search result rejected for r0v7, resolved type: android.telephony.CellInfoWcdma */
    /* JADX DEBUG: Multi-variable search result rejected for r0v9, resolved type: android.telephony.CellInfoLte */
    /* JADX DEBUG: Multi-variable search result rejected for r0v10, resolved type: android.telephony.CellInfoNr */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:11:0x004e  */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x0174  */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x01c3  */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x01d8  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static android.telephony.CellInfo convertLegacyIncrScanOperatorInfoToCellInfo(com.android.internal.telephony.OperatorInfo r20) {
        /*
        // Method dump skipped, instructions count: 476
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.network.telephony.CellInfoUtil.convertLegacyIncrScanOperatorInfoToCellInfo(com.android.internal.telephony.OperatorInfo):android.telephony.CellInfo");
    }

    public static String cellInfoListToString(List<CellInfo> list) {
        return (String) list.stream().map($$Lambda$CellInfoUtil$Qzf0JKtnhKRUHWJfKmx3LHFmuG0.INSTANCE).collect(Collectors.joining(", "));
    }

    public static String cellInfoToString(CellInfo cellInfo) {
        String str;
        String mccString;
        String mncString;
        String simpleName = cellInfo.getClass().getSimpleName();
        CellIdentity cellIdentity = getCellIdentity(cellInfo);
        String str2 = null;
        if (cellIdentity != null) {
            if (cellIdentity instanceof CellIdentityGsm) {
                CellIdentityGsm cellIdentityGsm = (CellIdentityGsm) cellIdentity;
                mccString = cellIdentityGsm.getMccString();
                mncString = cellIdentityGsm.getMncString();
            } else if (cellIdentity instanceof CellIdentityWcdma) {
                CellIdentityWcdma cellIdentityWcdma = (CellIdentityWcdma) cellIdentity;
                mccString = cellIdentityWcdma.getMccString();
                mncString = cellIdentityWcdma.getMncString();
            } else if (cellIdentity instanceof CellIdentityTdscdma) {
                CellIdentityTdscdma cellIdentityTdscdma = (CellIdentityTdscdma) cellIdentity;
                mccString = cellIdentityTdscdma.getMccString();
                mncString = cellIdentityTdscdma.getMncString();
            } else if (cellIdentity instanceof CellIdentityLte) {
                CellIdentityLte cellIdentityLte = (CellIdentityLte) cellIdentity;
                mccString = cellIdentityLte.getMccString();
                mncString = cellIdentityLte.getMncString();
            } else if (cellIdentity instanceof CellIdentityNr) {
                CellIdentityNr cellIdentityNr = (CellIdentityNr) cellIdentity;
                mccString = cellIdentityNr.getMccString();
                mncString = cellIdentityNr.getMncString();
            }
            str = mncString;
            str2 = mccString;
            return String.format("{CellType = %s, isRegistered = %b, mcc = %s, mnc = %s, alphaL = %s, alphaS = %s}", simpleName, Boolean.valueOf(cellInfo.isRegistered()), str2, str, cellIdentity.getOperatorAlphaLong(), cellIdentity.getOperatorAlphaShort());
        }
        str = null;
        return String.format("{CellType = %s, isRegistered = %b, mcc = %s, mnc = %s, alphaL = %s, alphaS = %s}", simpleName, Boolean.valueOf(cellInfo.isRegistered()), str2, str, cellIdentity.getOperatorAlphaLong(), cellIdentity.getOperatorAlphaShort());
    }
}
