package com.automation.mobile.driver;

import com.automation.mobile.exceptions.DriverInitializationException;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.Map;

/**
 * {@link DriverFactory} implementation responsible for building an
 * AndroidDriver (UiAutomator2) session — resolving Android capabilities
 * and connecting to the Appium server for Android native/hybrid/Flutter
 * apps.
 * <p>
 * {@code platformName} and {@code automationName} are fixed to
 * "Android"/"UiAutomator2" here since that is exactly what this factory
 * builds; every other capability (device, app path/package/activity,
 * etc.) is supplied by the caller via the capabilities map — this class
 * never hardcodes environment- or machine-specific values.
 */
public class AndroidDriverFactory implements DriverFactory {

    private static final Logger LOGGER = LogManager.getLogger(AndroidDriverFactory.class);

    @Override
    public AndroidDriver createDriver(URL appiumServerUrl, Map<String, Object> capabilities) {
        LOGGER.info("Starting Android driver creation against Appium server: {}", appiumServerUrl);

        UiAutomator2Options options = buildOptions(capabilities);
        LOGGER.debug("Android capabilities prepared with keys: {}",
                capabilities == null ? "[]" : capabilities.keySet());

        try {
            AndroidDriver driver = new AndroidDriver(appiumServerUrl, options);
            LOGGER.info("Android driver session created successfully");
            return driver;
        } catch (Exception e) {
            LOGGER.error("Failed to create Android driver session", e);
            throw new DriverInitializationException("Failed to create Android driver session", e);
        }
    }

    private UiAutomator2Options buildOptions(Map<String, Object> capabilities) {
        UiAutomator2Options options = new UiAutomator2Options()
                .setPlatformName("Android")
                .setAutomationName("UiAutomator2");

        if (capabilities != null) {
            capabilities.forEach(options::amend);
        }

        return options;
    }
}
