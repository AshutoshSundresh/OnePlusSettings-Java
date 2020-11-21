package androidx.leanback.widget;

import android.graphics.drawable.Drawable;
import android.view.View;
import androidx.leanback.widget.SearchOrbView;

public abstract class TitleViewAdapter {

    public interface Provider {
        TitleViewAdapter getTitleViewAdapter();
    }

    public abstract View getSearchAffordanceView();

    public abstract void setAnimationEnabled(boolean z);

    public abstract void setBadgeDrawable(Drawable drawable);

    public abstract void setOnSearchClickedListener(View.OnClickListener onClickListener);

    public abstract void setSearchAffordanceColors(SearchOrbView.Colors colors);

    public abstract void setTitle(CharSequence charSequence);

    public abstract void updateComponentsVisibility(int i);
}
