package com.github.toolbelt.web.services.statistics.support.io

import com.github.toolbelt.web.services.statistics.test.util.decorator.DecoratorTestingCapable
import org.spockframework.mock.IMockInvocation
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

import java.lang.reflect.Method

/**
 * Design spec for {@link DelegatingOutputStream}.
 *
 * @author <a href="mailto:y.skopets@gmail.com">Yaroslav Skopets</a>
 */
class DelegatingOutputStreamSpec extends Specification implements DecoratorTestingCapable {

    def delegate = Mock(OutputStream)

    @Subject
    def delegatingOutputStream = new DelegatingOutputStream(delegate)

    @Unroll
    def "should delegate invocations of #methodSignature"() {
        given:
        def dummyArguments = dummyArgumentsFor(method as Method)

        when:
        method.invoke(delegatingOutputStream, dummyArguments)
        then:
        1 * delegate._ >> { IMockInvocation mockInvocation ->
            assert mockInvocation.method.method == method
            assert mockInvocation.arguments == dummyArguments as List
        }
        0 * _

        where:
        method << allPublicMethodsOf(OutputStream)
        methodSignature = signatureOf(method as Method)
    }
}
