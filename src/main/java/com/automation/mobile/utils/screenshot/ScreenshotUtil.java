package com.automation.mobile.utils.screenshot;
import io.appium.java_client.android.AndroidDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ScreenshotUtil {

    private static final Logger LOGGER =
            LogManager.getLogger(ScreenshotUtil.class);

    private static final String SCREENSHOT_DIRECTORY =
            "target/screenshots";

    private ScreenshotUtil() {
        // Utility class
    }

    public static String takeScreenshot(
            AndroidDriver driver,
            String testName) {

        try {
            File source = driver.getScreenshotAs(OutputType.FILE);

            Path directory = Paths.get(SCREENSHOT_DIRECTORY);

            Files.createDirectories(directory);

            String fileName =
                    testName + "_" + System.currentTimeMillis() + ".png";

            Path destination = directory.resolve(fileName);

            Files.copy(
                    source.toPath(),
                    destination
            );

            LOGGER.info(
                    "Screenshot saved: {}",
                    destination.toAbsolutePath()
            );

            return destination.toAbsolutePath().toString();

        } catch (IOException e) {

            LOGGER.error(
                    "Failed to save screenshot for test: {}",
                    testName,
                    e
            );

            return null;
        }
    }
}