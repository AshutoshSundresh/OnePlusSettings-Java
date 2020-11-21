package com.android.settings.password;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.android.settings.C0012R$layout;

public class PasswordRequirementAdapter extends RecyclerView.Adapter<PasswordRequirementViewHolder> {
    private String[] mRequirements;

    public PasswordRequirementAdapter() {
        setHasStableIds(true);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public PasswordRequirementViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new PasswordRequirementViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(C0012R$layout.password_requirement_item, viewGroup, false));
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        return this.mRequirements.length;
    }

    public void setRequirements(String[] strArr) {
        this.mRequirements = strArr;
        notifyDataSetChanged();
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public long getItemId(int i) {
        return (long) this.mRequirements[i].hashCode();
    }

    public void onBindViewHolder(PasswordRequirementViewHolder passwordRequirementViewHolder, int i) {
        passwordRequirementViewHolder.mDescriptionText.setText(this.mRequirements[i]);
    }

    public static class PasswordRequirementViewHolder extends RecyclerView.ViewHolder {
        private TextView mDescriptionText;

        public PasswordRequirementViewHolder(View view) {
            super(view);
            this.mDescriptionText = (TextView) view;
        }
    }
}
