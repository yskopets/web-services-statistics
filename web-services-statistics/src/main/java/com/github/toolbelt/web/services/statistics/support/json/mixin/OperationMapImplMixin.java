package com.github.toolbelt.web.services.statistics.support.json.mixin;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * Configures {@code Java -> JSON} mapping of {@link com.springsource.insight.intercept.operation.OperationMapImpl} type.
 *
 * @author <a href="mailto:y.skopets@gmail.com">Yaroslav Skopets</a>
 */
@TurnOffJsonAutoDetect
public abstract class OperationMapImplMixin {

    @JsonProperty
    Map<String, Object> properties;
}
