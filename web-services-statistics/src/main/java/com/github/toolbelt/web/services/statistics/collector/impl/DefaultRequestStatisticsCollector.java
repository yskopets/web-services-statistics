package com.github.toolbelt.web.services.statistics.collector.impl;

import com.github.toolbelt.web.services.statistics.RequestStatistics;
import com.github.toolbelt.web.services.statistics.collector.RequestDescriptionBuilder;
import com.github.toolbelt.web.services.statistics.collector.RequestStatisticsCollector;
import com.github.toolbelt.web.services.statistics.collector.ResponseDescriptionBuilder;
import com.github.toolbelt.web.services.statistics.support.bson.ObjectId;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;

import static com.github.toolbelt.web.services.statistics.RequestStatistics.RootKeys.FormatVersion;
import static com.github.toolbelt.web.services.statistics.RequestStatistics.RootKeys.Request;
import static com.github.toolbelt.web.services.statistics.RequestStatistics.RootKeys.Response;
import static com.github.toolbelt.web.services.statistics.RequestStatistics.RootKeys.Uid;

/**
 * Default implementation of {@link RequestStatisticsCollector}.
 *
 * @author <a href="mailto:y.skopets@gmail.com">Yaroslav Skopets</a>
 */
public class DefaultRequestStatisticsCollector implements RequestStatisticsCollector {

    /**
     * Notice: this value should be incremented every time when structure of the {@code Map} returned by {@link #summarizeCollectedStatistics()} changes.
     */
    private static final int FRAMEWORK_LEVEL_FORMAT_VERSION = 1;

    private static final int DEFAULT_APPLICATION_LEVEL_FORMAT_VERSION = 1;

    private static final int[] DEFAULT_FORMAT_VERSION = new int[] {FRAMEWORK_LEVEL_FORMAT_VERSION, DEFAULT_APPLICATION_LEVEL_FORMAT_VERSION};

    private static final RequestStatistics EMPTY_STATISTICS = new DefaultRequestStatistics(ImmutableMap.of());

    private final DefaultRequestDescriptionBuilder requestDescriptionBuilder = new DefaultRequestDescriptionBuilder();

    private final DefaultResponseDescriptionBuilder responseDescriptionBuilder = new DefaultResponseDescriptionBuilder();

    private final String uniqueId;

    /** Combination of the internal {@link #FRAMEWORK_LEVEL_FORMAT_VERSION} and an application-level version. */
    private final int[] formatVersion;

    public DefaultRequestStatisticsCollector() {
        this(DEFAULT_APPLICATION_LEVEL_FORMAT_VERSION);
    }

    public DefaultRequestStatisticsCollector(int applicationLevelFormatVersion) {
        this(ObjectId.get().toString(), applicationLevelFormatVersion);
    }

    @VisibleForTesting
    public DefaultRequestStatisticsCollector(String uniqueId) {
        this(uniqueId, DEFAULT_APPLICATION_LEVEL_FORMAT_VERSION);
    }

    @VisibleForTesting
    public DefaultRequestStatisticsCollector(String uniqueId, int applicationLevelFormatVersion) {
        this.uniqueId = uniqueId;
        this.formatVersion = applicationLevelFormatVersion == DEFAULT_APPLICATION_LEVEL_FORMAT_VERSION ?
                DEFAULT_FORMAT_VERSION : new int[] {FRAMEWORK_LEVEL_FORMAT_VERSION, applicationLevelFormatVersion};
    }

    @Override
    public String uniqueId() {
        return uniqueId;
    }

    @Override
    public boolean isResourceRecognized() {
        return requestDescriptionBuilder.isResourceRecognized();
    }

    @Override
    public RequestDescriptionBuilder describeRequest() {
        return requestDescriptionBuilder;
    }

    @Override
    public ResponseDescriptionBuilder describeResponse() {
        return responseDescriptionBuilder;
    }

    @Override
    public RequestStatistics summarizeCollectedStatistics() {
        if (isResourceRecognized()) {
            final ImmutableMap map = ImmutableMap.of(
                FormatVersion, formatVersion,
                Request, requestDescriptionBuilder.getData(),
                // Note: we want {@link UNIQUE_ID} to appear in the log file after {@link REQUEST_DESCRIPTOR}
                // to avoid breaking out-of-the-box sorting by {@code RequestTime}
                Uid, uniqueId,
                Response, responseDescriptionBuilder.getData()
            );
            return new DefaultRequestStatistics(map);
        } else {
            return EMPTY_STATISTICS;
        }
    }
}
