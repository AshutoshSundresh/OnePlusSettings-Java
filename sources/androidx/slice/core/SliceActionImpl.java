package androidx.slice.core;

import android.app.PendingIntent;
import androidx.core.graphics.drawable.IconCompat;
import androidx.slice.Slice;
import androidx.slice.SliceItem;

public class SliceActionImpl implements SliceAction {
    private PendingIntent mAction;
    private SliceItem mActionItem;
    private CharSequence mContentDescription;
    private IconCompat mIcon;
    private int mImageMode;
    private boolean mIsActivity;
    private boolean mIsChecked;
    private boolean mIsToggle;
    private int mPriority;
    private SliceItem mSliceItem;
    private CharSequence mTitle;

    public SliceActionImpl(PendingIntent pendingIntent, IconCompat iconCompat, int i, CharSequence charSequence) {
        this.mImageMode = 5;
        this.mPriority = -1;
        this.mAction = pendingIntent;
        this.mIcon = iconCompat;
        this.mTitle = charSequence;
        this.mImageMode = i;
    }

    public SliceActionImpl(PendingIntent pendingIntent, IconCompat iconCompat, CharSequence charSequence, boolean z) {
        this(pendingIntent, iconCompat, 0, charSequence);
        this.mIsChecked = z;
        this.mIsToggle = true;
    }

    public SliceActionImpl(PendingIntent pendingIntent, CharSequence charSequence, boolean z) {
        this.mImageMode = 5;
        this.mPriority = -1;
        this.mAction = pendingIntent;
        this.mTitle = charSequence;
        this.mIsToggle = true;
        this.mIsChecked = z;
    }

    public SliceActionImpl(SliceItem sliceItem) {
        this.mImageMode = 5;
        int i = -1;
        this.mPriority = -1;
        this.mSliceItem = sliceItem;
        SliceItem find = SliceQuery.find(sliceItem, "action");
        if (find != null) {
            this.mActionItem = find;
            this.mAction = find.getAction();
            SliceItem find2 = SliceQuery.find(find.getSlice(), "image");
            if (find2 != null) {
                this.mIcon = find2.getIcon();
                this.mImageMode = parseImageMode(find2);
            }
            SliceItem find3 = SliceQuery.find(find.getSlice(), "text", "title", (String) null);
            if (find3 != null) {
                this.mTitle = find3.getSanitizedText();
            }
            SliceItem findSubtype = SliceQuery.findSubtype(find.getSlice(), "text", "content_description");
            if (findSubtype != null) {
                this.mContentDescription = findSubtype.getText();
            }
            boolean equals = "toggle".equals(find.getSubType());
            this.mIsToggle = equals;
            if (equals) {
                this.mIsChecked = find.hasHint("selected");
            }
            this.mIsActivity = this.mSliceItem.hasHint("activity");
            SliceItem findSubtype2 = SliceQuery.findSubtype(find.getSlice(), "int", "priority");
            this.mPriority = findSubtype2 != null ? findSubtype2.getInt() : i;
        }
    }

    @Override // androidx.slice.core.SliceAction
    public PendingIntent getAction() {
        PendingIntent pendingIntent = this.mAction;
        return pendingIntent != null ? pendingIntent : this.mActionItem.getAction();
    }

    public SliceItem getActionItem() {
        return this.mActionItem;
    }

    @Override // androidx.slice.core.SliceAction
    public IconCompat getIcon() {
        return this.mIcon;
    }

    @Override // androidx.slice.core.SliceAction
    public CharSequence getTitle() {
        return this.mTitle;
    }

    public CharSequence getContentDescription() {
        return this.mContentDescription;
    }

    @Override // androidx.slice.core.SliceAction
    public int getPriority() {
        return this.mPriority;
    }

    @Override // androidx.slice.core.SliceAction
    public boolean isToggle() {
        return this.mIsToggle;
    }

    public boolean isChecked() {
        return this.mIsChecked;
    }

    @Override // androidx.slice.core.SliceAction
    public int getImageMode() {
        return this.mImageMode;
    }

    public boolean isDefaultToggle() {
        return this.mIsToggle && this.mIcon == null;
    }

    public SliceItem getSliceItem() {
        return this.mSliceItem;
    }

    public Slice buildSlice(Slice.Builder builder) {
        builder.addHints("shortcut");
        builder.addAction(this.mAction, buildSliceContent(builder).build(), getSubtype());
        return builder.build();
    }

    public Slice buildPrimaryActionSlice(Slice.Builder builder) {
        Slice.Builder buildSliceContent = buildSliceContent(builder);
        buildSliceContent.addHints("shortcut", "title");
        return buildSliceContent.build();
    }

    private Slice.Builder buildSliceContent(Slice.Builder builder) {
        String[] strArr;
        Slice.Builder builder2 = new Slice.Builder(builder);
        if (this.mIcon != null) {
            if (this.mImageMode == 0) {
                strArr = new String[0];
            } else {
                strArr = new String[]{"no_tint"};
            }
            builder2.addIcon(this.mIcon, (String) null, strArr);
        }
        CharSequence charSequence = this.mTitle;
        if (charSequence != null) {
            builder2.addText(charSequence, (String) null, "title");
        }
        CharSequence charSequence2 = this.mContentDescription;
        if (charSequence2 != null) {
            builder2.addText(charSequence2, "content_description", new String[0]);
        }
        if (this.mIsToggle && this.mIsChecked) {
            builder2.addHints("selected");
        }
        int i = this.mPriority;
        if (i != -1) {
            builder2.addInt(i, "priority", new String[0]);
        }
        if (this.mIsActivity) {
            builder.addHints("activity");
        }
        return builder2;
    }

    public String getSubtype() {
        if (this.mIsToggle) {
            return "toggle";
        }
        return null;
    }

    public void setActivity(boolean z) {
        this.mIsActivity = z;
    }

    public static int parseImageMode(SliceItem sliceItem) {
        if (!sliceItem.hasHint("no_tint")) {
            return 0;
        }
        return sliceItem.hasHint("raw") ? sliceItem.hasHint("large") ? 4 : 3 : sliceItem.hasHint("large") ? 2 : 1;
    }
}
