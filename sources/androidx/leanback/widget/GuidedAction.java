package androidx.leanback.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.leanback.R$string;
import java.util.List;

public class GuidedAction extends Action {
    int mActionFlags;
    String[] mAutofillHints;
    int mCheckSetId;
    int mDescriptionEditInputType;
    int mDescriptionInputType;
    private CharSequence mEditDescription;
    int mEditInputType;
    private CharSequence mEditTitle;
    int mEditable;
    int mInputType;
    Intent mIntent;
    List<GuidedAction> mSubActions;

    static boolean isPasswordVariant(int i) {
        int i2 = i & 4080;
        return i2 == 128 || i2 == 144 || i2 == 224;
    }

    public static abstract class BuilderBase<B extends BuilderBase> {
        private int mActionFlags;
        private String[] mAutofillHints;
        private int mCheckSetId = 0;
        private Context mContext;
        private CharSequence mDescription;
        private int mDescriptionEditInputType = 1;
        private int mDescriptionInputType = 524289;
        private CharSequence mEditDescription;
        private int mEditInputType = 1;
        private CharSequence mEditTitle;
        private int mEditable = 0;
        private Drawable mIcon;
        private long mId;
        private int mInputType = 524289;
        private Intent mIntent;
        private List<GuidedAction> mSubActions;
        private CharSequence mTitle;

        public BuilderBase(Context context) {
            this.mContext = context;
            this.mActionFlags = 112;
        }

        /* access modifiers changed from: protected */
        public final void applyValues(GuidedAction guidedAction) {
            guidedAction.setId(this.mId);
            guidedAction.setLabel1(this.mTitle);
            guidedAction.setEditTitle(this.mEditTitle);
            guidedAction.setLabel2(this.mDescription);
            guidedAction.setEditDescription(this.mEditDescription);
            guidedAction.setIcon(this.mIcon);
            guidedAction.mIntent = this.mIntent;
            guidedAction.mEditable = this.mEditable;
            guidedAction.mInputType = this.mInputType;
            guidedAction.mDescriptionInputType = this.mDescriptionInputType;
            guidedAction.mAutofillHints = this.mAutofillHints;
            guidedAction.mEditInputType = this.mEditInputType;
            guidedAction.mDescriptionEditInputType = this.mDescriptionEditInputType;
            guidedAction.mActionFlags = this.mActionFlags;
            guidedAction.mCheckSetId = this.mCheckSetId;
            guidedAction.mSubActions = this.mSubActions;
        }

        public B clickAction(long j) {
            if (j == -4) {
                this.mId = -4;
                this.mTitle = this.mContext.getString(17039370);
            } else if (j == -5) {
                this.mId = -5;
                this.mTitle = this.mContext.getString(17039360);
            } else if (j == -6) {
                this.mId = -6;
                this.mTitle = this.mContext.getString(R$string.lb_guidedaction_finish_title);
            } else if (j == -7) {
                this.mId = -7;
                this.mTitle = this.mContext.getString(R$string.lb_guidedaction_continue_title);
            } else if (j == -8) {
                this.mId = -8;
                this.mTitle = this.mContext.getString(17039370);
            } else if (j == -9) {
                this.mId = -9;
                this.mTitle = this.mContext.getString(17039360);
            }
            return this;
        }
    }

    public static class Builder extends BuilderBase<Builder> {
        @Deprecated
        public Builder() {
            super(null);
        }

        public Builder(Context context) {
            super(context);
        }

        public GuidedAction build() {
            GuidedAction guidedAction = new GuidedAction();
            applyValues(guidedAction);
            return guidedAction;
        }
    }

    protected GuidedAction() {
        super(0);
    }

    private void setFlags(int i, int i2) {
        this.mActionFlags = (i & i2) | (this.mActionFlags & (~i2));
    }

    public CharSequence getTitle() {
        return getLabel1();
    }

    public void setTitle(CharSequence charSequence) {
        setLabel1(charSequence);
    }

    public CharSequence getEditTitle() {
        return this.mEditTitle;
    }

    public void setEditTitle(CharSequence charSequence) {
        this.mEditTitle = charSequence;
    }

    public CharSequence getEditDescription() {
        return this.mEditDescription;
    }

    public void setEditDescription(CharSequence charSequence) {
        this.mEditDescription = charSequence;
    }

    public CharSequence getDescription() {
        return getLabel2();
    }

    public void setDescription(CharSequence charSequence) {
        setLabel2(charSequence);
    }

    public boolean isEditable() {
        return this.mEditable == 1;
    }

    public boolean isDescriptionEditable() {
        return this.mEditable == 2;
    }

    public boolean hasTextEditable() {
        int i = this.mEditable;
        return i == 1 || i == 2;
    }

    public boolean hasEditableActivatorView() {
        return this.mEditable == 3;
    }

    public int getEditInputType() {
        return this.mEditInputType;
    }

    public int getDescriptionEditInputType() {
        return this.mDescriptionEditInputType;
    }

    public int getInputType() {
        return this.mInputType;
    }

    public int getDescriptionInputType() {
        return this.mDescriptionInputType;
    }

    public boolean isChecked() {
        return (this.mActionFlags & 1) == 1;
    }

    public void setChecked(boolean z) {
        setFlags(z ? 1 : 0, 1);
    }

    public int getCheckSetId() {
        return this.mCheckSetId;
    }

    public boolean hasMultilineDescription() {
        return (this.mActionFlags & 2) == 2;
    }

    public boolean isEnabled() {
        return (this.mActionFlags & 16) == 16;
    }

    public boolean isFocusable() {
        return (this.mActionFlags & 32) == 32;
    }

    public String[] getAutofillHints() {
        return this.mAutofillHints;
    }

    public boolean hasNext() {
        return (this.mActionFlags & 4) == 4;
    }

    public boolean infoOnly() {
        return (this.mActionFlags & 8) == 8;
    }

    public List<GuidedAction> getSubActions() {
        return this.mSubActions;
    }

    public boolean hasSubActions() {
        return this.mSubActions != null;
    }

    public final boolean isAutoSaveRestoreEnabled() {
        return (this.mActionFlags & 64) == 64;
    }

    public void onSaveInstanceState(Bundle bundle, String str) {
        if (needAutoSaveTitle() && getTitle() != null) {
            bundle.putString(str, getTitle().toString());
        } else if (needAutoSaveDescription() && getDescription() != null) {
            bundle.putString(str, getDescription().toString());
        } else if (getCheckSetId() != 0) {
            bundle.putBoolean(str, isChecked());
        }
    }

    public void onRestoreInstanceState(Bundle bundle, String str) {
        if (needAutoSaveTitle()) {
            String string = bundle.getString(str);
            if (string != null) {
                setTitle(string);
            }
        } else if (needAutoSaveDescription()) {
            String string2 = bundle.getString(str);
            if (string2 != null) {
                setDescription(string2);
            }
        } else if (getCheckSetId() != 0) {
            setChecked(bundle.getBoolean(str, isChecked()));
        }
    }

    /* access modifiers changed from: package-private */
    public final boolean needAutoSaveTitle() {
        return isEditable() && !isPasswordVariant(getEditInputType());
    }

    /* access modifiers changed from: package-private */
    public final boolean needAutoSaveDescription() {
        return isDescriptionEditable() && !isPasswordVariant(getDescriptionEditInputType());
    }
}
