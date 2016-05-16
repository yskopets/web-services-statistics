package com.github.toolbelt.web.services.statistics.collector;

import com.github.toolbelt.web.services.statistics.resource.ResourceParameter;
import com.github.toolbelt.web.services.statistics.resource.ResourceType;

/**
 * {@code Fluent API} for describing the requested {@code Resource} as it seen at business level.
 *
 * @author <a href="mailto:y.skopets@gmail.com">Yaroslav Skopets</a>
 */
public interface ResourceDescriptionBuilder {

    /**
     * Fills in a {@code Type} of the requested {@code Resource}.
     *
     * @param resourceType {@code Type} of the requested {@code Resource}
     * @return this {@code Builder}
     */
    ResourceDescriptionBuilder ofType(ResourceType resourceType);

    /**
     * Fills in a parameter of the requested {@code Resource}.
     *
     * @param parameter {@code Type} of {@code Resource Parameter}
     * @param value datum
     * @return this {@code Builder}
     */
    ResourceDescriptionBuilder withParameter(ResourceParameter parameter, Object value);

    /**
     * Returns a {@code Type} of the requested {@code Resource} or {@code null} if it hasn't been recognized yet.
     *
     * @return {@code Type} of the requested {@code Resource}
     *         or {@code null} if it hasn't been recognized yet
     */
    ResourceType getResourceType();
}
