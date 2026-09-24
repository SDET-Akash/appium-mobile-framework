
package com.automation.mobile.reports;

import com.automation.mobile.config.ConfigManager;
import io.qameta.allure.Allure;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class AllureManager {

    private static final Logger LOGGER =
            LogManager.getLogger(AllureManager.class);

    private static final Path ALLURE_RESULTS_DIRECTORY =
            Path.of("allure-results");

    private AllureManager() {
    }

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

    public static void writeEnvironmentProperties(
            ConfigManager configManager) {

        try {
            Files.createDirectories(ALLURE_RESULTS_DIRECTORY);

            String environmentProperties = String.join(
                    System.lineSeparator(),
                    "Environment=" + configManager.getEnvironment(),
                    "Platform=" + configManager.getPlatform(),
                    "AutomationName=" + configManager.getAutomationName(),
                    "DeviceName=" + configManager.getDeviceName(),
                    "AppPackage=" + configManager.getAppPackage()
            );

            Path environmentFile =
                    ALLURE_RESULTS_DIRECTORY.resolve("environment.properties");

            Files.writeString(
                    environmentFile,
                    environmentProperties + System.lineSeparator(),
                    StandardCharsets.UTF_8
            );

            LOGGER.info(
                    "Allure environment properties generated: {}",
                    environmentFile.toAbsolutePath()
            );

        } catch (Exception e) {
            LOGGER.error(
                    "Unable to generate Allure environment properties",
                    e
            );
        }
    }
}