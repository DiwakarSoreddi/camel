/*
 * Camel ApiMethod Enumeration generated by camel-component-util-maven-plugin
 * Generated on: Fri May 23 17:35:21 PDT 2014
 */
package org.apache.camel.maven;

import java.lang.reflect.Method;
import java.util.List;

import org.apache.camel.maven.TestProxy;

import org.apache.camel.util.component.ApiMethod;
import org.apache.camel.util.component.ApiMethodImpl;

/**
 * Camel {@link ApiMethod} Enumeration for org.apache.camel.maven.TestProxy
 */
public enum TestProxyApiMethod implements ApiMethod {

    GREETALL(java.lang.String.class, "greetAll", new java.lang.String[0].getClass(), "names"),
    GREETALL_1(java.lang.String.class, "greetAll", java.util.List.class, "namesList"),
    GREETME(java.lang.String.class, "greetMe", java.lang.String.class, "name"),
    GREETTIMES(new java.lang.String[0].getClass(), "greetTimes", java.lang.String.class, "name", int.class, "times"),
    GREETUS(java.lang.String.class, "greetUs", java.lang.String.class, "name1", java.lang.String.class, "name2"),
    SAYHI(java.lang.String.class, "sayHi"),
    SAYHI_1(java.lang.String.class, "sayHi", java.lang.String.class, "name");

    private final ApiMethod apiMethod;

    private TestProxyApiMethod(Class<?> resultType, String name, Object... args) {
        this.apiMethod = new ApiMethodImpl(TestProxy.class, resultType, name, args);
    }

    @Override
    public String getName() { return apiMethod.getName(); }

    @Override
    public Class<?> getResultType() { return apiMethod.getResultType(); }

    @Override
    public List<String> getArgNames() { return apiMethod.getArgNames(); }

    @Override
    public List<Class<?>> getArgTypes() { return apiMethod.getArgTypes(); }

    @Override
    public Method getMethod() { return apiMethod.getMethod(); }
}