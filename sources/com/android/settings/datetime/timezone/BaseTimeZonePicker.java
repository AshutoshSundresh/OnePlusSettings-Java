package com.android.settings.datetime.timezone;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.settings.C0006R$color;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0013R$menu;
import com.android.settings.core.InstrumentedFragment;
import com.android.settings.datetime.timezone.BaseTimeZoneAdapter;
import com.android.settings.datetime.timezone.model.TimeZoneData;
import com.android.settings.datetime.timezone.model.TimeZoneDataLoader;
import com.oneplus.settings.edgeeffect.SpringRelativeLayout;
import com.oneplus.settings.utils.OPUtils;
import java.util.Locale;

public abstract class BaseTimeZonePicker extends InstrumentedFragment implements SearchView.OnQueryTextListener {
    private BaseTimeZoneAdapter mAdapter;
    private final boolean mDefaultExpandSearch;
    private RecyclerView mRecyclerView;
    private final boolean mSearchEnabled;
    private final int mSearchHintResId;
    private SearchView mSearchView;
    private TimeZoneData mTimeZoneData;
    private final int mTitleResId;

    public interface OnListItemClickListener<T extends BaseTimeZoneAdapter.AdapterItem> {
        void onListItemClick(T t);
    }

    /* access modifiers changed from: protected */
    public abstract BaseTimeZoneAdapter createAdapter(TimeZoneData timeZoneData);

    @Override // androidx.appcompat.widget.SearchView.OnQueryTextListener
    public boolean onQueryTextSubmit(String str) {
        return false;
    }

    protected BaseTimeZonePicker(int i, int i2, boolean z, boolean z2) {
        this.mTitleResId = i;
        this.mSearchHintResId = i2;
        this.mSearchEnabled = z;
        this.mDefaultExpandSearch = z2;
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setHasOptionsMenu(true);
        getActivity().setTitle(this.mTitleResId);
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(C0012R$layout.recycler_view, viewGroup, false);
        RecyclerView recyclerView = (RecyclerView) inflate.findViewById(C0010R$id.recycler_view);
        this.mRecyclerView = recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), 1, false));
        this.mRecyclerView.setAdapter(this.mAdapter);
        final SpringRelativeLayout springRelativeLayout = (SpringRelativeLayout) inflate.findViewById(C0010R$id.spring_layout);
        springRelativeLayout.addSpringView(C0010R$id.apps_list);
        this.mRecyclerView.setEdgeEffectFactory(springRelativeLayout.createEdgeEffectFactory());
        this.mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(this) {
            /* class com.android.settings.datetime.timezone.BaseTimeZonePicker.AnonymousClass1 */
            int state = 0;

            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                this.state = i;
            }

            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                if (this.state == 1 && i != 0) {
                    springRelativeLayout.onRecyclerViewScrolled();
                }
            }
        });
        getLoaderManager().initLoader(0, null, new TimeZoneDataLoader.LoaderCreator(getContext(), new TimeZoneDataLoader.OnDataReadyCallback() {
            /* class com.android.settings.datetime.timezone.$$Lambda$MBKbnic3yruONZHLQGUj0vAB5hk */

            @Override // com.android.settings.datetime.timezone.model.TimeZoneDataLoader.OnDataReadyCallback
            public final void onTimeZoneDataReady(TimeZoneData timeZoneData) {
                BaseTimeZonePicker.this.onTimeZoneDataReady(timeZoneData);
            }
        }));
        return inflate;
    }

    public void onTimeZoneDataReady(TimeZoneData timeZoneData) {
        if (this.mTimeZoneData == null && timeZoneData != null) {
            this.mTimeZoneData = timeZoneData;
            BaseTimeZoneAdapter createAdapter = createAdapter(timeZoneData);
            this.mAdapter = createAdapter;
            RecyclerView recyclerView = this.mRecyclerView;
            if (recyclerView != null) {
                recyclerView.setAdapter(createAdapter);
            }
        }
    }

    /* access modifiers changed from: protected */
    public Locale getLocale() {
        return getContext().getResources().getConfiguration().getLocales().get(0);
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        if (this.mSearchEnabled) {
            menuInflater.inflate(C0013R$menu.time_zone_base_search_menu, menu);
            MenuItem findItem = menu.findItem(C0010R$id.time_zone_search_menu);
            SearchView searchView = (SearchView) findItem.getActionView();
            this.mSearchView = searchView;
            searchView.setQueryHint(getText(this.mSearchHintResId));
            this.mSearchView.setOnQueryTextListener(this);
            if (this.mDefaultExpandSearch) {
                findItem.expandActionView();
                this.mSearchView.setIconified(false);
                this.mSearchView.setActivated(true);
                this.mSearchView.setQuery("", true);
            }
            TextView textView = (TextView) this.mSearchView.findViewById(C0010R$id.search_src_text);
            Context context = this.mSearchView.getContext();
            if (OPUtils.isWhiteModeOn(context.getContentResolver())) {
                textView.setTextColor(context.getResources().getColor(C0006R$color.op_control_text_color_primary_light));
                textView.setHintTextColor(Color.parseColor("#44444444"));
            } else {
                textView.setTextColor(context.getResources().getColor(C0006R$color.op_control_text_color_primary_dark));
                textView.setHintTextColor(Color.parseColor("#88888888"));
            }
            textView.setPadding(0, textView.getPaddingTop(), 0, textView.getPaddingBottom());
            View findViewById = this.mSearchView.findViewById(C0010R$id.search_edit_frame);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) findViewById.getLayoutParams();
            layoutParams.setMarginStart(0);
            layoutParams.setMarginEnd(0);
            findViewById.setLayoutParams(layoutParams);
        }
    }

    @Override // androidx.appcompat.widget.SearchView.OnQueryTextListener
    public boolean onQueryTextChange(String str) {
        BaseTimeZoneAdapter baseTimeZoneAdapter = this.mAdapter;
        if (baseTimeZoneAdapter == null) {
            return false;
        }
        baseTimeZoneAdapter.getFilter().filter(str);
        return false;
    }
}
