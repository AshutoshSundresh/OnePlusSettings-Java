package androidx.preference;

import java.util.Set;

public abstract class PreferenceDataStore {
    public abstract boolean getBoolean(String str, boolean z);

    public abstract int getInt(String str, int i);

    public abstract String getString(String str, String str2);

    public abstract Set<String> getStringSet(String str, Set<String> set);

    public abstract void putBoolean(String str, boolean z);

    public abstract void putInt(String str, int i);

    public abstract void putString(String str, String str2);

    public abstract void putStringSet(String str, Set<String> set);
}
