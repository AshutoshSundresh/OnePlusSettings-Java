package com.google.android.material.indicator.draw.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

public class PositionSavedState extends View.BaseSavedState {
    public static final Parcelable.Creator<PositionSavedState> CREATOR = new Parcelable.Creator<PositionSavedState>() {
        /* class com.google.android.material.indicator.draw.data.PositionSavedState.AnonymousClass1 */

        @Override // android.os.Parcelable.Creator
        public PositionSavedState createFromParcel(Parcel parcel) {
            return new PositionSavedState(parcel);
        }

        @Override // android.os.Parcelable.Creator
        public PositionSavedState[] newArray(int i) {
            return new PositionSavedState[i];
        }
    };
    private int lastSelectedPosition;
    private int selectedPosition;
    private int selectingPosition;

    public PositionSavedState(Parcelable parcelable) {
        super(parcelable);
    }

    private PositionSavedState(Parcel parcel) {
        super(parcel);
        this.selectedPosition = parcel.readInt();
        this.selectingPosition = parcel.readInt();
        this.lastSelectedPosition = parcel.readInt();
    }

    public int getSelectedPosition() {
        return this.selectedPosition;
    }

    public void setSelectedPosition(int i) {
        this.selectedPosition = i;
    }

    public int getSelectingPosition() {
        return this.selectingPosition;
    }

    public void setSelectingPosition(int i) {
        this.selectingPosition = i;
    }

    public int getLastSelectedPosition() {
        return this.lastSelectedPosition;
    }

    public void setLastSelectedPosition(int i) {
        this.lastSelectedPosition = i;
    }

    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeInt(this.selectedPosition);
        parcel.writeInt(this.selectingPosition);
        parcel.writeInt(this.lastSelectedPosition);
    }
}
