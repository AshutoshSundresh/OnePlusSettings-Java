package com.android.settings.deviceinfo.simstatus;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.DeadObjectException;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.SparseArray;
import com.android.settings.C0003R$array;
import org.codeaurora.internal.BearerAllocationStatus;
import org.codeaurora.internal.Client;
import org.codeaurora.internal.DcParam;
import org.codeaurora.internal.IExtTelephony;
import org.codeaurora.internal.INetworkCallback;
import org.codeaurora.internal.NetworkCallbackBase;
import org.codeaurora.internal.NrConfigType;
import org.codeaurora.internal.NrIconType;
import org.codeaurora.internal.ServiceUtil;
import org.codeaurora.internal.SignalStrength;
import org.codeaurora.internal.Status;
import org.codeaurora.internal.Token;
import org.codeaurora.internal.UpperLayerIndInfo;

public class FiveGNetWorkStatus {
    private int mBindRetryTimes = 0;
    private INetworkCallback mCallback = new NetworkCallbackBase() {
        /* class com.android.settings.deviceinfo.simstatus.FiveGNetWorkStatus.AnonymousClass3 */

        public void on5gStatus(int i, Token token, Status status, boolean z) throws RemoteException {
            Log.d("FiveGNetWorkStatus", "on5gStatus: slotId= " + i + " token=" + token + " status=" + status + " enableStatus=" + z);
        }

        public void onNrDcParam(int i, Token token, Status status, DcParam dcParam) throws RemoteException {
            Log.d("FiveGNetWorkStatus", "onNrDcParam: slotId=" + i + " token=" + token + " status=" + status + " dcParam=" + dcParam);
            if (status.get() == 1) {
                FiveGServiceState currentServiceState = FiveGNetWorkStatus.this.getCurrentServiceState(i);
                currentServiceState.mDcnr = dcParam.getDcnr();
                FiveGNetWorkStatus.this.update5GIcon(currentServiceState, i);
            }
        }

        public void onSignalStrength(int i, Token token, Status status, SignalStrength signalStrength) throws RemoteException {
            Log.d("FiveGNetWorkStatus", "onSignalStrength: slotId=" + i + " token=" + token + " status=" + status + " signalStrength=" + signalStrength);
            if (status.get() == 1 && signalStrength != null) {
                FiveGServiceState currentServiceState = FiveGNetWorkStatus.this.getCurrentServiceState(i);
                currentServiceState.mLevel = FiveGNetWorkStatus.this.getRsrpLevel(signalStrength.getRsrp());
                currentServiceState.mRsrp = signalStrength.getRsrp();
                currentServiceState.mSnr = signalStrength.getSnr();
                FiveGNetWorkStatus.this.update5GIcon(currentServiceState, i);
            }
        }

        public void onAnyNrBearerAllocation(int i, Token token, Status status, BearerAllocationStatus bearerAllocationStatus) throws RemoteException {
            Log.d("FiveGNetWorkStatus", "onAnyNrBearerAllocation bearerStatus=" + bearerAllocationStatus.get());
            if (status.get() == 1) {
                FiveGServiceState currentServiceState = FiveGNetWorkStatus.this.getCurrentServiceState(i);
                currentServiceState.mBearerAllocationStatus = bearerAllocationStatus.get();
                FiveGNetWorkStatus.this.update5GIcon(currentServiceState, i);
            }
        }

        public void onUpperLayerIndInfo(int i, Token token, Status status, UpperLayerIndInfo upperLayerIndInfo) throws RemoteException {
            Log.d("FiveGNetWorkStatus", "onUpperLayerIndInfo plmn=" + upperLayerIndInfo.getPlmnInfoListAvailable() + " upperLayerIndInfo=" + upperLayerIndInfo.getUpperLayerIndInfoAvailable());
            if (status.get() == 1) {
                FiveGServiceState currentServiceState = FiveGNetWorkStatus.this.getCurrentServiceState(i);
                currentServiceState.mPlmn = upperLayerIndInfo.getPlmnInfoListAvailable();
                currentServiceState.mUpperLayerInd = upperLayerIndInfo.getUpperLayerIndInfoAvailable();
                FiveGNetWorkStatus.this.update5GIcon(currentServiceState, i);
            }
        }

        public void on5gConfigInfo(int i, Token token, Status status, NrConfigType nrConfigType) throws RemoteException {
            Log.d("FiveGNetWorkStatus", "on5gConfigInfo: slotId = " + i + " token = " + token + " status" + status + " NrConfigType = " + nrConfigType);
            if (status.get() == 1) {
                FiveGServiceState currentServiceState = FiveGNetWorkStatus.this.getCurrentServiceState(i);
                currentServiceState.mNrConfigType = nrConfigType.get();
                On5gInfomationUpdateListener on5gInfomationUpdateListener = FiveGNetWorkStatus.this.mOn5gInfomationUpdateListener;
                if (on5gInfomationUpdateListener != null) {
                    on5gInfomationUpdateListener.On5gInfomationUpdate(currentServiceState.mNrConfigType);
                }
                FiveGNetWorkStatus.this.update5GIcon(currentServiceState, i);
            }
        }

        public void onNrIconType(int i, Token token, Status status, NrIconType nrIconType) throws RemoteException {
            Log.d("FiveGNetWorkStatus", "onNrIconType: slotId = " + i + " token = " + token + " status" + status + " NrIconType = " + nrIconType);
            if (status.get() == 1) {
                FiveGServiceState currentServiceState = FiveGNetWorkStatus.this.getCurrentServiceState(i);
                currentServiceState.mNrIconType = nrIconType.get();
                FiveGNetWorkStatus.this.update5GIcon(currentServiceState, i);
            }
        }
    };
    private Client mClient;
    private Context mContext;
    private final SparseArray<FiveGServiceState> mCurrentServiceStates = new SparseArray<>();
    private Handler mHandler = new Handler() {
        /* class com.android.settings.deviceinfo.simstatus.FiveGNetWorkStatus.AnonymousClass1 */

        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 1024) {
                FiveGNetWorkStatus.this.binderService();
            } else if (i == 1025) {
                FiveGNetWorkStatus.this.initFiveGServiceState();
            }
        }
    };
    private int mInitRetryTimes = 0;
    private final SparseArray<FiveGServiceState> mLastServiceStates = new SparseArray<>();
    private IExtTelephony mNetworkService;
    On5gInfomationUpdateListener mOn5gInfomationUpdateListener;
    private String mPackageName;
    private int mPhoneCount;
    private final int[] mRsrpThresholds;
    private boolean mServiceConnected;
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        /* class com.android.settings.deviceinfo.simstatus.FiveGNetWorkStatus.AnonymousClass2 */

        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d("FiveGNetWorkStatus", "onServiceConnected:" + iBinder);
            try {
                IExtTelephony asInterface = IExtTelephony.Stub.asInterface(iBinder);
                if (asInterface != FiveGNetWorkStatus.this.mNetworkService || FiveGNetWorkStatus.this.mClient == null) {
                    if (FiveGNetWorkStatus.this.mNetworkService != null) {
                        FiveGNetWorkStatus.this.mNetworkService.unRegisterCallback(FiveGNetWorkStatus.this.mCallback);
                    }
                    FiveGNetWorkStatus.this.mNetworkService = asInterface;
                    FiveGNetWorkStatus.this.mClient = FiveGNetWorkStatus.this.mNetworkService.registerCallback(FiveGNetWorkStatus.this.mPackageName, FiveGNetWorkStatus.this.mCallback);
                }
                FiveGNetWorkStatus.this.mServiceConnected = true;
                FiveGNetWorkStatus.this.initFiveGServiceState();
                Log.d("FiveGNetWorkStatus", "Client = " + FiveGNetWorkStatus.this.mClient);
            } catch (Exception e) {
                Log.d("FiveGNetWorkStatus", "onServiceConnected: Exception = " + e);
            }
        }

        public void onServiceDisconnected(ComponentName componentName) {
            Log.d("FiveGNetWorkStatus", "onServiceDisconnected:" + componentName);
            cleanup();
        }

        public void onBindingDied(ComponentName componentName) {
            Log.d("FiveGNetWorkStatus", "onBindingDied:" + componentName);
            cleanup();
            if (FiveGNetWorkStatus.this.mBindRetryTimes < 4) {
                Log.d("FiveGNetWorkStatus", "try to re-bind");
                FiveGNetWorkStatus.this.mHandler.sendEmptyMessageDelayed(1024, (long) ((FiveGNetWorkStatus.this.mBindRetryTimes * 2000) + 3000));
            }
        }

        private void cleanup() {
            Log.d("FiveGNetWorkStatus", "cleanup");
            FiveGNetWorkStatus.this.mServiceConnected = false;
            FiveGNetWorkStatus.this.mNetworkService = null;
            FiveGNetWorkStatus.this.mClient = null;
        }
    };

    public interface On5gInfomationUpdateListener {
        void On5gInfomationUpdate(int i);
    }

    private boolean getNrIconGroup(int i, int i2) {
        return i == 1 || i == 2;
    }

    public static class FiveGServiceState {
        private int mBearerAllocationStatus = 0;
        private boolean mCampOnFiveGService;
        private int mDcnr;
        private int mLevel;
        private int mNrConfigType = 0;
        private int mNrIconType = -1;
        private int mPlmn;
        private int mRsrp;
        private boolean mShowFiveGService = false;
        private int mSnr;
        private int mUpperLayerInd;

        public boolean isConnectedOnSaMode() {
            return this.mNrConfigType == 1 && this.mShowFiveGService;
        }

        public boolean isConnectedOnNsaMode() {
            return this.mNrConfigType == 0 && this.mShowFiveGService;
        }

        public boolean getShowFiveGService() {
            return this.mShowFiveGService;
        }

        public int getRsrp() {
            return this.mRsrp;
        }

        public int getSnr() {
            return this.mSnr;
        }

        /* access modifiers changed from: package-private */
        public int getNrConfigType() {
            return this.mNrConfigType;
        }
    }

    public FiveGNetWorkStatus(Context context) {
        this.mContext = context;
        this.mPackageName = context.getPackageName();
        this.mPhoneCount = ((TelephonyManager) this.mContext.getSystemService("phone")).getPhoneCount();
        this.mRsrpThresholds = this.mContext.getResources().getIntArray(C0003R$array.config_5g_signal_rsrp_thresholds);
        this.mContext.getResources().getIntArray(C0003R$array.config_5g_signal_snr_thresholds);
    }

    public void registerListener(int i, On5gInfomationUpdateListener on5gInfomationUpdateListener) {
        Log.d("FiveGNetWorkStatus", "registerListener phoneId=" + i);
        this.mOn5gInfomationUpdateListener = on5gInfomationUpdateListener;
        this.mBindRetryTimes = 0;
        this.mInitRetryTimes = 0;
        if (!isServiceConnected()) {
            binderService();
        } else {
            initFiveGServiceState(i);
        }
    }

    public void unregisterListener(int i) {
        Log.d("FiveGNetWorkStatus", "unregisterListener phoneId=" + i);
        if (this.mOn5gInfomationUpdateListener != null) {
            this.mOn5gInfomationUpdateListener = null;
        }
        this.mCurrentServiceStates.remove(i);
        this.mLastServiceStates.remove(i);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void binderService() {
        boolean bindService = ServiceUtil.bindService(this.mContext, this.mServiceConnection);
        Log.d("FiveGNetWorkStatus", " bind service " + bindService);
        if (!bindService && this.mBindRetryTimes < 4 && !this.mHandler.hasMessages(1024)) {
            this.mHandler.sendEmptyMessageDelayed(1024, (long) ((this.mBindRetryTimes * 2000) + 3000));
            this.mBindRetryTimes++;
        }
    }

    public boolean isServiceConnected() {
        return this.mServiceConnected;
    }

    public FiveGServiceState getCurrentServiceState(int i) {
        return getServiceState(i, this.mCurrentServiceStates);
    }

    public static FiveGServiceState getServiceState(int i, SparseArray<FiveGServiceState> sparseArray) {
        FiveGServiceState fiveGServiceState = sparseArray.get(i);
        if (fiveGServiceState != null) {
            return fiveGServiceState;
        }
        FiveGServiceState fiveGServiceState2 = new FiveGServiceState();
        sparseArray.put(i, fiveGServiceState2);
        return fiveGServiceState2;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private int getRsrpLevel(int i) {
        return getLevel(i, this.mRsrpThresholds);
    }

    private static int getLevel(int i, int[] iArr) {
        int i2 = 0;
        if (iArr[iArr.length - 1] >= i && i >= iArr[0]) {
            while (true) {
                if (i2 >= iArr.length - 1) {
                    i2 = 1;
                    break;
                }
                if (iArr[i2] < i) {
                    int i3 = i2 + 1;
                    if (i <= iArr[i3]) {
                        i2 = i3;
                        break;
                    }
                }
                i2++;
            }
        }
        return i2 + 1;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void initFiveGServiceState() {
        Log.d("FiveGNetWorkStatus", "initFiveGServiceState mPhoneCount=" + this.mPhoneCount);
        for (int i = 0; i < this.mPhoneCount; i++) {
            initFiveGServiceState(i);
        }
    }

    private void initFiveGServiceState(int i) {
        Log.d("FiveGNetWorkStatus", "mNetworkService=" + this.mNetworkService + " mClient=" + this.mClient);
        if (this.mNetworkService != null && this.mClient != null) {
            Log.d("FiveGNetWorkStatus", "query 5G service state for phoneId " + i);
            try {
                Log.d("FiveGNetWorkStatus", "queryNrDcParam result:" + this.mNetworkService.queryNrDcParam(i, this.mClient));
                Log.d("FiveGNetWorkStatus", "queryNrBearerAllocation result:" + this.mNetworkService.queryNrBearerAllocation(i, this.mClient));
                Log.d("FiveGNetWorkStatus", "queryNrSignalStrength result:" + this.mNetworkService.queryNrSignalStrength(i, this.mClient));
                Log.d("FiveGNetWorkStatus", "queryUpperLayerIndInfo result:" + this.mNetworkService.queryUpperLayerIndInfo(i, this.mClient));
                Log.d("FiveGNetWorkStatus", "query5gConfigInfo result:" + this.mNetworkService.query5gConfigInfo(i, this.mClient));
                Log.d("FiveGNetWorkStatus", "queryNrIconType result:" + this.mNetworkService.queryNrIconType(i, this.mClient));
            } catch (DeadObjectException e) {
                if (this.mBindRetryTimes < 4 && !this.mHandler.hasMessages(1024)) {
                    this.mHandler.sendEmptyMessageDelayed(1024, (long) ((this.mBindRetryTimes * 2000) + 3000));
                    this.mBindRetryTimes++;
                    Log.d("FiveGNetWorkStatus", "initFiveGServiceState: Exception = " + e);
                }
            } catch (Exception e2) {
                Log.d("FiveGNetWorkStatus", "initFiveGServiceState: Exception = " + e2);
                if (this.mInitRetryTimes < 4 && !this.mHandler.hasMessages(1025)) {
                    this.mHandler.sendEmptyMessageDelayed(1025, (long) ((this.mInitRetryTimes * 2000) + 3000));
                    this.mInitRetryTimes++;
                }
            }
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void update5GIcon(FiveGServiceState fiveGServiceState, int i) {
        if (fiveGServiceState.mNrConfigType == 1) {
            fiveGServiceState.mShowFiveGService = getSaIcon(fiveGServiceState);
            fiveGServiceState.mCampOnFiveGService = getSaIcon(fiveGServiceState);
        } else if (fiveGServiceState.mNrConfigType == 0) {
            fiveGServiceState.mShowFiveGService = getNrIconGroup(fiveGServiceState.mNrIconType, i);
            fiveGServiceState.mCampOnFiveGService = false;
        } else {
            fiveGServiceState.mShowFiveGService = false;
            fiveGServiceState.mCampOnFiveGService = false;
        }
    }

    private boolean getSaIcon(FiveGServiceState fiveGServiceState) {
        return fiveGServiceState.mBearerAllocationStatus > 0;
    }
}
