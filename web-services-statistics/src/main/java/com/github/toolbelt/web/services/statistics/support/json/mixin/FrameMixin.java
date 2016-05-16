package com.github.toolbelt.web.services.statistics.support.json.mixin;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.springsource.insight.intercept.operation.Operation;
import com.springsource.insight.intercept.trace.Frame;
import com.springsource.insight.intercept.trace.FrameId;
import com.springsource.insight.util.time.TimeRange;

import java.util.List;

/**
 * Configures {@code Java -> JSON} mapping of {@link com.springsource.insight.intercept.trace.Frame} type.
 *
 * @author <a href="mailto:y.skopets@gmail.com">Yaroslav Skopets</a>
 */
@TurnOffJsonAutoDetect
public interface FrameMixin {

    @JsonProperty
    @JsonUnwrapped
    FrameId getId();

    @JsonProperty
    Operation getOperation();

    @JsonProperty
    TimeRange getRange();

    @JsonProperty
    List<Frame> getChildren();
}
