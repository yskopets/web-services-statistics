package com.github.toolbelt.web.services.statistics.support.io;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Convenient base class for {@link OutputStream} {@code Decorator}s.
 *
 * @author <a href="mailto:y.skopets@gmail.com">Yaroslav Skopets</a>
 */
public class DelegatingOutputStream extends OutputStream {

    private final OutputStream delegate;

    public DelegatingOutputStream(OutputStream delegate) {
        this.delegate = delegate;
    }

    @Override
    public void write(int b) throws IOException {
        delegate.write(b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        delegate.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        delegate.write(b, off, len);
    }

    @Override
    public void flush() throws IOException {
        delegate.flush();
    }

    @Override
    public void close() throws IOException {
        delegate.close();
    }
}
