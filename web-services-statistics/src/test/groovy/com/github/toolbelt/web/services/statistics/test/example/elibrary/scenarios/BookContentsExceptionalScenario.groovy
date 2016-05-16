package com.github.toolbelt.web.services.statistics.test.example.elibrary.scenarios

import com.github.toolbelt.web.services.statistics.collector.RequestStatisticsCollector
import com.github.toolbelt.web.services.statistics.test.given.SampleOperationsCapable
import com.github.toolbelt.web.services.statistics.test.util.scenario.RequestProcessingScenario
import com.github.toolbelt.web.services.statistics.trace.TracingSubsystem
import com.github.toolbelt.web.services.statistics.trace.TracingSupport
import groovy.transform.TupleConstructor

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import static com.github.toolbelt.web.services.statistics.test.example.elibrary.resource.BookLibraryResource.BookContents
import static com.github.toolbelt.web.services.statistics.test.example.elibrary.resource.parameters.BookContentsParameter.ISBN
import static com.github.toolbelt.web.services.statistics.test.example.elibrary.resource.parameters.BookContentsParameter.TranslationLanguage

/**
 * Represents a response for the {@code BookContents} resource that fails with an exception.
 *
 * @author <a href="mailto:y.skopets@gmail.com">Yaroslav Skopets</a>
 */
@TupleConstructor
class BookContentsExceptionalScenario implements RequestProcessingScenario, SampleOperationsCapable {

    RequestStatisticsCollector statsCollector

    TracingSubsystem tracingSubsystem

    Throwable exceptionToSimulate

    @Override
    void enact(HttpServletRequest req, HttpServletResponse resp) {

        /**
         * Stage 1: Describe the requested resource from the business perspective.
         *
         * Notice: It is crucial to collect this information before actual request processing gets the first chance to fail.
         */

        statsCollector.describeRequest().requestForResource()
                .ofType(BookContents)
                .withParameter(ISBN, '1234567890123')
                .withParameter(TranslationLanguage, 'nl')

        /**
         * Stage 2: Do actual request processing and trace the flow of execution along the way.
         *
         * Notice: application code can either {@code trace} explicitly or make use of {@code AOP} machinery for that purpose.
         */

        new TracingSupport(tracingSubsystem).with {
            // tree of interim operations
            enter(webRequestOperation)
                enter(methodCallOperation)
                exitAbnormal(exceptionToSimulate)
            exitNormal()
        }

        /**
         * Stage 3: Stream the generated response back to the client.
         *
         * Notice: request processing, response streaming and response metrics collection can be intermixed in any order.
         */

        // no chance for a successful response
        throw exceptionToSimulate
    }
}
