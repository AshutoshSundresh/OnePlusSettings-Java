package com.google.android.setupcompat.internal;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import com.google.android.setupcompat.ISetupCompatService;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

public class SetupCompatServiceProvider {
    static final Intent COMPAT_SERVICE_INTENT = new Intent().setPackage("com.google.android.setupwizard").setAction("com.google.android.setupcompat.SetupCompatService.BIND");
    static boolean disableLooperCheckForTesting = false;
    @SuppressLint({"StaticFieldLeak"})
    private static volatile SetupCompatServiceProvider instance;
    private final AtomicReference<CountDownLatch> connectedConditionRef = new AtomicReference<>();
    private final Context context;
    final ServiceConnection serviceConnection = new ServiceConnection() {
        /* class com.google.android.setupcompat.internal.SetupCompatServiceProvider.AnonymousClass1 */

        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            State state = State.CONNECTED;
            if (iBinder == null) {
                state = State.DISCONNECTED;
                Log.w("SucServiceProvider", "Binder is null when onServiceConnected was called!");
            }
            SetupCompatServiceProvider.this.swapServiceContextAndNotify(new ServiceContext(state, ISetupCompatService.Stub.asInterface(iBinder)));
        }

        public void onServiceDisconnected(ComponentName componentName) {
            SetupCompatServiceProvider.this.swapServiceContextAndNotify(new ServiceContext(State.DISCONNECTED));
        }

        public void onBindingDied(ComponentName componentName) {
            SetupCompatServiceProvider.this.swapServiceContextAndNotify(new ServiceContext(State.REBIND_REQUIRED));
        }

        public void onNullBinding(ComponentName componentName) {
            SetupCompatServiceProvider.this.swapServiceContextAndNotify(new ServiceContext(State.SERVICE_NOT_USABLE));
        }
    };
    private volatile ServiceContext serviceContext = new ServiceContext(State.NOT_STARTED);

    /* access modifiers changed from: package-private */
    public enum State {
        NOT_STARTED,
        BIND_FAILED,
        BINDING,
        CONNECTED,
        DISCONNECTED,
        SERVICE_NOT_USABLE,
        REBIND_REQUIRED
    }

    public static ISetupCompatService get(Context context2, long j, TimeUnit timeUnit) throws TimeoutException, InterruptedException {
        return getInstance(context2).getService(j, timeUnit);
    }

    public ISetupCompatService getService(long j, TimeUnit timeUnit) throws TimeoutException, InterruptedException {
        Preconditions.checkState(disableLooperCheckForTesting || Looper.getMainLooper() != Looper.myLooper(), "getService blocks and should not be called from the main thread.");
        ServiceContext currentServiceState = getCurrentServiceState();
        switch (AnonymousClass2.$SwitchMap$com$google$android$setupcompat$internal$SetupCompatServiceProvider$State[currentServiceState.state.ordinal()]) {
            case 1:
                return currentServiceState.compatService;
            case 2:
            case 3:
                return null;
            case 4:
            case 5:
                return waitForConnection(j, timeUnit);
            case 6:
                requestServiceBind();
                return waitForConnection(j, timeUnit);
            case 7:
                throw new IllegalStateException("NOT_STARTED state only possible before instance is created.");
            default:
                throw new IllegalStateException("Unknown state = " + currentServiceState.state);
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: com.google.android.setupcompat.internal.SetupCompatServiceProvider$2  reason: invalid class name */
    public static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$com$google$android$setupcompat$internal$SetupCompatServiceProvider$State;

        /* JADX WARNING: Can't wrap try/catch for region: R(14:0|1|2|3|4|5|6|7|8|9|10|11|12|(3:13|14|16)) */
        /* JADX WARNING: Can't wrap try/catch for region: R(16:0|1|2|3|4|5|6|7|8|9|10|11|12|13|14|16) */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:11:0x003e */
        /* JADX WARNING: Missing exception handler attribute for start block: B:13:0x0049 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0012 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x001d */
        /* JADX WARNING: Missing exception handler attribute for start block: B:7:0x0028 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:9:0x0033 */
        static {
            /*
                com.google.android.setupcompat.internal.SetupCompatServiceProvider$State[] r0 = com.google.android.setupcompat.internal.SetupCompatServiceProvider.State.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                com.google.android.setupcompat.internal.SetupCompatServiceProvider.AnonymousClass2.$SwitchMap$com$google$android$setupcompat$internal$SetupCompatServiceProvider$State = r0
                com.google.android.setupcompat.internal.SetupCompatServiceProvider$State r1 = com.google.android.setupcompat.internal.SetupCompatServiceProvider.State.CONNECTED     // Catch:{ NoSuchFieldError -> 0x0012 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0012 }
                r2 = 1
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0012 }
            L_0x0012:
                int[] r0 = com.google.android.setupcompat.internal.SetupCompatServiceProvider.AnonymousClass2.$SwitchMap$com$google$android$setupcompat$internal$SetupCompatServiceProvider$State     // Catch:{ NoSuchFieldError -> 0x001d }
                com.google.android.setupcompat.internal.SetupCompatServiceProvider$State r1 = com.google.android.setupcompat.internal.SetupCompatServiceProvider.State.SERVICE_NOT_USABLE     // Catch:{ NoSuchFieldError -> 0x001d }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001d }
                r2 = 2
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001d }
            L_0x001d:
                int[] r0 = com.google.android.setupcompat.internal.SetupCompatServiceProvider.AnonymousClass2.$SwitchMap$com$google$android$setupcompat$internal$SetupCompatServiceProvider$State     // Catch:{ NoSuchFieldError -> 0x0028 }
                com.google.android.setupcompat.internal.SetupCompatServiceProvider$State r1 = com.google.android.setupcompat.internal.SetupCompatServiceProvider.State.BIND_FAILED     // Catch:{ NoSuchFieldError -> 0x0028 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0028 }
                r2 = 3
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0028 }
            L_0x0028:
                int[] r0 = com.google.android.setupcompat.internal.SetupCompatServiceProvider.AnonymousClass2.$SwitchMap$com$google$android$setupcompat$internal$SetupCompatServiceProvider$State     // Catch:{ NoSuchFieldError -> 0x0033 }
                com.google.android.setupcompat.internal.SetupCompatServiceProvider$State r1 = com.google.android.setupcompat.internal.SetupCompatServiceProvider.State.DISCONNECTED     // Catch:{ NoSuchFieldError -> 0x0033 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0033 }
                r2 = 4
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0033 }
            L_0x0033:
                int[] r0 = com.google.android.setupcompat.internal.SetupCompatServiceProvider.AnonymousClass2.$SwitchMap$com$google$android$setupcompat$internal$SetupCompatServiceProvider$State     // Catch:{ NoSuchFieldError -> 0x003e }
                com.google.android.setupcompat.internal.SetupCompatServiceProvider$State r1 = com.google.android.setupcompat.internal.SetupCompatServiceProvider.State.BINDING     // Catch:{ NoSuchFieldError -> 0x003e }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x003e }
                r2 = 5
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x003e }
            L_0x003e:
                int[] r0 = com.google.android.setupcompat.internal.SetupCompatServiceProvider.AnonymousClass2.$SwitchMap$com$google$android$setupcompat$internal$SetupCompatServiceProvider$State     // Catch:{ NoSuchFieldError -> 0x0049 }
                com.google.android.setupcompat.internal.SetupCompatServiceProvider$State r1 = com.google.android.setupcompat.internal.SetupCompatServiceProvider.State.REBIND_REQUIRED     // Catch:{ NoSuchFieldError -> 0x0049 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0049 }
                r2 = 6
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0049 }
            L_0x0049:
                int[] r0 = com.google.android.setupcompat.internal.SetupCompatServiceProvider.AnonymousClass2.$SwitchMap$com$google$android$setupcompat$internal$SetupCompatServiceProvider$State     // Catch:{ NoSuchFieldError -> 0x0054 }
                com.google.android.setupcompat.internal.SetupCompatServiceProvider$State r1 = com.google.android.setupcompat.internal.SetupCompatServiceProvider.State.NOT_STARTED     // Catch:{ NoSuchFieldError -> 0x0054 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0054 }
                r2 = 7
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0054 }
            L_0x0054:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.android.setupcompat.internal.SetupCompatServiceProvider.AnonymousClass2.<clinit>():void");
        }
    }

    private ISetupCompatService waitForConnection(long j, TimeUnit timeUnit) throws TimeoutException, InterruptedException {
        ServiceContext currentServiceState = getCurrentServiceState();
        if (currentServiceState.state == State.CONNECTED) {
            return currentServiceState.compatService;
        }
        CountDownLatch connectedCondition = getConnectedCondition();
        Log.i("SucServiceProvider", "Waiting for service to get connected");
        if (connectedCondition.await(j, timeUnit)) {
            ServiceContext currentServiceState2 = getCurrentServiceState();
            if (Log.isLoggable("SucServiceProvider", 4)) {
                Log.i("SucServiceProvider", String.format("Finished waiting for service to get connected. Current state = %s", currentServiceState2.state));
            }
            return currentServiceState2.compatService;
        }
        requestServiceBind();
        throw new TimeoutException(String.format("Failed to acquire connection after [%s %s]", Long.valueOf(j), timeUnit));
    }

    /* access modifiers changed from: protected */
    public CountDownLatch createCountDownLatch() {
        return new CountDownLatch(1);
    }

    private synchronized void requestServiceBind() {
        boolean z;
        ServiceContext currentServiceState = getCurrentServiceState();
        if (currentServiceState.state == State.CONNECTED) {
            Log.i("SucServiceProvider", "Refusing to rebind since current state is already connected");
            return;
        }
        if (currentServiceState.state != State.NOT_STARTED) {
            Log.i("SucServiceProvider", "Unbinding existing service connection.");
            this.context.unbindService(this.serviceConnection);
        }
        try {
            z = this.context.bindService(COMPAT_SERVICE_INTENT, this.serviceConnection, 1);
        } catch (SecurityException e) {
            Log.e("SucServiceProvider", "Unable to bind to compat service", e);
            z = false;
        }
        if (!z) {
            swapServiceContextAndNotify(new ServiceContext(State.BIND_FAILED));
            Log.e("SucServiceProvider", "Context#bindService did not succeed.");
        } else if (getCurrentState() != State.CONNECTED) {
            swapServiceContextAndNotify(new ServiceContext(State.BINDING));
            Log.i("SucServiceProvider", "Context#bindService went through, now waiting for service connection");
        }
    }

    /* access modifiers changed from: package-private */
    public State getCurrentState() {
        return this.serviceContext.state;
    }

    private synchronized ServiceContext getCurrentServiceState() {
        return this.serviceContext;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void swapServiceContextAndNotify(ServiceContext serviceContext2) {
        if (Log.isLoggable("SucServiceProvider", 4)) {
            Log.i("SucServiceProvider", String.format("State changed: %s -> %s", this.serviceContext.state, serviceContext2.state));
        }
        this.serviceContext = serviceContext2;
        CountDownLatch andClearConnectedCondition = getAndClearConnectedCondition();
        if (andClearConnectedCondition != null) {
            andClearConnectedCondition.countDown();
        }
    }

    private CountDownLatch getAndClearConnectedCondition() {
        return this.connectedConditionRef.getAndSet(null);
    }

    private CountDownLatch getConnectedCondition() {
        CountDownLatch createCountDownLatch;
        do {
            CountDownLatch countDownLatch = this.connectedConditionRef.get();
            if (countDownLatch != null) {
                return countDownLatch;
            }
            createCountDownLatch = createCountDownLatch();
        } while (!this.connectedConditionRef.compareAndSet(null, createCountDownLatch));
        return createCountDownLatch;
    }

    SetupCompatServiceProvider(Context context2) {
        this.context = context2.getApplicationContext();
    }

    /* access modifiers changed from: private */
    public static final class ServiceContext {
        final ISetupCompatService compatService;
        final State state;

        private ServiceContext(State state2, ISetupCompatService iSetupCompatService) {
            this.state = state2;
            this.compatService = iSetupCompatService;
            if (state2 == State.CONNECTED) {
                Preconditions.checkNotNull(iSetupCompatService, "CompatService cannot be null when state is connected");
            }
        }

        private ServiceContext(State state2) {
            this(state2, (ISetupCompatService) null);
        }
    }

    static SetupCompatServiceProvider getInstance(Context context2) {
        Preconditions.checkNotNull(context2, "Context object cannot be null.");
        SetupCompatServiceProvider setupCompatServiceProvider = instance;
        if (setupCompatServiceProvider == null) {
            synchronized (SetupCompatServiceProvider.class) {
                setupCompatServiceProvider = instance;
                if (setupCompatServiceProvider == null) {
                    setupCompatServiceProvider = new SetupCompatServiceProvider(context2.getApplicationContext());
                    instance = setupCompatServiceProvider;
                    instance.requestServiceBind();
                }
            }
        }
        return setupCompatServiceProvider;
    }

    public static void setInstanceForTesting(SetupCompatServiceProvider setupCompatServiceProvider) {
        instance = setupCompatServiceProvider;
    }
}
