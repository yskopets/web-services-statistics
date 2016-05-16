package com.github.toolbelt.web.services.statistics.support.io;

import org.apache.commons.io.output.NullWriter;

import java.io.PrintWriter;
import java.util.Locale;

/**
 * Convenient base class for {@link PrintWriter} {@code Decorator}s.
 *
 * @author <a href="mailto:y.skopets@gmail.com">Yaroslav Skopets</a>
 */
public class DelegatingPrintWriter extends PrintWriter {

    private final PrintWriter delegate;

    public DelegatingPrintWriter(PrintWriter delegate) {
        super(new NullWriter());
        this.delegate = delegate;
    }

    // > override methods of java.io.Writer

    @Override
    public void write(int c) {
        delegate.write(c);
    }

    @Override
    public void write(char[] buf) {
        delegate.write(buf);
    }

    @Override
    public void write(char[] buf, int off, int len) {
        delegate.write(buf, off, len);
    }

    @Override
    public void write(String s) {
        delegate.write(s);
    }

    @Override
    public void write(String s, int off, int len) {
        delegate.write(s, off, len);
    }

    @Override
    public PrintWriter append(CharSequence csq) {
        delegate.append(csq);
        return this;
    }

    @Override
    public PrintWriter append(CharSequence csq, int start, int end) {
        delegate.append(csq, start, end);
        return this;
    }

    @Override
    public PrintWriter append(char c) {
        delegate.append(c);
        return this;
    }

    @Override
    public void flush() {
        delegate.flush();
    }

    @Override
    public void close() {
        delegate.close();
    }

    // < override methods of java.io.Writer

    // > override methods of java.io.PrintWriter

    @Override
    public boolean checkError() {
        return delegate.checkError();
    }

    @Override
    public void print(boolean b) {
        delegate.print(b);
    }

    @Override
    public void print(char c) {
        delegate.print(c);
    }

    @Override
    public void print(int i) {
        delegate.print(i);
    }

    @Override
    public void print(long l) {
        delegate.print(l);
    }

    @Override
    public void print(float f) {
        delegate.print(f);
    }

    @Override
    public void print(double d) {
        delegate.print(d);
    }

    @Override
    public void print(char[] s) {
        delegate.print(s);
    }

    @Override
    public void print(String s) {
        delegate.print(s);
    }

    @Override
    public void print(Object obj) {
        delegate.print(obj);
    }

    @Override
    public void println() {
        delegate.println();
    }

    @Override
    public void println(boolean x) {
        delegate.println(x);
    }

    @Override
    public void println(char x) {
        delegate.println(x);
    }

    @Override
    public void println(int x) {
        delegate.println(x);
    }

    @Override
    public void println(long x) {
        delegate.println(x);
    }

    @Override
    public void println(float x) {
        delegate.println(x);
    }

    @Override
    public void println(double x) {
        delegate.println(x);
    }

    @Override
    public void println(char[] x) {
        delegate.println(x);
    }

    @Override
    public void println(String x) {
        delegate.println(x);
    }

    @Override
    public void println(Object x) {
        delegate.println(x);
    }

    @Override
    public PrintWriter printf(String format, Object... args) {
        delegate.printf(format, args);
        return this;
    }

    @Override
    public PrintWriter printf(Locale l, String format, Object... args) {
        delegate.printf(l, format, args);
        return this;
    }

    @Override
    public PrintWriter format(String format, Object... args) {
        delegate.format(format, args);
        return this;
    }

    @Override
    public PrintWriter format(Locale l, String format, Object... args) {
        delegate.format(l, format, args);
        return this;
    }

    // < override methods of java.io.PrintWriter
}
