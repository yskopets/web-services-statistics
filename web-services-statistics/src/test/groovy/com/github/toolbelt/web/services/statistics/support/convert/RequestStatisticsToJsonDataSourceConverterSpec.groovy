package com.github.toolbelt.web.services.statistics.support.convert

import com.github.toolbelt.web.services.statistics.collector.impl.DefaultRequestStatisticsCollector
import com.github.toolbelt.web.services.statistics.test.given.PredictableTimingCapable
import com.github.toolbelt.web.services.statistics.test.util.json.JsonOperationsCapable
import spock.lang.Specification
import spock.lang.Subject

import static com.github.toolbelt.web.services.statistics.request.metainfo.GenericRequestMetaInfo.RequestTime
import static com.github.toolbelt.web.services.statistics.response.metainfo.GenericResponseMetaInfo.ContentLength
import static com.github.toolbelt.web.services.statistics.response.metainfo.GenericResponseMetaInfo.StatusCode
import static com.github.toolbelt.web.services.statistics.response.metrics.GenericResponseMetric.TimeTillFirstResponseByteMillis
import static com.github.toolbelt.web.services.statistics.test.example.elibrary.resource.BookLibraryResource.IndividualPageScan
import static com.github.toolbelt.web.services.statistics.test.example.elibrary.resource.parameters.IndividualPageScanParameter.ISBN
import static com.github.toolbelt.web.services.statistics.test.example.elibrary.resource.parameters.IndividualPageScanParameter.ImageFormat
import static com.github.toolbelt.web.services.statistics.test.example.elibrary.resource.parameters.IndividualPageScanParameter.PageNumber
import static com.google.common.net.MediaType.JSON_UTF_8

/**
 * Design spec for {@link RequestStatisticsToJsonDataSourceConverter}.
 *
 * @author <a href="mailto:y.skopets@gmail.com">Yaroslav Skopets</a>
 */
class RequestStatisticsToJsonDataSourceConverterSpec extends Specification
        implements PredictableTimingCapable, JsonOperationsCapable {

    @Subject
    def converter = new RequestStatisticsToJsonDataSourceConverter()

    def "should properly convert `null` value"() {
        when:
        def dataSource = converter.convert(null)
        then:
        dataSource.contentType == JSON_UTF_8 as String
        dataSource.inputStream.bytes == 'null' as byte[]
    }

    def "should properly convert regular `RequestStatistics` objects"() {
        given:
        def statsCollector = new DefaultRequestStatisticsCollector('5724f1061d917a7db90a589a')
        statsCollector.describeRequest()
                .withMetaInfo(RequestTime, predictableStartTime)
        statsCollector.describeRequest().requestForResource()
                .ofType(IndividualPageScan)
                .withParameter(ISBN, '1234567890123')
                .withParameter(PageNumber, 17)
                .withParameter(ImageFormat, 'image/png')
        statsCollector.describeResponse()
                .withMetaInfo(StatusCode, 200)
                .withMetaInfo(ContentLength, 23456)
                .withMetric(TimeTillFirstResponseByteMillis, 21)
        def statistics = statsCollector.summarizeCollectedStatistics()

        when:
        def dataSource = converter.convert(statistics)
        then:
        dataSource.contentType == JSON_UTF_8 as String
        dataSource.inputStream.bytes == compact('''
            {
              "data": {
                "FormatVersion": [1, 1],
                "Request": {
                  "MetaInfo": {
                    "RequestTime": "2015-04-30T12:42:54.628+02:00"
                  },
                  "Resource": {
                    "Type": "IndividualPageScan",
                    "Parameters": {
                      "ISBN": "1234567890123",
                      "PageNumber": 17,
                      "ImageFormat": "image/png"
                    }
                  }
                },
                "Uid": "5724f1061d917a7db90a589a",
                "Response": {
                  "MetaInfo": {
                    "StatusCode": 200,
                    "ContentLength": 23456
                  },
                  "Metrics": {
                    "TimeTillFirstResponseByteMillis": 21
                  }
                }
              }
            }''') as byte[]
    }
}
