package com.android.settings.homepage.contextualcards;

import java.util.List;
import java.util.Map;

public interface ContextualCardUpdateListener {
    void onContextualCardUpdated(Map<Integer, List<ContextualCard>> map);
}
