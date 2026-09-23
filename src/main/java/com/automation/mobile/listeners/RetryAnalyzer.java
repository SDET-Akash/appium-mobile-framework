package com.automation.mobile.listeners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * TestNG {@code IRetryAnalyzer} implementation that governs automatic
 * re-execution of failed tests up to a configured retry count, to
 * absorb transient mobile/device flakiness (e.g. a slow emulator boot,
 * a dropped tap) without masking genuine failures.
 * <p>
 * A new instance is created by TestNG per test method, so {@link
 * #retryCount} is safely scoped to a single method's attempts — it does
 * not leak across unrelated tests. Once {@link #MAX_RETRY_COUNT} is
 * reached, {@link #retry(ITestResult)} returns {@code false} and TestNG
 * reports the method's final attempt exactly as it would without any
 * retry analyzer — a test that keeps failing stays FAILED.
 */
public class RetryAnalyzer implements IRetryAnalyzer {

    private static final Logger LOGGER =
            LogManager.getLogger(RetryAnalyzer.class);

    /**
     * Deliberately small and fixed: absorbs a single transient failure
     * without turning retry into a way to paper over a genuinely broken
     * or flaky test. Not exposed as configuration — a test that needs
     * more than one retry to pass is a test (or product) problem to fix,
     * not a threshold to tune upward.
     */
    private static final int MAX_RETRY_COUNT = 1;

    private int retryCount = 0;

    @Override
    public boolean retry(ITestResult result) {

        if (retryCount >= MAX_RETRY_COUNT) {
            return false;
        }

        retryCount++;

        LOGGER.warn(
                "Retrying failed test '{}' (attempt {} of {}) after: {}",
                result.getMethod().getMethodName(),
                retryCount + 1,
                MAX_RETRY_COUNT + 1,
                result.getThrowable() != null
                        ? result.getThrowable().getMessage()
                        : "no throwable captured"
        );

        return true;
    }
}
