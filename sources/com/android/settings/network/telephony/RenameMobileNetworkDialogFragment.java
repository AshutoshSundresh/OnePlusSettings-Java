package com.android.settings.network.telephony;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import com.android.settings.C0003R$array;
import com.android.settings.C0007R$dimen;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.android.settingslib.DeviceInfoUtils;
import java.util.Iterator;
import java.util.List;

public class RenameMobileNetworkDialogFragment extends InstrumentedDialogFragment {
    private Spinner mColorSpinner;
    private Color[] mColors;
    private EditText mNameView;
    private int mSubId;
    private SubscriptionManager mSubscriptionManager;
    private TelephonyManager mTelephonyManager;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1642;
    }

    public static RenameMobileNetworkDialogFragment newInstance(int i) {
        Bundle bundle = new Bundle(1);
        bundle.putInt("subscription_id", i);
        RenameMobileNetworkDialogFragment renameMobileNetworkDialogFragment = new RenameMobileNetworkDialogFragment();
        renameMobileNetworkDialogFragment.setArguments(bundle);
        return renameMobileNetworkDialogFragment;
    }

    /* access modifiers changed from: protected */
    public TelephonyManager getTelephonyManager(Context context) {
        return (TelephonyManager) context.getSystemService(TelephonyManager.class);
    }

    /* access modifiers changed from: protected */
    public SubscriptionManager getSubscriptionManager(Context context) {
        return (SubscriptionManager) context.getSystemService(SubscriptionManager.class);
    }

    /* access modifiers changed from: protected */
    public EditText getNameView() {
        return this.mNameView;
    }

    /* access modifiers changed from: protected */
    public Spinner getColorSpinnerView() {
        return this.mColorSpinner;
    }

    @Override // androidx.fragment.app.Fragment, com.android.settings.core.instrumentation.InstrumentedDialogFragment, com.android.settingslib.core.lifecycle.ObservableDialogFragment, androidx.fragment.app.DialogFragment
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mTelephonyManager = getTelephonyManager(context);
        this.mSubscriptionManager = getSubscriptionManager(context);
        this.mSubId = getArguments().getInt("subscription_id");
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        this.mColors = getColors();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View inflate = ((LayoutInflater) builder.getContext().getSystemService(LayoutInflater.class)).inflate(C0012R$layout.dialog_mobile_network_rename, (ViewGroup) null);
        populateView(inflate);
        builder.setTitle(C0017R$string.mobile_network_sim_name);
        builder.setView(inflate);
        builder.setPositiveButton(C0017R$string.mobile_network_sim_name_rename, new DialogInterface.OnClickListener() {
            /* class com.android.settings.network.telephony.$$Lambda$RenameMobileNetworkDialogFragment$32uZAtr_w5Fn719afgZJG1yBu8g */

            public final void onClick(DialogInterface dialogInterface, int i) {
                RenameMobileNetworkDialogFragment.this.lambda$onCreateDialog$0$RenameMobileNetworkDialogFragment(dialogInterface, i);
            }
        });
        builder.setNegativeButton(17039360, (DialogInterface.OnClickListener) null);
        return builder.create();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onCreateDialog$0 */
    public /* synthetic */ void lambda$onCreateDialog$0$RenameMobileNetworkDialogFragment(DialogInterface dialogInterface, int i) {
        Color color;
        this.mSubscriptionManager.setDisplayName(this.mNameView.getText().toString(), this.mSubId, 2);
        Spinner spinner = this.mColorSpinner;
        if (spinner == null) {
            color = this.mColors[0];
        } else {
            color = this.mColors[spinner.getSelectedItemPosition()];
        }
        this.mSubscriptionManager.setIconTint(color.getColor(), this.mSubId);
    }

    /* access modifiers changed from: protected */
    public void populateView(View view) {
        SubscriptionInfo subscriptionInfo;
        this.mNameView = (EditText) view.findViewById(C0010R$id.name_edittext);
        List availableSubscriptionInfoList = this.mSubscriptionManager.getAvailableSubscriptionInfoList();
        if (availableSubscriptionInfoList != null) {
            Iterator it = availableSubscriptionInfoList.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                subscriptionInfo = (SubscriptionInfo) it.next();
                if (subscriptionInfo.getSubscriptionId() == this.mSubId) {
                    break;
                }
            }
        }
        subscriptionInfo = null;
        if (subscriptionInfo == null) {
            Log.w("RenameMobileNetwork", "got null SubscriptionInfo for mSubId:" + this.mSubId);
            return;
        }
        CharSequence displayName = subscriptionInfo.getDisplayName();
        this.mNameView.setText(displayName);
        if (!TextUtils.isEmpty(displayName)) {
            this.mNameView.setSelection(displayName.length());
        }
        this.mColorSpinner = (Spinner) view.findViewById(C0010R$id.color_spinner);
        this.mColorSpinner.setAdapter((SpinnerAdapter) new ColorAdapter(this, getContext(), C0012R$layout.dialog_mobile_network_color_picker_item, this.mColors));
        int i = 0;
        int i2 = 0;
        while (true) {
            Color[] colorArr = this.mColors;
            if (i2 >= colorArr.length) {
                break;
            } else if (colorArr[i2].getColor() == subscriptionInfo.getIconTint()) {
                this.mColorSpinner.setSelection(i2);
                break;
            } else {
                i2++;
            }
        }
        TelephonyManager createForSubscriptionId = this.mTelephonyManager.createForSubscriptionId(this.mSubId);
        this.mTelephonyManager = createForSubscriptionId;
        ((TextView) view.findViewById(C0010R$id.operator_name_value)).setText(createForSubscriptionId.getServiceState().getOperatorAlphaLong());
        TextView textView = (TextView) view.findViewById(C0010R$id.number_label);
        if (subscriptionInfo.isOpportunistic()) {
            i = 8;
        }
        textView.setVisibility(i);
        ((TextView) view.findViewById(C0010R$id.number_value)).setText(DeviceInfoUtils.getBidiFormattedPhoneNumber(getContext(), subscriptionInfo));
    }

    /* access modifiers changed from: private */
    public class ColorAdapter extends ArrayAdapter<Color> {
        private Context mContext;
        private int mItemResId;

        public ColorAdapter(RenameMobileNetworkDialogFragment renameMobileNetworkDialogFragment, Context context, int i, Color[] colorArr) {
            super(context, i, colorArr);
            this.mContext = context;
            this.mItemResId = i;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            LayoutInflater layoutInflater = (LayoutInflater) this.mContext.getSystemService("layout_inflater");
            if (view == null) {
                view = layoutInflater.inflate(this.mItemResId, (ViewGroup) null);
            }
            ((ImageView) view.findViewById(C0010R$id.color_icon)).setImageDrawable(((Color) getItem(i)).getDrawable());
            ((TextView) view.findViewById(C0010R$id.color_label)).setText(((Color) getItem(i)).getLabel());
            return view;
        }

        public View getDropDownView(int i, View view, ViewGroup viewGroup) {
            return getView(i, view, viewGroup);
        }
    }

    private Color[] getColors() {
        Resources resources = getContext().getResources();
        int[] intArray = resources.getIntArray(17236132);
        String[] stringArray = resources.getStringArray(C0003R$array.color_picker);
        int dimensionPixelSize = resources.getDimensionPixelSize(C0007R$dimen.color_swatch_size);
        int dimensionPixelSize2 = resources.getDimensionPixelSize(C0007R$dimen.color_swatch_stroke_width);
        int length = intArray.length;
        Color[] colorArr = new Color[length];
        for (int i = 0; i < length; i++) {
            colorArr[i] = new Color(stringArray[i], intArray[i], dimensionPixelSize, dimensionPixelSize2);
        }
        return colorArr;
    }

    /* access modifiers changed from: private */
    public static class Color {
        private int mColor;
        private ShapeDrawable mDrawable;
        private String mLabel;

        private Color(String str, int i, int i2, int i3) {
            this.mLabel = str;
            this.mColor = i;
            ShapeDrawable shapeDrawable = new ShapeDrawable(new OvalShape());
            this.mDrawable = shapeDrawable;
            shapeDrawable.setIntrinsicHeight(i2);
            this.mDrawable.setIntrinsicWidth(i2);
            this.mDrawable.getPaint().setStrokeWidth((float) i3);
            this.mDrawable.getPaint().setStyle(Paint.Style.FILL_AND_STROKE);
            this.mDrawable.getPaint().setColor(i);
        }

        /* access modifiers changed from: private */
        /* access modifiers changed from: public */
        private String getLabel() {
            return this.mLabel;
        }

        /* access modifiers changed from: private */
        /* access modifiers changed from: public */
        private int getColor() {
            return this.mColor;
        }

        /* access modifiers changed from: private */
        /* access modifiers changed from: public */
        private ShapeDrawable getDrawable() {
            return this.mDrawable;
        }
    }
}
