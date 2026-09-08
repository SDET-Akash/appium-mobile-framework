package com.automation.mobile.config;

import com.automation.mobile.exceptions.ConfigurationException;

public final class EnvironmentManager {

    private static final String ENV_PROPERTY = "env";
    private static final String DEFAULT_ENVIRONMENT = "qa";

    private EnvironmentManager() {
        // Utility class
    }

    public static Environment getEnvironment() {

        String environment =
                System.getProperty(ENV_PROPERTY, DEFAULT_ENVIRONMENT);

        try {
            return Environment.valueOf(environment.toUpperCase());

        } catch (IllegalArgumentException e) {

            throw new ConfigurationException(
                    "Invalid environment: " + environment
                            + ". Supported environments: QA, STAG, PROD",
                    e
            );
        }
    }
}