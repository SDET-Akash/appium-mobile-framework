package com.automation.mobile.config;

import com.automation.mobile.exceptions.ConfigurationException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class UserDataReader {

    private final Properties properties = new Properties();

    public UserDataReader(Environment environment) {
        loadProperties(environment);
    }

    private void loadProperties(Environment environment) {

        String resourceName =
                "testdata/" + environment.name().toLowerCase() + ".properties";

        try (InputStream inputStream =
                     getClass().getClassLoader()
                             .getResourceAsStream(resourceName)) {

            if (inputStream == null) {
                throw new ConfigurationException(
                        "User data file not found on classpath: "
                                + resourceName
                );
            }

            properties.load(inputStream);

        } catch (IOException e) {
            throw new ConfigurationException(
                    "Failed to load user data file: "
                            + resourceName,
                    e
            );
        }
    }

    public String get(String key) {

        String value = properties.getProperty(key);

        if (value == null || value.trim().isEmpty()) {
            throw new ConfigurationException(
                    "User data value is missing or blank for key: " + key
            );
        }

        return value.trim();
    }
}