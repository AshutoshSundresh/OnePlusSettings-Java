package com.android.settings.network.telephony;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.ArraySet;
import com.google.common.collect.Sets;
import com.google.common.collect.UnmodifiableIterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class SignalStrengthListener {
    private TelephonyManager mBaseTelephonyManager;
    private Callback mCallback;
    private Map<Integer, PhoneStateListener> mListeners = new TreeMap();

    public interface Callback {
        void onSignalStrengthChanged();
    }

    public SignalStrengthListener(Context context, Callback callback) {
        this.mBaseTelephonyManager = (TelephonyManager) context.getSystemService(TelephonyManager.class);
        this.mCallback = callback;
    }

    public void resume() {
        for (Integer num : this.mListeners.keySet()) {
            startListening(num.intValue());
        }
    }

    public void pause() {
        for (Integer num : this.mListeners.keySet()) {
            stopListening(num.intValue());
        }
    }

    public void updateSubscriptionIds(Set<Integer> set) {
        ArraySet arraySet = new ArraySet(this.mListeners.keySet());
        UnmodifiableIterator it = Sets.difference(arraySet, set).iterator();
        while (it.hasNext()) {
            int intValue = ((Integer) it.next()).intValue();
            stopListening(intValue);
            this.mListeners.remove(Integer.valueOf(intValue));
        }
        UnmodifiableIterator it2 = Sets.difference(set, arraySet).iterator();
        while (it2.hasNext()) {
            int intValue2 = ((Integer) it2.next()).intValue();
            this.mListeners.put(Integer.valueOf(intValue2), new PhoneStateListener() {
                /* class com.android.settings.network.telephony.SignalStrengthListener.AnonymousClass1 */

                public void onSignalStrengthsChanged(SignalStrength signalStrength) {
                    SignalStrengthListener.this.mCallback.onSignalStrengthChanged();
                }
            });
            startListening(intValue2);
        }
    }

    private void startListening(int i) {
        this.mBaseTelephonyManager.createForSubscriptionId(i).listen(this.mListeners.get(Integer.valueOf(i)), 256);
    }

    private void stopListening(int i) {
        this.mBaseTelephonyManager.createForSubscriptionId(i).listen(this.mListeners.get(Integer.valueOf(i)), 0);
    }
}
