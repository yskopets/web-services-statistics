package com.github.toolbelt.web.services.statistics.trace

import com.github.toolbelt.web.services.statistics.test.given.PredictableTimingCapable
import com.github.toolbelt.web.services.statistics.test.given.SampleOperationsCapable
import com.github.toolbelt.web.services.statistics.test.given.StatisticsSerializationCapable
import com.github.toolbelt.web.services.statistics.test.util.environment.PredictableLineSeparatorCapable
import com.github.toolbelt.web.services.statistics.trace.impl.SimpleTracingSubsystem
import spock.lang.Specification
import spock.lang.Subject

import static com.github.toolbelt.web.services.statistics.test.util.throwable.PredictableThrowablesCapable.withoutStackTrace

/**
 * Design spec for {@link TracingSupport}.
 *
 * @author <a href="mailto:y.skopets@gmail.com">Yaroslav Skopets</a>
 */
class TracingSupportSpec extends Specification implements PredictableTimingCapable, SampleOperationsCapable,
        StatisticsSerializationCapable, PredictableLineSeparatorCapable {

    @Subject
    def tracingSupport = new TracingSupport(new SimpleTracingSubsystem({ -> predictableStopWatch}))

    def "should fill in `void` return value when #exitNormal() is called"() {
        when:
        tracingSupport.enter(webRequestOperation)
        and:
        def frame = tracingSupport.exitNormal()
        then:
        pretty(jsonOf(frame.operation)) == pretty('''
            {
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
            }
            ''')
    }

    def "should fill in actual return value when #exitNormal(Object returnValue) is called"() {
        when:
        tracingSupport.enter(webRequestOperation)
        and:
        def frame = tracingSupport.exitNormal('actual return value')
        then:
        pretty(jsonOf(frame.operation)) == pretty('''
            {
              "type": "web_request",
              "label": "restful-web-service-endpoint",
              "properties": {
                "arguments": {
                  "properties": {
                    "client_id": "spock",
                    "api_version": 2
                  }
                },
                "returnValue": "actual return value"
              }
            }
            ''')
    }

    def "should fill in thrown exception when #exitAbnormal(Throwable throwable) is called"() {
        when:
        tracingSupport.enter(webRequestOperation)
        and:
        def frame = tracingSupport.exitAbnormal(withoutStackTrace(new IllegalStateException('Request processing failed')))
        then:
        pretty(jsonOf(frame.operation)) == pretty('''
            {
              "type": "web_request",
              "label": "restful-web-service-endpoint",
              "properties": {
                "arguments": {
                  "properties": {
                    "client_id": "spock",
                    "api_version": 2
                  }
                },
                "exception": "java.lang.IllegalStateException: Request processing failed\\r\\n"
              }
            }
            ''')
    }

    def "should create a new `Frame` on every single call of #enter(Operation operation)"() {
        when:
        tracingSupport.enter(webRequestOperation)
        and:
        tracingSupport.enter(methodCallOperation)
        and:
        tracingSupport.exitNormal('actual return value')
        and:
        def rootFrame = tracingSupport.exitNormal()

        then:
        pretty(jsonOf(rootFrame)) == pretty('''
            {
              "id": 0,
              "children": [
                {
                  "id": 1,
                  "children": [
                  ],
                  "operation": {
                    "type": "method",
                    "label": "controller",
                    "properties": {
                      "arguments": {
                        "items": [
                          "1234567890123",
                          17
                        ]
                      },
                      "returnValue": "actual return value"
                    }
                  },
                  "range": {
                    "start": 20000001,
                    "end": 30000001,
                    "durationMillis": 10
                  }
                }
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
                "end": 40000001,
                "durationMillis": 30
              }
            }
            ''')
    }

    def "should gracefully handle a case where #exitNormal() is called while there is no active operation on the stack"() {
        when:
        def frame = tracingSupport.exitNormal()
        then:
        frame == null
        and:
        noExceptionThrown()
    }

    def "should gracefully handle a case where #exitNormal(Object returnValue) is called while there is no active operation on the stack"() {
        when:
        def frame = tracingSupport.exitNormal('any value')
        then:
        frame == null
        and:
        noExceptionThrown()
    }

    def "should gracefully handle a case where #exitAbnormal(Throwable throwable) is called while there is no active operation on the stack"() {
        when:
        def frame = tracingSupport.exitAbnormal(new IllegalStateException())
        then:
        frame == null
        and:
        noExceptionThrown()
    }
}
