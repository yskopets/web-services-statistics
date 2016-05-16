package com.github.toolbelt.web.services.statistics.trace;

import com.springsource.insight.intercept.trace.Frame;

/**
 * Represents a {@code Trace} recorded by the {@code Trace Recording Subsystem}.
 *
 * <p>Notice: Adapted version of {@link com.springsource.insight.intercept.trace.Trace}.</p>
 *
 * @author <a href="mailto:y.skopets@gmail.com">Yaroslav Skopets</a>
 */
public class ResponseTrace {

    private final Frame rootFrame;

    public ResponseTrace(Frame rootFrame) {
        this.rootFrame = rootFrame;
    }

    public Frame getRootFrame() {
        return rootFrame;
    }
}
