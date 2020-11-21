package com.android.settings.homepage.contextualcards;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.loader.app.LoaderManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.core.InstrumentedFragment;
import com.android.settings.homepage.contextualcards.FocusRecyclerView;
import com.android.settings.homepage.contextualcards.slices.BluetoothUpdateWorker;
import com.android.settings.homepage.contextualcards.slices.SwipeDismissalDelegate;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.wifi.slice.ContextualWifiScanWorker;

public class ContextualCardsFragment extends InstrumentedFragment implements FocusRecyclerView.FocusListener {
    private static final boolean DEBUG = Build.IS_DEBUGGABLE;
    static boolean sRestartLoaderNeeded;
    private FocusRecyclerView mCardsContainer;
    private ContextualCardManager mContextualCardManager;
    private ContextualCardsAdapter mContextualCardsAdapter;
    private ItemTouchHelper mItemTouchHelper;
    BroadcastReceiver mKeyEventReceiver;
    private GridLayoutManager mLayoutManager;
    BroadcastReceiver mScreenOffReceiver;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1502;
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Context context = getContext();
        if (bundle == null) {
            FeatureFactory.getFactory(context).getSlicesFeatureProvider().newUiSession();
            BluetoothUpdateWorker.initLocalBtManager(getContext());
        }
        this.mContextualCardManager = new ContextualCardManager(context, getSettingsLifecycle(), bundle);
        this.mKeyEventReceiver = new KeyEventReceiver();
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        registerScreenOffReceiver();
        registerKeyEventReceiver();
        ContextualWifiScanWorker.newVisibleUiSession();
        this.mContextualCardManager.loadContextualCards(LoaderManager.getInstance(this), sRestartLoaderNeeded);
        sRestartLoaderNeeded = false;
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
    public void onStop() {
        unregisterKeyEventReceiver();
        super.onStop();
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        unregisterScreenOffReceiver();
        super.onDestroy();
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        Context context = getContext();
        View inflate = layoutInflater.inflate(C0012R$layout.settings_homepage, viewGroup, false);
        this.mCardsContainer = (FocusRecyclerView) inflate.findViewById(C0010R$id.card_container);
        GridLayoutManager gridLayoutManager = new GridLayoutManager((Context) getActivity(), 2, 1, false);
        this.mLayoutManager = gridLayoutManager;
        this.mCardsContainer.setLayoutManager(gridLayoutManager);
        this.mContextualCardsAdapter = new ContextualCardsAdapter(context, this, this.mContextualCardManager);
        this.mCardsContainer.setItemAnimator(null);
        this.mCardsContainer.setAdapter(this.mContextualCardsAdapter);
        this.mContextualCardManager.setListener(this.mContextualCardsAdapter);
        this.mCardsContainer.setListener(this);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeDismissalDelegate(this.mContextualCardsAdapter));
        this.mItemTouchHelper = itemTouchHelper;
        itemTouchHelper.attachToRecyclerView(this.mCardsContainer);
        return inflate;
    }

    @Override // com.android.settings.homepage.contextualcards.FocusRecyclerView.FocusListener
    public void onWindowFocusChanged(boolean z) {
        this.mContextualCardManager.onWindowFocusChanged(z);
    }

    private void registerKeyEventReceiver() {
        getActivity().registerReceiver(this.mKeyEventReceiver, new IntentFilter("android.intent.action.CLOSE_SYSTEM_DIALOGS"));
    }

    private void unregisterKeyEventReceiver() {
        getActivity().unregisterReceiver(this.mKeyEventReceiver);
    }

    private void registerScreenOffReceiver() {
        if (this.mScreenOffReceiver == null) {
            this.mScreenOffReceiver = new ScreenOffReceiver();
            getActivity().registerReceiver(this.mScreenOffReceiver, new IntentFilter("android.intent.action.SCREEN_OFF"));
        }
    }

    private void unregisterScreenOffReceiver() {
        if (this.mScreenOffReceiver != null) {
            getActivity().unregisterReceiver(this.mScreenOffReceiver);
            this.mScreenOffReceiver = null;
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void resetSession(Context context) {
        sRestartLoaderNeeded = true;
        unregisterScreenOffReceiver();
        FeatureFactory.getFactory(context).getSlicesFeatureProvider().newUiSession();
    }

    class KeyEventReceiver extends BroadcastReceiver {
        KeyEventReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            if (intent != null && "android.intent.action.CLOSE_SYSTEM_DIALOGS".equals(intent.getAction())) {
                String stringExtra = intent.getStringExtra("reason");
                if ("recentapps".equals(stringExtra) || "homekey".equals(stringExtra)) {
                    if (ContextualCardsFragment.DEBUG) {
                        Log.d("ContextualCardsFragment", "key pressed = " + stringExtra);
                    }
                    ContextualCardsFragment.this.resetSession(context);
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public class ScreenOffReceiver extends BroadcastReceiver {
        ScreenOffReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            if (intent != null && "android.intent.action.SCREEN_OFF".equals(intent.getAction())) {
                if (ContextualCardsFragment.DEBUG) {
                    Log.d("ContextualCardsFragment", "screen off");
                }
                ContextualCardsFragment.this.resetSession(context);
            }
        }
    }
}
