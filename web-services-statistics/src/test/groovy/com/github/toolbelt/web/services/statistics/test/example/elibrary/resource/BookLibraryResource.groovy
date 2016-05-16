package com.github.toolbelt.web.services.statistics.test.example.elibrary.resource

import com.github.toolbelt.web.services.statistics.resource.ResourceType

/**
 * Enumerates {@code resource types} supported by {@code E-Library} {@code Web Service}.
 *
 * @author <a href="mailto:y.skopets@gmail.com">Yaroslav Skopets</a>
 */
enum BookLibraryResource implements ResourceType {

    /**
     * {@code Table of Contents} of a {@code Book}
     */
    BookContents,

    /**
     * Scans of all {@code Page}s in a {@code Book} wrapped into a container format,
     * such as multi-page {@code image/tiff} or {@code application/pdf}
     */
    CompleteBookScan,

    /**
     * Scan of an individual {@code Page} of a {@code Book}
     */
    IndividualPageScan
}