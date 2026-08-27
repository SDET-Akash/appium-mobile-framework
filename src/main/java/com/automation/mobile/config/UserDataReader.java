package com.automation.mobile.config;

import com.automation.mobile.exceptions.ConfigurationException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class UserDataReader {

    private final Properties properties = new Properties();
    private static final String USER_DATA_FILE =
            "testdata/users.properties";

    public UserDataReader() {
        loadProperties();
    }

    private void loadProperties() {

        try (InputStream inputStream =
                     getClass().getClassLoader()
                             .getResourceAsStream(USER_DATA_FILE)) {

            if (inputStream == null) {
                throw new ConfigurationException(
                        "User data file not found on classpath: "
                                + USER_DATA_FILE
                );
            }

            properties.load(inputStream);

        } catch (IOException e) {
            throw new ConfigurationException(
                    "Failed to load user data file: "
                            + USER_DATA_FILE,
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