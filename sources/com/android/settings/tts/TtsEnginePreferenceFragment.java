package com.android.settings.tts;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TtsEngines;
import android.util.Log;
import androidx.appcompat.app.AlertDialog;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.widget.RadioButtonPickerFragment;
import com.android.settingslib.widget.CandidateInfo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TtsEnginePreferenceFragment extends RadioButtonPickerFragment {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C0019R$xml.tts_engine_picker);
    private Context mContext;
    private Map<String, EngineCandidateInfo> mEngineMap;
    private TtsEngines mEnginesHelper = null;
    private String mPreviousEngine;
    private TextToSpeech mTts = null;
    private final TextToSpeech.OnInitListener mUpdateListener = new TextToSpeech.OnInitListener() {
        /* class com.android.settings.tts.TtsEnginePreferenceFragment.AnonymousClass1 */

        public void onInit(int i) {
            TtsEnginePreferenceFragment.this.onUpdateEngine(i);
        }
    };

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 93;
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        this.mContext = getContext().getApplicationContext();
        this.mEnginesHelper = new TtsEngines(this.mContext);
        this.mEngineMap = new HashMap();
        this.mTts = new TextToSpeech(this.mContext, null);
        super.onCreate(bundle);
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onDestroy() {
        super.onDestroy();
        TextToSpeech textToSpeech = this.mTts;
        if (textToSpeech != null) {
            textToSpeech.shutdown();
            this.mTts = null;
        }
    }

    public void onUpdateEngine(int i) {
        if (i == 0) {
            Log.d("TtsEnginePrefFragment", "Updating engine: Successfully bound to the engine: " + this.mTts.getCurrentEngine());
            Settings.Secure.putString(this.mContext.getContentResolver(), "tts_default_synth", this.mTts.getCurrentEngine());
            return;
        }
        Log.d("TtsEnginePrefFragment", "Updating engine: Failed to bind to engine, reverting.");
        if (this.mPreviousEngine != null) {
            this.mTts = new TextToSpeech(this.mContext, null, this.mPreviousEngine);
            updateCheckedState(this.mPreviousEngine);
        }
        this.mPreviousEngine = null;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.widget.RadioButtonPickerFragment
    public void onRadioButtonConfirmed(String str) {
        EngineCandidateInfo engineCandidateInfo = this.mEngineMap.get(str);
        if (shouldDisplayDataAlert(engineCandidateInfo)) {
            displayDataAlert(engineCandidateInfo, new DialogInterface.OnClickListener(str) {
                /* class com.android.settings.tts.$$Lambda$TtsEnginePreferenceFragment$FMoEpZlbdu64aUolMsMWdHuqokg */
                public final /* synthetic */ String f$1;

                {
                    this.f$1 = r2;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    TtsEnginePreferenceFragment.this.lambda$onRadioButtonConfirmed$0$TtsEnginePreferenceFragment(this.f$1, dialogInterface, i);
                }
            });
        } else {
            setDefaultKey(str);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onRadioButtonConfirmed$0 */
    public /* synthetic */ void lambda$onRadioButtonConfirmed$0$TtsEnginePreferenceFragment(String str, DialogInterface dialogInterface, int i) {
        setDefaultKey(str);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.widget.RadioButtonPickerFragment
    public List<? extends CandidateInfo> getCandidates() {
        ArrayList arrayList = new ArrayList();
        for (TextToSpeech.EngineInfo engineInfo : this.mEnginesHelper.getEngines()) {
            EngineCandidateInfo engineCandidateInfo = new EngineCandidateInfo(engineInfo);
            arrayList.add(engineCandidateInfo);
            this.mEngineMap.put(engineInfo.name, engineCandidateInfo);
        }
        return arrayList;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.widget.RadioButtonPickerFragment
    public String getDefaultKey() {
        return this.mEnginesHelper.getDefaultEngine();
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.widget.RadioButtonPickerFragment
    public boolean setDefaultKey(String str) {
        updateDefaultEngine(str);
        updateCheckedState(str);
        return true;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.widget.RadioButtonPickerFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.tts_engine_picker;
    }

    private boolean shouldDisplayDataAlert(EngineCandidateInfo engineCandidateInfo) {
        return !engineCandidateInfo.isSystem();
    }

    private void displayDataAlert(EngineCandidateInfo engineCandidateInfo, DialogInterface.OnClickListener onClickListener) {
        Log.i("TtsEnginePrefFragment", "Displaying data alert for :" + engineCandidateInfo.getKey());
        AlertDialog.Builder builder = new AlertDialog.Builder(getPrefContext());
        builder.setTitle(17039380);
        builder.setMessage(this.mContext.getString(C0017R$string.tts_engine_security_warning, engineCandidateInfo.loadLabel()));
        builder.setCancelable(true);
        builder.setPositiveButton(17039370, onClickListener);
        builder.setNegativeButton(17039360, (DialogInterface.OnClickListener) null);
        builder.create().show();
    }

    private void updateDefaultEngine(String str) {
        Log.d("TtsEnginePrefFragment", "Updating default synth to : " + str);
        this.mPreviousEngine = this.mTts.getCurrentEngine();
        Log.i("TtsEnginePrefFragment", "Shutting down current tts engine");
        TextToSpeech textToSpeech = this.mTts;
        if (textToSpeech != null) {
            try {
                textToSpeech.shutdown();
                this.mTts = null;
            } catch (Exception e) {
                Log.e("TtsEnginePrefFragment", "Error shutting down TTS engine" + e);
            }
        }
        Log.i("TtsEnginePrefFragment", "Updating engine : Attempting to connect to engine: " + str);
        this.mTts = new TextToSpeech(this.mContext, this.mUpdateListener, str);
        Log.i("TtsEnginePrefFragment", "Success");
    }

    public static class EngineCandidateInfo extends CandidateInfo {
        private final TextToSpeech.EngineInfo mEngineInfo;

        @Override // com.android.settingslib.widget.CandidateInfo
        public Drawable loadIcon() {
            return null;
        }

        EngineCandidateInfo(TextToSpeech.EngineInfo engineInfo) {
            super(true);
            this.mEngineInfo = engineInfo;
        }

        @Override // com.android.settingslib.widget.CandidateInfo
        public CharSequence loadLabel() {
            return this.mEngineInfo.label;
        }

        @Override // com.android.settingslib.widget.CandidateInfo
        public String getKey() {
            return this.mEngineInfo.name;
        }

        public boolean isSystem() {
            return this.mEngineInfo.system;
        }
    }
}
