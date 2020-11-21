package okhttp3;

import java.io.IOException;

public enum Protocol {
    HTTP_1_0("http/1.0"),
    HTTP_1_1("http/1.1"),
    SPDY_3("spdy/3.1"),
    HTTP_2("h2");
    
    private final String protocol;

    private Protocol(String str) {
        this.protocol = str;
    }

    public static Protocol get(String str) throws IOException {
        Protocol protocol2 = SPDY_3;
        Protocol protocol3 = HTTP_2;
        Protocol protocol4 = HTTP_1_1;
        Protocol protocol5 = HTTP_1_0;
        if (str.equals(protocol5.protocol)) {
            return protocol5;
        }
        if (str.equals(protocol4.protocol)) {
            return protocol4;
        }
        if (str.equals(protocol3.protocol)) {
            return protocol3;
        }
        if (str.equals(protocol2.protocol)) {
            return protocol2;
        }
        throw new IOException("Unexpected protocol: " + str);
    }

    public String toString() {
        return this.protocol;
    }
}
