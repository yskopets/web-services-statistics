package com.github.toolbelt.web.services.statistics.filter;

import com.github.toolbelt.web.services.statistics.support.io.DelegatingWriter;
import com.github.toolbelt.web.services.statistics.timing.Clock;

import java.io.IOException;
import java.io.Writer;

/**
 * {@link Writer} {@code Decorator} that collects generic response {@code metrics}, such as
 * {@code Time Till First Response Byte In Millis} and {@code Response Body Written Chars Count}.
 *
 * @author <a href="mailto:y.skopets@gmail.com">Yaroslav Skopets</a>
 */
public class StatisticsCollectingWriter extends DelegatingWriter {

    private final Clock clock;

    // Apparently, we have response sizes greater than 2 GB. For such cases {@code int} range isn't enough
    private long writtenCharsCount = 0;

    private Long timeFirstCharWrittenMillis;

    public StatisticsCollectingWriter(Writer actualWriter, Clock clock) {
        super(actualWriter, false);
        this.clock = clock;
    }

    @Override
    public void write(int c) throws IOException {
        super.write(c);
        writtenCharsCount += 1;
        markTime();
    }

    @Override
    public void write(char[] buf) throws IOException {
        super.write(buf);
        writtenCharsCount += buf.length;
        markTime();
    }

    @Override
    public void write(char[] buf, int off, int len) throws IOException {
        super.write(buf, off, len);
        writtenCharsCount += len;
        markTime();
    }

    @Override
    public void write(String s) throws IOException {
        super.write(s);
        writtenCharsCount += s.length();
        markTime();
    }

    @Override
    public void write(String s, int off, int len) throws IOException {
        super.write(s, off, len);
        writtenCharsCount += len;
        markTime();
    }

    private void markTime() {
        if (timeFirstCharWrittenMillis == null) {
            timeFirstCharWrittenMillis = clock.currentTimeMillis();
        }
    }

    public long getWrittenCharsCount() {
        return writtenCharsCount;
    }

    public Long getTimeFirstCharWrittenMillis() {
        return timeFirstCharWrittenMillis;
    }
}
