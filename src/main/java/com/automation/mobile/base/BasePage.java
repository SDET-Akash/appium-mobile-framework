package com.automation.mobile.base;

/**
 * Base class every Page Object extends. Owns all low-level driver
 * interaction (element lookup, waits, gestures) by obtaining the active
 * driver through {@code DriverManager} internally.
 * <p>
 * Page Objects must never access {@code DriverManager} directly — they
 * interact with the driver exclusively through the methods this class
 * exposes, keeping driver-lifecycle knowledge out of the page layer.
 * <p>
 * Implementation intentionally deferred to a later iteration.
 */
public class BasePage {
}
