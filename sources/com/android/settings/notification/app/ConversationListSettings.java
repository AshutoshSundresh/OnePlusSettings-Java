package com.android.settings.notification.app;

import android.content.Context;
import android.util.Log;
import com.android.settings.C0019R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.notification.NotificationBackend;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.ArrayList;
import java.util.List;

public class ConversationListSettings extends DashboardFragment {
    NotificationBackend mBackend = new NotificationBackend();
    protected List<AbstractPreferenceController> mControllers = new ArrayList();

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "ConvoListSettings";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1834;
    }

    static {
        Log.isLoggable("ConvoListSettings", 3);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.conversation_list_settings;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        ArrayList arrayList = new ArrayList();
        this.mControllers = arrayList;
        arrayList.add(new NoConversationsPreferenceController(context, this.mBackend));
        this.mControllers.add(new PriorityConversationsPreferenceController(context, this.mBackend));
        this.mControllers.add(new AllConversationsPreferenceController(context, this.mBackend));
        return new ArrayList(this.mControllers);
    }
}
