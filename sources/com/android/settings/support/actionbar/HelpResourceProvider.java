package com.android.settings.support.actionbar;

import com.android.settings.C0017R$string;

public interface HelpResourceProvider {
    default int getHelpResource() {
        return C0017R$string.help_uri_default;
    }
}
