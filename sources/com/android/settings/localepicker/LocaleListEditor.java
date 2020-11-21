package com.android.settings.localepicker;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.LocaleList;
import android.provider.SearchIndexableData;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import com.android.internal.app.LocalePicker;
import com.android.internal.app.LocaleStore;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0015R$plurals;
import com.android.settings.C0017R$string;
import com.android.settings.RestrictedSettingsFragment;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.SearchIndexableRaw;
import java.util.ArrayList;
import java.util.List;

public class LocaleListEditor extends RestrictedSettingsFragment {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider() {
        /* class com.android.settings.localepicker.LocaleListEditor.AnonymousClass7 */

        @Override // com.android.settingslib.search.Indexable$SearchIndexProvider, com.android.settings.search.BaseSearchIndexProvider
        public List<SearchIndexableRaw> getRawDataToIndex(Context context, boolean z) {
            Resources resources = context.getResources();
            ArrayList arrayList = new ArrayList();
            SearchIndexableRaw searchIndexableRaw = new SearchIndexableRaw(context);
            ((SearchIndexableData) searchIndexableRaw).key = "add_language";
            searchIndexableRaw.title = resources.getString(C0017R$string.add_a_language);
            searchIndexableRaw.keywords = resources.getString(C0017R$string.keywords_add_language);
            arrayList.add(searchIndexableRaw);
            return arrayList;
        }
    };
    private LocaleDragAndDropAdapter mAdapter;
    private View mAddLanguage;
    private boolean mIsUiRestricted;
    private Menu mMenu;
    private boolean mRemoveMode;
    private boolean mShowingRemoveDialog;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 344;
    }

    public LocaleListEditor() {
        super("no_config_locale");
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.RestrictedSettingsFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setHasOptionsMenu(true);
        LocaleStore.fillCache(getContext());
        this.mAdapter = new LocaleDragAndDropAdapter(getContext(), getUserLocaleList());
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View onCreateView = super.onCreateView(layoutInflater, viewGroup, bundle);
        configureDragAndDrop(layoutInflater.inflate(C0012R$layout.locale_order_list, (ViewGroup) onCreateView));
        return onCreateView;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.RestrictedSettingsFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        boolean z = this.mIsUiRestricted;
        this.mIsUiRestricted = isUiRestricted();
        TextView emptyTextView = getEmptyTextView();
        if (this.mIsUiRestricted && !z) {
            emptyTextView.setText(C0017R$string.language_empty_list_user_restricted);
            emptyTextView.setVisibility(0);
            updateVisibilityOfRemoveMenu();
        } else if (!this.mIsUiRestricted && z) {
            emptyTextView.setVisibility(8);
            updateVisibilityOfRemoveMenu();
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onViewStateRestored(Bundle bundle) {
        super.onViewStateRestored(bundle);
        if (bundle != null) {
            this.mRemoveMode = bundle.getBoolean("localeRemoveMode", false);
            this.mShowingRemoveDialog = bundle.getBoolean("showingLocaleRemoveDialog", false);
        }
        setRemoveMode(this.mRemoveMode);
        this.mAdapter.restoreState(bundle);
        if (this.mShowingRemoveDialog) {
            showRemoveLocaleWarningDialog();
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.RestrictedSettingsFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putBoolean("localeRemoveMode", this.mRemoveMode);
        bundle.putBoolean("showingLocaleRemoveDialog", this.mShowingRemoveDialog);
        this.mAdapter.saveState(bundle);
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == 2) {
            if (this.mRemoveMode) {
                showRemoveLocaleWarningDialog();
            } else {
                setRemoveMode(true);
            }
            return true;
        } else if (itemId != 16908332 || !this.mRemoveMode) {
            return super.onOptionsItemSelected(menuItem);
        } else {
            setRemoveMode(false);
            return true;
        }
    }

    @Override // com.android.settings.RestrictedSettingsFragment, androidx.fragment.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        if (i == 0 && i2 == -1 && intent != null) {
            this.mAdapter.addLocale(intent.getSerializableExtra("localeInfo"));
            updateVisibilityOfRemoveMenu();
        }
        super.onActivityResult(i, i2, intent);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void setRemoveMode(boolean z) {
        this.mRemoveMode = z;
        this.mAdapter.setRemoveMode(z);
        this.mAddLanguage.setVisibility(z ? 4 : 0);
        updateVisibilityOfRemoveMenu();
    }

    /* access modifiers changed from: package-private */
    public void showRemoveLocaleWarningDialog() {
        int checkedCount = this.mAdapter.getCheckedCount();
        if (checkedCount == 0) {
            setRemoveMode(!this.mRemoveMode);
        } else if (checkedCount == this.mAdapter.getItemCount()) {
            this.mShowingRemoveDialog = true;
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(C0017R$string.dlg_remove_locales_error_title);
            builder.setMessage(C0017R$string.dlg_remove_locales_error_message);
            builder.setPositiveButton(17039379, new DialogInterface.OnClickListener(this) {
                /* class com.android.settings.localepicker.LocaleListEditor.AnonymousClass2 */

                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                /* class com.android.settings.localepicker.LocaleListEditor.AnonymousClass1 */

                public void onDismiss(DialogInterface dialogInterface) {
                    LocaleListEditor.this.mShowingRemoveDialog = false;
                }
            });
            builder.create().show();
        } else {
            String quantityString = getResources().getQuantityString(C0015R$plurals.dlg_remove_locales_title, checkedCount);
            this.mShowingRemoveDialog = true;
            AlertDialog.Builder builder2 = new AlertDialog.Builder(getActivity());
            if (this.mAdapter.isFirstLocaleChecked()) {
                builder2.setMessage(C0017R$string.dlg_remove_locales_message);
            }
            builder2.setTitle(quantityString);
            builder2.setNegativeButton(17039369, new DialogInterface.OnClickListener() {
                /* class com.android.settings.localepicker.LocaleListEditor.AnonymousClass5 */

                public void onClick(DialogInterface dialogInterface, int i) {
                    LocaleListEditor.this.setRemoveMode(false);
                }
            });
            builder2.setPositiveButton(C0017R$string.locale_remove_menu, new DialogInterface.OnClickListener() {
                /* class com.android.settings.localepicker.LocaleListEditor.AnonymousClass4 */

                public void onClick(DialogInterface dialogInterface, int i) {
                    LocaleListEditor.this.mRemoveMode = false;
                    LocaleListEditor.this.mShowingRemoveDialog = false;
                    LocaleListEditor.this.mAdapter.removeChecked();
                    LocaleListEditor.this.setRemoveMode(false);
                }
            });
            builder2.setOnDismissListener(new DialogInterface.OnDismissListener() {
                /* class com.android.settings.localepicker.LocaleListEditor.AnonymousClass3 */

                public void onDismiss(DialogInterface dialogInterface) {
                    LocaleListEditor.this.mShowingRemoveDialog = false;
                }
            });
            builder2.create().show();
        }
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        MenuItem add = menu.add(0, 2, 0, C0017R$string.locale_remove_menu);
        add.setShowAsAction(4);
        add.setIcon(C0008R$drawable.op_ic_delete);
        super.onCreateOptionsMenu(menu, menuInflater);
        this.mMenu = menu;
        updateVisibilityOfRemoveMenu();
    }

    private List<LocaleStore.LocaleInfo> getUserLocaleList() {
        ArrayList arrayList = new ArrayList();
        LocaleList locales = LocalePicker.getLocales();
        for (int i = 0; i < locales.size(); i++) {
            arrayList.add(LocaleStore.getLocaleInfo(locales.get(i)));
        }
        return arrayList;
    }

    private void configureDragAndDrop(View view) {
        RecyclerView recyclerView = (RecyclerView) view.findViewById(C0010R$id.dragList);
        LocaleLinearLayoutManager localeLinearLayoutManager = new LocaleLinearLayoutManager(getContext(), this.mAdapter);
        localeLinearLayoutManager.setAutoMeasureEnabled(true);
        recyclerView.setLayoutManager(localeLinearLayoutManager);
        recyclerView.setHasFixedSize(true);
        this.mAdapter.setRecyclerView(recyclerView);
        recyclerView.setAdapter(this.mAdapter);
        View findViewById = view.findViewById(C0010R$id.add_language);
        this.mAddLanguage = findViewById;
        findViewById.setOnClickListener(new View.OnClickListener() {
            /* class com.android.settings.localepicker.LocaleListEditor.AnonymousClass6 */

            public void onClick(View view) {
                FeatureFactory.getFactory(LocaleListEditor.this.getContext()).getMetricsFeatureProvider().logSettingsTileClick("add_language", LocaleListEditor.this.getMetricsCategory());
                LocaleListEditor.this.startActivityForResult(new Intent(LocaleListEditor.this.getActivity(), LocalePickerWithRegionActivity.class), 0);
            }
        });
    }

    private void updateVisibilityOfRemoveMenu() {
        Menu menu = this.mMenu;
        if (menu != null) {
            int i = 2;
            MenuItem findItem = menu.findItem(2);
            if (findItem != null) {
                boolean z = false;
                if (!this.mRemoveMode) {
                    i = 0;
                }
                findItem.setShowAsAction(i);
                if ((this.mAdapter.getItemCount() > 1) && !this.mIsUiRestricted) {
                    z = true;
                }
                findItem.setVisible(z);
            }
        }
    }
}
