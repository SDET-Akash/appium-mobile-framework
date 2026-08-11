package com.automation.mobile.exceptions;

/**
 * Thrown when required configuration is missing, malformed, or cannot
 * be resolved (e.g. an unknown {@code Environment}, a missing property
 * key) — raised by {@code ConfigReader}/{@code ConfigManager}
 * implementations.
 * <p>
 * Implementation intentionally deferred to a later iteration.
 */
public class ConfigurationException extends FrameworkException {
}
