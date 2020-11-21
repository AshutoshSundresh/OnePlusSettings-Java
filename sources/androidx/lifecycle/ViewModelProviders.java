package androidx.lifecycle;

import androidx.fragment.app.Fragment;

@Deprecated
public class ViewModelProviders {
    @Deprecated
    public static ViewModelProvider of(Fragment fragment) {
        return new ViewModelProvider(fragment);
    }
}
