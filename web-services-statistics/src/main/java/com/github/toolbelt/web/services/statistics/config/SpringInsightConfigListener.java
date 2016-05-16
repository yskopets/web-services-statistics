package com.github.toolbelt.web.services.statistics.config;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.lang.reflect.Field;

/**
 * Responsible for proper initialization of the embedded {@code Spring Insight Core}.
 *
 * <p>Warning: to have the desired effect this class MUST be loaded before {@code Spring Insight Core}.
 *
 * Essentially, this is a workaround for the fact that some parameters of {@code Spring Insight Core} get initialized
 * by means of {@code final static} fields.</p>
 *
 * @author <a href="mailto:y.skopets@gmail.com">Yaroslav Skopets</a>
 */
public class SpringInsightConfigListener implements ServletContextListener {

    private static final Logger LOG = LoggerFactory.getLogger(SpringInsightConfigListener.class);

    /**
     * System property through which {@code Spring Insight} can be configured with a custom {@code Maximum Number of Frames per Trace}.
     */
    @VisibleForTesting
    public static final String MAX_FRAMES_PER_TRACE_SYSTEM_PROPERTY = "insight.max.frames";

    /**
     * Name of the servlet context parameter through which {@code SpringInsightConfigListener} can be configured
     * with a custom {@code Maximum Number of Frames per Trace}.
     */
    public static final String MAX_FRAMES_PER_TRACE_PARAM = MAX_FRAMES_PER_TRACE_SYSTEM_PROPERTY;

    private static final String INITIALIZER_CLASS_NAME = "com.springsource.insight.intercept.trace.FrameBuilder";

    private static final String INITIALIZER_FIELD_NAME = "MAX_FRAMES_PER_TRACE";

    private final ClassLoader springInsightClassLoader;

    public SpringInsightConfigListener() {
        this(SpringInsightConfigListener.class.getClassLoader());
    }

    @VisibleForTesting
    public SpringInsightConfigListener(ClassLoader springInsightClassLoader) {
        this.springInsightClassLoader = springInsightClassLoader;
    }

    @Override
    public void contextInitialized(ServletContextEvent event) {
        final ServletContext servletContext = event.getServletContext();
        final String param = servletContext.getInitParameter(MAX_FRAMES_PER_TRACE_PARAM);
        if (!Strings.isNullOrEmpty(param)) {
            final int maxFramesPerTrace = Integer.parseInt(param);

            System.setProperty(MAX_FRAMES_PER_TRACE_SYSTEM_PROPERTY, String.valueOf(maxFramesPerTrace));

            final int effectiveMaxFramesPerTrace = getEffectiveMaxFramesPerTrace();

            if (effectiveMaxFramesPerTrace != maxFramesPerTrace) {
                final String message = String.format("Failed to set-up '%s' config parameter. Effective value is [%s]",
                        INITIALIZER_FIELD_NAME, effectiveMaxFramesPerTrace);
                LOG.error(message);
                throw new IllegalStateException(message);
            } else {
                LOG.info("'{}' has been set up to [{}]", INITIALIZER_FIELD_NAME, effectiveMaxFramesPerTrace);
            }
        } else {
            LOG.warn("{} is defined in web.xml but required context-param '{}' is missing",
                    SpringInsightConfigListener.class.getSimpleName(), MAX_FRAMES_PER_TRACE_PARAM);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
    }

    @VisibleForTesting
    public int getEffectiveMaxFramesPerTrace() {
        try {
            final Class<?> frameBuilderClass = ClassUtils.forName(INITIALIZER_CLASS_NAME, springInsightClassLoader);
            final Field maxFramesPerTraceField = ReflectionUtils.findField(frameBuilderClass, INITIALIZER_FIELD_NAME);
            return (Integer) maxFramesPerTraceField.get(null);
        } catch (ClassNotFoundException e) {
            throw Throwables.propagate(e);
        } catch (LinkageError e) {
            throw Throwables.propagate(e);
        } catch (IllegalAccessException e) {
            throw Throwables.propagate(e);
        }
    }
}
