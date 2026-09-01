package com.automation.mobile.config;

import com.automation.mobile.exceptions.ConfigurationException;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
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

    public ConfigManager(Environment environment) {
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
     * {@code appPath} is stored as a classpath-relative resource (e.g.
     * {@code apps/qa/qa.apk}) so the properties files stay machine-agnostic.
     * If the environment's app path has not yet been confirmed, this returns
     * the blank value from the properties file rather than substituting a
     * default/fake path.
     *
     * @throws ConfigurationException if a non-blank appPath cannot be found on the classpath
     */
    public String getAppPath() {
        String appPath = get("appPath");
        if (appPath == null || appPath.trim().isEmpty()) {
            return appPath;
        }

        URL resource = getClass().getClassLoader().getResource(appPath);
        if (resource == null) {
            throw new ConfigurationException("Configured appPath resource not found on classpath: " + appPath);
        }

        Path resolvedPath = Paths.get(toUri(resource));
        return resolvedPath.toAbsolutePath().toString();
    }

    private static URI toUri(URL resource) {
        try {
            return resource.toURI();
        } catch (URISyntaxException e) {
            throw new ConfigurationException("Failed to resolve classpath resource URL: " + resource, e);
        }
    }
}
