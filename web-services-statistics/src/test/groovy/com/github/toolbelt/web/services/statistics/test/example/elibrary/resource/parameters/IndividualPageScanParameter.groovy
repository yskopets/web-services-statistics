package com.github.toolbelt.web.services.statistics.test.example.elibrary.resource.parameters

import com.github.toolbelt.web.services.statistics.resource.ResourceParameter

/**
 * Enumerates {@code parameters} supported by
 * {@link com.github.toolbelt.web.services.statistics.test.example.elibrary.resource.BookLibraryResource#IndividualPageScan IndividualPageScan}
 * resource.
 *
 * @author <a href="mailto:y.skopets@gmail.com">Yaroslav Skopets</a>
 */
enum IndividualPageScanParameter implements ResourceParameter {

    ISBN,

    PageNumber,

    /**
     * E.g., {@code image/tiff} or {@code image/png}
     */
    ImageFormat
}