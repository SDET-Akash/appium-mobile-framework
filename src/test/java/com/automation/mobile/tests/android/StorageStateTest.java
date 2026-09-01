package com.automation.mobile.tests.android;

import com.automation.mobile.config.ConfigManager;
import com.automation.mobile.config.Environment;
import com.automation.mobile.config.EnvironmentManager;
import com.automation.mobile.utils.StorageStateManager;
import org.testng.annotations.Test;

public class StorageStateTest {

    @Test
    public void verifyStorageStateCanBeRestored() {

        Environment environment =
                EnvironmentManager.getEnvironment();

        ConfigManager configManager =
                new ConfigManager(environment);

        StorageStateManager.restore(configManager);
    }
}