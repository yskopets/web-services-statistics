package com.github.toolbelt.web.services.statistics.test.example.elibrary.resource.parameters

import com.github.toolbelt.web.services.statistics.resource.ResourceParameter

/**
 * Enumerates {@code parameters} supported by
 * {@link com.github.toolbelt.web.services.statistics.test.example.elibrary.resource.BookLibraryResource#BookContents BookContents}
 * resource.
 *
 * @author <a href="mailto:y.skopets@gmail.com">Yaroslav Skopets</a>
 */
enum BookContentsParameter implements ResourceParameter {

    ISBN,

    TranslationLanguage
}