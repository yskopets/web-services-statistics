package com.github.toolbelt.web.services.statistics.response.metainfo;

import com.github.toolbelt.web.services.statistics.response.ResponseMetaInfo;

/**
 * Enumerates generic points of response {@code meta-info}.
 *
 * @author <a href="mailto:y.skopets@gmail.com">Yaroslav Skopets</a>
 */
public enum GenericResponseMetaInfo implements ResponseMetaInfo {

    /**
     * Identity of the server instance that processed the request.
     */
    ProcessingNode,

    /**
     * Value of the response {@code Status Code}.
     */
    StatusCode,

    /**
     * Value of the {@code Content-Type} response header.
     */
    ContentType,

    /**
     * Value of the {@code Content-Length} response header.
     */
    ContentLength
}
