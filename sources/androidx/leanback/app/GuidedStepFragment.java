package androidx.leanback.app;

import android.animation.AnimatorSet;
import android.app.Fragment;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.transition.Transition;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import androidx.leanback.R$attr;
import androidx.leanback.R$id;
import androidx.leanback.R$layout;
import androidx.leanback.transition.TransitionHelper;
import androidx.leanback.widget.GuidanceStylist;
import androidx.leanback.widget.GuidedAction;
import androidx.leanback.widget.GuidedActionAdapter;
import androidx.leanback.widget.GuidedActionAdapterGroup;
import androidx.leanback.widget.GuidedActionsStylist;
import androidx.leanback.widget.NonOverlappingLinearLayout;
import java.util.ArrayList;
import java.util.List;

@Deprecated
public class GuidedStepFragment extends Fragment implements GuidedActionAdapter.FocusListener {
    private int entranceTransitionType = 0;
    private List<GuidedAction> mActions = new ArrayList();
    GuidedActionsStylist mActionsStylist;
    private GuidedActionAdapter mAdapter;
    private GuidedActionAdapterGroup mAdapterGroup;
    private List<GuidedAction> mButtonActions = new ArrayList();
    private GuidedActionsStylist mButtonActionsStylist;
    private GuidedActionAdapter mButtonAdapter;
    private GuidanceStylist mGuidanceStylist;
    private GuidedActionAdapter mSubAdapter;
    private ContextThemeWrapper mThemeWrapper;

    public boolean isFocusOutEndAllowed() {
        return false;
    }

    public boolean isFocusOutStartAllowed() {
        return false;
    }

    public abstract void onCreateActions(List<GuidedAction> list, Bundle bundle);

    public void onCreateButtonActions(List<GuidedAction> list, Bundle bundle) {
    }

    public abstract GuidanceStylist.Guidance onCreateGuidance(Bundle bundle);

    public abstract void onGuidedActionClicked(GuidedAction guidedAction);

    @Deprecated
    public void onGuidedActionEdited(GuidedAction guidedAction) {
    }

    @Override // androidx.leanback.widget.GuidedActionAdapter.FocusListener
    public void onGuidedActionFocused(GuidedAction guidedAction) {
    }

    public abstract int onProvideTheme();

    public boolean onSubGuidedActionClicked(GuidedAction guidedAction) {
        return true;
    }

    public GuidedStepFragment() {
        onProvideFragmentTransitions();
    }

    public GuidanceStylist onCreateGuidanceStylist() {
        return new GuidanceStylist();
    }

    public GuidedActionsStylist onCreateActionsStylist() {
        return new GuidedActionsStylist();
    }

    public GuidedActionsStylist onCreateButtonActionsStylist() {
        GuidedActionsStylist guidedActionsStylist = new GuidedActionsStylist();
        guidedActionsStylist.setAsButtonActions();
        return guidedActionsStylist;
    }

    public boolean isExpanded() {
        return this.mActionsStylist.isExpanded();
    }

    public void expandAction(GuidedAction guidedAction, boolean z) {
        this.mActionsStylist.expandAction(guidedAction, z);
    }

    public void collapseSubActions() {
        collapseAction(true);
    }

    public void collapseAction(boolean z) {
        GuidedActionsStylist guidedActionsStylist = this.mActionsStylist;
        if (guidedActionsStylist != null && guidedActionsStylist.getActionsGridView() != null) {
            this.mActionsStylist.collapseAction(z);
        }
    }

    public void onGuidedActionEditCanceled(GuidedAction guidedAction) {
        onGuidedActionEdited(guidedAction);
    }

    public long onGuidedActionEditedAndProceed(GuidedAction guidedAction) {
        onGuidedActionEdited(guidedAction);
        return -2;
    }

    public void setButtonActions(List<GuidedAction> list) {
        this.mButtonActions = list;
        GuidedActionAdapter guidedActionAdapter = this.mButtonAdapter;
        if (guidedActionAdapter != null) {
            guidedActionAdapter.setActions(list);
        }
    }

    public void setActions(List<GuidedAction> list) {
        this.mActions = list;
        GuidedActionAdapter guidedActionAdapter = this.mAdapter;
        if (guidedActionAdapter != null) {
            guidedActionAdapter.setActions(list);
        }
    }

    /* access modifiers changed from: protected */
    public void onProvideFragmentTransitions() {
        if (Build.VERSION.SDK_INT >= 21) {
            int uiStyle = getUiStyle();
            if (uiStyle == 0) {
                Object createFadeAndShortSlide = TransitionHelper.createFadeAndShortSlide(8388613);
                TransitionHelper.exclude(createFadeAndShortSlide, R$id.guidedstep_background, true);
                TransitionHelper.exclude(createFadeAndShortSlide, R$id.guidedactions_sub_list_background, true);
                setEnterTransition((Transition) createFadeAndShortSlide);
                Object createFadeTransition = TransitionHelper.createFadeTransition(3);
                TransitionHelper.include(createFadeTransition, R$id.guidedactions_sub_list_background);
                Object createChangeBounds = TransitionHelper.createChangeBounds(false);
                Object createTransitionSet = TransitionHelper.createTransitionSet(false);
                TransitionHelper.addTransition(createTransitionSet, createFadeTransition);
                TransitionHelper.addTransition(createTransitionSet, createChangeBounds);
                setSharedElementEnterTransition((Transition) createTransitionSet);
            } else if (uiStyle == 1) {
                if (this.entranceTransitionType == 0) {
                    Object createFadeTransition2 = TransitionHelper.createFadeTransition(3);
                    TransitionHelper.include(createFadeTransition2, R$id.guidedstep_background);
                    Object createFadeAndShortSlide2 = TransitionHelper.createFadeAndShortSlide(8388615);
                    TransitionHelper.include(createFadeAndShortSlide2, R$id.content_fragment);
                    TransitionHelper.include(createFadeAndShortSlide2, R$id.action_fragment_root);
                    Object createTransitionSet2 = TransitionHelper.createTransitionSet(false);
                    TransitionHelper.addTransition(createTransitionSet2, createFadeTransition2);
                    TransitionHelper.addTransition(createTransitionSet2, createFadeAndShortSlide2);
                    setEnterTransition((Transition) createTransitionSet2);
                } else {
                    Object createFadeAndShortSlide3 = TransitionHelper.createFadeAndShortSlide(80);
                    TransitionHelper.include(createFadeAndShortSlide3, R$id.guidedstep_background_view_root);
                    Object createTransitionSet3 = TransitionHelper.createTransitionSet(false);
                    TransitionHelper.addTransition(createTransitionSet3, createFadeAndShortSlide3);
                    setEnterTransition((Transition) createTransitionSet3);
                }
                setSharedElementEnterTransition(null);
            } else if (uiStyle == 2) {
                setEnterTransition(null);
                setSharedElementEnterTransition(null);
            }
            Object createFadeAndShortSlide4 = TransitionHelper.createFadeAndShortSlide(8388611);
            TransitionHelper.exclude(createFadeAndShortSlide4, R$id.guidedstep_background, true);
            TransitionHelper.exclude(createFadeAndShortSlide4, R$id.guidedactions_sub_list_background, true);
            setExitTransition((Transition) createFadeAndShortSlide4);
        }
    }

    public View onCreateBackgroundView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return layoutInflater.inflate(R$layout.lb_guidedstep_background, viewGroup, false);
    }

    public int getUiStyle() {
        Bundle arguments = getArguments();
        if (arguments == null) {
            return 1;
        }
        return arguments.getInt("uiStyle", 1);
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mGuidanceStylist = onCreateGuidanceStylist();
        this.mActionsStylist = onCreateActionsStylist();
        this.mButtonActionsStylist = onCreateButtonActionsStylist();
        onProvideFragmentTransitions();
        ArrayList arrayList = new ArrayList();
        onCreateActions(arrayList, bundle);
        if (bundle != null) {
            onRestoreActions(arrayList, bundle);
        }
        setActions(arrayList);
        ArrayList arrayList2 = new ArrayList();
        onCreateButtonActions(arrayList2, bundle);
        if (bundle != null) {
            onRestoreButtonActions(arrayList2, bundle);
        }
        setButtonActions(arrayList2);
    }

    public void onDestroyView() {
        this.mGuidanceStylist.onDestroyView();
        this.mActionsStylist.onDestroyView();
        this.mButtonActionsStylist.onDestroyView();
        this.mAdapter = null;
        this.mSubAdapter = null;
        this.mButtonAdapter = null;
        this.mAdapterGroup = null;
        super.onDestroyView();
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        resolveTheme();
        LayoutInflater themeInflater = getThemeInflater(layoutInflater);
        GuidedStepRootLayout guidedStepRootLayout = (GuidedStepRootLayout) themeInflater.inflate(R$layout.lb_guidedstep_fragment, viewGroup, false);
        guidedStepRootLayout.setFocusOutStart(isFocusOutStartAllowed());
        guidedStepRootLayout.setFocusOutEnd(isFocusOutEndAllowed());
        ViewGroup viewGroup2 = (ViewGroup) guidedStepRootLayout.findViewById(R$id.content_fragment);
        ViewGroup viewGroup3 = (ViewGroup) guidedStepRootLayout.findViewById(R$id.action_fragment);
        ((NonOverlappingLinearLayout) viewGroup3).setFocusableViewAvailableFixEnabled(true);
        viewGroup2.addView(this.mGuidanceStylist.onCreateView(themeInflater, viewGroup2, onCreateGuidance(bundle)));
        viewGroup3.addView(this.mActionsStylist.onCreateView(themeInflater, viewGroup3));
        View onCreateView = this.mButtonActionsStylist.onCreateView(themeInflater, viewGroup3);
        viewGroup3.addView(onCreateView);
        AnonymousClass1 r13 = new GuidedActionAdapter.EditListener() {
            /* class androidx.leanback.app.GuidedStepFragment.AnonymousClass1 */

            @Override // androidx.leanback.widget.GuidedActionAdapter.EditListener
            public void onImeOpen() {
                GuidedStepFragment.this.runImeAnimations(true);
            }

            @Override // androidx.leanback.widget.GuidedActionAdapter.EditListener
            public void onImeClose() {
                GuidedStepFragment.this.runImeAnimations(false);
            }

            @Override // androidx.leanback.widget.GuidedActionAdapter.EditListener
            public long onGuidedActionEditedAndProceed(GuidedAction guidedAction) {
                return GuidedStepFragment.this.onGuidedActionEditedAndProceed(guidedAction);
            }

            @Override // androidx.leanback.widget.GuidedActionAdapter.EditListener
            public void onGuidedActionEditCanceled(GuidedAction guidedAction) {
                GuidedStepFragment.this.onGuidedActionEditCanceled(guidedAction);
            }
        };
        this.mAdapter = new GuidedActionAdapter(this.mActions, new GuidedActionAdapter.ClickListener() {
            /* class androidx.leanback.app.GuidedStepFragment.AnonymousClass2 */

            @Override // androidx.leanback.widget.GuidedActionAdapter.ClickListener
            public void onGuidedActionClicked(GuidedAction guidedAction) {
                GuidedStepFragment.this.onGuidedActionClicked(guidedAction);
                if (GuidedStepFragment.this.isExpanded()) {
                    GuidedStepFragment.this.collapseAction(true);
                } else if (guidedAction.hasSubActions() || guidedAction.hasEditableActivatorView()) {
                    GuidedStepFragment.this.expandAction(guidedAction, true);
                }
            }
        }, this, this.mActionsStylist, false);
        this.mButtonAdapter = new GuidedActionAdapter(this.mButtonActions, new GuidedActionAdapter.ClickListener() {
            /* class androidx.leanback.app.GuidedStepFragment.AnonymousClass3 */

            @Override // androidx.leanback.widget.GuidedActionAdapter.ClickListener
            public void onGuidedActionClicked(GuidedAction guidedAction) {
                GuidedStepFragment.this.onGuidedActionClicked(guidedAction);
            }
        }, this, this.mButtonActionsStylist, false);
        this.mSubAdapter = new GuidedActionAdapter(null, new GuidedActionAdapter.ClickListener() {
            /* class androidx.leanback.app.GuidedStepFragment.AnonymousClass4 */

            @Override // androidx.leanback.widget.GuidedActionAdapter.ClickListener
            public void onGuidedActionClicked(GuidedAction guidedAction) {
                if (!GuidedStepFragment.this.mActionsStylist.isInExpandTransition() && GuidedStepFragment.this.onSubGuidedActionClicked(guidedAction)) {
                    GuidedStepFragment.this.collapseSubActions();
                }
            }
        }, this, this.mActionsStylist, true);
        GuidedActionAdapterGroup guidedActionAdapterGroup = new GuidedActionAdapterGroup();
        this.mAdapterGroup = guidedActionAdapterGroup;
        guidedActionAdapterGroup.addAdpter(this.mAdapter, this.mButtonAdapter);
        this.mAdapterGroup.addAdpter(this.mSubAdapter, null);
        this.mAdapterGroup.setEditListener(r13);
        this.mActionsStylist.setEditListener(r13);
        this.mActionsStylist.getActionsGridView().setAdapter(this.mAdapter);
        if (this.mActionsStylist.getSubActionsGridView() != null) {
            this.mActionsStylist.getSubActionsGridView().setAdapter(this.mSubAdapter);
        }
        this.mButtonActionsStylist.getActionsGridView().setAdapter(this.mButtonAdapter);
        if (this.mButtonActions.size() == 0) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) onCreateView.getLayoutParams();
            layoutParams.weight = 0.0f;
            onCreateView.setLayoutParams(layoutParams);
        } else {
            Context context = this.mThemeWrapper;
            if (context == null) {
                context = FragmentUtil.getContext(this);
            }
            TypedValue typedValue = new TypedValue();
            if (context.getTheme().resolveAttribute(R$attr.guidedActionContentWidthWeightTwoPanels, typedValue, true)) {
                View findViewById = guidedStepRootLayout.findViewById(R$id.action_fragment_root);
                float f = typedValue.getFloat();
                LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) findViewById.getLayoutParams();
                layoutParams2.weight = f;
                findViewById.setLayoutParams(layoutParams2);
            }
        }
        View onCreateBackgroundView = onCreateBackgroundView(themeInflater, guidedStepRootLayout, bundle);
        if (onCreateBackgroundView != null) {
            ((FrameLayout) guidedStepRootLayout.findViewById(R$id.guidedstep_background_view_root)).addView(onCreateBackgroundView, 0);
        }
        return guidedStepRootLayout;
    }

    public void onResume() {
        super.onResume();
        getView().findViewById(R$id.action_fragment).requestFocus();
    }

    /* access modifiers changed from: package-private */
    public final String getAutoRestoreKey(GuidedAction guidedAction) {
        return "action_" + guidedAction.getId();
    }

    /* access modifiers changed from: package-private */
    public final String getButtonAutoRestoreKey(GuidedAction guidedAction) {
        return "buttonaction_" + guidedAction.getId();
    }

    static boolean isSaveEnabled(GuidedAction guidedAction) {
        return guidedAction.isAutoSaveRestoreEnabled() && guidedAction.getId() != -1;
    }

    /* access modifiers changed from: package-private */
    public final void onRestoreActions(List<GuidedAction> list, Bundle bundle) {
        int size = list.size();
        for (int i = 0; i < size; i++) {
            GuidedAction guidedAction = list.get(i);
            if (isSaveEnabled(guidedAction)) {
                guidedAction.onRestoreInstanceState(bundle, getAutoRestoreKey(guidedAction));
            }
        }
    }

    /* access modifiers changed from: package-private */
    public final void onRestoreButtonActions(List<GuidedAction> list, Bundle bundle) {
        int size = list.size();
        for (int i = 0; i < size; i++) {
            GuidedAction guidedAction = list.get(i);
            if (isSaveEnabled(guidedAction)) {
                guidedAction.onRestoreInstanceState(bundle, getButtonAutoRestoreKey(guidedAction));
            }
        }
    }

    /* access modifiers changed from: package-private */
    public final void onSaveActions(List<GuidedAction> list, Bundle bundle) {
        int size = list.size();
        for (int i = 0; i < size; i++) {
            GuidedAction guidedAction = list.get(i);
            if (isSaveEnabled(guidedAction)) {
                guidedAction.onSaveInstanceState(bundle, getAutoRestoreKey(guidedAction));
            }
        }
    }

    /* access modifiers changed from: package-private */
    public final void onSaveButtonActions(List<GuidedAction> list, Bundle bundle) {
        int size = list.size();
        for (int i = 0; i < size; i++) {
            GuidedAction guidedAction = list.get(i);
            if (isSaveEnabled(guidedAction)) {
                guidedAction.onSaveInstanceState(bundle, getButtonAutoRestoreKey(guidedAction));
            }
        }
    }

    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        onSaveActions(this.mActions, bundle);
        onSaveButtonActions(this.mButtonActions, bundle);
    }

    private static boolean isGuidedStepTheme(Context context) {
        int i = R$attr.guidedStepThemeFlag;
        TypedValue typedValue = new TypedValue();
        if (!context.getTheme().resolveAttribute(i, typedValue, true) || typedValue.type != 18 || typedValue.data == 0) {
            return false;
        }
        return true;
    }

    private void resolveTheme() {
        Context context = FragmentUtil.getContext(this);
        int onProvideTheme = onProvideTheme();
        if (onProvideTheme == -1 && !isGuidedStepTheme(context)) {
            int i = R$attr.guidedStepTheme;
            TypedValue typedValue = new TypedValue();
            boolean resolveAttribute = context.getTheme().resolveAttribute(i, typedValue, true);
            if (resolveAttribute) {
                ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(context, typedValue.resourceId);
                if (isGuidedStepTheme(contextThemeWrapper)) {
                    this.mThemeWrapper = contextThemeWrapper;
                } else {
                    resolveAttribute = false;
                    this.mThemeWrapper = null;
                }
            }
            if (!resolveAttribute) {
                Log.e("GuidedStepF", "GuidedStepFragment does not have an appropriate theme set.");
            }
        } else if (onProvideTheme != -1) {
            this.mThemeWrapper = new ContextThemeWrapper(context, onProvideTheme);
        }
    }

    private LayoutInflater getThemeInflater(LayoutInflater layoutInflater) {
        ContextThemeWrapper contextThemeWrapper = this.mThemeWrapper;
        if (contextThemeWrapper == null) {
            return layoutInflater;
        }
        return layoutInflater.cloneInContext(contextThemeWrapper);
    }

    /* access modifiers changed from: package-private */
    public void runImeAnimations(boolean z) {
        ArrayList arrayList = new ArrayList();
        if (z) {
            this.mGuidanceStylist.onImeAppearing(arrayList);
            this.mActionsStylist.onImeAppearing(arrayList);
            this.mButtonActionsStylist.onImeAppearing(arrayList);
        } else {
            this.mGuidanceStylist.onImeDisappearing(arrayList);
            this.mActionsStylist.onImeDisappearing(arrayList);
            this.mButtonActionsStylist.onImeDisappearing(arrayList);
        }
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(arrayList);
        animatorSet.start();
    }
}
