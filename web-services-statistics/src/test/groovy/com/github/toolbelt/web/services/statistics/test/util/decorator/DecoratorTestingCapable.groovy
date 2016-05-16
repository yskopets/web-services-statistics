package com.github.toolbelt.web.services.statistics.test.util.decorator

import com.github.toolbelt.web.services.statistics.test.util.mock.EmptyOrDummyValueFactory

import java.lang.reflect.Method

/**
 * Collection of utility methods for testing classes that implement <em>Design Pattern</em> <em>Decorator</em>.
 *
 * @author <a href="mailto:y.skopets@gmail.com">Yaroslav Skopets</a>
 */
trait DecoratorTestingCapable extends EmptyOrDummyValueFactory {

    /**
     * Returns all public non-synthetic methods of a given {@code type}.
     *
     * @param type type
     * @return all public non-synthetic methods of a given {@code type}
     */
    Method[] allPublicMethodsOf(Class type) {
        type.methods.findAll { it.declaringClass != Object }.findAll { !it.synthetic }
    }

    /**
     * Returns all public non-synthetic methods of a given {@code type} that are also defined in the {@code superclass}.
     *
     * @param type type
     * @param superclass superclass
     * @return all public non-synthetic methods of a given {@code type} that are also defined in the {@code superclass}
     */
    Method[] allPublicMethodsFromSuperclass(Class type, Class superclass) {
        assert superclass.isAssignableFrom(type)
        def allMethodsOfType = allPublicMethodsOf(type)
        allPublicMethodsOf(superclass).collect { methodOfSuperclass -> allMethodsOfType.find { it.name == methodOfSuperclass.name && it.parameterTypes == methodOfSuperclass.parameterTypes } }
    }

    /**
     * Returns short description of a given {@code method}, e.g. <code>#println(char)</code>.
     *
     * @param method method
     * @return short description of a given {@code method}
     */
    String signatureOf(Method method) {
        "#${method.name}(" + method.parameterTypes.collect { it.name }.join(",") + ")"
    }

    /**
     * Returns an array of arguments suitable to call a given {@code method} with.
     *
     * @param method method
     * @return an array of arguments suitable to call a given {@code method} with
     */
    Object[] dummyArgumentsFor(Method method) {
        method.parameterTypes.collect { emptyOrDummyValueOf(it) }.toArray()
    }
}
