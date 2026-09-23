package com.automation.mobile.config;

import com.automation.mobile.exceptions.ConfigurationException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Framework-facing configuration API. {@link ConfigReader} reads raw
 * key/value properties for a single {@link Environment}; ConfigManager
 * wraps that with the typed accessors the rest of the framework uses, so
 * callers never touch property keys or {@link ConfigReader} directly.
 */
public class ConfigManager {

    private final ConfigReader configReader;
    private final Environment environment;

    public ConfigManager(Environment environment) {
        this.environment = environment;
        this.configReader = new ConfigReader(environment);
    }

    /**
     * Returns the raw value for the given property key.
     *
     * @param key the property key to look up
     * @return the value associated with the key
     */
    public String get(String key) {
        return configReader.get(key);
    }
    public Environment getEnvironment() {
        return environment;
    }

    public String getPlatform() {
        return get("platform");
    }

    public String getAutomationName() {
        return get("automationName");
    }

    public String getDeviceName() {
        return get("deviceName");
    }

    public String getAppiumServerUrl() {
        return get("appiumServerUrl");
    }

    public String getAppPackage() {
        return get("appPackage");
    }

    public boolean getGrantPermission() {
        String value = get("autoGrantPermissions");

        if (!value.equalsIgnoreCase("true") && !value.equalsIgnoreCase("false")) {
            throw new ConfigurationException(
                    "Configuration value 'autoGrantPermissions' must be true or false"
            );
        }

        return Boolean.parseBoolean(value);
    }

    public boolean getAppInstall() {
        String value = get("enforceAppInstall");

        if (!value.equalsIgnoreCase("true") && !value.equalsIgnoreCase("false")) {
            throw new ConfigurationException(
                    "Configuration value 'enforceAppInstall' must be true or false"
            );
        }

        return Boolean.parseBoolean(value);
    }

    /**
     * Resolves the configured APK location to an absolute filesystem path.
     * <p>
     * {@code appPath} is stored relative to the project's test resources
     * directory (e.g. {@code apps/qa/qa-debug.apk}) so the properties
     * files stay machine-agnostic. Resolved by direct filesystem
     * construction relative to the project's base directory
     * ({@code user.dir} — the default working directory under both
     * Maven Surefire and an IDE's default test run configuration)
     * rather than classloader resource lookup, so resolution doesn't
     * depend on the APK already having been copied onto the classpath.
     * If the environment's app path has not yet been confirmed, this
     * returns the blank value from the properties file rather than
     * substituting a default/fake path.
     *
     * @throws ConfigurationException if a non-blank appPath does not
     * resolve to an existing regular file
     */
    public String getAppPath() {
        String appPath = get("appPath");
        if (appPath == null || appPath.trim().isEmpty()) {
            return appPath;
        }

        Path testResourcesRoot = Paths.get(
                System.getProperty("user.dir"),
                "src", "test", "resources"
        );

        Path resolvedPath = testResourcesRoot.resolve(appPath);

        if (!Files.isRegularFile(resolvedPath)) {
            throw new ConfigurationException(
                    "Configured appPath does not exist or is not a regular file: "
                            + resolvedPath
            );
        }

        return resolvedPath.toAbsolutePath().toString();
    }
}
