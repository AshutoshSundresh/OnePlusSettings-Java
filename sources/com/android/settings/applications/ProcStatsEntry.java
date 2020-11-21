package com.android.settings.applications;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.ArrayMap;
import android.util.Log;
import com.android.internal.app.procstats.ProcessState;
import com.android.internal.app.procstats.ProcessStats;
import com.android.internal.app.procstats.ServiceState;
import java.io.PrintWriter;
import java.util.ArrayList;

public final class ProcStatsEntry implements Parcelable {
    public static final Parcelable.Creator<ProcStatsEntry> CREATOR = new Parcelable.Creator<ProcStatsEntry>() {
        /* class com.android.settings.applications.ProcStatsEntry.AnonymousClass1 */

        @Override // android.os.Parcelable.Creator
        public ProcStatsEntry createFromParcel(Parcel parcel) {
            return new ProcStatsEntry(parcel);
        }

        @Override // android.os.Parcelable.Creator
        public ProcStatsEntry[] newArray(int i) {
            return new ProcStatsEntry[i];
        }
    };
    private static boolean DEBUG = false;
    final long mAvgBgMem;
    final long mAvgRunMem;
    String mBestTargetPackage;
    final long mBgDuration;
    final double mBgWeight;
    public CharSequence mLabel;
    final long mMaxBgMem;
    final long mMaxRunMem;
    final String mName;
    final String mPackage;
    final ArrayList<String> mPackages = new ArrayList<>();
    final long mRunDuration;
    final double mRunWeight;
    ArrayMap<String, ArrayList<Service>> mServices = new ArrayMap<>(1);
    final int mUid;

    public int describeContents() {
        return 0;
    }

    public ProcStatsEntry(ProcessState processState, String str, ProcessStats.ProcessDataCollection processDataCollection, ProcessStats.ProcessDataCollection processDataCollection2, boolean z) {
        processState.computeProcessData(processDataCollection, 0);
        processState.computeProcessData(processDataCollection2, 0);
        this.mPackage = processState.getPackage();
        this.mUid = processState.getUid();
        this.mName = processState.getName();
        this.mPackages.add(str);
        this.mBgDuration = processDataCollection.totalTime;
        this.mAvgBgMem = z ? processDataCollection.avgUss : processDataCollection.avgPss;
        this.mMaxBgMem = z ? processDataCollection.maxUss : processDataCollection.maxPss;
        this.mBgWeight = ((double) this.mAvgBgMem) * ((double) this.mBgDuration);
        this.mRunDuration = processDataCollection2.totalTime;
        this.mAvgRunMem = z ? processDataCollection2.avgUss : processDataCollection2.avgPss;
        this.mMaxRunMem = z ? processDataCollection2.maxUss : processDataCollection2.maxPss;
        this.mRunWeight = ((double) this.mAvgRunMem) * ((double) this.mRunDuration);
        if (DEBUG) {
            Log.d("ProcStatsEntry", "New proc entry " + processState.getName() + ": dur=" + this.mBgDuration + " avgpss=" + this.mAvgBgMem + " weight=" + this.mBgWeight);
        }
    }

    public ProcStatsEntry(String str, int i, String str2, long j, long j2, long j3) {
        this.mPackage = str;
        this.mUid = i;
        this.mName = str2;
        this.mRunDuration = j;
        this.mBgDuration = j;
        this.mMaxRunMem = j2;
        this.mAvgRunMem = j2;
        this.mMaxBgMem = j2;
        this.mAvgBgMem = j2;
        double d = ((double) j3) * ((double) j2);
        this.mRunWeight = d;
        this.mBgWeight = d;
        if (DEBUG) {
            Log.d("ProcStatsEntry", "New proc entry " + str2 + ": dur=" + this.mBgDuration + " avgpss=" + this.mAvgBgMem + " weight=" + this.mBgWeight);
        }
    }

    public ProcStatsEntry(Parcel parcel) {
        this.mPackage = parcel.readString();
        this.mUid = parcel.readInt();
        this.mName = parcel.readString();
        parcel.readStringList(this.mPackages);
        this.mBgDuration = parcel.readLong();
        this.mAvgBgMem = parcel.readLong();
        this.mMaxBgMem = parcel.readLong();
        this.mBgWeight = parcel.readDouble();
        this.mRunDuration = parcel.readLong();
        this.mAvgRunMem = parcel.readLong();
        this.mMaxRunMem = parcel.readLong();
        this.mRunWeight = parcel.readDouble();
        this.mBestTargetPackage = parcel.readString();
        int readInt = parcel.readInt();
        if (readInt > 0) {
            this.mServices.ensureCapacity(readInt);
            for (int i = 0; i < readInt; i++) {
                String readString = parcel.readString();
                ArrayList arrayList = new ArrayList();
                parcel.readTypedList(arrayList, Service.CREATOR);
                this.mServices.append(readString, arrayList);
            }
        }
    }

    public void addPackage(String str) {
        this.mPackages.add(str);
    }

    /* JADX WARNING: Removed duplicated region for block: B:107:0x0388  */
    /* JADX WARNING: Removed duplicated region for block: B:111:0x03b6  */
    /* JADX WARNING: Removed duplicated region for block: B:123:0x03f1  */
    /* JADX WARNING: Removed duplicated region for block: B:144:0x0412 A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void evaluateTargetPackage(android.content.pm.PackageManager r24, com.android.internal.app.procstats.ProcessStats r25, com.android.internal.app.procstats.ProcessStats.ProcessDataCollection r26, com.android.internal.app.procstats.ProcessStats.ProcessDataCollection r27, java.util.Comparator<com.android.settings.applications.ProcStatsEntry> r28, boolean r29) {
        /*
        // Method dump skipped, instructions count: 1099
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.applications.ProcStatsEntry.evaluateTargetPackage(android.content.pm.PackageManager, com.android.internal.app.procstats.ProcessStats, com.android.internal.app.procstats.ProcessStats$ProcessDataCollection, com.android.internal.app.procstats.ProcessStats$ProcessDataCollection, java.util.Comparator, boolean):void");
    }

    public void addService(ServiceState serviceState) {
        ArrayList<Service> arrayList = this.mServices.get(serviceState.getPackage());
        if (arrayList == null) {
            arrayList = new ArrayList<>();
            this.mServices.put(serviceState.getPackage(), arrayList);
        }
        arrayList.add(new Service(serviceState));
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.mPackage);
        parcel.writeInt(this.mUid);
        parcel.writeString(this.mName);
        parcel.writeStringList(this.mPackages);
        parcel.writeLong(this.mBgDuration);
        parcel.writeLong(this.mAvgBgMem);
        parcel.writeLong(this.mMaxBgMem);
        parcel.writeDouble(this.mBgWeight);
        parcel.writeLong(this.mRunDuration);
        parcel.writeLong(this.mAvgRunMem);
        parcel.writeLong(this.mMaxRunMem);
        parcel.writeDouble(this.mRunWeight);
        parcel.writeString(this.mBestTargetPackage);
        int size = this.mServices.size();
        parcel.writeInt(size);
        for (int i2 = 0; i2 < size; i2++) {
            parcel.writeString(this.mServices.keyAt(i2));
            parcel.writeTypedList(this.mServices.valueAt(i2));
        }
    }

    public int getUid() {
        return this.mUid;
    }

    public static final class Service implements Parcelable {
        public static final Parcelable.Creator<Service> CREATOR = new Parcelable.Creator<Service>() {
            /* class com.android.settings.applications.ProcStatsEntry.Service.AnonymousClass1 */

            @Override // android.os.Parcelable.Creator
            public Service createFromParcel(Parcel parcel) {
                return new Service(parcel);
            }

            @Override // android.os.Parcelable.Creator
            public Service[] newArray(int i) {
                return new Service[i];
            }
        };
        final long mDuration;
        final String mName;
        final String mPackage;
        final String mProcess;

        public int describeContents() {
            return 0;
        }

        public Service(ServiceState serviceState) {
            this.mPackage = serviceState.getPackage();
            this.mName = serviceState.getName();
            this.mProcess = serviceState.getProcessName();
            this.mDuration = serviceState.dumpTime((PrintWriter) null, (String) null, 0, -1, 0, 0);
        }

        public Service(Parcel parcel) {
            this.mPackage = parcel.readString();
            this.mName = parcel.readString();
            this.mProcess = parcel.readString();
            this.mDuration = parcel.readLong();
        }

        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(this.mPackage);
            parcel.writeString(this.mName);
            parcel.writeString(this.mProcess);
            parcel.writeLong(this.mDuration);
        }
    }
}
