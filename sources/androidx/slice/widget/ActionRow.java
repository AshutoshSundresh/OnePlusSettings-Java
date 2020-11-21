package androidx.slice.widget;

import android.app.PendingIntent;
import android.app.RemoteInput;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.graphics.drawable.IconCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.slice.SliceItem;
import androidx.slice.core.SliceAction;
import androidx.slice.core.SliceActionImpl;
import androidx.slice.core.SliceQuery;
import java.util.Iterator;
import java.util.List;

public class ActionRow extends FrameLayout {
    private final LinearLayout mActionsGroup;
    private int mColor = -16777216;
    private final int mIconPadding;
    private final int mSize;

    public ActionRow(Context context, boolean z) {
        super(context);
        this.mSize = (int) TypedValue.applyDimension(1, 48.0f, context.getResources().getDisplayMetrics());
        this.mIconPadding = (int) TypedValue.applyDimension(1, 12.0f, context.getResources().getDisplayMetrics());
        LinearLayout linearLayout = new LinearLayout(context);
        this.mActionsGroup = linearLayout;
        linearLayout.setOrientation(0);
        this.mActionsGroup.setLayoutParams(new FrameLayout.LayoutParams(-1, -2));
        addView(this.mActionsGroup);
    }

    private void setColor(int i) {
        this.mColor = i;
        for (int i2 = 0; i2 < this.mActionsGroup.getChildCount(); i2++) {
            View childAt = this.mActionsGroup.getChildAt(i2);
            if (((Integer) childAt.getTag()).intValue() == 0) {
                ImageViewCompat.setImageTintList((ImageView) childAt, ColorStateList.valueOf(this.mColor));
            }
        }
    }

    private ImageView addAction(IconCompat iconCompat, boolean z) {
        ImageView imageView = new ImageView(getContext());
        int i = this.mIconPadding;
        imageView.setPadding(i, i, i, i);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setImageDrawable(iconCompat.loadDrawable(getContext()));
        if (z) {
            ImageViewCompat.setImageTintList(imageView, ColorStateList.valueOf(this.mColor));
        }
        imageView.setBackground(SliceViewUtil.getDrawable(getContext(), 16843534));
        imageView.setTag(Boolean.valueOf(z));
        addAction(imageView);
        return imageView;
    }

    public void setActions(List<SliceAction> list, int i) {
        IconCompat icon;
        removeAllViews();
        this.mActionsGroup.removeAllViews();
        addView(this.mActionsGroup);
        if (i != -1) {
            setColor(i);
        }
        Iterator<SliceAction> it = list.iterator();
        while (true) {
            int i2 = 0;
            boolean z = false;
            if (it.hasNext()) {
                SliceAction next = it.next();
                if (this.mActionsGroup.getChildCount() < 5) {
                    SliceActionImpl sliceActionImpl = (SliceActionImpl) next;
                    SliceItem sliceItem = sliceActionImpl.getSliceItem();
                    final SliceItem actionItem = sliceActionImpl.getActionItem();
                    SliceItem find = SliceQuery.find(sliceItem, "input");
                    SliceItem find2 = SliceQuery.find(sliceItem, "image");
                    if (find == null || find2 == null) {
                        if (!(next.getIcon() == null || (icon = next.getIcon()) == null || actionItem == null)) {
                            if (next.getImageMode() == 0) {
                                z = true;
                            }
                            addAction(icon, z).setOnClickListener(new View.OnClickListener(this) {
                                /* class androidx.slice.widget.ActionRow.AnonymousClass1 */

                                public void onClick(View view) {
                                    try {
                                        actionItem.fireAction(null, null);
                                    } catch (PendingIntent.CanceledException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    } else if (Build.VERSION.SDK_INT >= 21) {
                        handleSetRemoteInputActions(find, find2, actionItem);
                    } else {
                        Log.w("ActionRow", "Received RemoteInput on API <20 " + find);
                    }
                } else {
                    return;
                }
            } else {
                if (getChildCount() == 0) {
                    i2 = 8;
                }
                setVisibility(i2);
                return;
            }
        }
    }

    private void addAction(View view) {
        LinearLayout linearLayout = this.mActionsGroup;
        int i = this.mSize;
        linearLayout.addView(view, new LinearLayout.LayoutParams(i, i, 1.0f));
    }

    private void handleSetRemoteInputActions(final SliceItem sliceItem, SliceItem sliceItem2, final SliceItem sliceItem3) {
        if (sliceItem.getRemoteInput().getAllowFreeFormInput()) {
            addAction(sliceItem2.getIcon(), !sliceItem2.hasHint("no_tint")).setOnClickListener(new View.OnClickListener() {
                /* class androidx.slice.widget.ActionRow.AnonymousClass2 */

                public void onClick(View view) {
                    ActionRow.this.handleRemoteInputClick(view, sliceItem3, sliceItem.getRemoteInput());
                }
            });
            createRemoteInputView(this.mColor, getContext());
        }
    }

    private void createRemoteInputView(int i, Context context) {
        RemoteInputView inflate = RemoteInputView.inflate(context, this);
        inflate.setVisibility(4);
        addView(inflate, new FrameLayout.LayoutParams(-1, -1));
        inflate.setBackgroundColor(i);
    }

    /* access modifiers changed from: package-private */
    public boolean handleRemoteInputClick(View view, SliceItem sliceItem, RemoteInput remoteInput) {
        if (remoteInput == null) {
            return false;
        }
        ViewParent parent = view.getParent().getParent();
        RemoteInputView remoteInputView = null;
        while (parent != null && (!(parent instanceof View) || (remoteInputView = findRemoteInputView((View) parent)) == null)) {
            parent = parent.getParent();
        }
        if (remoteInputView == null) {
            return false;
        }
        int width = view.getWidth();
        if (view instanceof TextView) {
            TextView textView = (TextView) view;
            if (textView.getLayout() != null) {
                width = Math.min(width, ((int) textView.getLayout().getLineWidth(0)) + textView.getCompoundPaddingLeft() + textView.getCompoundPaddingRight());
            }
        }
        int left = view.getLeft() + (width / 2);
        int top = view.getTop() + (view.getHeight() / 2);
        int width2 = remoteInputView.getWidth();
        int height = remoteInputView.getHeight() - top;
        int i = width2 - left;
        remoteInputView.setRevealParameters(left, top, Math.max(Math.max(left + top, left + height), Math.max(i + top, i + height)));
        remoteInputView.setAction(sliceItem);
        remoteInputView.setRemoteInput(new RemoteInput[]{remoteInput}, remoteInput);
        remoteInputView.focusAnimated();
        return true;
    }

    private RemoteInputView findRemoteInputView(View view) {
        if (view == null) {
            return null;
        }
        return (RemoteInputView) view.findViewWithTag(RemoteInputView.VIEW_TAG);
    }
}
