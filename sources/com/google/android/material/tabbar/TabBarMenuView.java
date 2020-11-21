package com.google.android.material.tabbar;

import android.animation.TimeInterpolator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.LinearLayout;
import androidx.appcompat.R$attr;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuItemImpl;
import androidx.appcompat.view.menu.MenuView;
import androidx.core.util.Pools$Pool;
import androidx.core.util.Pools$SynchronizedPool;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;
import androidx.transition.TransitionSet;
import com.google.android.material.R$dimen;
import com.google.android.material.R$drawable;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.internal.TextScale;
import com.google.android.material.internal.ViewUtils;
import java.util.HashSet;

public class TabBarMenuView extends LinearLayout implements MenuView {
    private static final int[] CHECKED_STATE_SET = {16842912};
    private static final int[] DISABLED_STATE_SET = {-16842910};
    private final int activeItemMaxWidth;
    private final int activeItemMinWidth;
    private SparseArray<BadgeDrawable> badgeDrawables;
    private TabBarItemView[] buttons;
    private final int inactiveItemMaxWidth;
    private final int inactiveItemMinWidth;
    private Drawable itemBackground;
    private int itemBackgroundRes;
    private final int itemHeight;
    private boolean itemHorizontalTranslationEnabled;
    private int itemIconSize;
    private ColorStateList itemIconTint;
    private final Pools$Pool<TabBarItemView> itemPool;
    private int labelVisibilityMode;
    private boolean mDisplayAnim;
    private MenuBuilder menu;
    private final View.OnClickListener onClickListener;
    private TabBarPresenter presenter;
    private int selectedItemId;
    private int selectedItemPosition;
    private final TransitionSet set;
    private int[] tempChildWidths;

    private boolean isValidId(int i) {
        return i != -1;
    }

    public int getWindowAnimations() {
        return 0;
    }

    public TabBarMenuView(Context context) {
        this(context, null);
    }

    public TabBarMenuView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.itemPool = new Pools$SynchronizedPool(15);
        this.selectedItemId = 0;
        this.selectedItemPosition = 0;
        this.badgeDrawables = new SparseArray<>(15);
        this.mDisplayAnim = true;
        Resources resources = getResources();
        this.inactiveItemMaxWidth = resources.getDimensionPixelSize(R$dimen.design_tab_bar_item_max_width);
        this.inactiveItemMinWidth = resources.getDimensionPixelSize(R$dimen.design_tab_bar_item_min_width);
        this.activeItemMaxWidth = resources.getDimensionPixelSize(R$dimen.design_tab_bar_active_item_max_width);
        this.activeItemMinWidth = resources.getDimensionPixelSize(R$dimen.design_tab_bar_active_item_min_width);
        this.itemHeight = resources.getDimensionPixelSize(R$dimen.design_tab_bar_height);
        createDefaultColorStateList(16842808);
        AutoTransition autoTransition = new AutoTransition();
        this.set = autoTransition;
        autoTransition.setOrdering(0);
        this.set.setDuration(115L);
        this.set.setInterpolator((TimeInterpolator) new FastOutSlowInInterpolator());
        this.set.addTransition(new TextScale());
        this.onClickListener = new View.OnClickListener() {
            /* class com.google.android.material.tabbar.TabBarMenuView.AnonymousClass1 */

            public void onClick(View view) {
                MenuItemImpl itemData = ((TabBarItemView) view).getItemData();
                if (!TabBarMenuView.this.menu.performItemAction(itemData, TabBarMenuView.this.presenter, 0)) {
                    itemData.setChecked(true);
                }
            }
        };
        this.tempChildWidths = new int[15];
        ViewCompat.setImportantForAccessibility(this, 1);
    }

    @Override // androidx.appcompat.view.menu.MenuView
    public void initialize(MenuBuilder menuBuilder) {
        this.menu = menuBuilder;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        int size = View.MeasureSpec.getSize(i);
        int size2 = this.menu.getVisibleItems().size();
        int childCount = getChildCount();
        int makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(this.itemHeight, 1073741824);
        if (!isShifting(this.labelVisibilityMode, size2) || !this.itemHorizontalTranslationEnabled) {
            int min = Math.min(size / (size2 == 0 ? 1 : size2), this.activeItemMaxWidth);
            int i3 = size - (size2 * min);
            for (int i4 = 0; i4 < childCount; i4++) {
                if (getChildAt(i4).getVisibility() != 8) {
                    if (this.menu.size() <= 6 || getResources().getConfiguration().orientation == 2) {
                        this.tempChildWidths[i4] = min;
                    } else {
                        this.tempChildWidths[i4] = (int) ViewUtils.dpToPx(getContext(), 60);
                    }
                    if (i3 > 0) {
                        int[] iArr = this.tempChildWidths;
                        iArr[i4] = iArr[i4] + 1;
                        i3--;
                    }
                } else {
                    this.tempChildWidths[i4] = 0;
                }
            }
        } else {
            View childAt = getChildAt(this.selectedItemPosition);
            int i5 = this.activeItemMinWidth;
            if (childAt.getVisibility() != 8) {
                childAt.measure(View.MeasureSpec.makeMeasureSpec(this.activeItemMaxWidth, Integer.MIN_VALUE), makeMeasureSpec);
                i5 = Math.max(i5, childAt.getMeasuredWidth());
            }
            int i6 = size2 - (childAt.getVisibility() != 8 ? 1 : 0);
            int min2 = Math.min(size - (this.inactiveItemMinWidth * i6), Math.min(i5, this.activeItemMaxWidth));
            int i7 = size - min2;
            int min3 = Math.min(i7 / (i6 == 0 ? 1 : i6), this.inactiveItemMaxWidth);
            int i8 = i7 - (i6 * min3);
            int i9 = 0;
            while (i9 < childCount) {
                if (getChildAt(i9).getVisibility() != 8) {
                    if (this.menu.size() <= 6 || getResources().getConfiguration().orientation == 2) {
                        this.tempChildWidths[i9] = i9 == this.selectedItemPosition ? min2 : min3;
                    } else {
                        this.tempChildWidths[i9] = (int) ViewUtils.dpToPx(getContext(), 60);
                    }
                    if (i8 > 0) {
                        int[] iArr2 = this.tempChildWidths;
                        iArr2[i9] = iArr2[i9] + 1;
                        i8--;
                    }
                } else {
                    this.tempChildWidths[i9] = 0;
                }
                i9++;
            }
        }
        int i10 = 0;
        for (int i11 = 0; i11 < childCount; i11++) {
            View childAt2 = getChildAt(i11);
            if (childAt2.getVisibility() != 8) {
                childAt2.measure(View.MeasureSpec.makeMeasureSpec(this.tempChildWidths[i11], 1073741824), makeMeasureSpec);
                ((LinearLayout.LayoutParams) childAt2.getLayoutParams()).width = childAt2.getMeasuredWidth();
                i10 += childAt2.getMeasuredWidth();
            }
        }
        setMeasuredDimension(View.resolveSizeAndState(i10, View.MeasureSpec.makeMeasureSpec(i10, 1073741824), 0), View.resolveSizeAndState(this.itemHeight, makeMeasureSpec, 0));
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int childCount = getChildCount();
        int i5 = i3 - i;
        int i6 = i4 - i2;
        int i7 = 0;
        for (int i8 = 0; i8 < childCount; i8++) {
            View childAt = getChildAt(i8);
            if (childAt.getVisibility() != 8) {
                if (ViewCompat.getLayoutDirection(this) == 1) {
                    int i9 = i5 - i7;
                    childAt.layout(i9 - childAt.getMeasuredWidth(), 0, i9, i6);
                } else {
                    childAt.layout(i7, 0, childAt.getMeasuredWidth() + i7, i6);
                }
                i7 += childAt.getMeasuredWidth();
            }
        }
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        AccessibilityNodeInfoCompat.wrap(accessibilityNodeInfo).setCollectionInfo(AccessibilityNodeInfoCompat.CollectionInfoCompat.obtain(1, this.menu.getVisibleItems().size(), false, 1));
    }

    public void setIconTintList(ColorStateList colorStateList) {
        this.itemIconTint = colorStateList;
        TabBarItemView[] tabBarItemViewArr = this.buttons;
        if (tabBarItemViewArr != null) {
            for (TabBarItemView tabBarItemView : tabBarItemViewArr) {
                tabBarItemView.setIconTintList(colorStateList);
            }
        }
    }

    public ColorStateList getIconTintList() {
        return this.itemIconTint;
    }

    public void setItemIconSize(int i) {
        this.itemIconSize = i;
        TabBarItemView[] tabBarItemViewArr = this.buttons;
        if (tabBarItemViewArr != null) {
            for (TabBarItemView tabBarItemView : tabBarItemViewArr) {
                tabBarItemView.setIconSize(i);
            }
        }
    }

    public int getItemIconSize() {
        return this.itemIconSize;
    }

    public void setItemBackgroundRes(int i) {
        this.itemBackgroundRes = i;
        TabBarItemView[] tabBarItemViewArr = this.buttons;
        if (tabBarItemViewArr != null) {
            for (TabBarItemView tabBarItemView : tabBarItemViewArr) {
                tabBarItemView.setItemBackground(i);
            }
        }
    }

    @Deprecated
    public int getItemBackgroundRes() {
        return this.itemBackgroundRes;
    }

    public void setItemBackground(Drawable drawable) {
        this.itemBackground = drawable;
        TabBarItemView[] tabBarItemViewArr = this.buttons;
        if (tabBarItemViewArr != null) {
            for (TabBarItemView tabBarItemView : tabBarItemViewArr) {
                tabBarItemView.setItemBackground(drawable);
            }
        }
    }

    public Drawable getItemBackground() {
        TabBarItemView[] tabBarItemViewArr = this.buttons;
        if (tabBarItemViewArr == null || tabBarItemViewArr.length <= 0) {
            return this.itemBackground;
        }
        return tabBarItemViewArr[0].getBackground();
    }

    public void setLabelVisibilityMode(int i) {
        this.labelVisibilityMode = i;
    }

    public int getLabelVisibilityMode() {
        return this.labelVisibilityMode;
    }

    public void setItemHorizontalTranslationEnabled(boolean z) {
        this.itemHorizontalTranslationEnabled = z;
    }

    public boolean isItemHorizontalTranslationEnabled() {
        return this.itemHorizontalTranslationEnabled;
    }

    public ColorStateList createDefaultColorStateList(int i) {
        int[] iArr = DISABLED_STATE_SET;
        TypedValue typedValue = new TypedValue();
        if (!getContext().getTheme().resolveAttribute(i, typedValue, true)) {
            return null;
        }
        ColorStateList colorStateList = AppCompatResources.getColorStateList(getContext(), typedValue.resourceId);
        if (!getContext().getTheme().resolveAttribute(R$attr.colorPrimary, typedValue, true)) {
            return null;
        }
        int i2 = typedValue.data;
        int defaultColor = colorStateList.getDefaultColor();
        return new ColorStateList(new int[][]{iArr, CHECKED_STATE_SET, LinearLayout.EMPTY_STATE_SET}, new int[]{colorStateList.getColorForState(iArr, defaultColor), i2, defaultColor});
    }

    public void setPresenter(TabBarPresenter tabBarPresenter) {
        this.presenter = tabBarPresenter;
    }

    public void buildMenuView(boolean z) {
        Drawable drawable;
        removeAllViews();
        TabBarItemView[] tabBarItemViewArr = this.buttons;
        if (tabBarItemViewArr != null) {
            for (TabBarItemView tabBarItemView : tabBarItemViewArr) {
                if (tabBarItemView != null) {
                    this.itemPool.release(tabBarItemView);
                    tabBarItemView.removeBadge();
                }
            }
        }
        if (this.menu.size() == 0) {
            this.selectedItemId = 0;
            this.selectedItemPosition = 0;
            this.buttons = null;
            return;
        }
        removeUnusedBadges();
        this.buttons = new TabBarItemView[this.menu.size()];
        boolean isShifting = isShifting(this.labelVisibilityMode, this.menu.size());
        if (getResources().getConfiguration().orientation == 2) {
            switch (this.menu.getVisibleItems().size()) {
                case 1:
                case 2:
                    drawable = getResources().getDrawable(R$drawable.op_tabbar_land_large);
                    break;
                case 3:
                    drawable = getResources().getDrawable(R$drawable.op_tabbar_land_normal);
                    break;
                case 4:
                case 5:
                case 6:
                    drawable = getResources().getDrawable(R$drawable.op_tabbar_land_short);
                    break;
                default:
                    drawable = getResources().getDrawable(R$drawable.op_tabbar_line_short);
                    break;
            }
        } else {
            int size = this.menu.getVisibleItems().size();
            drawable = (size == 1 || size == 2) ? getResources().getDrawable(R$drawable.op_tabbar_land_short) : size != 3 ? size != 4 ? getResources().getDrawable(R$drawable.op_tabbar_line_min_short) : getResources().getDrawable(R$drawable.op_tabbar_line_short) : getResources().getDrawable(R$drawable.op_tabbar_line_three);
        }
        for (int i = 0; i < this.menu.size(); i++) {
            this.presenter.setUpdateSuspended(true);
            this.menu.getItem(i).setCheckable(true);
            this.presenter.setUpdateSuspended(false);
            TabBarItemView newItem = getNewItem();
            this.buttons[i] = newItem;
            newItem.setIconTintList(this.itemIconTint);
            newItem.setIconSize(this.itemIconSize);
            Drawable drawable2 = this.itemBackground;
            if (drawable2 != null) {
                newItem.setItemBackground(drawable2);
            } else {
                newItem.setItemBackground(this.itemBackgroundRes);
            }
            newItem.setShifting(isShifting);
            newItem.setLabelVisibilityMode(this.labelVisibilityMode);
            newItem.setItemPosition(i);
            if (z) {
                newItem.setLineDrawable(drawable);
                newItem.setDisplayAnimation(this.mDisplayAnim);
                newItem.initialize((MenuItemImpl) this.menu.getItem(i), 1);
            } else {
                newItem.initialize((MenuItemImpl) this.menu.getItem(i), 0);
            }
            newItem.setOnClickListener(this.onClickListener);
            if (this.selectedItemId != 0 && this.menu.getItem(i).getItemId() == this.selectedItemId) {
                this.selectedItemPosition = i;
            }
            setBadgeIfNeeded(newItem);
            if (this.menu.size() <= 6 || getResources().getConfiguration().orientation != 1) {
                addView(newItem, new LinearLayout.LayoutParams(-2, -2));
            } else {
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-2, -2);
                layoutParams.width = 0;
                layoutParams.weight = 1.0f;
                addView(newItem, layoutParams);
            }
        }
        int min = Math.min(this.menu.size() - 1, this.selectedItemPosition);
        this.selectedItemPosition = min;
        this.menu.getItem(min).setChecked(true);
    }

    public void updateMenuView() {
        MenuBuilder menuBuilder = this.menu;
        if (!(menuBuilder == null || this.buttons == null)) {
            int size = menuBuilder.size();
            if (size != this.buttons.length) {
                buildMenuView(false);
                return;
            }
            int i = this.selectedItemId;
            for (int i2 = 0; i2 < size; i2++) {
                MenuItem item = this.menu.getItem(i2);
                if (item.isChecked()) {
                    this.selectedItemId = item.getItemId();
                    this.selectedItemPosition = i2;
                }
            }
            if (i != this.selectedItemId) {
                TransitionManager.beginDelayedTransition(this, this.set);
            }
            boolean isShifting = isShifting(this.labelVisibilityMode, this.menu.getVisibleItems().size());
            for (int i3 = 0; i3 < size; i3++) {
                this.presenter.setUpdateSuspended(true);
                this.buttons[i3].setLabelVisibilityMode(this.labelVisibilityMode);
                this.buttons[i3].setShifting(isShifting);
                this.buttons[i3].initialize((MenuItemImpl) this.menu.getItem(i3), 0);
                this.presenter.setUpdateSuspended(false);
            }
        }
    }

    private TabBarItemView getNewItem() {
        TabBarItemView acquire = this.itemPool.acquire();
        return acquire == null ? new TabBarItemView(getContext()) : acquire;
    }

    public int getSelectedItemId() {
        return this.selectedItemId;
    }

    public void setSelectedItemId(int i) {
        this.selectedItemId = i;
    }

    private boolean isShifting(int i, int i2) {
        if (i == -1) {
            if (i2 <= 6 || getResources().getConfiguration().orientation != 1) {
                return false;
            }
        } else if (i != 0) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: package-private */
    public void tryRestoreSelectedItemId(int i) {
        int size = this.menu.size();
        for (int i2 = 0; i2 < size; i2++) {
            MenuItem item = this.menu.getItem(i2);
            if (i == item.getItemId()) {
                this.selectedItemId = i;
                this.selectedItemPosition = i2;
                item.setChecked(true);
                return;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public SparseArray<BadgeDrawable> getBadgeDrawables() {
        return this.badgeDrawables;
    }

    /* access modifiers changed from: package-private */
    public void setBadgeDrawables(SparseArray<BadgeDrawable> sparseArray) {
        this.badgeDrawables = sparseArray;
        TabBarItemView[] tabBarItemViewArr = this.buttons;
        if (tabBarItemViewArr != null) {
            for (TabBarItemView tabBarItemView : tabBarItemViewArr) {
                tabBarItemView.setBadge(sparseArray.get(tabBarItemView.getId()));
            }
        }
    }

    private void setBadgeIfNeeded(TabBarItemView tabBarItemView) {
        BadgeDrawable badgeDrawable;
        int id = tabBarItemView.getId();
        if (isValidId(id) && (badgeDrawable = this.badgeDrawables.get(id)) != null) {
            tabBarItemView.setBadge(badgeDrawable);
        }
    }

    private void removeUnusedBadges() {
        HashSet hashSet = new HashSet();
        for (int i = 0; i < this.menu.size(); i++) {
            hashSet.add(Integer.valueOf(this.menu.getItem(i).getItemId()));
        }
        for (int i2 = 0; i2 < this.badgeDrawables.size(); i2++) {
            int keyAt = this.badgeDrawables.keyAt(i2);
            if (!hashSet.contains(Integer.valueOf(keyAt))) {
                this.badgeDrawables.delete(keyAt);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public TabBarItemView findItemView(int i) {
        validateMenuItemId(i);
        TabBarItemView[] tabBarItemViewArr = this.buttons;
        if (tabBarItemViewArr == null) {
            return null;
        }
        for (TabBarItemView tabBarItemView : tabBarItemViewArr) {
            if (tabBarItemView.getId() == i) {
                return tabBarItemView;
            }
        }
        return null;
    }

    private void validateMenuItemId(int i) {
        if (!isValidId(i)) {
            throw new IllegalArgumentException(i + " is not a valid view id");
        }
    }

    public void displayAnim(boolean z) {
        this.mDisplayAnim = z;
    }
}
