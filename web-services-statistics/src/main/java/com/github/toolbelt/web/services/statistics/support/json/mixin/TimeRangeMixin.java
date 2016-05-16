package com.github.toolbelt.web.services.statistics.support.json.mixin;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Configures {@code Java -> JSON} mapping of {@link com.springsource.insight.util.time.TimeRange} type.
 *
 * @author <a href="mailto:y.skopets@gmail.com">Yaroslav Skopets</a>
 */
@TurnOffJsonAutoDetect
public interface TimeRangeMixin {

    @JsonProperty
    long getStart();

    @JsonProperty
    long getEnd();

    @JsonProperty
    long getDurationMillis();
}
