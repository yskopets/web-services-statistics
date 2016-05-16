package com.github.toolbelt.web.services.statistics.trace.impl;

import com.springsource.insight.intercept.endpoint.EndPointPopulator;
import com.springsource.insight.intercept.operation.Operation;
import com.springsource.insight.intercept.trace.FrameBuilder;
import com.springsource.insight.intercept.trace.FrameBuilderCallback;
import com.springsource.insight.intercept.trace.SimpleFrameBuilder;
import com.springsource.insight.util.time.StopWatchFactory;

/**
 * Extends original {@link com.springsource.insight.intercept.trace.ThreadLocalFrameBuilder ThreadLocalFrameBuilder}
 * with a configurable {@link StopWatchFactory}.
 *
 * @author <a href="mailto:y.skopets@gmail.com">Yaroslav Skopets</a>
 */
public class ThreadLocalFrameBuilder extends com.springsource.insight.intercept.trace.ThreadLocalFrameBuilder {

    private final StopWatchFactory stopWatchFactory;

    private final FrameBuilderCallback callbacks;

    public ThreadLocalFrameBuilder(StopWatchFactory stopWatchFactory, FrameBuilderCallback callback) {
        super(callback);
        this.stopWatchFactory = stopWatchFactory;
        this.callbacks = callback;
    }

    /**
     * Overrides {@link com.springsource.insight.intercept.trace.ThreadLocalFrameBuilder#enter(Operation)} method
     * only to redefine {@link com.springsource.insight.intercept.trace.ThreadLocalFrameBuilder#createMyBuilder()}.
     *
     * @param operation Operation associated with the frame we are entering.
     */
    @Override
    public void enter(Operation operation) {
        SimpleFrameBuilder myBuilder = getMyThreadBuilder();
        if (myBuilder == null) {
            myBuilder = createMyBuilder();
        }
        myBuilder.enter(operation);
    }

    /**
     * Overrides {@link com.springsource.insight.intercept.trace.ThreadLocalFrameBuilder#setHintIfRoot(String, Object)}
     * method only to redefine {@link com.springsource.insight.intercept.trace.ThreadLocalFrameBuilder#createMyBuilder()}.
     *
     * @param hint hint
     * @param value value
     */
    @Override
    public void setHintIfRoot(String hint, Object value) {
        FrameBuilder myBuilder = getMyThreadBuilder();
        if (myBuilder == null) {
            if (hint.equals(EndPointPopulator.HINT_NAME)) {
                myBuilder = createMyBuilder();
            } else {
                return;
            }
        }
        myBuilder.setHintIfRoot(hint, value);
    }

    /**
     * Redefines {@link com.springsource.insight.intercept.trace.ThreadLocalFrameBuilder#createMyBuilder()}
     * to refactor out creation of a {@link SimpleFrameBuilder} instance into a {@code Factory Method}.
     *
     * @return {@link SimpleFrameBuilder} instance
     */
    protected SimpleFrameBuilder createMyBuilder() {
        final SimpleFrameBuilder myBuilder = newSimpleFrameBuilder();
        setMyThreadBuilder(myBuilder);
        return myBuilder;
    }

    /**
     * {@code Factory Method} for {@link SimpleFrameBuilder} instances.
     *
     * @return {@link SimpleFrameBuilder} instance
     */
    protected SimpleFrameBuilder newSimpleFrameBuilder() {
        return new SimpleFrameBuilder(stopWatchFactory, callbacks);
    }
}
