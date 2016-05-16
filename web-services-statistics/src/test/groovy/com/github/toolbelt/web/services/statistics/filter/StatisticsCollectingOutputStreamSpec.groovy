package com.github.toolbelt.web.services.statistics.filter

import com.github.toolbelt.web.services.statistics.test.given.PredictableTimingCapable
import com.github.toolbelt.web.services.statistics.test.util.decorator.DecoratorTestingCapable
import org.spockframework.mock.IMockInvocation
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

import java.lang.reflect.Method

/**
 * Design spec for {@link StatisticsCollectingOutputStream}.
 *
 * @author <a href="mailto:y.skopets@gmail.com">Yaroslav Skopets</a>
 */
class StatisticsCollectingOutputStreamSpec extends Specification implements PredictableTimingCapable, DecoratorTestingCapable {

    def actualOutputStream = Mock(OutputStream)

    @Subject
    def statsOutputStream = new StatisticsCollectingOutputStream(actualOutputStream, predictableClock)

    def "should account 0 written bytes if there were no writes"() {
        expect:
        statsOutputStream.writtenBytesCount == 0
        statsOutputStream.timeFirstByteWrittenMillis == null
    }

    def "should account bytes written through #write(int b)"() {
        when:
        statsOutputStream.write(12)
        then:
        statsOutputStream.writtenBytesCount == 1
        statsOutputStream.timeFirstByteWrittenMillis == predictableStartTime.millis

        when:
        statsOutputStream.write(34)
        then:
        statsOutputStream.writtenBytesCount == 2
        statsOutputStream.timeFirstByteWrittenMillis == predictableStartTime.millis
    }

    def "should not account bytes writes written through #write(int b) that ended up with an exception"() {
        when:
        statsOutputStream.write(12)
        then: "expect delegate stream to handle write normally"
        actualOutputStream.write(12) >> {}
        and:
        statsOutputStream.writtenBytesCount == 1
        statsOutputStream.timeFirstByteWrittenMillis == predictableStartTime.millis

        when:
        statsOutputStream.write(34)
        then: "expect delegate stream to throw an exception on write"
        actualOutputStream.write(34) >> { throw new IOException() }
        and: "expect statistics collecting stream not to count failed write"
        thrown(IOException)
        statsOutputStream.writtenBytesCount == 1
        statsOutputStream.timeFirstByteWrittenMillis == predictableStartTime.millis
    }

    def "should account bytes written through #write(byte[] b)"() {
        when:
        statsOutputStream.write([1, 2] as byte[])
        then:
        statsOutputStream.writtenBytesCount == 2
        statsOutputStream.timeFirstByteWrittenMillis == predictableStartTime.millis

        when:
        statsOutputStream.write([3, 4] as byte[])
        then:
        statsOutputStream.writtenBytesCount == 4
        statsOutputStream.timeFirstByteWrittenMillis == predictableStartTime.millis
    }

    def "should not account bytes written through #write(byte[] b) that ended up with an exception"() {
        when:
        statsOutputStream.write([1, 2] as byte[])
        then: "expect delegate stream to handle write normally"
        actualOutputStream.write([1, 2] as byte[]) >> {}
        and:
        statsOutputStream.writtenBytesCount == 2
        statsOutputStream.timeFirstByteWrittenMillis == predictableStartTime.millis

        when:
        statsOutputStream.write([3, 4] as byte[])
        then: "expect delegate stream to throw an exception on write"
        actualOutputStream.write([3, 4] as byte[]) >> { throw new IOException() }
        and: "expect statistics collecting stream not to count failed write"
        thrown(IOException)
        statsOutputStream.writtenBytesCount == 2
        statsOutputStream.timeFirstByteWrittenMillis == predictableStartTime.millis
    }

    def "should account bytes written through #write(byte[] b, int off, int len)"() {
        when:
        statsOutputStream.write([1, 2, 3] as byte[], 0, 2)
        then:
        statsOutputStream.writtenBytesCount == 2
        statsOutputStream.timeFirstByteWrittenMillis == predictableStartTime.millis

        when:
        statsOutputStream.write([4, 5, 6] as byte[], 0, 3)
        then:
        statsOutputStream.writtenBytesCount == 5
        statsOutputStream.timeFirstByteWrittenMillis == predictableStartTime.millis
    }

    def "should not account bytes written through #write(byte[] b, int off, int len) that ended up with an exception"() {
        when:
        statsOutputStream.write([1, 2, 3] as byte[], 0, 2)
        then: "expect delegate stream to handle write normally"
        actualOutputStream.write([1, 2, 3] as byte[], 0, 2) >> {}
        and:
        statsOutputStream.writtenBytesCount == 2
        statsOutputStream.timeFirstByteWrittenMillis == predictableStartTime.millis

        when:
        statsOutputStream.write([4, 5, 6] as byte[], 0, 3)
        then: "expect delegate stream to throw an exception on write"
        actualOutputStream.write([4, 5, 6] as byte[], 0, 3) >> { throw new IOException() }
        and: "expect statistics collecting stream not to count failed write"
        thrown(IOException)
        statsOutputStream.writtenBytesCount == 2
        statsOutputStream.timeFirstByteWrittenMillis == predictableStartTime.millis
    }

    def "should not overflow on Integer.MAX_VALUE-length write"() {
        when:
        statsOutputStream.write([] as byte[], 0, Integer.MAX_VALUE)
        then:
        statsOutputStream.writtenBytesCount == Integer.MAX_VALUE

        when:
        statsOutputStream.write([] as byte[], 0, Integer.MAX_VALUE)
        then:
        statsOutputStream.writtenBytesCount == 2L * Integer.MAX_VALUE
    }

    @Unroll
    def "should delegate invocations of #methodSignature"() {
        given:
        def dummyArguments = dummyArgumentsFor(method as Method)

        when:
        method.invoke(statsOutputStream, dummyArguments)
        then:
        1 * actualOutputStream._ >> { IMockInvocation mockInvocation ->
            assert mockInvocation.method.method == method
            assert mockInvocation.arguments == dummyArguments as List
        }
        0 * _

        where:
        method << allPublicMethodsOf(OutputStream)
        methodSignature = signatureOf(method as Method)
    }
}
