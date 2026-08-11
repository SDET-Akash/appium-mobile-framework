package com.automation.mobile.config;

/**
 * Contract for reading raw configuration values (properties files,
 * environment variables, system properties) into key/value lookups,
 * without any knowledge of how those values are used.
 * <p>
 * Kept separate from {@link ConfigManager} so the source of configuration
 * (file-based today, potentially remote/secrets-manager-based later) can
 * change without affecting how the rest of the framework consumes config.
 * <p>
 * Implementation intentionally deferred to a later iteration.
 */
public interface ConfigReader {
}
