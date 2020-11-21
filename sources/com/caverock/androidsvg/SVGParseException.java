package com.caverock.androidsvg;

public class SVGParseException extends Exception {
    public SVGParseException(String str) {
        super(str);
    }

    public SVGParseException(String str, Throwable th) {
        super(str, th);
    }
}
