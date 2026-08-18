package com.automation.mobile.driver;

import com.automation.mobile.exceptions.DriverInitializationException;
import io.appium.java_client.android.AndroidDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Owns the active {@link AndroidDriver} instance for the current thread
 * (one session per thread, via {@link ThreadLocal}), so the framework
 * stays safe for future parallel execution without any static shared
 * driver field.
 * <p>
 * Decouples "which driver is currently active" from "how a driver is
 * built" ({@link DriverFactory} implementations) so callers depend only
 * on this manager, never on a concrete factory or driver type.
 */
public final class DriverManager {

    private static final Logger LOGGER = LogManager.getLogger(DriverManager.class);
    private static final ThreadLocal<AndroidDriver> DRIVER_THREAD_LOCAL = new ThreadLocal<>();

    private DriverManager() {
    }

    /**
     * Stores the given driver as the active session for the current thread.
     *
     * @param driver the driver session to associate with this thread
     */
    public static void setDriver(AndroidDriver driver) {
        DRIVER_THREAD_LOCAL.set(driver);
    }

    /**
     * Returns the active driver session for the current thread.
     *
     * @return the current thread's driver
     * @throws DriverInitializationException if no driver has been set for this thread
     */
    public static AndroidDriver getDriver() {
        AndroidDriver driver = DRIVER_THREAD_LOCAL.get();
        if (driver == null) {
            throw new DriverInitializationException(
                    "No driver initialized for the current thread. Call DriverManager.setDriver() first.");
        }
        return driver;
    }

    /**
     * Quits the active driver session (if any) and clears the thread-local
     * reference for the current thread.
     */
    public static void removeDriver() {
        AndroidDriver driver = DRIVER_THREAD_LOCAL.get();

        try {
            if (driver != null) {
                LOGGER.info("Cleaning up Android driver session");
                driver.quit();
            }
        } finally {
            DRIVER_THREAD_LOCAL.remove();
        }
    }
}
