package com.automation.mobile.driver;

import io.appium.java_client.AppiumDriver;

import java.net.URL;
import java.util.Map;

/**
 * {@link DriverFactory} implementation responsible for building an
 * IOSDriver (XCUITest) session — resolving iOS capabilities and
 * connecting to the Appium server for iOS native/hybrid apps.
 * <p>
 * Reserved for future iOS support; no active usage until iOS execution
 * is introduced.
 * <p>
 * Implementation intentionally deferred to a later iteration.
 */
public class IOSDriverFactory implements DriverFactory {

    @Override
    public AppiumDriver createDriver(URL appiumServerUrl, Map<String, Object> capabilities) {
        throw new UnsupportedOperationException("iOS driver creation is not implemented yet");
    }
}
