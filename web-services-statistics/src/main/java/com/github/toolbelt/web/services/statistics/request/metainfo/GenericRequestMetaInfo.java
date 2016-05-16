package com.github.toolbelt.web.services.statistics.request.metainfo;

import com.github.toolbelt.web.services.statistics.request.RequestMetaInfo;

/**
 * Enumerates generic points of request {@code meta-info}.
 *
 * @author <a href="mailto:y.skopets@gmail.com">Yaroslav Skopets</a>
 */
public enum GenericRequestMetaInfo implements RequestMetaInfo {

    /**
     * Time when the request was received.
     */
    RequestTime,

    /**
     * Internet Protocol (IP) address of the client or last proxy that sent the request.
     */
    ClientIPAddress,

    /**
     * Name of the user making request, if the user has been authenticated, or {@code null} if the user has not been authenticated.
     */
    UserName,

    /**
     * HTTP method of the request, such as {@code GET}, {@code POST}, {@code PUT}, etc.
     */
    RequestMethod,

    /**
     * Value of the {@code Referer} request header.
     */
    Referer,

    /**
     * Value of the {@code User-Agent} request header.
     */
    UserAgent
}
