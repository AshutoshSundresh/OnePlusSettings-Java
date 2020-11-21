package com.android.settings.network.telephony;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncResult;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.telephony.SubscriptionManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import androidx.constraintlayout.widget.R$styleable;
import com.android.internal.telephony.uicc.IccUtils;
import com.android.internal.telephony.uicc.PlmnActRecord;
import com.android.settings.C0003R$array;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.codeaurora.internal.IExtTelephony;

public class UserPLMNListActivity extends PreferenceActivity implements DialogInterface.OnCancelListener {
    private boolean mAirplaneModeOn = false;
    private IExtTelephony mExtTelephony;
    private MyHandler mHandler = new MyHandler();
    protected boolean mIsForeground = false;
    private int mNumRec = 0;
    private UPLMNInfoWithEf mOldInfo;
    private int mPhoneId = 0;
    private Map<Preference, UPLMNInfoWithEf> mPreferenceMap = new LinkedHashMap();
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        /* class com.android.settings.network.telephony.UserPLMNListActivity.AnonymousClass1 */

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("android.intent.action.AIRPLANE_MODE".equals(action)) {
                UserPLMNListActivity.this.mAirplaneModeOn = intent.getBooleanExtra("state", false);
                UserPLMNListActivity.this.setScreenEnabled();
            } else if ("com.qualcomm.qti.intent.action.ACTION_READ_EF_RESULT".equals(action)) {
                if (intent.getBooleanExtra("exception", false)) {
                    UserPLMNListActivity.log("ACTION_READ_EF_BROADCAST with exception");
                    Message obtainMessage = UserPLMNListActivity.this.mHandler.obtainMessage();
                    obtainMessage.what = 0;
                    obtainMessage.obj = new AsyncResult((Object) null, (Object) null, new Exception());
                    UserPLMNListActivity.this.mHandler.sendMessage(obtainMessage);
                    return;
                }
                UserPLMNListActivity.this.handleGetEFDone(intent.getByteArrayExtra("payload"));
            } else if ("com.qualcomm.qti.intent.action.ACTION_WRITE_EF_RESULT".equals(action)) {
                if (intent.getBooleanExtra("exception", false)) {
                    UserPLMNListActivity.log("ACTION_WRITE_EF_BROADCAST with exception");
                } else {
                    UserPLMNListActivity.log("handleSetEFDone: with OK result!");
                }
                UserPLMNListActivity.this.getUPLMNInfoFromEf();
            }
        }
    };
    private List<UPLMNInfoWithEf> mUPLMNList;
    private PreferenceScreen mUPLMNListContainer;

    private int convertAccessTech2NetworkMode(int i) {
        int i2 = (i & 16384) != 0 ? 8 : 0;
        if ((32768 & i) != 0) {
            i2 |= 4;
        }
        if ((i & 128) != 0) {
            i2 |= 1;
        }
        return (i & 64) != 0 ? i2 | 2 : i2;
    }

    private int convertNetworkMode2AccessTech(int i) {
        int i2 = (i & 8) != 0 ? 16384 : 0;
        if ((i & 4) != 0) {
            i2 |= 32768;
        }
        if ((i & 1) != 0) {
            i2 |= 128;
        }
        return (i & 2) != 0 ? i2 | 64 : i2;
    }

    /* access modifiers changed from: protected */
    public Dialog onCreateDialog(int i) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getText(C0017R$string.uplmn_settings_title));
        progressDialog.setIndeterminate(true);
        if (i == 99) {
            progressDialog.setCancelable(true);
            progressDialog.setOnCancelListener(this);
            progressDialog.setMessage(getText(C0017R$string.reading_settings));
            return progressDialog;
        } else if (i != 100) {
            return null;
        } else {
            progressDialog.setCancelable(false);
            progressDialog.setMessage(getText(C0017R$string.updating_settings));
            return progressDialog;
        }
    }

    public void onCancel(DialogInterface dialogInterface) {
        finish();
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(C0019R$xml.uplmn_settings);
        this.mUPLMNListContainer = (PreferenceScreen) findPreference("button_uplmn_list_key");
        this.mPhoneId = SubscriptionManager.getPhoneId(getIntent().getIntExtra("android.telephony.extra.SUBSCRIPTION_INDEX", -1));
        this.mExtTelephony = IExtTelephony.Stub.asInterface(ServiceManager.getService("qti.radio.extphone"));
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.AIRPLANE_MODE");
        intentFilter.addAction("com.qualcomm.qti.intent.action.ACTION_WRITE_EF_RESULT");
        intentFilter.addAction("com.qualcomm.qti.intent.action.ACTION_READ_EF_RESULT");
        registerReceiver(this.mReceiver, intentFilter);
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(this.mReceiver);
    }

    public void onResume() {
        super.onResume();
        boolean z = true;
        this.mIsForeground = true;
        getUPLMNInfoFromEf();
        showReadingDialog();
        if (Settings.System.getInt(getContentResolver(), "airplane_mode_on", 0) != 1) {
            z = false;
        }
        this.mAirplaneModeOn = z;
    }

    public void onPause() {
        super.onPause();
        this.mIsForeground = false;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, 1, 0, C0017R$string.uplmn_list_setting_add_plmn).setShowAsAction(1);
        return true;
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        if (menu != null) {
            menu.setGroupEnabled(0, !this.mAirplaneModeOn);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId != 1) {
            if (itemId == 16908332) {
                finish();
                return true;
            }
        } else if (!hasGetIccFileHandler()) {
            return true;
        } else {
            Intent intent = new Intent(this, UserPLMNEditorActivity.class);
            intent.putExtra("uplmn_code", "");
            intent.putExtra("uplmn_priority", 0);
            intent.putExtra("uplmn_service", 0);
            intent.putExtra("uplmn_add", true);
            intent.putExtra("uplmn_size", this.mUPLMNList.size());
            startActivityForResult(intent, R$styleable.Constraint_layout_goneMarginRight);
        }
        return super.onOptionsItemSelected(menuItem);
    }

    private void showReadingDialog() {
        if (this.mIsForeground && hasGetIccFileHandler()) {
            showDialog(99);
        }
    }

    private void showSavingDialog() {
        if (this.mIsForeground) {
            showDialog(100);
        }
    }

    private void dismissDialogSafely(int i) {
        try {
            dismissDialog(i);
        } catch (IllegalArgumentException unused) {
        }
    }

    public void onFinished(Preference preference, boolean z) {
        log("onFinished reading: " + z);
        if (z) {
            dismissDialogSafely(99);
        } else {
            dismissDialogSafely(100);
        }
        preference.setEnabled(true);
        setScreenEnabled();
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void getUPLMNInfoFromEf() {
        log("UPLMNInfoFromEf Start read...");
        if (!readEfFromIcc(28512)) {
            Log.w("UserPLMNListActivity", "mIccFileHandler is null");
        }
    }

    private boolean hasGetIccFileHandler() {
        try {
            if (this.mExtTelephony != null) {
                return this.mExtTelephony.hasGetIccFileHandler(this.mPhoneId, 1);
            }
            return false;
        } catch (RemoteException | NullPointerException e) {
            Log.e("UserPLMNListActivity", "hasGetIccFileHandler Exception: ", e);
            return false;
        }
    }

    private boolean readEfFromIcc(int i) {
        try {
            if (this.mExtTelephony != null) {
                return this.mExtTelephony.readEfFromIcc(this.mPhoneId, 1, i);
            }
            return false;
        } catch (RemoteException | NullPointerException e) {
            Log.e("UserPLMNListActivity", "readEfFromIcc Exception: ", e);
            return false;
        }
    }

    private boolean writeEfToIcc(byte[] bArr, int i) {
        try {
            if (this.mExtTelephony != null) {
                return this.mExtTelephony.writeEfToIcc(this.mPhoneId, 1, i, bArr);
            }
            return false;
        } catch (RemoteException | NullPointerException e) {
            Log.e("UserPLMNListActivity", "writeEfToIcc Exception: ", e);
            return false;
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void refreshUPLMNListPreference(ArrayList<UPLMNInfoWithEf> arrayList) {
        if (this.mUPLMNListContainer.getPreferenceCount() != 0) {
            this.mUPLMNListContainer.removeAll();
        }
        Map<Preference, UPLMNInfoWithEf> map = this.mPreferenceMap;
        if (map != null) {
            map.clear();
        }
        List<UPLMNInfoWithEf> list = this.mUPLMNList;
        if (list != null) {
            list.clear();
        }
        this.mUPLMNList = arrayList;
        if (arrayList == null) {
            log("refreshUPLMNListPreference : NULL UPLMN list!");
        } else {
            log("refreshUPLMNListPreference : list.size()" + arrayList.size());
        }
        if (arrayList == null || arrayList.size() == 0) {
            log("refreshUPLMNListPreference : NULL UPLMN list!");
            if (arrayList == null) {
                this.mUPLMNList = new ArrayList();
                return;
            }
            return;
        }
        Iterator<UPLMNInfoWithEf> it = arrayList.iterator();
        while (it.hasNext()) {
            UPLMNInfoWithEf next = it.next();
            addUPLMNPreference(next);
            log(next.toString());
        }
    }

    /* access modifiers changed from: package-private */
    public class UPLMNInfoWithEf {
        private int mNetworkMode;
        private String mOperatorNumeric;
        private int mPriority;

        public String getOperatorNumeric() {
            return this.mOperatorNumeric;
        }

        public int getNetworMode() {
            return this.mNetworkMode;
        }

        public int getPriority() {
            return this.mPriority;
        }

        public void setOperatorNumeric(String str) {
            this.mOperatorNumeric = str;
        }

        public void setPriority(int i) {
            this.mPriority = i;
        }

        public UPLMNInfoWithEf(UserPLMNListActivity userPLMNListActivity, String str, int i, int i2) {
            this.mOperatorNumeric = str;
            this.mNetworkMode = i;
            this.mPriority = i2;
        }

        public String toString() {
            return "UPLMNInfoWithEf " + this.mOperatorNumeric + "/" + this.mNetworkMode + "/" + this.mPriority;
        }
    }

    /* access modifiers changed from: package-private */
    public class PriorityCompare implements Comparator<UPLMNInfoWithEf> {
        PriorityCompare(UserPLMNListActivity userPLMNListActivity) {
        }

        public int compare(UPLMNInfoWithEf uPLMNInfoWithEf, UPLMNInfoWithEf uPLMNInfoWithEf2) {
            return uPLMNInfoWithEf.getPriority() - uPLMNInfoWithEf2.getPriority();
        }
    }

    private void addUPLMNPreference(UPLMNInfoWithEf uPLMNInfoWithEf) {
        Preference preference = new Preference(this);
        String operatorNumeric = uPLMNInfoWithEf.getOperatorNumeric();
        String networkModeString = getNetworkModeString(uPLMNInfoWithEf.getNetworMode(), operatorNumeric);
        preference.setTitle(operatorNumeric + "(" + networkModeString + ")");
        this.mUPLMNListContainer.addPreference(preference);
        this.mPreferenceMap.put(preference, uPLMNInfoWithEf);
    }

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        Intent intent = new Intent(this, UserPLMNEditorActivity.class);
        UPLMNInfoWithEf uPLMNInfoWithEf = this.mPreferenceMap.get(preference);
        this.mOldInfo = uPLMNInfoWithEf;
        intent.putExtra("uplmn_code", uPLMNInfoWithEf.getOperatorNumeric());
        intent.putExtra("uplmn_priority", uPLMNInfoWithEf.getPriority());
        intent.putExtra("uplmn_service", uPLMNInfoWithEf.getNetworMode());
        intent.putExtra("uplmn_add", false);
        intent.putExtra("uplmn_size", this.mUPLMNList.size());
        startActivityForResult(intent, R$styleable.Constraint_layout_goneMarginStart);
        return true;
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int i, int i2, Intent intent) {
        log("resultCode = " + i2 + ", requestCode = " + i);
        if (intent != null) {
            UPLMNInfoWithEf createNetworkInfofromIntent = createNetworkInfofromIntent(intent);
            if (i2 == 102) {
                handleSetUPLMN(handleDeleteList(this.mOldInfo));
            } else if (i2 != 101) {
            } else {
                if (i == 101) {
                    handleAddList(createNetworkInfofromIntent);
                } else if (i == 102) {
                    handleSetUPLMN(handleModifiedList(createNetworkInfofromIntent, this.mOldInfo));
                }
            }
        }
    }

    private UPLMNInfoWithEf createNetworkInfofromIntent(Intent intent) {
        return new UPLMNInfoWithEf(this, intent.getStringExtra("uplmn_code"), intent.getIntExtra("uplmn_service", 0), intent.getIntExtra("uplmn_priority", 0));
    }

    public static byte[] stringToBcdPlmn(String str) {
        if (str.length() == 5) {
            str = str + "f";
        }
        byte[] hexStringToBytes = IccUtils.hexStringToBytes(str);
        return new byte[]{(byte) ((hexStringToBytes[0] >> 4) | ((hexStringToBytes[0] << 4) & 240)), (byte) ((hexStringToBytes[1] >> 4) | ((hexStringToBytes[2] << 4) & 240)), (byte) ((hexStringToBytes[1] & 15) | (hexStringToBytes[2] & 240))};
    }

    private void handleSetUPLMN(ArrayList<UPLMNInfoWithEf> arrayList) {
        showSavingDialog();
        byte[] bArr = new byte[(this.mNumRec * 5)];
        for (int i = 0; i < this.mNumRec; i++) {
            int i2 = i * 5;
            bArr[i2] = -1;
            bArr[i2 + 1] = -1;
            bArr[i2 + 2] = -1;
            bArr[i2 + 3] = 0;
            bArr[i2 + 4] = 0;
        }
        int i3 = 0;
        while (i3 < arrayList.size() && i3 < this.mNumRec) {
            UPLMNInfoWithEf uPLMNInfoWithEf = arrayList.get(i3);
            String operatorNumeric = uPLMNInfoWithEf.getOperatorNumeric();
            if (TextUtils.isEmpty(operatorNumeric)) {
                break;
            }
            log("strOperNumeric = " + operatorNumeric);
            int i4 = i3 * 5;
            System.arraycopy(stringToBcdPlmn(operatorNumeric), 0, bArr, i4, 3);
            log("data[0] = " + ((int) bArr[i4]));
            log("data[1] = " + ((int) bArr[i4 + 1]));
            log("data[2] = " + ((int) bArr[i4 + 2]));
            int convertNetworkMode2AccessTech = convertNetworkMode2AccessTech(uPLMNInfoWithEf.getNetworMode());
            int i5 = i4 + 3;
            bArr[i5] = (byte) (convertNetworkMode2AccessTech >> 8);
            int i6 = i4 + 4;
            bArr[i6] = (byte) (convertNetworkMode2AccessTech & 255);
            log("accessTech = " + convertNetworkMode2AccessTech);
            log("data[3] = " + ((int) bArr[i5]));
            log("data[4] = " + ((int) bArr[i6]));
            i3++;
        }
        log("update EFuplmn Start.");
        writeEfToIcc(bArr, 28512);
    }

    private void handleAddList(UPLMNInfoWithEf uPLMNInfoWithEf) {
        log("handleAddList: add new network: " + uPLMNInfoWithEf);
        dumpUPLMNInfo(this.mUPLMNList);
        ArrayList<UPLMNInfoWithEf> arrayList = new ArrayList<>();
        for (int i = 0; i < this.mUPLMNList.size(); i++) {
            arrayList.add(this.mUPLMNList.get(i));
        }
        int binarySearch = Collections.binarySearch(this.mUPLMNList, uPLMNInfoWithEf, new PriorityCompare(this));
        if (binarySearch >= 0) {
            arrayList.add(binarySearch, uPLMNInfoWithEf);
        } else {
            arrayList.add(uPLMNInfoWithEf);
        }
        updateListPriority(arrayList);
        dumpUPLMNInfo(arrayList);
        handleSetUPLMN(arrayList);
    }

    private void dumpUPLMNInfo(List<UPLMNInfoWithEf> list) {
        for (int i = 0; i < list.size(); i++) {
            log("dumpUPLMNInfo : " + list.get(i).toString());
        }
    }

    private ArrayList<UPLMNInfoWithEf> handleModifiedList(UPLMNInfoWithEf uPLMNInfoWithEf, UPLMNInfoWithEf uPLMNInfoWithEf2) {
        log("handleModifiedList: change old info: " + uPLMNInfoWithEf2.toString() + " new info: " + uPLMNInfoWithEf.toString());
        dumpUPLMNInfo(this.mUPLMNList);
        PriorityCompare priorityCompare = new PriorityCompare(this);
        int binarySearch = Collections.binarySearch(this.mUPLMNList, uPLMNInfoWithEf2, priorityCompare);
        int binarySearch2 = Collections.binarySearch(this.mUPLMNList, uPLMNInfoWithEf, priorityCompare);
        ArrayList<UPLMNInfoWithEf> arrayList = new ArrayList<>();
        for (int i = 0; i < this.mUPLMNList.size(); i++) {
            arrayList.add(this.mUPLMNList.get(i));
        }
        if (binarySearch > binarySearch2) {
            arrayList.remove(binarySearch);
            arrayList.add(binarySearch2, uPLMNInfoWithEf);
        } else if (binarySearch < binarySearch2) {
            arrayList.add(binarySearch2 + 1, uPLMNInfoWithEf);
            arrayList.remove(binarySearch);
        } else {
            arrayList.remove(binarySearch);
            arrayList.add(binarySearch, uPLMNInfoWithEf);
        }
        updateListPriority(arrayList);
        dumpUPLMNInfo(arrayList);
        return arrayList;
    }

    private void updateListPriority(ArrayList<UPLMNInfoWithEf> arrayList) {
        Iterator<UPLMNInfoWithEf> it = arrayList.iterator();
        int i = 0;
        while (it.hasNext()) {
            it.next().setPriority(i);
            i++;
        }
    }

    private ArrayList<UPLMNInfoWithEf> handleDeleteList(UPLMNInfoWithEf uPLMNInfoWithEf) {
        log("handleDeleteList : " + uPLMNInfoWithEf.toString());
        dumpUPLMNInfo(this.mUPLMNList);
        ArrayList<UPLMNInfoWithEf> arrayList = new ArrayList<>();
        int binarySearch = Collections.binarySearch(this.mUPLMNList, uPLMNInfoWithEf, new PriorityCompare(this));
        for (int i = 0; i < this.mUPLMNList.size(); i++) {
            arrayList.add(this.mUPLMNList.get(i));
        }
        arrayList.remove(binarySearch);
        uPLMNInfoWithEf.setOperatorNumeric(null);
        arrayList.add(uPLMNInfoWithEf);
        updateListPriority(arrayList);
        dumpUPLMNInfo(arrayList);
        return arrayList;
    }

    /* access modifiers changed from: private */
    public class MyHandler extends Handler {
        private MyHandler() {
        }

        public void handleMessage(Message message) {
            if (message.what == 0) {
                handleGetUPLMNList(message);
            }
        }

        public void handleGetUPLMNList(Message message) {
            UserPLMNListActivity.log("handleGetUPLMNList: done");
            if (message.arg2 == 0) {
                UserPLMNListActivity userPLMNListActivity = UserPLMNListActivity.this;
                userPLMNListActivity.onFinished(userPLMNListActivity.mUPLMNListContainer, true);
            } else {
                UserPLMNListActivity userPLMNListActivity2 = UserPLMNListActivity.this;
                userPLMNListActivity2.onFinished(userPLMNListActivity2.mUPLMNListContainer, false);
            }
            AsyncResult asyncResult = (AsyncResult) message.obj;
            if (asyncResult.exception != null) {
                UserPLMNListActivity.log("handleGetUPLMNList with exception = " + asyncResult.exception);
                if (UserPLMNListActivity.this.mUPLMNList == null) {
                    UserPLMNListActivity.this.mUPLMNList = new ArrayList();
                    return;
                }
                return;
            }
            UserPLMNListActivity.this.refreshUPLMNListPreference((ArrayList) asyncResult.result);
        }
    }

    public void handleGetEFDone(byte[] bArr) {
        log("handleGetEFDone: done");
        this.mNumRec = bArr.length / 5;
        log("Received a PlmnActRecord, raw=" + IccUtils.bytesToHexString(bArr));
        PlmnActRecord[] records = PlmnActRecord.getRecords(bArr);
        log("PlmnActRecords=" + Arrays.toString(records));
        ArrayList arrayList = new ArrayList();
        int length = records.length;
        int i = 0;
        for (int i2 = 0; i2 < length; i2++) {
            PlmnActRecord plmnActRecord = records[i2];
            if (!plmnActRecord.plmn.regionMatches(true, 0, "FFFFF", 0, 5) && plmnActRecord.accessTechs != -1 && !TextUtils.isEmpty(plmnActRecord.plmn) && plmnActRecord.plmn.length() >= 5) {
                arrayList.add(new UPLMNInfoWithEf(this, plmnActRecord.plmn, convertAccessTech2NetworkMode(plmnActRecord.accessTechs), i));
            }
            i++;
        }
        Message obtainMessage = this.mHandler.obtainMessage();
        obtainMessage.what = 0;
        log("handleGetEFDone : ret.size()" + arrayList.size());
        obtainMessage.obj = new AsyncResult(obtainMessage.obj, arrayList, (Throwable) null);
        this.mHandler.sendMessage(obtainMessage);
    }

    private String getNetworkModeString(int i, String str) {
        Log.d("UserPLMNListActivity", "plmn = " + str);
        int convertEFMode2Ap = UserPLMNEditorActivity.convertEFMode2Ap(i);
        for (String str2 : getResources().getStringArray(C0003R$array.uplmn_cu_mcc_mnc_values)) {
            if (str.equals(str2)) {
                return getResources().getStringArray(C0003R$array.uplmn_prefer_network_mode_w_choices)[convertEFMode2Ap];
            }
        }
        return getResources().getStringArray(C0003R$array.uplmn_prefer_network_mode_td_choices)[convertEFMode2Ap];
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void setScreenEnabled() {
        getPreferenceScreen().setEnabled(!this.mAirplaneModeOn);
        invalidateOptionsMenu();
    }

    /* access modifiers changed from: private */
    public static void log(String str) {
        Log.d("UserPLMNListActivity", str);
    }
}
