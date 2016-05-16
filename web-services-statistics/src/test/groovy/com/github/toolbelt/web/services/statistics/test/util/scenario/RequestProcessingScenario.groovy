package com.github.toolbelt.web.services.statistics.test.util.scenario

import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Represents a scenario of request processing.
 *
 * <p>Notice: Implementations are expected to be concise and use pure {@link HttpServletRequest} and
 * {@link HttpServletResponse} {@code APIs} instead of full-featured web frameworks.</p>
 *
 * @author <a href="mailto:y.skopets@gmail.com">Yaroslav Skopets</a>
 */
trait RequestProcessingScenario implements FilterChain {

    abstract void enact(HttpServletRequest req, HttpServletResponse resp)

    @Override
    void doFilter(ServletRequest req, ServletResponse resp) throws IOException, ServletException {
        enact(req as HttpServletRequest, resp as HttpServletResponse)
    }
}