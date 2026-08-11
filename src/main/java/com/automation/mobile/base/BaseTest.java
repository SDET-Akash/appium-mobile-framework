package com.automation.mobile.base;

/**
 * Base class every test class extends. Owns suite/class/method-level
 * setup and teardown: resolving config via {@code ConfigManager},
 * obtaining a driver session through {@code DriverManager}, and
 * releasing it after the test — so individual test classes never touch
 * config or driver lifecycle directly.
 * <p>
 * Implementation intentionally deferred to a later iteration.
 */
public class BaseTest {
}
