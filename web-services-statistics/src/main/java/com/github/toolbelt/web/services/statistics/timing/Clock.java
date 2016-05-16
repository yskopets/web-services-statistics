package com.github.toolbelt.web.services.statistics.timing;

/**
 * Represents a source of time, inherently enables unit testing of time-related application behaviour.
 *
 * @author <a href="mailto:y.skopets@gmail.com">Yaroslav Skopets</a>
 *
 * @see SystemClock
 */
public interface Clock {

    /**
     * Returns the current time in milliseconds.
     *
     * @return  the difference, measured in milliseconds, between
     *          the current time and midnight, January 1, 1970 UTC.
     *
     * @see System#currentTimeMillis()
     */
    long currentTimeMillis();
}
