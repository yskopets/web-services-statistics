package com.github.toolbelt.web.services.statistics.test.util.environment

/**
 * Sets system property {@code line.separator}, which is implicitly used by {@code JDK} classes such as
 * {@link java.io.BufferedWriter}, to a fixed value {@link PredictableLineSeparatorCapable#CRLF CRLF}.
 *
 * @author <a href="mailto:y.skopets@gmail.com">Yaroslav Skopets</a>
 */
trait PredictableLineSeparatorCapable extends RestoreSystemPropertiesCapable {

    static final String CRLF = '\r\n'

    @Override
    void afterOriginalSystemPropertiesGotRemembered() {
        System.setProperty('line.separator', CRLF)
    }
}