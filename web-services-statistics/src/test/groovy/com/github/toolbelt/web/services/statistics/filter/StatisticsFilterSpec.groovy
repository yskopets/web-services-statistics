package com.github.toolbelt.web.services.statistics.filter

import com.github.toolbelt.web.services.statistics.RequestStatistics
import com.github.toolbelt.web.services.statistics.RequestStatisticsHandler
import com.github.toolbelt.web.services.statistics.collector.impl.DefaultRequestStatisticsCollector
import com.github.toolbelt.web.services.statistics.test.example.elibrary.scenarios.BookContentsExceptionalScenario
import com.github.toolbelt.web.services.statistics.test.example.elibrary.scenarios.CompleteBookScanSuccessfulScenario
import com.github.toolbelt.web.services.statistics.test.example.elibrary.scenarios.IndividualPageScanExceptionalScenario
import com.github.toolbelt.web.services.statistics.test.given.PreconfiguredRequestsCapable
import com.github.toolbelt.web.services.statistics.test.given.PredictableTimingCapable
import com.github.toolbelt.web.services.statistics.test.given.StatisticsFilterFactory
import com.github.toolbelt.web.services.statistics.test.given.StatisticsSerializationCapable
import com.github.toolbelt.web.services.statistics.test.util.environment.PredictableLineSeparatorCapable
import com.github.toolbelt.web.services.statistics.trace.impl.SimpleTracingSubsystem
import org.springframework.mock.web.MockHttpServletResponse
import spock.lang.Specification
import spock.lang.Subject

import javax.servlet.FilterChain
import javax.servlet.ServletException

import static com.github.toolbelt.web.services.statistics.test.util.throwable.PredictableThrowablesCapable.withoutStackTrace

/**
 * Design spec for {@link StatisticsFilter}.
 *
 * @author <a href="mailto:y.skopets@gmail.com">Yaroslav Skopets</a>
 */
class StatisticsFilterSpec extends Specification implements StatisticsFilterFactory, PredictableTimingCapable,
        PreconfiguredRequestsCapable, StatisticsSerializationCapable, PredictableLineSeparatorCapable {

    def statisticsCollector = new DefaultRequestStatisticsCollector('5724f1061d917a7db90a589a')

    def statisticsHandler = Mock(RequestStatisticsHandler)

    def tracingSubsystem = new SimpleTracingSubsystem({ -> predictableStopWatch })

    @Subject
    def statisticsFilter = newStatisticsFilter(statisticsCollector, statisticsHandler, tracingSubsystem, predictableClock, 'farm-007', 'statisticsAttribute')

    def "should not emit any statistics if the requested resource hasn't been recognized"() {
        given:
        def request = newPreconfiguredRequest('GET', '/api/v2/e-library/books/1234567890123')
        def response = new MockHttpServletResponse()
        def handler = Stub(FilterChain)

        when:
        statisticsFilter.doFilter(request, response, handler)

        then:
        0 * statisticsHandler._
    }

    def "should not publish any statistics as a request attribute if the requested resource hasn't been recognized"() {
        given:
        def request = newPreconfiguredRequest('GET', '/api/v2/e-library/books/1234567890123')
        def response = new MockHttpServletResponse()
        def handler = Stub(FilterChain)

        when:
        statisticsFilter.doFilter(request, response, handler)

        then:
        request.getAttribute('statisticsAttribute') == null
    }

    def "should publish collected statistics as a request attribute if the requested resource has been recognized"() {
        given:
        def request = newPreconfiguredRequest('GET', '/api/v2/e-library/books/1234567890123')
        def response = new MockHttpServletResponse()
        def handler = new CompleteBookScanSuccessfulScenario(statisticsCollector, tracingSubsystem)
        and:
        RequestStatistics collectedStatistics = null

        when:
        statisticsFilter.doFilter(request, response, handler)

        then: "capture statistics object passed to the handler"
        1 * statisticsHandler.handle(_) >> { RequestStatistics it -> collectedStatistics = it }
        and:
        request.getAttribute('statisticsAttribute').is(collectedStatistics)
    }

    def "should emit collected statistics if the requested resource has been recognized and request was processed successfully"() {
        given:
        def request = newPreconfiguredRequest('GET', '/api/v2/e-library/books/1234567890123/scan', 'container_format=application/pdf')
        def response = new MockHttpServletResponse()
        def handler = new CompleteBookScanSuccessfulScenario(statisticsCollector, tracingSubsystem)
        and:
        RequestStatistics collectedStatistics = null

        when:
        statisticsFilter.doFilter(request, response, handler)

        then: "capture statistics object passed to the handler"
        1 * statisticsHandler.handle(_) >> { RequestStatistics it -> collectedStatistics = it }
        and:
        pretty(jsonOf(collectedStatistics)) == pretty('''
            {
              "data": {
                "FormatVersion": [1, 1],
                "Request": {
                  "MetaInfo": {
                    "RequestTime": "2015-04-30T12:42:54.628+02:00",
                    "ClientIPAddress": "1.2.3.4",
                    "UserName": null,
                    "RequestMethod": "GET",
                    "Referer": "https://www.google.com/",
                    "UserAgent": "Mozilla"
                  },
                  "Resource": {
                    "Type": "CompleteBookScan",
                    "Parameters": {
                      "ISBN": "1234567890123",
                      "ContainerFormat": "application/pdf"
                    }
                  }
                },
                "Uid": "5724f1061d917a7db90a589a",
                "Response": {
                  "MetaInfo": {
                    "ProcessingNode": "farm-007",
                    "StatusCode": 200,
                    "ContentType": "application/pdf",
                    "ContentLength": 12345
                  },
                  "Metrics": {
                    "NumberOfPages": 257,
                    "TimeTillFirstResponseByteMillis": 100,
                    "TotalProcessingTimeMillis": 200,
                    "ResponseBodyWrittenBytesCount": 32,
                    "ResponseBodyWrittenCharsCount": 0
                  },
                  "Trace": {
                    "rootFrame": {
                      "id": 0,
                      "children": [
                        {
                          "id": 1,
                          "children": [
                            {
                              "id": 2,
                              "children": [
                                {
                                  "id": 3,
                                  "children": [],
                                  "operation": {
                                    "type": "http",
                                    "label": "external-call",
                                    "properties": {
                                      "arguments": {
                                        "properties": {
                                          "isbn": "1234567890123",
                                          "page_number": 17
                                        }
                                      },
                                      "returnValue": "401"
                                    }
                                  },
                                  "range": {
                                    "start": 40000001,
                                    "end": 50000001,
                                    "durationMillis": 10
                                  }
                                }
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
                                  "returnValue": "interim return value"
                                }
                              },
                              "range": {
                                "start": 30000001,
                                "end": 60000001,
                                "durationMillis": 30
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
                            "start": 20000001,
                            "end": 70000001,
                            "durationMillis": 50
                          }
                        }
                      ],
                      "operation": {
                        "type": "request_statistics_filter",
                        "label": "",
                        "properties": {
                          "returnValue": "void"
                        }
                      },
                      "range": {
                        "start": 10000001,
                        "end": 80000001,
                        "durationMillis": 70
                      }
                    }
                  }
                }
              }
            }
            ''')
    }

    def "should emit collected statistics if the requested resource has been recognized but request processing failed with IOException"() {
        given:
        def request = newPreconfiguredRequest('GET', '/api/v2/e-library/books/1234567890123')
        def response = new MockHttpServletResponse()
        def handler = new BookContentsExceptionalScenario(statisticsCollector, tracingSubsystem, withoutStackTrace(new IOException('Disk operation failed')))
        and:
        RequestStatistics collectedStatistics = null

        when:
        statisticsFilter.doFilter(request, response, handler)

        then:
        def e = thrown(IOException)
        e.message == 'Disk operation failed'
        and: "capture statistics object passed to the handler"
        1 * statisticsHandler.handle(_) >> { RequestStatistics it -> collectedStatistics = it }
        and:
        pretty(jsonOf(collectedStatistics)) == pretty('''
            {
              "data": {
                "FormatVersion": [1, 1],
                "Request": {
                  "MetaInfo": {
                    "RequestTime": "2015-04-30T12:42:54.628+02:00",
                    "ClientIPAddress": "1.2.3.4",
                    "UserName": null,
                    "RequestMethod": "GET",
                    "Referer": "https://www.google.com/",
                    "UserAgent": "Mozilla"
                  },
                  "Resource": {
                    "Type": "BookContents",
                    "Parameters": {
                      "ISBN": "1234567890123",
                      "TranslationLanguage": "nl"
                    }
                  }
                },
                "Uid": "5724f1061d917a7db90a589a",
                "Response": {
                  "MetaInfo": {
                    "ProcessingNode": "farm-007",
                    "StatusCode": 500,
                    "ContentType": null,
                    "ContentLength": null
                  },
                  "Metrics": {
                    "TimeTillFirstResponseByteMillis": null,
                    "TotalProcessingTimeMillis": 100,
                    "ResponseBodyWrittenBytesCount": 0,
                    "ResponseBodyWrittenCharsCount": 0
                  },
                  "Trace": {
                    "rootFrame": {
                      "id": 0,
                      "children": [
                        {
                          "id": 1,
                          "children": [
                            {
                              "id": 2,
                              "children": [],
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
                                  "exception": "java.io.IOException: Disk operation failed\\r\\n"
                                }
                              },
                              "range": {
                                "start": 30000001,
                                "end": 40000001,
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
                            "start": 20000001,
                            "end": 50000001,
                            "durationMillis": 30
                          }
                        }
                      ],
                      "operation": {
                        "type": "request_statistics_filter",
                        "label": "",
                        "properties": {
                          "exception": "java.io.IOException: Disk operation failed\\r\\n"
                        }
                      },
                      "range": {
                        "start": 10000001,
                        "end": 60000001,
                        "durationMillis": 50
                      }
                    }
                  }
                }
              }
            }
            ''')
    }

    def "should emit collected statistics if the requested resource has been recognized but request processing failed with ServletException"() {
        given:
        def request = newPreconfiguredRequest('GET', '/api/v2/e-library/books/1234567890123')
        def response = new MockHttpServletResponse()
        def handler = new BookContentsExceptionalScenario(statisticsCollector, tracingSubsystem, withoutStackTrace(new ServletException('System is temporary unavailable')))
        and:
        RequestStatistics collectedStatistics = null

        when:
        statisticsFilter.doFilter(request, response, handler)

        then:
        def e = thrown(ServletException)
        e.message == 'System is temporary unavailable'
        and: "capture statistics object passed to the handler"
        1 * statisticsHandler.handle(_) >> { RequestStatistics it -> collectedStatistics = it }
        and:
        pretty(jsonOf(collectedStatistics)) == pretty('''
            {
              "data": {
                "FormatVersion": [1, 1],
                "Request": {
                  "MetaInfo": {
                    "RequestTime": "2015-04-30T12:42:54.628+02:00",
                    "ClientIPAddress": "1.2.3.4",
                    "UserName": null,
                    "RequestMethod": "GET",
                    "Referer": "https://www.google.com/",
                    "UserAgent": "Mozilla"
                  },
                  "Resource": {
                    "Type": "BookContents",
                    "Parameters": {
                      "ISBN": "1234567890123",
                      "TranslationLanguage": "nl"
                    }
                  }
                },
                "Uid": "5724f1061d917a7db90a589a",
                "Response": {
                  "MetaInfo": {
                    "ProcessingNode": "farm-007",
                    "StatusCode": 500,
                    "ContentType": null,
                    "ContentLength": null
                  },
                  "Metrics": {
                    "TimeTillFirstResponseByteMillis": null,
                    "TotalProcessingTimeMillis": 100,
                    "ResponseBodyWrittenBytesCount": 0,
                    "ResponseBodyWrittenCharsCount": 0
                  },
                  "Trace": {
                    "rootFrame": {
                      "id": 0,
                      "children": [
                        {
                          "id": 1,
                          "children": [
                            {
                              "id": 2,
                              "children": [],
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
                                  "exception": "javax.servlet.ServletException: System is temporary unavailable\\r\\n"
                                }
                              },
                              "range": {
                                "start": 30000001,
                                "end": 40000001,
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
                            "start": 20000001,
                            "end": 50000001,
                            "durationMillis": 30
                          }
                        }
                      ],
                      "operation": {
                        "type": "request_statistics_filter",
                        "label": "",
                        "properties": {
                          "exception": "javax.servlet.ServletException: System is temporary unavailable\\r\\n"
                        }
                      },
                      "range": {
                        "start": 10000001,
                        "end": 60000001,
                        "durationMillis": 50
                      }
                    }
                  }
                }
              }
            }
            ''')
    }

    def "should emit collected statistics if the requested resource has been recognized but request processing failed with OutOfMemoryError"() {
        given:
        def request = newPreconfiguredRequest('GET', '/api/v2/e-library/books/1234567890123')
        def response = new MockHttpServletResponse()
        def handler = new BookContentsExceptionalScenario(statisticsCollector, tracingSubsystem, withoutStackTrace(new OutOfMemoryError('System has crashed')))
        and:
        RequestStatistics collectedStatistics = null

        when:
        statisticsFilter.doFilter(request, response, handler)

        then:
        def e = thrown(OutOfMemoryError)
        e.message == 'System has crashed'
        and: "capture statistics object passed to the handler"
        1 * statisticsHandler.handle(_) >> { RequestStatistics it -> collectedStatistics = it }
        and:
        pretty(jsonOf(collectedStatistics)) == pretty('''
            {
              "data": {
                "FormatVersion": [1, 1],
                "Request": {
                  "MetaInfo": {
                    "RequestTime": "2015-04-30T12:42:54.628+02:00",
                    "ClientIPAddress": "1.2.3.4",
                    "UserName": null,
                    "RequestMethod": "GET",
                    "Referer": "https://www.google.com/",
                    "UserAgent": "Mozilla"
                  },
                  "Resource": {
                    "Type": "BookContents",
                    "Parameters": {
                      "ISBN": "1234567890123",
                      "TranslationLanguage": "nl"
                    }
                  }
                },
                "Uid": "5724f1061d917a7db90a589a",
                "Response": {
                  "MetaInfo": {
                    "ProcessingNode": "farm-007",
                    "StatusCode": 500,
                    "ContentType": null,
                    "ContentLength": null
                  },
                  "Metrics": {
                    "TimeTillFirstResponseByteMillis": null,
                    "TotalProcessingTimeMillis": 100,
                    "ResponseBodyWrittenBytesCount": 0,
                    "ResponseBodyWrittenCharsCount": 0
                  },
                  "Trace": {
                    "rootFrame": {
                      "id": 0,
                      "children": [
                        {
                          "id": 1,
                          "children": [
                            {
                              "id": 2,
                              "children": [],
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
                                  "exception": "java.lang.OutOfMemoryError: System has crashed\\r\\n"
                                }
                              },
                              "range": {
                                "start": 30000001,
                                "end": 40000001,
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
                            "start": 20000001,
                            "end": 50000001,
                            "durationMillis": 30
                          }
                        }
                      ],
                      "operation": {
                        "type": "request_statistics_filter",
                        "label": "",
                        "properties": {
                          "exception": "java.lang.OutOfMemoryError: System has crashed\\r\\n"
                        }
                      },
                      "range": {
                        "start": 10000001,
                        "end": 60000001,
                        "durationMillis": 50
                      }
                    }
                  }
                }
              }
            }
            ''')
    }

    def "should emit collected statistics if the requested resource has been recognized but request processing failed in the middle of response streaming"() {
        given:
        def request = newPreconfiguredRequest('GET', '/api/v2/e-library/books/1234567890123/pages/17/scan', 'image_format=image/png')
        def response = new MockHttpServletResponse()
        def handler = new IndividualPageScanExceptionalScenario(statisticsCollector, tracingSubsystem, withoutStackTrace(new IllegalStateException('Client has aborted the request')))
        and:
        RequestStatistics collectedStatistics = null

        when:
        statisticsFilter.doFilter(request, response, handler)

        then:
        def e = thrown(IllegalStateException)
        e.message == 'Client has aborted the request'
        and: "capture statistics object passed to the handler"
        1 * statisticsHandler.handle(_) >> { RequestStatistics it -> collectedStatistics = it }
        and:
        pretty(jsonOf(collectedStatistics)) == pretty('''
            {
              "data": {
                "FormatVersion": [1, 1],
                "Request": {
                  "MetaInfo": {
                    "RequestTime": "2015-04-30T12:42:54.628+02:00",
                    "ClientIPAddress": "1.2.3.4",
                    "UserName": null,
                    "RequestMethod": "GET",
                    "Referer": "https://www.google.com/",
                    "UserAgent": "Mozilla"
                  },
                  "Resource": {
                    "Type": "IndividualPageScan",
                    "Parameters": {
                      "ISBN": "1234567890123",
                      "PageNumber": "17",
                      "ImageFormat": "image/png"
                    }
                  }
                },
                "Uid": "5724f1061d917a7db90a589a",
                "Response": {
                  "MetaInfo": {
                    "ProcessingNode": "farm-007",
                    "StatusCode": 500,
                    "ContentType": "image/png",
                    "ContentLength": 12345
                  },
                  "Metrics": {
                    "TimeTillFirstResponseByteMillis": 100,
                    "TotalProcessingTimeMillis": 200,
                    "ResponseBodyWrittenBytesCount": 32,
                    "ResponseBodyWrittenCharsCount": 0
                  },
                  "Trace": {
                    "rootFrame": {
                      "id": 0,
                      "children": [
                        {
                          "id": 1,
                          "children": [
                            {
                              "id": 2,
                              "children": [],
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
                                  "exception": "java.lang.IllegalStateException: Client has aborted the request\\r\\n"
                                }
                              },
                              "range": {
                                "start": 30000001,
                                "end": 40000001,
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
                            "start": 20000001,
                            "end": 50000001,
                            "durationMillis": 30
                          }
                        }
                      ],
                      "operation": {
                        "type": "request_statistics_filter",
                        "label": "",
                        "properties": {
                          "exception": "java.lang.IllegalStateException: Client has aborted the request\\r\\n"
                        }
                      },
                      "range": {
                        "start": 10000001,
                        "end": 60000001,
                        "durationMillis": 50
                      }
                    }
                  }
                }
              }
            }
            ''')
    }
}
