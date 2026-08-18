package com.automation.mobile.exceptions;

/**
 * Thrown when creating or initializing a mobile driver session fails
 * (e.g. Appium server unreachable, invalid capabilities, session
 * creation timeout) — raised by {@code DriverFactory}/{@code DriverManager}
 * implementations.
 */
public class DriverInitializationException extends FrameworkException {

    public DriverInitializationException(String message) {
        super(message);
    }

    public DriverInitializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
