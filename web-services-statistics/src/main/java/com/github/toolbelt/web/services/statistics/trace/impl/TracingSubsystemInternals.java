package com.github.toolbelt.web.services.statistics.trace.impl;

import com.github.toolbelt.web.services.statistics.trace.TracingSubsystem;
import com.springsource.insight.intercept.trace.Frame;

/**
 * Extended interface of the {@code Trace Recording Subsystem} that is NOT a part of public API.
 *
 * @author <a href="mailto:y.skopets@gmail.com">Yaroslav Skopets</a>
 */
public interface TracingSubsystemInternals extends TracingSubsystem {

    /**
     * Dispatches the {@code root Frame} of a {@code Trace} recorded by {@code Spring Insight}
     * to the respective client of the {@code Trace Recording Subsystem}.
     *
     * @param rootFrame the {@code root Frame} of a {@code Trace} recorded by {@code Spring Insight}
     */
    void dispatchTrace(Frame rootFrame);
}
