package com.github.toolbelt.web.services.statistics.timing;

/**
 * Default implementation of {@link Clock} abstraction backed by the standard {@link System#currentTimeMillis()} facility.
 *
 * @author <a href="mailto:y.skopets@gmail.com">Yaroslav Skopets</a>
 */
public class SystemClock implements Clock {

    /**
     * Singleton instance of {@link SystemClock}.
     */
    public static SystemClock INSTANCE = new SystemClock();

    @Override
    public long currentTimeMillis() {
        return System.currentTimeMillis();
    }
}
