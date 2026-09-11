package com.automation.mobile.config;

import com.automation.mobile.exceptions.ConfigurationException;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Transforms framework configuration exposed by {@link ConfigManager} into the
 * Appium session capabilities map required by {@code AndroidDriverFactory}.
 * <p>
 * This class does not create or start an Appium driver; it only performs the
 * configuration-to-map transformation.
 */
public class CapabilityBuilder {

    private final ConfigManager configManager;

    public CapabilityBuilder(ConfigManager configManager) {
        this.configManager = configManager;
    }

    /**
     * Builds a new Appium capabilities map from the current configuration.
     *
     * @return a new {@code Map<String, Object>} of capabilities
     * @throws ConfigurationException if a required configuration value is missing or blank
     */
    public Map<String, Object> build() {
        String platform = configManager.getPlatform();
        String automationName = configManager.getAutomationName();
        String deviceName = configManager.getDeviceName();
        String appPath = configManager.getAppPath();
        boolean permission = configManager.getGrantPermission();
        boolean enforceInstall = configManager.getAppInstall();

        requireNonBlank("platform", platform);
        requireNonBlank("automationName", automationName);
        requireNonBlank("deviceName", deviceName);

        Map<String, Object> capabilities = new LinkedHashMap<>();
        capabilities.put("platformName", platform);
        capabilities.put("automationName", automationName);
        capabilities.put("deviceName", deviceName);
        capabilities.put("autoGrantPermissions", permission);
        capabilities.put("enforceAppInstall", enforceInstall);
        // The framework performs its own deterministic, synchronous app
        // reset (StorageStateManager.clear()/restore()) after the driver
        // session is created. Appium's own noReset=false reset runs
        // asynchronously relative to session creation returning, and racing
        // it against our own reset corrupts app state (confirmed via
        // logcat: concurrent "clear data" events followed by an Activity
        // pause/resume timeout). noReset=true disables that automatic
        // reset so our own reset is the single source of truth.
//        capabilities.put("noReset", true);
//        capabilities.put("autoLaunch", false);


        if (appPath != null && !appPath.trim().isEmpty()) {
            capabilities.put("app", appPath);
        }

        return capabilities;
    }

    private void requireNonBlank(String name, String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new ConfigurationException("Required configuration value '" + name + "' is missing or blank");
        }
    }
}
