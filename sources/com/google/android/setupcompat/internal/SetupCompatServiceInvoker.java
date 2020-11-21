package com.google.android.setupcompat.internal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.setupcompat.ISetupCompatService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class SetupCompatServiceInvoker {
    private static final long MAX_WAIT_TIME_FOR_CONNECTION_MS = TimeUnit.SECONDS.toMillis(10);
    @SuppressLint({"StaticFieldLeak"})
    private static SetupCompatServiceInvoker instance;
    private final Context context;
    private final ExecutorService loggingExecutor = ExecutorProvider.setupCompatServiceInvoker.get();
    private final ExecutorService setupCompatExecutor = ExecutorProvider.setupCompatExecutor.get();
    private final long waitTimeInMillisForServiceConnection = MAX_WAIT_TIME_FOR_CONNECTION_MS;

    public void logMetricEvent(int i, Bundle bundle) {
        try {
            this.loggingExecutor.execute(new Runnable(i, bundle) {
                /* class com.google.android.setupcompat.internal.$$Lambda$SetupCompatServiceInvoker$GFPjyrrRlOrpKTzzcCmBJ0LIo */
                public final /* synthetic */ int f$1;
                public final /* synthetic */ Bundle f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    SetupCompatServiceInvoker.this.lambda$logMetricEvent$0$SetupCompatServiceInvoker(this.f$1, this.f$2);
                }
            });
        } catch (RejectedExecutionException e) {
            Log.e("SucServiceInvoker", String.format("Metric of type %d dropped since queue is full.", Integer.valueOf(i)), e);
        }
    }

    public void bindBack(String str, Bundle bundle) {
        try {
            this.setupCompatExecutor.execute(new Runnable(str, bundle) {
                /* class com.google.android.setupcompat.internal.$$Lambda$SetupCompatServiceInvoker$VdTwkewDljNbUIk0fy0aGXN1QHk */
                public final /* synthetic */ String f$1;
                public final /* synthetic */ Bundle f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    SetupCompatServiceInvoker.this.lambda$bindBack$1$SetupCompatServiceInvoker(this.f$1, this.f$2);
                }
            });
        } catch (RejectedExecutionException e) {
            Log.e("SucServiceInvoker", String.format("Screen %s bind back fail.", str), e);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: invokeLogMetric */
    public void lambda$logMetricEvent$0(int i, Bundle bundle) {
        try {
            ISetupCompatService iSetupCompatService = SetupCompatServiceProvider.get(this.context, this.waitTimeInMillisForServiceConnection, TimeUnit.MILLISECONDS);
            if (iSetupCompatService != null) {
                iSetupCompatService.logMetric(i, bundle, Bundle.EMPTY);
            } else {
                Log.w("SucServiceInvoker", "logMetric failed since service reference is null. Are the permissions valid?");
            }
        } catch (RemoteException | InterruptedException | TimeoutException e) {
            Log.e("SucServiceInvoker", String.format("Exception occurred while trying to log metric = [%s]", bundle), e);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: invokeBindBack */
    public void lambda$bindBack$1(String str, Bundle bundle) {
        try {
            ISetupCompatService iSetupCompatService = SetupCompatServiceProvider.get(this.context, this.waitTimeInMillisForServiceConnection, TimeUnit.MILLISECONDS);
            if (iSetupCompatService != null) {
                iSetupCompatService.validateActivity(str, bundle);
            } else {
                Log.w("SucServiceInvoker", "BindBack failed since service reference is null. Are the permissions valid?");
            }
        } catch (RemoteException | InterruptedException | TimeoutException e) {
            Log.e("SucServiceInvoker", String.format("Exception occurred while %s trying bind back to SetupWizard.", str), e);
        }
    }

    private SetupCompatServiceInvoker(Context context2) {
        this.context = context2;
    }

    public static synchronized SetupCompatServiceInvoker get(Context context2) {
        SetupCompatServiceInvoker setupCompatServiceInvoker;
        synchronized (SetupCompatServiceInvoker.class) {
            if (instance == null) {
                instance = new SetupCompatServiceInvoker(context2.getApplicationContext());
            }
            setupCompatServiceInvoker = instance;
        }
        return setupCompatServiceInvoker;
    }

    static void setInstanceForTesting(SetupCompatServiceInvoker setupCompatServiceInvoker) {
        instance = setupCompatServiceInvoker;
    }
}
