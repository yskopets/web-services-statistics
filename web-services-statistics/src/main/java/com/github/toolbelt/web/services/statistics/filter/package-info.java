/**
 * Implementation classes that hook into the filter chain model of HTTP request processing in order to
 * <ol>
 *     <li>drive the entire process of {@code Advanced Statistics} collection</li>
 *     <li>collect generic {@code meta-info} about request and response</li>
 *     <li>dispatch collected {@code statistics} for further processing (namely, for persistence).</li>
 * </ol>
 *
 * @author <a href="mailto:y.skopets@gmail.com">Yaroslav Skopets</a>
 */
package com.github.toolbelt.web.services.statistics.filter;
