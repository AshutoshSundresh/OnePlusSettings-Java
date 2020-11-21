package com.android.settings.panel;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.graphics.drawable.IconCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.slice.Slice;
import androidx.slice.SliceMetadata;
import androidx.slice.widget.SliceLiveData;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settings.C0003R$array;
import com.android.settings.C0007R$dimen;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.panel.PanelFragment;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import com.android.settingslib.utils.ThreadUtils;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PanelFragment extends Fragment {
    private PanelSlicesAdapter mAdapter;
    private TextView mDoneButton;
    private View mFooterDivider;
    private TextView mHeaderSubtitle;
    private TextView mHeaderTitle;
    @VisibleForTesting
    View mLayoutView;
    private int mMaxHeight;
    private MetricsFeatureProvider mMetricsProvider;
    private final ViewTreeObserver.OnGlobalLayoutListener mOnGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        /* class com.android.settings.panel.PanelFragment.AnonymousClass2 */

        public void onGlobalLayout() {
            PanelFragment.this.animateIn();
            if (PanelFragment.this.mPanelSlices != null) {
                PanelFragment.this.mPanelSlices.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        }
    };
    private ViewTreeObserver.OnPreDrawListener mOnPreDrawListener = $$Lambda$PanelFragment$rdpxKzRnUEXEAOP00WJYU0ZKA.INSTANCE;
    private PanelContent mPanel;
    private String mPanelClosedKey;
    private LinearLayout mPanelHeader;
    private final ViewTreeObserver.OnGlobalLayoutListener mPanelLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        /* class com.android.settings.panel.PanelFragment.AnonymousClass1 */

        public void onGlobalLayout() {
            if (PanelFragment.this.mLayoutView.getHeight() > PanelFragment.this.mMaxHeight) {
                ViewGroup.LayoutParams layoutParams = PanelFragment.this.mLayoutView.getLayoutParams();
                layoutParams.height = PanelFragment.this.mMaxHeight;
                PanelFragment.this.mLayoutView.setLayoutParams(layoutParams);
            }
        }
    };
    private RecyclerView mPanelSlices;
    @VisibleForTesting
    PanelSlicesLoaderCountdownLatch mPanelSlicesLoaderCountdownLatch;
    private TextView mSeeMoreButton;
    private final Map<Uri, LiveData<Slice>> mSliceLiveData = new LinkedHashMap();
    private ImageView mTitleIcon;
    private TextView mTitleView;

    static /* synthetic */ boolean lambda$new$0() {
        return false;
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(C0012R$layout.panel_layout, viewGroup, false);
        this.mLayoutView = inflate;
        inflate.getViewTreeObserver().addOnGlobalLayoutListener(this.mPanelLayoutListener);
        this.mMaxHeight = getResources().getDimensionPixelSize(C0007R$dimen.output_switcher_slice_max_height);
        createPanelContent();
        return this.mLayoutView;
    }

    /* access modifiers changed from: package-private */
    public void updatePanelWithAnimation() {
        AnimatorSet buildAnimatorSet = buildAnimatorSet(this.mLayoutView, 0.0f, (float) this.mLayoutView.findViewById(C0010R$id.panel_container).getHeight(), 1.0f, 0.0f, 200);
        ValueAnimator valueAnimator = new ValueAnimator();
        valueAnimator.setFloatValues(0.0f, 1.0f);
        buildAnimatorSet.play(valueAnimator);
        buildAnimatorSet.addListener(new AnimatorListenerAdapter() {
            /* class com.android.settings.panel.PanelFragment.AnonymousClass3 */

            public void onAnimationEnd(Animator animator) {
                PanelFragment.this.createPanelContent();
            }
        });
        buildAnimatorSet.start();
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void createPanelContent() {
        FragmentActivity activity = getActivity();
        if (this.mLayoutView == null) {
            activity.finish();
        }
        ViewGroup.LayoutParams layoutParams = this.mLayoutView.getLayoutParams();
        layoutParams.height = -2;
        this.mLayoutView.setLayoutParams(layoutParams);
        this.mPanelSlices = (RecyclerView) this.mLayoutView.findViewById(C0010R$id.panel_parent_layout);
        this.mSeeMoreButton = (TextView) this.mLayoutView.findViewById(C0010R$id.see_more);
        this.mDoneButton = (TextView) this.mLayoutView.findViewById(C0010R$id.done);
        this.mTitleView = (TextView) this.mLayoutView.findViewById(C0010R$id.panel_title);
        this.mPanelHeader = (LinearLayout) this.mLayoutView.findViewById(C0010R$id.panel_header);
        this.mTitleIcon = (ImageView) this.mLayoutView.findViewById(C0010R$id.title_icon);
        this.mHeaderTitle = (TextView) this.mLayoutView.findViewById(C0010R$id.header_title);
        this.mHeaderSubtitle = (TextView) this.mLayoutView.findViewById(C0010R$id.header_subtitle);
        this.mFooterDivider = this.mLayoutView.findViewById(C0010R$id.footer_divider);
        this.mPanelSlices.setVisibility(8);
        Bundle arguments = getArguments();
        String string = arguments.getString("PANEL_CALLING_PACKAGE_NAME");
        PanelContent panel = FeatureFactory.getFactory(activity).getPanelFeatureProvider().getPanel(activity, arguments);
        this.mPanel = panel;
        if (panel == null) {
            activity.finish();
        }
        this.mPanel.registerCallback(new LocalPanelCallback());
        if (this.mPanel instanceof LifecycleObserver) {
            getLifecycle().addObserver((LifecycleObserver) this.mPanel);
        }
        this.mMetricsProvider = FeatureFactory.getFactory(activity).getMetricsFeatureProvider();
        this.mPanelSlices.setLayoutManager(new LinearLayoutManager(activity));
        this.mLayoutView.getViewTreeObserver().addOnPreDrawListener(this.mOnPreDrawListener);
        loadAllSlices();
        IconCompat icon = this.mPanel.getIcon();
        CharSequence title = this.mPanel.getTitle();
        if (icon == null) {
            this.mTitleView.setVisibility(0);
            this.mPanelHeader.setVisibility(8);
            this.mTitleView.setText(title);
        } else {
            this.mTitleView.setVisibility(8);
            this.mPanelHeader.setVisibility(0);
            this.mPanelHeader.setAccessibilityPaneTitle(title);
            this.mTitleIcon.setImageIcon(icon.toIcon(getContext()));
            this.mHeaderTitle.setText(title);
            this.mHeaderSubtitle.setText(this.mPanel.getSubTitle());
            if (this.mPanel.getHeaderIconIntent() != null) {
                this.mTitleIcon.setOnClickListener(getHeaderIconListener());
                this.mTitleIcon.setLayoutParams(new LinearLayout.LayoutParams(-2, -2));
            } else {
                int dimensionPixelSize = getResources().getDimensionPixelSize(C0007R$dimen.output_switcher_panel_icon_size);
                this.mTitleIcon.setLayoutParams(new LinearLayout.LayoutParams(dimensionPixelSize, dimensionPixelSize));
            }
        }
        if (this.mPanel.getViewType() == 2) {
            this.mFooterDivider.setVisibility(0);
        } else {
            this.mFooterDivider.setVisibility(8);
        }
        this.mSeeMoreButton.setOnClickListener(getSeeMoreListener());
        this.mDoneButton.setOnClickListener(getCloseListener());
        if (this.mPanel.isCustomizedButtonUsed()) {
            CharSequence customizedButtonTitle = this.mPanel.getCustomizedButtonTitle();
            if (TextUtils.isEmpty(customizedButtonTitle)) {
                this.mSeeMoreButton.setVisibility(8);
            } else {
                this.mSeeMoreButton.setVisibility(0);
                this.mSeeMoreButton.setText(customizedButtonTitle);
            }
        } else if (this.mPanel.getSeeMoreIntent() == null) {
            this.mSeeMoreButton.setVisibility(8);
        }
        this.mMetricsProvider.action(0, 1, this.mPanel.getMetricsCategory(), string, 0);
    }

    private void loadAllSlices() {
        this.mSliceLiveData.clear();
        List<Uri> slices = this.mPanel.getSlices();
        this.mPanelSlicesLoaderCountdownLatch = new PanelSlicesLoaderCountdownLatch(slices.size());
        for (Uri uri : slices) {
            LiveData<Slice> fromUri = SliceLiveData.fromUri(getActivity(), uri, new SliceLiveData.OnErrorListener(uri) {
                /* class com.android.settings.panel.$$Lambda$PanelFragment$uDVUYDPBeDRTbj_G_7viH7rl6o */
                public final /* synthetic */ Uri f$1;

                {
                    this.f$1 = r2;
                }

                @Override // androidx.slice.widget.SliceLiveData.OnErrorListener
                public final void onSliceError(int i, Throwable th) {
                    PanelFragment.this.lambda$loadAllSlices$1$PanelFragment(this.f$1, i, th);
                }
            });
            this.mSliceLiveData.put(uri, fromUri);
            fromUri.observe(getViewLifecycleOwner(), new Observer(uri) {
                /* class com.android.settings.panel.$$Lambda$PanelFragment$733KKi6x5qZdxlsFpx2EjWDfK7c */
                public final /* synthetic */ Uri f$1;

                {
                    this.f$1 = r2;
                }

                @Override // androidx.lifecycle.Observer
                public final void onChanged(Object obj) {
                    PanelFragment.this.lambda$loadAllSlices$3$PanelFragment(this.f$1, (Slice) obj);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$loadAllSlices$1 */
    public /* synthetic */ void lambda$loadAllSlices$1$PanelFragment(Uri uri, int i, Throwable th) {
        removeSliceLiveData(uri);
        this.mPanelSlicesLoaderCountdownLatch.markSliceLoaded(uri);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$loadAllSlices$3 */
    public /* synthetic */ void lambda$loadAllSlices$3$PanelFragment(Uri uri, Slice slice) {
        if (!this.mPanelSlicesLoaderCountdownLatch.isSliceLoaded(uri)) {
            SliceMetadata from = SliceMetadata.from(getActivity(), slice);
            if (slice == null || from.isErrorSlice()) {
                removeSliceLiveData(uri);
                this.mPanelSlicesLoaderCountdownLatch.markSliceLoaded(uri);
            } else if (from.getLoadingState() == 2) {
                this.mPanelSlicesLoaderCountdownLatch.markSliceLoaded(uri);
            } else {
                new Handler().postDelayed(new Runnable(uri) {
                    /* class com.android.settings.panel.$$Lambda$PanelFragment$68iLbW2RqT9FL47AbuyGF_gb58 */
                    public final /* synthetic */ Uri f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        PanelFragment.this.lambda$loadAllSlices$2$PanelFragment(this.f$1);
                    }
                }, 250);
            }
            loadPanelWhenReady();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$loadAllSlices$2 */
    public /* synthetic */ void lambda$loadAllSlices$2$PanelFragment(Uri uri) {
        this.mPanelSlicesLoaderCountdownLatch.markSliceLoaded(uri);
        loadPanelWhenReady();
    }

    private void removeSliceLiveData(Uri uri) {
        if (!Arrays.asList(getResources().getStringArray(C0003R$array.config_panel_keep_observe_uri)).contains(uri.toString())) {
            this.mSliceLiveData.remove(uri);
        }
    }

    private void loadPanelWhenReady() {
        if (this.mPanelSlicesLoaderCountdownLatch.isPanelReadyToLoad()) {
            PanelSlicesAdapter panelSlicesAdapter = new PanelSlicesAdapter(this, this.mSliceLiveData, this.mPanel.getMetricsCategory());
            this.mAdapter = panelSlicesAdapter;
            this.mPanelSlices.setAdapter(panelSlicesAdapter);
            this.mPanelSlices.getViewTreeObserver().addOnGlobalLayoutListener(this.mOnGlobalLayoutListener);
            this.mPanelSlices.setVisibility(0);
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void animateIn() {
        AnimatorSet buildAnimatorSet = buildAnimatorSet(this.mLayoutView, (float) this.mLayoutView.findViewById(C0010R$id.panel_container).getHeight(), 0.0f, 0.0f, 1.0f, 250);
        ValueAnimator valueAnimator = new ValueAnimator();
        valueAnimator.setFloatValues(0.0f, 1.0f);
        buildAnimatorSet.play(valueAnimator);
        buildAnimatorSet.start();
        this.mLayoutView.getViewTreeObserver().removeOnPreDrawListener(this.mOnPreDrawListener);
    }

    private static AnimatorSet buildAnimatorSet(View view, float f, float f2, float f3, float f4, int i) {
        View findViewById = view.findViewById(C0010R$id.panel_container);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration((long) i);
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.playTogether(ObjectAnimator.ofFloat(findViewById, View.TRANSLATION_Y, f, f2), ObjectAnimator.ofFloat(findViewById, View.ALPHA, f3, f4));
        return animatorSet;
    }

    @Override // androidx.fragment.app.Fragment
    public void onDestroyView() {
        super.onDestroyView();
        if (TextUtils.isEmpty(this.mPanelClosedKey)) {
            this.mPanelClosedKey = "others";
        }
        View view = this.mLayoutView;
        if (view != null) {
            view.getViewTreeObserver().removeOnGlobalLayoutListener(this.mPanelLayoutListener);
        }
        this.mMetricsProvider.action(0, 2, this.mPanel.getMetricsCategory(), this.mPanelClosedKey, 0);
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public View.OnClickListener getSeeMoreListener() {
        return new View.OnClickListener() {
            /* class com.android.settings.panel.$$Lambda$PanelFragment$qzrDgY7NYIpIoF6xu9DUs5CSAm4 */

            public final void onClick(View view) {
                PanelFragment.this.lambda$getSeeMoreListener$4$PanelFragment(view);
            }
        };
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$getSeeMoreListener$4 */
    public /* synthetic */ void lambda$getSeeMoreListener$4$PanelFragment(View view) {
        this.mPanelClosedKey = "see_more";
        if (this.mPanel.isCustomizedButtonUsed()) {
            this.mPanel.onClickCustomizedButton();
            return;
        }
        FragmentActivity activity = getActivity();
        activity.startActivityForResult(this.mPanel.getSeeMoreIntent(), 0);
        activity.finish();
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public View.OnClickListener getCloseListener() {
        return new View.OnClickListener() {
            /* class com.android.settings.panel.$$Lambda$PanelFragment$wA18xnPrODGA3u1MDrl3uC5dUzY */

            public final void onClick(View view) {
                PanelFragment.this.lambda$getCloseListener$5$PanelFragment(view);
            }
        };
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$getCloseListener$5 */
    public /* synthetic */ void lambda$getCloseListener$5$PanelFragment(View view) {
        this.mPanelClosedKey = "done";
        getActivity().finish();
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public View.OnClickListener getHeaderIconListener() {
        return new View.OnClickListener() {
            /* class com.android.settings.panel.$$Lambda$PanelFragment$N3DMhbGuDEaImQpPovL7Ug2k3kc */

            public final void onClick(View view) {
                PanelFragment.this.lambda$getHeaderIconListener$6$PanelFragment(view);
            }
        };
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$getHeaderIconListener$6 */
    public /* synthetic */ void lambda$getHeaderIconListener$6$PanelFragment(View view) {
        getActivity().startActivity(this.mPanel.getHeaderIconIntent());
    }

    /* access modifiers changed from: package-private */
    public int getPanelViewType() {
        return this.mPanel.getViewType();
    }

    /* access modifiers changed from: package-private */
    public class LocalPanelCallback implements PanelContentCallback {
        LocalPanelCallback() {
        }

        @Override // com.android.settings.panel.PanelContentCallback
        public void onCustomizedButtonStateChanged() {
            ThreadUtils.postOnMainThread(new Runnable() {
                /* class com.android.settings.panel.$$Lambda$PanelFragment$LocalPanelCallback$4cDjHpASvueF3wzqvVwC5Zj11fU */

                public final void run() {
                    PanelFragment.LocalPanelCallback.this.lambda$onCustomizedButtonStateChanged$0$PanelFragment$LocalPanelCallback();
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$onCustomizedButtonStateChanged$0 */
        public /* synthetic */ void lambda$onCustomizedButtonStateChanged$0$PanelFragment$LocalPanelCallback() {
            PanelFragment.this.mSeeMoreButton.setVisibility(PanelFragment.this.mPanel.isCustomizedButtonUsed() ? 0 : 8);
            PanelFragment.this.mSeeMoreButton.setText(PanelFragment.this.mPanel.getCustomizedButtonTitle());
        }

        @Override // com.android.settings.panel.PanelContentCallback
        public void onHeaderChanged() {
            ThreadUtils.postOnMainThread(new Runnable() {
                /* class com.android.settings.panel.$$Lambda$PanelFragment$LocalPanelCallback$_oN9G82As_0L8bDKbGGq9tZzmw */

                public final void run() {
                    PanelFragment.LocalPanelCallback.this.lambda$onHeaderChanged$1$PanelFragment$LocalPanelCallback();
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$onHeaderChanged$1 */
        public /* synthetic */ void lambda$onHeaderChanged$1$PanelFragment$LocalPanelCallback() {
            PanelFragment.this.mTitleIcon.setImageIcon(PanelFragment.this.mPanel.getIcon().toIcon(PanelFragment.this.getContext()));
            PanelFragment.this.mHeaderTitle.setText(PanelFragment.this.mPanel.getTitle());
            PanelFragment.this.mHeaderSubtitle.setText(PanelFragment.this.mPanel.getSubTitle());
        }

        @Override // com.android.settings.panel.PanelContentCallback
        public void forceClose() {
            PanelFragment.this.mPanelClosedKey = "others";
            getFragmentActivity().finish();
        }

        /* access modifiers changed from: package-private */
        @VisibleForTesting
        public FragmentActivity getFragmentActivity() {
            return PanelFragment.this.getActivity();
        }
    }
}
