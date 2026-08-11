package com.automation.mobile.exceptions;

/**
 * Thrown when creating or initializing a mobile driver session fails
 * (e.g. Appium server unreachable, invalid capabilities, session
 * creation timeout) — raised by {@code DriverFactory}/{@code DriverManager}
 * implementations.
 * <p>
 * Implementation intentionally deferred to a later iteration.
 */
public class DriverInitializationException extends FrameworkException {
}
