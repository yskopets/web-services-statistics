package com.github.toolbelt.web.services.statistics.support.json.mixin;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Configures {@code Java -> JSON} mapping of {@link com.springsource.insight.intercept.trace.FrameId} type.
 *
 * @author <a href="mailto:y.skopets@gmail.com">Yaroslav Skopets</a>
 */
@TurnOffJsonAutoDetect
public interface FrameIdMixin {

    @JsonProperty
    long getId();
}
