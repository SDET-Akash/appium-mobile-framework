package com.automation.mobile.tests.android;

import com.automation.mobile.base.BaseTest;
import com.automation.mobile.driver.DriverManager;
import io.appium.java_client.android.AndroidDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Temporary smoke test verifying that the Configuration Layer
 * (ConfigManager + CapabilityBuilder) and the Driver Layer
 * (AndroidDriverFactory + DriverManager) work together end to end to
 * create a real Android Appium session.
 * <p>
 * This is NOT a framework test class — it exists only to validate that
 * the layers are wired together correctly. To be removed/replaced once
 * real tests are built on top of BaseTest/BasePage.
 */
public class DriverSmokeTest extends BaseTest {

    private static final Logger LOGGER = LogManager.getLogger(DriverSmokeTest.class);

    @Test
    public void verifyAndroidDriverSessionIsCreated() {
        AndroidDriver activeDriver = DriverManager.getDriver();
        Assert.assertNotNull(activeDriver, "Driver returned by DriverManager must not be null");
        Assert.assertNotNull(activeDriver.getSessionId(), "Appium session ID must not be null");

        LOGGER.info("Session ID: {}", activeDriver.getSessionId());
        LOGGER.info("Current package: {}", activeDriver.getCurrentPackage());
        LOGGER.info("Current activity: {}", activeDriver.currentActivity());

        Assert.assertNotNull(activeDriver.getCurrentPackage(), "App does not appear to have launched — current package is null");
        Assert.assertFalse(activeDriver.getCurrentPackage().trim().isEmpty(), "App does not appear to have launched — current package is blank");
    }
}
