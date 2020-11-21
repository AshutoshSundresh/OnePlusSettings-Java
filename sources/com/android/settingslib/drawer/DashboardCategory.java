package com.android.settingslib.drawer;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DashboardCategory implements Parcelable {
    public static final Parcelable.Creator<DashboardCategory> CREATOR = new Parcelable.Creator<DashboardCategory>() {
        /* class com.android.settingslib.drawer.DashboardCategory.AnonymousClass1 */

        @Override // android.os.Parcelable.Creator
        public DashboardCategory createFromParcel(Parcel parcel) {
            return new DashboardCategory(parcel);
        }

        @Override // android.os.Parcelable.Creator
        public DashboardCategory[] newArray(int i) {
            return new DashboardCategory[i];
        }
    };
    public final String key;
    private List<Tile> mTiles = new ArrayList();

    public int describeContents() {
        return 0;
    }

    public DashboardCategory(String str) {
        this.key = str;
    }

    DashboardCategory(Parcel parcel) {
        this.key = parcel.readString();
        int readInt = parcel.readInt();
        for (int i = 0; i < readInt; i++) {
            this.mTiles.add(Tile.CREATOR.createFromParcel(parcel));
        }
    }

    public synchronized List<Tile> getTiles() {
        ArrayList arrayList;
        arrayList = new ArrayList(this.mTiles.size());
        for (Tile tile : this.mTiles) {
            arrayList.add(tile);
        }
        return arrayList;
    }

    public synchronized void addTile(Tile tile) {
        this.mTiles.add(tile);
    }

    public synchronized void removeTile(int i) {
        this.mTiles.remove(i);
    }

    public int getTilesCount() {
        return this.mTiles.size();
    }

    public Tile getTile(int i) {
        return this.mTiles.get(i);
    }

    public void sortTiles() {
        Collections.sort(this.mTiles, Tile.TILE_COMPARATOR);
    }

    public synchronized void sortTiles(String str) {
        Collections.sort(this.mTiles, new Comparator(str) {
            /* class com.android.settingslib.drawer.$$Lambda$DashboardCategory$hMIMtvkEGTs2t7RyY7SqwVmOgI */
            public final /* synthetic */ String f$0;

            {
                this.f$0 = r1;
            }

            @Override // java.util.Comparator
            public final int compare(Object obj, Object obj2) {
                return DashboardCategory.lambda$sortTiles$0(this.f$0, (Tile) obj, (Tile) obj2);
            }
        });
    }

    static /* synthetic */ int lambda$sortTiles$0(String str, Tile tile, Tile tile2) {
        int order = tile2.getOrder() - tile.getOrder();
        if (order != 0) {
            return order;
        }
        String packageName = tile.getPackageName();
        String packageName2 = tile2.getPackageName();
        int compare = String.CASE_INSENSITIVE_ORDER.compare(packageName, packageName2);
        if (compare != 0) {
            if (TextUtils.equals(packageName, str)) {
                return -1;
            }
            if (TextUtils.equals(packageName2, str)) {
                return 1;
            }
        }
        return compare;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.key);
        int size = this.mTiles.size();
        parcel.writeInt(size);
        for (int i2 = 0; i2 < size; i2++) {
            this.mTiles.get(i2).writeToParcel(parcel, i);
        }
    }
}
