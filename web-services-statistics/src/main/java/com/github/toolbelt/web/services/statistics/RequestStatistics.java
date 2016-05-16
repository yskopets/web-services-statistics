package com.github.toolbelt.web.services.statistics;

import com.google.common.collect.ImmutableMap;

/**
 * Represents statistical information on a processed request.
 *
 * <p>Notice: we chose the simplest form of a representation - {@code Map} structure -
 * since statistical data doesn't need too much of type safety.
 * The only requirement is to be easily serializable into {@code JSON}.</p>
 *
 * @author <a href="mailto:y.skopets@gmail.com">Yaroslav Skopets</a>
 */
public interface RequestStatistics {

    /**
     * Returns statistical data in the form of a {@code Map} with the following structure:
     *
     * <blockquote>
     * <pre>
     * {
     *   FormatVersion: ... ,
     *   Request: {
     *     MetaInfo: { ... },
     *     Resource: {
     *        Type: ... ,
     *        Parameters: { ... }
     *      }
     *   }
     *   Uid: ... ,
     *   Response: {
     *     MetaInfo: { ... },
     *     Metrics: { ... },
     *     Trace: { ... }
     *   }
     * }
     * </pre>
     * </blockquote>
     *
     * @return statistical data in the form of a {@code Map}
     */
    ImmutableMap getData();

    /** Root-level keys in the {@code Map} returned by {@link #getData()}. */
    enum RootKeys {

        FormatVersion,

        Uid,

        Request,

        Response
    }

    /** {@code Request}-level keys in the {@code Map} returned by {@link #getData()}. */
    enum RequestKeys {

        MetaInfo,

        Resource
    }

    /** {@code Resource}-level keys in the {@code Map} returned by {@link #getData()}. */
    enum ResourceKeys {

        Type,

        Parameters
    }

    /** {@code Response}-level keys in the {@code Map} returned by {@link #getData()}. */
    enum ResponseKeys {

        MetaInfo,

        Metrics,

        Trace
    }
}
