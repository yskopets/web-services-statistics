package com.github.toolbelt.web.services.statistics.collector;

import com.github.toolbelt.web.services.statistics.response.ResponseMetaInfo;
import com.github.toolbelt.web.services.statistics.response.ResponseMetric;
import com.github.toolbelt.web.services.statistics.trace.ResponseTrace;

/**
 * {@code Fluent API} for describing the generated {@code Response}.
 *
 * <p>Can be used directly from business level in order to attach {@code Resource}-specific {@code metrics},
 * such as {@code Number Of Pages} in a dynamically generated PDF file.</p>
 *
 * @author <a href="mailto:y.skopets@gmail.com">Yaroslav Skopets</a>
 */
public interface ResponseDescriptionBuilder {

    /**
     * Associates given {@code meta-info} with the {@code Response}.
     *
     * @param metaInfo type of response {@code meta-info}
     * @param value datum
     * @return this {@code Builder}
     */
    ResponseDescriptionBuilder withMetaInfo(ResponseMetaInfo metaInfo, Object value);

    /**
     * Associates given {@code metric} with the {@code Response}.
     *
     * @param metric type of response {@code metric}
     * @param value datum
     * @return this {@code Builder}
     */
    ResponseDescriptionBuilder withMetric(ResponseMetric metric, Object value);

    /**
     * Associates profiling information with the {@code Response}.
     *
     * @param responseTrace profiling information collected by {@link com.github.toolbelt.web.services.statistics.trace.TracingSubsystem TracingSubsystem}
     * @return this {@code Builder}
     */
    ResponseDescriptionBuilder withTrace(ResponseTrace responseTrace);
}
