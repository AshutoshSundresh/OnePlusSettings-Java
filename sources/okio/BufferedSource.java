package okio;

import java.io.IOException;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;

public interface BufferedSource extends Source, ReadableByteChannel {
    @Deprecated
    Buffer buffer();

    boolean exhausted() throws IOException;

    Buffer getBuffer();

    long indexOf(byte b) throws IOException;

    long indexOf(ByteString byteString) throws IOException;

    long indexOfElement(ByteString byteString) throws IOException;

    boolean rangeEquals(long j, ByteString byteString) throws IOException;

    byte readByte() throws IOException;

    byte[] readByteArray(long j) throws IOException;

    ByteString readByteString(long j) throws IOException;

    void readFully(byte[] bArr) throws IOException;

    long readHexadecimalUnsignedLong() throws IOException;

    int readInt() throws IOException;

    int readIntLe() throws IOException;

    short readShort() throws IOException;

    short readShortLe() throws IOException;

    String readString(Charset charset) throws IOException;

    String readUtf8LineStrict() throws IOException;

    boolean request(long j) throws IOException;

    void require(long j) throws IOException;

    int select(Options options) throws IOException;

    void skip(long j) throws IOException;
}
