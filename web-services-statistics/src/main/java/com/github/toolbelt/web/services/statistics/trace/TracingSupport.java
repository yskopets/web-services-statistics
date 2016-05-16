package com.github.toolbelt.web.services.statistics.trace;

import com.springsource.insight.intercept.operation.Operation;
import com.springsource.insight.intercept.trace.Frame;
import com.springsource.insight.util.StringFormatterUtils;

import static com.springsource.insight.intercept.operation.OperationFields.EXCEPTION;
import static com.springsource.insight.intercept.operation.OperationFields.RETURN_VALUE;

/**
 * Provides a high-level interface for {@code tracing} method calls.
 *
 * <p>Hides away original {@link com.springsource.insight.intercept.trace.FrameBuilder FrameBuilder} abstraction
 * from the rest of the source code.
 *
 * @author <a href="mailto:y.skopets@gmail.com">Yaroslav Skopets</a>
 */
public class TracingSupport {

    /**
     * Notice: the field has been intentionally left mutable - subclasses of {@link TracingSupport}
     * should have an option of a default constructor as long as they guarantee proper initialization later on.
     */
    private TracingSubsystem tracingSubsystem;

    public TracingSupport(TracingSubsystem tracingSubsystem) {
        this.tracingSubsystem = tracingSubsystem;
    }

    protected TracingSupport() {
    }

    public void setTracingSubsystem(TracingSubsystem tracingSubsystem) {
        this.tracingSubsystem = tracingSubsystem;
    }

    public void enter(final Operation operation) {
        tracingSubsystem.getFrameBuilder().enter(operation);
    }

    public Frame exitAbnormal(Throwable throwable) {
        final Operation op = workingOperation();
        if (op != null) {
            op.put(EXCEPTION, StringFormatterUtils.formatStackTrace(throwable));
        }
        return exit();
    }

    public Frame exitNormal(Object returnValue) {
        final Operation op = workingOperation();
        if (op != null) {
            op.put(RETURN_VALUE, StringFormatterUtils.formatObject(returnValue));
        }
        return exit();
    }

    public Frame exitNormal() {
        final Operation op = workingOperation();
        if (op != null) {
            op.put(RETURN_VALUE, StringFormatterUtils.NO_VALUE_STRING);
        }
        return exit();
    }

    protected Operation workingOperation() {
        return tracingSubsystem.getFrameBuilder().peek();
    }

    protected Frame exit() {
        return tracingSubsystem.getFrameBuilder().exit();
    }
}
