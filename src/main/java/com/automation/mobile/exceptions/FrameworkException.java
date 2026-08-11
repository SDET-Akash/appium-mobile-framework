package com.automation.mobile.exceptions;

/**
 * Root unchecked exception type for the framework. All other custom
 * framework exceptions extend this so calling code can catch a single
 * type when it only cares that "something in the framework failed",
 * while still allowing more specific catches where needed.
 * <p>
 * Implementation intentionally deferred to a later iteration.
 */
public class FrameworkException extends RuntimeException {
}
