package com.automation.mobile.config;

import com.automation.mobile.exceptions.ConfigurationException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {

    private final Environment environment;
    private final Properties properties;

    public ConfigReader(Environment environment) {
        this.environment = environment;
        this.properties = loadProperties(environment);
    }

    public static Environment getEnvironment() {
        String env = System.getProperty("env", "QA");
        return Environment.valueOf(env.toUpperCase());
    }

    public String get(String key) {
        String value = properties.getProperty(key);

        if (value == null) {
            throw new ConfigurationException(
                    "Missing required property '" + key +
                            "' for environment " + environment
            );
        }

        return value;
    }

    private Properties loadProperties(Environment environment) {

        String resourceName =
                "config/" +
                        environment.name().toLowerCase() +
                        ".properties";

        Properties loaded = new Properties();

        try (InputStream inputStream =
                     ConfigReader.class
                             .getClassLoader()
                             .getResourceAsStream(resourceName)) {

            if (inputStream == null) {
                throw new ConfigurationException(
                        "Configuration file not found on classpath: "
                                + resourceName
                );
            }

            loaded.load(inputStream);

        } catch (IOException e) {
            throw new ConfigurationException(
                    "Failed to load configuration file: "
                            + resourceName,
                    e
            );
        }

        return loaded;
    }
}