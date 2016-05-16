package com.github.toolbelt.web.services.statistics.trace;

import com.springsource.insight.intercept.trace.Frame;

/**
 * Represents a handler of a {@code Trace} recorded by {@code Spring Insight}.
 *
 * <p>Notice: Adapted version of {@link com.springsource.insight.intercept.InterceptDispatcher}.</p>
 *
 * @author <a href="mailto:y.skopets@gmail.com">Yaroslav Skopets</a>
 */
public interface TraceCapture {

    /**
     * Handles a {@code Trace} recorded by {@code Spring Insight}.
     *
     * @param rootFrame the {@code root Frame} of a {@code Trace} recorded by {@code Spring Insight}
     */
    void capture(Frame rootFrame);
}
