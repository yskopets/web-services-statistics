package com.github.toolbelt.web.services.statistics.filter;

import com.github.toolbelt.web.services.statistics.support.io.BridgingServletOutputStream;
import com.github.toolbelt.web.services.statistics.timing.Clock;

import javax.servlet.ServletOutputStream;

/**
 * {@code Decorator} of {@link javax.servlet.ServletResponse#getOutputStream()}
 * that collects generic response {@code metrics}, such as
 * {@code Time Till First Response Byte In Millis} and {@code Response Body Written Bytes Count}.
 *
 * <p>Notice: this class does NOT {@code Decorate} original {@link ServletOutputStream} but rather
 * {@code Bridges} another {@link ServletOutputStream} into the {@link java.io.OutputStream} part of the original.
 * What it means is that {@code print(*)} and {@code println(*)} methods of the original {@link ServletOutputStream}
 * will NEVER be called. Fortunately, all {@link ServletOutputStream} subclasses we've met so far
 * do NOT override those methods.</p>
 *
 * @author <a href="mailto:y.skopets@gmail.com">Yaroslav Skopets</a>
 */
public class StatisticsCollectingServletOutputStream extends BridgingServletOutputStream {

    public StatisticsCollectingServletOutputStream(ServletOutputStream actualOutputStream, Clock clock) {
        super(new StatisticsCollectingOutputStream(actualOutputStream, clock));
    }

    public long getWrittenBytesCount() {
        return ((StatisticsCollectingOutputStream) getOutputStream()).getWrittenBytesCount();
    }

    public Long getTimeFirstByteWrittenMillis() {
        return ((StatisticsCollectingOutputStream) getOutputStream()).getTimeFirstByteWrittenMillis();
    }
}
