package com.automation.mobile.config;

/**
 * Represents the supported application environments for mobile
 * automation. Used by {@link ConfigManager} to select which
 * environment-specific configuration source to load.
 */
public enum Environment {
    QA,
    STAG,
    PROD
}
