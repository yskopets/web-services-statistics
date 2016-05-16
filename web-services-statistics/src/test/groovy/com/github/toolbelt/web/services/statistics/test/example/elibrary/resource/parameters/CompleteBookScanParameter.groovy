package com.github.toolbelt.web.services.statistics.test.example.elibrary.resource.parameters

import com.github.toolbelt.web.services.statistics.resource.ResourceParameter

/**
 * Enumerates {@code parameters} supported by
 * {@link com.github.toolbelt.web.services.statistics.test.example.elibrary.resource.BookLibraryResource#CompleteBookScan CompleteBookScan}
 * resource.
 *
 * @author <a href="mailto:y.skopets@gmail.com">Yaroslav Skopets</a>
 */
enum CompleteBookScanParameter implements ResourceParameter {

    ISBN,

    /**
     * E.g., multi-page {@code image/tiff} or {@code application/pdf}
     */
    ContainerFormat
}