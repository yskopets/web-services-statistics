package com.github.toolbelt.web.services.statistics.filter;

import com.github.toolbelt.web.services.statistics.support.io.DelegatingOutputStream;
import com.github.toolbelt.web.services.statistics.timing.Clock;

import java.io.IOException;
import java.io.OutputStream;

/**
 * {@link OutputStream} {@code Decorator} that collects generic response {@code metrics}, such as
 * {@code Time Till First Response Byte In Millis} and {@code Response Body Written Bytes Count}.
 *
 * @author <a href="mailto:y.skopets@gmail.com">Yaroslav Skopets</a>
 */
public class StatisticsCollectingOutputStream extends DelegatingOutputStream {

    private final Clock clock;

    // Apparently, we have response sizes greater than 2 GB. For such cases {@code int} range isn't enough
    private long writtenBytesCount = 0;

    private Long timeFirstByteWrittenMillis;

    public StatisticsCollectingOutputStream(OutputStream actualOutputStream, Clock clock) {
        super(actualOutputStream);
        this.clock = clock;
    }

    @Override
    public void write(int b) throws IOException {
        super.write(b);
        writtenBytesCount += 1;
        markTime();
    }

    @Override
    public void write(byte[] b) throws IOException {
        super.write(b);
        writtenBytesCount += b.length;
        markTime();
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        super.write(b, off, len);
        writtenBytesCount += len;
        markTime();
    }

    private void markTime() {
        if (timeFirstByteWrittenMillis == null) {
            timeFirstByteWrittenMillis = clock.currentTimeMillis();
        }
    }

    public long getWrittenBytesCount() {
        return writtenBytesCount;
    }

    public Long getTimeFirstByteWrittenMillis() {
        return timeFirstByteWrittenMillis;
    }
}
