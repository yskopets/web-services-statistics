package com.github.toolbelt.web.services.statistics.test.util.mock

import groovy.transform.SelfType
import org.spockframework.gentyref.GenericTypeReflector
import org.spockframework.mock.MockImplementation
import org.spockframework.mock.MockNature
import org.spockframework.util.ReflectionUtil
import spock.lang.Specification

import java.lang.reflect.Array
import java.lang.reflect.Method
import java.lang.reflect.Type

/**
 * Generates a {@code zero} | {@code empty} | {@code default} value of a given {@code type}.
 *
 * @author <a href="mailto:y.skopets@gmail.com">Yaroslav Skopets</a>
 *
 * @see org.spockframework.mock.EmptyOrDummyResponse
 */
@SelfType(Specification)
trait EmptyOrDummyValueFactory {

    /**
     * Generates a {@code zero} | {@code empty} | {@code default} value of a given {@code exactType}.
     *
     * @param exactType either a {@link Class} or a {@link java.lang.reflect.ParameterizedType}
     * @return a {@code zero} | {@code empty} | {@code default} value of a given {@code exactType}
     */
    Object emptyOrDummyValueOf(Type exactType) {
        Class<?> returnType = GenericTypeReflector.erase(exactType)

        if (returnType == void.class || returnType == Void) {
            return null
        }

        if (returnType.isPrimitive()) {
            return ReflectionUtil.getDefaultValue(returnType)
        }

        if (returnType.isInterface()) {
            if (returnType == Iterable) return new ArrayList()
            if (returnType == Collection) return new ArrayList()
            if (returnType == List) return new ArrayList()
            if (returnType == Set) return new HashSet()
            if (returnType == Map) return new HashMap()
            if (returnType == Queue) return new LinkedList()
            if (returnType == SortedSet) return new TreeSet()
            if (returnType == SortedMap) return new TreeMap()
            if (returnType == CharSequence) return '\t'
            return createDummy(exactType, returnType)
        }

        if (returnType.isArray()) {
            return Array.newInstance(returnType.getComponentType(), 0)
        }

        if (returnType.isEnum()) {
            Object[] enumConstants = returnType.getEnumConstants()
            return enumConstants.length > 0 ? enumConstants[0] : null // null is only permissible value
        }

        if (CharSequence.isAssignableFrom(returnType)) {
            if (returnType == String) return '\t'
            if (returnType == StringBuilder) return new StringBuilder()
            if (returnType == StringBuffer) return new StringBuffer()
            if (returnType == GString) return GString.EMPTY
            // continue on
        }

        Object emptyWrapper = createEmptyWrapper(returnType)
        if (emptyWrapper != null) return emptyWrapper

        Object emptyObject = createEmptyObject(returnType)
        if (emptyObject != null) return emptyObject

        // since it's not possible to mock final classes, define their dummy values expicitly
        if (returnType == Locale) return Locale.ENGLISH

        return createDummy(exactType, returnType)
    }

    // also handles some numeric types which aren't primitive wrapper types
    private Object createEmptyWrapper(Class<?> type) {
        if (Number.isAssignableFrom(type)) {
            Method method = ReflectionUtil.getDeclaredMethodBySignature(type, "valueOf", String)
            if (method != null && method.getReturnType() == type) {
                return ReflectionUtil.invokeMethod(type, method, "0")
            }
            if (type == BigInteger) return BigInteger.ZERO
            if (type == BigDecimal) return BigDecimal.ZERO
            return null
        }
        if (type == Boolean) return false
        if (type == Character) return 0 as char // better return something else?
        return null
    }

    private Object createEmptyObject(Class<?> type) {
        try {
            return type.newInstance()
        } catch (Exception e) {
            return null
        }
    }

    private Object createDummy(Type exactType, Class<?> type) {
        Specification spec = this as Specification
        return spec.createMock("dummy", exactType, MockNature.STUB, GroovyObject.isAssignableFrom(type) ?
                MockImplementation.GROOVY : MockImplementation.JAVA, Collections.<String, Object>emptyMap(), null)
    }
}
