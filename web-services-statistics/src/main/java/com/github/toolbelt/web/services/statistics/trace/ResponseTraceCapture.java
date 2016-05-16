package com.github.toolbelt.web.services.statistics.trace;

import com.springsource.insight.intercept.trace.Frame;

/**
 * Default implementation of {@link TraceCapture} that simply keeps a reference to
 * the {@code root Frame} of a {@code Trace} recorded by {@code Spring Insight}.
 *
 * <p><b>Notice:</b> This class does NOT post-process or persist a recorded {@code Trace},
 * it only holds a reference for future use.</p>
 *
 * @author <a href="mailto:y.skopets@gmail.com">Yaroslav Skopets</a>
 */
public class ResponseTraceCapture implements TraceCapture {

    private ResponseTrace responseTrace;

    public boolean isCaptured() {
        return responseTrace != null;
    }

    public ResponseTrace getResponseTrace() {
        return responseTrace;
    }

    @Override
    public void capture(Frame rootFrame) {
        this.responseTrace = new ResponseTrace(rootFrame);
    }
}
