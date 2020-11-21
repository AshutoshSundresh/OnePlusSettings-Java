package com.android.settings.notification;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.UserManager;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.android.settings.C0018R$style;
import com.android.settings.RestrictedRadioButton;
import com.android.settings.SettingsActivity;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.SetupRedactionInterstitial;
import com.android.settings.Utils;
import com.android.settings.notification.RedactionInterstitial;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.google.android.setupcompat.template.FooterBarMixin;
import com.google.android.setupcompat.template.FooterButton;
import com.google.android.setupdesign.GlifLayout;

public class RedactionInterstitial extends SettingsActivity {
    @Override // com.android.settings.SettingsActivity
    public Intent getIntent() {
        Intent intent = new Intent(super.getIntent());
        intent.putExtra(":settings:show_fragment", RedactionInterstitialFragment.class.getName());
        return intent;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.SettingsActivity
    public boolean isValidFragment(String str) {
        return RedactionInterstitialFragment.class.getName().equals(str);
    }

    /* access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, com.oneplus.settings.BaseAppCompatActivity, com.android.settings.core.SettingsBaseActivity, com.android.settings.SettingsActivity
    public void onCreate(Bundle bundle) {
        this.mNeedShowAppBar = false;
        super.onCreate(bundle);
        findViewById(C0010R$id.content_parent).setFitsSystemWindows(true);
    }

    public static Intent createStartIntent(Context context, int i) {
        int i2;
        Intent intent = new Intent(context, RedactionInterstitial.class);
        if (UserManager.get(context).isManagedProfile(i)) {
            i2 = C0017R$string.lock_screen_notifications_interstitial_title_profile;
        } else {
            i2 = C0017R$string.lock_screen_notifications_interstitial_title;
        }
        return intent.putExtra(":settings:show_fragment_title_resid", i2).putExtra("android.intent.extra.USER_ID", i);
    }

    public static class RedactionInterstitialFragment extends SettingsPreferenceFragment implements RadioGroup.OnCheckedChangeListener {
        private RestrictedRadioButton mHideAllButton;
        private RestrictedRadioButton mRedactSensitiveButton;
        private RestrictedRadioButton mShowAllButton;
        private int mUserId;
        private View mViewHideAll;
        private View mViewRedactSensitive;
        private View mViewShowAll;

        @Override // com.android.settingslib.core.instrumentation.Instrumentable
        public int getMetricsCategory() {
            return 74;
        }

        @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment
        public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
            return layoutInflater.inflate(C0012R$layout.redaction_interstitial, viewGroup, false);
        }

        @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
        public void onViewCreated(View view, Bundle bundle) {
            super.onViewCreated(view, bundle);
            this.mShowAllButton = (RestrictedRadioButton) view.findViewById(C0010R$id.show_all);
            this.mRedactSensitiveButton = (RestrictedRadioButton) view.findViewById(C0010R$id.redact_sensitive);
            this.mHideAllButton = (RestrictedRadioButton) view.findViewById(C0010R$id.hide_all);
            this.mViewShowAll = view.findViewById(C0010R$id.redaction_interstitial_show_all);
            this.mViewRedactSensitive = view.findViewById(C0010R$id.redaction_interstitial_redact_sensitive);
            this.mViewHideAll = view.findViewById(C0010R$id.redaction_interstitial_hide_all);
            this.mViewShowAll.setOnClickListener(new View.OnClickListener() {
                /* class com.android.settings.notification.$$Lambda$RedactionInterstitial$RedactionInterstitialFragment$QobFpHeJyFqbW6REC3cKnbstC0 */

                public final void onClick(View view) {
                    RedactionInterstitial.RedactionInterstitialFragment.this.lambda$onViewCreated$0$RedactionInterstitial$RedactionInterstitialFragment(view);
                }
            });
            this.mViewRedactSensitive.setOnClickListener(new View.OnClickListener() {
                /* class com.android.settings.notification.$$Lambda$RedactionInterstitial$RedactionInterstitialFragment$F5IIl_CbfayK0_5qKU64MwGXxYo */

                public final void onClick(View view) {
                    RedactionInterstitial.RedactionInterstitialFragment.this.lambda$onViewCreated$1$RedactionInterstitial$RedactionInterstitialFragment(view);
                }
            });
            this.mViewHideAll.setOnClickListener(new View.OnClickListener() {
                /* class com.android.settings.notification.$$Lambda$RedactionInterstitial$RedactionInterstitialFragment$IWVrGQZcZWJbU3XViTmTXXF2eGk */

                public final void onClick(View view) {
                    RedactionInterstitial.RedactionInterstitialFragment.this.lambda$onViewCreated$2$RedactionInterstitial$RedactionInterstitialFragment(view);
                }
            });
            this.mUserId = Utils.getUserIdFromBundle(getContext(), getActivity().getIntent().getExtras());
            if (UserManager.get(getContext()).isManagedProfile(this.mUserId)) {
                ((TextView) view.findViewById(C0010R$id.sud_layout_description)).setText(C0017R$string.lock_screen_notifications_interstitial_message_profile);
                this.mShowAllButton.setText(C0017R$string.lock_screen_notifications_summary_show_profile);
                this.mRedactSensitiveButton.setText(C0017R$string.lock_screen_notifications_summary_hide_profile);
                this.mViewHideAll.setVisibility(8);
            }
            FooterButton.Builder builder = new FooterButton.Builder(getContext());
            builder.setText(C0017R$string.app_notifications_dialog_done);
            builder.setListener(new View.OnClickListener() {
                /* class com.android.settings.notification.$$Lambda$RedactionInterstitial$RedactionInterstitialFragment$8pyteZIVW5XOCBth5aVPDVC6DzI */

                public final void onClick(View view) {
                    RedactionInterstitial.RedactionInterstitialFragment.this.onDoneButtonClicked(view);
                }
            });
            builder.setButtonType(5);
            builder.setTheme(C0018R$style.OnePlusPrimaryButtonStyle);
            ((FooterBarMixin) ((GlifLayout) view.findViewById(C0010R$id.setup_wizard_layout)).getMixin(FooterBarMixin.class)).setPrimaryButton(builder.build());
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$onViewCreated$0 */
        public /* synthetic */ void lambda$onViewCreated$0$RedactionInterstitial$RedactionInterstitialFragment(View view) {
            onCheckedChanged(1);
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$onViewCreated$1 */
        public /* synthetic */ void lambda$onViewCreated$1$RedactionInterstitial$RedactionInterstitialFragment(View view) {
            onCheckedChanged(2);
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$onViewCreated$2 */
        public /* synthetic */ void lambda$onViewCreated$2$RedactionInterstitial$RedactionInterstitialFragment(View view) {
            onCheckedChanged(3);
        }

        /* access modifiers changed from: private */
        public void onDoneButtonClicked(View view) {
            SetupRedactionInterstitial.setEnabled(getContext(), false);
            RedactionInterstitial redactionInterstitial = (RedactionInterstitial) getActivity();
            if (redactionInterstitial != null) {
                redactionInterstitial.setResult(-1, null);
                finish();
            }
        }

        @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
        public void onResume() {
            super.onResume();
            checkNotificationFeaturesAndSetDisabled(this.mShowAllButton, 12);
            checkNotificationFeaturesAndSetDisabled(this.mRedactSensitiveButton, 4);
            loadFromSettings();
        }

        private void checkNotificationFeaturesAndSetDisabled(RestrictedRadioButton restrictedRadioButton, int i) {
            restrictedRadioButton.setDisabledByAdmin(RestrictedLockUtilsInternal.checkIfKeyguardFeaturesDisabled(getActivity(), i, this.mUserId));
        }

        private void loadFromSettings() {
            boolean z = false;
            int i = 1;
            boolean z2 = UserManager.get(getContext()).isManagedProfile(this.mUserId) || Settings.Secure.getIntForUser(getContentResolver(), "lock_screen_show_notifications", 0, this.mUserId) != 0;
            if (Settings.Secure.getIntForUser(getContentResolver(), "lock_screen_allow_private_notifications", 1, this.mUserId) != 0) {
                z = true;
            }
            if (z2) {
                if (!z || this.mShowAllButton.isDisabledByAdmin()) {
                    if (!this.mRedactSensitiveButton.isDisabledByAdmin()) {
                        i = 2;
                    }
                }
                updateRadioButton(i);
            }
            i = 3;
            updateRadioButton(i);
        }

        /* JADX DEBUG: Multi-variable search result rejected for r6v1, resolved type: java.lang.StringBuilder */
        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference failed for: r0v0 */
        /* JADX WARN: Type inference failed for: r5v2, types: [int, boolean] */
        /* JADX WARN: Type inference failed for: r0v1, types: [int, boolean] */
        /* JADX WARN: Type inference failed for: r0v2 */
        /* JADX WARN: Type inference failed for: r5v3 */
        /* JADX WARN: Type inference failed for: r5v4 */
        /* JADX WARNING: Unknown variable types count: 2 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onCheckedChanged(android.widget.RadioGroup r5, int r6) {
            /*
                r4 = this;
                int r5 = com.android.settings.C0010R$id.show_all
                r0 = 1
                r1 = 0
                if (r6 != r5) goto L_0x0008
                r5 = r0
                goto L_0x0009
            L_0x0008:
                r5 = r1
            L_0x0009:
                int r2 = com.android.settings.C0010R$id.hide_all
                if (r6 == r2) goto L_0x000e
                goto L_0x000f
            L_0x000e:
                r0 = r1
            L_0x000f:
                java.lang.StringBuilder r6 = new java.lang.StringBuilder
                r6.<init>()
                java.lang.String r1 = "onCheckedChanged show:"
                r6.append(r1)
                r6.append(r5)
                java.lang.String r1 = "  enabled:"
                r6.append(r1)
                r6.append(r0)
                java.lang.String r6 = r6.toString()
                java.lang.String r1 = "RedactionInterstitial"
                android.util.Log.i(r1, r6)
                android.content.ContentResolver r6 = r4.getContentResolver()
                int r1 = r4.mUserId
                java.lang.String r2 = "lock_screen_allow_private_notifications"
                android.provider.Settings.Secure.putIntForUser(r6, r2, r5, r1)
                android.content.ContentResolver r6 = r4.getContentResolver()
                int r1 = r4.mUserId
                java.lang.String r3 = "lock_screen_show_notifications"
                android.provider.Settings.Secure.putIntForUser(r6, r3, r0, r1)
                android.content.ContentResolver r6 = r4.getContentResolver()
                r1 = 999(0x3e7, float:1.4E-42)
                android.provider.Settings.Secure.putIntForUser(r6, r2, r5, r1)
                android.content.ContentResolver r4 = r4.getContentResolver()
                android.provider.Settings.Secure.putIntForUser(r4, r3, r0, r1)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.settings.notification.RedactionInterstitial.RedactionInterstitialFragment.onCheckedChanged(android.widget.RadioGroup, int):void");
        }

        /* JADX DEBUG: Multi-variable search result rejected for r6v1, resolved type: java.lang.StringBuilder */
        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference failed for: r0v0 */
        /* JADX WARN: Type inference failed for: r2v0, types: [int, boolean] */
        /* JADX WARN: Type inference failed for: r0v1, types: [int, boolean] */
        /* JADX WARN: Type inference failed for: r0v2 */
        /* JADX WARN: Type inference failed for: r2v1 */
        /* JADX WARN: Type inference failed for: r2v2 */
        /* JADX WARNING: Unknown variable types count: 2 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private void onCheckedChanged(int r6) {
            /*
                r5 = this;
                r5.updateRadioButton(r6)
                r0 = 0
                r1 = 1
                if (r6 != r1) goto L_0x0009
                r2 = r1
                goto L_0x000a
            L_0x0009:
                r2 = r0
            L_0x000a:
                r3 = 3
                if (r6 == r3) goto L_0x000e
                r0 = r1
            L_0x000e:
                java.lang.StringBuilder r6 = new java.lang.StringBuilder
                r6.<init>()
                java.lang.String r1 = "onCheckedChanged show:"
                r6.append(r1)
                r6.append(r2)
                java.lang.String r1 = "  enabled:"
                r6.append(r1)
                r6.append(r0)
                java.lang.String r6 = r6.toString()
                java.lang.String r1 = "RedactionInterstitial"
                android.util.Log.i(r1, r6)
                android.content.ContentResolver r6 = r5.getContentResolver()
                int r1 = r5.mUserId
                java.lang.String r3 = "lock_screen_allow_private_notifications"
                android.provider.Settings.Secure.putIntForUser(r6, r3, r2, r1)
                android.content.ContentResolver r6 = r5.getContentResolver()
                int r1 = r5.mUserId
                java.lang.String r4 = "lock_screen_show_notifications"
                android.provider.Settings.Secure.putIntForUser(r6, r4, r0, r1)
                android.content.ContentResolver r6 = r5.getContentResolver()
                r1 = 999(0x3e7, float:1.4E-42)
                android.provider.Settings.Secure.putIntForUser(r6, r3, r2, r1)
                android.content.ContentResolver r5 = r5.getContentResolver()
                android.provider.Settings.Secure.putIntForUser(r5, r4, r0, r1)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.settings.notification.RedactionInterstitial.RedactionInterstitialFragment.onCheckedChanged(int):void");
        }

        private void updateRadioButton(int i) {
            if (i == 1) {
                this.mShowAllButton.setChecked(true);
                this.mRedactSensitiveButton.setChecked(false);
                this.mHideAllButton.setChecked(false);
            } else if (i == 2) {
                this.mShowAllButton.setChecked(false);
                this.mRedactSensitiveButton.setChecked(true);
                this.mHideAllButton.setChecked(false);
            } else if (i == 3) {
                this.mShowAllButton.setChecked(false);
                this.mRedactSensitiveButton.setChecked(false);
                this.mHideAllButton.setChecked(true);
            }
        }
    }
}
