package com.android.settings.applications;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import com.android.settings.C0017R$string;
import com.android.settingslib.Utils;
import java.util.ArrayList;

public class ProcStatsPackageEntry implements Parcelable {
    public static final Parcelable.Creator<ProcStatsPackageEntry> CREATOR = new Parcelable.Creator<ProcStatsPackageEntry>() {
        /* class com.android.settings.applications.ProcStatsPackageEntry.AnonymousClass1 */

        @Override // android.os.Parcelable.Creator
        public ProcStatsPackageEntry createFromParcel(Parcel parcel) {
            return new ProcStatsPackageEntry(parcel);
        }

        @Override // android.os.Parcelable.Creator
        public ProcStatsPackageEntry[] newArray(int i) {
            return new ProcStatsPackageEntry[i];
        }
    };
    long mAvgBgMem;
    long mAvgRunMem;
    long mBgDuration;
    double mBgWeight;
    final ArrayList<ProcStatsEntry> mEntries = new ArrayList<>();
    long mMaxBgMem;
    long mMaxRunMem;
    final String mPackage;
    long mRunDuration;
    double mRunWeight;
    public String mUiLabel;
    public ApplicationInfo mUiTargetApp;

    public int describeContents() {
        return 0;
    }

    public ProcStatsPackageEntry(String str, long j) {
        this.mPackage = str;
    }

    public ProcStatsPackageEntry(Parcel parcel) {
        this.mPackage = parcel.readString();
        parcel.readTypedList(this.mEntries, ProcStatsEntry.CREATOR);
        this.mBgDuration = parcel.readLong();
        this.mAvgBgMem = parcel.readLong();
        this.mMaxBgMem = parcel.readLong();
        this.mBgWeight = parcel.readDouble();
        this.mRunDuration = parcel.readLong();
        this.mAvgRunMem = parcel.readLong();
        this.mMaxRunMem = parcel.readLong();
        this.mRunWeight = parcel.readDouble();
    }

    public void addEntry(ProcStatsEntry procStatsEntry) {
        this.mEntries.add(procStatsEntry);
    }

    public void updateMetrics() {
        this.mMaxBgMem = 0;
        this.mAvgBgMem = 0;
        this.mBgDuration = 0;
        this.mBgWeight = 0.0d;
        this.mMaxRunMem = 0;
        this.mAvgRunMem = 0;
        this.mRunDuration = 0;
        this.mRunWeight = 0.0d;
        int size = this.mEntries.size();
        for (int i = 0; i < size; i++) {
            ProcStatsEntry procStatsEntry = this.mEntries.get(i);
            this.mBgDuration = Math.max(procStatsEntry.mBgDuration, this.mBgDuration);
            this.mAvgBgMem += procStatsEntry.mAvgBgMem;
            this.mBgWeight += procStatsEntry.mBgWeight;
            this.mRunDuration = Math.max(procStatsEntry.mRunDuration, this.mRunDuration);
            this.mAvgRunMem += procStatsEntry.mAvgRunMem;
            this.mRunWeight += procStatsEntry.mRunWeight;
            this.mMaxBgMem += procStatsEntry.mMaxBgMem;
            this.mMaxRunMem += procStatsEntry.mMaxRunMem;
        }
        long j = (long) size;
        this.mAvgBgMem /= j;
        this.mAvgRunMem /= j;
    }

    public void retrieveUiData(Context context, PackageManager packageManager) {
        this.mUiTargetApp = null;
        String str = this.mPackage;
        this.mUiLabel = str;
        try {
            if ("os".equals(str)) {
                this.mUiTargetApp = packageManager.getApplicationInfo("android", 4227584);
                this.mUiLabel = context.getString(C0017R$string.process_stats_os_label);
                return;
            }
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(this.mPackage, 4227584);
            this.mUiTargetApp = applicationInfo;
            this.mUiLabel = applicationInfo.loadLabel(packageManager).toString();
        } catch (PackageManager.NameNotFoundException unused) {
            Log.d("ProcStatsEntry", "could not find package: " + this.mPackage);
        }
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.mPackage);
        parcel.writeTypedList(this.mEntries);
        parcel.writeLong(this.mBgDuration);
        parcel.writeLong(this.mAvgBgMem);
        parcel.writeLong(this.mMaxBgMem);
        parcel.writeDouble(this.mBgWeight);
        parcel.writeLong(this.mRunDuration);
        parcel.writeLong(this.mAvgRunMem);
        parcel.writeLong(this.mMaxRunMem);
        parcel.writeDouble(this.mRunWeight);
    }

    public static CharSequence getFrequency(float f, Context context) {
        if (f > 0.95f) {
            return context.getString(C0017R$string.always_running, Utils.formatPercentage((int) (f * 100.0f)));
        } else if (f > 0.25f) {
            return context.getString(C0017R$string.sometimes_running, Utils.formatPercentage((int) (f * 100.0f)));
        } else {
            return context.getString(C0017R$string.rarely_running, Utils.formatPercentage((int) (f * 100.0f)));
        }
    }

    public double getRunWeight() {
        return this.mRunWeight;
    }

    public double getBgWeight() {
        return this.mBgWeight;
    }

    public ArrayList<ProcStatsEntry> getEntries() {
        return this.mEntries;
    }
}
