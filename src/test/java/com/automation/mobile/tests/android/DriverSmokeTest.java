package com.automation.mobile.tests.android;

import com.automation.mobile.driver.AndroidDriverFactory;
import com.automation.mobile.driver.DriverManager;
import io.appium.java_client.android.AndroidDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

/**
 * Temporary smoke test verifying that the Driver Layer
 * (AndroidDriverFactory + DriverManager) can create and manage a real
 * Android Appium session end to end.
 * <p>
 * This is NOT a framework test class — it exists only to validate the
 * Driver Layer in isolation. Appium server URL, device name and APK path
 * are hardcoded here deliberately, since the Configuration Layer does
 * not exist yet. To be removed/replaced once real tests are built on
 * top of BaseTest/BasePage.
 */
public class DriverSmokeTest {

    private static final Logger LOGGER = LogManager.getLogger(DriverSmokeTest.class);

    private static final String APPIUM_SERVER_URL = "http://127.0.0.1:4723";
    private static final String DEVICE_NAME = "emulator-5554";
    private static final String APP_PATH = "/home/akash/kylasApk/app-dev-debug.apk/app-dev-debug.apk";

    @Test
    public void verifyAndroidDriverSessionIsCreated() throws MalformedURLException {
        Map<String, Object> capabilities = Map.of(
                "deviceName", DEVICE_NAME,
                "app", APP_PATH
        );

        AndroidDriverFactory androidDriverFactory = new AndroidDriverFactory();
        AndroidDriver driver = androidDriverFactory.createDriver(new URL(APPIUM_SERVER_URL), capabilities);
        DriverManager.setDriver(driver);

        AndroidDriver activeDriver = DriverManager.getDriver();
        Assert.assertNotNull(activeDriver, "Driver returned by DriverManager must not be null");
        Assert.assertNotNull(activeDriver.getSessionId(), "Appium session ID must not be null");

        LOGGER.info("Session ID: {}", activeDriver.getSessionId());
        LOGGER.info("Current package: {}", activeDriver.getCurrentPackage());
        LOGGER.info("Current activity: {}", activeDriver.currentActivity());

        Assert.assertNotNull(activeDriver.getCurrentPackage(), "App does not appear to have launched — current package is null");
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        DriverManager.removeDriver();
    }
}
