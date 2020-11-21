package com.android.settings.display;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.viewpager.widget.ViewPager;
import com.android.settings.C0010R$id;
import com.android.settings.C0017R$string;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.display.PreviewSeekBarPreferenceFragment;
import com.android.settings.widget.DotsPageIndicator;
import com.android.settings.widget.LabeledSeekBar;

public abstract class PreviewSeekBarPreferenceFragment extends SettingsPreferenceFragment {
    protected int mCurrentIndex;
    protected String[] mEntries;
    protected int mInitialIndex;
    private TextView mLabel;
    private View mLarger;
    private DotsPageIndicator mPageIndicator;
    private ViewPager.OnPageChangeListener mPageIndicatorPageChangeListener = new ViewPager.OnPageChangeListener() {
        /* class com.android.settings.display.PreviewSeekBarPreferenceFragment.AnonymousClass2 */

        @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
        public void onPageScrollStateChanged(int i) {
        }

        @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
        public void onPageScrolled(int i, float f, int i2) {
        }

        @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
        public void onPageSelected(int i) {
            PreviewSeekBarPreferenceFragment.this.setPagerIndicatorContentDescription(i);
        }
    };
    private ViewPager.OnPageChangeListener mPreviewPageChangeListener = new ViewPager.OnPageChangeListener() {
        /* class com.android.settings.display.PreviewSeekBarPreferenceFragment.AnonymousClass1 */

        @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
        public void onPageScrollStateChanged(int i) {
        }

        @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
        public void onPageScrolled(int i, float f, int i2) {
        }

        @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
        public void onPageSelected(int i) {
            PreviewSeekBarPreferenceFragment.this.mPreviewPager.sendAccessibilityEvent(16384);
        }
    };
    private ViewPager mPreviewPager;
    private PreviewPagerAdapter mPreviewPagerAdapter;
    private LabeledSeekBar mSeekBar;
    private View mSmaller;

    /* access modifiers changed from: protected */
    public abstract void commit();

    /* access modifiers changed from: protected */
    public abstract Configuration createConfig(Configuration configuration, int i);

    /* access modifiers changed from: protected */
    public abstract int getActivityLayoutResId();

    /* access modifiers changed from: protected */
    public abstract int[] getPreviewSampleResIds();

    /* access modifiers changed from: private */
    public class onPreviewSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        private boolean mSeekByTouch;

        private onPreviewSeekBarChangeListener() {
        }

        public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
            PreviewSeekBarPreferenceFragment.this.setPreviewLayer(i, false);
            if (!this.mSeekByTouch) {
                PreviewSeekBarPreferenceFragment.this.commit();
            }
        }

        public void onStartTrackingTouch(SeekBar seekBar) {
            this.mSeekByTouch = true;
        }

        public void onStopTrackingTouch(SeekBar seekBar) {
            if (PreviewSeekBarPreferenceFragment.this.mPreviewPagerAdapter.isAnimating()) {
                PreviewSeekBarPreferenceFragment.this.mPreviewPagerAdapter.setAnimationEndAction(new Runnable() {
                    /* class com.android.settings.display.$$Lambda$PreviewSeekBarPreferenceFragment$onPreviewSeekBarChangeListener$LuPHkQtN1jvtRG766hiZseS4Js */

                    public final void run() {
                        PreviewSeekBarPreferenceFragment.onPreviewSeekBarChangeListener.this.lambda$onStopTrackingTouch$0$PreviewSeekBarPreferenceFragment$onPreviewSeekBarChangeListener();
                    }
                });
            } else {
                PreviewSeekBarPreferenceFragment.this.commit();
            }
            this.mSeekByTouch = false;
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$onStopTrackingTouch$0 */
        public /* synthetic */ void lambda$onStopTrackingTouch$0$PreviewSeekBarPreferenceFragment$onPreviewSeekBarChangeListener() {
            PreviewSeekBarPreferenceFragment.this.commit();
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View onCreateView = super.onCreateView(layoutInflater, viewGroup, bundle);
        ViewGroup viewGroup2 = (ViewGroup) onCreateView.findViewById(16908351);
        viewGroup2.removeAllViews();
        View inflate = layoutInflater.inflate(getActivityLayoutResId(), viewGroup2, false);
        viewGroup2.addView(inflate);
        this.mLabel = (TextView) inflate.findViewById(C0010R$id.current_label);
        int max = Math.max(1, this.mEntries.length - 1);
        LabeledSeekBar labeledSeekBar = (LabeledSeekBar) inflate.findViewById(C0010R$id.seek_bar);
        this.mSeekBar = labeledSeekBar;
        labeledSeekBar.setLabels(this.mEntries);
        this.mSeekBar.setMax(max);
        View findViewById = inflate.findViewById(C0010R$id.smaller);
        this.mSmaller = findViewById;
        findViewById.setOnClickListener(new View.OnClickListener() {
            /* class com.android.settings.display.$$Lambda$PreviewSeekBarPreferenceFragment$nsTIuQKr0QeKuykgH3GSjjQwy0U */

            public final void onClick(View view) {
                PreviewSeekBarPreferenceFragment.this.lambda$onCreateView$0$PreviewSeekBarPreferenceFragment(view);
            }
        });
        View findViewById2 = inflate.findViewById(C0010R$id.larger);
        this.mLarger = findViewById2;
        findViewById2.setOnClickListener(new View.OnClickListener() {
            /* class com.android.settings.display.$$Lambda$PreviewSeekBarPreferenceFragment$YZVIWVH43LF5m8117asT1G96GFU */

            public final void onClick(View view) {
                PreviewSeekBarPreferenceFragment.this.lambda$onCreateView$1$PreviewSeekBarPreferenceFragment(view);
            }
        });
        if (this.mEntries.length == 1) {
            this.mSeekBar.setEnabled(false);
        }
        Context context = getContext();
        Configuration configuration = context.getResources().getConfiguration();
        boolean z = configuration.getLayoutDirection() == 1;
        Configuration[] configurationArr = new Configuration[this.mEntries.length];
        for (int i = 0; i < this.mEntries.length; i++) {
            configurationArr[i] = createConfig(configuration, i);
        }
        int[] previewSampleResIds = getPreviewSampleResIds();
        this.mPreviewPager = (ViewPager) inflate.findViewById(C0010R$id.preview_pager);
        PreviewPagerAdapter previewPagerAdapter = new PreviewPagerAdapter(context, z, previewSampleResIds, configurationArr);
        this.mPreviewPagerAdapter = previewPagerAdapter;
        this.mPreviewPager.setAdapter(previewPagerAdapter);
        this.mPreviewPager.setCurrentItem(z ? previewSampleResIds.length - 1 : 0);
        this.mPreviewPager.addOnPageChangeListener(this.mPreviewPageChangeListener);
        DotsPageIndicator dotsPageIndicator = (DotsPageIndicator) inflate.findViewById(C0010R$id.page_indicator);
        this.mPageIndicator = dotsPageIndicator;
        if (previewSampleResIds.length > 1) {
            dotsPageIndicator.setViewPager(this.mPreviewPager);
            this.mPageIndicator.setVisibility(0);
            this.mPageIndicator.setOnPageChangeListener(this.mPageIndicatorPageChangeListener);
        } else {
            dotsPageIndicator.setVisibility(8);
        }
        setPreviewLayer(this.mInitialIndex, false);
        return onCreateView;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onCreateView$0 */
    public /* synthetic */ void lambda$onCreateView$0$PreviewSeekBarPreferenceFragment(View view) {
        int progress = this.mSeekBar.getProgress();
        if (progress > 0) {
            this.mSeekBar.setProgress(progress - 1, true);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onCreateView$1 */
    public /* synthetic */ void lambda$onCreateView$1$PreviewSeekBarPreferenceFragment(View view) {
        int progress = this.mSeekBar.getProgress();
        if (progress < this.mSeekBar.getMax()) {
            this.mSeekBar.setProgress(progress + 1, true);
        }
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onStart() {
        super.onStart();
        this.mSeekBar.setProgress(this.mCurrentIndex);
        this.mSeekBar.setOnSeekBarChangeListener(new onPreviewSeekBarChangeListener());
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onStop() {
        super.onStop();
        this.mSeekBar.setOnSeekBarChangeListener(null);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void setPreviewLayer(int i, boolean z) {
        this.mLabel.setText(this.mEntries[i]);
        boolean z2 = false;
        this.mSmaller.setEnabled(i > 0);
        View view = this.mLarger;
        if (i < this.mEntries.length - 1) {
            z2 = true;
        }
        view.setEnabled(z2);
        setPagerIndicatorContentDescription(this.mPreviewPager.getCurrentItem());
        this.mPreviewPagerAdapter.setPreviewLayer(i, this.mCurrentIndex, this.mPreviewPager.getCurrentItem(), z);
        this.mCurrentIndex = i;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void setPagerIndicatorContentDescription(int i) {
        this.mPageIndicator.setContentDescription(getString(C0017R$string.preview_page_indicator_content_description, Integer.valueOf(i + 1), Integer.valueOf(getPreviewSampleResIds().length)));
    }
}
