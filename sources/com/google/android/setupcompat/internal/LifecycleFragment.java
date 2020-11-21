package com.google.android.setupcompat.internal;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Build;
import android.os.PersistableBundle;
import android.util.Log;
import com.google.android.setupcompat.logging.CustomEvent;
import com.google.android.setupcompat.logging.MetricKey;
import com.google.android.setupcompat.logging.SetupMetricsLogger;
import com.google.android.setupcompat.util.WizardManagerHelper;
import java.util.concurrent.TimeUnit;

public class LifecycleFragment extends Fragment {
    private static final String LOG_TAG = LifecycleFragment.class.getSimpleName();
    private long durationInNanos = 0;
    private MetricKey metricKey;
    private long startInNanos;

    public LifecycleFragment() {
        setRetainInstance(true);
    }

    public static LifecycleFragment attachNow(Activity activity) {
        FragmentManager fragmentManager;
        String str = LOG_TAG;
        if (WizardManagerHelper.isAnySetupWizard(activity.getIntent())) {
            SetupCompatServiceInvoker.get(activity.getApplicationContext()).bindBack(LayoutBindBackHelper.getScreenName(activity), LayoutBindBackHelper.getExtraBundle(activity));
            if (Build.VERSION.SDK_INT > 23 && (fragmentManager = activity.getFragmentManager()) != null && !fragmentManager.isDestroyed()) {
                Fragment findFragmentByTag = fragmentManager.findFragmentByTag("lifecycle_monitor");
                if (findFragmentByTag == null) {
                    LifecycleFragment lifecycleFragment = new LifecycleFragment();
                    try {
                        fragmentManager.beginTransaction().add(lifecycleFragment, "lifecycle_monitor").commitNow();
                        findFragmentByTag = lifecycleFragment;
                    } catch (IllegalStateException e) {
                        Log.e(str, "Error occurred when attach to Activity:" + activity.getComponentName(), e);
                    }
                } else if (!(findFragmentByTag instanceof LifecycleFragment)) {
                    Log.wtf(str, activity.getClass().getSimpleName() + " Incorrect instance on lifecycle fragment.");
                    return null;
                }
                return (LifecycleFragment) findFragmentByTag;
            }
        }
        return null;
    }

    @Override // android.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        this.metricKey = MetricKey.get("ScreenDuration", getActivity());
    }

    public void onDetach() {
        super.onDetach();
        SetupMetricsLogger.logDuration(getActivity(), this.metricKey, TimeUnit.NANOSECONDS.toMillis(this.durationInNanos));
    }

    public void onResume() {
        super.onResume();
        this.startInNanos = ClockProvider.timeInNanos();
        logScreenResume();
    }

    public void onPause() {
        super.onPause();
        this.durationInNanos += ClockProvider.timeInNanos() - this.startInNanos;
    }

    private void logScreenResume() {
        if (Build.VERSION.SDK_INT >= 29) {
            PersistableBundle persistableBundle = new PersistableBundle();
            persistableBundle.putLong("onScreenResume", System.nanoTime());
            SetupMetricsLogger.logCustomEvent(getActivity(), CustomEvent.create(MetricKey.get("ScreenActivity", getActivity()), persistableBundle));
        }
    }
}
