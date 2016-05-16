package com.github.toolbelt.web.services.statistics.trace.impl

import com.github.toolbelt.web.services.statistics.test.given.PredictableTimingCapable
import com.github.toolbelt.web.services.statistics.test.given.SampleOperationsCapable
import com.github.toolbelt.web.services.statistics.test.given.StatisticsSerializationCapable
import com.github.toolbelt.web.services.statistics.trace.ResponseTraceCapture
import com.github.toolbelt.web.services.statistics.trace.TracingSupport
import spock.lang.Specification
import spock.lang.Subject

/**
 * Design spec for {@link SimpleTracingSubsystem}.
 *
 * @author <a href="mailto:y.skopets@gmail.com">Yaroslav Skopets</a>
 */
class SimpleTracingSubsystemSpec extends Specification implements PredictableTimingCapable, SampleOperationsCapable,
        StatisticsSerializationCapable {

    @Subject
    def tracingSubsystem = new SimpleTracingSubsystem({ -> predictableStopWatch })

    def tracingSupport = new TracingSupport(tracingSubsystem)

    def "should allow to capture the recorded Trace"() {
        given:
        def traceCapture = new ResponseTraceCapture()

        when:
        tracingSubsystem.registerTraceCapture(traceCapture)
        and:
        tracingSupport.enter(webRequestOperation)
        and: "exiting the root Frame will cause emission of a Trace"
        tracingSupport.exitNormal()

        then:
        traceCapture.captured
        and:
        pretty(jsonOf(traceCapture.responseTrace.rootFrame)) == pretty('''
            {
                "id": 0,
                "children": [

                ],
                "operation": {
                    "type": "web_request",
                    "label": "restful-web-service-endpoint",
                    "properties": {
                        "arguments": {
                            "properties": {
                                "client_id": "spock",
                                "api_version": 2
                            }
                        },
                        "returnValue": "void"
                    }
                },
                "range": {
                    "start": 10000001,
                    "end": 20000001,
                    "durationMillis": 10
                }
            }
            ''')
    }

    def "should allow to cancel subscription for the recorded Trace"() {
        given:
        def traceCapture = new ResponseTraceCapture()

        when:
        tracingSubsystem.registerTraceCapture(traceCapture)
        and:
        tracingSupport.enter(webRequestOperation)
        and: "cancel subscription in the middle of Trace recording"
        tracingSubsystem.resetTraceCapture()
        and: "exiting the root Frame will cause emission of a Trace"
        tracingSupport.exitNormal()

        then:
        !traceCapture.captured
    }
}
