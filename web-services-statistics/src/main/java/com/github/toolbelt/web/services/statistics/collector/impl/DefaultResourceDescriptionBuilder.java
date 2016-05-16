package com.github.toolbelt.web.services.statistics.collector.impl;

import com.github.toolbelt.web.services.statistics.collector.ResourceDescriptionBuilder;
import com.github.toolbelt.web.services.statistics.resource.ResourceParameter;
import com.github.toolbelt.web.services.statistics.resource.ResourceType;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import java.util.Map;

import static com.github.toolbelt.web.services.statistics.RequestStatistics.ResourceKeys.Parameters;
import static com.github.toolbelt.web.services.statistics.RequestStatistics.ResourceKeys.Type;
import static java.util.Collections.unmodifiableMap;

/**
 * Default implementation of {@link ResourceDescriptionBuilder}.
 *
 * @author <a href="mailto:y.skopets@gmail.com">Yaroslav Skopets</a>
 */
public class DefaultResourceDescriptionBuilder implements ResourceDescriptionBuilder {

    private ResourceType resourceType;

    /** Notice: we don't use {@link ImmutableMap} here since we do want to support {@code null} values. */
    private final Map<ResourceParameter, Object> resourceParameters = Maps.newLinkedHashMap();

    @Override
    public ResourceDescriptionBuilder ofType(ResourceType resourceType) {
        this.resourceType = resourceType;
        return this;
    }

    @Override
    public ResourceDescriptionBuilder withParameter(ResourceParameter resourceParameter, Object value) {
        resourceParameters.put(resourceParameter, value);
        return this;
    }

    protected boolean isTypeRecognized() {
        return resourceType != null;
    }

    protected ImmutableMap getData() {
        return ImmutableMap.of(
            Type, resourceType,
            Parameters, unmodifiableMap(resourceParameters));
    }

    @Override
    public ResourceType getResourceType() {
        return this.resourceType;
    }
}
