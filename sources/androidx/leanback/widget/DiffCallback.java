package androidx.leanback.widget;

public abstract class DiffCallback<Value> {
    public abstract boolean areContentsTheSame(Value value, Value value2);

    public abstract boolean areItemsTheSame(Value value, Value value2);

    public Object getChangePayload(Value value, Value value2) {
        return null;
    }
}
