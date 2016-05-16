package com.github.toolbelt.web.services.statistics.collector.impl;

import com.github.toolbelt.web.services.statistics.collector.ResponseDescriptionBuilder;
import com.github.toolbelt.web.services.statistics.response.ResponseMetaInfo;
import com.github.toolbelt.web.services.statistics.response.ResponseMetric;
import com.github.toolbelt.web.services.statistics.trace.ResponseTrace;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import java.util.Map;

import static com.github.toolbelt.web.services.statistics.RequestStatistics.ResponseKeys.MetaInfo;
import static com.github.toolbelt.web.services.statistics.RequestStatistics.ResponseKeys.Metrics;
import static com.github.toolbelt.web.services.statistics.RequestStatistics.ResponseKeys.Trace;
import static java.util.Collections.unmodifiableMap;

/**
 * Default implementation of {@link ResponseDescriptionBuilder}.
 *
 * @author <a href="mailto:y.skopets@gmail.com">Yaroslav Skopets</a>
 */
public class DefaultResponseDescriptionBuilder implements ResponseDescriptionBuilder {

    /** Notice: we don't use {@link ImmutableMap} here since we do want to support {@code null} values. */
    private final Map<ResponseMetaInfo, Object> responseMetaInfo = Maps.newLinkedHashMap();

    /** Notice: we don't use {@link ImmutableMap} here since we do want to support {@code null} values. */
    private final Map<ResponseMetric, Object> responseMetrics = Maps.newLinkedHashMap();

    private ResponseTrace responseTrace;

    @Override
    public ResponseDescriptionBuilder withMetaInfo(ResponseMetaInfo metaInfo, Object value) {
        responseMetaInfo.put(metaInfo, value);
        return this;
    }

    @Override
    public ResponseDescriptionBuilder withMetric(ResponseMetric metric, Object value) {
        responseMetrics.put(metric, value);
        return this;
    }

    @Override
    public ResponseDescriptionBuilder withTrace(ResponseTrace responseTrace) {
        this.responseTrace = responseTrace;
        return this;
    }

    protected ImmutableMap getData() {
        final ImmutableMap.Builder<Object, Object> builder = ImmutableMap.builder()
                .put(MetaInfo, unmodifiableMap(responseMetaInfo))
                .put(Metrics, unmodifiableMap(responseMetrics));
        // Guava collections don't allow `null` elements
        if (responseTrace != null) {
            builder.put(Trace, responseTrace);
        }
        return builder.build();
    }
}
