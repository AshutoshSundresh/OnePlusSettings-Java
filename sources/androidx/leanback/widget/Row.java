package androidx.leanback.widget;

public class Row {
    private HeaderItem mHeaderItem;

    public boolean isRenderedAsRowView() {
        return true;
    }

    public final HeaderItem getHeaderItem() {
        return this.mHeaderItem;
    }
}
