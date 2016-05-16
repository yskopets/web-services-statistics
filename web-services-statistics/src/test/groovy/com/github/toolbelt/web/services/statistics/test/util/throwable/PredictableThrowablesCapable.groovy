package com.github.toolbelt.web.services.statistics.test.util.throwable

/**
 * Collection of utility methods for dealing with {@code Throwable}s in unit tests.
 *
 * <p>Notice: Not a {@code trait} since {@code Spock} doesn't handle it very well.</p>
 *
 * @author <a href="mailto:y.skopets@gmail.com">Yaroslav Skopets</a>
 */
class PredictableThrowablesCapable {

    static def <T extends Throwable> T withoutStackTrace(T throwable) {
        withPredictableStackTrace(throwable, 0)
    }

    static def <T extends Throwable> T withPredictableStackTrace(T throwable, int depth = 1) {
        def originalStackTrace = throwable.stackTrace
        def predictableStackTrace = originalStackTrace.toList().subList(0, Math.min(originalStackTrace.length, depth))
        throwable.stackTrace = predictableStackTrace
        throwable
    }
}
