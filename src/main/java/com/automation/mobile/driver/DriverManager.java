package com.automation.mobile.driver;

/**
 * Owns the lifecycle of the active mobile driver instance for the current
 * test execution (creation, thread-local storage for parallel runs,
 * retrieval, and teardown/quit).
 * <p>
 * Decouples "which driver is currently active" from "how a driver is
 * built" ({@link DriverFactory} implementations) so tests and page objects
 * depend only on this manager, never on a concrete factory or driver type.
 * <p>
 * Implementation intentionally deferred to a later iteration.
 */
public class DriverManager {
}
