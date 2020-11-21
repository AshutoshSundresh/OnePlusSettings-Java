package androidx.preference;

import android.app.Fragment;
import androidx.preference.DialogPreference;
import androidx.preference.PreferenceManager;

@Deprecated
public abstract class PreferenceFragment extends Fragment implements PreferenceManager.OnPreferenceTreeClickListener, PreferenceManager.OnDisplayPreferenceDialogListener, PreferenceManager.OnNavigateToScreenListener, DialogPreference.TargetFragment {
}
