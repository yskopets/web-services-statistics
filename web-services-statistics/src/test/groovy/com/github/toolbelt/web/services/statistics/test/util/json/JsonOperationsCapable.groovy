package com.github.toolbelt.web.services.statistics.test.util.json

import groovy.json.JsonOutput
import com.fasterxml.jackson.databind.ObjectMapper

/**
 * Collection of utility methods for dealing with {@code JSON}.
 *
 * @author <a href="mailto:y.skopets@gmail.com">Yaroslav Skopets</a>
 */
trait JsonOperationsCapable {

    private objectMapper = new ObjectMapper()

    String compact(String anyJson) {
        objectMapper.writeValueAsString(objectMapper.readTree(anyJson))
    }

    String pretty(String anyJson) {
        JsonOutput.prettyPrint(compact(anyJson))
    }
}
