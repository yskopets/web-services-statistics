package com.github.toolbelt.web.services.statistics.collector.impl;

import com.github.toolbelt.web.services.statistics.RequestStatistics;
import com.google.common.collect.ImmutableMap;

/**
 * Default implementation of {@link RequestStatistics}.
 *
 * @author <a href="mailto:y.skopets@gmail.com">Yaroslav Skopets</a>
 */
public class DefaultRequestStatistics implements RequestStatistics {

    // Notice: we don't put any type constraints on map contents
    private final ImmutableMap data;

    public DefaultRequestStatistics(ImmutableMap data) {
        this.data = data;
    }

    @Override
    public ImmutableMap getData() {
        return data;
    }
}
