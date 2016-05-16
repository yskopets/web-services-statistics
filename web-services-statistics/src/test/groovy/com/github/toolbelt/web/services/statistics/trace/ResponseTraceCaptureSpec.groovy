package com.github.toolbelt.web.services.statistics.trace

import com.springsource.insight.intercept.trace.Frame
import spock.lang.Specification
import spock.lang.Subject

/**
 * Design spec for {@link ResponseTraceCapture}.
 *
 * @author <a href="mailto:y.skopets@gmail.com">Yaroslav Skopets</a>
 */
class ResponseTraceCaptureSpec extends Specification {

    @Subject
    def capture = new ResponseTraceCapture()

    def "should stay empty until `Spring Insight` emits the root Frame of the recorded Trace"() {
        expect:
        !capture.captured
        capture.responseTrace == null
    }

    def "should capture the root Frame of a Trace recorded by `Spring Insight`"() {
        given:
        def rootFrame = Stub(Frame)

        when:
        capture.capture(rootFrame)
        then:
        capture.captured
        capture.responseTrace
        capture.responseTrace.rootFrame.is(rootFrame)
    }
}
