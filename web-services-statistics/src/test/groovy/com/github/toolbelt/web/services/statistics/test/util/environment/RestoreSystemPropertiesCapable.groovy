package com.github.toolbelt.web.services.statistics.test.util.environment

import org.junit.After
import org.junit.Before

/**
 * Reproduces functional of
 * {@link org.spockframework.runtime.extension.builtin.RestoreSystemPropertiesInterceptor RestoreSystemPropertiesInterceptor}
 * in the form of a {@code Groovy Trait}.
 *
 * @author <a href="mailto:y.skopets@gmail.com">Yaroslav Skopets</a>
 *
 * @see org.spockframework.runtime.extension.builtin.RestoreSystemPropertiesInterceptor
 */
trait RestoreSystemPropertiesCapable {

    private originalSystemProperties = new Properties()

    @Before
    void rememberOriginalSystemProperties() {
        originalSystemProperties.putAll(System.getProperties())

        afterOriginalSystemPropertiesGotRemembered()
    }

    void afterOriginalSystemPropertiesGotRemembered() {
    }

    @After
    void restoreOriginalSystemProperties() {
        System.setProperties(originalSystemProperties)
    }
}