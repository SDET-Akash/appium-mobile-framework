package com.automation.mobile.base;

import com.automation.mobile.config.ConfigManager;
import com.automation.mobile.utils.StorageStateManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Base class for tests that require an authenticated application session.
 *
 * <p>The Android driver is created by {@link BaseTest} first.
 * Once the driver exists, the authentication storage state is
 * restored and the application is launched again so that it
 * starts with the restored authenticated session.</p>
 */
public class AuthenticatedBaseTest extends BaseTest {

    private static final Logger LOGGER =
            LogManager.getLogger(AuthenticatedBaseTest.class);

    @Override
    protected void afterDriverSetup(
            ConfigManager configManager
    ) {

        LOGGER.info(
                "Preparing authenticated application session"
        );

        StorageStateManager.restore(configManager);

        String appPackage =
                configManager.getAppPackage();

        LOGGER.info(
                "Launching application with restored authentication: {}",
                appPackage
        );

        getDriver().activateApp(appPackage);

        LOGGER.info(
                "Authenticated application session ready"
        );
    }
}