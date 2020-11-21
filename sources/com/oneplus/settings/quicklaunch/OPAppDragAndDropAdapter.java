package com.oneplus.settings.quicklaunch;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import androidx.core.view.MotionEventCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.android.settings.C0012R$layout;
import com.oneplus.settings.better.OPAppModel;
import com.oneplus.settings.utils.OPUtils;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/* access modifiers changed from: package-private */
public class OPAppDragAndDropAdapter extends RecyclerView.Adapter<CustomViewHolder> {
    private List<OPAppModel> mAppItemList;
    private final Context mContext;
    private boolean mDragEnabled = true;
    private final ItemTouchHelper mItemTouchHelper;
    private RecyclerView mParentView = null;
    private boolean mRemoveMode = false;

    /* access modifiers changed from: package-private */
    public class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnTouchListener {
        private final OPAppDragCell mAppDragCell;

        public CustomViewHolder(OPAppDragCell oPAppDragCell) {
            super(oPAppDragCell);
            this.mAppDragCell = oPAppDragCell;
            oPAppDragCell.getDragHandle().setOnTouchListener(this);
        }

        public OPAppDragCell getAppDragCell() {
            return this.mAppDragCell;
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (!OPAppDragAndDropAdapter.this.mDragEnabled || MotionEventCompat.getActionMasked(motionEvent) != 0) {
                return false;
            }
            OPAppDragAndDropAdapter.this.mItemTouchHelper.startDrag(this);
            return false;
        }
    }

    public void setAppList(List<OPAppModel> list) {
        this.mAppItemList = list;
        notifyDataSetChanged();
    }

    public OPAppDragAndDropAdapter(Context context, List<OPAppModel> list) {
        NumberFormat.getNumberInstance();
        this.mAppItemList = list;
        this.mContext = context;
        final float applyDimension = TypedValue.applyDimension(1, 8.0f, context.getResources().getDisplayMetrics());
        this.mItemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(3, 0) {
            /* class com.oneplus.settings.quicklaunch.OPAppDragAndDropAdapter.AnonymousClass1 */
            private int mSelectionStatus = -1;

            @Override // androidx.recyclerview.widget.ItemTouchHelper.Callback
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int i) {
            }

            @Override // androidx.recyclerview.widget.ItemTouchHelper.Callback
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder2) {
                OPAppDragAndDropAdapter.this.onItemMove(viewHolder.getAdapterPosition(), viewHolder2.getAdapterPosition());
                return true;
            }

            @Override // androidx.recyclerview.widget.ItemTouchHelper.Callback
            public void onChildDraw(Canvas canvas, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float f, float f2, int i, boolean z) {
                super.onChildDraw(canvas, recyclerView, viewHolder, f, f2, i, z);
                int i2 = this.mSelectionStatus;
                if (i2 != -1) {
                    viewHolder.itemView.setElevation(i2 == 1 ? applyDimension : 0.0f);
                    this.mSelectionStatus = -1;
                }
            }

            @Override // androidx.recyclerview.widget.ItemTouchHelper.Callback
            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int i) {
                super.onSelectedChanged(viewHolder, i);
                if (i == 2) {
                    this.mSelectionStatus = 1;
                } else if (i == 0) {
                    this.mSelectionStatus = 0;
                }
            }
        });
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        this.mParentView = recyclerView;
        this.mItemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new CustomViewHolder((OPAppDragCell) LayoutInflater.from(this.mContext).inflate(C0012R$layout.op_app_drag_cell, viewGroup, false));
    }

    public void onBindViewHolder(CustomViewHolder customViewHolder, final int i) {
        OPAppModel oPAppModel = this.mAppItemList.get(i);
        final OPAppDragCell appDragCell = customViewHolder.getAppDragCell();
        appDragCell.setLabelAndDescription(oPAppModel.getLabel(), "");
        if (oPAppModel.getType() == 1) {
            appDragCell.setAppIcon(oPAppModel.getShortCutIcon());
            appDragCell.setSmallIcon(oPAppModel.getAppIcon());
        } else {
            appDragCell.setAppIcon(oPAppModel.getAppIcon());
            appDragCell.setSmallIcon(null);
        }
        appDragCell.setShowCheckbox(false);
        appDragCell.setShowAppIcon(true);
        appDragCell.setShowHandle(true);
        appDragCell.setChecked(false);
        appDragCell.setTag(oPAppModel);
        appDragCell.getCheckbox().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(this) {
            /* class com.oneplus.settings.quicklaunch.OPAppDragAndDropAdapter.AnonymousClass2 */

            public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                ((OPAppModel) appDragCell.getTag()).setSelected(z);
            }
        });
        appDragCell.getDeleteButton().setOnClickListener(new View.OnClickListener() {
            /* class com.oneplus.settings.quicklaunch.OPAppDragAndDropAdapter.AnonymousClass3 */

            public void onClick(View view) {
                OPAppDragAndDropAdapter.this.removeItem(i);
            }
        });
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        List<OPAppModel> list = this.mAppItemList;
        int size = list != null ? list.size() : 0;
        if (size < 2 || this.mRemoveMode) {
            setDragEnabled(false);
        } else {
            setDragEnabled(true);
        }
        return size;
    }

    /* access modifiers changed from: package-private */
    public void onItemMove(int i, int i2) {
        if (i < 0 || i2 < 0) {
            Log.e("LocaleDragAndDropAdapter", String.format(Locale.US, "Negative position in onItemMove %d -> %d", Integer.valueOf(i), Integer.valueOf(i2)));
        } else {
            this.mAppItemList.remove(i);
            this.mAppItemList.add(i2, this.mAppItemList.get(i));
        }
        notifyItemChanged(i);
        notifyItemChanged(i2);
        notifyItemMoved(i, i2);
    }

    /* access modifiers changed from: package-private */
    public void setRemoveMode(boolean z) {
        this.mRemoveMode = z;
        int size = this.mAppItemList.size();
        for (int i = 0; i < size; i++) {
            this.mAppItemList.get(i).setSelected(false);
            notifyItemChanged(i);
        }
    }

    /* access modifiers changed from: package-private */
    public boolean isRemoveMode() {
        return this.mRemoveMode;
    }

    /* access modifiers changed from: package-private */
    public void removeItem(int i) {
        int size = this.mAppItemList.size();
        if (i >= 0 && i < size) {
            OPAppModel oPAppModel = this.mAppItemList.get(i);
            StringBuilder sb = new StringBuilder(OPUtils.getAllQuickLaunchStrings(this.mContext));
            if (oPAppModel.getType() == 0) {
                String quickLaunchAppString = OPUtils.getQuickLaunchAppString(oPAppModel);
                int indexOf = sb.indexOf(quickLaunchAppString);
                sb.delete(indexOf, quickLaunchAppString.length() + indexOf);
            } else if (oPAppModel.getType() == 1) {
                String quickLaunchShortcutsString = OPUtils.getQuickLaunchShortcutsString(oPAppModel);
                int indexOf2 = sb.indexOf(quickLaunchShortcutsString);
                sb.delete(indexOf2, quickLaunchShortcutsString.length() + indexOf2);
            } else if (oPAppModel.getType() == 2) {
                String quickPayAppString = OPUtils.getQuickPayAppString(oPAppModel);
                int indexOf3 = sb.indexOf(quickPayAppString);
                sb.delete(indexOf3, quickPayAppString.length() + indexOf3);
            } else if (oPAppModel.getType() == 3) {
                String quickMiniProgrameString = OPUtils.getQuickMiniProgrameString(oPAppModel);
                int indexOf4 = sb.indexOf(quickMiniProgrameString);
                sb.delete(indexOf4, quickMiniProgrameString.length() + indexOf4);
            }
            OPUtils.saveQuickLaunchStrings(this.mContext, sb.toString());
            this.mAppItemList.remove(i);
            notifyDataSetChanged();
        }
    }

    /* access modifiers changed from: package-private */
    public void removeChecked() {
        for (int size = this.mAppItemList.size() - 1; size >= 0; size--) {
            if (this.mAppItemList.get(size).isSelected()) {
                OPAppModel oPAppModel = this.mAppItemList.get(size);
                StringBuilder sb = new StringBuilder(OPUtils.getAllQuickLaunchStrings(this.mContext));
                if (oPAppModel.getType() == 0) {
                    String quickLaunchAppString = OPUtils.getQuickLaunchAppString(oPAppModel);
                    int indexOf = sb.indexOf(quickLaunchAppString);
                    sb.delete(indexOf, quickLaunchAppString.length() + indexOf);
                } else if (oPAppModel.getType() == 1) {
                    String quickLaunchShortcutsString = OPUtils.getQuickLaunchShortcutsString(oPAppModel);
                    int indexOf2 = sb.indexOf(quickLaunchShortcutsString);
                    sb.delete(indexOf2, quickLaunchShortcutsString.length() + indexOf2);
                } else if (oPAppModel.getType() == 2) {
                    String quickPayAppString = OPUtils.getQuickPayAppString(oPAppModel);
                    int indexOf3 = sb.indexOf(quickPayAppString);
                    sb.delete(indexOf3, quickPayAppString.length() + indexOf3);
                } else if (oPAppModel.getType() == 3) {
                    String quickMiniProgrameString = OPUtils.getQuickMiniProgrameString(oPAppModel);
                    int indexOf4 = sb.indexOf(quickMiniProgrameString);
                    sb.delete(indexOf4, quickMiniProgrameString.length() + indexOf4);
                }
                OPUtils.saveQuickLaunchStrings(this.mContext, sb.toString());
                this.mAppItemList.remove(size);
            }
        }
        notifyDataSetChanged();
        doTheUpdate();
    }

    public void doTheUpdate() {
        updateLocalesWhenAnimationStops();
    }

    public void updateLocalesWhenAnimationStops() {
        final int size = this.mAppItemList.size();
        this.mParentView.getItemAnimator().isRunning(new RecyclerView.ItemAnimator.ItemAnimatorFinishedListener() {
            /* class com.oneplus.settings.quicklaunch.OPAppDragAndDropAdapter.AnonymousClass4 */

            @Override // androidx.recyclerview.widget.RecyclerView.ItemAnimator.ItemAnimatorFinishedListener
            public void onAnimationsFinished() {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < size; i++) {
                    OPAppModel oPAppModel = (OPAppModel) OPAppDragAndDropAdapter.this.mAppItemList.get(i);
                    if (oPAppModel.getType() == 0) {
                        sb.append(OPUtils.getQuickLaunchAppString(oPAppModel));
                    } else if (oPAppModel.getType() == 1) {
                        sb.append(OPUtils.getQuickLaunchShortcutsString(oPAppModel));
                    } else if (oPAppModel.getType() == 2) {
                        sb.append(OPUtils.getQuickPayAppString(oPAppModel));
                    } else if (oPAppModel.getType() == 3) {
                        sb.append(OPUtils.getQuickMiniProgrameString(oPAppModel));
                    }
                    OPUtils.saveQuickLaunchStrings(OPAppDragAndDropAdapter.this.mContext, sb.toString());
                }
            }
        });
    }

    private void setDragEnabled(boolean z) {
        this.mDragEnabled = z;
    }

    public void saveState(Bundle bundle) {
        if (bundle != null) {
            ArrayList<String> arrayList = new ArrayList<>();
            for (OPAppModel oPAppModel : this.mAppItemList) {
                if (oPAppModel.isSelected()) {
                    arrayList.add(oPAppModel.getPkgName());
                }
            }
            bundle.putStringArrayList("selectedLocales", arrayList);
        }
    }

    public void restoreState(Bundle bundle) {
        ArrayList<String> stringArrayList;
        if (!(bundle == null || !this.mRemoveMode || (stringArrayList = bundle.getStringArrayList("selectedLocales")) == null || stringArrayList.isEmpty())) {
            for (OPAppModel oPAppModel : this.mAppItemList) {
                oPAppModel.setSelected(stringArrayList.contains(oPAppModel.getPkgName()));
            }
            notifyItemRangeChanged(0, this.mAppItemList.size());
        }
    }
}
