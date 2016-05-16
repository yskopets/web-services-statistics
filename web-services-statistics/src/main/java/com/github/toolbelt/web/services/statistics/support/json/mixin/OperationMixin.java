package com.github.toolbelt.web.services.statistics.support.json.mixin;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.springsource.insight.intercept.operation.OperationType;

import java.util.Map;

/**
 * Configures {@code Java -> JSON} mapping of {@link com.springsource.insight.intercept.operation.Operation} type.
 *
 * @author <a href="mailto:y.skopets@gmail.com">Yaroslav Skopets</a>
 */
@TurnOffJsonAutoDetect
public abstract class OperationMixin {

    @JsonProperty
    @JsonUnwrapped
    abstract OperationType getType();

    @JsonProperty
    abstract String getLabel();

    @JsonProperty
    Map<String, Object> properties;
}
