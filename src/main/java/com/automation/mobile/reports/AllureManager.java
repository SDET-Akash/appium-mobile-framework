package com.automation.mobile.reports;

import io.qameta.allure.Allure;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Helper for Allure-specific reporting concerns not covered by the
 * {@code allure-testng} adapter alone — currently limited to attaching
 * an already-captured screenshot file to the current Allure test result.
 */
public final class AllureManager {

    private static final Logger LOGGER =
            LogManager.getLogger(AllureManager.class);

    private AllureManager() {
    }

    /**
     * Attaches an already-saved screenshot file to the current Allure
     * test result. Reads the same file {@link
     * com.automation.mobile.utils.screenshot.ScreenshotUtil} already
     * saved to disk rather than capturing a second screenshot, so the
     * attachment always matches exactly what was logged/saved.
     * <p>
     * Failures to attach are logged and swallowed rather than
     * propagated — a reporting problem must never fail the test itself
     * or interfere with retry handling.
     *
     * @param screenshotPath absolute path to a previously saved PNG, as
     *                       returned by {@code ScreenshotUtil.takeScreenshot()}
     *                       (may be {@code null} if that capture failed)
     * @param name           label shown for the attachment in the report
     */
    public static void attachScreenshot(String screenshotPath, String name) {

        if (screenshotPath == null || screenshotPath.isBlank()) {
            return;
        }

        try {
            byte[] content = Files.readAllBytes(Path.of(screenshotPath));

            Allure.addAttachment(
                    name,
                    "image/png",
                    new ByteArrayInputStream(content),
                    "png"
            );

        } catch (Exception e) {
            LOGGER.error(
                    "Unable to attach screenshot to Allure report: {}",
                    screenshotPath,
                    e
            );
        }
    }
}
