package cm.android.log;

import java.io.IOException;
import java.io.OutputStream;

public class MeasureOutputStream extends OutputStream {

    OutputStream wrapped;

    long length;

    public MeasureOutputStream(OutputStream stream, long currentLength) {
        wrapped = stream;
        length = currentLength;
    }

    public MeasureOutputStream(OutputStream stream) {
        this(stream, 0);
    }

    @Override
    public void write(int oneByte) throws IOException {
        wrapped.write(oneByte);
        length++;
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        wrapped.write(b, off, len);
        length += len;
    }

    @Override
    public void close() throws IOException {
        wrapped.close();
    }

    @Override
    public void flush() throws IOException {
        wrapped.flush();
    }

    public long getLength() {
        return length;
    }

    public void setLength(long newLength) {
        length = newLength;
    }
}
