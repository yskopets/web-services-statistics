package com.github.toolbelt.web.services.statistics.support.json

import com.github.toolbelt.web.services.statistics.test.given.SampleOperationsCapable
import com.github.toolbelt.web.services.statistics.test.util.json.JsonOperationsCapable
import com.springsource.insight.intercept.trace.FrameId
import com.springsource.insight.intercept.trace.SimpleFrame
import com.springsource.insight.util.time.TimeRange
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

/**
 * Design spec for {@link RequestStatisticsMapperFactory}.
 *
 * @author <a href="mailto:y.skopets@gmail.com">Yaroslav Skopets</a>
 */
class RequestStatisticsMapperFactorySpec extends Specification implements SampleOperationsCapable, JsonOperationsCapable {

    @Shared
    def rootFrame = new SimpleFrame(FrameId.valueOf(1), null, webRequestOperation, new TimeRange(12, 3456789), [])

    @Shared
    def childFrame = new SimpleFrame(FrameId.valueOf(2), rootFrame, methodCallOperation, new TimeRange(23, 2345678), [])

    @Shared
    def grandchildFrame = new SimpleFrame(FrameId.valueOf(3), childFrame, httpOperation, new TimeRange(34, 1234567), [])

    @Subject
    def statisticsMapper = new RequestStatisticsMapperFactory().get()

    def "should properly serialize a root Frame with no children"() {
        expect:
        statisticsMapper.writeValueAsString(frame) == expectedJson

        where:
        frame = rootFrame

        expectedJson = compact('''
            {
              "id": 1,
              "range": {
                "start": 12,
                "end": 3456789,
                "durationMillis": 3
              },
              "operation": {
                "type": "web_request",
                "label": "restful-web-service-endpoint",
                "properties": {
                  "arguments": {
                    "properties": {
                      "client_id": "spock",
                      "api_version": 2
                    }
                  }
                }
              },
              "children": []
            }
            ''')
    }

    def "should properly serialize a root Frame with 1 child"() {
        expect:
        statisticsMapper.writeValueAsString(frame) == expectedJson

        where:
        frame = rootFrame.with { children = [childFrame]; return it }

        expectedJson = compact('''
            {
              "id": 1,
              "range": {
                "start": 12,
                "end": 3456789,
                "durationMillis": 3
              },
              "operation": {
                "type": "web_request",
                "label": "restful-web-service-endpoint",
                "properties": {
                  "arguments": {
                    "properties": {
                      "client_id": "spock",
                      "api_version": 2
                    }
                  }
                }
              },
              "children": [
                {
                  "id": 2,
                  "range": {
                    "start": 23,
                    "end": 2345678,
                    "durationMillis": 2
                  },
                  "operation": {
                    "type": "method",
                    "label": "controller",
                    "properties": {
                      "arguments": {
                        "items": [
                          "1234567890123",
                          17
                        ]
                      }
                    }
                  },
                  "children": []
                }
              ]
            }
            ''')
    }

    def "should properly serialize a root Frame with 1 child and 1 grandchild"() {
        expect:
        statisticsMapper.writeValueAsString(frame) == expectedJson

        where:
        frame = rootFrame.with { children = [childFrame.with { children = [grandchildFrame]; return it }]; return it }

        expectedJson = compact('''
            {
              "id": 1,
              "range": {
                "start": 12,
                "end": 3456789,
                "durationMillis": 3
              },
              "operation": {
                "type": "web_request",
                "label": "restful-web-service-endpoint",
                "properties": {
                  "arguments": {
                    "properties": {
                      "client_id": "spock",
                      "api_version": 2
                    }
                  }
                }
              },
              "children": [
                {
                  "id": 2,
                  "range": {
                    "start": 23,
                    "end": 2345678,
                    "durationMillis": 2
                  },
                  "operation": {
                    "type": "method",
                    "label": "controller",
                    "properties": {
                      "arguments": {
                        "items": [
                          "1234567890123",
                          17
                        ]
                      }
                    }
                  },
                  "children": [
                    {
                      "id": 3,
                      "range": {
                        "start": 34,
                        "end": 1234567,
                        "durationMillis": 1
                      },
                      "operation": {
                        "type": "http",
                        "label": "external-call",
                        "properties": {
                          "arguments": {
                            "properties": {
                              "isbn": "1234567890123",
                              "page_number": 17
                            }
                          }
                        }
                      },
                      "children": []
                    }
                  ]
                }
              ]
            }
            ''')
    }
}
