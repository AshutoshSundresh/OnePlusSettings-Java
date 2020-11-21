package com.android.settings.network.telephony;

import android.content.Context;
import android.telephony.AccessNetworkConstants;
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
import android.telephony.CellSignalStrength;
import android.telephony.SignalStrength;
import android.util.Log;
import androidx.preference.Preference;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0017R$string;
import com.android.settings.Utils;
import java.util.List;
import java.util.Objects;

public class NetworkOperatorPreference extends Preference {
    private CellIdentity mCellId;
    private CellInfo mCellInfo;
    private List<String> mForbiddenPlmns;
    private boolean mIsAdvancedScanSupported;
    private int mLevel;
    private boolean mShow4GForLTE;

    public NetworkOperatorPreference(Context context, CellInfo cellInfo, List<String> list, boolean z) {
        this(context, list, z);
        updateCell(cellInfo);
    }

    public NetworkOperatorPreference(Context context, CellIdentity cellIdentity, List<String> list, boolean z) {
        this(context, list, z);
        updateCell(null, cellIdentity);
    }

    private NetworkOperatorPreference(Context context, List<String> list, boolean z) {
        super(context);
        this.mLevel = -1;
        this.mForbiddenPlmns = list;
        this.mShow4GForLTE = z;
        this.mIsAdvancedScanSupported = Utils.isAdvancedPlmnScanSupported();
    }

    public void updateCell(CellInfo cellInfo) {
        updateCell(cellInfo, CellInfoUtil.getCellIdentity(cellInfo));
    }

    private void updateCell(CellInfo cellInfo, CellIdentity cellIdentity) {
        this.mCellInfo = cellInfo;
        this.mCellId = cellIdentity;
        refresh();
    }

    public boolean isSameCell(CellInfo cellInfo) {
        if (cellInfo == null) {
            return false;
        }
        return this.mCellId.equals(CellInfoUtil.getCellIdentity(cellInfo));
    }

    public void refresh() {
        String operatorName = getOperatorName();
        List<String> list = this.mForbiddenPlmns;
        if (list != null && list.contains(getOperatorNumeric())) {
            operatorName = operatorName + " " + getContext().getResources().getString(C0017R$string.forbidden_network);
        }
        setTitle(Objects.toString(operatorName, ""));
        CellInfo cellInfo = this.mCellInfo;
        if (cellInfo != null) {
            CellSignalStrength cellSignalStrength = getCellSignalStrength(cellInfo);
            int level = cellSignalStrength != null ? cellSignalStrength.getLevel() : -1;
            this.mLevel = level;
            updateIcon(level);
        }
    }

    @Override // androidx.preference.Preference
    public void setIcon(int i) {
        updateIcon(i);
    }

    public String getOperatorNumeric() {
        CellIdentityNr cellIdentityNr;
        String mccString;
        CellIdentity cellIdentity = this.mCellId;
        if (cellIdentity == null) {
            return null;
        }
        if (cellIdentity instanceof CellIdentityGsm) {
            return ((CellIdentityGsm) cellIdentity).getMobileNetworkOperator();
        }
        if (cellIdentity instanceof CellIdentityWcdma) {
            return ((CellIdentityWcdma) cellIdentity).getMobileNetworkOperator();
        }
        if (cellIdentity instanceof CellIdentityTdscdma) {
            return ((CellIdentityTdscdma) cellIdentity).getMobileNetworkOperator();
        }
        if (cellIdentity instanceof CellIdentityLte) {
            return ((CellIdentityLte) cellIdentity).getMobileNetworkOperator();
        }
        if (!(cellIdentity instanceof CellIdentityNr) || (mccString = (cellIdentityNr = (CellIdentityNr) cellIdentity).getMccString()) == null) {
            return null;
        }
        return mccString.concat(cellIdentityNr.getMncString());
    }

    public String getOperatorName() {
        return CellInfoUtil.getNetworkTitle(this.mCellId, getOperatorNumeric());
    }

    private int getIconIdForCell(CellInfo cellInfo) {
        if (cellInfo instanceof CellInfoGsm) {
            return C0008R$drawable.signal_strength_g;
        }
        if (cellInfo instanceof CellInfoCdma) {
            return C0008R$drawable.signal_strength_1x;
        }
        if ((cellInfo instanceof CellInfoWcdma) || (cellInfo instanceof CellInfoTdscdma)) {
            return C0008R$drawable.signal_strength_3g;
        }
        if (cellInfo instanceof CellInfoLte) {
            return this.mShow4GForLTE ? C0008R$drawable.ic_signal_strength_4g : C0008R$drawable.signal_strength_lte;
        }
        if (cellInfo instanceof CellInfoNr) {
            return C0008R$drawable.signal_strength_5g;
        }
        return 0;
    }

    private CellSignalStrength getCellSignalStrength(CellInfo cellInfo) {
        if (cellInfo instanceof CellInfoGsm) {
            return ((CellInfoGsm) cellInfo).getCellSignalStrength();
        }
        if (cellInfo instanceof CellInfoCdma) {
            return ((CellInfoCdma) cellInfo).getCellSignalStrength();
        }
        if (cellInfo instanceof CellInfoWcdma) {
            return ((CellInfoWcdma) cellInfo).getCellSignalStrength();
        }
        if (cellInfo instanceof CellInfoTdscdma) {
            return ((CellInfoTdscdma) cellInfo).getCellSignalStrength();
        }
        if (cellInfo instanceof CellInfoLte) {
            return ((CellInfoLte) cellInfo).getCellSignalStrength();
        }
        if (cellInfo instanceof CellInfoNr) {
            return ((CellInfoNr) cellInfo).getCellSignalStrength();
        }
        return null;
    }

    private void updateIcon(int i) {
        if (this.mIsAdvancedScanSupported && i >= 0 && i < SignalStrength.NUM_SIGNAL_STRENGTH_BINS) {
            setIcon(MobileNetworkUtils.getSignalStrengthIcon(getContext(), i, SignalStrength.NUM_SIGNAL_STRENGTH_BINS, getIconIdForCell(this.mCellInfo), false));
        }
    }

    public int getAccessNetworkType() {
        CellIdentity cellIdentity = this.mCellId;
        int i = 0;
        int type = cellIdentity == null ? 0 : cellIdentity.getType();
        if (type == 1) {
            i = 1;
        } else if (type == 3) {
            i = 3;
        } else if (type == 4 || type == 5) {
            i = 2;
        } else if (type == 6) {
            i = 6;
        }
        Log.d("NetworkOperatorPref", "AccessNetworkType: " + AccessNetworkConstants.AccessNetworkType.toString(i) + " (" + i + "), mCellId: " + this.mCellId.toString());
        return i;
    }
}
