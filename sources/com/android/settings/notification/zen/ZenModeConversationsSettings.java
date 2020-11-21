package com.android.settings.notification.zen;

import android.content.Context;
import com.android.settings.C0019R$xml;
import com.android.settings.notification.NotificationBackend;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import java.util.ArrayList;
import java.util.List;

public class ZenModeConversationsSettings extends ZenModeSettingsBase {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C0019R$xml.zen_mode_conversations_settings) {
        /* class com.android.settings.notification.zen.ZenModeConversationsSettings.AnonymousClass1 */

        @Override // com.android.settings.search.BaseSearchIndexProvider
        public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
            return ZenModeConversationsSettings.buildPreferenceControllers(context, null, null);
        }
    };
    private final NotificationBackend mNotificationBackend = new NotificationBackend();

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1837;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return buildPreferenceControllers(context, getSettingsLifecycle(), this.mNotificationBackend);
    }

    /* access modifiers changed from: private */
    public static List<AbstractPreferenceController> buildPreferenceControllers(Context context, Lifecycle lifecycle, NotificationBackend notificationBackend) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new ZenModeConversationsImagePreferenceController(context, "zen_mode_conversations_image", lifecycle, notificationBackend));
        arrayList.add(new ZenModePriorityConversationsPreferenceController(context, "zen_mode_conversations_radio_buttons", lifecycle, notificationBackend));
        return arrayList;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.zen_mode_conversations_settings;
    }
}
