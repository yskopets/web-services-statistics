package com.github.toolbelt.web.services.statistics.support.convert

import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Specification
import spock.lang.Subject

import static com.google.common.net.MediaType.JSON_UTF_8

/**
 * Design spec for {@link ObjectToJsonDataSourceConverter}.
 *
 * @author <a href="mailto:y.skopets@gmail.com">Yaroslav Skopets</a>
 */
class ObjectToJsonDataSourceConverterSpec extends Specification {

    @Subject
    def converter = new ObjectToJsonDataSourceConverter(new ObjectMapper())

    def "should properly convert `null` value"() {
        when:
        def dataSource = converter.convert(null)
        then:
        dataSource.contentType == JSON_UTF_8 as String
        dataSource.inputStream.bytes == 'null' as byte[]
    }

    def "should properly convert regular Java objects"() {
        when:
        def dataSource = converter.convert([key: 'value'])
        then:
        dataSource.contentType == JSON_UTF_8 as String
        dataSource.inputStream.bytes == '{"key":"value"}' as byte[]
    }
}
