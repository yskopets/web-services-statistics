package com.github.toolbelt.web.services.statistics.test.given

import com.github.toolbelt.web.services.statistics.test.util.timing.PredictableClock
import com.github.toolbelt.web.services.statistics.test.util.timing.PredictableStopWatch
import com.github.toolbelt.web.services.statistics.timing.Clock
import com.springsource.insight.util.time.StopWatch
import org.joda.time.DateTime

import static com.github.toolbelt.web.services.statistics.test.util.timing.HandyTimeTicks.periodicNanoTicks
import static java.util.concurrent.TimeUnit.MILLISECONDS

/**
 * Provides {@link Clock} and {@link StopWatch} with predictable behaviour.
 *
 * @author <a href="mailto:y.skopets@gmail.com">Yaroslav Skopets</a>
 */
trait PredictableTimingCapable {

    /**
     * Notice: declared {@code static} only as a workaround,
     * otherwise field initializer in {@link StatisticsSerializationCapable} was unable to see this value.
     */
    static TimeZone predictableTimeZone = TimeZone.getTimeZone('Europe/Amsterdam')

    DateTime predictableStartTime = DateTime.parse("2015-04-30T12:42:54.628+02:00")

    Clock predictableClock = new PredictableClock(predictableStartTime, 100, MILLISECONDS)

    StopWatch predictableStopWatch = new PredictableStopWatch(periodicNanoTicks(1, 10, MILLISECONDS))
}
