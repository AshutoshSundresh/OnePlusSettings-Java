package com.android.settings.widget;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import androidx.viewpager.widget.ViewPager;
import java.util.Locale;

public final class RtlCompatibleViewPager extends ViewPager {
    public RtlCompatibleViewPager(Context context) {
        this(context, null);
    }

    public RtlCompatibleViewPager(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override // androidx.viewpager.widget.ViewPager
    public int getCurrentItem() {
        return getRtlAwareIndex(super.getCurrentItem());
    }

    @Override // androidx.viewpager.widget.ViewPager
    public void setCurrentItem(int i) {
        super.setCurrentItem(getRtlAwareIndex(i));
    }

    @Override // androidx.viewpager.widget.ViewPager
    public Parcelable onSaveInstanceState() {
        RtlSavedState rtlSavedState = new RtlSavedState(super.onSaveInstanceState());
        rtlSavedState.position = getCurrentItem();
        return rtlSavedState;
    }

    @Override // androidx.viewpager.widget.ViewPager
    public void onRestoreInstanceState(Parcelable parcelable) {
        RtlSavedState rtlSavedState = (RtlSavedState) parcelable;
        super.onRestoreInstanceState(rtlSavedState.getSuperState());
        setCurrentItem(rtlSavedState.position);
    }

    public int getRtlAwareIndex(int i) {
        return TextUtils.getLayoutDirectionFromLocale(Locale.getDefault()) == 1 ? (getAdapter().getCount() - i) - 1 : i;
    }

    /* access modifiers changed from: package-private */
    public static class RtlSavedState extends View.BaseSavedState {
        public static final Parcelable.ClassLoaderCreator<RtlSavedState> CREATOR = new Parcelable.ClassLoaderCreator<RtlSavedState>() {
            /* class com.android.settings.widget.RtlCompatibleViewPager.RtlSavedState.AnonymousClass1 */

            @Override // android.os.Parcelable.ClassLoaderCreator
            public RtlSavedState createFromParcel(Parcel parcel, ClassLoader classLoader) {
                return new RtlSavedState(parcel, classLoader);
            }

            @Override // android.os.Parcelable.Creator
            public RtlSavedState createFromParcel(Parcel parcel) {
                return new RtlSavedState(parcel, null);
            }

            @Override // android.os.Parcelable.Creator
            public RtlSavedState[] newArray(int i) {
                return new RtlSavedState[i];
            }
        };
        int position;

        public RtlSavedState(Parcelable parcelable) {
            super(parcelable);
        }

        private RtlSavedState(Parcel parcel, ClassLoader classLoader) {
            super(parcel, classLoader);
            this.position = parcel.readInt();
        }

        public void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            parcel.writeInt(this.position);
        }
    }
}
