package com.android.settings.panel;

import android.content.Intent;
import android.net.Uri;
import androidx.core.graphics.drawable.IconCompat;
import com.android.settingslib.core.instrumentation.Instrumentable;
import java.util.List;

public interface PanelContent extends Instrumentable {
    default CharSequence getCustomizedButtonTitle() {
        return null;
    }

    default Intent getHeaderIconIntent() {
        return null;
    }

    default IconCompat getIcon() {
        return null;
    }

    Intent getSeeMoreIntent();

    List<Uri> getSlices();

    default CharSequence getSubTitle() {
        return null;
    }

    CharSequence getTitle();

    default int getViewType() {
        return 0;
    }

    default boolean isCustomizedButtonUsed() {
        return false;
    }

    default void onClickCustomizedButton() {
    }

    default void registerCallback(PanelContentCallback panelContentCallback) {
    }
}
