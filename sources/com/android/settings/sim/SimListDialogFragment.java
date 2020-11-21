package com.android.settings.sim;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.android.settingslib.Utils;
import java.util.ArrayList;
import java.util.List;

public class SimListDialogFragment extends SimDialogFragment implements DialogInterface.OnClickListener {
    protected SelectSubscriptionAdapter mAdapter;
    List<SubscriptionInfo> mSubscriptions;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1707;
    }

    public static SimListDialogFragment newInstance(int i, int i2, boolean z) {
        SimListDialogFragment simListDialogFragment = new SimListDialogFragment();
        Bundle initArguments = SimDialogFragment.initArguments(i, i2);
        initArguments.putBoolean("include_ask_every_time", z);
        simListDialogFragment.setArguments(initArguments);
        return simListDialogFragment;
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        this.mSubscriptions = new ArrayList();
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getTitleResId());
        this.mAdapter = new SelectSubscriptionAdapter(builder.getContext(), this.mSubscriptions);
        setAdapter(builder);
        AlertDialog create = builder.create();
        updateDialog();
        return create;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        if (i >= 0 && i < this.mSubscriptions.size()) {
            int i2 = -1;
            SubscriptionInfo subscriptionInfo = this.mSubscriptions.get(i);
            if (subscriptionInfo != null) {
                i2 = subscriptionInfo.getSubscriptionId();
            }
            ((SimDialogActivity) getActivity()).onSubscriptionSelected(getDialogType(), i2);
        }
    }

    /* access modifiers changed from: protected */
    public List<SubscriptionInfo> getCurrentSubscriptions() {
        return ((SubscriptionManager) getContext().getSystemService(SubscriptionManager.class)).getActiveSubscriptionInfoList();
    }

    @Override // com.android.settings.sim.SimDialogFragment
    public void updateDialog() {
        List<SubscriptionInfo> currentSubscriptions = getCurrentSubscriptions();
        if (currentSubscriptions == null) {
            dismiss();
            return;
        }
        if (getArguments().getBoolean("include_ask_every_time")) {
            ArrayList arrayList = new ArrayList(currentSubscriptions.size() + 1);
            arrayList.add(null);
            arrayList.addAll(currentSubscriptions);
            currentSubscriptions = arrayList;
        }
        if (!currentSubscriptions.equals(this.mSubscriptions)) {
            this.mSubscriptions.clear();
            this.mSubscriptions.addAll(currentSubscriptions);
            this.mAdapter.notifyDataSetChanged();
        }
    }

    /* access modifiers changed from: package-private */
    public void setAdapter(AlertDialog.Builder builder) {
        builder.setAdapter(this.mAdapter, this);
    }

    /* access modifiers changed from: private */
    public static class SelectSubscriptionAdapter extends BaseAdapter {
        private Context mContext;
        private LayoutInflater mInflater;
        List<SubscriptionInfo> mSubscriptions;

        public SelectSubscriptionAdapter(Context context, List<SubscriptionInfo> list) {
            this.mSubscriptions = list;
            this.mContext = context;
        }

        public int getCount() {
            return this.mSubscriptions.size();
        }

        public SubscriptionInfo getItem(int i) {
            return this.mSubscriptions.get(i);
        }

        public long getItemId(int i) {
            SubscriptionInfo subscriptionInfo = this.mSubscriptions.get(i);
            if (subscriptionInfo == null) {
                return -1;
            }
            return (long) subscriptionInfo.getSubscriptionId();
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                if (this.mInflater == null) {
                    this.mInflater = LayoutInflater.from(viewGroup.getContext());
                }
                view = this.mInflater.inflate(C0012R$layout.select_account_list_item, viewGroup, false);
            }
            SubscriptionInfo item = getItem(i);
            TextView textView = (TextView) view.findViewById(C0010R$id.title);
            TextView textView2 = (TextView) view.findViewById(C0010R$id.summary);
            ImageView imageView = (ImageView) view.findViewById(C0010R$id.icon);
            String str = "";
            if (item == null) {
                textView.setText(C0017R$string.sim_calls_ask_first_prefs_title);
                textView2.setText(str);
                imageView.setImageDrawable(this.mContext.getDrawable(C0008R$drawable.ic_feedback_24dp));
                imageView.setImageTintList(Utils.getColorAttr(this.mContext, 16842808));
            } else {
                textView.setText(item.getDisplayName());
                if (isMdnProvisioned(item.getNumber())) {
                    str = item.getNumber();
                }
                textView2.setText(str);
                imageView.setImageBitmap(item.createIconBitmap(this.mContext));
            }
            return view;
        }

        private boolean isMdnProvisioned(String str) {
            return !TextUtils.isEmpty(str) && !str.matches("[\\D0]+");
        }
    }
}
