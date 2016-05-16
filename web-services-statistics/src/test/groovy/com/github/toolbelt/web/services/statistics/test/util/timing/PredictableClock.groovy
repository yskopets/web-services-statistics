package com.github.toolbelt.web.services.statistics.test.util.timing

import com.github.toolbelt.web.services.statistics.timing.Clock
import org.joda.time.DateTime
import org.joda.time.ReadableInstant

import java.util.concurrent.TimeUnit

import static com.github.toolbelt.web.services.statistics.test.util.timing.HandyTimeTicks.periodicMilliTicks

/**
 * Implementation of {@link Clock} abstraction convenient for use in unit tests.
 *
 * @author <a href="mailto:y.skopets@gmail.com">Yaroslav Skopets</a>
 *
 * @see com.github.toolbelt.web.services.statistics.timing.SystemClock
 */
class PredictableClock implements Clock {

    final TimeTicks timeTicks

    PredictableClock(String moment, int step, TimeUnit stepTimeUnit) {
        this(DateTime.parse(moment), step, stepTimeUnit)
    }

    PredictableClock(ReadableInstant moment, int step, TimeUnit stepTimeUnit) {
        this(periodicMilliTicks(moment.getMillis(), step, stepTimeUnit))
    }

    PredictableClock(TimeTicks timeTicks) {
        this.timeTicks = timeTicks
    }

    @Override
    long currentTimeMillis() {
        timeTicks.next()
    }
}
