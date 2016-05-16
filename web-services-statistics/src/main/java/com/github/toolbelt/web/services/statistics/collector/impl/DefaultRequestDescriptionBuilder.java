package com.github.toolbelt.web.services.statistics.collector.impl;

import com.github.toolbelt.web.services.statistics.collector.RequestDescriptionBuilder;
import com.github.toolbelt.web.services.statistics.collector.ResourceDescriptionBuilder;
import com.github.toolbelt.web.services.statistics.request.RequestMetaInfo;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import java.util.Map;

import static com.github.toolbelt.web.services.statistics.RequestStatistics.RequestKeys.MetaInfo;
import static com.github.toolbelt.web.services.statistics.RequestStatistics.RequestKeys.Resource;
import static java.util.Collections.unmodifiableMap;

/**
 * Default implementation of {@link RequestDescriptionBuilder}.
 *
 * @author <a href="mailto:y.skopets@gmail.com">Yaroslav Skopets</a>
 */
public class DefaultRequestDescriptionBuilder implements RequestDescriptionBuilder {

    /**
     * Notice: we can't use {@link ImmutableMap} here since we want to support {@code null} values.
     */
    private final Map<RequestMetaInfo, Object> requestMetaInfo = Maps.newLinkedHashMap();

    private final DefaultResourceDescriptionBuilder resourceDescriptionBuilder = new DefaultResourceDescriptionBuilder();

    @Override
    public RequestDescriptionBuilder withMetaInfo(RequestMetaInfo metaInfo, Object value) {
        requestMetaInfo.put(metaInfo, value);
        return this;
    }

    @Override
    public ResourceDescriptionBuilder requestForResource() {
        return resourceDescriptionBuilder;
    }

    protected boolean isResourceRecognized() {
        return resourceDescriptionBuilder.isTypeRecognized();
    }

    protected ImmutableMap getData() {
        if (isResourceRecognized()) {
            return ImmutableMap.of(
                    MetaInfo, unmodifiableMap(requestMetaInfo),
                    Resource, resourceDescriptionBuilder.getData());
        } else {
            return ImmutableMap.of();
        }
    }
}
