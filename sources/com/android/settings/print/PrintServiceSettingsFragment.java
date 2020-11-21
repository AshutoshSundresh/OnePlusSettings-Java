package com.android.settings.print;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.print.PrintManager;
import android.print.PrinterDiscoverySession;
import android.print.PrinterId;
import android.print.PrinterInfo;
import android.printservice.PrintServiceInfo;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityManager;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Switch;
import android.widget.TextView;
import androidx.fragment.app.FragmentActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.RecyclerView;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0013R$menu;
import com.android.settings.C0017R$string;
import com.android.settings.SettingsActivity;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.print.PrintServiceSettingsFragment;
import com.android.settings.widget.SwitchBar;
import com.android.settings.widget.ToggleSwitch;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PrintServiceSettingsFragment extends SettingsPreferenceFragment implements SwitchBar.OnSwitchChangeListener, LoaderManager.LoaderCallbacks<List<PrintServiceInfo>> {
    private Intent mAddPrintersIntent;
    private ComponentName mComponentName;
    private final RecyclerView.AdapterDataObserver mDataObserver = new RecyclerView.AdapterDataObserver() {
        /* class com.android.settings.print.PrintServiceSettingsFragment.AnonymousClass1 */

        @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
        public void onChanged() {
            invalidateOptionsMenuIfNeeded();
            PrintServiceSettingsFragment.this.updateEmptyView();
        }

        private void invalidateOptionsMenuIfNeeded() {
            int unfilteredCount = PrintServiceSettingsFragment.this.mPrintersAdapter.getUnfilteredCount();
            if ((PrintServiceSettingsFragment.this.mLastUnfilteredItemCount <= 0 && unfilteredCount > 0) || (PrintServiceSettingsFragment.this.mLastUnfilteredItemCount > 0 && unfilteredCount <= 0)) {
                PrintServiceSettingsFragment.this.getActivity().invalidateOptionsMenu();
            }
            PrintServiceSettingsFragment.this.mLastUnfilteredItemCount = unfilteredCount;
        }
    };
    private int mLastUnfilteredItemCount;
    private String mPreferenceKey;
    private PrintersAdapter mPrintersAdapter;
    private SearchView mSearchView;
    private boolean mServiceEnabled;
    private Intent mSettingsIntent;
    private SwitchBar mSwitchBar;
    private ToggleSwitch mToggleSwitch;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 79;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        String string = getArguments().getString("EXTRA_TITLE");
        if (!TextUtils.isEmpty(string)) {
            getActivity().setTitle(string);
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View onCreateView = super.onCreateView(layoutInflater, viewGroup, bundle);
        this.mServiceEnabled = getArguments().getBoolean("EXTRA_CHECKED");
        return onCreateView;
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onStart() {
        super.onStart();
        initComponents();
        updateUiForArguments();
        updateEmptyView();
        updateUiForServiceState();
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onPause() {
        SearchView searchView = this.mSearchView;
        if (searchView != null) {
            searchView.setOnQueryTextListener(null);
        }
        super.onPause();
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onStop() {
        super.onStop();
        this.mSwitchBar.removeOnSwitchChangeListener(this);
        this.mSwitchBar.hide();
        this.mPrintersAdapter.unregisterAdapterDataObserver(this.mDataObserver);
    }

    private void onPreferenceToggled(String str, boolean z) {
        ((PrintManager) getContext().getSystemService("print")).setPrintServiceEnabled(this.mComponentName, z);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    @Override // com.android.settings.SettingsPreferenceFragment
    private void updateEmptyView() {
        ViewGroup viewGroup = (ViewGroup) getListView().getParent();
        View emptyView = getEmptyView();
        if (!this.mToggleSwitch.isChecked()) {
            if (emptyView != null) {
                viewGroup.removeView(emptyView);
                emptyView = null;
            }
            if (emptyView == null) {
                View inflate = getActivity().getLayoutInflater().inflate(C0012R$layout.empty_print_state, viewGroup, false);
                ((TextView) inflate.findViewById(C0010R$id.message)).setText(C0017R$string.print_service_disabled);
                viewGroup.addView(inflate);
                setEmptyView(inflate);
            }
        } else if (this.mPrintersAdapter.getUnfilteredCount() <= 0) {
            if (emptyView != null) {
                viewGroup.removeView(emptyView);
                emptyView = null;
            }
            if (emptyView == null) {
                View inflate2 = getActivity().getLayoutInflater().inflate(C0012R$layout.empty_printers_list_service_enabled, viewGroup, false);
                viewGroup.addView(inflate2);
                setEmptyView(inflate2);
            }
        } else if (this.mPrintersAdapter.getItemCount() <= 0) {
            if (emptyView != null) {
                viewGroup.removeView(emptyView);
                emptyView = null;
            }
            if (emptyView == null) {
                View inflate3 = getActivity().getLayoutInflater().inflate(C0012R$layout.empty_print_state, viewGroup, false);
                ((TextView) inflate3.findViewById(C0010R$id.message)).setText(C0017R$string.print_no_printers_found);
                viewGroup.addView(inflate3);
                setEmptyView(inflate3);
            }
        } else if (this.mPrintersAdapter.getItemCount() > 0 && emptyView != null) {
            viewGroup.removeView(emptyView);
        }
    }

    private void updateUiForServiceState() {
        if (this.mServiceEnabled) {
            this.mSwitchBar.setCheckedInternal(true);
            this.mPrintersAdapter.enable();
        } else {
            this.mSwitchBar.setCheckedInternal(false);
            this.mPrintersAdapter.disable();
        }
        getActivity().invalidateOptionsMenu();
    }

    private void initComponents() {
        PrintersAdapter printersAdapter = new PrintersAdapter();
        this.mPrintersAdapter = printersAdapter;
        printersAdapter.registerAdapterDataObserver(this.mDataObserver);
        SwitchBar switchBar = ((SettingsActivity) getActivity()).getSwitchBar();
        this.mSwitchBar = switchBar;
        switchBar.addOnSwitchChangeListener(this);
        this.mSwitchBar.show();
        ToggleSwitch toggleSwitch = this.mSwitchBar.getSwitch();
        this.mToggleSwitch = toggleSwitch;
        toggleSwitch.setOnBeforeCheckedChangeListener(new ToggleSwitch.OnBeforeCheckedChangeListener() {
            /* class com.android.settings.print.$$Lambda$PrintServiceSettingsFragment$wJmgZtEcdbSu_UC74HchKS4YJ3w */

            @Override // com.android.settings.widget.ToggleSwitch.OnBeforeCheckedChangeListener
            public final boolean onBeforeCheckedChanged(ToggleSwitch toggleSwitch, boolean z) {
                return PrintServiceSettingsFragment.this.lambda$initComponents$0$PrintServiceSettingsFragment(toggleSwitch, z);
            }
        });
        getListView().setAdapter(this.mPrintersAdapter);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$initComponents$0 */
    public /* synthetic */ boolean lambda$initComponents$0$PrintServiceSettingsFragment(ToggleSwitch toggleSwitch, boolean z) {
        onPreferenceToggled(this.mPreferenceKey, z);
        return false;
    }

    @Override // com.android.settings.widget.SwitchBar.OnSwitchChangeListener
    public void onSwitchChanged(Switch r1, boolean z) {
        updateEmptyView();
    }

    private void updateUiForArguments() {
        Bundle arguments = getArguments();
        ComponentName unflattenFromString = ComponentName.unflattenFromString(arguments.getString("EXTRA_SERVICE_COMPONENT_NAME"));
        this.mComponentName = unflattenFromString;
        this.mPreferenceKey = unflattenFromString.flattenToString();
        this.mSwitchBar.setCheckedInternal(arguments.getBoolean("EXTRA_CHECKED"));
        getLoaderManager().initLoader(2, null, this);
        setHasOptionsMenu(true);
    }

    @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
    public Loader<List<PrintServiceInfo>> onCreateLoader(int i, Bundle bundle) {
        return new SettingsPrintServicesLoader((PrintManager) getContext().getSystemService("print"), getContext(), 3);
    }

    /* JADX WARNING: Removed duplicated region for block: B:10:0x002a  */
    /* JADX WARNING: Removed duplicated region for block: B:13:0x003b  */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x0071  */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x0079  */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x00af  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onLoadFinished(androidx.loader.content.Loader<java.util.List<android.printservice.PrintServiceInfo>> r7, java.util.List<android.printservice.PrintServiceInfo> r8) {
        /*
        // Method dump skipped, instructions count: 181
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.print.PrintServiceSettingsFragment.onLoadFinished(androidx.loader.content.Loader, java.util.List):void");
    }

    @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
    public void onLoaderReset(Loader<List<PrintServiceInfo>> loader) {
        updateUiForServiceState();
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        Intent intent;
        Intent intent2;
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(C0013R$menu.print_service_settings, menu);
        MenuItem findItem = menu.findItem(C0010R$id.print_menu_item_add_printer);
        if (!this.mServiceEnabled || (intent2 = this.mAddPrintersIntent) == null) {
            menu.removeItem(C0010R$id.print_menu_item_add_printer);
        } else {
            findItem.setIntent(intent2);
        }
        MenuItem findItem2 = menu.findItem(C0010R$id.print_menu_item_settings);
        if (!this.mServiceEnabled || (intent = this.mSettingsIntent) == null) {
            menu.removeItem(C0010R$id.print_menu_item_settings);
        } else {
            findItem2.setIntent(intent);
        }
        MenuItem findItem3 = menu.findItem(C0010R$id.print_menu_item_search);
        if (!this.mServiceEnabled || this.mPrintersAdapter.getUnfilteredCount() <= 0) {
            menu.removeItem(C0010R$id.print_menu_item_search);
            return;
        }
        SearchView searchView = (SearchView) findItem3.getActionView();
        this.mSearchView = searchView;
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            /* class com.android.settings.print.PrintServiceSettingsFragment.AnonymousClass2 */

            public boolean onQueryTextSubmit(String str) {
                return true;
            }

            public boolean onQueryTextChange(String str) {
                PrintServiceSettingsFragment.this.mPrintersAdapter.getFilter().filter(str);
                return true;
            }
        });
        this.mSearchView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            /* class com.android.settings.print.PrintServiceSettingsFragment.AnonymousClass3 */

            public void onViewAttachedToWindow(View view) {
                if (AccessibilityManager.getInstance(PrintServiceSettingsFragment.this.getActivity()).isEnabled()) {
                    view.announceForAccessibility(PrintServiceSettingsFragment.this.getString(C0017R$string.print_search_box_shown_utterance));
                }
            }

            public void onViewDetachedFromWindow(View view) {
                FragmentActivity activity = PrintServiceSettingsFragment.this.getActivity();
                if (activity != null && !activity.isFinishing() && AccessibilityManager.getInstance(activity).isEnabled()) {
                    view.announceForAccessibility(PrintServiceSettingsFragment.this.getString(C0017R$string.print_search_box_hidden_utterance));
                }
            }
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View view) {
            super(view);
        }
    }

    /* access modifiers changed from: private */
    public final class PrintersAdapter extends RecyclerView.Adapter<ViewHolder> implements LoaderManager.LoaderCallbacks<List<PrinterInfo>>, Filterable {
        private final List<PrinterInfo> mFilteredPrinters;
        private CharSequence mLastSearchString;
        private final Object mLock;
        private final List<PrinterInfo> mPrinters;

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public long getItemId(int i) {
            return (long) i;
        }

        private PrintersAdapter() {
            this.mLock = new Object();
            this.mPrinters = new ArrayList();
            this.mFilteredPrinters = new ArrayList();
        }

        public void enable() {
            PrintServiceSettingsFragment.this.getLoaderManager().initLoader(1, null, this);
        }

        public void disable() {
            PrintServiceSettingsFragment.this.getLoaderManager().destroyLoader(1);
            this.mPrinters.clear();
        }

        public int getUnfilteredCount() {
            return this.mPrinters.size();
        }

        public Filter getFilter() {
            return new Filter() {
                /* class com.android.settings.print.PrintServiceSettingsFragment.PrintersAdapter.AnonymousClass1 */

                /* access modifiers changed from: protected */
                public Filter.FilterResults performFiltering(CharSequence charSequence) {
                    synchronized (PrintersAdapter.this.mLock) {
                        if (TextUtils.isEmpty(charSequence)) {
                            return null;
                        }
                        Filter.FilterResults filterResults = new Filter.FilterResults();
                        ArrayList arrayList = new ArrayList();
                        String lowerCase = charSequence.toString().toLowerCase();
                        int size = PrintersAdapter.this.mPrinters.size();
                        for (int i = 0; i < size; i++) {
                            PrinterInfo printerInfo = (PrinterInfo) PrintersAdapter.this.mPrinters.get(i);
                            String name = printerInfo.getName();
                            if (name != null && name.toLowerCase().contains(lowerCase)) {
                                arrayList.add(printerInfo);
                            }
                        }
                        filterResults.values = arrayList;
                        filterResults.count = arrayList.size();
                        return filterResults;
                    }
                }

                /* access modifiers changed from: protected */
                public void publishResults(CharSequence charSequence, Filter.FilterResults filterResults) {
                    synchronized (PrintersAdapter.this.mLock) {
                        PrintersAdapter.this.mLastSearchString = charSequence;
                        PrintersAdapter.this.mFilteredPrinters.clear();
                        if (filterResults == null) {
                            PrintersAdapter.this.mFilteredPrinters.addAll(PrintersAdapter.this.mPrinters);
                        } else {
                            PrintersAdapter.this.mFilteredPrinters.addAll((List) filterResults.values);
                        }
                    }
                    PrintersAdapter.this.notifyDataSetChanged();
                }
            };
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            int size;
            synchronized (this.mLock) {
                size = this.mFilteredPrinters.size();
            }
            return size;
        }

        private Object getItem(int i) {
            PrinterInfo printerInfo;
            synchronized (this.mLock) {
                printerInfo = this.mFilteredPrinters.get(i);
            }
            return printerInfo;
        }

        public boolean isActionable(int i) {
            return ((PrinterInfo) getItem(i)).getStatus() != 3;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(C0012R$layout.printer_dropdown_item, viewGroup, false));
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            viewHolder.itemView.setEnabled(isActionable(i));
            final PrinterInfo printerInfo = (PrinterInfo) getItem(i);
            String name = printerInfo.getName();
            String description = printerInfo.getDescription();
            Drawable loadIcon = printerInfo.loadIcon(PrintServiceSettingsFragment.this.getActivity());
            ((TextView) viewHolder.itemView.findViewById(C0010R$id.title)).setText(name);
            TextView textView = (TextView) viewHolder.itemView.findViewById(C0010R$id.subtitle);
            if (!TextUtils.isEmpty(description)) {
                textView.setText(description);
                textView.setVisibility(0);
            } else {
                textView.setText((CharSequence) null);
                textView.setVisibility(8);
            }
            LinearLayout linearLayout = (LinearLayout) viewHolder.itemView.findViewById(C0010R$id.more_info);
            if (printerInfo.getInfoIntent() != null) {
                linearLayout.setVisibility(0);
                linearLayout.setOnClickListener(new View.OnClickListener() {
                    /* class com.android.settings.print.PrintServiceSettingsFragment.PrintersAdapter.AnonymousClass2 */

                    public void onClick(View view) {
                        try {
                            PrintServiceSettingsFragment.this.getActivity().startIntentSender(printerInfo.getInfoIntent().getIntentSender(), null, 0, 0, 0);
                        } catch (IntentSender.SendIntentException e) {
                            Log.e("PrintServiceSettings", "Could not execute pending info intent: %s", e);
                        }
                    }
                });
            } else {
                linearLayout.setVisibility(8);
            }
            ImageView imageView = (ImageView) viewHolder.itemView.findViewById(C0010R$id.icon);
            if (loadIcon != null) {
                imageView.setVisibility(0);
                if (!isActionable(i)) {
                    loadIcon.mutate();
                    TypedValue typedValue = new TypedValue();
                    PrintServiceSettingsFragment.this.getActivity().getTheme().resolveAttribute(16842803, typedValue, true);
                    loadIcon.setAlpha((int) (typedValue.getFloat() * 255.0f));
                }
                imageView.setImageDrawable(loadIcon);
            } else {
                imageView.setVisibility(8);
            }
            viewHolder.itemView.setOnClickListener(new View.OnClickListener(i) {
                /* class com.android.settings.print.$$Lambda$PrintServiceSettingsFragment$PrintersAdapter$F1U5y4I3bQpDRp4sOs1YaH6Wo0 */
                public final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void onClick(View view) {
                    PrintServiceSettingsFragment.PrintersAdapter.this.lambda$onBindViewHolder$0$PrintServiceSettingsFragment$PrintersAdapter(this.f$1, view);
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$onBindViewHolder$0 */
        public /* synthetic */ void lambda$onBindViewHolder$0$PrintServiceSettingsFragment$PrintersAdapter(int i, View view) {
            PrinterInfo printerInfo = (PrinterInfo) getItem(i);
            if (printerInfo.getInfoIntent() != null) {
                try {
                    PrintServiceSettingsFragment.this.getActivity().startIntentSender(printerInfo.getInfoIntent().getIntentSender(), null, 0, 0, 0);
                } catch (IntentSender.SendIntentException e) {
                    Log.e("PrintServiceSettings", "Could not execute info intent: %s", e);
                }
            }
        }

        @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
        public Loader<List<PrinterInfo>> onCreateLoader(int i, Bundle bundle) {
            if (i == 1) {
                return new PrintersLoader(PrintServiceSettingsFragment.this.getContext());
            }
            return null;
        }

        public void onLoadFinished(Loader<List<PrinterInfo>> loader, List<PrinterInfo> list) {
            synchronized (this.mLock) {
                this.mPrinters.clear();
                int size = list.size();
                for (int i = 0; i < size; i++) {
                    PrinterInfo printerInfo = list.get(i);
                    if (printerInfo.getId().getServiceName().equals(PrintServiceSettingsFragment.this.mComponentName)) {
                        this.mPrinters.add(printerInfo);
                    }
                }
                this.mFilteredPrinters.clear();
                this.mFilteredPrinters.addAll(this.mPrinters);
                if (!TextUtils.isEmpty(this.mLastSearchString)) {
                    getFilter().filter(this.mLastSearchString);
                }
            }
            notifyDataSetChanged();
        }

        @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
        public void onLoaderReset(Loader<List<PrinterInfo>> loader) {
            synchronized (this.mLock) {
                this.mPrinters.clear();
                this.mFilteredPrinters.clear();
                this.mLastSearchString = null;
            }
            notifyDataSetChanged();
        }
    }

    private static class PrintersLoader extends Loader<List<PrinterInfo>> {
        private PrinterDiscoverySession mDiscoverySession;
        private final Map<PrinterId, PrinterInfo> mPrinters = new LinkedHashMap();

        public PrintersLoader(Context context) {
            super(context);
        }

        public void deliverResult(List<PrinterInfo> list) {
            if (isStarted()) {
                super.deliverResult((Object) list);
            }
        }

        /* access modifiers changed from: protected */
        @Override // androidx.loader.content.Loader
        public void onStartLoading() {
            if (!this.mPrinters.isEmpty()) {
                deliverResult((List<PrinterInfo>) new ArrayList(this.mPrinters.values()));
            }
            onForceLoad();
        }

        /* access modifiers changed from: protected */
        @Override // androidx.loader.content.Loader
        public void onStopLoading() {
            onCancelLoad();
        }

        /* access modifiers changed from: protected */
        @Override // androidx.loader.content.Loader
        public void onForceLoad() {
            loadInternal();
        }

        /* access modifiers changed from: protected */
        @Override // androidx.loader.content.Loader
        public boolean onCancelLoad() {
            return cancelInternal();
        }

        /* access modifiers changed from: protected */
        @Override // androidx.loader.content.Loader
        public void onReset() {
            onStopLoading();
            this.mPrinters.clear();
            PrinterDiscoverySession printerDiscoverySession = this.mDiscoverySession;
            if (printerDiscoverySession != null) {
                printerDiscoverySession.destroy();
                this.mDiscoverySession = null;
            }
        }

        /* access modifiers changed from: protected */
        @Override // androidx.loader.content.Loader
        public void onAbandon() {
            onStopLoading();
        }

        private boolean cancelInternal() {
            PrinterDiscoverySession printerDiscoverySession = this.mDiscoverySession;
            if (printerDiscoverySession == null || !printerDiscoverySession.isPrinterDiscoveryStarted()) {
                return false;
            }
            this.mDiscoverySession.stopPrinterDiscovery();
            return true;
        }

        private void loadInternal() {
            if (this.mDiscoverySession == null) {
                PrinterDiscoverySession createPrinterDiscoverySession = ((PrintManager) getContext().getSystemService("print")).createPrinterDiscoverySession();
                this.mDiscoverySession = createPrinterDiscoverySession;
                createPrinterDiscoverySession.setOnPrintersChangeListener(new PrinterDiscoverySession.OnPrintersChangeListener() {
                    /* class com.android.settings.print.PrintServiceSettingsFragment.PrintersLoader.AnonymousClass1 */

                    public void onPrintersChanged() {
                        PrintersLoader.this.deliverResult((List<PrinterInfo>) new ArrayList(PrintersLoader.this.mDiscoverySession.getPrinters()));
                    }
                });
            }
            this.mDiscoverySession.startPrinterDiscovery((List) null);
        }
    }
}
