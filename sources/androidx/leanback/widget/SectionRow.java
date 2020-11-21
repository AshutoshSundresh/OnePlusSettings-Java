package androidx.leanback.widget;

public class SectionRow extends Row {
    @Override // androidx.leanback.widget.Row
    public final boolean isRenderedAsRowView() {
        return false;
    }
}
