package com.android.settings.bluetooth;

import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import androidx.core.graphics.drawable.IconCompat;
import androidx.slice.Slice;
import androidx.slice.builders.ListBuilder;
import androidx.slice.builders.SliceAction;
import com.android.settings.C0017R$string;
import com.android.settings.SubSettings;
import com.android.settings.connecteddevice.BluetoothDashboardFragment;
import com.android.settings.slices.CustomSliceRegistry;
import com.android.settings.slices.SliceBroadcastReceiver;
import com.android.settings.slices.SliceBuilderUtils;
import com.android.settingslib.Utils;

public class BluetoothSliceBuilder {
    public static final IntentFilter INTENT_FILTER;

    static {
        IntentFilter intentFilter = new IntentFilter();
        INTENT_FILTER = intentFilter;
        intentFilter.addAction("android.bluetooth.adapter.action.CONNECTION_STATE_CHANGED");
        INTENT_FILTER.addAction("android.bluetooth.adapter.action.STATE_CHANGED");
    }

    public static Slice getSlice(Context context) {
        boolean isBluetoothEnabled = isBluetoothEnabled();
        CharSequence text = context.getText(C0017R$string.bluetooth_settings);
        IconCompat createWithResource = IconCompat.createWithResource(context, 17302820);
        int defaultColor = Utils.getColorAccent(context).getDefaultColor();
        PendingIntent broadcastIntent = getBroadcastIntent(context);
        SliceAction createDeeplink = SliceAction.createDeeplink(getPrimaryAction(context), createWithResource, 0, text);
        SliceAction createToggle = SliceAction.createToggle(broadcastIntent, null, isBluetoothEnabled);
        ListBuilder listBuilder = new ListBuilder(context, CustomSliceRegistry.BLUETOOTH_URI, -1);
        listBuilder.setAccentColor(defaultColor);
        ListBuilder.RowBuilder rowBuilder = new ListBuilder.RowBuilder();
        rowBuilder.setTitle(text);
        rowBuilder.addEndItem(createToggle);
        rowBuilder.setPrimaryAction(createDeeplink);
        listBuilder.addRow(rowBuilder);
        return listBuilder.build();
    }

    public static Intent getIntent(Context context) {
        String charSequence = context.getText(C0017R$string.bluetooth_settings_title).toString();
        return SliceBuilderUtils.buildSearchResultPageIntent(context, BluetoothDashboardFragment.class.getName(), null, charSequence, 747).setClassName(context.getPackageName(), SubSettings.class.getName()).setData(new Uri.Builder().appendPath("bluetooth").build());
    }

    public static void handleUriChange(Context context, Intent intent) {
        boolean booleanExtra = intent.getBooleanExtra("android.app.slice.extra.TOGGLE_STATE", false);
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (booleanExtra) {
            defaultAdapter.enable();
        } else {
            defaultAdapter.disable();
        }
    }

    private static boolean isBluetoothEnabled() {
        return BluetoothAdapter.getDefaultAdapter().isEnabled();
    }

    private static PendingIntent getPrimaryAction(Context context) {
        return PendingIntent.getActivity(context, 0, getIntent(context), 0);
    }

    private static PendingIntent getBroadcastIntent(Context context) {
        return PendingIntent.getBroadcast(context, 0, new Intent("com.android.settings.bluetooth.action.BLUETOOTH_MODE_CHANGED").setClass(context, SliceBroadcastReceiver.class), 134217728);
    }
}
