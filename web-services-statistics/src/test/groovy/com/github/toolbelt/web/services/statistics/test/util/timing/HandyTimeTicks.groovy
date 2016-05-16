package com.github.toolbelt.web.services.statistics.test.util.timing

import java.util.concurrent.TimeUnit

/**
 * Collection of utility methods for dealing with {@link TimeTicks}.
 *
 * @author <a href="mailto:y.skopets@gmail.com">Yaroslav Skopets</a>
 */
class HandyTimeTicks {

    static TimeTicks periodicMilliTicks(long sinceMillis, long step, TimeUnit stepTimeUnit) {
        periodicTicks(sinceMillis, stepTimeUnit.toMillis(step))
    }

    static TimeTicks periodicNanoTicks(long sinceNanos, long step, TimeUnit stepTimeUnit) {
        periodicTicks(sinceNanos, stepTimeUnit.toNanos(step))
    }

    static TimeTicks periodicTicks(long sinceMoment, long step) {
        def lastTick = null
        return { ->
            if (lastTick == null) {
                lastTick = sinceMoment
            } else {
                lastTick += step
            }
            lastTick
        }
    }
}
