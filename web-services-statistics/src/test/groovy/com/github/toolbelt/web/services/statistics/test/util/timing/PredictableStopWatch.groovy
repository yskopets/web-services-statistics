package com.github.toolbelt.web.services.statistics.test.util.timing

import com.springsource.insight.util.time.StopWatch

/**
 * Implementation of {@link StopWatch} abstraction convenient for use in unit tests.
 *
 * @author <a href="mailto:y.skopets@gmail.com">Yaroslav Skopets</a>
 *
 * @see com.springsource.insight.util.time.NanoStopWatch
 */
class PredictableStopWatch implements StopWatch {

    boolean started

    long startNanoOffset

    long startNanos

    long lastMark

    final TimeTicks nanoTicks

    PredictableStopWatch(TimeTicks nanoTicks) {
        this.nanoTicks = nanoTicks
    }

    @Override
    void start(long nanoStart) {
        if (started) {
            throw new IllegalStateException("start() called, but already running")
        }
        started = true

        startNanoOffset = nanoTicks.next()
        // Notice: we intentionally ignore passed-in {@code nanoStart} time
        nanoStart = startNanoOffset
        startNanos = nanoStart
        lastMark = nanoStart

    }

    @Override
    long mark() {
        if (!started) {
            throw new IllegalStateException("mark() called, but not started")
        }

        long endNanoOffset = nanoTicks.next()
        long duration = endNanoOffset - startNanoOffset

        long endNanos = startNanos + duration

        // Ensure that 2 successive calls return increasing values.
        if (endNanos <= lastMark) {
            endNanos = lastMark + 1
        }
        lastMark = endNanos
        return lastMark
    }
}
