package com.android.settings.homepage.contextualcards;

import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.util.ArrayMap;
import android.util.Log;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import com.android.settings.C0005R$bool;
import com.android.settings.homepage.contextualcards.ContextualCard;
import com.android.settings.homepage.contextualcards.ContextualCardLoader;
import com.android.settings.homepage.contextualcards.conditional.ConditionalCardController;
import com.android.settings.homepage.contextualcards.logging.ContextualCardLogUtils;
import com.android.settings.homepage.contextualcards.slices.SliceContextualCardRenderer;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnSaveInstanceState;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ContextualCardManager implements ContextualCardLoader.CardContentLoaderListener, ContextualCardUpdateListener, LifecycleObserver, OnSaveInstanceState {
    static final long CARD_CONTENT_LOADER_TIMEOUT_MS = 1000;
    static final String KEY_CONTEXTUAL_CARDS = "key_contextual_cards";
    static final String KEY_GLOBAL_CARD_LOADER_TIMEOUT = "global_card_loader_timeout_key";
    private final Context mContext;
    final List<ContextualCard> mContextualCards = new ArrayList();
    final ControllerRendererPool mControllerRendererPool = new ControllerRendererPool();
    boolean mIsFirstLaunch;
    private final Lifecycle mLifecycle;
    private final List<LifecycleObserver> mLifecycleObservers = new ArrayList();
    private ContextualCardUpdateListener mListener;
    List<String> mSavedCards;
    long mStartTime;

    public ContextualCardManager(Context context, Lifecycle lifecycle, Bundle bundle) {
        this.mContext = context;
        this.mLifecycle = lifecycle;
        this.mLifecycle.addObserver(this);
        if (bundle == null) {
            this.mIsFirstLaunch = true;
            this.mSavedCards = null;
        } else {
            this.mSavedCards = bundle.getStringArrayList(KEY_CONTEXTUAL_CARDS);
        }
        for (int i : getSettingsCards()) {
            setupController(i);
        }
    }

    /* access modifiers changed from: package-private */
    public void loadContextualCards(LoaderManager loaderManager, boolean z) {
        if (this.mContext.getResources().getBoolean(C0005R$bool.config_use_legacy_suggestion)) {
            Log.w("ContextualCardManager", "Legacy suggestion contextual card enabled, skipping contextual cards.");
            return;
        }
        this.mStartTime = System.currentTimeMillis();
        CardContentLoaderCallbacks cardContentLoaderCallbacks = new CardContentLoaderCallbacks(this.mContext);
        cardContentLoaderCallbacks.setListener(this);
        if (!z) {
            loaderManager.initLoader(1, null, cardContentLoaderCallbacks);
            return;
        }
        this.mIsFirstLaunch = true;
        loaderManager.restartLoader(1, null, cardContentLoaderCallbacks);
    }

    private void loadCardControllers() {
        for (ContextualCard contextualCard : this.mContextualCards) {
            setupController(contextualCard.getCardType());
        }
    }

    /* access modifiers changed from: package-private */
    public int[] getSettingsCards() {
        return new int[]{3, 2};
    }

    /* access modifiers changed from: package-private */
    public void setupController(int i) {
        ContextualCardController controller = this.mControllerRendererPool.getController(this.mContext, i);
        if (controller == null) {
            Log.w("ContextualCardManager", "Cannot find ContextualCardController for type " + i);
            return;
        }
        controller.setCardUpdateListener(this);
        if ((controller instanceof LifecycleObserver) && !this.mLifecycleObservers.contains(controller)) {
            LifecycleObserver lifecycleObserver = (LifecycleObserver) controller;
            this.mLifecycleObservers.add(lifecycleObserver);
            this.mLifecycle.addObserver(lifecycleObserver);
        }
    }

    /* access modifiers changed from: package-private */
    public List<ContextualCard> sortCards(List<ContextualCard> list) {
        List<ContextualCard> list2 = (List) list.stream().sorted($$Lambda$ContextualCardManager$Gw08Tb6P3Z00HUKmrUC8W3DcoSw.INSTANCE).collect(Collectors.toList());
        List list3 = (List) list2.stream().filter($$Lambda$ContextualCardManager$ZzsLYHxWMenJTAe12ptvN9VFX9A.INSTANCE).collect(Collectors.toList());
        list2.removeAll(list3);
        list2.addAll(list3);
        return list2;
    }

    static /* synthetic */ boolean lambda$sortCards$1(ContextualCard contextualCard) {
        return contextualCard.getCategory() == 6;
    }

    @Override // com.android.settings.homepage.contextualcards.ContextualCardUpdateListener
    public void onContextualCardUpdated(Map<Integer, List<ContextualCard>> map) {
        List list;
        Set<Integer> keySet = map.keySet();
        if (keySet.isEmpty()) {
            list = (List) this.mContextualCards.stream().filter(new Predicate(new TreeSet<Integer>() {
                /* class com.android.settings.homepage.contextualcards.ContextualCardManager.AnonymousClass1 */

                {
                    add(3);
                    add(4);
                    add(5);
                }
            }) {
                /* class com.android.settings.homepage.contextualcards.$$Lambda$ContextualCardManager$6I5X9CXkt_h4chR8W4I7r_0Wm4M */
                public final /* synthetic */ Set f$0;

                {
                    this.f$0 = r1;
                }

                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    return this.f$0.contains(Integer.valueOf(((ContextualCard) obj).getCardType()));
                }
            }).collect(Collectors.toList());
        } else {
            list = (List) this.mContextualCards.stream().filter(new Predicate(keySet) {
                /* class com.android.settings.homepage.contextualcards.$$Lambda$ContextualCardManager$F96aUf62ki0JSkDwa2Dc7ZmrVk */
                public final /* synthetic */ Set f$0;

                {
                    this.f$0 = r1;
                }

                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    return ContextualCardManager.lambda$onContextualCardUpdated$3(this.f$0, (ContextualCard) obj);
                }
            }).collect(Collectors.toList());
        }
        ArrayList arrayList = new ArrayList();
        arrayList.addAll(list);
        arrayList.addAll((Collection) map.values().stream().flatMap($$Lambda$seyL25CSW2NInOydsTbSDrNW6pM.INSTANCE).collect(Collectors.toList()));
        this.mContextualCards.clear();
        this.mContextualCards.addAll(getCardsWithViewType(sortCards(arrayList)));
        loadCardControllers();
        if (this.mListener != null) {
            ArrayMap arrayMap = new ArrayMap();
            arrayMap.put(0, this.mContextualCards);
            this.mListener.onContextualCardUpdated(arrayMap);
        }
    }

    static /* synthetic */ boolean lambda$onContextualCardUpdated$3(Set set, ContextualCard contextualCard) {
        return !set.contains(Integer.valueOf(contextualCard.getCardType()));
    }

    @Override // com.android.settings.homepage.contextualcards.ContextualCardLoader.CardContentLoaderListener
    public void onFinishCardLoading(List<ContextualCard> list) {
        long currentTimeMillis = System.currentTimeMillis() - this.mStartTime;
        Log.d("ContextualCardManager", "Total loading time = " + currentTimeMillis);
        List<ContextualCard> cardsToKeep = getCardsToKeep(list);
        MetricsFeatureProvider metricsFeatureProvider = FeatureFactory.getFactory(this.mContext).getMetricsFeatureProvider();
        if (!this.mIsFirstLaunch) {
            onContextualCardUpdated((Map) cardsToKeep.stream().collect(Collectors.groupingBy($$Lambda$tvgAa5trWmVB27u8qU1xIO56s.INSTANCE)));
            metricsFeatureProvider.action(this.mContext, 1663, ContextualCardLogUtils.buildCardListLog(cardsToKeep));
            return;
        }
        if (currentTimeMillis <= getCardLoaderTimeout()) {
            onContextualCardUpdated((Map) list.stream().collect(Collectors.groupingBy($$Lambda$tvgAa5trWmVB27u8qU1xIO56s.INSTANCE)));
            metricsFeatureProvider.action(this.mContext, 1663, ContextualCardLogUtils.buildCardListLog(list));
        } else {
            metricsFeatureProvider.action(0, 1685, 1502, null, (int) currentTimeMillis);
        }
        metricsFeatureProvider.action(this.mContext, 1662, (int) (System.currentTimeMillis() - this.mStartTime));
        this.mIsFirstLaunch = false;
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnSaveInstanceState
    public void onSaveInstanceState(Bundle bundle) {
        bundle.putStringArrayList(KEY_CONTEXTUAL_CARDS, (ArrayList) this.mContextualCards.stream().map($$Lambda$YA1g3b8EtANWfDvtA9kJ8cpQ18g.INSTANCE).collect(Collectors.toCollection($$Lambda$OGSS2qx6njxlnp0dnKb4lA3jnw8.INSTANCE)));
    }

    public void onWindowFocusChanged(boolean z) {
        boolean z2 = false;
        for (ContextualCard contextualCard : new ArrayList(this.mContextualCards)) {
            ContextualCardController controller = getControllerRendererPool().getController(this.mContext, contextualCard.getCardType());
            if (controller instanceof ConditionalCardController) {
                z2 = true;
            }
            if (z && (controller instanceof OnStart)) {
                ((OnStart) controller).onStart();
            }
            if (!z && (controller instanceof OnStop)) {
                ((OnStop) controller).onStop();
            }
        }
        if (!z2) {
            ContextualCardController controller2 = getControllerRendererPool().getController(this.mContext, 3);
            if (z && (controller2 instanceof OnStart)) {
                ((OnStart) controller2).onStart();
            }
            if (!z && (controller2 instanceof OnStop)) {
                ((OnStop) controller2).onStop();
            }
        }
    }

    public ControllerRendererPool getControllerRendererPool() {
        return this.mControllerRendererPool;
    }

    /* access modifiers changed from: package-private */
    public void setListener(ContextualCardUpdateListener contextualCardUpdateListener) {
        this.mListener = contextualCardUpdateListener;
    }

    /* access modifiers changed from: package-private */
    public List<ContextualCard> getCardsWithViewType(List<ContextualCard> list) {
        if (list.isEmpty()) {
            return list;
        }
        return getCardsWithSuggestionViewType(getCardsWithStickyViewType(list));
    }

    /* access modifiers changed from: package-private */
    public long getCardLoaderTimeout() {
        return Settings.Global.getLong(this.mContext.getContentResolver(), KEY_GLOBAL_CARD_LOADER_TIMEOUT, CARD_CONTENT_LOADER_TIMEOUT_MS);
    }

    private List<ContextualCard> getCardsWithSuggestionViewType(List<ContextualCard> list) {
        ArrayList arrayList = new ArrayList(list);
        int i = 1;
        while (i < arrayList.size()) {
            int i2 = i - 1;
            ContextualCard contextualCard = (ContextualCard) arrayList.get(i2);
            ContextualCard contextualCard2 = (ContextualCard) arrayList.get(i);
            if (contextualCard2.getCategory() == 1 && contextualCard.getCategory() == 1) {
                ContextualCard.Builder mutate = contextualCard.mutate();
                mutate.setViewType(SliceContextualCardRenderer.VIEW_TYPE_HALF_WIDTH);
                arrayList.set(i2, mutate.build());
                ContextualCard.Builder mutate2 = contextualCard2.mutate();
                mutate2.setViewType(SliceContextualCardRenderer.VIEW_TYPE_HALF_WIDTH);
                arrayList.set(i, mutate2.build());
                i++;
            }
            i++;
        }
        return arrayList;
    }

    private List<ContextualCard> getCardsWithStickyViewType(List<ContextualCard> list) {
        ArrayList arrayList = new ArrayList(list);
        for (int i = 0; i < arrayList.size(); i++) {
            ContextualCard contextualCard = list.get(i);
            if (contextualCard.getCategory() == 6) {
                ContextualCard.Builder mutate = contextualCard.mutate();
                mutate.setViewType(SliceContextualCardRenderer.VIEW_TYPE_STICKY);
                arrayList.set(i, mutate.build());
            }
        }
        return arrayList;
    }

    /* access modifiers changed from: package-private */
    public List<ContextualCard> getCardsToKeep(List<ContextualCard> list) {
        if (this.mSavedCards == null) {
            return (List) list.stream().filter(new Predicate() {
                /* class com.android.settings.homepage.contextualcards.$$Lambda$ContextualCardManager$L7DBf888zZk06LeX2e2URIjntj0 */

                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    return ContextualCardManager.this.lambda$getCardsToKeep$5$ContextualCardManager((ContextualCard) obj);
                }
            }).collect(Collectors.toList());
        }
        List<ContextualCard> list2 = (List) list.stream().filter(new Predicate() {
            /* class com.android.settings.homepage.contextualcards.$$Lambda$ContextualCardManager$IHnSQwitjV_m7ge6UmILB0vu3aQ */

            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return ContextualCardManager.this.lambda$getCardsToKeep$4$ContextualCardManager((ContextualCard) obj);
            }
        }).collect(Collectors.toList());
        this.mSavedCards = null;
        return list2;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$getCardsToKeep$4 */
    public /* synthetic */ boolean lambda$getCardsToKeep$4$ContextualCardManager(ContextualCard contextualCard) {
        return this.mSavedCards.contains(contextualCard.getName());
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$getCardsToKeep$5 */
    public /* synthetic */ boolean lambda$getCardsToKeep$5$ContextualCardManager(ContextualCard contextualCard) {
        return this.mContextualCards.contains(contextualCard);
    }

    /* access modifiers changed from: package-private */
    public static class CardContentLoaderCallbacks implements LoaderManager.LoaderCallbacks<List<ContextualCard>> {
        private Context mContext;
        private ContextualCardLoader.CardContentLoaderListener mListener;

        @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
        public void onLoaderReset(Loader<List<ContextualCard>> loader) {
        }

        CardContentLoaderCallbacks(Context context) {
            this.mContext = context.getApplicationContext();
        }

        /* access modifiers changed from: protected */
        public void setListener(ContextualCardLoader.CardContentLoaderListener cardContentLoaderListener) {
            this.mListener = cardContentLoaderListener;
        }

        @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
        public Loader<List<ContextualCard>> onCreateLoader(int i, Bundle bundle) {
            if (i == 1) {
                return new ContextualCardLoader(this.mContext);
            }
            throw new IllegalArgumentException("Unknown loader id: " + i);
        }

        public void onLoadFinished(Loader<List<ContextualCard>> loader, List<ContextualCard> list) {
            ContextualCardLoader.CardContentLoaderListener cardContentLoaderListener = this.mListener;
            if (cardContentLoaderListener != null) {
                cardContentLoaderListener.onFinishCardLoading(list);
            }
        }
    }
}
