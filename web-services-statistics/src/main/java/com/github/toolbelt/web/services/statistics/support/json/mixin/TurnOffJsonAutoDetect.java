package com.github.toolbelt.web.services.statistics.support.json.mixin;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;

/**
 * Conveniently turns off {@code Jackson}'s {@code auto-detect} behaviour.
 *
 * @author <a href="mailto:y.skopets@gmail.com">Yaroslav Skopets</a>
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@JsonAutoDetect(getterVisibility = NONE, isGetterVisibility = NONE, setterVisibility = NONE, creatorVisibility = NONE,
        fieldVisibility = NONE)
public @interface TurnOffJsonAutoDetect {
}
