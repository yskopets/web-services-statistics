package com.github.toolbelt.web.services.statistics.filter;

import com.github.toolbelt.web.services.statistics.timing.Clock;
import com.google.common.net.HttpHeaders;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Represents a {@link javax.servlet.http.HttpServletResponseWrapper HttpServletResponseWrapper} that is responsible
 * for collection of generic response {@code meta-info} (e.g., {@code Status Code} and {@code Content Type})
 * and generic response {@code metrics} (e.g., {@code Time Till First Response Byte in Millis}
 * and {@code Response Body Written Bytes Count}).
 *
 * @author <a href="mailto:y.skopets@gmail.com">Yaroslav Skopets</a>
 */
public class StatisticsCollectingResponseWrapper extends HttpServletResponseWrapper {

    private final Clock clock;

    private String lastSetContentType;

    private Integer lastSetContentLength;

    private StatisticsCollectingServletOutputStream outputStream;

    private StatisticsCollectingPrintWriter writer;

    /**
     * Constructs a response adaptor wrapping the given response.
     *
     * @param response original {@link HttpServletResponse} instance
     * @param clock    source of time
     * @throws IllegalArgumentException if the response is null
     */
    public StatisticsCollectingResponseWrapper(HttpServletResponse response, Clock clock) {
        super(response);
        this.clock = clock;
    }

    // > response content type detection

    @Override
    public void setContentType(String type) {
        super.setContentType(type);
        setLastContentType(type);
    }

    @Override
    public void addHeader(String name, String value) {
        super.addHeader(name, value);
        if (HttpHeaders.CONTENT_TYPE.equalsIgnoreCase(name)) {
            setLastContentType(value);
        } else if (HttpHeaders.CONTENT_LENGTH.equalsIgnoreCase(name)) {
            setLastContentLength(value);
        }
    }

    @Override
    public void setHeader(String name, String value) {
        super.setHeader(name, value);
        if (HttpHeaders.CONTENT_TYPE.equalsIgnoreCase(name)) {
            setLastContentType(value);
        } else if (HttpHeaders.CONTENT_LENGTH.equalsIgnoreCase(name)) {
            setLastContentLength(value);
        }
    }

    // < response content type detection

    // > response content length detection

    @Override
    public void setContentLength(int len) {
        super.setContentLength(len);
        lastSetContentLength = len;
    }

    // < response content length detection

    // > response body written bytes count detection

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        if (outputStream == null) {
            outputStream = new StatisticsCollectingServletOutputStream(super.getOutputStream(), clock);
        }
        return outputStream;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        if (writer == null) {
            writer = new StatisticsCollectingPrintWriter(super.getWriter(), clock);
        }
        return writer;
    }

    // < response body written bytes count detection

    @Override
    public String getContentType() {
        final String contentType = super.getContentType();
        return contentType != null ? contentType : this.lastSetContentType;
    }

    public Integer getContentLength() {
        return lastSetContentLength;
    }

    public long getWrittenBytesCount() {
        if (outputStream != null) {
            return outputStream.getWrittenBytesCount();
        }
        return 0;
    }

    public long getWrittenCharsCount() {
        if (writer != null) {
            return writer.getWrittenCharsCount();
        }
        return 0;
    }

    public Long getTimeFirstByteWrittenMillis() {
        if (outputStream != null) {
            return outputStream.getTimeFirstByteWrittenMillis();
        }
        if (writer != null) {
            return writer.getTimeFirstCharWrittenMillis();
        }
        return null;
    }

    protected void setLastContentType(String type) {
        this.lastSetContentType = type;
    }

    protected void setLastContentLength(String value) {
        try {
            this.lastSetContentLength = Integer.valueOf(value);
        } catch (NumberFormatException e) {
            this.lastSetContentLength = null;
        }
    }
}
