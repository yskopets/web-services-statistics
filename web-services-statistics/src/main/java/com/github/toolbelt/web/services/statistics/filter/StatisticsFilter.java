package com.github.toolbelt.web.services.statistics.filter;

import com.github.toolbelt.web.services.statistics.RequestStatistics;
import com.github.toolbelt.web.services.statistics.RequestStatisticsHandler;
import com.github.toolbelt.web.services.statistics.collector.RequestStatisticsCollector;
import com.github.toolbelt.web.services.statistics.request.metainfo.GenericRequestMetaInfo;
import com.github.toolbelt.web.services.statistics.response.metainfo.GenericResponseMetaInfo;
import com.github.toolbelt.web.services.statistics.response.metrics.GenericResponseMetric;
import com.github.toolbelt.web.services.statistics.timing.Clock;
import com.github.toolbelt.web.services.statistics.trace.ResponseTrace;
import com.github.toolbelt.web.services.statistics.trace.ResponseTraceCapture;
import com.github.toolbelt.web.services.statistics.trace.TracingSubsystem;
import com.github.toolbelt.web.services.statistics.trace.TracingSupport;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.net.HttpHeaders;
import com.springsource.insight.intercept.operation.Operation;
import com.springsource.insight.intercept.operation.OperationType;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * A {@link javax.servlet.Filter Filter} that gets embedded into the request processing chain and drives the process of
 * {@code Advanced Statistics Collection}.
 * <p>Filter itself is a perfect place to collect:
 * <ul>
 *     <li>Generic request {@code meta-info}, such as {@code Request Time}, {@code Client IP Address}, etc.
 *     It is exactly the same info you normally find in the {@code Apache access log} file</li>
 *     <li>Generic response {@code meta-info}, such as {@code Processing Node}, {@code Status Code},
 *     {@code Content Type}, etc</li>
 *     <li>Generic response {@code metrics}, such as {@code Time Till First Byte in Millis},
 *     {@code Total Processing Time in Millis},{@code Response Size in Bytes}, etc</li>
 * </ul>
 * <p>It's left to the business code to collect:
 * <ul>
 *     <li>Type of the requested resource, such as {@code Book Contents}, {@code Page Scan}, etc,
 *     as well as all the relevant resource parameters, such as {@code ISBN}, {@code Translation Language},
 *     {@code Page Number}, etc</li>
 *     <li>Resource-specific response {@code metrics}, such as {@code Number of Pages in the Response}
 *     (applicable when servings PDF files)</li>
 * </ul>
 * <p>With regards to {@code Trace} collection, {@link StatisticsFilter} does the following:
 * <ul>
 *     <li>registers a handler responsible for catching the {@code Trace} recorded by {@code Spring Insight} machinery</li>
 *     <li>enters the {@code Root Frame} before request processing starts (in order to guarantee
 *     that request processing always leaves a {@code Trace})</li>
 *     <li>exits the {@code Root Frame} once request processing is complete (in order to force
 *     emission of a {@code Trace})</li>
 *     <li>attaches the {@code Trace} recorded by {@code Spring Insight} to the rest of the statistics</li>
 * </ul>
 * <p>Once request processing is complete, {@link StatisticsFilter} checks if business code
 * has recognized it as a request for a monitored {@code Resource}.
 * If so, {@link StatisticsFilter} behaves as follows:
 * <ul>
 *     <li>complements statistics collected at business level with the generic one</li>
 *     <li>attaches {@link ResponseTrace} to the statistics</li>
 *     <li>passes on the resulting {@link RequestStatistics} instance to the {@code requestStatisticsHandler}</li>
 * </ul>
 * <p>Notice: {@link StatisticsFilter} is NOT responsible for what happens to the resulting
 * {@link RequestStatistics} instance next, e.g. percolation or persistence.
 * For the sake of separation of concerns, {@link StatisticsFilter} is only responsible for dispatching
 * collected statistics to the {@code requestStatisticsHandler}.
 *
 * @author <a href="mailto:y.skopets@gmail.com">Yaroslav Skopets</a>
 */
public class StatisticsFilter extends OncePerRequestFilter {

    private static final OperationType REQUEST_STATISTICS_FILTER_TYPE = OperationType.valueOf("request_statistics_filter");

    private final RequestStatisticsCollector requestStatisticsCollector;

    private final RequestStatisticsHandler requestStatisticsHandler;

    private final TracingSubsystem tracingSubsystem;

    private final TracingSupport tracingSupport;

    private final Clock clock;

    private String serverNodeName;

    private String publishStatisticsAsRequestAttribute;

    @Autowired
    public StatisticsFilter(RequestStatisticsCollector statisticsCollector, RequestStatisticsHandler statisticsHandler,
                            TracingSubsystem tracingSubsystem, Clock clock) {
        this.requestStatisticsCollector = statisticsCollector;
        this.requestStatisticsHandler = statisticsHandler;
        this.tracingSubsystem = tracingSubsystem;
        this.tracingSupport = new TracingSupport(tracingSubsystem);
        this.clock = clock;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                throws ServletException, IOException {
        final long requestTimeMillis = clock.currentTimeMillis();
        final StatisticsCollectingResponseWrapper responseWrapper = new StatisticsCollectingResponseWrapper(response, clock);
        final ResponseTraceCapture responseTraceCapture = new ResponseTraceCapture();
        boolean exceptionalFlow = true;
        try {
            // kill active trace if there is one (just for sure)
            tracingSubsystem.getFrameBuilder().dump();
            tracingSubsystem.registerTraceCapture(responseTraceCapture);
            tracingSupport.enter(new Operation().type(REQUEST_STATISTICS_FILTER_TYPE));

            filterChain.doFilter(request, responseWrapper);

            tracingSupport.exitNormal();

            exceptionalFlow = false;
        } catch (IOException e) {
            tracingSupport.exitAbnormal(e);
            throw e;
        } catch (ServletException e) {
            tracingSupport.exitAbnormal(e);
            throw e;
        } catch (Throwable e) {
            tracingSupport.exitAbnormal(e);
            throw Throwables.propagate(e);
        } finally {
            // nor further actions should affect the {@code Trace} collected so far
            tracingSubsystem.resetTraceCapture();

            describeGenericPartOfRequest(requestStatisticsCollector, request, requestTimeMillis);
            describeCustomPartOfRequest(requestStatisticsCollector, request);

            describeGenericPartOfResponse(requestStatisticsCollector, responseWrapper, requestTimeMillis, exceptionalFlow);
            describeCustomPartOfResponse(requestStatisticsCollector, responseWrapper);

            if (responseTraceCapture.isCaptured()) {
                requestStatisticsCollector.describeResponse().withTrace(responseTraceCapture.getResponseTrace());
            }

            handleCollectedStatistics(request, responseWrapper, requestStatisticsCollector);
        }
    }

    protected void describeGenericPartOfRequest(RequestStatisticsCollector requestStatisticsCollector,
                                                HttpServletRequest request, long requestTimeMillis) {
        final String remoteAddress = request.getRemoteAddr();
        final String remoteUser = request.getRemoteUser();
        final String requestMethod = request.getMethod();
        final String referer = request.getHeader(HttpHeaders.REFERER);
        final String userAgent = request.getHeader(HttpHeaders.USER_AGENT);
        requestStatisticsCollector.describeRequest()
                .withMetaInfo(GenericRequestMetaInfo.RequestTime, new DateTime(requestTimeMillis))
                .withMetaInfo(GenericRequestMetaInfo.ClientIPAddress, remoteAddress)
                .withMetaInfo(GenericRequestMetaInfo.UserName, remoteUser)
                .withMetaInfo(GenericRequestMetaInfo.RequestMethod, requestMethod)
                .withMetaInfo(GenericRequestMetaInfo.Referer, referer)
                .withMetaInfo(GenericRequestMetaInfo.UserAgent, userAgent);
    }

    protected void describeCustomPartOfRequest(RequestStatisticsCollector requestStatisticsCollector, HttpServletRequest request) {
    }

    protected void describeGenericPartOfResponse(RequestStatisticsCollector requestStatisticsCollector,
                                                 StatisticsCollectingResponseWrapper responseWrapper,
                                                 long requestTimeMillis, boolean exceptionalFlow) {
        // in some cases request processing ends with exception but error status code is set later by servlet container
        // to handle such case appropriately we recognize it and log status code as '500' even before it was set
        final int statusCode = exceptionalFlow ? HttpServletResponse.SC_INTERNAL_SERVER_ERROR : responseWrapper
                .getStatus();
        final String contentType = responseWrapper.getContentType();
        final long totalProcessingTimeMillis = clock.currentTimeMillis() - requestTimeMillis;
        final Integer contentLength = responseWrapper.getContentLength();
        final Long timeTillFirstResponseByteMillis = responseWrapper.getTimeFirstByteWrittenMillis() != null
                ? responseWrapper.getTimeFirstByteWrittenMillis() - requestTimeMillis
                : null;
        final long responseBodyWrittenBytesCount = responseWrapper.getWrittenBytesCount();
        final long responseBodyWrittenCharsCount = responseWrapper.getWrittenCharsCount();

        requestStatisticsCollector
                .describeResponse()
                        // meta info
                .withMetaInfo(GenericResponseMetaInfo.ProcessingNode, serverNodeName)
                .withMetaInfo(GenericResponseMetaInfo.StatusCode, statusCode)
                .withMetaInfo(GenericResponseMetaInfo.ContentType, contentType)
                .withMetaInfo(GenericResponseMetaInfo.ContentLength, contentLength)
                        // generic metrics
                .withMetric(GenericResponseMetric.TimeTillFirstResponseByteMillis,
                        timeTillFirstResponseByteMillis)
                .withMetric(GenericResponseMetric.TotalProcessingTimeMillis, totalProcessingTimeMillis)
                .withMetric(GenericResponseMetric.ResponseBodyWrittenBytesCount,
                        responseBodyWrittenBytesCount)
                .withMetric(GenericResponseMetric.ResponseBodyWrittenCharsCount,
                        responseBodyWrittenCharsCount);
    }

    protected void describeCustomPartOfResponse(RequestStatisticsCollector requestStatisticsCollector,
                                                StatisticsCollectingResponseWrapper responseWrapper) {
    }

    /**
     * At this point it's possible to inspect {@code Request Info}, {@code Response Info} and {@code Trace}
     * in order to decide what kind of precessing is required, e.g. appending a record to the {@code Statistics Log}.
     *
     * @param request request
     * @param responseWrapper response
     * @param requestStatisticsCollector structured description of current request
     *                                   in the form of {@link RequestStatisticsCollector}
     */
    protected void handleCollectedStatistics(HttpServletRequest request,
                                             StatisticsCollectingResponseWrapper responseWrapper,
                                             RequestStatisticsCollector requestStatisticsCollector) {
        if (requestStatisticsCollector.isResourceRecognized()) {
            final RequestStatistics requestStatistics = requestStatisticsCollector.summarizeCollectedStatistics();

            if (!Strings.isNullOrEmpty(publishStatisticsAsRequestAttribute)) {
                request.setAttribute(publishStatisticsAsRequestAttribute, requestStatistics);
            }

            handleRecognizedResourceStatistics(requestStatistics);
        }
    }

    protected void handleRecognizedResourceStatistics(RequestStatistics requestStatistics) {
        requestStatisticsHandler.handle(requestStatistics);
    }

    public void setServerNodeName(String serverNodeName) {
        this.serverNodeName = serverNodeName;
    }

    public void setPublishStatisticsAsRequestAttribute(String publishStatisticsAsRequestAttribute) {
        this.publishStatisticsAsRequestAttribute = publishStatisticsAsRequestAttribute;
    }
}
