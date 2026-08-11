package com.automation.mobile.driver;

/**
 * Abstraction for creating a platform-specific mobile driver session
 * (Android via UiAutomator2, iOS via XCUITest).
 * <p>
 * Concrete platform factories ({@link AndroidDriverFactory},
 * {@link IOSDriverFactory}) will implement this contract so that driver
 * creation is decoupled from the platform being targeted, in line with the
 * Open/Closed and Dependency Inversion principles — new platforms can be
 * added without modifying existing call sites.
 * <p>
 * Implementation intentionally deferred to a later iteration.
 */
public interface DriverFactory {
}
