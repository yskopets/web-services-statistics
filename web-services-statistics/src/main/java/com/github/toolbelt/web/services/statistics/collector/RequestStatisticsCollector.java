package com.github.toolbelt.web.services.statistics.collector;

import com.github.toolbelt.web.services.statistics.RequestStatistics;

/**
 * Entry point into the {@code Fluent API} for collecting {@code Advanced Statistics}.
 *
 * @author <a href="mailto:y.skopets@gmail.com">Yaroslav Skopets</a>
 */
public interface RequestStatisticsCollector {

    /**
     * Returns globally unique identifier of this request.
     *
     * <p>Notice: Such identifier can be used to correlate {@code Advanced Statistics}
     * with information from other log files, such as {@code Troubleshooting Log}.</p>
     *
     * @return globally unique identifier of this request
     */
    String uniqueId();

    /**
     * Checks if the request relates to a {@code Resource} that is being monitored.
     *
     * @return {@code true} if the request was recognized as a request for a {@code Resource} that is being monitored,
     *         {@code false} otherwise
     */
    boolean isResourceRecognized();

    /**
     * Returns a {@code Builder} of the {@code Request} description.
     *
     * @return {@code Builder} of the {@code Request} description
     */
    RequestDescriptionBuilder describeRequest();

    /**
     * Returns a {@code Builder} of the {@code Response} description.
     *
     * @return {@code Builder} of the {@code Response} description
     */
    ResponseDescriptionBuilder describeResponse();

    /**
     * Builds a {@link RequestStatistics} instance out of information collected so far.
     *
     * @return {@link RequestStatistics} instance that aggregates all information collected so far
     */
    RequestStatistics summarizeCollectedStatistics();
}
