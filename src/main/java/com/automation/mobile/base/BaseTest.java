package com.automation.mobile.base;

import com.automation.mobile.config.CapabilityBuilder;
import com.automation.mobile.config.ConfigManager;
import com.automation.mobile.config.Environment;
import com.automation.mobile.config.EnvironmentManager;
import com.automation.mobile.driver.AndroidDriverFactory;
import com.automation.mobile.driver.DriverManager;
import com.automation.mobile.exceptions.DriverInitializationException;
import com.automation.mobile.utils.StorageStateManager;
import io.appium.java_client.android.AndroidDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

/**
 * Base class every test class extends.
 *
 * <p>Owns the method-level Android driver lifecycle:
 * resolving the current environment configuration,
 * building capabilities, creating the Android driver session,
 * and storing/releasing it through DriverManager.</p>
 *
 * <p>Subclasses can customize the test-session setup through
 * {@link #afterDriverSetup(ConfigManager)} without creating
 * their own independent TestNG lifecycle methods.</p>
 */
public class BaseTest {

    private static final Logger LOGGER =
            LogManager.getLogger(BaseTest.class);

    /**
     * Refreshes the authentication storage state once per suite run,
     * before any test's {@link #setUp()} executes, by performing one
     * real login and capturing the resulting session. This guarantees
     * {@link AuthenticatedBaseTest}-based tests always restore a fresh
     * session instead of a static file that can silently go stale.
     *
     * <p>TestNG runs a {@code @BeforeSuite} method exactly once per
     * suite regardless of how many classes inherit it. If this fails,
     * TestNG skips every test in the suite with this method's failure
     * attached as the reason, rather than letting each authenticated
     * test fail individually against a broken storage state.</p>
     */
    @BeforeSuite(alwaysRun = true)
    public void refreshStorageState() {
        StorageStateRefresher.refresh();
    }

    @BeforeMethod
    public void setUp() {

        Environment environment =
                EnvironmentManager.getEnvironment();

        LOGGER.info(
                "Setting up Android driver session for environment: {}",
                environment
        );

        ConfigManager configManager =
                new ConfigManager(environment);

        AndroidDriver driver =
                createAndroidDriver(configManager);

        DriverManager.setDriver(driver);

        LOGGER.info("Android driver session ready");

        afterDriverSetup(configManager);
    }

    /**
     * Builds capabilities and creates a new Android driver session for
     * the given configuration. Shared by the per-test {@link #setUp()}
     * lifecycle and {@link StorageStateRefresher}, which needs its own
     * driver session outside the normal per-method lifecycle.
     *
     * @param configManager configuration for the current environment
     * @return a newly created Android driver session
     */
    static AndroidDriver createAndroidDriver(ConfigManager configManager) {

        CapabilityBuilder capabilityBuilder =
                new CapabilityBuilder(configManager);

        Map<String, Object> capabilities =
                capabilityBuilder.build();

        URL appiumServerUrl =
                toUrl(configManager.getAppiumServerUrl());

        AndroidDriverFactory androidDriverFactory =
                new AndroidDriverFactory();

        return androidDriverFactory.createDriver(
                appiumServerUrl,
                capabilities
        );
    }

    /**
     * Hook executed after the Android driver has been created
     * and registered with DriverManager.
     *
     * <p>Subclasses can override this method when additional
     * session preparation is required.</p>
     *
     * @param configManager configuration for the current environment
     */
    protected void afterDriverSetup(ConfigManager configManager) {

        StorageStateManager.clear(configManager);

        String appPackage = configManager.getAppPackage();

        LOGGER.info(
                "Launching application: {}",
                appPackage
        );

        getDriver().activateApp(appPackage);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {

        LOGGER.info(
                "Tearing down Android driver session"
        );

        DriverManager.removeDriver();
    }

    /**
     * Returns the active driver session for the current test thread.
     *
     * @return the current thread's AndroidDriver
     */
    protected AndroidDriver getDriver() {

        return DriverManager.getDriver();
    }

    private static URL toUrl(String appiumServerUrl) {

        try {
            return new URL(appiumServerUrl);

        } catch (MalformedURLException e) {

            throw new DriverInitializationException(
                    "Configured appiumServerUrl is not a valid URL: "
                            + appiumServerUrl,
                    e
            );
        }
    }
}