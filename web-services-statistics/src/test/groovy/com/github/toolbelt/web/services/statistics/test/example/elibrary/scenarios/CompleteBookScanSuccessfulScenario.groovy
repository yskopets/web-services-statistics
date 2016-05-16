package com.github.toolbelt.web.services.statistics.test.example.elibrary.scenarios

import com.github.toolbelt.web.services.statistics.collector.RequestStatisticsCollector
import com.github.toolbelt.web.services.statistics.test.given.SampleOperationsCapable
import com.github.toolbelt.web.services.statistics.test.util.scenario.RequestProcessingScenario
import com.github.toolbelt.web.services.statistics.trace.TracingSubsystem
import com.github.toolbelt.web.services.statistics.trace.TracingSupport
import groovy.transform.TupleConstructor

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import static com.github.toolbelt.web.services.statistics.test.example.elibrary.resource.BookLibraryResource.CompleteBookScan
import static com.github.toolbelt.web.services.statistics.test.example.elibrary.resource.parameters.CompleteBookScanParameter.ContainerFormat
import static com.github.toolbelt.web.services.statistics.test.example.elibrary.resource.parameters.CompleteBookScanParameter.ISBN
import static com.github.toolbelt.web.services.statistics.test.example.elibrary.response.metrics.CompleteBookScanMetric.NumberOfPages
import static com.google.common.net.MediaType.PDF

/**
 * Represents a successful response for the {@code CompleteBookScan} resource.
 *
 * @author <a href="mailto:y.skopets@gmail.com">Yaroslav Skopets</a>
 */
@TupleConstructor
class CompleteBookScanSuccessfulScenario implements RequestProcessingScenario, SampleOperationsCapable {

    RequestStatisticsCollector statsCollector

    TracingSubsystem tracingSubsystem

    @Override
    void enact(HttpServletRequest req, HttpServletResponse resp) {

        /**
         * Stage 1: Describe the requested resource from the business perspective.
         *
         * Notice: It is crucial to collect this information before actual request processing gets the first chance to fail.
         */

        statsCollector.describeRequest().requestForResource()
                .ofType(CompleteBookScan)
                .withParameter(ISBN, '1234567890123')
                .withParameter(ContainerFormat, 'application/pdf')

        /**
         * Stage 2: Do actual request processing and trace the flow of execution along the way.
         *
         * Notice: application code can either {@code trace} explicitly or make use of {@code AOP} machinery for that purpose.
         */

        new TracingSupport(tracingSubsystem).with {
            // tree of interim operations
            enter(webRequestOperation)
                enter(methodCallOperation)
                    enter(httpOperation)
                    exitNormal(401)
                exitNormal('interim return value')
            exitNormal()
        }

        /**
         * Stage 3: Stream the generated response back to the client.
         *
         * Notice: request processing, response streaming and response metrics collection can be intermixed in any order.
         */

        resp.with {
            setContentType(PDF as String)
            // intentionally set 'Content-Length' to a different value from the actual response size (to see how well statistics will handle it)
            setContentLength(12345)
            outputStream.write('Stub for the actual PDF contents' as byte[])
        }

        /**
         * Stage 4: Describe metrics of the generated response from the business perspective.
         *
         * Notice: this stage is convenient for metrics that become available in the very end of request processing.
         */

        statsCollector.describeResponse().withMetric(NumberOfPages, 257)
    }
}
