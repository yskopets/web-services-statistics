package com.github.toolbelt.web.services.statistics.support.io

import com.github.toolbelt.web.services.statistics.test.util.decorator.DecoratorTestingCapable
import org.spockframework.mock.IMockInvocation
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

import java.lang.reflect.Method

/**
 * Design spec for {@link DelegatingWriter}.
 *
 * @author <a href="mailto:y.skopets@gmail.com">Yaroslav Skopets</a>
 */
class DelegatingWriterSpec extends Specification implements DecoratorTestingCapable {

    def delegate = Mock(Writer)

    @Subject
    DelegatingWriter delegatingWriter

    @Unroll
    def "should delegate invocations of #methodSignature when `delegateMethodsOfAppendable` == `true`"() {
        given:
        delegatingWriter = new DelegatingWriter(delegate)
        and:
        def dummyArguments = dummyArgumentsFor(method as Method)

        when:
        method.invoke(delegatingWriter, dummyArguments)
        then:
        1 * delegate._ >> { IMockInvocation mockInvocation ->
            assert mockInvocation.method.method == method
            assert mockInvocation.arguments == dummyArguments as List
        }
        0 * _

        where:
        method << allPublicMethodsOf(Writer)
        methodSignature = signatureOf(method as Method)
    }

    @Unroll
    def "should return itself as a result of #methodSignature when `delegateMethodsOfAppendable` == `true`"() {
        given:
        delegatingWriter = new DelegatingWriter(delegate)
        and:
        def dummyArguments = dummyArgumentsFor(method as Method)

        expect:
        method.invoke(delegatingWriter, dummyArguments).is(delegatingWriter)

        where:
        method << allPublicMethodsOf(Writer).findAll { it.returnType == Writer }
        methodSignature = signatureOf(method as Method)
    }

    @Unroll
    def "should delegate invocations of #methodSignature when `delegateMethodsOfAppendable` == `false`"() {
        given:
        delegatingWriter = new DelegatingWriter(delegate, false)
        and:
        def dummyArguments = dummyArgumentsFor(method as Method)

        when:
        method.invoke(delegatingWriter, dummyArguments)
        then:
        1 * delegate._ >> { IMockInvocation mockInvocation ->
            assert mockInvocation.method.method == method
            assert mockInvocation.arguments == dummyArguments as List
        }
        0 * _

        where:
        method << allPublicMethodsOf(Writer) - allPublicMethodsFromSuperclass(Writer, Appendable)
        methodSignature = signatureOf(method as Method)
    }

    def "should handle invocation of #append(CharSequence csq) on its own when `delegateMethodsOfAppendable` == `false`"() {
        given:
        delegatingWriter = new DelegatingWriter(delegate, false)

        when:
        delegatingWriter.append('#append(CharSequence csq)' as CharSequence)
        then:
        1 * delegate.write('#append(CharSequence csq)')
        0 * _
    }

    def "should handle invocation of #append(CharSequence csq, int start, int end) on its own when `delegateMethodsOfAppendable` == `false`"() {
        given:
        delegatingWriter = new DelegatingWriter(delegate, false)

        when:
        delegatingWriter.append('#append(CharSequence csq)' as CharSequence, 8, 20)
        then:
        1 * delegate.write('CharSequence')
        0 * _
    }

    def "should handle invocation of #append(char c) on its own when `delegateMethodsOfAppendable` == `false`"() {
        given:
        delegatingWriter = new DelegatingWriter(delegate, false)

        when:
        delegatingWriter.append('c' as char)
        then:
        1 * delegate.write('c'.charAt(0))
        0 * _
    }

    @Unroll
    def "should return itself as a result of #methodSignature when `delegateMethodsOfAppendable` == `false`"() {
        given:
        delegatingWriter = new DelegatingWriter(delegate, false)
        and:
        def dummyArguments = dummyArgumentsFor(method as Method)

        expect:
        method.invoke(delegatingWriter, dummyArguments).is(delegatingWriter)

        where:
        method << allPublicMethodsOf(Writer).findAll { it.returnType == Writer }
        methodSignature = signatureOf(method as Method)
    }
}
