package com.github.toolbelt.web.services.statistics.test.given

import com.github.toolbelt.web.services.statistics.RequestStatistics
import com.github.toolbelt.web.services.statistics.support.json.RequestStatisticsMapperFactory
import com.github.toolbelt.web.services.statistics.test.util.json.JsonOperationsCapable
import com.springsource.insight.intercept.operation.Operation
import com.springsource.insight.intercept.trace.Frame
import com.fasterxml.jackson.databind.ObjectMapper

/**
 * Collection of utility methods for serializing {@link RequestStatistics} objects.
 *
 * @author <a href="mailto:y.skopets@gmail.com">Yaroslav Skopets</a>
 */
trait StatisticsSerializationCapable implements PredictableTimingCapable, JsonOperationsCapable {

    ObjectMapper statisticsMapper = RequestStatisticsMapperFactory.INSTANCE.get().with {
        setTimeZone(predictableTimeZone)
        return it
    }

    String jsonOf(RequestStatistics statistics) {
        compactJsonOf(statistics)
    }

    String jsonOf(Frame frame) {
        compactJsonOf(frame)
    }

    String jsonOf(Operation operation) {
        compactJsonOf(operation)
    }

    private String compactJsonOf(Object object) {
        statisticsMapper.writeValueAsString(object)
    }
}
