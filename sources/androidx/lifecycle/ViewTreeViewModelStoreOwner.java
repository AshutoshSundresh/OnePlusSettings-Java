package androidx.lifecycle;

import android.view.View;
import androidx.lifecycle.viewmodel.R$id;

public class ViewTreeViewModelStoreOwner {
    public static void set(View view, ViewModelStoreOwner viewModelStoreOwner) {
        view.setTag(R$id.view_tree_view_model_store_owner, viewModelStoreOwner);
    }
}
