package com.github.toolbelt.web.services.statistics.test.given

import com.github.toolbelt.web.services.statistics.RequestStatisticsHandler
import com.github.toolbelt.web.services.statistics.collector.RequestStatisticsCollector
import com.github.toolbelt.web.services.statistics.filter.StatisticsFilter
import com.github.toolbelt.web.services.statistics.timing.Clock
import com.github.toolbelt.web.services.statistics.trace.TracingSubsystem

/**
 * Factory of {@link StatisticsFilter} objects.
 *
 * @author <a href="mailto:y.skopets@gmail.com">Yaroslav Skopets</a>
 */
trait StatisticsFilterFactory {

    StatisticsFilter newStatisticsFilter(RequestStatisticsCollector statisticsCollector,
                                         RequestStatisticsHandler statisticsHandler,
                                         TracingSubsystem tracingSubsystem,
                                         Clock clock,
                                         String nodeName,
                                         String requestAttributeName) {
        def filter = new StatisticsFilter(statisticsCollector, statisticsHandler, tracingSubsystem, clock)
        filter.with {
            setServerNodeName(nodeName)
            setPublishStatisticsAsRequestAttribute(requestAttributeName)
        }
        // fulfil the contract of {@code Spring Framework}
        filter.afterPropertiesSet()
        filter
    }
}
