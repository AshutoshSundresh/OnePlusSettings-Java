package com.android.settings.applications.assist;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.service.voice.VoiceInteractionServiceInfo;
import java.util.ArrayList;
import java.util.List;

public final class VoiceInputHelper {
    final ArrayList<InteractionInfo> mAvailableInteractionInfos = new ArrayList<>();
    final List<ResolveInfo> mAvailableRecognition;
    final ArrayList<RecognizerInfo> mAvailableRecognizerInfos = new ArrayList<>();
    final List<ResolveInfo> mAvailableVoiceInteractions;
    final Context mContext;
    ComponentName mCurrentRecognizer;
    ComponentName mCurrentVoiceInteraction;

    public static class BaseInfo implements Comparable {
        public final CharSequence appLabel;
        public final ComponentName componentName;
        public final String key;
        public final CharSequence label;
        public final String labelStr;
        public final ServiceInfo service;
        public final ComponentName settings;

        public BaseInfo(PackageManager packageManager, ServiceInfo serviceInfo, String str) {
            this.service = serviceInfo;
            ComponentName componentName2 = new ComponentName(serviceInfo.packageName, serviceInfo.name);
            this.componentName = componentName2;
            this.key = componentName2.flattenToShortString();
            this.settings = str != null ? new ComponentName(serviceInfo.packageName, str) : null;
            CharSequence loadLabel = serviceInfo.loadLabel(packageManager);
            this.label = loadLabel;
            this.labelStr = loadLabel.toString();
            this.appLabel = serviceInfo.applicationInfo.loadLabel(packageManager);
        }

        @Override // java.lang.Comparable
        public int compareTo(Object obj) {
            return this.labelStr.compareTo(((BaseInfo) obj).labelStr);
        }
    }

    public static class InteractionInfo extends BaseInfo {
        public final VoiceInteractionServiceInfo serviceInfo;

        public InteractionInfo(PackageManager packageManager, VoiceInteractionServiceInfo voiceInteractionServiceInfo) {
            super(packageManager, voiceInteractionServiceInfo.getServiceInfo(), voiceInteractionServiceInfo.getSettingsActivity());
            this.serviceInfo = voiceInteractionServiceInfo;
        }
    }

    public static class RecognizerInfo extends BaseInfo {
        public RecognizerInfo(PackageManager packageManager, ServiceInfo serviceInfo, String str) {
            super(packageManager, serviceInfo, str);
        }
    }

    public VoiceInputHelper(Context context) {
        this.mContext = context;
        this.mAvailableVoiceInteractions = context.getPackageManager().queryIntentServices(new Intent("android.service.voice.VoiceInteractionService"), 128);
        this.mAvailableRecognition = this.mContext.getPackageManager().queryIntentServices(new Intent("android.speech.RecognitionService"), 128);
    }

    /* JADX DEBUG: Failed to insert an additional move for type inference into block B:22:0x00e7 */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r9v1, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r9v2 */
    /* JADX WARN: Type inference failed for: r9v8 */
    /* JADX WARN: Type inference failed for: r9v9 */
    /* JADX WARN: Type inference failed for: r9v24 */
    /* JADX WARN: Type inference failed for: r9v25 */
    /* JADX WARNING: Removed duplicated region for block: B:58:0x0169 A[Catch:{ XmlPullParserException -> 0x0173, IOException -> 0x016a, NameNotFoundException -> 0x0161, all -> 0x015f, all -> 0x0192 }] */
    /* JADX WARNING: Removed duplicated region for block: B:63:0x0172 A[Catch:{ XmlPullParserException -> 0x0173, IOException -> 0x016a, NameNotFoundException -> 0x0161, all -> 0x015f, all -> 0x0192 }] */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x0196  */
    /* JADX WARNING: Removed duplicated region for block: B:84:0x017c A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:85:0x017c A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:86:0x017c A[SYNTHETIC] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void buildUi() {
        /*
        // Method dump skipped, instructions count: 416
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.applications.assist.VoiceInputHelper.buildUi():void");
    }
}
