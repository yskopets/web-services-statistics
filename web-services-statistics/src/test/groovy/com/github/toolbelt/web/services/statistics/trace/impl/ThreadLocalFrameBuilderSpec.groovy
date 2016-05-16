package com.github.toolbelt.web.services.statistics.trace.impl

import com.github.toolbelt.web.services.statistics.test.given.PredictableTimingCapable
import com.github.toolbelt.web.services.statistics.test.given.SampleOperationsCapable
import com.springsource.insight.intercept.trace.FrameBuilderCallback
import com.springsource.insight.util.time.TimeRange
import spock.lang.Specification
import spock.lang.Subject

import static com.springsource.insight.intercept.endpoint.EndPointPopulator.HINT_NAME

/**
 * Design spec for {@link ThreadLocalFrameBuilder}.
 *
 * @author <a href="mailto:y.skopets@gmail.com">Yaroslav Skopets</a>
 */
class ThreadLocalFrameBuilderSpec extends Specification implements PredictableTimingCapable, SampleOperationsCapable {

    @Subject
    def frameBuilder = new ThreadLocalFrameBuilder({ -> predictableStopWatch}, Stub(FrameBuilderCallback))

    def "should use passed-in `StopWatchFactory` instance in #enter(Operation operation) method"() {
        when: "force creation of an internal `SimpleFrameBuilder` configured with the passed-in `StopWatchFactory` instance"
        frameBuilder.enter(webRequestOperation)
        and:
        def rootFrame = frameBuilder.exit()

        then:
        rootFrame.operation.is(webRequestOperation)
        rootFrame.range == TimeRange.nanoTimeRange(10000001L, 20000001L)
    }

    def "should use passed-in `StopWatchFactory` instance in #setHintIfRoot(String hint, Object value) method"() {
        when: "force creation of an internal `SimpleFrameBuilder` configured with the passed-in `StopWatchFactory` instance"
        frameBuilder.setHintIfRoot(HINT_NAME, 'value')
        and: "cause re-use of a `SimpleFrameBuilder` from the step above"
        frameBuilder.enter(webRequestOperation)
        and:
        def rootFrame = frameBuilder.exit()

        then:
        rootFrame.operation.is(webRequestOperation)
        rootFrame.range == TimeRange.nanoTimeRange(10000001L, 20000001L)
    }
}
