package androidx.remotecallback;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class ProviderRelayReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        if ("androidx.remotecallback.action.PROVIDER_RELAY".equals(intent.getAction())) {
            context.getContentResolver().call(new Uri.Builder().scheme("content").authority(intent.getStringExtra("androidx.remotecallback.extra.AUTHORITY")).build(), "androidx.remotecallback.method.PROVIDER_CALLBACK", (String) null, intent.getExtras());
        }
    }
}
