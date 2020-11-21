package com.oneplus.settings.product;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.viewpager.widget.ViewPager;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.oneplus.settings.utils.OPUtils;
import java.util.ArrayList;
import java.util.List;

public class ProductInfoActivity extends Activity {
    private static int count;
    private TextView mCountTextView;
    private ViewPager.OnPageChangeListener mPageChangeListener = new ViewPager.OnPageChangeListener() {
        /* class com.oneplus.settings.product.ProductInfoActivity.AnonymousClass1 */

        @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
        public void onPageScrollStateChanged(int i) {
        }

        @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
        public void onPageScrolled(int i, float f, int i2) {
        }

        @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
        public void onPageSelected(int i) {
            ProductInfoActivity.this.updatePagerViews(i);
        }
    };
    private NovicePagerAdapter mPagerAdapter = null;
    private ViewPager mViewPager = null;
    private List<View> mViews = null;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (OPUtils.isGuaProject() || OPUtils.isHDProject() || OPUtils.isSM8X50Products()) {
            getWindow().setFlags(1024, 1024);
            setRequestedOrientation(1);
        } else if (!Build.MODEL.equalsIgnoreCase(getString(C0017R$string.oneplus_model_for_china_and_india)) && !Build.MODEL.equalsIgnoreCase(getString(C0017R$string.oneplus_model_for_europe_and_america))) {
            getWindow().setFlags(1024, 1024);
            setRequestedOrientation(0);
        }
        try {
            initViews();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initViews() {
        setContentView(C0012R$layout.op_product_layout);
        this.mViews = new ArrayList();
        LayoutInflater from = LayoutInflater.from(this);
        if (OPUtils.isGuaProject() || OPUtils.isHDProject() || OPUtils.isSM8X50Products()) {
            int i = 0;
            while (i < 17) {
                View inflate = from.inflate(C0012R$layout.op_product_img_item, (ViewGroup) null);
                Resources resources = getResources();
                StringBuilder sb = new StringBuilder();
                sb.append("product_info_gua_");
                i++;
                sb.append(autoGenericCode(i, 2));
                ((ImageView) inflate.findViewById(C0010R$id.image)).setImageResource(resources.getIdentifier(sb.toString(), "drawable", getPackageName()));
                this.mViews.add(inflate);
            }
        } else if (Build.MODEL.equalsIgnoreCase(getString(C0017R$string.oneplus_model_for_china_and_india)) || Build.MODEL.equalsIgnoreCase(getString(C0017R$string.oneplus_model_for_europe_and_america))) {
            int i2 = 0;
            while (i2 < 11) {
                View inflate2 = from.inflate(C0012R$layout.op_product_img_item, (ViewGroup) null);
                Resources resources2 = getResources();
                StringBuilder sb2 = new StringBuilder();
                sb2.append("product_info_6x_");
                i2++;
                sb2.append(autoGenericCode(i2, 2));
                ((ImageView) inflate2.findViewById(C0010R$id.image)).setImageResource(resources2.getIdentifier(sb2.toString(), "drawable", getPackageName()));
                this.mViews.add(inflate2);
            }
        } else {
            OPUtils.isSurportProductInfo16859(getApplicationContext());
            int i3 = 0;
            while (i3 < 18) {
                View inflate3 = from.inflate(C0012R$layout.op_product_img_item, (ViewGroup) null);
                Resources resources3 = getResources();
                StringBuilder sb3 = new StringBuilder();
                sb3.append("product_info_");
                i3++;
                sb3.append(autoGenericCode(i3, 2));
                ((ImageView) inflate3.findViewById(C0010R$id.image)).setImageResource(resources3.getIdentifier(sb3.toString(), "drawable", getPackageName()));
                this.mViews.add(inflate3);
            }
        }
        this.mViewPager = (ViewPager) findViewById(C0010R$id.main_pager);
        TextView textView = (TextView) findViewById(C0010R$id.textcount);
        this.mCountTextView = textView;
        textView.setVisibility(4);
        NovicePagerAdapter novicePagerAdapter = new NovicePagerAdapter(this.mViews);
        this.mPagerAdapter = novicePagerAdapter;
        this.mViewPager.setAdapter(novicePagerAdapter);
        this.mViewPager.setCurrentItem(0);
        this.mViewPager.setOnPageChangeListener(this.mPageChangeListener);
        count = this.mPagerAdapter.getCount();
        this.mCountTextView.setText("1/" + count);
    }

    public static String autoGenericCode(int i, int i2) {
        return String.format("%0" + i2 + "d", Integer.valueOf(i));
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void updatePagerViews(int i) {
        TextView textView = this.mCountTextView;
        textView.setText((i + 1) + "/" + count);
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        super.onStart();
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        super.onStop();
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
    }
}
