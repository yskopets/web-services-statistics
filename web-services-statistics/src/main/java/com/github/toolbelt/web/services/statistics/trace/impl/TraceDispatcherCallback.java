package com.github.toolbelt.web.services.statistics.trace.impl;

import com.springsource.insight.intercept.trace.Frame;
import com.springsource.insight.intercept.trace.NullFrameBuilderCallback;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Represents a handler of events emitted by {@code Spring Insight}.
 *
 * <p>Adapted version of {@link com.springsource.insight.intercept.TraceDispatchCallback}.</p>
 *
 * @author <a href="mailto:y.skopets@gmail.com">Yaroslav Skopets</a>
 */
public class TraceDispatcherCallback extends NullFrameBuilderCallback {

    private static final List<FrameBuilderEvent> LIST_TO_EVENTS = Collections.singletonList(FrameBuilderEvent.ROOT_EXIT);

    private final TracingSubsystemInternals tracingSubsystem;

    public TraceDispatcherCallback(TracingSubsystemInternals tracingSubsystem) {
        this.tracingSubsystem = tracingSubsystem;
    }

    @Override
    public List<FrameBuilderEvent> listensTo() {
        return LIST_TO_EVENTS;
    }

    @Override
    public void exitRootFrame(Frame rootFrame, Map<String, Object> hints) {
        tracingSubsystem.dispatchTrace(rootFrame);
    }
}
