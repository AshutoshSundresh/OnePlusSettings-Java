package android.support.v4.media;

import android.os.Bundle;
import java.util.List;

public abstract class MediaBrowserCompat$SearchCallback {
    public abstract void onError(String str, Bundle bundle);

    public abstract void onSearchResult(String str, Bundle bundle, List<MediaBrowserCompat$MediaItem> list);
}
