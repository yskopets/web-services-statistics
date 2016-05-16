package com.github.toolbelt.web.services.statistics.support.io

import com.github.toolbelt.web.services.statistics.test.util.decorator.DecoratorTestingCapable
import org.spockframework.mock.IMockInvocation
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

import java.lang.reflect.Method

/**
 * Design spec for {@link DelegatingPrintWriter}.
 *
 * @author <a href="mailto:y.skopets@gmail.com">Yaroslav Skopets</a>
 */
class DelegatingPrintWriterSpec extends Specification implements DecoratorTestingCapable {

    def delegate = Mock(PrintWriter)

    @Subject
    def delegatingPrintWriter = new DelegatingPrintWriter(delegate)

    @Unroll
    def "should delegate invocations of #methodSignature"() {
        given:
        def dummyArguments = dummyArgumentsFor(method as Method)

        when:
        method.invoke(delegatingPrintWriter, dummyArguments)
        then:
        1 * delegate._ >> { IMockInvocation mockInvocation ->
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
        def dummyArguments = dummyArgumentsFor(method as Method)

        expect:
        method.invoke(delegatingPrintWriter, dummyArguments).is(delegatingPrintWriter)

        where:
        method << allPublicMethodsOf(PrintWriter).findAll { it.returnType == PrintWriter }
        methodSignature = signatureOf(method as Method)
    }
}
