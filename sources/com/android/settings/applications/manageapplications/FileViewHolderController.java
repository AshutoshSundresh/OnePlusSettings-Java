package com.android.settings.applications.manageapplications;

import androidx.fragment.app.Fragment;

public interface FileViewHolderController {
    void onClick(Fragment fragment);

    void queryStats();

    void setupView(ApplicationViewHolder applicationViewHolder);

    boolean shouldShow();
}
