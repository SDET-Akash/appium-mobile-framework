package com.automation.mobile.utils;

import com.automation.mobile.config.ConfigManager;
import com.automation.mobile.config.Environment;
import com.automation.mobile.exceptions.ConfigurationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.automation.mobile.config.Environment.PROD;

/**
 * Manages restoration of an authenticated Android application state.
 *
 * <p>The storage state is maintained as a test resource and restored into
 * the application's FlutterSharedPreferences.xml using adb/run-as.</p>
 */
public final class StorageStateManager {

    private static final Logger LOGGER =
            LogManager.getLogger(StorageStateManager.class);

//    private static final String STORAGE_STATE_RESOURCE =
//            "qa/storageStates/user.xml";

    private static final String SHARED_PREFERENCES_PATH =
            "shared_prefs/FlutterSharedPreferences.xml";

    private StorageStateManager() {
    }

    /**
     * Restores the configured storage state to the Android application.
     *
     * @param configManager framework configuration
     */
    public static void restore(ConfigManager configManager) {

        if (configManager == null) {
            throw new ConfigurationException(
                    "ConfigManager must not be null"
            );
        }

        String appPackage = configManager.getAppPackage();

        LOGGER.info(
                "Restoring authentication storage state for application: {}",
                appPackage
        );

        Path storageState =
                resolveStorageState(configManager.getEnvironment());

        if (!Files.isRegularFile(storageState)) {
            throw new ConfigurationException(
                    "Storage state is not a regular file: " + storageState
            );
        }

        stopApplication(appPackage);
        restorePreferences(appPackage, storageState);

        LOGGER.info(
                "Authentication storage state restored successfully"
        );
    }

    /**
     * Resolves the storage state from the test classpath.
     *
     * @return absolute path to the storage-state file
     */
    private static Path resolveStorageState(
            Environment environment
    ) {

        String storageStateResource;

        switch (environment) {
            case QA:
                storageStateResource =
                        "qa/storageStates/user.xml";
                break;

            case STAG:
                storageStateResource =
                        "stage/storageStates/user.xml";
                break;

            case PROD:
                throw new ConfigurationException(
                        "Storage state is not configured for PROD"
                );

            default:
                throw new ConfigurationException(
                        "Unsupported environment: " + environment
                );
        }

        try {
            var resource = StorageStateManager.class
                    .getClassLoader()
                    .getResource(storageStateResource);

            if (resource == null) {
                throw new ConfigurationException(
                        "Storage state file not found on classpath: "
                                + storageStateResource
                );
            }

            return Path.of(resource.toURI());

        } catch (Exception e) {
            throw new ConfigurationException(
                    "Failed to resolve storage state: "
                            + storageStateResource,
                    e
            );
        }
    }

    /**
     * Stops the application before modifying its preferences.
     */
    private static void stopApplication(String appPackage) {

        executeAdb(
                "shell",
                "am",
                "force-stop",
                appPackage
        );

        LOGGER.debug(
                "Application stopped: {}",
                appPackage
        );
    }

    /**
     * Pushes the storage-state file to the device and copies it into
     * the application's SharedPreferences directory using run-as.
     */
    private static void restorePreferences(
            String appPackage,
            Path storageState
    ) {

        final String tempFile =
                "/data/local/tmp/storage_state.xml";

        LOGGER.debug(
                "Pushing storage state to device: {}",
                storageState
        );

        executeAdb(
                "push",
                storageState.toString(),
                tempFile
        );

        LOGGER.debug(
                "Copying storage state into application preferences"
        );

        executeAdb(
                "shell",
                "run-as",
                appPackage,
                "cp",
                tempFile,
                SHARED_PREFERENCES_PATH
        );

        LOGGER.debug(
                "Removing temporary storage state from device"
        );

        executeAdb(
                "shell",
                "rm",
                tempFile
        );
    }

    /**
     * Executes an adb command and fails when the command is unsuccessful.
     */
    private static void executeAdb(String... arguments) {

        ProcessBuilder processBuilder =
                new ProcessBuilder(buildAdbCommand(arguments));

        processBuilder.redirectErrorStream(true);

        try {
            Process process = processBuilder.start();

            String output = new String(
                    process.getInputStream().readAllBytes(),
                    StandardCharsets.UTF_8
            );

            int exitCode = process.waitFor();

            if (exitCode != 0) {
                throw new ConfigurationException(
                        "ADB command failed. Exit code: "
                                + exitCode
                                + ". Output: "
                                + output
                );
            }

        } catch (IOException e) {
            throw new ConfigurationException(
                    "Failed to execute adb command",
                    e
            );

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

            throw new ConfigurationException(
                    "ADB command was interrupted",
                    e
            );
        }
    }

    /**
     * Builds the complete adb command.
     *
     * @param arguments adb command arguments
     * @return command array beginning with adb
     */
    private static String[] buildAdbCommand(String[] arguments) {

        String[] command = new String[arguments.length + 1];

        command[0] = "adb";

        System.arraycopy(
                arguments,
                0,
                command,
                1,
                arguments.length
        );

        return command;
    }
}