package com.google.analytics.tracking.android;

import android.text.TextUtils;
import com.google.analytics.tracking.android.GAUsage;
import java.util.HashMap;
import java.util.Map;

public class MapBuilder {
    private Map<String, String> map = new HashMap();

    public MapBuilder set(String str, String str2) {
        GAUsage.getInstance().setUsage(GAUsage.Field.MAP_BUILDER_SET);
        if (str != null) {
            this.map.put(str, str2);
        } else {
            Log.w(" MapBuilder.set() called with a null paramName.");
        }
        return this;
    }

    public Map<String, String> build() {
        return new HashMap(this.map);
    }

    public static MapBuilder createEvent(String str, String str2, String str3, Long l) {
        String str4;
        GAUsage.getInstance().setUsage(GAUsage.Field.CONSTRUCT_EVENT);
        MapBuilder mapBuilder = new MapBuilder();
        mapBuilder.set("&t", "event");
        mapBuilder.set("&ec", str);
        mapBuilder.set("&ea", str2);
        mapBuilder.set("&el", str3);
        if (l == null) {
            str4 = null;
        } else {
            str4 = Long.toString(l.longValue());
        }
        mapBuilder.set("&ev", str4);
        return mapBuilder;
    }

    public MapBuilder setCampaignParamsFromUrl(String str) {
        GAUsage.getInstance().setUsage(GAUsage.Field.MAP_BUILDER_SET_CAMPAIGN_PARAMS);
        String filterCampaign = Utils.filterCampaign(str);
        if (TextUtils.isEmpty(filterCampaign)) {
            return this;
        }
        Map<String, String> parseURLParameters = Utils.parseURLParameters(filterCampaign);
        set("&cc", parseURLParameters.get("utm_content"));
        set("&cm", parseURLParameters.get("utm_medium"));
        set("&cn", parseURLParameters.get("utm_campaign"));
        set("&cs", parseURLParameters.get("utm_source"));
        set("&ck", parseURLParameters.get("utm_term"));
        set("&ci", parseURLParameters.get("utm_id"));
        set("&gclid", parseURLParameters.get("gclid"));
        set("&dclid", parseURLParameters.get("dclid"));
        set("&gmob_t", parseURLParameters.get("gmob_t"));
        return this;
    }
}
