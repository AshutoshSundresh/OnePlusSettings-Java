package com.oneplus.security.network.operator;

import android.content.Context;

public class OperatorDataModelFactory {
    public static OperatorModelInterface getOperatorDataModel(Context context) {
        return NativeOperatorDataModel.getInstance(context);
    }
}
