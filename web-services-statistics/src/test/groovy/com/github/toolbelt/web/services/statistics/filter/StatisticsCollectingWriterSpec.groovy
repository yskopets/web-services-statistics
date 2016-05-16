package com.github.toolbelt.web.services.statistics.filter

import com.github.toolbelt.web.services.statistics.test.given.PredictableTimingCapable
import com.github.toolbelt.web.services.statistics.test.util.decorator.DecoratorTestingCapable
import org.spockframework.mock.IMockInvocation
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

import java.lang.reflect.Method

/**
 * Design spec for {@link StatisticsCollectingWriter}.
 *
 * @author <a href="mailto:y.skopets@gmail.com">Yaroslav Skopets</a>
 */
class StatisticsCollectingWriterSpec extends Specification implements PredictableTimingCapable, DecoratorTestingCapable {

    def actualWriter = Mock(Writer)

    @Subject
    def statsCollectingWriter = new StatisticsCollectingWriter(actualWriter, predictableClock)

    def "should account 0 written characters if there were no writes"() {
        expect:
        statsCollectingWriter.writtenCharsCount == 0
        statsCollectingWriter.timeFirstCharWrittenMillis == null
    }

    def "should account characters written through #write(int c)"() {
        when:
        statsCollectingWriter.write(12)
        then:
        statsCollectingWriter.writtenCharsCount == 1
        statsCollectingWriter.timeFirstCharWrittenMillis == predictableStartTime.millis

        when:
        statsCollectingWriter.write(34)
        then:
        statsCollectingWriter.writtenCharsCount == 2
        statsCollectingWriter.timeFirstCharWrittenMillis == predictableStartTime.millis
    }

    def "should account characters written through #write(char[] buf)"() {
        when:
        statsCollectingWriter.write([12, 34] as char[])
        then:
        statsCollectingWriter.writtenCharsCount == 2
        statsCollectingWriter.timeFirstCharWrittenMillis == predictableStartTime.millis

        when:
        statsCollectingWriter.write([56, 78, 90] as char[])
        then:
        statsCollectingWriter.writtenCharsCount == 5
        statsCollectingWriter.timeFirstCharWrittenMillis == predictableStartTime.millis
    }

    def "should account characters written through #write(char[] buf, int off, int len)"() {
        when:
        statsCollectingWriter.write([12, 34] as char[], 0, 1)
        then:
        statsCollectingWriter.writtenCharsCount == 1
        statsCollectingWriter.timeFirstCharWrittenMillis == predictableStartTime.millis

        when:
        statsCollectingWriter.write([56, 78, 90] as char[], 1, 2)
        then:
        statsCollectingWriter.writtenCharsCount == 3
        statsCollectingWriter.timeFirstCharWrittenMillis == predictableStartTime.millis
    }

    def "should account characters written through #write(String s)"() {
        when:
        statsCollectingWriter.write('12')
        then:
        statsCollectingWriter.writtenCharsCount == 2
        statsCollectingWriter.timeFirstCharWrittenMillis == predictableStartTime.millis

        when:
        statsCollectingWriter.write('345')
        then:
        statsCollectingWriter.writtenCharsCount == 5
        statsCollectingWriter.timeFirstCharWrittenMillis == predictableStartTime.millis
    }

    def "should account characters written through #write(String s, int off, int len)"() {
        when:
        statsCollectingWriter.write('12', 0, 1)
        then:
        statsCollectingWriter.writtenCharsCount == 1
        statsCollectingWriter.timeFirstCharWrittenMillis == predictableStartTime.millis

        when:
        statsCollectingWriter.write('345', 1, 2)
        then:
        statsCollectingWriter.writtenCharsCount == 3
        statsCollectingWriter.timeFirstCharWrittenMillis == predictableStartTime.millis
    }

    def "should account characters written through #append(CharSequence csq)"() {
        when:
        statsCollectingWriter.append('append' as CharSequence)
        then:
        statsCollectingWriter.writtenCharsCount == 6
        statsCollectingWriter.timeFirstCharWrittenMillis == predictableStartTime.millis

        when:
        statsCollectingWriter.append('CharSequence' as CharSequence)
        then:
        statsCollectingWriter.writtenCharsCount == 18
        statsCollectingWriter.timeFirstCharWrittenMillis == predictableStartTime.millis
    }

    def "should account characters written through #append(CharSequence csq, int start, int end)"() {
        when:
        statsCollectingWriter.append('#append(CharSequence csq)' as CharSequence, 1, 7)
        then:
        statsCollectingWriter.writtenCharsCount == 6
        statsCollectingWriter.timeFirstCharWrittenMillis == predictableStartTime.millis

        when:
        statsCollectingWriter.append('#append(CharSequence csq)' as CharSequence, 8, 20)
        then:
        statsCollectingWriter.writtenCharsCount == 18
        statsCollectingWriter.timeFirstCharWrittenMillis == predictableStartTime.millis
    }

    def "should account characters written through #append(char c)"() {
        when:
        statsCollectingWriter.append('c' as char)
        then:
        statsCollectingWriter.writtenCharsCount == 1
        statsCollectingWriter.timeFirstCharWrittenMillis == predictableStartTime.millis

        when:
        statsCollectingWriter.append('A' as char)
        then:
        statsCollectingWriter.writtenCharsCount == 2
        statsCollectingWriter.timeFirstCharWrittenMillis == predictableStartTime.millis
    }

    def "should not overflow on Integer.MAX_VALUE-length write"() {
        when:
        statsCollectingWriter.write([] as char[], 0, Integer.MAX_VALUE)
        then:
        statsCollectingWriter.writtenCharsCount == Integer.MAX_VALUE

        when:
        statsCollectingWriter.write([] as char[], 0, Integer.MAX_VALUE)
        then:
        statsCollectingWriter.writtenCharsCount == 2L * Integer.MAX_VALUE
    }

    @Unroll
    def "should delegate invocations of #methodSignature"() {
        given:
        def dummyArguments = dummyArgumentsFor(method as Method)

        when:
        method.invoke(statsCollectingWriter, dummyArguments)
        then:
        1 * actualWriter._ >> { IMockInvocation mockInvocation ->
            assert mockInvocation.method.method == method
            assert mockInvocation.arguments == dummyArguments as List
        }
        0 * _

        where:
        method << allPublicMethodsOf(Writer) - allPublicMethodsFromSuperclass(Writer, Appendable)
        methodSignature = signatureOf(method as Method)
    }

    def "should handle invocation of #append(CharSequence csq) on its own"() {
        when:
        statsCollectingWriter.append('#append(CharSequence csq)' as CharSequence)
        then:
        1 * actualWriter.write('#append(CharSequence csq)')
        0 * _
    }

    def "should handle invocation of #append(CharSequence csq, int start, int end) on its own"() {
        when:
        statsCollectingWriter.append('#append(CharSequence csq)' as CharSequence, 8, 20)
        then:
        1 * actualWriter.write('CharSequence')
        0 * _
    }

    def "should handle invocation of #append(char c) on its own"() {
        when:
        statsCollectingWriter.append('c' as char)
        then:
        1 * actualWriter.write('c'.charAt(0))
        0 * _
    }

    @Unroll
    def "should return itself as a result of #methodSignature"() {
        given:
        def dummyArguments = dummyArgumentsFor(method as Method)

        expect:
        method.invoke(statsCollectingWriter, dummyArguments).is(statsCollectingWriter)

        where:
        method << allPublicMethodsOf(Writer).findAll { it.returnType == Writer }
        methodSignature = signatureOf(method as Method)
    }
}
