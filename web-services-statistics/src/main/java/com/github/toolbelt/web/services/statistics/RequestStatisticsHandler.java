package com.github.toolbelt.web.services.statistics;

/**
 * A {@code Gateway Interface} in terms of {@code Integration Patterns} and {@code Spring Integration} in
 * particular.
 *
 * <p>Hides away from the caller usage of {@code Spring Integration} to process collected {@link RequestStatistics},</p>
 *
 * @author <a href="mailto:y.skopets@gmail.com">Yaroslav Skopets</a>
 */
public interface RequestStatisticsHandler {

    /**
     * Handles collected statistics, e.g. percolates and persists it.
     *
     * @param requestStatistics collected statistics
     */
    void handle(RequestStatistics requestStatistics);
}
