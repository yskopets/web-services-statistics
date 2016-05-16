package com.github.toolbelt.web.services.statistics.support.io;

import javax.servlet.ServletOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Convenient base class to change the relationship type between {@link ServletOutputStream} and {@link OutputStream}
 * classes from <em>inheritance</em> to <em>association</em>.
 *
 * @author <a href="mailto:y.skopets@gmail.com">Yaroslav Skopets</a>
 */
public class BridgingServletOutputStream extends ServletOutputStream {

    private final OutputStream outputStream;

    public BridgingServletOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    @Override
    public void write(int b) throws IOException {
        outputStream.write(b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        outputStream.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        outputStream.write(b, off, len);
    }

    @Override
    public void flush() throws IOException {
        outputStream.flush();
    }

    @Override
    public void close() throws IOException {
        outputStream.close();
    }

    protected OutputStream getOutputStream() {
        return outputStream;
    }
}
