package com.github.toolbelt.web.services.statistics.trace;

import com.springsource.insight.intercept.trace.FrameBuilder;

/**
 * {@code Facade} for interacting with the {@code Trace Recording Subsystem}.
 *
 * <p>Adapted version of {@link com.springsource.insight.intercept.InterceptConfiguration}.</p>
 *
 * @author <a href="mailto:y.skopets@gmail.com">Yaroslav Skopets</a>
 */
public interface TracingSubsystem {

    /**
     * Allows a client of the {@code Trace Recording Subsystem} to subscribe for a recorded {@code Trace}.
     *
     * @param traceCapture a handler of recorded {@code Trace}s
     */
    void registerTraceCapture(TraceCapture traceCapture);

    /**
     * Allows a client of the {@code Trace Recording Subsystem} to cancel its subscription for a recorded {@code Trace}.
     *
     * <p>Notice: thread-local behaviour is implied here</p>
     */
    void resetTraceCapture();

    /**
     * Allows a client of the {@code Trace Recording Subsystem} to get a hold of {@link FrameBuilder} machinery
     * to drive the process of {@code Trace} construction.
     *
     * <p>Notice: It is expected that a client will normally wrap a low-level {@link FrameBuilder}
     * with a convenient {@link TracingSupport} adapter.</p>
     *
     * <p>Notice: thread-local behaviour is implied here</p>
     *
     * @return {@link FrameBuilder} machinery of {@code Spring Insight}
     *
     * @see TracingSupport
     */
    FrameBuilder getFrameBuilder();
}
