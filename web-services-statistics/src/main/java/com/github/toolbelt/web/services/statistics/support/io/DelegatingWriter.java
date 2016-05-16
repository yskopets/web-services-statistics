package com.github.toolbelt.web.services.statistics.support.io;

import java.io.IOException;
import java.io.Writer;

/**
 * Convenient base class for {@link Writer} {@code Decorator}s.
 *
 * @author <a href="mailto:y.skopets@gmail.com">Yaroslav Skopets</a>
 */
public class DelegatingWriter extends Writer {

    private final Writer delegate;

    /**
     * In some cases it might seem more natural not to delegate methods of {@link java.lang.Appendable}.
     */
    private final boolean delegateMethodsOfAppendable;

    public DelegatingWriter(Writer delegate) {
        this(delegate, true);
    }

    public DelegatingWriter(Writer delegate, boolean delegateMethodsOfAppendable) {
        this.delegate = delegate;
        this.delegateMethodsOfAppendable = delegateMethodsOfAppendable;
    }

    @Override
    public void write(int c) throws IOException {
        delegate.write(c);
    }

    @Override
    public void write(char[] buf) throws IOException {
        delegate.write(buf);
    }

    @Override
    public void write(char[] buf, int off, int len) throws IOException {
        delegate.write(buf, off, len);
    }

    @Override
    public void write(String s) throws IOException {
        delegate.write(s);
    }

    @Override
    public void write(String s, int off, int len) throws IOException {
        delegate.write(s, off, len);
    }

    @Override
    public Writer append(CharSequence csq) throws IOException {
        if (delegateMethodsOfAppendable) {
            delegate.append(csq);
        } else {
            super.append(csq);
        }
        return this;
    }

    @Override
    public Writer append(CharSequence csq, int start, int end) throws IOException {
        if (delegateMethodsOfAppendable) {
            delegate.append(csq, start, end);
        } else {
            super.append(csq, start, end);
        }
        return this;
    }

    @Override
    public Writer append(char c) throws IOException {
        if (delegateMethodsOfAppendable) {
            delegate.append(c);
        } else {
            super.append(c);
        }
        return this;
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
