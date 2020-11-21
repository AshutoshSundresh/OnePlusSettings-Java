package com.android.settings.users;

import android.os.UserHandle;
import java.util.List;

public interface UserFeatureProvider {
    List<UserHandle> getUserProfiles();
}
