package com.android.settings.development.storage;

import android.app.ActionBar;
import android.app.ListActivity;
import android.app.blob.BlobInfo;
import android.app.blob.BlobStoreManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.UserHandle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import com.android.internal.util.CollectionUtils;
import com.android.settings.C0017R$string;
import java.io.IOException;
import java.util.List;

public class BlobInfoListView extends ListActivity {
    private BlobListAdapter mAdapter;
    private BlobStoreManager mBlobStoreManager;
    private Context mContext;
    private LayoutInflater mInflater;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mContext = this;
        this.mBlobStoreManager = (BlobStoreManager) getSystemService(BlobStoreManager.class);
        this.mInflater = (LayoutInflater) getSystemService(LayoutInflater.class);
        BlobListAdapter blobListAdapter = new BlobListAdapter(this);
        this.mAdapter = blobListAdapter;
        setListAdapter(blobListAdapter);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public boolean onNavigateUp() {
        finish();
        return true;
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        queryBlobsAndUpdateList();
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i == 8108 && i2 == -1) {
            Toast.makeText(this, C0017R$string.shared_data_delete_failure_text, 1).show();
        }
    }

    /* access modifiers changed from: protected */
    public void onListItemClick(ListView listView, View view, int i, long j) {
        BlobInfo blobInfo = (BlobInfo) this.mAdapter.getItem(i);
        if (CollectionUtils.isEmpty(blobInfo.getLeases())) {
            showDeleteBlobDialog(blobInfo);
            return;
        }
        Intent intent = new Intent(this, LeaseInfoListView.class);
        intent.putExtra("BLOB_KEY", (Parcelable) blobInfo);
        startActivityForResult(intent, 8108);
    }

    private void showDeleteBlobDialog(BlobInfo blobInfo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.mContext);
        builder.setMessage(C0017R$string.shared_data_no_accessors_dialog_text);
        builder.setPositiveButton(17039370, getDialogOnClickListener(blobInfo));
        builder.setNegativeButton(17039360, (DialogInterface.OnClickListener) null);
        builder.create().show();
    }

    private DialogInterface.OnClickListener getDialogOnClickListener(BlobInfo blobInfo) {
        return new DialogInterface.OnClickListener(blobInfo) {
            /* class com.android.settings.development.storage.$$Lambda$BlobInfoListView$mNRtXv7UcK7Swcmvco9OQUcRSQ8 */
            public final /* synthetic */ BlobInfo f$1;

            {
                this.f$1 = r2;
            }

            public final void onClick(DialogInterface dialogInterface, int i) {
                BlobInfoListView.this.lambda$getDialogOnClickListener$0$BlobInfoListView(this.f$1, dialogInterface, i);
            }
        };
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$getDialogOnClickListener$0 */
    public /* synthetic */ void lambda$getDialogOnClickListener$0$BlobInfoListView(BlobInfo blobInfo, DialogInterface dialogInterface, int i) {
        try {
            this.mBlobStoreManager.deleteBlob(blobInfo);
        } catch (IOException e) {
            Log.e("BlobInfoListView", "Unable to delete blob: " + e.getMessage());
            Toast.makeText(this, C0017R$string.shared_data_delete_failure_text, 1).show();
        }
        queryBlobsAndUpdateList();
    }

    private void queryBlobsAndUpdateList() {
        try {
            this.mAdapter.updateList(this.mBlobStoreManager.queryBlobsForUser(UserHandle.CURRENT));
        } catch (IOException e) {
            Log.e("BlobInfoListView", "Unable to fetch blobs for current user: " + e.getMessage());
            Toast.makeText(this, C0017R$string.shared_data_query_failure_text, 1).show();
            finish();
        }
    }

    /* access modifiers changed from: private */
    public class BlobListAdapter extends ArrayAdapter<BlobInfo> {
        BlobListAdapter(Context context) {
            super(context, 0);
        }

        /* access modifiers changed from: package-private */
        public void updateList(List<BlobInfo> list) {
            clear();
            if (list.isEmpty()) {
                BlobInfoListView.this.finish();
            } else {
                addAll(list);
            }
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            BlobInfoViewHolder createOrRecycle = BlobInfoViewHolder.createOrRecycle(BlobInfoListView.this.mInflater, view);
            View view2 = createOrRecycle.rootView;
            BlobInfo blobInfo = (BlobInfo) getItem(i);
            createOrRecycle.blobLabel.setText(blobInfo.getLabel());
            createOrRecycle.blobId.setText(BlobInfoListView.this.getString(C0017R$string.blob_id_text, new Object[]{Long.valueOf(blobInfo.getId())}));
            createOrRecycle.blobExpiry.setText(BlobInfoListView.this.getString(C0017R$string.blob_expires_text, new Object[]{SharedDataUtils.formatTime(blobInfo.getExpiryTimeMs())}));
            createOrRecycle.blobSize.setText(SharedDataUtils.formatSize(blobInfo.getSizeBytes()));
            return view2;
        }
    }
}
