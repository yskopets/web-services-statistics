package com.github.toolbelt.web.services.statistics.response.metrics;

import com.github.toolbelt.web.services.statistics.response.ResponseMetric;

/**
 * Enumerates generic response {@code metrics}.
 *
 * @author <a href="mailto:y.skopets@gmail.com">Yaroslav Skopets</a>
 */
public enum GenericResponseMetric implements ResponseMetric {

    /**
     * Total request processing time (in milliseconds).
     */
    TotalProcessingTimeMillis,

    /**
     * Total number of bytes written to {@link javax.servlet.http.HttpServletResponse#getOutputStream() HttpServletResponse#getOutputStream()}.
     *
     * <p>Notice that this value might differ from
     * {@link com.github.toolbelt.web.services.statistics.response.metainfo.GenericResponseMetaInfo#ContentLength ContentLength}.</p>
     */
    ResponseBodyWrittenBytesCount,

    /**
     * Total number of characters written to {@link javax.servlet.http.HttpServletResponse#getWriter() HttpServletResponse#getWriter()}.
     *
     * <p>Notice that this value might differ from
     * {@link com.github.toolbelt.web.services.statistics.response.metainfo.GenericResponseMetaInfo#ContentLength ContentLength}.</p>
     */
    ResponseBodyWrittenCharsCount,

    /**
     * Time till the first byte of the response (in milliseconds).
     */
    TimeTillFirstResponseByteMillis
}
