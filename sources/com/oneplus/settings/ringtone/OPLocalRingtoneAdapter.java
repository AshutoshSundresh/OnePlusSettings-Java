package com.oneplus.settings.ringtone;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import java.util.ArrayList;
import java.util.List;

public class OPLocalRingtoneAdapter extends BaseAdapter {
    private Context mContext;
    private List mData = new ArrayList();

    public long getItemId(int i) {
        return (long) i;
    }

    public OPLocalRingtoneAdapter(Context context, List list) {
        this.mContext = context;
        this.mData = list;
    }

    public int getCount() {
        return this.mData.size();
    }

    public Object getItem(int i) {
        return this.mData.get(i);
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        View view2;
        HoldView holdView;
        RingtoneData ringtoneData;
        if (view == null) {
            holdView = new HoldView();
            view2 = LayoutInflater.from(this.mContext).inflate(C0012R$layout.op_locatringtone_item, (ViewGroup) null);
            holdView.mTitle = (TextView) view2.findViewById(16908310);
            holdView.button = (RadioButton) view2.findViewById(C0010R$id.id_button);
            view2.setTag(holdView);
        } else {
            view2 = view;
            holdView = (HoldView) view.getTag();
        }
        List list = this.mData;
        if (!(list == null || (ringtoneData = (RingtoneData) list.get(i)) == null)) {
            holdView.mTitle.setText(ringtoneData.title);
            holdView.button.setChecked(ringtoneData.isCheck);
        }
        return view2;
    }

    static class HoldView {
        RadioButton button;
        TextView mTitle;

        HoldView() {
        }
    }

    public static class RingtoneData {
        public String filepath;
        public boolean isCheck;
        public Uri mUri;
        public String mimetype;
        public String title;

        public RingtoneData(Uri uri, String str, boolean z) {
            this.mUri = uri;
            this.title = str;
            this.isCheck = z;
        }
    }
}
