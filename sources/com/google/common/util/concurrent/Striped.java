package com.google.common.util.concurrent;

public abstract class Striped<L> {

    static class LargeLazyStriped<L> extends PowerOfTwoStriped<L> {
    }

    private static abstract class PowerOfTwoStriped<L> extends Striped<L> {
    }

    static class SmallLazyStriped<L> extends PowerOfTwoStriped<L> {
    }

    private Striped() {
    }
}
