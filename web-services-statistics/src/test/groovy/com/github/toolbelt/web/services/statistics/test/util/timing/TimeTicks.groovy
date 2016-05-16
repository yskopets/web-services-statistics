package com.github.toolbelt.web.services.statistics.test.util.timing

/**
 * Represents a sequence of time ticks.
 *
 * <p>Can be used as for both {@code millisecond} ticks as for {@code nanosecond} ticks.</p>
 *
 * @author <a href="mailto:y.skopets@gmail.com">Yaroslav Skopets</a>
 */
interface TimeTicks {

    long next()
}