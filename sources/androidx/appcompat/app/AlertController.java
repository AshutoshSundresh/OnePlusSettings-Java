package androidx.appcompat.app;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewStub;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.CursorAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import androidx.appcompat.R$attr;
import androidx.appcompat.R$color;
import androidx.appcompat.R$dimen;
import androidx.appcompat.R$drawable;
import androidx.appcompat.R$id;
import androidx.appcompat.R$layout;
import androidx.appcompat.R$style;
import androidx.appcompat.R$styleable;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.SmoothRoundLayout;
import androidx.core.widget.NestedScrollView;
import java.lang.ref.WeakReference;

/* access modifiers changed from: package-private */
public class AlertController {
    private FrameLayout dialogImageViewLayout;
    ListAdapter mAdapter;
    private int mAlertDialogLayout;
    public boolean mBottomShow;
    private final View.OnClickListener mButtonHandler = new View.OnClickListener() {
        /* class androidx.appcompat.app.AlertController.AnonymousClass1 */
        private long curTime;

        public void onClick(View view) {
            Message message;
            Message message2;
            Message message3;
            Message message4;
            if (Math.abs(this.curTime - System.currentTimeMillis()) < 120) {
                Log.d("AlertController", "double click");
                return;
            }
            AlertController alertController = AlertController.this;
            if (view != alertController.mButtonPositive || (message4 = alertController.mButtonPositiveMessage) == null) {
                AlertController alertController2 = AlertController.this;
                if (view != alertController2.mButtonNegative || (message3 = alertController2.mButtonNegativeMessage) == null) {
                    AlertController alertController3 = AlertController.this;
                    message = (view != alertController3.mButtonNeutral || (message2 = alertController3.mButtonNeutralMessage) == null) ? null : Message.obtain(message2);
                } else {
                    message = Message.obtain(message3);
                }
            } else {
                message = Message.obtain(message4);
            }
            if (message != null) {
                AlertController.this.mHandler.sendMessageDelayed(message, 120);
            }
            AlertController alertController4 = AlertController.this;
            AlertController.this.mHandler.sendMessageDelayed(alertController4.mHandler.obtainMessage(1, alertController4.mDialog), 120);
            this.curTime = System.currentTimeMillis();
        }
    };
    Button mButtonNegative;
    Message mButtonNegativeMessage;
    private CharSequence mButtonNegativeText;
    Button mButtonNeutral;
    Message mButtonNeutralMessage;
    private CharSequence mButtonNeutralText;
    Button mButtonPositive;
    Message mButtonPositiveMessage;
    private CharSequence mButtonPositiveText;
    int mCheckedItem = -1;
    private final Context mContext;
    private Drawable mCustomImage;
    private FrameLayout mCustomImageLayout;
    private ImageView mCustomImageView;
    private View mCustomTitleView;
    final AppCompatDialog mDialog;
    Handler mHandler;
    private Drawable mIcon;
    private int mIconId = 0;
    private ImageView mIconView;
    int mListItemLayout;
    int mListLayout;
    ListView mListView;
    private CharSequence mMessage;
    private TextView mMessageView;
    int mMultiChoiceItemLayout;
    private int mProgressStyle = -1;
    NestedScrollView mScrollView;
    private boolean mShowTitle;
    int mSingleChoiceItemLayout;
    private CharSequence mTitle;
    private TextView mTitleView;
    private View mView;
    private int mViewLayoutResId;
    private int mViewSpacingBottom;
    private int mViewSpacingLeft;
    private int mViewSpacingRight;
    private boolean mViewSpacingSpecified = false;
    private int mViewSpacingTop;
    private final Window mWindow;

    private static final class ButtonHandler extends Handler {
        private WeakReference<DialogInterface> mDialog;

        public ButtonHandler(DialogInterface dialogInterface) {
            this.mDialog = new WeakReference<>(dialogInterface);
        }

        public void handleMessage(Message message) {
            int i = message.what;
            if (i == -3 || i == -2 || i == -1) {
                ((DialogInterface.OnClickListener) message.obj).onClick(this.mDialog.get(), message.what);
            } else if (i == 1) {
                ((DialogInterface) message.obj).dismiss();
            }
        }
    }

    public AlertController(Context context, AppCompatDialog appCompatDialog, Window window) {
        this.mContext = context;
        this.mDialog = appCompatDialog;
        this.mWindow = window;
        this.mHandler = new ButtonHandler(appCompatDialog);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(null, R$styleable.AlertDialog, R$attr.alertDialogStyle, 0);
        this.mAlertDialogLayout = obtainStyledAttributes.getResourceId(R$styleable.AlertDialog_android_layout, R$layout.op_alert_dialog_material);
        this.mListLayout = obtainStyledAttributes.getResourceId(R$styleable.AlertDialog_listLayout, R$layout.op_select_dialog_material);
        this.mMultiChoiceItemLayout = obtainStyledAttributes.getResourceId(R$styleable.AlertDialog_multiChoiceItemLayout, R$layout.op_select_dialog_multichoice_material);
        this.mSingleChoiceItemLayout = obtainStyledAttributes.getResourceId(R$styleable.AlertDialog_singleChoiceItemLayout, R$layout.op_select_dialog_singlechoice_material);
        this.mListItemLayout = obtainStyledAttributes.getResourceId(R$styleable.AlertDialog_listItemLayout, R$layout.op_select_dialog_item_material);
        this.mShowTitle = obtainStyledAttributes.getBoolean(R$styleable.AlertDialog_showTitle, true);
        obtainStyledAttributes.getDimensionPixelSize(R$styleable.AlertDialog_buttonIconDimen, 0);
        obtainStyledAttributes.recycle();
        appCompatDialog.supportRequestWindowFeature(1);
    }

    static boolean canTextInput(View view) {
        if (view.onCheckIsTextEditor()) {
            return true;
        }
        if (!(view instanceof ViewGroup)) {
            return false;
        }
        ViewGroup viewGroup = (ViewGroup) view;
        int childCount = viewGroup.getChildCount();
        while (childCount > 0) {
            childCount--;
            if (canTextInput(viewGroup.getChildAt(childCount))) {
                return true;
            }
        }
        return false;
    }

    public void installContent() {
        this.mWindow.requestFeature(1);
        this.mWindow.setContentView(this.mAlertDialogLayout);
        if (!this.mBottomShow) {
            this.mWindow.setWindowAnimations(R$style.Oneplus_popup_normal_animation);
            if (this.mWindow.findViewById(R$id.parentPanel).getParent() != null && (this.mWindow.findViewById(R$id.parentPanel).getParent() instanceof SmoothRoundLayout)) {
                ((SmoothRoundLayout) this.mWindow.findViewById(R$id.parentPanel).getParent()).setCornerRadius((float) this.mContext.getResources().getDimensionPixelOffset(R$dimen.op_control_radius_r12));
            }
        } else {
            showInBottom(true);
        }
        setupView();
        setupDecor();
    }

    public void setCustomImage(Drawable drawable) {
        this.mCustomImage = drawable;
        updateImage();
    }

    public void updateImageLayout() {
        FrameLayout frameLayout;
        if (this.dialogImageViewLayout != null && (frameLayout = this.mCustomImageLayout) != null && frameLayout.getChildCount() == 0) {
            this.dialogImageViewLayout.setLayoutParams(this.mCustomImageLayout.getLayoutParams());
            this.mCustomImageLayout.addView(this.dialogImageViewLayout);
            this.mCustomImageLayout.setVisibility(0);
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void setCustomLayout(FrameLayout frameLayout) {
        this.dialogImageViewLayout = frameLayout;
        updateImageLayout();
    }

    public void setTitle(CharSequence charSequence) {
        this.mTitle = charSequence;
        TextView textView = this.mTitleView;
        if (textView != null) {
            textView.setText(charSequence);
        }
    }

    public void setCustomTitle(View view) {
        this.mCustomTitleView = view;
    }

    public void setMessage(CharSequence charSequence) {
        this.mMessage = charSequence;
        TextView textView = this.mMessageView;
        if (textView != null) {
            textView.setText(charSequence);
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void showInBottom(boolean z) {
        this.mBottomShow = z;
        if (z) {
            WindowManager.LayoutParams attributes = this.mWindow.getAttributes();
            attributes.width = -1;
            if (this.mWindow.findViewById(R$id.parentPanel) != null) {
                View findViewById = this.mWindow.findViewById(R$id.parentPanel);
                View findViewById2 = findViewById.findViewById(R$id.topPanel);
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(findViewById.getLayoutParams());
                layoutParams.topMargin = this.mContext.getResources().getDimensionPixelOffset(R$dimen.op_control_margin_space2);
                findViewById.setLayoutParams(layoutParams);
                LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(findViewById2.getLayoutParams());
                layoutParams2.topMargin = this.mContext.getResources().getDimensionPixelOffset(R$dimen.op_control_margin_space3);
                layoutParams2.bottomMargin = this.mContext.getResources().getDimensionPixelOffset(R$dimen.op_control_margin_space3);
                layoutParams2.leftMargin = this.mContext.getResources().getDimensionPixelOffset(R$dimen.op_control_margin_screen_left3);
                layoutParams2.rightMargin = this.mContext.getResources().getDimensionPixelOffset(R$dimen.op_control_margin_screen_right3);
                findViewById2.setLayoutParams(layoutParams2);
            }
            this.mWindow.setAttributes(attributes);
            this.mWindow.setGravity(80);
            this.mWindow.setBackgroundDrawableResource(R$drawable.op_dialog_material_background_bottom);
            this.mWindow.setWindowAnimations(R$style.Oneplus_popup_bottom_animation);
        }
    }

    public void setView(int i) {
        this.mView = null;
        this.mViewLayoutResId = i;
        this.mViewSpacingSpecified = false;
    }

    public void setView(View view) {
        this.mView = view;
        this.mViewLayoutResId = 0;
        this.mViewSpacingSpecified = false;
    }

    public void setView(View view, int i, int i2, int i3, int i4) {
        this.mView = view;
        this.mViewLayoutResId = 0;
        this.mViewSpacingSpecified = true;
        this.mViewSpacingLeft = i;
        this.mViewSpacingTop = i2;
        this.mViewSpacingRight = i3;
        this.mViewSpacingBottom = i4;
    }

    public void setDestory() {
        this.mButtonPositiveMessage = null;
        this.mButtonNegativeMessage = null;
        this.mButtonNeutralMessage = null;
        this.mButtonPositive.setOnClickListener(null);
        this.mButtonNegative.setOnClickListener(null);
        this.mButtonNeutral.setOnClickListener(null);
        ListView listView = this.mListView;
        if (listView != null) {
            listView.setAdapter((ListAdapter) null);
            this.mListView.setOnItemClickListener(null);
            this.mListView.setOnItemSelectedListener(null);
            this.mListView = null;
        }
        this.dialogImageViewLayout = null;
        this.mCustomImageLayout = null;
        this.mAdapter = null;
    }

    public void setButton(int i, CharSequence charSequence, DialogInterface.OnClickListener onClickListener, Message message, Drawable drawable) {
        if (message == null && onClickListener != null) {
            message = this.mHandler.obtainMessage(i, onClickListener);
        }
        if (i == -3) {
            this.mButtonNeutralText = charSequence;
            this.mButtonNeutralMessage = message;
        } else if (i == -2) {
            this.mButtonNegativeText = charSequence;
            this.mButtonNegativeMessage = message;
        } else if (i == -1) {
            this.mButtonPositiveText = charSequence;
            this.mButtonPositiveMessage = message;
        } else {
            throw new IllegalArgumentException("Button does not exist");
        }
    }

    public void setIcon(int i) {
        this.mIcon = null;
        this.mIconId = i;
        ImageView imageView = this.mIconView;
        if (imageView == null) {
            return;
        }
        if (i != 0) {
            imageView.setVisibility(0);
            this.mIconView.setImageResource(this.mIconId);
            return;
        }
        imageView.setVisibility(8);
    }

    public void setIcon(Drawable drawable) {
        this.mIcon = drawable;
        this.mIconId = 0;
        ImageView imageView = this.mIconView;
        if (imageView == null) {
            return;
        }
        if (drawable != null) {
            imageView.setVisibility(0);
            this.mIconView.setImageDrawable(drawable);
            return;
        }
        imageView.setVisibility(8);
    }

    public int getIconAttributeResId(int i) {
        TypedValue typedValue = new TypedValue();
        this.mContext.getTheme().resolveAttribute(i, typedValue, true);
        return typedValue.resourceId;
    }

    public ListView getListView() {
        return this.mListView;
    }

    public Button getButton(int i) {
        if (i == -3) {
            return this.mButtonNeutral;
        }
        if (i == -2) {
            return this.mButtonNegative;
        }
        if (i != -1) {
            return null;
        }
        return this.mButtonPositive;
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        NestedScrollView nestedScrollView = this.mScrollView;
        return nestedScrollView != null && nestedScrollView.executeKeyEvent(keyEvent);
    }

    public boolean onKeyUp(int i, KeyEvent keyEvent) {
        NestedScrollView nestedScrollView = this.mScrollView;
        return nestedScrollView != null && nestedScrollView.executeKeyEvent(keyEvent);
    }

    private ViewGroup resolvePanel(View view, View view2) {
        if (view == null) {
            if (view2 instanceof ViewStub) {
                view2 = ((ViewStub) view2).inflate();
            }
            return (ViewGroup) view2;
        }
        if (view2 != null) {
            ViewParent parent = view2.getParent();
            if (parent instanceof ViewGroup) {
                ((ViewGroup) parent).removeView(view2);
            }
        }
        if (view instanceof ViewStub) {
            view = ((ViewStub) view).inflate();
        }
        return (ViewGroup) view;
    }

    private void setupView() {
        ListAdapter listAdapter;
        NestedScrollView nestedScrollView;
        View findViewById;
        View findViewById2 = this.mWindow.findViewById(R$id.parentPanel);
        this.mCustomImageView = (ImageView) findViewById2.findViewById(R$id.imagePanel);
        this.mCustomImageLayout = (FrameLayout) findViewById2.findViewById(R$id.imageLayoutPanel);
        View findViewById3 = findViewById2.findViewById(R$id.topPanel);
        View findViewById4 = findViewById2.findViewById(R$id.contentPanel);
        View findViewById5 = findViewById2.findViewById(R$id.buttonPanel);
        ViewGroup viewGroup = (ViewGroup) findViewById2.findViewById(R$id.customPanel);
        setupCustomContent(viewGroup);
        View findViewById6 = viewGroup.findViewById(R$id.topPanel);
        View findViewById7 = viewGroup.findViewById(R$id.contentPanel);
        View findViewById8 = viewGroup.findViewById(R$id.buttonPanel);
        ViewGroup resolvePanel = resolvePanel(findViewById6, findViewById3);
        ViewGroup resolvePanel2 = resolvePanel(findViewById7, findViewById4);
        ViewGroup resolvePanel3 = resolvePanel(findViewById8, findViewById5);
        updateImage();
        updateImageLayout();
        setupContent(resolvePanel2);
        setupButtons(resolvePanel3);
        setupTitle(resolvePanel);
        int i = 0;
        boolean z = (viewGroup == null || viewGroup.getVisibility() == 8) ? false : true;
        boolean z2 = (resolvePanel == null || resolvePanel.getVisibility() == 8) ? false : true;
        boolean z3 = (resolvePanel3 == null || resolvePanel3.getVisibility() == 8) ? false : true;
        if (!z3 && resolvePanel2 != null && (findViewById = resolvePanel2.findViewById(R$id.textSpacerNoButtons)) != null && TextUtils.isEmpty(this.mTitle)) {
            findViewById.setVisibility(0);
        }
        if (z2 && (nestedScrollView = this.mScrollView) != null) {
            nestedScrollView.setClipToPadding(true);
        }
        ListView listView = this.mListView;
        if (listView instanceof RecycleListView) {
            ((RecycleListView) listView).setHasDecor(z2, z3);
        }
        if (!z) {
            View view = this.mListView;
            if (view == null) {
                view = this.mScrollView;
            }
            if (view != null) {
                if (z3) {
                    i = 2;
                }
                int i2 = z2 ? 1 : 0;
                char c = z2 ? 1 : 0;
                char c2 = z2 ? 1 : 0;
                int i3 = i | i2;
                if (Build.VERSION.SDK_INT >= 23) {
                    view.setScrollIndicators(i3, 3);
                }
            }
        }
        ListView listView2 = this.mListView;
        if (!(listView2 == null || (listAdapter = this.mAdapter) == null)) {
            listView2.setAdapter(listAdapter);
            int i4 = this.mCheckedItem;
            if (i4 > -1) {
                listView2.setItemChecked(i4, true);
                listView2.setSelection(i4);
            }
        }
        setBackground(resolvePanel, resolvePanel2, viewGroup, resolvePanel3, z2, z, z3);
    }

    private void setupDecor() {
        View decorView = this.mWindow.getDecorView();
        final View findViewById = this.mWindow.findViewById(R$id.parentPanel);
        if (findViewById != null && decorView != null && Build.VERSION.SDK_INT >= 20) {
            decorView.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
                /* class androidx.appcompat.app.AlertController.AnonymousClass2 */

                public WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
                    if (windowInsets.isRound()) {
                        int dimensionPixelOffset = AlertController.this.mContext.getResources().getDimensionPixelOffset(R$dimen.oneplus_alert_dialog_round_padding);
                        findViewById.setPadding(dimensionPixelOffset, dimensionPixelOffset, dimensionPixelOffset, dimensionPixelOffset);
                    }
                    return windowInsets.consumeSystemWindowInsets();
                }
            });
            decorView.setFitsSystemWindows(true);
            decorView.requestApplyInsets();
        }
    }

    private void setupCustomContent(ViewGroup viewGroup) {
        View view = this.mView;
        boolean z = false;
        if (view == null) {
            view = this.mViewLayoutResId != 0 ? LayoutInflater.from(this.mContext).inflate(this.mViewLayoutResId, viewGroup, false) : null;
        }
        if (view != null) {
            z = true;
        }
        if (!z || !canTextInput(view)) {
            this.mWindow.setFlags(131072, 131072);
        }
        if (z) {
            FrameLayout frameLayout = (FrameLayout) this.mWindow.findViewById(16908331);
            if (frameLayout != null) {
                frameLayout.addView(view, new ViewGroup.LayoutParams(-1, -1));
                if (this.mViewSpacingSpecified) {
                    frameLayout.setPadding(this.mViewSpacingLeft, this.mViewSpacingTop, this.mViewSpacingRight, this.mViewSpacingBottom);
                }
                if (this.mListView != null) {
                    ((LinearLayoutCompat.LayoutParams) viewGroup.getLayoutParams()).weight = 0.0f;
                }
                View findViewById = this.mWindow.findViewById(R$id.layoutPanel);
                if (findViewById == null) {
                    return;
                }
                if (!TextUtils.isEmpty(this.mMessage)) {
                    ((LinearLayoutCompat.LayoutParams) findViewById.getLayoutParams()).weight = 100.0f;
                } else {
                    ((LinearLayoutCompat.LayoutParams) findViewById.getLayoutParams()).weight = 0.0f;
                }
            }
        } else {
            viewGroup.setVisibility(8);
        }
    }

    private void setupTitle(ViewGroup viewGroup) {
        if (this.mCustomTitleView != null) {
            viewGroup.addView(this.mCustomTitleView, 0, new ViewGroup.LayoutParams(-1, -2));
            this.mWindow.findViewById(R$id.title_template).setVisibility(8);
            return;
        }
        this.mIconView = (ImageView) this.mWindow.findViewById(16908294);
        LinearLayout linearLayout = (LinearLayout) this.mWindow.findViewById(R$id.title_template);
        if (!(!TextUtils.isEmpty(this.mTitle)) || !this.mShowTitle) {
            this.mWindow.findViewById(R$id.title_template).setVisibility(8);
            this.mIconView.setVisibility(8);
            viewGroup.setVisibility(8);
            return;
        }
        TextView textView = (TextView) this.mWindow.findViewById(R$id.alertTitle);
        this.mTitleView = textView;
        if (this.mBottomShow) {
            textView.setTextAppearance(R$style.op_control_text_style_h5);
            this.mTitleView.setTextColor(this.mContext.getResources().getColor(R$color.op_control_text_color_primary_default));
        }
        if (!TextUtils.isEmpty(this.mMessage)) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.mMessageView.getLayoutParams();
            layoutParams.bottomMargin = this.mContext.getResources().getDimensionPixelOffset(R$dimen.op_control_margin_space4);
            this.mMessageView.setLayoutParams(layoutParams);
        } else if (!this.mBottomShow) {
            LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) this.mTitleView.getLayoutParams();
            layoutParams2.bottomMargin = this.mContext.getResources().getDimensionPixelOffset(R$dimen.op_control_margin_space4);
            this.mTitleView.setLayoutParams(layoutParams2);
        }
        this.mTitleView.setText(this.mTitle);
        int i = this.mIconId;
        if (i != 0) {
            this.mIconView.setImageResource(i);
            return;
        }
        Drawable drawable = this.mIcon;
        if (drawable != null) {
            this.mIconView.setImageDrawable(drawable);
            return;
        }
        this.mTitleView.setPadding(this.mIconView.getPaddingLeft(), this.mIconView.getPaddingTop(), this.mIconView.getPaddingRight(), this.mIconView.getPaddingBottom());
        this.mIconView.setVisibility(8);
    }

    private void updateImage() {
        ImageView imageView = this.mCustomImageView;
        if (imageView != null) {
            Drawable drawable = this.mCustomImage;
            if (drawable != null) {
                imageView.setImageDrawable(drawable);
                this.mCustomImageView.setVisibility(0);
                if (this.mWindow.findViewById(R$id.parentPanel).getParent() != null && (this.mWindow.findViewById(R$id.parentPanel).getParent() instanceof SmoothRoundLayout)) {
                    ((SmoothRoundLayout) this.mWindow.findViewById(R$id.parentPanel).getParent()).setCornerRadius((float) this.mContext.getResources().getDimensionPixelOffset(R$dimen.op_control_radius_r12));
                    return;
                }
                return;
            }
            imageView.setVisibility(8);
        }
    }

    private void setupContent(ViewGroup viewGroup) {
        NestedScrollView nestedScrollView = (NestedScrollView) viewGroup.findViewById(R$id.scrollView);
        this.mScrollView = nestedScrollView;
        nestedScrollView.setFocusable(false);
        this.mScrollView.setNestedScrollingEnabled(false);
        TextView textView = (TextView) viewGroup.findViewById(16908299);
        this.mMessageView = textView;
        if (textView != null) {
            CharSequence charSequence = this.mMessage;
            if (charSequence != null) {
                textView.setText(charSequence);
                return;
            }
            textView.setVisibility(8);
            this.mScrollView.removeView(this.mMessageView);
            if (this.mListView != null) {
                ViewGroup viewGroup2 = (ViewGroup) this.mScrollView.getParent();
                int indexOfChild = viewGroup2.indexOfChild(this.mScrollView);
                viewGroup2.removeViewAt(indexOfChild);
                viewGroup2.addView(this.mListView, indexOfChild, new ViewGroup.LayoutParams(-1, -1));
                return;
            }
            viewGroup.setVisibility(8);
        }
    }

    private void setupButtons(ViewGroup viewGroup) {
        boolean z;
        boolean z2 = true;
        boolean z3 = this.mContext.obtainStyledAttributes(new int[]{R$attr.isLightTheme}).getBoolean(0, true);
        Button button = (Button) viewGroup.findViewById(16908313);
        this.mButtonPositive = button;
        button.setOnClickListener(this.mButtonHandler);
        if (!z3) {
            this.mButtonPositive.setBackgroundResource(R$drawable.op_btn_borderless_mini_material_dark);
        }
        if (TextUtils.isEmpty(this.mButtonPositiveText)) {
            this.mButtonPositive.setVisibility(8);
            z = false;
        } else {
            this.mButtonPositive.setText(this.mButtonPositiveText);
            this.mButtonPositive.setVisibility(0);
            z = true;
        }
        Button button2 = (Button) viewGroup.findViewById(16908314);
        this.mButtonNegative = button2;
        button2.setOnClickListener(this.mButtonHandler);
        if (!z3) {
            this.mButtonNegative.setBackgroundResource(R$drawable.op_btn_borderless_mini_material_dark);
        }
        if (TextUtils.isEmpty(this.mButtonNegativeText)) {
            this.mButtonNegative.setVisibility(8);
        } else {
            this.mButtonNegative.setText(this.mButtonNegativeText);
            this.mButtonNegative.setVisibility(0);
            z |= true;
        }
        Button button3 = (Button) viewGroup.findViewById(16908315);
        this.mButtonNeutral = button3;
        button3.setOnClickListener(this.mButtonHandler);
        if (!z3) {
            this.mButtonNeutral.setBackgroundResource(R$drawable.op_btn_borderless_mini_material_dark);
        }
        if (TextUtils.isEmpty(this.mButtonNeutralText)) {
            this.mButtonNeutral.setVisibility(8);
        } else {
            this.mButtonNeutral.setText(this.mButtonNeutralText);
            this.mButtonNeutral.setVisibility(0);
            z |= true;
        }
        if (z) {
            centerButton(this.mButtonPositive);
        } else if (z) {
            centerButton(this.mButtonNegative);
        } else if (z) {
            centerButton(this.mButtonNeutral);
        }
        if (!z) {
            z2 = false;
        }
        if (!z2) {
            viewGroup.setVisibility(8);
        }
    }

    private void centerButton(Button button) {
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) button.getLayoutParams();
        layoutParams.gravity = 1;
        layoutParams.weight = 0.5f;
        button.setLayoutParams(layoutParams);
    }

    private void setBackground(View view, View view2, View view3, View view4, boolean z, boolean z2, boolean z3) {
        int i;
        ListAdapter listAdapter;
        View[] viewArr = new View[4];
        boolean[] zArr = new boolean[4];
        if (z) {
            viewArr[0] = view;
            zArr[0] = false;
            i = 1;
        } else {
            i = 0;
        }
        View view5 = null;
        if (view2.getVisibility() == 8) {
            view2 = null;
        }
        viewArr[i] = view2;
        zArr[i] = this.mListView != null;
        int i2 = i + 1;
        if (z2) {
            viewArr[i2] = view3;
            i2++;
        }
        if (z3) {
            viewArr[i2] = view4;
            zArr[i2] = true;
        }
        boolean z4 = false;
        for (int i3 = 0; i3 < 4; i3++) {
            View view6 = viewArr[i3];
            if (view6 != null) {
                if (view5 != null) {
                    if (!z4) {
                        view5.setBackgroundResource(0);
                    } else {
                        view5.setBackgroundResource(0);
                    }
                    z4 = true;
                }
                boolean z5 = zArr[i3];
                view5 = view6;
            }
        }
        if (view5 != null) {
            if (z4) {
                view5.setBackgroundResource(0);
            } else {
                view5.setBackgroundResource(0);
            }
        }
        ListView listView = this.mListView;
        if (!(listView == null || (listAdapter = this.mAdapter) == null)) {
            listView.setAdapter(listAdapter);
            int i4 = this.mCheckedItem;
            if (i4 > -1) {
                listView.setItemChecked(i4, true);
                listView.setSelection(i4);
            }
        }
    }

    public static class RecycleListView extends ListView {
        private final int mPaddingBottomNoButtons;
        private final int mPaddingTopNoTitle;

        public RecycleListView(Context context) {
            this(context, null);
        }

        public RecycleListView(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.RecycleListView);
            this.mPaddingBottomNoButtons = obtainStyledAttributes.getDimensionPixelOffset(R$styleable.RecycleListView_paddingBottomNoButtons, -1);
            this.mPaddingTopNoTitle = obtainStyledAttributes.getDimensionPixelOffset(R$styleable.RecycleListView_paddingTopNoTitle, -1);
            obtainStyledAttributes.recycle();
        }

        public void setHasDecor(boolean z, boolean z2) {
            if (!z2 || !z) {
                setPadding(getPaddingLeft(), z ? getPaddingTop() : this.mPaddingTopNoTitle, getPaddingRight(), z2 ? getPaddingBottom() : this.mPaddingBottomNoButtons);
            }
        }
    }

    public static class AlertParams {
        public ListAdapter mAdapter;
        public boolean mBottomShow;
        public boolean mCancelable;
        public int mCheckedItem = -1;
        public boolean[] mCheckedItems;
        public final Context mContext;
        public Cursor mCursor;
        public Drawable mCustomImage;
        public FrameLayout mCustomImageViewLayout;
        public View mCustomTitleView;
        public Drawable mIcon;
        public int mIconAttrId = 0;
        public int mIconId = 0;
        public final LayoutInflater mInflater;
        public String mIsCheckedColumn;
        public boolean mIsMultiChoice;
        public boolean mIsSingleChoice;
        public CharSequence[] mItems;
        public String mLabelColumn;
        public CharSequence mMessage;
        public Drawable mNegativeButtonIcon;
        public DialogInterface.OnClickListener mNegativeButtonListener;
        public CharSequence mNegativeButtonText;
        public Drawable mNeutralButtonIcon;
        public DialogInterface.OnClickListener mNeutralButtonListener;
        public CharSequence mNeutralButtonText;
        public DialogInterface.OnCancelListener mOnCancelListener;
        public DialogInterface.OnMultiChoiceClickListener mOnCheckboxClickListener;
        public DialogInterface.OnClickListener mOnClickListener;
        public DialogInterface.OnDismissListener mOnDismissListener;
        public AdapterView.OnItemSelectedListener mOnItemSelectedListener;
        public DialogInterface.OnKeyListener mOnKeyListener;
        public OnPrepareListViewListener mOnPrepareListViewListener;
        public Drawable mPositiveButtonIcon;
        public DialogInterface.OnClickListener mPositiveButtonListener;
        public CharSequence mPositiveButtonText;
        public CharSequence[] mSubItems;
        public CharSequence mTitle;
        public View mView;
        public int mViewLayoutResId;
        public int mViewSpacingBottom;
        public int mViewSpacingLeft;
        public int mViewSpacingRight;
        public boolean mViewSpacingSpecified = false;
        public int mViewSpacingTop;

        public interface OnPrepareListViewListener {
            void onPrepareListView(ListView listView);
        }

        public AlertParams(Context context) {
            this.mContext = context;
            this.mCancelable = true;
            this.mInflater = (LayoutInflater) context.getSystemService("layout_inflater");
        }

        public void apply(AlertController alertController) {
            CharSequence charSequence;
            View view = this.mCustomTitleView;
            if (view != null) {
                alertController.setCustomTitle(view);
            } else {
                Drawable drawable = this.mCustomImage;
                if (drawable != null) {
                    alertController.setCustomImage(drawable);
                }
                FrameLayout frameLayout = this.mCustomImageViewLayout;
                if (frameLayout != null) {
                    alertController.setCustomLayout(frameLayout);
                }
                if (TextUtils.isEmpty(this.mTitle) && (charSequence = this.mMessage) != null) {
                    this.mTitle = charSequence;
                    this.mMessage = null;
                }
                CharSequence charSequence2 = this.mTitle;
                if (charSequence2 != null) {
                    alertController.setTitle(charSequence2);
                }
                Drawable drawable2 = this.mIcon;
                if (drawable2 != null) {
                    alertController.setIcon(drawable2);
                }
                int i = this.mIconId;
                if (i != 0) {
                    alertController.setIcon(i);
                }
                int i2 = this.mIconAttrId;
                if (i2 != 0) {
                    alertController.setIcon(alertController.getIconAttributeResId(i2));
                }
            }
            alertController.showInBottom(this.mBottomShow);
            CharSequence charSequence3 = this.mMessage;
            if (charSequence3 != null) {
                alertController.setMessage(charSequence3);
            }
            if (!(this.mPositiveButtonText == null && this.mPositiveButtonIcon == null)) {
                alertController.setButton(-1, this.mPositiveButtonText, this.mPositiveButtonListener, null, this.mPositiveButtonIcon);
            }
            if (!(this.mNegativeButtonText == null && this.mNegativeButtonIcon == null)) {
                alertController.setButton(-2, this.mNegativeButtonText, this.mNegativeButtonListener, null, this.mNegativeButtonIcon);
            }
            if (!(this.mNeutralButtonText == null && this.mNeutralButtonIcon == null)) {
                alertController.setButton(-3, this.mNeutralButtonText, this.mNeutralButtonListener, null, this.mNeutralButtonIcon);
            }
            if (!(this.mItems == null && this.mCursor == null && this.mAdapter == null)) {
                createListView(alertController);
            }
            View view2 = this.mView;
            if (view2 == null) {
                int i3 = this.mViewLayoutResId;
                if (i3 != 0) {
                    alertController.setView(i3);
                }
            } else if (this.mViewSpacingSpecified) {
                alertController.setView(view2, this.mViewSpacingLeft, this.mViewSpacingTop, this.mViewSpacingRight, this.mViewSpacingBottom);
            } else {
                alertController.setView(view2);
            }
        }

        private void createListView(final AlertController alertController) {
            ListAdapter listAdapter;
            int i;
            final RecycleListView recycleListView = (RecycleListView) this.mInflater.inflate(alertController.mListLayout, (ViewGroup) null);
            if (!this.mIsMultiChoice) {
                if (this.mIsSingleChoice) {
                    i = alertController.mSingleChoiceItemLayout;
                    CharSequence[] charSequenceArr = this.mSubItems;
                    if (charSequenceArr != null && charSequenceArr.length > 0) {
                        i = R$layout.select_dialog_singlechoice_with_subtitle_material;
                    }
                } else {
                    i = alertController.mListItemLayout;
                    CharSequence[] charSequenceArr2 = this.mSubItems;
                    if (charSequenceArr2 != null && charSequenceArr2.length > 0) {
                        i = R$layout.select_dialog_item_with_subtitle_material;
                    }
                }
                if (this.mCursor != null) {
                    listAdapter = new SimpleCursorAdapter(this.mContext, i, this.mCursor, new String[]{this.mLabelColumn}, new int[]{16908308});
                } else {
                    listAdapter = this.mAdapter;
                    if (listAdapter == null) {
                        listAdapter = new CheckedItemAdapter(this.mContext, i, 16908308, this.mItems) {
                            /* class androidx.appcompat.app.AlertController.AlertParams.AnonymousClass3 */

                            public View getView(int i, View view, ViewGroup viewGroup) {
                                if (view != null) {
                                    if (view.findViewById(16908308) instanceof CheckedTextView) {
                                        CheckedTextView checkedTextView = (CheckedTextView) view.findViewById(16908308);
                                        TextView textView = (TextView) view.findViewById(R$id.singlechoice_subtitle);
                                        if (textView != null) {
                                            CharSequence[] charSequenceArr = AlertParams.this.mSubItems;
                                            if (charSequenceArr != null && charSequenceArr.length > i) {
                                                textView.setText(charSequenceArr[i]);
                                            }
                                            int lineCount = textView.getLineCount();
                                            if (!TextUtils.isEmpty(textView.getText()) && lineCount > 1) {
                                                checkedTextView.setHeight(AlertParams.this.mContext.getResources().getDimensionPixelOffset(R$dimen.oneplus_dialog_subtitle_minHeight_multiline));
                                                textView.setVisibility(0);
                                            } else if (!TextUtils.isEmpty(textView.getText())) {
                                                checkedTextView.setHeight(AlertParams.this.mContext.getResources().getDimensionPixelOffset(R$dimen.oneplus_dialog_subtitle_minHeight_singleline));
                                                textView.setVisibility(0);
                                            } else if (TextUtils.isEmpty(textView.getText())) {
                                                checkedTextView.setMinHeight(AlertParams.this.mContext.getResources().getDimensionPixelOffset(R$dimen.oneplus_dialog_subtitle_minHeight_noline));
                                                textView.setVisibility(8);
                                            } else {
                                                checkedTextView.setMinHeight(AlertParams.this.mContext.getResources().getDimensionPixelOffset(R$dimen.oneplus_dialog_subtitle_minHeight_singleline));
                                                textView.setVisibility(0);
                                            }
                                        }
                                        notifyDataSetChanged();
                                        if (i == AlertParams.this.mCheckedItem) {
                                            checkedTextView.setChecked(true);
                                        } else {
                                            checkedTextView.setChecked(false);
                                        }
                                    } else {
                                        TextView textView2 = (TextView) view.findViewById(16908308);
                                        TextView textView3 = (TextView) view.findViewById(R$id.singlechoice_subtitle);
                                        if (textView3 != null) {
                                            CharSequence[] charSequenceArr2 = AlertParams.this.mSubItems;
                                            if (charSequenceArr2 != null && charSequenceArr2.length > i) {
                                                textView3.setText(charSequenceArr2[i]);
                                            }
                                            int lineCount2 = textView3.getLineCount();
                                            if (!TextUtils.isEmpty(textView3.getText()) && lineCount2 > 1) {
                                                textView2.setHeight(AlertParams.this.mContext.getResources().getDimensionPixelOffset(R$dimen.oneplus_dialog_subtitle_minHeight_multiline));
                                                textView3.setVisibility(0);
                                            } else if (!TextUtils.isEmpty(textView3.getText())) {
                                                textView2.setHeight(AlertParams.this.mContext.getResources().getDimensionPixelOffset(R$dimen.oneplus_dialog_subtitle_minHeight_singleline));
                                                textView3.setVisibility(0);
                                            } else if (TextUtils.isEmpty(textView3.getText())) {
                                                textView2.setMinHeight(AlertParams.this.mContext.getResources().getDimensionPixelOffset(R$dimen.oneplus_dialog_subtitle_minHeight_noline));
                                                textView3.setVisibility(8);
                                            } else {
                                                textView2.setMinHeight(AlertParams.this.mContext.getResources().getDimensionPixelOffset(R$dimen.oneplus_dialog_subtitle_minHeight_singleline));
                                                textView3.setVisibility(0);
                                            }
                                        }
                                        notifyDataSetChanged();
                                    }
                                }
                                return super.getView(i, view, viewGroup);
                            }
                        };
                    }
                }
            } else if (this.mCursor == null) {
                int i2 = alertController.mMultiChoiceItemLayout;
                CharSequence[] charSequenceArr3 = this.mSubItems;
                if (charSequenceArr3 != null && charSequenceArr3.length > 0) {
                    i2 = R$layout.select_dialog_multichoice_with_subtitle_material;
                }
                listAdapter = new ArrayAdapter<CharSequence>(this.mContext, i2, 16908308, this.mItems) {
                    /* class androidx.appcompat.app.AlertController.AlertParams.AnonymousClass1 */

                    public View getView(int i, View view, ViewGroup viewGroup) {
                        View view2 = super.getView(i, view, viewGroup);
                        if (view != null) {
                            CheckedTextView checkedTextView = (CheckedTextView) view.findViewById(16908308);
                            TextView textView = (TextView) view.findViewById(R$id.singlechoice_subtitle);
                            if (textView != null) {
                                CharSequence[] charSequenceArr = AlertParams.this.mSubItems;
                                if (charSequenceArr != null && charSequenceArr.length > i) {
                                    textView.setText(charSequenceArr[i]);
                                }
                                int lineCount = textView.getLineCount();
                                if (AlertParams.this.mCheckedItems != null) {
                                    if (!TextUtils.isEmpty(textView.getText()) && lineCount > 1) {
                                        checkedTextView.setHeight(AlertParams.this.mContext.getResources().getDimensionPixelOffset(R$dimen.oneplus_dialog_subtitle_minHeight_multiline));
                                        textView.setVisibility(0);
                                    } else if (!TextUtils.isEmpty(textView.getText())) {
                                        checkedTextView.setHeight(AlertParams.this.mContext.getResources().getDimensionPixelOffset(R$dimen.oneplus_dialog_subtitle_minHeight_singleline));
                                        textView.setVisibility(0);
                                    } else if (TextUtils.isEmpty(textView.getText())) {
                                        checkedTextView.setMinHeight(AlertParams.this.mContext.getResources().getDimensionPixelOffset(R$dimen.oneplus_dialog_subtitle_minHeight_noline));
                                        textView.setVisibility(8);
                                    } else {
                                        checkedTextView.setMinHeight(AlertParams.this.mContext.getResources().getDimensionPixelOffset(R$dimen.oneplus_dialog_subtitle_minHeight_singleline));
                                        textView.setVisibility(0);
                                    }
                                    notifyDataSetChanged();
                                    if (AlertParams.this.mCheckedItems[i]) {
                                        checkedTextView.setChecked(true);
                                    } else {
                                        checkedTextView.setChecked(false);
                                    }
                                    if (AlertParams.this.mCheckedItems[i]) {
                                        recycleListView.setItemChecked(i, true);
                                    }
                                }
                            }
                        }
                        return view2;
                    }
                };
            } else {
                listAdapter = new CursorAdapter(this.mContext, this.mCursor, false) {
                    /* class androidx.appcompat.app.AlertController.AlertParams.AnonymousClass2 */
                    private final int mIsCheckedIndex;
                    private final int mLabelIndex;

                    {
                        Cursor cursor = getCursor();
                        this.mLabelIndex = cursor.getColumnIndexOrThrow(AlertParams.this.mLabelColumn);
                        this.mIsCheckedIndex = cursor.getColumnIndexOrThrow(AlertParams.this.mIsCheckedColumn);
                    }

                    public void bindView(View view, Context context, Cursor cursor) {
                        ((CheckedTextView) view.findViewById(16908308)).setText(cursor.getString(this.mLabelIndex));
                        RecycleListView recycleListView = recycleListView;
                        int position = cursor.getPosition();
                        int i = cursor.getInt(this.mIsCheckedIndex);
                        boolean z = true;
                        if (i != 1) {
                            z = false;
                        }
                        recycleListView.setItemChecked(position, z);
                    }

                    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
                        return AlertParams.this.mInflater.inflate(alertController.mMultiChoiceItemLayout, viewGroup, false);
                    }
                };
            }
            OnPrepareListViewListener onPrepareListViewListener = this.mOnPrepareListViewListener;
            if (onPrepareListViewListener != null) {
                onPrepareListViewListener.onPrepareListView(recycleListView);
            }
            alertController.mAdapter = listAdapter;
            alertController.mCheckedItem = this.mCheckedItem;
            if (this.mOnClickListener != null) {
                recycleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    /* class androidx.appcompat.app.AlertController.AlertParams.AnonymousClass4 */

                    @Override // android.widget.AdapterView.OnItemClickListener
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                        CheckedTextView checkedTextView;
                        View childAt;
                        AlertParams.this.mOnClickListener.onClick(alertController.mDialog, i);
                        if (AlertParams.this.mIsSingleChoice && (checkedTextView = (CheckedTextView) view.findViewById(16908308)) != null) {
                            AlertParams.this.mCheckedItem = i;
                            for (int i2 = 0; i2 < recycleListView.getCount(); i2++) {
                                if (!(i2 == i || (childAt = recycleListView.getChildAt(i2)) == null)) {
                                    ((CheckedTextView) childAt.findViewById(16908308)).setChecked(false);
                                }
                            }
                            checkedTextView.setChecked(true);
                        }
                        if (!AlertParams.this.mIsSingleChoice) {
                            alertController.mDialog.dismiss();
                        }
                    }
                });
            } else if (this.mOnCheckboxClickListener != null) {
                recycleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    /* class androidx.appcompat.app.AlertController.AlertParams.AnonymousClass5 */

                    @Override // android.widget.AdapterView.OnItemClickListener
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                        CheckedTextView checkedTextView = (CheckedTextView) view.findViewById(16908308);
                        if (checkedTextView != null) {
                            boolean[] zArr = AlertParams.this.mCheckedItems;
                            if (zArr != null) {
                                zArr[i] = !zArr[i];
                                checkedTextView.setChecked(zArr[i]);
                            }
                            AlertParams.this.mOnCheckboxClickListener.onClick(alertController.mDialog, i, checkedTextView.isChecked());
                        }
                    }
                });
            }
            AdapterView.OnItemSelectedListener onItemSelectedListener = this.mOnItemSelectedListener;
            if (onItemSelectedListener != null) {
                recycleListView.setOnItemSelectedListener(onItemSelectedListener);
            }
            if (this.mIsSingleChoice) {
                recycleListView.setChoiceMode(1);
            } else if (this.mIsMultiChoice) {
                recycleListView.setChoiceMode(2);
            }
            alertController.mListView = recycleListView;
        }
    }

    private static class CheckedItemAdapter extends ArrayAdapter<CharSequence> {
        public long getItemId(int i) {
            return (long) i;
        }

        public boolean hasStableIds() {
            return true;
        }

        public CheckedItemAdapter(Context context, int i, int i2, CharSequence[] charSequenceArr) {
            super(context, i, i2, charSequenceArr);
        }
    }
}
