package com.automation.mobile.base;

import com.automation.mobile.config.CapabilityBuilder;
import com.automation.mobile.config.ConfigManager;
import com.automation.mobile.config.Environment;
import com.automation.mobile.driver.AndroidDriverFactory;
import com.automation.mobile.driver.DriverManager;
import com.automation.mobile.exceptions.DriverInitializationException;
import io.appium.java_client.android.AndroidDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

/**
 * Base class every test class extends. Owns method-level driver
 * lifecycle: resolving config via {@link ConfigManager}, building
 * capabilities via {@link CapabilityBuilder}, creating a driver session
 * through {@link AndroidDriverFactory}, and storing/releasing it via
 * {@link DriverManager} — so individual test classes never touch config
 * or driver lifecycle directly.
 * <p>
 * Environment is currently fixed to {@link Environment#QA}; a proper
 * environment-selection mechanism will be introduced later.
 */
public class BaseTest {

    private static final Logger LOGGER = LogManager.getLogger(BaseTest.class);

    @BeforeMethod
    public void setUp() {
        LOGGER.info("Setting up Android driver session for environment: {}", Environment.QA);


//        Environment is currently fixed to Environment#QA;
//        a proper environment-selection mechanism will be introduced later.

        ConfigManager configManager = new ConfigManager(Environment.QA);
        CapabilityBuilder capabilityBuilder = new CapabilityBuilder(configManager);
        Map<String, Object> capabilities = capabilityBuilder.build();

        URL appiumServerUrl = toUrl(configManager.getAppiumServerUrl());

        AndroidDriverFactory androidDriverFactory = new AndroidDriverFactory();
        AndroidDriver driver = androidDriverFactory.createDriver(appiumServerUrl, capabilities);
        DriverManager.setDriver(driver);

        LOGGER.info("Android driver session ready");
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        LOGGER.info("Tearing down Android driver session");
        DriverManager.removeDriver();
    }

    /**
     * Returns the active driver session for the current test thread.
     *
     * @return the current thread's {@link AndroidDriver}
     */
    protected AndroidDriver getDriver() {

        return DriverManager.getDriver();
    }

    private static URL toUrl(String appiumServerUrl) {
        try {
            return new URL(appiumServerUrl);
        } catch (MalformedURLException e) {
            throw new DriverInitializationException(
                    "Configured appiumServerUrl is not a valid URL: " + appiumServerUrl, e);
        }
    }
}
