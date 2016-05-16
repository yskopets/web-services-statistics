package com.github.toolbelt.web.services.statistics.collector;

import com.github.toolbelt.web.services.statistics.request.RequestMetaInfo;

/**
 * {@code Fluent API} for describing the {@code Request} that is being processed.
 *
 * @author <a href="mailto:y.skopets@gmail.com">Yaroslav Skopets</a>
 */
public interface RequestDescriptionBuilder {

    /**
     * Associates given {@code meta-info} with the {@code Request}.
     *
     * @param metaInfo type of request {@code meta-info}
     * @param value datum
     * @return this {@code Builder}
     */
    RequestDescriptionBuilder withMetaInfo(RequestMetaInfo metaInfo, Object value);

    /**
     * Returns a {@code Builder} of the {@code Resource} description.
     *
     * @return {@code Builder} of the {@code Resource} description
     */
    ResourceDescriptionBuilder requestForResource();
}
