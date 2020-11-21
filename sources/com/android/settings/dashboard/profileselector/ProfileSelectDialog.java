package com.android.settings.dashboard.profileselector;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.Log;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.R$string;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import com.android.settingslib.drawer.Tile;
import java.util.ArrayList;

public class ProfileSelectDialog extends DialogFragment implements DialogInterface.OnClickListener {
    private static final boolean DEBUG = Log.isLoggable("ProfileSelectDialog", 3);
    private Tile mSelectedTile;
    private int mSourceMetricCategory;

    public static void show(FragmentManager fragmentManager, Tile tile, int i) {
        ProfileSelectDialog profileSelectDialog = new ProfileSelectDialog();
        Bundle bundle = new Bundle();
        bundle.putParcelable("selectedTile", tile);
        bundle.putInt("sourceMetricCategory", i);
        profileSelectDialog.setArguments(bundle);
        profileSelectDialog.show(fragmentManager, "select_profile");
    }

    @Override // androidx.fragment.app.Fragment, androidx.fragment.app.DialogFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mSelectedTile = (Tile) getArguments().getParcelable("selectedTile");
        this.mSourceMetricCategory = getArguments().getInt("sourceMetricCategory");
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        FragmentActivity activity = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        UserAdapter createUserAdapter = UserAdapter.createUserAdapter(UserManager.get(activity), activity, this.mSelectedTile.userHandle);
        builder.setTitle(R$string.choose_profile);
        builder.setAdapter(createUserAdapter, this);
        return builder.create();
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        UserHandle userHandle = this.mSelectedTile.userHandle.get(i);
        Intent intent = this.mSelectedTile.getIntent();
        MetricsFeatureProvider metricsFeatureProvider = FeatureFactory.getFactory(getContext()).getMetricsFeatureProvider();
        int i2 = this.mSourceMetricCategory;
        boolean z = true;
        if (i != 1) {
            z = false;
        }
        metricsFeatureProvider.logStartedIntentWithProfile(intent, i2, z);
        intent.addFlags(32768);
        getActivity().startActivityAsUser(intent, userHandle);
    }

    public static void updateUserHandlesIfNeeded(Context context, Tile tile) {
        ArrayList<UserHandle> arrayList = tile.userHandle;
        if (arrayList != null && arrayList.size() > 1) {
            UserManager userManager = UserManager.get(context);
            for (int size = arrayList.size() - 1; size >= 0; size--) {
                if (userManager.getUserInfo(arrayList.get(size).getIdentifier()) == null) {
                    if (DEBUG) {
                        Log.d("ProfileSelectDialog", "Delete the user: " + arrayList.get(size).getIdentifier());
                    }
                    arrayList.remove(size);
                }
            }
        }
    }
}
