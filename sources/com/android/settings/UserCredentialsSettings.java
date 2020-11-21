package com.android.settings;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import android.os.UserHandle;
import android.security.KeyChain;
import android.security.KeyStore;
import android.security.keymaster.KeyCharacteristics;
import android.security.keymaster.KeymasterBlob;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.google.android.material.emptyview.EmptyPageView;
import java.security.UnrecoverableKeyException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class UserCredentialsSettings extends SettingsPreferenceFragment implements View.OnClickListener {
    private static final SparseArray<Credential.Type> credentialViewTypes;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 285;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        refreshItems();
    }

    public void onClick(View view) {
        Credential credential = (Credential) view.getTag();
        if (credential != null) {
            CredentialDialogFragment.show(this, credential);
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getActivity().setTitle(C0017R$string.user_credentials);
    }

    /* access modifiers changed from: protected */
    public void announceRemoval(String str) {
        if (isAdded()) {
            getListView().announceForAccessibility(getString(C0017R$string.user_credential_removed, str));
        }
    }

    /* access modifiers changed from: protected */
    public void refreshItems() {
        if (isAdded()) {
            new AliasLoader().execute(new Void[0]);
        }
    }

    public static class CredentialDialogFragment extends InstrumentedDialogFragment {
        @Override // com.android.settingslib.core.instrumentation.Instrumentable
        public int getMetricsCategory() {
            return 533;
        }

        public static void show(Fragment fragment, Credential credential) {
            Bundle bundle = new Bundle();
            bundle.putParcelable("credential", credential);
            if (fragment.getFragmentManager().findFragmentByTag("CredentialDialogFragment") == null) {
                CredentialDialogFragment credentialDialogFragment = new CredentialDialogFragment();
                credentialDialogFragment.setTargetFragment(fragment, -1);
                credentialDialogFragment.setArguments(bundle);
                credentialDialogFragment.show(fragment.getFragmentManager(), "CredentialDialogFragment");
            }
        }

        @Override // androidx.fragment.app.DialogFragment
        public Dialog onCreateDialog(Bundle bundle) {
            final Credential credential = (Credential) getArguments().getParcelable("credential");
            View inflate = getActivity().getLayoutInflater().inflate(C0012R$layout.user_credential_dialog, (ViewGroup) null);
            ViewGroup viewGroup = (ViewGroup) inflate.findViewById(C0010R$id.credential_container);
            viewGroup.addView(UserCredentialsSettings.getCredentialView(credential, C0012R$layout.user_credential, null, viewGroup, true));
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setView(inflate);
            builder.setTitle(C0017R$string.user_credential_title);
            builder.setPositiveButton(C0017R$string.done, (DialogInterface.OnClickListener) null);
            final int myUserId = UserHandle.myUserId();
            if (!RestrictedLockUtilsInternal.hasBaseUserRestriction(getContext(), "no_config_credentials", myUserId)) {
                builder.setNegativeButton(C0017R$string.trusted_credentials_remove_label, new DialogInterface.OnClickListener() {
                    /* class com.android.settings.UserCredentialsSettings.CredentialDialogFragment.AnonymousClass1 */

                    public void onClick(DialogInterface dialogInterface, int i) {
                        RestrictedLockUtils.EnforcedAdmin checkIfRestrictionEnforced = RestrictedLockUtilsInternal.checkIfRestrictionEnforced(CredentialDialogFragment.this.getContext(), "no_config_credentials", myUserId);
                        if (checkIfRestrictionEnforced != null) {
                            RestrictedLockUtils.sendShowAdminSupportDetailsIntent(CredentialDialogFragment.this.getContext(), checkIfRestrictionEnforced);
                        } else {
                            CredentialDialogFragment credentialDialogFragment = CredentialDialogFragment.this;
                            new RemoveCredentialsTask(credentialDialogFragment.getContext(), CredentialDialogFragment.this.getTargetFragment()).execute(credential);
                        }
                        dialogInterface.dismiss();
                    }
                });
            }
            return builder.create();
        }

        private class RemoveCredentialsTask extends AsyncTask<Credential, Void, Credential[]> {
            private Fragment targetFragment;

            /* Return type fixed from 'java.lang.Object' to match base method */
            /* JADX DEBUG: Method arguments types fixed to match base method, original types: [java.lang.Object[]] */
            /* access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public /* bridge */ /* synthetic */ Credential[] doInBackground(Credential[] credentialArr) {
                Credential[] credentialArr2 = credentialArr;
                doInBackground(credentialArr2);
                return credentialArr2;
            }

            public RemoveCredentialsTask(Context context, Fragment fragment) {
                this.targetFragment = fragment;
            }

            /* access modifiers changed from: protected */
            public Credential[] doInBackground(Credential... credentialArr) {
                for (Credential credential : credentialArr) {
                    if (credential.isSystem()) {
                        removeGrantsAndDelete(credential);
                    } else {
                        deleteWifiCredential(credential);
                    }
                }
                return credentialArr;
            }

            private void deleteWifiCredential(Credential credential) {
                KeyStore instance = KeyStore.getInstance();
                EnumSet<Credential.Type> storedTypes = credential.getStoredTypes();
                if (storedTypes.contains(Credential.Type.USER_KEY)) {
                    instance.delete("USRPKEY_" + credential.getAlias(), 1010);
                }
                if (storedTypes.contains(Credential.Type.USER_CERTIFICATE)) {
                    instance.delete("USRCERT_" + credential.getAlias(), 1010);
                }
                if (storedTypes.contains(Credential.Type.CA_CERTIFICATE)) {
                    instance.delete("CACERT_" + credential.getAlias(), 1010);
                }
            }

            private void removeGrantsAndDelete(Credential credential) {
                try {
                    KeyChain.KeyChainConnection bind = KeyChain.bind(CredentialDialogFragment.this.getContext());
                    try {
                        bind.getService().removeKeyPair(credential.alias);
                    } catch (RemoteException e) {
                        Log.w("CredentialDialogFragment", "Removing credentials", e);
                    } catch (Throwable th) {
                        bind.close();
                        throw th;
                    }
                    bind.close();
                } catch (InterruptedException e2) {
                    Log.w("CredentialDialogFragment", "Connecting to KeyChain", e2);
                }
            }

            /* access modifiers changed from: protected */
            public void onPostExecute(Credential... credentialArr) {
                Fragment fragment = this.targetFragment;
                if ((fragment instanceof UserCredentialsSettings) && fragment.isAdded()) {
                    UserCredentialsSettings userCredentialsSettings = (UserCredentialsSettings) this.targetFragment;
                    for (Credential credential : credentialArr) {
                        userCredentialsSettings.announceRemoval(credential.alias);
                    }
                    userCredentialsSettings.refreshItems();
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public class AliasLoader extends AsyncTask<Void, Void, List<Credential>> {
        private AliasLoader() {
        }

        /* access modifiers changed from: protected */
        public List<Credential> doInBackground(Void... voidArr) {
            KeyStore instance = KeyStore.getInstance();
            int myUserId = UserHandle.myUserId();
            int uid = UserHandle.getUid(myUserId, 1000);
            int uid2 = UserHandle.getUid(myUserId, 1010);
            ArrayList arrayList = new ArrayList();
            arrayList.addAll(getCredentialsForUid(instance, uid).values());
            arrayList.addAll(getCredentialsForUid(instance, uid2).values());
            return arrayList;
        }

        private boolean isAsymmetric(KeyStore keyStore, String str, int i) throws UnrecoverableKeyException {
            KeyCharacteristics keyCharacteristics = new KeyCharacteristics();
            int keyCharacteristics2 = keyStore.getKeyCharacteristics(str, (KeymasterBlob) null, (KeymasterBlob) null, i, keyCharacteristics);
            if (keyCharacteristics2 == 1) {
                Integer num = keyCharacteristics.getEnum(268435458);
                if (num == null) {
                    throw new UnrecoverableKeyException("Key algorithm unknown");
                } else if (num.intValue() == 1 || num.intValue() == 3) {
                    return true;
                } else {
                    return false;
                }
            } else {
                throw ((UnrecoverableKeyException) new UnrecoverableKeyException("Failed to obtain information about key").initCause(KeyStore.getKeyStoreException(keyCharacteristics2)));
            }
        }

        private SortedMap<String, Credential> getCredentialsForUid(KeyStore keyStore, int i) {
            UnrecoverableKeyException e;
            KeyStore keyStore2 = keyStore;
            int i2 = i;
            TreeMap treeMap = new TreeMap();
            Credential.Type[] values = Credential.Type.values();
            int length = values.length;
            int i3 = 0;
            while (i3 < length) {
                Credential.Type type = values[i3];
                String[] strArr = type.prefix;
                int length2 = strArr.length;
                int i4 = 0;
                while (i4 < length2) {
                    String str = strArr[i4];
                    String[] list = keyStore2.list(str, i2);
                    int length3 = list.length;
                    int i5 = 0;
                    while (i5 < length3) {
                        String str2 = list[i5];
                        if (UserHandle.getAppId(i) != 1000 || (!str2.startsWith("profile_key_name_encrypt_") && !str2.startsWith("profile_key_name_decrypt_") && !str2.startsWith("synthetic_password_"))) {
                            try {
                                if (type == Credential.Type.USER_KEY) {
                                    try {
                                        if (!isAsymmetric(keyStore2, str + str2, i2)) {
                                        }
                                    } catch (UnrecoverableKeyException e2) {
                                        e = e2;
                                        Log.e("UserCredentialsSettings", "Unable to determine algorithm of key: " + str + str2, e);
                                        i5++;
                                        keyStore2 = keyStore;
                                        i2 = i;
                                        values = values;
                                    }
                                }
                                Credential credential = (Credential) treeMap.get(str2);
                                if (credential == null) {
                                    credential = new Credential(str2, i2);
                                    treeMap.put(str2, credential);
                                }
                                credential.storedTypes.add(type);
                            } catch (UnrecoverableKeyException e3) {
                                e = e3;
                                Log.e("UserCredentialsSettings", "Unable to determine algorithm of key: " + str + str2, e);
                                i5++;
                                keyStore2 = keyStore;
                                i2 = i;
                                values = values;
                            }
                        }
                        i5++;
                        keyStore2 = keyStore;
                        i2 = i;
                        values = values;
                    }
                    i4++;
                    keyStore2 = keyStore;
                    i2 = i;
                    values = values;
                }
                i3++;
                keyStore2 = keyStore;
                i2 = i;
                values = values;
            }
            return treeMap;
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(List<Credential> list) {
            if (UserCredentialsSettings.this.isAdded()) {
                if (list == null || list.size() == 0) {
                    EmptyPageView emptyPageView = (EmptyPageView) UserCredentialsSettings.this.getActivity().findViewById(16908292);
                    emptyPageView.getEmptyTextView().setText(C0017R$string.user_credential_none_installed);
                    emptyPageView.getEmptyImageView().setImageResource(C0008R$drawable.op_empty);
                    UserCredentialsSettings.this.setEmptyView(emptyPageView);
                } else {
                    UserCredentialsSettings.this.setEmptyView(null);
                }
                UserCredentialsSettings.this.getListView().setAdapter(new CredentialAdapter(list, UserCredentialsSettings.this));
            }
        }
    }

    /* access modifiers changed from: private */
    public static class CredentialAdapter extends RecyclerView.Adapter<ViewHolder> {
        private static final int LAYOUT_RESOURCE = C0012R$layout.user_credential_preference;
        private final List<Credential> mItems;
        private final View.OnClickListener mListener;

        public CredentialAdapter(List<Credential> list, View.OnClickListener onClickListener) {
            this.mItems = list;
            this.mListener = onClickListener;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(LAYOUT_RESOURCE, viewGroup, false));
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            UserCredentialsSettings.getCredentialView(this.mItems.get(i), LAYOUT_RESOURCE, viewHolder.itemView, null, false);
            viewHolder.itemView.setTag(this.mItems.get(i));
            viewHolder.itemView.setOnClickListener(this.mListener);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return this.mItems.size();
        }
    }

    /* access modifiers changed from: private */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View view) {
            super(view);
        }
    }

    static {
        SparseArray<Credential.Type> sparseArray = new SparseArray<>();
        credentialViewTypes = sparseArray;
        sparseArray.put(C0010R$id.contents_userkey, Credential.Type.USER_KEY);
        credentialViewTypes.put(C0010R$id.contents_usercrt, Credential.Type.USER_CERTIFICATE);
        credentialViewTypes.put(C0010R$id.contents_cacrt, Credential.Type.CA_CERTIFICATE);
    }

    protected static View getCredentialView(Credential credential, int i, View view, ViewGroup viewGroup, boolean z) {
        int i2;
        if (view == null) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(i, viewGroup, false);
        }
        ((TextView) view.findViewById(C0010R$id.alias)).setText(credential.alias);
        TextView textView = (TextView) view.findViewById(C0010R$id.purpose);
        if (credential.isSystem()) {
            i2 = C0017R$string.credential_for_vpn_and_apps;
        } else {
            i2 = C0017R$string.credential_for_wifi;
        }
        textView.setText(i2);
        view.findViewById(C0010R$id.contents).setVisibility(z ? 0 : 8);
        if (z) {
            for (int i3 = 0; i3 < credentialViewTypes.size(); i3++) {
                view.findViewById(credentialViewTypes.keyAt(i3)).setVisibility(credential.storedTypes.contains(credentialViewTypes.valueAt(i3)) ? 0 : 8);
            }
        }
        return view;
    }

    /* access modifiers changed from: package-private */
    public static class Credential implements Parcelable {
        public static final Parcelable.Creator<Credential> CREATOR = new Parcelable.Creator<Credential>() {
            /* class com.android.settings.UserCredentialsSettings.Credential.AnonymousClass1 */

            @Override // android.os.Parcelable.Creator
            public Credential createFromParcel(Parcel parcel) {
                return new Credential(parcel);
            }

            @Override // android.os.Parcelable.Creator
            public Credential[] newArray(int i) {
                return new Credential[i];
            }
        };
        final String alias;
        final EnumSet<Type> storedTypes;
        final int uid;

        public int describeContents() {
            return 0;
        }

        /* access modifiers changed from: package-private */
        public enum Type {
            CA_CERTIFICATE("CACERT_"),
            USER_CERTIFICATE("USRCERT_"),
            USER_KEY("USRPKEY_", "USRSKEY_");
            
            final String[] prefix;

            private Type(String... strArr) {
                this.prefix = strArr;
            }
        }

        Credential(String str, int i) {
            this.storedTypes = EnumSet.noneOf(Type.class);
            this.alias = str;
            this.uid = i;
        }

        Credential(Parcel parcel) {
            this(parcel.readString(), parcel.readInt());
            long readLong = parcel.readLong();
            Type[] values = Type.values();
            for (Type type : values) {
                if (((1 << type.ordinal()) & readLong) != 0) {
                    this.storedTypes.add(type);
                }
            }
        }

        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(this.alias);
            parcel.writeInt(this.uid);
            Iterator it = this.storedTypes.iterator();
            long j = 0;
            while (it.hasNext()) {
                j |= 1 << ((Type) it.next()).ordinal();
            }
            parcel.writeLong(j);
        }

        public boolean isSystem() {
            return UserHandle.getAppId(this.uid) == 1000;
        }

        public String getAlias() {
            return this.alias;
        }

        public EnumSet<Type> getStoredTypes() {
            return this.storedTypes;
        }
    }
}
