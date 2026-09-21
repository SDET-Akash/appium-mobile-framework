package com.automation.mobile.base;

import com.automation.mobile.config.ConfigManager;
import com.automation.mobile.config.Environment;
import com.automation.mobile.config.EnvironmentManager;
import com.automation.mobile.exceptions.ConfigurationException;
import com.automation.mobile.flows.LoginFlow;
import com.automation.mobile.pages.android.DashboardPage;
import com.automation.mobile.utils.StorageStateManager;
import io.appium.java_client.android.AndroidDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Refreshes the authentication storage state by performing one real
 * login and capturing the resulting session, so {@link
 * AuthenticatedBaseTest}-based tests always restore a guaranteed-fresh
 * session rather than a static file that can silently expire between
 * runs. See {@link BaseTest#refreshStorageState()} for when this runs.
 */
final class StorageStateRefresher {

    private static final Logger LOGGER =
            LogManager.getLogger(StorageStateRefresher.class);

    private StorageStateRefresher() {
    }

    static void refresh() {

        Environment environment =
                EnvironmentManager.getEnvironment();

        ConfigManager configManager =
                new ConfigManager(environment);

        LOGGER.info(
                "Refreshing authentication storage state for environment {} before suite execution",
                environment
        );

        AndroidDriver driver = null;

        try {
            driver = BaseTest.createAndroidDriver(configManager);

            StorageStateManager.clear(configManager);

            driver.activateApp(configManager.getAppPackage());

            LoginFlow loginFlow = new LoginFlow(driver);

            DashboardPage dashboardPage =
                    loginFlow.loginAsValidUser();

            if (!dashboardPage.isDashboardDisplayed()) {
                throw new ConfigurationException(
                        "Storage state refresh login did not reach the Dashboard. "
                                + "Check credentials in testdata/"
                                + environment.name().toLowerCase()
                                + ".properties and QA backend availability."
                );
            }

            StorageStateManager.capture(configManager);

            LOGGER.info(
                    "Storage state refreshed successfully; the suite will use a fresh authenticated session"
            );

        } catch (Exception e) {

            LOGGER.error(
                    "========== STORAGE STATE REFRESH FAILED =========="
            );
            LOGGER.error(
                    "Could not establish a fresh authenticated session before the suite started. "
                            + "Every AuthenticatedBaseTest-based test would fail against a missing or stale "
                            + "storage state, so the suite is being aborted here instead of letting each of "
                            + "them fail individually.",
                    e
            );

            throw new ConfigurationException(
                    "Storage state refresh failed at suite startup: " + e.getMessage(),
                    e
            );

        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
    }
}
