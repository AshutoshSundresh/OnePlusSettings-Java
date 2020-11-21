package com.oneplus.security;

import android.database.Cursor;
import android.database.MatrixCursor;
import android.provider.SearchIndexableResource;
import android.provider.SearchIndexablesContract;
import android.provider.SearchIndexablesProvider;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0019R$xml;
import com.oneplus.security.firewall.NetworkRestrictActivity;
import com.oneplus.security.network.view.DataUsageMainActivity;
import com.oneplus.security.utils.LogUtils;
import com.oneplus.settings.OPMemberController;

public class SecuritySearchIndexablesProvider extends SearchIndexablesProvider {
    private static SearchIndexableResource[] INDEXABLE_RES = {new SearchIndexableResource(1, C0019R$xml.data_usage_simcard_prefs, DataUsageMainActivity.class.getName(), C0008R$drawable.ic_settings_data_usage), new SearchIndexableResource(1, C0019R$xml.pref_search_network_control, NetworkRestrictActivity.class.getName(), C0008R$drawable.ic_firewall_setting)};

    public boolean onCreate() {
        return true;
    }

    public Cursor queryXmlResources(String[] strArr) {
        MatrixCursor matrixCursor = new MatrixCursor(SearchIndexablesContract.INDEXABLES_XML_RES_COLUMNS);
        int length = INDEXABLE_RES.length;
        LogUtils.d("SecuritySearchIndexablesProvider", "queryXmlResources INDEXABLE_RES.length:" + length);
        SearchIndexableResource[] searchIndexableResourceArr = INDEXABLE_RES;
        int length2 = searchIndexableResourceArr.length;
        for (int i = 0; i < length2; i++) {
            SearchIndexableResource searchIndexableResource = searchIndexableResourceArr[i];
            matrixCursor.addRow(new Object[]{Integer.valueOf(searchIndexableResource.rank), Integer.valueOf(searchIndexableResource.xmlResId), null, Integer.valueOf(searchIndexableResource.iconResId), "android.intent.action.MAIN", OPMemberController.PACKAGE_NAME, searchIndexableResource.className});
        }
        return matrixCursor;
    }

    public Cursor queryRawData(String[] strArr) {
        return new MatrixCursor(SearchIndexablesContract.INDEXABLES_RAW_COLUMNS);
    }

    public Cursor queryNonIndexableKeys(String[] strArr) {
        return new MatrixCursor(SearchIndexablesContract.NON_INDEXABLES_KEYS_COLUMNS);
    }
}
