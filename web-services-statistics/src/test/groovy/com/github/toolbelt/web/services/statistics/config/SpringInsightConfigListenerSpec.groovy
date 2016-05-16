package com.github.toolbelt.web.services.statistics.config

import org.springframework.instrument.classloading.SimpleThrowawayClassLoader
import org.springframework.mock.web.MockServletContext
import spock.lang.Specification
import spock.lang.Subject

import javax.servlet.ServletContextEvent

import static com.github.toolbelt.web.services.statistics.config.SpringInsightConfigListener.MAX_FRAMES_PER_TRACE_SYSTEM_PROPERTY

/**
 * Design spec for {@link SpringInsightConfigListener}.
 *
 * @author <a href="mailto:y.skopets@gmail.com">Yaroslav Skopets</a>
 */
class SpringInsightConfigListenerSpec extends Specification {

    def springInsightClassloader = new SimpleThrowawayClassLoader(SpringInsightConfigListenerSpec.classLoader)

    @Subject
    def configListener = new SpringInsightConfigListener(springInsightClassloader)

    def setup() {
        System.clearProperty(MAX_FRAMES_PER_TRACE_SYSTEM_PROPERTY)
    }

    def "should skip initialization if servlet context param 'insight.max.frames' is missing"() {
        given:
        def contextEvent = new ServletContextEvent(new MockServletContext())

        when:
        configListener.contextInitialized(contextEvent)

        then:
        System.getProperty(MAX_FRAMES_PER_TRACE_SYSTEM_PROPERTY) == null
    }

    def "should properly initialize `MAX_FRAMES_PER_TRACE` setting if servlet context param 'insight.max.frames' has a valid value"() {
        given:
        def servletContext = new MockServletContext()
        def contextEvent = new ServletContextEvent(servletContext)
        and:
        servletContext.setInitParameter(MAX_FRAMES_PER_TRACE_SYSTEM_PROPERTY, '20000')

        when:
        configListener.contextInitialized(contextEvent)

        then:
        configListener.effectiveMaxFramesPerTrace == 20000
        and:
        System.getProperty(MAX_FRAMES_PER_TRACE_SYSTEM_PROPERTY) == '20000'
    }

    def "should fail if `MAX_FRAMES_PER_TRACE` setting has already been initialized"() {
        given:
        def servletContext = new MockServletContext()
        def contextEvent = new ServletContextEvent(servletContext)
        and:
        servletContext.setInitParameter(MAX_FRAMES_PER_TRACE_SYSTEM_PROPERTY, '20000')

        when: "force premature initialization of `MAX_FRAMES_PER_TRACE` setting"
        def defaultValue = configListener.effectiveMaxFramesPerTrace
        and:
        configListener.contextInitialized(contextEvent)

        then:
        def exception = thrown(IllegalStateException)
        exception.message == "Failed to set-up 'MAX_FRAMES_PER_TRACE' config parameter. Effective value is [$defaultValue]" as String
        and:
        configListener.effectiveMaxFramesPerTrace == defaultValue
        and:
        System.getProperty(MAX_FRAMES_PER_TRACE_SYSTEM_PROPERTY) == '20000'
    }

    def "should fail if servlet context param 'insight.max.frames' has a non-integer value"() {
        given:
        def servletContext = new MockServletContext()
        def contextEvent = new ServletContextEvent(servletContext)
        and:
        servletContext.setInitParameter(MAX_FRAMES_PER_TRACE_SYSTEM_PROPERTY, 'anything-but-integer-value')

        when:
        configListener.contextInitialized(contextEvent)

        then:
        thrown(NumberFormatException)
        and:
        System.getProperty(MAX_FRAMES_PER_TRACE_SYSTEM_PROPERTY) == null
    }

    def cleanup() {
        System.clearProperty(MAX_FRAMES_PER_TRACE_SYSTEM_PROPERTY)
    }
}
