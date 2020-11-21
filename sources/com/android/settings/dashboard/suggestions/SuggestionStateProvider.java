package com.android.settings.dashboard.suggestions;

import android.content.ComponentName;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import com.android.settings.overlay.FeatureFactory;

public class SuggestionStateProvider extends ContentProvider {
    static final String EXTRA_CANDIDATE_ID = "candidate_id";
    static final String METHOD_GET_SUGGESTION_STATE = "getSuggestionState";

    public boolean onCreate() {
        return true;
    }

    public Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        throw new UnsupportedOperationException("query operation not supported currently.");
    }

    public String getType(Uri uri) {
        throw new UnsupportedOperationException("getType operation not supported currently.");
    }

    public Uri insert(Uri uri, ContentValues contentValues) {
        throw new UnsupportedOperationException("insert operation not supported currently.");
    }

    public int delete(Uri uri, String str, String[] strArr) {
        throw new UnsupportedOperationException("delete operation not supported currently.");
    }

    public int update(Uri uri, ContentValues contentValues, String str, String[] strArr) {
        throw new UnsupportedOperationException("update operation not supported currently.");
    }

    public Bundle call(String str, String str2, Bundle bundle) {
        boolean z;
        Bundle bundle2 = new Bundle();
        if (METHOD_GET_SUGGESTION_STATE.equals(str)) {
            String string = bundle.getString(EXTRA_CANDIDATE_ID);
            ComponentName componentName = (ComponentName) bundle.getParcelable("android.intent.extra.COMPONENT_NAME");
            if (componentName == null) {
                z = true;
            } else {
                Context context = getContext();
                z = FeatureFactory.getFactory(context).getSuggestionFeatureProvider(context).isSuggestionComplete(context, componentName);
            }
            Log.d("SugstStatusProvider", "Suggestion " + string + " complete: " + z);
            bundle2.putBoolean("candidate_is_complete", z);
        }
        return bundle2;
    }
}
