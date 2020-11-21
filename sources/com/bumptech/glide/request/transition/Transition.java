package com.bumptech.glide.request.transition;

public interface Transition<R> {

    public interface ViewAdapter {
    }

    boolean transition(R r, ViewAdapter viewAdapter);
}
