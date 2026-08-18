package com.automation.mobile.exceptions;

/**
 * Root unchecked exception type for the framework. All other custom
 * framework exceptions extend this so calling code can catch a single
 * type when it only cares that "something in the framework failed",
 * while still allowing more specific catches where needed.
 */
public class FrameworkException extends RuntimeException {

    public FrameworkException(String message) {
        super(message);
    }

    public FrameworkException(String message, Throwable cause) {
        super(message, cause);
    }
}
