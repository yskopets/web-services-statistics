package com.github.toolbelt.web.services.statistics.support.json;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.github.toolbelt.web.services.statistics.RequestStatistics;
import com.google.common.base.Supplier;

import java.util.TimeZone;

/**
 * Constructs an {@link ObjectMapper} capable of converting {@link RequestStatistics} objects.
 *
 * <p>Most importantly, includes mapping configuration for {@code Spring Insight} internal classes.
 *
 * @author <a href="mailto:y.skopets@gmail.com">Yaroslav Skopets</a>
 */
public class RequestStatisticsMapperFactory implements Supplier<ObjectMapper> {

    /**
     * Singleton instance of {@link RequestStatisticsMapperFactory}.
     */
    public static RequestStatisticsMapperFactory INSTANCE = new RequestStatisticsMapperFactory();

    @Override
    public ObjectMapper get() {
        final ObjectMapper objectMapper = new ObjectMapper();

        addCustomModules(objectMapper);
        configure(objectMapper);

        return objectMapper;
    }

    protected void addCustomModules(ObjectMapper objectMapper) {
        objectMapper
                .registerModule(newSpringInsightModule())
                .registerModule(newJodaModule());
    }

    protected Module newSpringInsightModule() {
        return new SpringInsightModule();
    }

    protected Module newJodaModule() {
        return new JodaModule();
    }

    protected void configure(ObjectMapper objectMapper) {
        // we want resulting json documents to be human readable
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        // we want to see dates formatted with {@code local time zone} rather than {@code UTC}
        objectMapper.setTimeZone(TimeZone.getDefault());
    }
}
