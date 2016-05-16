package com.github.toolbelt.web.services.statistics.support.json.mixin;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Configures {@code Java -> JSON} mapping of {@link com.springsource.insight.intercept.operation.OperationType} type.
 *
 * @author <a href="mailto:y.skopets@gmail.com">Yaroslav Skopets</a>
 */
@TurnOffJsonAutoDetect
public interface OperationTypeMixin {

    @JsonProperty("type")
    String getName();
}
