package com.automation.mobile.config;

import com.automation.mobile.exceptions.ConfigurationException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Loads real login credentials from the environment-specific,
 * untracked {@code .env.<environment>} file at the project root (e.g.
 * {@code .env.qa}, {@code .env.stag}) — the sole, authoritative source
 * for credential values.
 * <p>
 * There is no fallback to the tracked {@code testdata/*.properties}
 * files: those hold only dummy placeholder values for documentation
 * purposes and are never read here. A missing {@code .env} file, a
 * missing key, or a blank value all fail loudly via {@link
 * ConfigurationException} rather than silently substituting a
 * placeholder.
 */
public class UserDataReader {

    private final Properties properties = new Properties();

    public UserDataReader(Environment environment) {
        loadProperties(environment);
    }

    private void loadProperties(Environment environment) {

        Path envFile = Path.of(
                System.getProperty("user.dir"),
                ".env." + environment.name().toLowerCase()
        );

        if (!Files.isRegularFile(envFile)) {
            throw new ConfigurationException(
                    "Credential file not found: " + envFile
                            + ". Create it locally at the project root with "
                            + "the required validUserEmail/validUserPassword/"
                            + "invalidUserEmail/invalidUserPassword keys "
                            + "before running authenticated tests."
            );
        }

        try (InputStream inputStream = Files.newInputStream(envFile)) {
            properties.load(inputStream);

        } catch (IOException e) {
            throw new ConfigurationException(
                    "Failed to load credential file: " + envFile,
                    e
            );
        }
    }

    public String get(String key) {

        String value = properties.getProperty(key);

        if (value == null || value.trim().isEmpty()) {
            throw new ConfigurationException(
                    "Required credential is missing or blank for key: '"
                            + key
                            + "'. Check the .env.<environment> file at the "
                            + "project root."
            );
        }

        return value.trim();
    }
}