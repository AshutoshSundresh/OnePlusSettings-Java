package com.android.settings.development.storage;

import android.app.ActionBar;
import android.app.ListActivity;
import android.app.blob.BlobInfo;
import android.app.blob.BlobStoreManager;
import android.app.blob.LeaseInfo;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import com.android.internal.util.CollectionUtils;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import java.io.IOException;
import java.util.List;

public class LeaseInfoListView extends ListActivity {
    private LeaseListAdapter mAdapter;
    private BlobInfo mBlobInfo;
    private BlobStoreManager mBlobStoreManager;
    private Context mContext;
    private LayoutInflater mInflater;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mContext = this;
        this.mBlobStoreManager = (BlobStoreManager) getSystemService(BlobStoreManager.class);
        this.mInflater = (LayoutInflater) getSystemService(LayoutInflater.class);
        this.mBlobInfo = getIntent().getParcelableExtra("BLOB_KEY");
        LeaseListAdapter leaseListAdapter = new LeaseListAdapter(this);
        this.mAdapter = leaseListAdapter;
        if (leaseListAdapter.isEmpty()) {
            Log.e("LeaseInfoListView", "Error fetching leases for shared data: " + this.mBlobInfo.toString());
            finish();
        }
        setListAdapter(this.mAdapter);
        getListView().addHeaderView(getHeaderView());
        getListView().addFooterView(getFooterView());
        getListView().setClickable(false);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public boolean onNavigateUp() {
        finish();
        return true;
    }

    private LinearLayout getHeaderView() {
        LinearLayout linearLayout = (LinearLayout) this.mInflater.inflate(C0012R$layout.blob_list_item_view, (ViewGroup) null);
        linearLayout.setEnabled(false);
        TextView textView = (TextView) linearLayout.findViewById(C0010R$id.blob_label);
        textView.setText(this.mBlobInfo.getLabel());
        textView.setTypeface(Typeface.DEFAULT_BOLD);
        ((TextView) linearLayout.findViewById(C0010R$id.blob_id)).setText(getString(C0017R$string.blob_id_text, new Object[]{Long.valueOf(this.mBlobInfo.getId())}));
        ((TextView) linearLayout.findViewById(C0010R$id.blob_expiry)).setVisibility(8);
        ((TextView) linearLayout.findViewById(C0010R$id.blob_size)).setText(SharedDataUtils.formatSize(this.mBlobInfo.getSizeBytes()));
        return linearLayout;
    }

    private Button getFooterView() {
        Button button = new Button(this);
        button.setLayoutParams(new ViewGroup.LayoutParams(-1, -2));
        button.setText(C0017R$string.delete_blob_text);
        button.setOnClickListener(getButtonOnClickListener());
        return button;
    }

    private View.OnClickListener getButtonOnClickListener() {
        return new View.OnClickListener() {
            /* class com.android.settings.development.storage.$$Lambda$LeaseInfoListView$CMUt1eKJtW0KqtYKnvUHjRAN6E */

            public final void onClick(View view) {
                LeaseInfoListView.this.lambda$getButtonOnClickListener$0$LeaseInfoListView(view);
            }
        };
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$getButtonOnClickListener$0 */
    public /* synthetic */ void lambda$getButtonOnClickListener$0$LeaseInfoListView(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.mContext);
        builder.setMessage(C0017R$string.delete_blob_confirmation_text);
        builder.setPositiveButton(17039370, getDialogOnClickListener());
        builder.setNegativeButton(17039360, (DialogInterface.OnClickListener) null);
        builder.create().show();
    }

    private DialogInterface.OnClickListener getDialogOnClickListener() {
        return new DialogInterface.OnClickListener() {
            /* class com.android.settings.development.storage.$$Lambda$LeaseInfoListView$A4QVig8RPjgXAHHThxcOTGf6rTI */

            public final void onClick(DialogInterface dialogInterface, int i) {
                LeaseInfoListView.this.lambda$getDialogOnClickListener$1$LeaseInfoListView(dialogInterface, i);
            }
        };
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$getDialogOnClickListener$1 */
    public /* synthetic */ void lambda$getDialogOnClickListener$1$LeaseInfoListView(DialogInterface dialogInterface, int i) {
        try {
            this.mBlobStoreManager.deleteBlob(this.mBlobInfo);
            setResult(1);
        } catch (IOException e) {
            Log.e("LeaseInfoListView", "Unable to delete blob: " + e.getMessage());
            setResult(-1);
        }
        finish();
    }

    private class LeaseListAdapter extends ArrayAdapter<LeaseInfo> {
        private Context mContext;

        LeaseListAdapter(Context context) {
            super(context, 0);
            this.mContext = context;
            List leases = LeaseInfoListView.this.mBlobInfo.getLeases();
            if (!CollectionUtils.isEmpty(leases)) {
                addAll(leases);
            }
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            Drawable drawable;
            LeaseInfoViewHolder createOrRecycle = LeaseInfoViewHolder.createOrRecycle(LeaseInfoListView.this.mInflater, view);
            View view2 = createOrRecycle.rootView;
            view2.setEnabled(false);
            LeaseInfo leaseInfo = (LeaseInfo) getItem(i);
            try {
                drawable = this.mContext.getPackageManager().getApplicationIcon(leaseInfo.getPackageName());
            } catch (PackageManager.NameNotFoundException unused) {
                drawable = this.mContext.getDrawable(17301651);
            }
            createOrRecycle.appIcon.setImageDrawable(drawable);
            createOrRecycle.leasePackageName.setText(leaseInfo.getPackageName());
            createOrRecycle.leaseDescription.setText(getDescriptionString(leaseInfo));
            createOrRecycle.leaseExpiry.setText(LeaseInfoListView.this.getString(C0017R$string.accessor_expires_text, new Object[]{SharedDataUtils.formatTime(leaseInfo.getExpiryTimeMillis())}));
            return view2;
        }

        /* JADX WARNING: Code restructure failed: missing block: B:10:0x0022, code lost:
            r0 = r4.getDescription().toString();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:12:0x002e, code lost:
            if (android.text.TextUtils.isEmpty(r0) == false) goto L_0x0031;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:15:0x0037, code lost:
            if (android.text.TextUtils.isEmpty(null) != false) goto L_0x0039;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:16:0x0039, code lost:
            r3.this$0.getString(com.android.settings.C0017R$string.accessor_no_description_text);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:17:0x0040, code lost:
            throw r4;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:18:?, code lost:
            return r0;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:6:0x001a, code lost:
            r4 = move-exception;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:9:0x0020, code lost:
            if (r4.getDescription() != null) goto L_0x0022;
         */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:7:0x001c */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private java.lang.String getDescriptionString(android.app.blob.LeaseInfo r4) {
            /*
                r3 = this;
                r0 = 0
                com.android.settings.development.storage.LeaseInfoListView r1 = com.android.settings.development.storage.LeaseInfoListView.this     // Catch:{ NotFoundException -> 0x001c }
                int r2 = r4.getDescriptionResId()     // Catch:{ NotFoundException -> 0x001c }
                java.lang.String r4 = r1.getString(r2)     // Catch:{ NotFoundException -> 0x001c }
                boolean r0 = android.text.TextUtils.isEmpty(r4)
                if (r0 == 0) goto L_0x0032
            L_0x0011:
                com.android.settings.development.storage.LeaseInfoListView r3 = com.android.settings.development.storage.LeaseInfoListView.this
                int r4 = com.android.settings.C0017R$string.accessor_no_description_text
                java.lang.String r4 = r3.getString(r4)
                goto L_0x0032
            L_0x001a:
                r4 = move-exception
                goto L_0x0033
            L_0x001c:
                java.lang.CharSequence r1 = r4.getDescription()     // Catch:{ all -> 0x001a }
                if (r1 == 0) goto L_0x002a
                java.lang.CharSequence r4 = r4.getDescription()     // Catch:{ all -> 0x001a }
                java.lang.String r0 = r4.toString()     // Catch:{ all -> 0x001a }
            L_0x002a:
                boolean r4 = android.text.TextUtils.isEmpty(r0)
                if (r4 == 0) goto L_0x0031
                goto L_0x0011
            L_0x0031:
                r4 = r0
            L_0x0032:
                return r4
            L_0x0033:
                boolean r0 = android.text.TextUtils.isEmpty(r0)
                if (r0 == 0) goto L_0x0040
                com.android.settings.development.storage.LeaseInfoListView r3 = com.android.settings.development.storage.LeaseInfoListView.this
                int r0 = com.android.settings.C0017R$string.accessor_no_description_text
                r3.getString(r0)
            L_0x0040:
                throw r4
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.settings.development.storage.LeaseInfoListView.LeaseListAdapter.getDescriptionString(android.app.blob.LeaseInfo):java.lang.String");
        }
    }
}
