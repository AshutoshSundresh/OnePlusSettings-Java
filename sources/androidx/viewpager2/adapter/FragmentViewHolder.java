package androidx.viewpager2.adapter;

import android.widget.FrameLayout;
import androidx.recyclerview.widget.RecyclerView;

public final class FragmentViewHolder extends RecyclerView.ViewHolder {
    /* access modifiers changed from: package-private */
    public FrameLayout getContainer() {
        return (FrameLayout) this.itemView;
    }
}
