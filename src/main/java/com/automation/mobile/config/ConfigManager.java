package com.automation.mobile.config;

/**
 * Central access point for resolved, typed configuration used across the
 * framework (target {@link Environment}, platform, timeouts, app paths,
 * Appium server URL, etc.), built on top of one or more
 * {@link ConfigReader} sources.
 * <p>
 * Tests, page objects and the driver layer depend only on this manager —
 * never directly on a properties file or a {@link ConfigReader}
 * implementation.
 * <p>
 * Implementation intentionally deferred to a later iteration.
 */
public class ConfigManager {
}
