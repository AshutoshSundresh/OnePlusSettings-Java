package androidx.slice.builders;

import android.app.PendingIntent;
import androidx.core.graphics.drawable.IconCompat;
import androidx.slice.Slice;
import androidx.slice.core.SliceActionImpl;

public class SliceAction implements androidx.slice.core.SliceAction {
    private SliceActionImpl mSliceAction;

    public SliceAction(PendingIntent pendingIntent, IconCompat iconCompat, int i, CharSequence charSequence) {
        this.mSliceAction = new SliceActionImpl(pendingIntent, iconCompat, i, charSequence);
    }

    public SliceAction(PendingIntent pendingIntent, IconCompat iconCompat, CharSequence charSequence, boolean z) {
        this.mSliceAction = new SliceActionImpl(pendingIntent, iconCompat, charSequence, z);
    }

    public SliceAction(PendingIntent pendingIntent, CharSequence charSequence, boolean z) {
        this.mSliceAction = new SliceActionImpl(pendingIntent, charSequence, z);
    }

    public static SliceAction create(PendingIntent pendingIntent, IconCompat iconCompat, int i, CharSequence charSequence) {
        return new SliceAction(pendingIntent, iconCompat, i, charSequence);
    }

    public static SliceAction createDeeplink(PendingIntent pendingIntent, IconCompat iconCompat, int i, CharSequence charSequence) {
        SliceAction sliceAction = new SliceAction(pendingIntent, iconCompat, i, charSequence);
        sliceAction.mSliceAction.setActivity(true);
        return sliceAction;
    }

    public static SliceAction createToggle(PendingIntent pendingIntent, CharSequence charSequence, boolean z) {
        return new SliceAction(pendingIntent, charSequence, z);
    }

    public static SliceAction createToggle(PendingIntent pendingIntent, IconCompat iconCompat, CharSequence charSequence, boolean z) {
        return new SliceAction(pendingIntent, iconCompat, charSequence, z);
    }

    @Override // androidx.slice.core.SliceAction
    public PendingIntent getAction() {
        return this.mSliceAction.getAction();
    }

    @Override // androidx.slice.core.SliceAction
    public IconCompat getIcon() {
        return this.mSliceAction.getIcon();
    }

    @Override // androidx.slice.core.SliceAction
    public CharSequence getTitle() {
        return this.mSliceAction.getTitle();
    }

    @Override // androidx.slice.core.SliceAction
    public int getPriority() {
        return this.mSliceAction.getPriority();
    }

    @Override // androidx.slice.core.SliceAction
    public boolean isToggle() {
        return this.mSliceAction.isToggle();
    }

    @Override // androidx.slice.core.SliceAction
    public int getImageMode() {
        return this.mSliceAction.getImageMode();
    }

    public Slice buildSlice(Slice.Builder builder) {
        return this.mSliceAction.buildSlice(builder);
    }

    public SliceActionImpl getImpl() {
        return this.mSliceAction;
    }

    public void setPrimaryAction(Slice.Builder builder) {
        builder.addAction(this.mSliceAction.getAction(), this.mSliceAction.buildPrimaryActionSlice(builder), this.mSliceAction.getSubtype());
    }
}
