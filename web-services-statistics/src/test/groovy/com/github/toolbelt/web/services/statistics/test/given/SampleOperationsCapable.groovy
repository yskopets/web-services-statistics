package com.github.toolbelt.web.services.statistics.test.given

import com.springsource.insight.intercept.operation.Operation

import static com.springsource.insight.intercept.operation.OperationFields.ARGUMENTS
import static com.springsource.insight.intercept.operation.OperationType.HTTP
import static com.springsource.insight.intercept.operation.OperationType.METHOD
import static com.springsource.insight.intercept.operation.OperationType.WEB_REQUEST

/**
 * Collection of sample data reusable across unit tests.
 *
 * @author <a href="mailto:y.skopets@gmail.com">Yaroslav Skopets</a>
 */
trait SampleOperationsCapable {

    Operation webRequestOperation = new Operation().with {
        type(WEB_REQUEST).label('restful-web-service-endpoint')
        createMap(ARGUMENTS).put('client_id', 'spock').put('api_version', 2)
        return it
    }

    Operation methodCallOperation = new Operation().with {
        type(METHOD).label('controller')
        createList(ARGUMENTS).add('1234567890123').add(17)
        return it
    }

    Operation httpOperation = new Operation().with {
        type(HTTP).label('external-call')
        createMap(ARGUMENTS).put('isbn', '1234567890123').put('page_number', 17)
        return it
    }
}
