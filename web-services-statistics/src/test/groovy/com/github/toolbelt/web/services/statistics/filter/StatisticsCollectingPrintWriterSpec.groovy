package com.github.toolbelt.web.services.statistics.filter

import com.github.toolbelt.web.services.statistics.test.given.PredictableTimingCapable
import com.github.toolbelt.web.services.statistics.test.util.decorator.DecoratorTestingCapable
import com.github.toolbelt.web.services.statistics.test.util.environment.PredictableLineSeparatorCapable
import groovy.io.GroovyPrintWriter
import org.spockframework.mock.IMockInvocation
import org.springframework.mock.web.MockHttpServletResponse
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

import java.lang.reflect.Method
import java.util.concurrent.TimeUnit

import static java.util.Locale.ENGLISH

/**
 * Design spec for {@link StatisticsCollectingPrintWriter}.
 *
 * @author <a href="mailto:y.skopets@gmail.com">Yaroslav Skopets</a>
 */
class StatisticsCollectingPrintWriterSpec extends Specification
        implements DecoratorTestingCapable, PredictableTimingCapable, PredictableLineSeparatorCapable {

    @Subject
    StatisticsCollectingPrintWriter statsPrintWriter

    def setup() {
        // Notice: we can't initialize {@code #statsPrintWriter} as part of field declaration
        // since {@link PredictableLineSeparatorCapable} hasn't been applied yet
        statsPrintWriter = new StatisticsCollectingPrintWriter(new PrintWriter(Mock(Writer)), predictableClock, PrintWriter)
    }

    @Unroll
    def "should delegate invocations of #methodSignature"() {
        given:
        def actualPrintWriter = Mock(PrintWriter)
        statsPrintWriter = new StatisticsCollectingPrintWriter(actualPrintWriter, predictableClock, PrintWriter)
        and:
        def dummyArguments = dummyArgumentsFor(method as Method)

        when:
        method.invoke(statsPrintWriter, dummyArguments)
        then:
        1 * actualPrintWriter._ >> { IMockInvocation mockInvocation ->
            assert mockInvocation.method.method == method
            assert mockInvocation.arguments == dummyArguments as List
        }
        0 * _

        where:
        method << allPublicMethodsOf(PrintWriter)
        methodSignature = signatureOf(method as Method)
    }

    @Unroll
    def "should return itself as a result of #methodSignature"() {
        given:
        def actualPrintWriter = Mock(PrintWriter)
        statsPrintWriter = new StatisticsCollectingPrintWriter(actualPrintWriter, predictableClock, PrintWriter)
        and:
        def dummyArguments = dummyArgumentsFor(method as Method)

        expect:
        method.invoke(statsPrintWriter, dummyArguments).is(statsPrintWriter)

        where:
        method << allPublicMethodsOf(PrintWriter).findAll { it.returnType == PrintWriter }
        methodSignature = signatureOf(method as Method)
    }

    @Unroll
    def "should not collect statistics from a PrintWriter that hasn't been previously inspected: #unknownPrintWriterName"() {
        given:
        statsPrintWriter = new StatisticsCollectingPrintWriter(unknownPrintWriter, predictableClock, PrintWriter)

        expect:
        !statsPrintWriter.statisticsCollected

        where:
        unknownPrintWriter << [Mock(PrintWriter), new GroovyPrintWriter(Mock(PrintWriter)), new MockHttpServletResponse().getWriter()]
        unknownPrintWriterName = unknownPrintWriter.class.name
    }

    @Unroll
    def "should collect statistics from a PrintWriter that has been previously inspected and configured explicitly: #supportedPrintWriterName"() {
        given:
        statsPrintWriter = new StatisticsCollectingPrintWriter(supportedPrintWriter, predictableClock, supportedPrintWriter.class)

        expect:
        statsPrintWriter.statisticsCollected

        where:
        supportedPrintWriter << [new GroovyPrintWriter(Mock(PrintWriter)), new MockHttpServletResponse().getWriter()]
        supportedPrintWriterName = supportedPrintWriter.class.name
    }

    def "should account 0 written characters if statistics wasn't collected"() {
        given:
        statsPrintWriter = new StatisticsCollectingPrintWriter(Mock(PrintWriter), predictableClock, PrintWriter)

        when:
        statsPrintWriter.append('this content will not be reflected in the statistics')
        then:
        statsPrintWriter.writtenCharsCount == 0
        statsPrintWriter.timeFirstCharWrittenMillis == null
    }

    def "should account characters written through #write(int c)"() {
        when:
        statsPrintWriter.write(12)
        then:
        statsPrintWriter.writtenCharsCount == 1
        statsPrintWriter.timeFirstCharWrittenMillis == predictableStartTime.millis

        when:
        statsPrintWriter.write(34)
        then:
        statsPrintWriter.writtenCharsCount == 2
        statsPrintWriter.timeFirstCharWrittenMillis == predictableStartTime.millis
    }

    def "should account characters written through #write(char[] buf)"() {
        when:
        statsPrintWriter.write([12, 34] as char[])
        then:
        statsPrintWriter.writtenCharsCount == 2
        statsPrintWriter.timeFirstCharWrittenMillis == predictableStartTime.millis

        when:
        statsPrintWriter.write([56, 78, 90] as char[])
        then:
        statsPrintWriter.writtenCharsCount == 5
        statsPrintWriter.timeFirstCharWrittenMillis == predictableStartTime.millis
    }

    def "should account characters written through #write(char[] buf, int off, int len)"() {
        when:
        statsPrintWriter.write([12, 34] as char[], 0, 1)
        then:
        statsPrintWriter.writtenCharsCount == 1
        statsPrintWriter.timeFirstCharWrittenMillis == predictableStartTime.millis

        when:
        statsPrintWriter.write([56, 78, 90] as char[], 1, 2)
        then:
        statsPrintWriter.writtenCharsCount == 3
        statsPrintWriter.timeFirstCharWrittenMillis == predictableStartTime.millis
    }

    def "should account characters written through #write(String s)"() {
        when:
        statsPrintWriter.write('12')
        then:
        statsPrintWriter.writtenCharsCount == 2
        statsPrintWriter.timeFirstCharWrittenMillis == predictableStartTime.millis

        when:
        statsPrintWriter.write('345')
        then:
        statsPrintWriter.writtenCharsCount == 5
        statsPrintWriter.timeFirstCharWrittenMillis == predictableStartTime.millis
    }

    def "should account characters written through #write(String s, int off, int len)"() {
        when:
        statsPrintWriter.write('12', 0, 1)
        then:
        statsPrintWriter.writtenCharsCount == 1
        statsPrintWriter.timeFirstCharWrittenMillis == predictableStartTime.millis

        when:
        statsPrintWriter.write('345', 1, 2)
        then:
        statsPrintWriter.writtenCharsCount == 3
        statsPrintWriter.timeFirstCharWrittenMillis == predictableStartTime.millis
    }

    def "should account characters written through #append(CharSequence csq)"() {
        when:
        statsPrintWriter.append('append' as CharSequence)
        then:
        statsPrintWriter.writtenCharsCount == 6
        statsPrintWriter.timeFirstCharWrittenMillis == predictableStartTime.millis

        when:
        statsPrintWriter.append('CharSequence' as CharSequence)
        then:
        statsPrintWriter.writtenCharsCount == 18
        statsPrintWriter.timeFirstCharWrittenMillis == predictableStartTime.millis
    }

    def "should account characters written through #append(CharSequence csq, int start, int end)"() {
        when:
        statsPrintWriter.append('#append(CharSequence csq)' as CharSequence, 1, 7)
        then:
        statsPrintWriter.writtenCharsCount == 6
        statsPrintWriter.timeFirstCharWrittenMillis == predictableStartTime.millis

        when:
        statsPrintWriter.append('#append(CharSequence csq)' as CharSequence, 8, 20)
        then:
        statsPrintWriter.writtenCharsCount == 18
        statsPrintWriter.timeFirstCharWrittenMillis == predictableStartTime.millis
    }

    def "should account characters written through #append(char c)"() {
        when:
        statsPrintWriter.append('c' as char)
        then:
        statsPrintWriter.writtenCharsCount == 1
        statsPrintWriter.timeFirstCharWrittenMillis == predictableStartTime.millis

        when:
        statsPrintWriter.append('A' as char)
        then:
        statsPrintWriter.writtenCharsCount == 2
        statsPrintWriter.timeFirstCharWrittenMillis == predictableStartTime.millis
    }

    def "should account characters written through #print(boolean b)"() {
        when:
        statsPrintWriter.print(true)
        then:
        statsPrintWriter.writtenCharsCount == 4
        statsPrintWriter.timeFirstCharWrittenMillis == predictableStartTime.millis

        when:
        statsPrintWriter.print(false)
        then:
        statsPrintWriter.writtenCharsCount == 9
        statsPrintWriter.timeFirstCharWrittenMillis == predictableStartTime.millis
    }

    def "should account characters written through #print(char c)"() {
        when:
        statsPrintWriter.print('c' as char)
        then:
        statsPrintWriter.writtenCharsCount == 1
        statsPrintWriter.timeFirstCharWrittenMillis == predictableStartTime.millis

        when:
        statsPrintWriter.print('a' as char)
        then:
        statsPrintWriter.writtenCharsCount == 2
        statsPrintWriter.timeFirstCharWrittenMillis == predictableStartTime.millis
    }

    def "should account characters written through #print(int i)"() {
        when:
        statsPrintWriter.print(123)
        then:
        statsPrintWriter.writtenCharsCount == 3
        statsPrintWriter.timeFirstCharWrittenMillis == predictableStartTime.millis

        when:
        statsPrintWriter.print(4567)
        then:
        statsPrintWriter.writtenCharsCount == 7
        statsPrintWriter.timeFirstCharWrittenMillis == predictableStartTime.millis
    }

    def "should account characters written through #print(long l)"() {
        when:
        statsPrintWriter.print(123L)
        then:
        statsPrintWriter.writtenCharsCount == 3
        statsPrintWriter.timeFirstCharWrittenMillis == predictableStartTime.millis

        when:
        statsPrintWriter.print(4567L)
        then:
        statsPrintWriter.writtenCharsCount == 7
        statsPrintWriter.timeFirstCharWrittenMillis == predictableStartTime.millis
    }

    def "should account characters written through #print(float f)"() {
        when:
        statsPrintWriter.print(123f)
        then:
        statsPrintWriter.writtenCharsCount == 5
        statsPrintWriter.timeFirstCharWrittenMillis == predictableStartTime.millis

        when:
        statsPrintWriter.print(4567f)
        then:
        statsPrintWriter.writtenCharsCount == 11
        statsPrintWriter.timeFirstCharWrittenMillis == predictableStartTime.millis
    }

    def "should account characters written through #print(double d)"() {
        when:
        statsPrintWriter.print(123d)
        then:
        statsPrintWriter.writtenCharsCount == 5
        statsPrintWriter.timeFirstCharWrittenMillis == predictableStartTime.millis

        when:
        statsPrintWriter.print(4567d)
        then:
        statsPrintWriter.writtenCharsCount == 11
        statsPrintWriter.timeFirstCharWrittenMillis == predictableStartTime.millis
    }

    def "should account characters written through #print(char[] s)"() {
        when:
        statsPrintWriter.print('print' as char[])
        then:
        statsPrintWriter.writtenCharsCount == 5
        statsPrintWriter.timeFirstCharWrittenMillis == predictableStartTime.millis

        when:
        statsPrintWriter.print('char[] s' as char[])
        then:
        statsPrintWriter.writtenCharsCount == 13
        statsPrintWriter.timeFirstCharWrittenMillis == predictableStartTime.millis
    }

    def "should account characters written through #print(String s)"() {
        when:
        statsPrintWriter.print('print')
        then:
        statsPrintWriter.writtenCharsCount == 5
        statsPrintWriter.timeFirstCharWrittenMillis == predictableStartTime.millis

        when:
        statsPrintWriter.print('char[] s')
        then:
        statsPrintWriter.writtenCharsCount == 13
        statsPrintWriter.timeFirstCharWrittenMillis == predictableStartTime.millis
    }

    def "should account characters written through #print(Object obj)"() {
        when:
        statsPrintWriter.print(TimeUnit.DAYS)
        then:
        statsPrintWriter.writtenCharsCount == 4
        statsPrintWriter.timeFirstCharWrittenMillis == predictableStartTime.millis

        when:
        statsPrintWriter.print(TimeUnit.NANOSECONDS)
        then:
        statsPrintWriter.writtenCharsCount == 15
        statsPrintWriter.timeFirstCharWrittenMillis == predictableStartTime.millis
    }

    def "should account characters written through #println()"() {
        when:
        statsPrintWriter.println()
        then:
        statsPrintWriter.writtenCharsCount == 2
        statsPrintWriter.timeFirstCharWrittenMillis == predictableStartTime.millis

        when:
        statsPrintWriter.println()
        then:
        statsPrintWriter.writtenCharsCount == 4
        statsPrintWriter.timeFirstCharWrittenMillis == predictableStartTime.millis
    }

    def "should account characters written through #println(boolean b)"() {
        when:
        statsPrintWriter.println(true)
        then:
        statsPrintWriter.writtenCharsCount == 6
        statsPrintWriter.timeFirstCharWrittenMillis == predictableStartTime.millis

        when:
        statsPrintWriter.println(false)
        then:
        statsPrintWriter.writtenCharsCount == 13
        statsPrintWriter.timeFirstCharWrittenMillis == predictableStartTime.millis
    }

    def "should account characters written through #println(char c)"() {
        when:
        statsPrintWriter.println('c' as char)
        then:
        statsPrintWriter.writtenCharsCount == 3
        statsPrintWriter.timeFirstCharWrittenMillis == predictableStartTime.millis

        when:
        statsPrintWriter.println('a' as char)
        then:
        statsPrintWriter.writtenCharsCount == 6
        statsPrintWriter.timeFirstCharWrittenMillis == predictableStartTime.millis
    }

    def "should account characters written through #println(int i)"() {
        when:
        statsPrintWriter.println(123)
        then:
        statsPrintWriter.writtenCharsCount == 5
        statsPrintWriter.timeFirstCharWrittenMillis == predictableStartTime.millis

        when:
        statsPrintWriter.println(4567)
        then:
        statsPrintWriter.writtenCharsCount == 11
        statsPrintWriter.timeFirstCharWrittenMillis == predictableStartTime.millis
    }

    def "should account characters written through #println(long l)"() {
        when:
        statsPrintWriter.println(123L)
        then:
        statsPrintWriter.writtenCharsCount == 5
        statsPrintWriter.timeFirstCharWrittenMillis == predictableStartTime.millis

        when:
        statsPrintWriter.println(4567L)
        then:
        statsPrintWriter.writtenCharsCount == 11
        statsPrintWriter.timeFirstCharWrittenMillis == predictableStartTime.millis
    }

    def "should account characters written through #println(float f)"() {
        when:
        statsPrintWriter.println(123f)
        then:
        statsPrintWriter.writtenCharsCount == 7
        statsPrintWriter.timeFirstCharWrittenMillis == predictableStartTime.millis

        when:
        statsPrintWriter.println(4567f)
        then:
        statsPrintWriter.writtenCharsCount == 15
        statsPrintWriter.timeFirstCharWrittenMillis == predictableStartTime.millis
    }

    def "should account characters written through #println(double d)"() {
        when:
        statsPrintWriter.println(123d)
        then:
        statsPrintWriter.writtenCharsCount == 7
        statsPrintWriter.timeFirstCharWrittenMillis == predictableStartTime.millis

        when:
        statsPrintWriter.println(4567d)
        then:
        statsPrintWriter.writtenCharsCount == 15
        statsPrintWriter.timeFirstCharWrittenMillis == predictableStartTime.millis
    }

    def "should account characters written through #println(char[] s)"() {
        when:
        statsPrintWriter.println('print' as char[])
        then:
        statsPrintWriter.writtenCharsCount == 7
        statsPrintWriter.timeFirstCharWrittenMillis == predictableStartTime.millis

        when:
        statsPrintWriter.println('char[] s' as char[])
        then:
        statsPrintWriter.writtenCharsCount == 17
        statsPrintWriter.timeFirstCharWrittenMillis == predictableStartTime.millis
    }

    def "should account characters written through #println(String s)"() {
        when:
        statsPrintWriter.println('print')
        then:
        statsPrintWriter.writtenCharsCount == 7
        statsPrintWriter.timeFirstCharWrittenMillis == predictableStartTime.millis

        when:
        statsPrintWriter.println('char[] s')
        then:
        statsPrintWriter.writtenCharsCount == 17
        statsPrintWriter.timeFirstCharWrittenMillis == predictableStartTime.millis
    }

    def "should account characters written through #println(Object obj)"() {
        when:
        statsPrintWriter.println(TimeUnit.DAYS)
        then:
        statsPrintWriter.writtenCharsCount == 6
        statsPrintWriter.timeFirstCharWrittenMillis == predictableStartTime.millis

        when:
        statsPrintWriter.println(TimeUnit.NANOSECONDS)
        then:
        statsPrintWriter.writtenCharsCount == 19
        statsPrintWriter.timeFirstCharWrittenMillis == predictableStartTime.millis
    }

    def "should account characters written through #printf(String format, Object... args)"() {
        when:
        statsPrintWriter.printf('%s(%s)', 'printf', 'String format, Object... args')
        then:
        statsPrintWriter.writtenCharsCount == 37
        statsPrintWriter.timeFirstCharWrittenMillis == predictableStartTime.millis

        when:
        statsPrintWriter.printf('%d', 123)
        then:
        statsPrintWriter.writtenCharsCount == 40
        statsPrintWriter.timeFirstCharWrittenMillis == predictableStartTime.millis
    }

    def "should account characters written through #printf(Locale l, String format, Object... args)"() {
        when:
        statsPrintWriter.printf(ENGLISH, '%s(%s)', 'printf', 'Locale l, String format, Object... args')
        then:
        statsPrintWriter.writtenCharsCount == 47
        statsPrintWriter.timeFirstCharWrittenMillis == predictableStartTime.millis

        when:
        statsPrintWriter.printf(ENGLISH, '%d', 123)
        then:
        statsPrintWriter.writtenCharsCount == 50
        statsPrintWriter.timeFirstCharWrittenMillis == predictableStartTime.millis
    }

    def "should account characters written through #format(String format, Object ... args)"() {
        when:
        statsPrintWriter.printf('%s(%s)', 'format', 'String format, Object ... args')
        then:
        statsPrintWriter.writtenCharsCount == 38
        statsPrintWriter.timeFirstCharWrittenMillis == predictableStartTime.millis

        when:
        statsPrintWriter.printf('%d', 123)
        then:
        statsPrintWriter.writtenCharsCount == 41
        statsPrintWriter.timeFirstCharWrittenMillis == predictableStartTime.millis
    }

    def "should account characters written through #format(Locale l, String format, Object ... args)"() {
        when:
        statsPrintWriter.printf(ENGLISH, '%s(%s)', 'format', 'Locale l, String format, Object ... args')
        statsPrintWriter.timeFirstCharWrittenMillis == predictableStartTime.millis
        then:
        statsPrintWriter.writtenCharsCount == 48

        when:
        statsPrintWriter.printf(ENGLISH, '%d', 123)
        then:
        statsPrintWriter.writtenCharsCount == 51
        statsPrintWriter.timeFirstCharWrittenMillis == predictableStartTime.millis
    }

    def "should not overflow on Integer.MAX_VALUE-length write"() {
        when:
        statsPrintWriter.write([] as char[], 0, Integer.MAX_VALUE)
        then:
        statsPrintWriter.writtenCharsCount == Integer.MAX_VALUE

        when:
        statsPrintWriter.write([] as char[], 0, Integer.MAX_VALUE)
        then:
        statsPrintWriter.writtenCharsCount == 2L * Integer.MAX_VALUE
    }

    @Unroll
    def "should not override behaviour of the actual PrintWriter: #actualPrintWriterName"() {
        given:
        def statsPrintWriter = new StatisticsCollectingPrintWriter(actualPrintWriter, predictableClock, actualPrintWriter.class)

        when:
        statsPrintWriter.print(object)
        then:
        statsPrintWriter.writtenCharsCount == expectedWrittenCharsCount

        where:
        actualPrintWriter                   | object                 || expectedWrittenCharsCount
        new PrintWriter(Mock(Writer))       | Collections.emptyMap() || 2 // #toString() methods of the empty map returns `{}`
        new GroovyPrintWriter(Mock(Writer)) | Collections.emptyMap() || 3 // Groovy representation of the empty map is `[:]` even though original #toString() methods returns `{}`

        actualPrintWriterName = actualPrintWriter.class.name
    }
}
