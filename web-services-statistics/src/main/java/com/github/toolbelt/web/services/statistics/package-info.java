/**
 * Root package where reside all classes related to {@code Advanced Statistics} feature.
 *
 * <p>{@code Advanced Statistics} is a sum of the following components:
 * <ul>
 *     <li>Generic request {@code meta-info}, such as {@code Request Time}, {@code Client IP Address}, etc.
 *     It is exactly the same info you normally find in the {@code Apache access log} file</li>
 *     <li>Type of the requested resource, such as {@code Book Contents}, {@code Page Scan}, etc,
 *     plus all relevant parameters, such as {@code ISBN}, {@code Translation Language}, {@code Page Number}, etc</li>
 *     <li>Generic response {@code meta-info}, such as {@code Processing Node}, {@code Status Code}, etc</li>
 *     <li>Generic response {@code metrics}, such as {@code Time Till First Response Byte in Millis},
 *     {@code Total Processing Time in Millis}, {@code Response Size in Bytes}, etc</li>
 *     <li>Resource-specific response {@code metrics}, such as {@code Number of Pages in the Response}
 *     (applicable when servings PDF files)</li>
 *     <li>Request processing {@code Trace} that includes timing and summary of the most interesting interim events,
 *     such as external calls to {@code RESTful Web Services}, external calls to {@code SOAP-based Web Services},
 *     external calls to {@code RMI-based RPC Services}, etc. This is the point
 *     where we leverage of {@code Spring Insight}</li>
 * </ul>
 * <p>The entire solution is inspired by <a href="http://www.springsource.org/insight">Spring Insight</a>.
 * <p>While {@code Spring Insight} is a separate product (non-free, by the way)
 * we needed a solution that fits into the corporate environment.
 * <p>Thus, we decided to re-use open-source components of {@code Spring Insight} as libraries inside our own application.
 * It happens to be legal because {@code Spring Insight Core} and {@code Spring Insight Community Plugins}
 * get released under Apache Software License v2.0.
 * <p>Although our solution is based on a {@code Spring Insight Core} in general, there are a couple of distinctions:
 * <ul>
 *     <li>While {@code Spring Insight} is entirely based on AOP (and AspectJ in particular),
 *     we wanted to support explicit {@code Decoration} of services with {@code tracing} logic</li>
 *     <li>We don't use {@code Spring Insight Community Plugins} since we clearly understand that there will always be
 *     a performance penalty for collecting any extra data.
 *     At the moment, we don't see how we could benefit from info collected by Community Plugins</li>
 *     <li>{@code Spring Insight Core} is only responsible for {@code Trace} collection, but it doesn't cover persistence.
 *     This is the spot where we extend {@code Spring Insight} and attach recorded {@code Trace} to the rest of the
 *     collected statistics</li>
 *     <li>To export collected statistics outside of our application we utilize {@code Spring Integration} library.
 *     Export happens asynchronously to request processing. Collected data get serialized into {@code JSON} format
 *     and written to a log file as a single line per request</li>
 * </ul>
 * Finally, here are a couple of implementation notes to explain our motivation behind key aspects of the solution:
 * <ul>
 *     <li>To carry out statistics collection we use {@code Builder} pattern implemented with a
 *     {@code request-scoped} {@code Spring Bean}</li>
 *     <li>Due to limitations of a corporate environment, we can't persist collected statistics directly into a
 *     {@code NoSQL} database. So, we have to use regular log files as an intermediate storage</li>
 *     <li>We export statistics data in {@code JSON} format because
 *         <ol>
 *             <li>we want data to remain structured</li>
 *             <li>we want to have a text-based log file to leverage all the power of UNIX command line tools</li>
 *             <li>in the end we persist data in {@code MongoDB} and {@code Elasticsearch}</li>
 *         </ol>
 *     </li>
 * </ul>
 *
 * @author <a href="mailto:y.skopets@gmail.com">Yaroslav Skopets</a>
 */
package com.github.toolbelt.web.services.statistics;
