package com.github.toolbelt.web.services.statistics.trace.impl

import com.springsource.insight.intercept.trace.Frame
import spock.lang.Specification
import spock.lang.Subject

import static com.springsource.insight.intercept.trace.FrameBuilderCallback.FrameBuilderEvent.ROOT_EXIT

/**
 * Design spec for {@link TraceDispatcherCallback}.
 *
 * @author <a href="mailto:y.skopets@gmail.com">Yaroslav Skopets</a>
 */
class TraceDispatcherCallbackSpec extends Specification {

    def tracingSubsystem = Mock(TracingSubsystemInternals)

    @Subject
    def traceDispatcher = new TraceDispatcherCallback(tracingSubsystem)

    def "should listen to a `ROOT_EXIT` event emitted by `Spring Insight`"() {
        expect:
        traceDispatcher.listensTo() == [ROOT_EXIT]
    }

    def "should forward the root Frame of a Trace recorded by `Spring Insight` to `TracingSubsystem`"() {
        given:
        def rootFrame = Stub(Frame)

        when:
        traceDispatcher.exitRootFrame(rootFrame, ['hints':'will be completely ignored anyway'])
        then:
        1 * tracingSubsystem.dispatchTrace(rootFrame)
        0 * _
    }
}
