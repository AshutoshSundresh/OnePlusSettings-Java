package androidx.slice.compat;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.SystemClock;
import android.text.TextUtils;
import androidx.collection.ArraySet;
import androidx.core.util.ObjectsCompat;
import androidx.slice.SliceSpec;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CompatPinnedList {
    private final Context mContext;
    private final String mPrefsName;

    public CompatPinnedList(Context context, String str) {
        this.mContext = context;
        this.mPrefsName = str;
    }

    private SharedPreferences getPrefs() {
        SharedPreferences sharedPreferences = this.mContext.getSharedPreferences(this.mPrefsName, 0);
        long j = sharedPreferences.getLong("last_boot", 0);
        long bootTime = getBootTime();
        if (Math.abs(j - bootTime) > 2000) {
            sharedPreferences.edit().clear().putLong("last_boot", bootTime).apply();
        }
        return sharedPreferences;
    }

    public List<Uri> getPinnedSlices() {
        ArrayList arrayList = new ArrayList();
        for (String str : getPrefs().getAll().keySet()) {
            if (str.startsWith("pinned_")) {
                Uri parse = Uri.parse(str.substring(7));
                if (!getPins(parse).isEmpty()) {
                    arrayList.add(parse);
                }
            }
        }
        return arrayList;
    }

    private Set<String> getPins(Uri uri) {
        SharedPreferences prefs = getPrefs();
        return prefs.getStringSet("pinned_" + uri.toString(), new ArraySet());
    }

    public synchronized ArraySet<SliceSpec> getSpecs(Uri uri) {
        ArraySet<SliceSpec> arraySet = new ArraySet<>();
        SharedPreferences prefs = getPrefs();
        String string = prefs.getString("spec_names_" + uri.toString(), null);
        String string2 = prefs.getString("spec_revs_" + uri.toString(), null);
        if (!TextUtils.isEmpty(string)) {
            if (!TextUtils.isEmpty(string2)) {
                String[] split = string.split(",", -1);
                String[] split2 = string2.split(",", -1);
                if (split.length != split2.length) {
                    return new ArraySet<>();
                }
                for (int i = 0; i < split.length; i++) {
                    arraySet.add(new SliceSpec(split[i], Integer.parseInt(split2[i])));
                }
                return arraySet;
            }
        }
        return new ArraySet<>();
    }

    private void setPins(Uri uri, Set<String> set) {
        SharedPreferences.Editor edit = getPrefs().edit();
        edit.putStringSet("pinned_" + uri.toString(), set).apply();
    }

    private void setSpecs(Uri uri, ArraySet<SliceSpec> arraySet) {
        String[] strArr = new String[arraySet.size()];
        String[] strArr2 = new String[arraySet.size()];
        for (int i = 0; i < arraySet.size(); i++) {
            strArr[i] = arraySet.valueAt(i).getType();
            strArr2[i] = String.valueOf(arraySet.valueAt(i).getRevision());
        }
        SharedPreferences.Editor edit = getPrefs().edit();
        SharedPreferences.Editor putString = edit.putString("spec_names_" + uri.toString(), TextUtils.join(",", strArr));
        putString.putString("spec_revs_" + uri.toString(), TextUtils.join(",", strArr2)).apply();
    }

    /* access modifiers changed from: protected */
    public long getBootTime() {
        return System.currentTimeMillis() - SystemClock.elapsedRealtime();
    }

    public synchronized boolean addPin(Uri uri, String str, Set<SliceSpec> set) {
        boolean isEmpty;
        Set<String> pins = getPins(uri);
        isEmpty = pins.isEmpty();
        pins.add(str);
        setPins(uri, pins);
        if (isEmpty) {
            setSpecs(uri, new ArraySet<>(set));
        } else {
            ArraySet<SliceSpec> specs = getSpecs(uri);
            mergeSpecs(specs, set);
            setSpecs(uri, specs);
        }
        return isEmpty;
    }

    public synchronized boolean removePin(Uri uri, String str) {
        Set<String> pins = getPins(uri);
        boolean z = false;
        if (!pins.isEmpty()) {
            if (pins.contains(str)) {
                pins.remove(str);
                setPins(uri, pins);
                setSpecs(uri, new ArraySet<>());
                if (pins.size() == 0) {
                    z = true;
                }
                return z;
            }
        }
        return false;
    }

    private static ArraySet<SliceSpec> mergeSpecs(ArraySet<SliceSpec> arraySet, Set<SliceSpec> set) {
        int i;
        int i2 = 0;
        while (i2 < arraySet.size()) {
            SliceSpec valueAt = arraySet.valueAt(i2);
            SliceSpec findSpec = findSpec(set, valueAt.getType());
            if (findSpec == null) {
                i = i2 - 1;
                arraySet.removeAt(i2);
            } else if (findSpec.getRevision() < valueAt.getRevision()) {
                i = i2 - 1;
                arraySet.removeAt(i2);
                arraySet.add(findSpec);
            } else {
                i2++;
            }
            i2 = i;
            i2++;
        }
        return arraySet;
    }

    private static SliceSpec findSpec(Set<SliceSpec> set, String str) {
        for (SliceSpec sliceSpec : set) {
            if (ObjectsCompat.equals(sliceSpec.getType(), str)) {
                return sliceSpec;
            }
        }
        return null;
    }
}
