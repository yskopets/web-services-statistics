package com.github.toolbelt.web.services.statistics.support.convert;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.toolbelt.web.services.statistics.RequestStatistics;
import com.github.toolbelt.web.services.statistics.support.json.RequestStatisticsMapperFactory;

/**
 * {@link RequestStatistics} -> {@code JSON DataSource} converter.
 *
 * @author <a href="mailto:y.skopets@gmail.com">Yaroslav Skopets</a>
 */
public class RequestStatisticsToJsonDataSourceConverter extends ObjectToJsonDataSourceConverter<RequestStatistics> {

    public RequestStatisticsToJsonDataSourceConverter() {
        this(RequestStatisticsMapperFactory.INSTANCE.get());
    }

    public RequestStatisticsToJsonDataSourceConverter(ObjectMapper objectMapper) {
        super(objectMapper);
    }
}
