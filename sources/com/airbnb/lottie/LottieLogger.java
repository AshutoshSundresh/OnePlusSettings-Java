package com.airbnb.lottie;

public interface LottieLogger {
    void debug(String str);

    void warning(String str);

    void warning(String str, Throwable th);
}
