package com.github.toolbelt.web.services.statistics.support.io

import com.github.toolbelt.web.services.statistics.test.util.decorator.DecoratorTestingCapable
import org.spockframework.mock.IMockInvocation
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

import javax.servlet.ServletOutputStream
import java.lang.reflect.Method

/**
 * Design spec for {@link BridgingServletOutputStream}.
 *
 * @author <a href="mailto:y.skopets@gmail.com">Yaroslav Skopets</a>
 */
class BridgingServletOutputStreamSpec extends Specification implements DecoratorTestingCapable {

    def outputStream = Mock(OutputStream)

    @Subject
    def bridgingStream = new BridgingServletOutputStream(outputStream)

    @Unroll
    def "should delegate invocations of #methodSignature"() {
        given:
        def dummyArguments = dummyArgumentsFor(method as Method)

        when:
        method.invoke(bridgingStream, dummyArguments)
        then:
        1 * outputStream._ >> { IMockInvocation mockInvocation ->
            assert mockInvocation.method.method == method
            assert mockInvocation.arguments == dummyArguments as List
        }
        0 * _

        where:
        method << allPublicMethodsOf(OutputStream)
        methodSignature = signatureOf(method as Method)
    }

    @Unroll
    def "should handle invocations of #methodSignature on its own"() {
        given:
        def dummyArguments = dummyArgumentsFor(method as Method)

        when:
        method.invoke(bridgingStream, dummyArguments)
        then:
        (1.._) * outputStream.write(_)
        0 * _

        where:
        method << allPublicMethodsOf(ServletOutputStream) - allPublicMethodsFromSuperclass(ServletOutputStream, OutputStream)
        methodSignature = signatureOf(method as Method)
    }
}
