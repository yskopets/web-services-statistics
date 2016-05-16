package com.github.toolbelt.web.services.statistics.collector.impl

import com.github.toolbelt.web.services.statistics.RequestStatistics.RequestKeys
import com.github.toolbelt.web.services.statistics.RequestStatistics.ResponseKeys
import com.github.toolbelt.web.services.statistics.trace.ResponseTrace
import com.springsource.insight.intercept.trace.Frame
import spock.lang.Specification
import spock.lang.Subject

import static com.github.toolbelt.web.services.statistics.RequestStatistics.RequestKeys.Resource
import static com.github.toolbelt.web.services.statistics.RequestStatistics.ResourceKeys.Parameters
import static com.github.toolbelt.web.services.statistics.RequestStatistics.ResourceKeys.Type
import static com.github.toolbelt.web.services.statistics.RequestStatistics.ResponseKeys.Metrics
import static com.github.toolbelt.web.services.statistics.RequestStatistics.ResponseKeys.Trace
import static com.github.toolbelt.web.services.statistics.RequestStatistics.RootKeys.FormatVersion
import static com.github.toolbelt.web.services.statistics.RequestStatistics.RootKeys.Request
import static com.github.toolbelt.web.services.statistics.RequestStatistics.RootKeys.Response
import static com.github.toolbelt.web.services.statistics.RequestStatistics.RootKeys.Uid
import static com.github.toolbelt.web.services.statistics.request.metainfo.GenericRequestMetaInfo.ClientIPAddress
import static com.github.toolbelt.web.services.statistics.request.metainfo.GenericRequestMetaInfo.Referer
import static com.github.toolbelt.web.services.statistics.request.metainfo.GenericRequestMetaInfo.UserAgent
import static com.github.toolbelt.web.services.statistics.response.metainfo.GenericResponseMetaInfo.ContentType
import static com.github.toolbelt.web.services.statistics.response.metainfo.GenericResponseMetaInfo.StatusCode
import static com.github.toolbelt.web.services.statistics.response.metrics.GenericResponseMetric.TimeTillFirstResponseByteMillis
import static com.github.toolbelt.web.services.statistics.response.metrics.GenericResponseMetric.TotalProcessingTimeMillis
import static com.github.toolbelt.web.services.statistics.test.example.elibrary.resource.BookLibraryResource.BookContents
import static com.github.toolbelt.web.services.statistics.test.example.elibrary.resource.parameters.BookContentsParameter.ISBN
import static com.github.toolbelt.web.services.statistics.test.example.elibrary.resource.parameters.BookContentsParameter.TranslationLanguage

/**
 * Design spec for {@link DefaultRequestStatisticsCollector}.
 *
 * @author <a href="mailto:y.skopets@gmail.com">Yaroslav Skopets</a>
 */
class DefaultRequestStatisticsCollectorSpec extends Specification {

    @Subject
    def collector = new DefaultRequestStatisticsCollector()

    def "should consider resource unrecognized if no description was given"() {
        expect:
        !collector.resourceRecognized
        collector.summarizeCollectedStatistics().data as Map == [:]
        and: "unique id gets assigned anyway"
        collector.uniqueId()
    }

    def "should ignore any collected information unless resource is recognized"() {
        when:
        collector.describeRequest()
                .withMetaInfo(ClientIPAddress, '127.0.0.1')
                .withMetaInfo(UserAgent, 'Mozilla')
                .withMetaInfo(Referer, 'https://www.google.com')
        collector.describeResponse()
                .withMetaInfo(StatusCode, 200)
                .withMetaInfo(ContentType, 'application/json')
                .withMetric(TotalProcessingTimeMillis, 123)
                .withMetric(TimeTillFirstResponseByteMillis, 111)
                .withTrace(new ResponseTrace(Mock(Frame)))

        then:
        !collector.resourceRecognized
        collector.summarizeCollectedStatistics().data as Map == [:]
        and: "unique id gets assigned anyway"
        collector.uniqueId()
    }

    def "should retain all collected information if resource is recognized"() {
        given:
        def responseTrace = new ResponseTrace(Mock(Frame))

        when:
        collector.describeRequest().requestForResource()
                .ofType(BookContents)
                .withParameter(ISBN, '1234567890123')
                .withParameter(TranslationLanguage, 'nl')
        collector.describeRequest()
                .withMetaInfo(ClientIPAddress, '127.0.0.1')
                .withMetaInfo(UserAgent, 'Mozilla')
                .withMetaInfo(Referer, 'https://www.google.com')
        collector.describeResponse()
                .withMetaInfo(StatusCode, 200)
                .withMetaInfo(ContentType, 'application/json')
                .withMetric(TotalProcessingTimeMillis, 123)
                .withMetric(TimeTillFirstResponseByteMillis, 111)
                .withTrace(responseTrace)

        then:
        collector.resourceRecognized
        collector.summarizeCollectedStatistics().data as Map == [(FormatVersion): [1, 1],
                                                          (Request)      : [(RequestKeys.MetaInfo): [(ClientIPAddress): '127.0.0.1', (UserAgent): 'Mozilla', (Referer): 'https://www.google.com'],
                                                                            (Resource)            : [(Type): BookContents, (Parameters): [(ISBN): '1234567890123', (TranslationLanguage): 'nl']]],
                                                          (Uid)          : collector.uniqueId(),
                                                          (Response)     : [(ResponseKeys.MetaInfo): [(StatusCode): 200, (ContentType): 'application/json'],
                                                                            (Metrics)              : [(TotalProcessingTimeMillis): 123, (TimeTillFirstResponseByteMillis): 111],
                                                                            (Trace)                : responseTrace]]
        and: "unique id gets assigned anyway"
        collector.uniqueId()
    }

    def "should reflect a default application-level version of statistical data unless it has been defined explicitly"() {
        when:
        collector.describeRequest().requestForResource().ofType(BookContents)

        then:
        collector.summarizeCollectedStatistics().data as Map == [(FormatVersion): [1, 1],
                                                          (Request)      : [(RequestKeys.MetaInfo): [:],
                                                                            (Resource)            : [(Type): BookContents, (Parameters): [:]]],
                                                          (Uid)          : collector.uniqueId(),
                                                          (Response)     : [(ResponseKeys.MetaInfo): [:],
                                                                            (Metrics)              : [:]]]
    }

    def "should reflect an explicitly defined application-level version of statistical data"() {
        given:
        collector = new DefaultRequestStatisticsCollector(2)

        when:
        collector.describeRequest().requestForResource().ofType(BookContents)

        then:
        collector.summarizeCollectedStatistics().data as Map == [(FormatVersion): [1, 2],
                                                          (Request)      : [(RequestKeys.MetaInfo): [:],
                                                                            (Resource)            : [(Type): BookContents, (Parameters): [:]]],
                                                          (Uid)          : collector.uniqueId(),
                                                          (Response)     : [(ResponseKeys.MetaInfo): [:],
                                                                            (Metrics)              : [:]]]
    }
}