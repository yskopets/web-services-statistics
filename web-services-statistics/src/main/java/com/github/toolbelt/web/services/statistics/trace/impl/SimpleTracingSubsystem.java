package com.github.toolbelt.web.services.statistics.trace.impl;

import com.github.toolbelt.web.services.statistics.trace.TraceCapture;
import com.github.toolbelt.web.services.statistics.trace.TracingSubsystem;
import com.google.common.annotations.VisibleForTesting;
import com.springsource.insight.intercept.DelegatingFrameBuilderCallbacks;
import com.springsource.insight.intercept.trace.Frame;
import com.springsource.insight.intercept.trace.FrameBuilder;
import com.springsource.insight.util.time.NanoStopWatch.NanoStopWatchFactory;
import com.springsource.insight.util.time.StopWatchFactory;

/**
 * Default {@code ThreadLocal}-based implementation of {@link TracingSubsystem}.
 *
 * <p>Notice: we're using {@code ThreadLocal}s in order to have a single {@link TracingSubsystem} instance
 * shared by all {@code Thread}s.</p>
 *
 * @author <a href="mailto:y.skopets@gmail.com">Yaroslav Skopets</a>
 */
public class SimpleTracingSubsystem implements TracingSubsystemInternals {

    private static final StopWatchFactory DEFAULT_STOP_WATCH_FACTORY = new NanoStopWatchFactory();

    private final FrameBuilder threadBuilder;

    private final ThreadLocal<TraceCapture> traceCapture = new ThreadLocal<TraceCapture>();

    public SimpleTracingSubsystem() {
        this(DEFAULT_STOP_WATCH_FACTORY);
    }

    @VisibleForTesting
    public SimpleTracingSubsystem(StopWatchFactory stopWatchFactory) {
        this.threadBuilder = new ThreadLocalFrameBuilder(stopWatchFactory,
                new DelegatingFrameBuilderCallbacks(new TraceDispatcherCallback(this)));
    }

    @Override
    public void registerTraceCapture(final TraceCapture traceCapture) {
        this.traceCapture.set(traceCapture);
    }

    @Override
    public void resetTraceCapture() {
        this.traceCapture.set(null);
    }

    @Override
    public FrameBuilder getFrameBuilder() {
        return threadBuilder;
    }

    @Override
    public void dispatchTrace(Frame rootFrame) {
        final TraceCapture traceCapture = this.traceCapture.get();
        if (traceCapture != null) {
            traceCapture.capture(rootFrame);
        }
    }
}
