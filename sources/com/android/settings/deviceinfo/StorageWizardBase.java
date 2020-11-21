package com.android.settings.deviceinfo;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.storage.DiskInfo;
import android.os.storage.StorageEventListener;
import android.os.storage.StorageManager;
import android.os.storage.VolumeInfo;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.android.settings.C0018R$style;
import com.android.settingslib.Utils;
import com.google.android.setupcompat.template.FooterBarMixin;
import com.google.android.setupcompat.template.FooterButton;
import com.google.android.setupdesign.GlifLayout;
import com.oneplus.settings.BaseAppCompatActivity;
import java.text.NumberFormat;
import java.util.Objects;

public abstract class StorageWizardBase extends BaseAppCompatActivity {
    private FooterButton mBack;
    protected DiskInfo mDisk;
    private FooterBarMixin mFooterBarMixin;
    private FooterButton mNext;
    protected StorageManager mStorage;
    private final StorageEventListener mStorageListener = new StorageEventListener() {
        /* class com.android.settings.deviceinfo.StorageWizardBase.AnonymousClass1 */

        public void onDiskDestroyed(DiskInfo diskInfo) {
            if (StorageWizardBase.this.mDisk.id.equals(diskInfo.id)) {
                StorageWizardBase.this.finish();
            }
        }
    };
    protected VolumeInfo mVolume;

    /* access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, com.oneplus.settings.BaseAppCompatActivity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mStorage = (StorageManager) getSystemService(StorageManager.class);
        String stringExtra = getIntent().getStringExtra("android.os.storage.extra.VOLUME_ID");
        if (!TextUtils.isEmpty(stringExtra)) {
            this.mVolume = this.mStorage.findVolumeById(stringExtra);
        }
        String stringExtra2 = getIntent().getStringExtra("android.os.storage.extra.DISK_ID");
        if (!TextUtils.isEmpty(stringExtra2)) {
            this.mDisk = this.mStorage.findDiskById(stringExtra2);
        } else {
            VolumeInfo volumeInfo = this.mVolume;
            if (volumeInfo != null) {
                this.mDisk = volumeInfo.getDisk();
            }
        }
        if (this.mDisk != null) {
            this.mStorage.registerListener(this.mStorageListener);
        }
    }

    @Override // androidx.activity.ComponentActivity, androidx.appcompat.app.AppCompatActivity, android.app.Activity
    public void setContentView(int i) {
        super.setContentView(i);
        FooterBarMixin footerBarMixin = (FooterBarMixin) getGlifLayout().getMixin(FooterBarMixin.class);
        this.mFooterBarMixin = footerBarMixin;
        FooterButton.Builder builder = new FooterButton.Builder(this);
        builder.setText(C0017R$string.wizard_back);
        builder.setListener(new View.OnClickListener() {
            /* class com.android.settings.deviceinfo.$$Lambda$fM0gCSTTN1T2Je2_NvbxAeyKcxM */

            public final void onClick(View view) {
                StorageWizardBase.this.onNavigateBack(view);
            }
        });
        builder.setButtonType(0);
        builder.setTheme(C0018R$style.OnePlusSecondaryButtonStyle);
        footerBarMixin.setSecondaryButton(builder.build());
        FooterBarMixin footerBarMixin2 = this.mFooterBarMixin;
        FooterButton.Builder builder2 = new FooterButton.Builder(this);
        builder2.setText(C0017R$string.wizard_next);
        builder2.setListener(new View.OnClickListener() {
            /* class com.android.settings.deviceinfo.$$Lambda$zFPWNnsTxDJLytWHRumtDh9D8g */

            public final void onClick(View view) {
                StorageWizardBase.this.onNavigateNext(view);
            }
        });
        builder2.setButtonType(5);
        builder2.setTheme(C0018R$style.OnePlusPrimaryButtonStyle);
        footerBarMixin2.setPrimaryButton(builder2.build());
        this.mBack = this.mFooterBarMixin.getSecondaryButton();
        this.mNext = this.mFooterBarMixin.getPrimaryButton();
        setIcon(17302813);
    }

    /* access modifiers changed from: protected */
    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity
    public void onDestroy() {
        this.mStorage.unregisterListener(this.mStorageListener);
        super.onDestroy();
    }

    /* access modifiers changed from: protected */
    public void onApplyThemeResource(Resources.Theme theme, int i, boolean z) {
        theme.applyStyle(C0018R$style.SetupWizardPartnerResource, true);
        super.onApplyThemeResource(theme, i, z);
    }

    /* access modifiers changed from: protected */
    public GlifLayout getGlifLayout() {
        return (GlifLayout) requireViewById(C0010R$id.setup_wizard_layout);
    }

    /* access modifiers changed from: protected */
    public ProgressBar getProgressBar() {
        return (ProgressBar) requireViewById(C0010R$id.storage_wizard_progress);
    }

    /* access modifiers changed from: protected */
    public void setCurrentProgress(int i) {
        getProgressBar().setProgress(i);
        ((TextView) requireViewById(C0010R$id.storage_wizard_progress_summary)).setText(NumberFormat.getPercentInstance().format(((double) i) / 100.0d));
    }

    /* access modifiers changed from: protected */
    public void setHeaderText(int i, CharSequence... charSequenceArr) {
        CharSequence expandTemplate = TextUtils.expandTemplate(getText(i), charSequenceArr);
        getGlifLayout().setHeaderText(expandTemplate);
        setTitle(expandTemplate);
    }

    /* access modifiers changed from: protected */
    public void setBodyText(int i, CharSequence... charSequenceArr) {
        TextView textView = (TextView) requireViewById(C0010R$id.storage_wizard_body);
        textView.setText(TextUtils.expandTemplate(getText(i), charSequenceArr));
        textView.setVisibility(0);
    }

    /* access modifiers changed from: protected */
    public void setAuxChecklist() {
        FrameLayout frameLayout = (FrameLayout) requireViewById(C0010R$id.storage_wizard_aux);
        frameLayout.addView(LayoutInflater.from(frameLayout.getContext()).inflate(C0012R$layout.storage_wizard_checklist, (ViewGroup) frameLayout, false));
        frameLayout.setVisibility(0);
        ((TextView) frameLayout.requireViewById(C0010R$id.storage_wizard_migrate_v2_checklist_media)).setText(TextUtils.expandTemplate(getText(C0017R$string.storage_wizard_migrate_v2_checklist_media), getDiskShortDescription()));
    }

    /* access modifiers changed from: protected */
    public void setBackButtonText(int i, CharSequence... charSequenceArr) {
        this.mBack.setText(TextUtils.expandTemplate(getText(i), charSequenceArr));
        this.mBack.setVisibility(0);
    }

    /* access modifiers changed from: protected */
    public void setNextButtonText(int i, CharSequence... charSequenceArr) {
        this.mNext.setText(TextUtils.expandTemplate(getText(i), charSequenceArr));
        this.mNext.setVisibility(0);
    }

    /* access modifiers changed from: protected */
    public void setBackButtonVisibility(int i) {
        this.mBack.setVisibility(i);
    }

    /* access modifiers changed from: protected */
    public void setNextButtonVisibility(int i) {
        this.mNext.setVisibility(i);
    }

    /* access modifiers changed from: protected */
    public void setIcon(int i) {
        GlifLayout glifLayout = getGlifLayout();
        Drawable mutate = getDrawable(i).mutate();
        mutate.setTintList(Utils.getColorAccent(glifLayout.getContext()));
        glifLayout.setIcon(mutate);
    }

    /* access modifiers changed from: protected */
    public void setKeepScreenOn(boolean z) {
        getGlifLayout().setKeepScreenOn(z);
    }

    public void onNavigateBack(View view) {
        throw new UnsupportedOperationException();
    }

    public void onNavigateNext(View view) {
        throw new UnsupportedOperationException();
    }

    private void copyStringExtra(Intent intent, Intent intent2, String str) {
        if (intent.hasExtra(str) && !intent2.hasExtra(str)) {
            intent2.putExtra(str, intent.getStringExtra(str));
        }
    }

    private void copyBooleanExtra(Intent intent, Intent intent2, String str) {
        if (intent.hasExtra(str) && !intent2.hasExtra(str)) {
            intent2.putExtra(str, intent.getBooleanExtra(str, false));
        }
    }

    public void startActivity(Intent intent) {
        Intent intent2 = getIntent();
        copyStringExtra(intent2, intent, "android.os.storage.extra.DISK_ID");
        copyStringExtra(intent2, intent, "android.os.storage.extra.VOLUME_ID");
        copyStringExtra(intent2, intent, "format_forget_uuid");
        copyBooleanExtra(intent2, intent, "format_private");
        copyBooleanExtra(intent2, intent, "format_slow");
        copyBooleanExtra(intent2, intent, "migrate_skip");
        super.startActivity(intent);
    }

    /* access modifiers changed from: protected */
    public VolumeInfo findFirstVolume(int i) {
        return findFirstVolume(i, 1);
    }

    /* access modifiers changed from: protected */
    public VolumeInfo findFirstVolume(int i, int i2) {
        while (true) {
            for (VolumeInfo volumeInfo : this.mStorage.getVolumes()) {
                if (Objects.equals(this.mDisk.getId(), volumeInfo.getDiskId()) && volumeInfo.getType() == i && volumeInfo.getState() == 2) {
                    return volumeInfo;
                }
            }
            i2--;
            if (i2 <= 0) {
                return null;
            }
            Log.w("StorageSettings", "Missing mounted volume of type " + i + " hosted by disk " + this.mDisk.getId() + "; trying again");
            SystemClock.sleep(250);
        }
    }

    /* access modifiers changed from: protected */
    public CharSequence getDiskDescription() {
        DiskInfo diskInfo = this.mDisk;
        if (diskInfo != null) {
            return diskInfo.getDescription();
        }
        VolumeInfo volumeInfo = this.mVolume;
        if (volumeInfo != null) {
            return volumeInfo.getDescription();
        }
        return getText(C0017R$string.unknown);
    }

    /* access modifiers changed from: protected */
    public CharSequence getDiskShortDescription() {
        DiskInfo diskInfo = this.mDisk;
        if (diskInfo != null) {
            return diskInfo.getShortDescription();
        }
        VolumeInfo volumeInfo = this.mVolume;
        if (volumeInfo != null) {
            return volumeInfo.getDescription();
        }
        return getText(C0017R$string.unknown);
    }
}
