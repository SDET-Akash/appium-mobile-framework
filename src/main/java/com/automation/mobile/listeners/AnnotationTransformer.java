package com.automation.mobile.listeners;

import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * TestNG {@code IAnnotationTransformer} implementation used to
 * programmatically apply cross-cutting {@code @Test} annotation
 * behavior at runtime — wiring {@link RetryAnalyzer} onto every test
 * method without requiring it on each {@code @Test} annotation
 * individually. Registered as a suite-level listener (see
 * {@code testng.xml}), so it applies uniformly to every test class in
 * the suite.
 */
public class AnnotationTransformer implements IAnnotationTransformer {

    @Override
    public void transform(
            ITestAnnotation annotation,
            Class testClass,
            Constructor testConstructor,
            Method testMethod
    ) {
        annotation.setRetryAnalyzer(RetryAnalyzer.class);
    }
}
