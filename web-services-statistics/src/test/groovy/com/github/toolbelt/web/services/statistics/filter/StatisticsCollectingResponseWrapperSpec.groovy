package com.github.toolbelt.web.services.statistics.filter

import com.github.toolbelt.web.services.statistics.test.given.PredictableTimingCapable
import org.springframework.mock.web.MockHttpServletResponse
import spock.lang.Specification
import spock.lang.Subject

import javax.servlet.http.HttpServletResponse

import static com.google.common.base.Charsets.UTF_8
import static com.google.common.net.HttpHeaders.CONTENT_LENGTH
import static com.google.common.net.HttpHeaders.CONTENT_TYPE

/**
 * Design spec for {@link StatisticsCollectingResponseWrapper}.
 *
 * @author <a href="mailto:y.skopets@gmail.com">Yaroslav Skopets</a>
 */
class StatisticsCollectingResponseWrapperSpec extends Specification implements PredictableTimingCapable {

    @Subject
    def statsResponseWrapper = new StatisticsCollectingResponseWrapper(new MockHttpServletResponse(), predictableClock)

    def "should remember 'Content-Type' set through a call to #setContentType(String type)"() {
        when:
        statsResponseWrapper.setContentType('application/json')
        then:
        statsResponseWrapper.contentType == 'application/json'
    }

    def "should remember the very last 'Content-Type' set through a call to #setContentType(String type)"() {
        when:
        statsResponseWrapper.setContentType('application/json')
        statsResponseWrapper.setContentType('text/xml')
        statsResponseWrapper.setContentType('text/html')
        then:
        statsResponseWrapper.contentType == 'text/html'
    }

    def "should remember 'Content-Type' set through a call to #addHeader(String name, String value)"() {
        when:
        statsResponseWrapper.addHeader(CONTENT_TYPE, 'application/json')
        then:
        statsResponseWrapper.contentType == 'application/json'
    }

    def "should remember the very last 'Content-Type' set through a call to #addHeader(String name, String value)"() {
        when:
        statsResponseWrapper.addHeader(CONTENT_TYPE, 'application/json')
        statsResponseWrapper.addHeader(CONTENT_TYPE, 'text/xml')
        statsResponseWrapper.addHeader(CONTENT_TYPE, 'text/html')
        then:
        statsResponseWrapper.contentType == 'text/html'
    }

    def "should remember 'Content-Type' set through a call to #setHeader(String name, String value)"() {
        when:
        statsResponseWrapper.setHeader(CONTENT_TYPE, 'application/json')
        then:
        statsResponseWrapper.contentType == 'application/json'
    }

    def "should remember the very last 'Content-Type' set through a call to #setHeader(String name, String value)"() {
        when:
        statsResponseWrapper.setHeader(CONTENT_TYPE, 'application/json')
        statsResponseWrapper.setHeader(CONTENT_TYPE, 'text/xml')
        statsResponseWrapper.setHeader(CONTENT_TYPE, 'text/html')
        then:
        statsResponseWrapper.contentType == 'text/html'
    }

    def "should remember 'Content-Length' set through a call to #setContentType(String type)"() {
        when:
        statsResponseWrapper.setContentLength(123)
        then:
        statsResponseWrapper.contentLength == 123
    }

    def "should remember the very last 'Content-Length' set through a call to #setContentType(String type)"() {
        when:
        statsResponseWrapper.setContentLength(123)
        statsResponseWrapper.setContentLength(456)
        statsResponseWrapper.setContentLength(789)
        then:
        statsResponseWrapper.contentLength == 789
    }

    def "should remember 'Content-Length' set through a call to #addHeader(String name, String value)"() {
        when:
        statsResponseWrapper.addHeader(CONTENT_LENGTH, '123')
        then:
        statsResponseWrapper.contentLength == 123
    }

    def "should be tolerant to non-Integer values of 'Content-Length' set through a call to #addHeader(String name, String value)"() {
        given:
        statsResponseWrapper = new StatisticsCollectingResponseWrapper(Mock(HttpServletResponse), predictableClock)
        when:
        statsResponseWrapper.addHeader(CONTENT_LENGTH, 'short')
        then:
        statsResponseWrapper.contentLength == null
    }

    def "should remember the very last 'Content-Length' set through a call to #addHeader(String name, String value)"() {
        when:
        statsResponseWrapper.addHeader(CONTENT_LENGTH, '123')
        statsResponseWrapper.addHeader(CONTENT_LENGTH, '456')
        statsResponseWrapper.addHeader(CONTENT_LENGTH, '789')
        then:
        statsResponseWrapper.contentLength == 789
    }

    def "should remember 'Content-Length' set through a call to #setHeader(String name, String value)"() {
        when:
        statsResponseWrapper.setHeader(CONTENT_LENGTH, '123')
        then:
        statsResponseWrapper.contentLength == 123
    }

    def "should be tolerant to non-Integer values of 'Content-Length' set through a call to #setHeader(String name, String value)"() {
        given:
        statsResponseWrapper = new StatisticsCollectingResponseWrapper(Mock(HttpServletResponse), predictableClock)
        when:
        statsResponseWrapper.setHeader(CONTENT_LENGTH, 'short')
        then:
        statsResponseWrapper.contentLength == null
    }

    def "should remember the very last 'Content-Length' set through a call to #setHeader(String name, String value)"() {
        when:
        statsResponseWrapper.setHeader(CONTENT_LENGTH, '123')
        statsResponseWrapper.setHeader(CONTENT_LENGTH, '456')
        statsResponseWrapper.setHeader(CONTENT_LENGTH, '789')
        then:
        statsResponseWrapper.contentLength == 789
    }

    def "should account 0 written bytes if there were no writes through #getOutputStream()"() {
        expect:
        statsResponseWrapper.writtenBytesCount == 0
        statsResponseWrapper.timeFirstByteWrittenMillis == null
    }

    def "should account 0 written bytes if #getOutputStream() was obtained but there were no writes to it"() {
        when:
        statsResponseWrapper.getOutputStream()
        then:
        statsResponseWrapper.writtenBytesCount == 0
        statsResponseWrapper.timeFirstByteWrittenMillis == null
    }

    def "should account 0 written bytes if writes were done through #getWriter()"() {
        when:
        statsResponseWrapper.getWriter().write('<html></html>')
        then:
        statsResponseWrapper.writtenBytesCount == 0
        statsResponseWrapper.timeFirstByteWrittenMillis == predictableStartTime.millis
    }

    def "should account bytes written through #getOutputStream()"() {
        when:
        statsResponseWrapper.getOutputStream().write('<html></html>'.getBytes(UTF_8))
        then:
        statsResponseWrapper.writtenBytesCount == 13
        statsResponseWrapper.writtenCharsCount == 0
        statsResponseWrapper.timeFirstByteWrittenMillis == predictableStartTime.millis
    }

    def "should account 0 written chars if there were no writes through #getWriter()"() {
        expect:
        statsResponseWrapper.writtenCharsCount == 0
        statsResponseWrapper.timeFirstByteWrittenMillis == null
    }

    def "should account 0 written chars if #getWriter() was obtained but there were no writes to it"() {
        when:
        statsResponseWrapper.getOutputStream()
        then:
        statsResponseWrapper.writtenCharsCount == 0
        statsResponseWrapper.timeFirstByteWrittenMillis == null
    }

    def "should account 0 written chars if writes were done through #getOutputStream()"() {
        when:
        statsResponseWrapper.getOutputStream().write('<html></html>'.getBytes(UTF_8))
        then:
        statsResponseWrapper.writtenCharsCount == 0
        statsResponseWrapper.timeFirstByteWrittenMillis == predictableStartTime.millis
    }

    def "should account chars written through #getWriter()"() {
        when:
        statsResponseWrapper.getWriter().write('<html></html>')
        then:
        statsResponseWrapper.writtenCharsCount == 13
        statsResponseWrapper.writtenBytesCount == 0
        statsResponseWrapper.timeFirstByteWrittenMillis == predictableStartTime.millis
    }
}
