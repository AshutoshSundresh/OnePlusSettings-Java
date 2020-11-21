package com.oneplus.settings.ringtone;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import com.android.settings.C0003R$array;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.oneplus.settings.ringtone.OPLocalRingtoneAdapter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;

public class OPLocalRingtonePickerActivity extends OPRingtoneBaseActivity {
    private static final String ALARMS_PATH = (SDCARD_PATH + "/Alarms/");
    private static final String NOTIFICATIONS_PATH = (SDCARD_PATH + "/Notifications/");
    private static final String[] PROJECTION = {"_id", "_display_name", "title", "_data", "mime_type", "title"};
    private static final String RECORD_PATH = (SDCARD_PATH + "/Record/");
    private static final Uri RECORD_URI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    private static final String RINGTONE_PATH = (SDCARD_PATH + "/Ringtones/");
    private static final String SDCARD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
    private static final String[] SELECTION_ARGS_ALL = {RECORD_PATH + "%"};
    private static final String[] SELECTION_ARGS_PART = {RECORD_PATH + "%"};
    private boolean isFirst = true;
    private View mHeaderMargin;
    private ListView mListView;
    private View mNofileView;
    private OPLocalRingtoneAdapter mOPLocalRingtoneAdapter;
    private AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
        /* class com.oneplus.settings.ringtone.OPLocalRingtonePickerActivity.AnonymousClass3 */

        @Override // android.widget.AdapterView.OnItemClickListener
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
            OPLocalRingtoneAdapter.RingtoneData ringtoneData = (OPLocalRingtoneAdapter.RingtoneData) adapterView.getItemAtPosition(i);
            OPLocalRingtonePickerActivity oPLocalRingtonePickerActivity = OPLocalRingtonePickerActivity.this;
            Uri uri = ringtoneData.mUri;
            oPLocalRingtonePickerActivity.mUriForDefaultItem = uri;
            oPLocalRingtonePickerActivity.updateChecks(uri);
            if (OPLocalRingtonePickerActivity.this.mSetExternalThread != null) {
                OPLocalRingtonePickerActivity.this.mSetExternalThread.stopThread();
                OPLocalRingtonePickerActivity.this.mSetExternalThread = null;
            }
            OPLocalRingtonePickerActivity.this.mSetExternalThread = new SetExternalThread(ringtoneData);
            OPLocalRingtonePickerActivity.this.mSetExternalThread.start();
        }
    };
    private AdapterView.OnItemSelectedListener mOnItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        /* class com.oneplus.settings.ringtone.OPLocalRingtonePickerActivity.AnonymousClass2 */

        @Override // android.widget.AdapterView.OnItemSelectedListener
        public void onNothingSelected(AdapterView<?> adapterView) {
        }

        @Override // android.widget.AdapterView.OnItemSelectedListener
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
            if (OPLocalRingtonePickerActivity.this.isFirst) {
                OPLocalRingtonePickerActivity.this.isFirst = false;
                return;
            }
            OPLocalRingtonePickerActivity.this.stopAnyPlayingRingtone();
            String name = OPLocalRingtonePickerActivity.class.getName();
            Log.v(name, "mOnItemSelectedListener position = " + i);
            if (i == 0) {
                OPLocalRingtonePickerActivity.this.startTask(0);
            } else if (i == 1) {
                OPLocalRingtonePickerActivity.this.startTask(1);
            }
        }
    };
    private ProgressBar mProgressBar;
    private SetExternalThread mSetExternalThread;
    private List mSystemRings = null;
    private WorkAsyncTask mWorkAsyncTask;

    /* access modifiers changed from: protected */
    @Override // com.oneplus.settings.ringtone.OPRingtoneBaseActivity
    public void onCreate(Bundle bundle) {
        String string;
        if (!(bundle == null || (string = bundle.getString("key_selected_item_uri")) == null)) {
            this.mUriForDefaultItem = Uri.parse(string);
        }
        super.onCreate(bundle);
        setContentView(C0012R$layout.op_preference_list_content_material);
        initActionbar();
        this.mListView = getListView();
        this.mNofileView = findViewById(C0010R$id.id_empty);
        this.mProgressBar = (ProgressBar) findViewById(C0010R$id.id_progress);
        this.mHeaderMargin = findViewById(C0010R$id.header_margin);
        this.mListView.setEmptyView(this.mNofileView);
        this.mListView.setOnItemClickListener(this.mOnItemClickListener);
        this.mListView.setDivider(null);
        Log.v(OPLocalRingtonePickerActivity.class.getName(), "onCreate startTask");
        startTask(0);
    }

    private void initActionbar() {
        Toolbar toolbar = (Toolbar) findViewById(C0010R$id.action_bar);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                /* class com.oneplus.settings.ringtone.OPLocalRingtonePickerActivity.AnonymousClass1 */

                public void onClick(View view) {
                    OPLocalRingtonePickerActivity.this.onBackPressed();
                }
            });
            View inflate = View.inflate(this, C0012R$layout.op_spinner_main, null);
            Spinner spinner = (Spinner) inflate.findViewById(C0010R$id.id_spinner);
            ArrayAdapter arrayAdapter = new ArrayAdapter(this, C0012R$layout.op_simple_spinner_item, 16908308, getResources().getStringArray(C0003R$array.oneplus_select_items));
            arrayAdapter.setDropDownViewResource(17367049);
            spinner.setAdapter((SpinnerAdapter) arrayAdapter);
            spinner.setOnItemSelectedListener(this.mOnItemSelectedListener);
            toolbar.addView(inflate);
        }
    }

    /* access modifiers changed from: protected */
    @Override // com.oneplus.settings.ringtone.OPRingtoneBaseActivity
    public void updateSelected() {
        if (this.mSystemRings == null) {
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void updateChecks(Uri uri) {
        List<OPLocalRingtoneAdapter.RingtoneData> list = this.mSystemRings;
        if (!(list == null || this.mOPLocalRingtoneAdapter == null)) {
            for (OPLocalRingtoneAdapter.RingtoneData ringtoneData : list) {
                ringtoneData.isCheck = ringtoneData.mUri.equals(uri);
            }
            this.mOPLocalRingtoneAdapter.notifyDataSetChanged();
        }
    }

    /* access modifiers changed from: private */
    public class WorkAsyncTask extends AsyncTask<Integer, Void, Void> {
        private boolean isclose = false;
        private ContentResolver resolver;

        public WorkAsyncTask(ContentResolver contentResolver) {
            this.resolver = contentResolver;
        }

        /* access modifiers changed from: protected */
        /* JADX WARNING: Removed duplicated region for block: B:12:0x0052 A[RETURN] */
        /* JADX WARNING: Removed duplicated region for block: B:13:0x0053  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public java.lang.Void doInBackground(java.lang.Integer... r10) {
            /*
            // Method dump skipped, instructions count: 341
            */
            throw new UnsupportedOperationException("Method not decompiled: com.oneplus.settings.ringtone.OPLocalRingtonePickerActivity.WorkAsyncTask.doInBackground(java.lang.Integer[]):java.lang.Void");
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(Void r4) {
            if (!this.isclose) {
                OPLocalRingtonePickerActivity.this.mProgressBar.setVisibility(8);
                OPLocalRingtonePickerActivity.this.mListView.setVisibility(0);
                OPLocalRingtonePickerActivity.this.mHeaderMargin.setVisibility(0);
                if (OPLocalRingtonePickerActivity.this.mOPLocalRingtoneAdapter == null) {
                    OPLocalRingtonePickerActivity oPLocalRingtonePickerActivity = OPLocalRingtonePickerActivity.this;
                    OPLocalRingtonePickerActivity oPLocalRingtonePickerActivity2 = OPLocalRingtonePickerActivity.this;
                    oPLocalRingtonePickerActivity.mOPLocalRingtoneAdapter = new OPLocalRingtoneAdapter(oPLocalRingtonePickerActivity2, oPLocalRingtonePickerActivity2.mSystemRings);
                    if (OPLocalRingtonePickerActivity.this.mListView != null && OPLocalRingtonePickerActivity.this.mOPLocalRingtoneAdapter != null) {
                        OPLocalRingtonePickerActivity.this.mListView.setAdapter((ListAdapter) OPLocalRingtonePickerActivity.this.mOPLocalRingtoneAdapter);
                        return;
                    }
                    return;
                }
                OPLocalRingtonePickerActivity.this.mOPLocalRingtoneAdapter.notifyDataSetChanged();
            }
        }

        public void setClose() {
            this.isclose = true;
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0054, code lost:
        if (r0 != null) goto L_0x0031;
     */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x005a  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean isApeFile(java.lang.String r4, java.lang.String r5) {
        /*
            r3 = this;
            java.lang.String r3 = "audio/*"
            boolean r3 = r3.equals(r4)
            r4 = 0
            if (r3 == 0) goto L_0x005e
            r3 = 0
            android.media.MediaExtractor r0 = new android.media.MediaExtractor     // Catch:{ IOException -> 0x003b, all -> 0x0037 }
            r0.<init>()     // Catch:{ IOException -> 0x003b, all -> 0x0037 }
            r0.setDataSource(r5)     // Catch:{ IOException -> 0x0035 }
            r3 = r4
        L_0x0013:
            int r5 = r0.getTrackCount()     // Catch:{ IOException -> 0x0035 }
            if (r3 >= r5) goto L_0x0031
            android.media.MediaFormat r5 = r0.getTrackFormat(r3)     // Catch:{ IOException -> 0x0035 }
            java.lang.String r1 = "mime"
            java.lang.String r5 = r5.getString(r1)     // Catch:{ IOException -> 0x0035 }
            if (r5 == 0) goto L_0x002e
            java.lang.String r1 = "audio/"
            boolean r5 = r5.startsWith(r1)     // Catch:{ IOException -> 0x0035 }
            if (r5 == 0) goto L_0x002e
            goto L_0x0031
        L_0x002e:
            int r3 = r3 + 1
            goto L_0x0013
        L_0x0031:
            r0.release()
            goto L_0x005e
        L_0x0035:
            r3 = move-exception
            goto L_0x003e
        L_0x0037:
            r4 = move-exception
            r0 = r3
            r3 = r4
            goto L_0x0058
        L_0x003b:
            r5 = move-exception
            r0 = r3
            r3 = r5
        L_0x003e:
            java.lang.String r5 = ""
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x0057 }
            r1.<init>()     // Catch:{ all -> 0x0057 }
            java.lang.String r2 = "ringtoneCopyFrom3rdParty: "
            r1.append(r2)     // Catch:{ all -> 0x0057 }
            r1.append(r3)     // Catch:{ all -> 0x0057 }
            java.lang.String r3 = r1.toString()     // Catch:{ all -> 0x0057 }
            com.oneplus.settings.ringtone.OPMyLog.e(r5, r3)     // Catch:{ all -> 0x0057 }
            if (r0 == 0) goto L_0x005e
            goto L_0x0031
        L_0x0057:
            r3 = move-exception
        L_0x0058:
            if (r0 == 0) goto L_0x005d
            r0.release()
        L_0x005d:
            throw r3
        L_0x005e:
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.oneplus.settings.ringtone.OPLocalRingtonePickerActivity.isApeFile(java.lang.String, java.lang.String):boolean");
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void startTask(int i) {
        WorkAsyncTask workAsyncTask = this.mWorkAsyncTask;
        if (workAsyncTask != null) {
            workAsyncTask.setClose();
            this.mWorkAsyncTask = null;
        }
        this.mProgressBar.setVisibility(0);
        this.mNofileView.setVisibility(8);
        this.mHeaderMargin.setVisibility(8);
        this.mListView.setVisibility(8);
        WorkAsyncTask workAsyncTask2 = new WorkAsyncTask(getContentResolver());
        this.mWorkAsyncTask = workAsyncTask2;
        workAsyncTask2.execute(Integer.valueOf(i));
    }

    /* access modifiers changed from: package-private */
    public class SetExternalThread extends Thread {
        private boolean isClose = false;
        private OPLocalRingtoneAdapter.RingtoneData mPreference;

        public SetExternalThread(OPLocalRingtoneAdapter.RingtoneData ringtoneData) {
            this.mPreference = ringtoneData;
        }

        public void run() {
            Uri updateExternalFile = OPLocalRingtonePickerActivity.this.updateExternalFile(this.mPreference);
            if (!this.isClose && updateExternalFile != null) {
                OPLocalRingtonePickerActivity oPLocalRingtonePickerActivity = OPLocalRingtonePickerActivity.this;
                if (!oPLocalRingtonePickerActivity.mContactsRingtone) {
                    if (oPLocalRingtonePickerActivity.getSimId() == 2) {
                        OPRingtoneManager.setActualRingtoneUriBySubId(OPLocalRingtonePickerActivity.this.getApplicationContext(), 1, updateExternalFile);
                    } else if (OPLocalRingtonePickerActivity.this.getSimId() == 1) {
                        OPRingtoneManager.setActualRingtoneUriBySubId(OPLocalRingtonePickerActivity.this.getApplicationContext(), 0, updateExternalFile);
                    } else if (!OPLocalRingtonePickerActivity.this.isThreePart()) {
                        OPRingtoneManager.setActualDefaultRingtoneUri(OPLocalRingtonePickerActivity.this.getApplicationContext(), OPLocalRingtonePickerActivity.this.mType, updateExternalFile);
                    }
                    OPLocalRingtonePickerActivity oPLocalRingtonePickerActivity2 = OPLocalRingtonePickerActivity.this;
                    if (!oPLocalRingtonePickerActivity2.mHasDefaultItem) {
                        if (oPLocalRingtonePickerActivity2.mUriForDefaultItem.equals(updateExternalFile)) {
                            OPRingtoneManager.updateDb(OPLocalRingtonePickerActivity.this.getApplicationContext(), updateExternalFile, OPLocalRingtonePickerActivity.this.mType);
                        }
                        OPMyLog.d("chenhl", "set ringtone ok!");
                        return;
                    }
                    Intent intent = new Intent();
                    intent.putExtra("android.intent.extra.ringtone.PICKED_URI", OPLocalRingtonePickerActivity.this.mUriForDefaultItem);
                    OPLocalRingtonePickerActivity.this.setResult(-1, intent);
                }
            }
        }

        public void stopThread() {
            this.isClose = true;
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private Uri updateExternalFile(OPLocalRingtoneAdapter.RingtoneData ringtoneData) {
        OPMyLog.d("chenhl", "getKey:" + ringtoneData.mUri);
        String str = ringtoneData.filepath;
        OPMyLog.d("chenhl", "path:" + str);
        File file = new File(str);
        if (!file.exists()) {
            this.mHandler.post(new Runnable() {
                /* class com.oneplus.settings.ringtone.OPLocalRingtonePickerActivity.AnonymousClass4 */

                public void run() {
                    OPLocalRingtonePickerActivity oPLocalRingtonePickerActivity = OPLocalRingtonePickerActivity.this;
                    Toast.makeText(oPLocalRingtonePickerActivity, oPLocalRingtonePickerActivity.getString(C0017R$string.oneplus_file_not_exist), 0).show();
                }
            });
            return null;
        }
        playRingtone(300, this.mUriForDefaultItem);
        if (str == null || str.startsWith("/storage/emulated/legacy") || str.startsWith(SDCARD_PATH)) {
            return this.mUriForDefaultItem;
        }
        File file2 = new File(checkDir() + file.getName());
        if (!file2.exists()) {
            copyFile(file, file2);
        }
        return updateDb(ringtoneData, file2.getAbsolutePath());
    }

    private String checkDir() {
        String str = RINGTONE_PATH;
        int i = this.mType;
        if (i == 2 || i == 8) {
            str = NOTIFICATIONS_PATH;
        } else if (i == 4) {
            str = ALARMS_PATH;
        }
        File file = new File(str);
        if (!file.exists()) {
            file.mkdirs();
        }
        return str;
    }

    public void copyFile(File file, File file2) {
        try {
            if (file.exists()) {
                FileInputStream fileInputStream = new FileInputStream(file);
                FileOutputStream fileOutputStream = new FileOutputStream(file2);
                byte[] bArr = new byte[1444];
                while (true) {
                    int read = fileInputStream.read(bArr);
                    if (read != -1) {
                        fileOutputStream.write(bArr, 0, read);
                    } else {
                        fileInputStream.close();
                        fileOutputStream.close();
                        return;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Uri updateDb(OPLocalRingtoneAdapter.RingtoneData ringtoneData, String str) {
        Uri uri;
        Boolean bool = Boolean.TRUE;
        Uri uri2 = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor query = getContentResolver().query(uri2, new String[]{"_id"}, "_data=?", new String[]{str}, null);
        if (query == null || !query.moveToFirst()) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("_data", str);
            contentValues.put("title", ringtoneData.title);
            contentValues.put("mime_type", ringtoneData.mimetype);
            int i = this.mType;
            if (i == 1) {
                contentValues.put("is_ringtone", bool);
            } else if (i == 2 || i == 8) {
                contentValues.put("is_notification", bool);
            } else {
                contentValues.put("is_alarm", bool);
            }
            ContentResolver contentResolver = getContentResolver();
            contentResolver.delete(uri2, "_data=\"" + str + "\"", null);
            uri = getContentResolver().insert(uri2, contentValues);
        } else {
            uri = ContentUris.withAppendedId(uri2, query.getLong(0));
        }
        if (query != null) {
            query.close();
        }
        OPMyLog.d("chenhl", "defaultitem:" + uri + " path:" + str);
        return uri;
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        WorkAsyncTask workAsyncTask = this.mWorkAsyncTask;
        if (workAsyncTask != null) {
            workAsyncTask.setClose();
            this.mWorkAsyncTask = null;
        }
        super.onDestroy();
    }
}
