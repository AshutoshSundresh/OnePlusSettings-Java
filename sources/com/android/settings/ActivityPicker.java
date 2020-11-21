package com.android.settings;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.android.internal.app.AlertActivity;
import com.android.internal.app.AlertController;
import com.android.settings.AppWidgetLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ActivityPicker extends AlertActivity implements DialogInterface.OnClickListener, DialogInterface.OnCancelListener {
    private PickAdapter mAdapter;
    private Intent mBaseIntent;

    /* JADX DEBUG: Multi-variable search result rejected for r3v0, resolved type: com.android.settings.ActivityPicker */
    /* JADX WARN: Multi-variable type inference failed */
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        ActivityPicker.super.onCreate(bundle);
        Intent intent = getIntent();
        Parcelable parcelableExtra = intent.getParcelableExtra("android.intent.extra.INTENT");
        if (parcelableExtra instanceof Intent) {
            Intent intent2 = (Intent) parcelableExtra;
            this.mBaseIntent = intent2;
            intent2.setFlags(intent2.getFlags() & -196);
        } else {
            Intent intent3 = new Intent("android.intent.action.MAIN", (Uri) null);
            this.mBaseIntent = intent3;
            intent3.addCategory("android.intent.category.DEFAULT");
        }
        AlertController.AlertParams alertParams = ((AlertActivity) this).mAlertParams;
        alertParams.mOnClickListener = this;
        alertParams.mOnCancelListener = this;
        if (intent.hasExtra("android.intent.extra.TITLE")) {
            alertParams.mTitle = intent.getStringExtra("android.intent.extra.TITLE");
        } else {
            alertParams.mTitle = getTitle();
        }
        PickAdapter pickAdapter = new PickAdapter(this, getItems());
        this.mAdapter = pickAdapter;
        alertParams.mAdapter = pickAdapter;
        setupAlert();
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        setResult(-1, getIntentForPosition(i));
        finish();
    }

    public void onCancel(DialogInterface dialogInterface) {
        setResult(0);
        finish();
    }

    /* access modifiers changed from: protected */
    public Intent getIntentForPosition(int i) {
        return ((PickAdapter.Item) this.mAdapter.getItem(i)).getIntent(this.mBaseIntent);
    }

    /* JADX DEBUG: Multi-variable search result rejected for r9v0, resolved type: com.android.settings.ActivityPicker */
    /* JADX WARN: Multi-variable type inference failed */
    /* access modifiers changed from: protected */
    public List<PickAdapter.Item> getItems() {
        PackageManager packageManager = getPackageManager();
        ArrayList arrayList = new ArrayList();
        Intent intent = getIntent();
        ArrayList<String> stringArrayListExtra = intent.getStringArrayListExtra("android.intent.extra.shortcut.NAME");
        ArrayList parcelableArrayListExtra = intent.getParcelableArrayListExtra("android.intent.extra.shortcut.ICON_RESOURCE");
        if (!(stringArrayListExtra == null || parcelableArrayListExtra == null || stringArrayListExtra.size() != parcelableArrayListExtra.size())) {
            for (int i = 0; i < stringArrayListExtra.size(); i++) {
                String str = stringArrayListExtra.get(i);
                Drawable drawable = null;
                try {
                    Intent.ShortcutIconResource shortcutIconResource = (Intent.ShortcutIconResource) parcelableArrayListExtra.get(i);
                    Resources resourcesForApplication = packageManager.getResourcesForApplication(shortcutIconResource.packageName);
                    drawable = resourcesForApplication.getDrawable(resourcesForApplication.getIdentifier(shortcutIconResource.resourceName, null, null), null);
                } catch (PackageManager.NameNotFoundException unused) {
                }
                arrayList.add(new PickAdapter.Item((Context) this, (CharSequence) str, drawable));
            }
        }
        Intent intent2 = this.mBaseIntent;
        if (intent2 != null) {
            putIntentItems(intent2, arrayList);
        }
        return arrayList;
    }

    /* JADX DEBUG: Multi-variable search result rejected for r5v0, resolved type: com.android.settings.ActivityPicker */
    /* JADX WARN: Multi-variable type inference failed */
    /* access modifiers changed from: protected */
    public void putIntentItems(Intent intent, List<PickAdapter.Item> list) {
        PackageManager packageManager = getPackageManager();
        List<ResolveInfo> queryIntentActivities = packageManager.queryIntentActivities(intent, 0);
        Collections.sort(queryIntentActivities, new ResolveInfo.DisplayNameComparator(packageManager));
        int size = queryIntentActivities.size();
        for (int i = 0; i < size; i++) {
            list.add(new PickAdapter.Item((Context) this, packageManager, queryIntentActivities.get(i)));
        }
    }

    /* access modifiers changed from: protected */
    public static class PickAdapter extends BaseAdapter {
        private final LayoutInflater mInflater;
        private final List<Item> mItems;

        public long getItemId(int i) {
            return (long) i;
        }

        public static class Item implements AppWidgetLoader.LabelledItem {
            protected static IconResizer sResizer;
            String className;
            Bundle extras;
            Drawable icon;
            CharSequence label;
            String packageName;

            /* access modifiers changed from: protected */
            public IconResizer getResizer(Context context) {
                if (sResizer == null) {
                    Resources resources = context.getResources();
                    int dimension = (int) resources.getDimension(17104896);
                    sResizer = new IconResizer(dimension, dimension, resources.getDisplayMetrics());
                }
                return sResizer;
            }

            Item(Context context, CharSequence charSequence, Drawable drawable) {
                this.label = charSequence;
                this.icon = getResizer(context).createIconThumbnail(drawable);
            }

            Item(Context context, PackageManager packageManager, ResolveInfo resolveInfo) {
                ActivityInfo activityInfo;
                CharSequence loadLabel = resolveInfo.loadLabel(packageManager);
                this.label = loadLabel;
                if (loadLabel == null && (activityInfo = resolveInfo.activityInfo) != null) {
                    this.label = activityInfo.name;
                }
                this.icon = getResizer(context).createIconThumbnail(resolveInfo.loadIcon(packageManager));
                ActivityInfo activityInfo2 = resolveInfo.activityInfo;
                this.packageName = activityInfo2.applicationInfo.packageName;
                this.className = activityInfo2.name;
            }

            /* access modifiers changed from: package-private */
            public Intent getIntent(Intent intent) {
                String str;
                Intent intent2 = new Intent(intent);
                String str2 = this.packageName;
                if (str2 == null || (str = this.className) == null) {
                    intent2.setAction("android.intent.action.CREATE_SHORTCUT");
                    intent2.putExtra("android.intent.extra.shortcut.NAME", this.label);
                } else {
                    intent2.setClassName(str2, str);
                    Bundle bundle = this.extras;
                    if (bundle != null) {
                        intent2.putExtras(bundle);
                    }
                }
                return intent2;
            }

            @Override // com.android.settings.AppWidgetLoader.LabelledItem
            public CharSequence getLabel() {
                return this.label;
            }
        }

        public PickAdapter(Context context, List<Item> list) {
            this.mInflater = (LayoutInflater) context.getSystemService("layout_inflater");
            this.mItems = list;
        }

        public int getCount() {
            return this.mItems.size();
        }

        public Object getItem(int i) {
            return this.mItems.get(i);
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = this.mInflater.inflate(C0012R$layout.pick_item, viewGroup, false);
            }
            Item item = (Item) getItem(i);
            TextView textView = (TextView) view;
            textView.setText(item.label);
            textView.setCompoundDrawablesWithIntrinsicBounds(item.icon, (Drawable) null, (Drawable) null, (Drawable) null);
            return view;
        }
    }

    /* access modifiers changed from: private */
    public static class IconResizer {
        private final Canvas mCanvas;
        private final int mIconHeight;
        private final int mIconWidth;
        private final DisplayMetrics mMetrics;
        private final Rect mOldBounds = new Rect();

        public IconResizer(int i, int i2, DisplayMetrics displayMetrics) {
            Canvas canvas = new Canvas();
            this.mCanvas = canvas;
            canvas.setDrawFilter(new PaintFlagsDrawFilter(4, 2));
            this.mMetrics = displayMetrics;
            this.mIconWidth = i;
            this.mIconHeight = i2;
        }

        public Drawable createIconThumbnail(Drawable drawable) {
            int i = this.mIconWidth;
            int i2 = this.mIconHeight;
            if (drawable == null) {
                return new EmptyDrawable(i, i2);
            }
            try {
                if (drawable instanceof PaintDrawable) {
                    PaintDrawable paintDrawable = (PaintDrawable) drawable;
                    paintDrawable.setIntrinsicWidth(i);
                    paintDrawable.setIntrinsicHeight(i2);
                } else if (drawable instanceof BitmapDrawable) {
                    BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                    if (bitmapDrawable.getBitmap().getDensity() == 0) {
                        bitmapDrawable.setTargetDensity(this.mMetrics);
                    }
                }
                int intrinsicWidth = drawable.getIntrinsicWidth();
                int intrinsicHeight = drawable.getIntrinsicHeight();
                if (intrinsicWidth <= 0 || intrinsicHeight <= 0) {
                    return drawable;
                }
                if (i >= intrinsicWidth) {
                    if (i2 >= intrinsicHeight) {
                        if (intrinsicWidth >= i || intrinsicHeight >= i2) {
                            return drawable;
                        }
                        Bitmap createBitmap = Bitmap.createBitmap(this.mIconWidth, this.mIconHeight, Bitmap.Config.ARGB_8888);
                        Canvas canvas = this.mCanvas;
                        canvas.setBitmap(createBitmap);
                        this.mOldBounds.set(drawable.getBounds());
                        int i3 = (i - intrinsicWidth) / 2;
                        int i4 = (i2 - intrinsicHeight) / 2;
                        drawable.setBounds(i3, i4, intrinsicWidth + i3, intrinsicHeight + i4);
                        drawable.draw(canvas);
                        drawable.setBounds(this.mOldBounds);
                        BitmapDrawable bitmapDrawable2 = new BitmapDrawable(createBitmap);
                        bitmapDrawable2.setTargetDensity(this.mMetrics);
                        canvas.setBitmap(null);
                        return bitmapDrawable2;
                    }
                }
                float f = ((float) intrinsicWidth) / ((float) intrinsicHeight);
                if (intrinsicWidth > intrinsicHeight) {
                    i2 = (int) (((float) i) / f);
                } else if (intrinsicHeight > intrinsicWidth) {
                    i = (int) (((float) i2) * f);
                }
                Bitmap createBitmap2 = Bitmap.createBitmap(this.mIconWidth, this.mIconHeight, drawable.getOpacity() != -1 ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
                Canvas canvas2 = this.mCanvas;
                canvas2.setBitmap(createBitmap2);
                this.mOldBounds.set(drawable.getBounds());
                int i5 = (this.mIconWidth - i) / 2;
                int i6 = (this.mIconHeight - i2) / 2;
                drawable.setBounds(i5, i6, i5 + i, i6 + i2);
                drawable.draw(canvas2);
                drawable.setBounds(this.mOldBounds);
                BitmapDrawable bitmapDrawable3 = new BitmapDrawable(createBitmap2);
                bitmapDrawable3.setTargetDensity(this.mMetrics);
                canvas2.setBitmap(null);
                return bitmapDrawable3;
            } catch (Throwable unused) {
                return new EmptyDrawable(i, i2);
            }
        }
    }

    /* access modifiers changed from: private */
    public static class EmptyDrawable extends Drawable {
        private final int mHeight;
        private final int mWidth;

        public void draw(Canvas canvas) {
        }

        public int getOpacity() {
            return -3;
        }

        public void setAlpha(int i) {
        }

        public void setColorFilter(ColorFilter colorFilter) {
        }

        EmptyDrawable(int i, int i2) {
            this.mWidth = i;
            this.mHeight = i2;
        }

        public int getIntrinsicWidth() {
            return this.mWidth;
        }

        public int getIntrinsicHeight() {
            return this.mHeight;
        }

        public int getMinimumWidth() {
            return this.mWidth;
        }

        public int getMinimumHeight() {
            return this.mHeight;
        }
    }
}
