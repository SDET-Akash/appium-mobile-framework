package com.automation.mobile.driver;

import com.automation.mobile.exceptions.DriverInitializationException;
import io.appium.java_client.AppiumDriver;

import java.net.URL;
import java.util.Map;

/**
 * Abstraction for creating a platform-specific mobile driver session
 * (Android via UiAutomator2, iOS via XCUITest).
 * <p>
 * Concrete platform factories ({@link AndroidDriverFactory},
 * {@link IOSDriverFactory}) implement this contract so that driver
 * creation is decoupled from the platform being targeted, in line with the
 * Open/Closed and Dependency Inversion principles — new platforms can be
 * added without modifying existing call sites.
 * <p>
 * Capability/config resolution is the caller's responsibility (the
 * future Configuration layer); this contract only turns an already
 * resolved server URL and capability set into a connected driver.
 */
public interface DriverFactory {

    /**
     * Creates and returns a new, connected Appium driver session.
     *
     * @param appiumServerUrl the URL of the already-running Appium server
     * @param capabilities    the desired session capabilities
     * @return a connected {@link AppiumDriver} instance
     * @throws DriverInitializationException if the session cannot be created
     */
    AppiumDriver createDriver(URL appiumServerUrl, Map<String, Object> capabilities);
}
