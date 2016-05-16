package com.github.toolbelt.web.services.statistics.test.given

import org.springframework.mock.web.MockHttpServletRequest

import static com.google.common.net.HttpHeaders.REFERER
import static com.google.common.net.HttpHeaders.USER_AGENT

/**
 * Factory of {@link MockHttpServletRequest} objects.
 *
 * @author <a href="mailto:y.skopets@gmail.com">Yaroslav Skopets</a>
 */
trait PreconfiguredRequestsCapable {

    MockHttpServletRequest newPreconfiguredRequest(String method, String requestURI, String queryString = null) {
        def request = new MockHttpServletRequest(method, requestURI)
        request.with {
            setQueryString(queryString)
            setProtocol('HTTP/1.1')
            setRemoteAddr('1.2.3.4')
            addHeader(REFERER, 'https://www.google.com/')
            addHeader(USER_AGENT, 'Mozilla')
        }
        request
    }
}
