package com.automation.mobile.flows;

import com.automation.mobile.config.EnvironmentManager;
import com.automation.mobile.config.UserDataManager;
import com.automation.mobile.pages.android.DashboardPage;
import com.automation.mobile.pages.android.LoginPage;
import io.appium.java_client.android.AndroidDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoginFlow {

    private static final Logger LOGGER = LogManager.getLogger(LoginFlow.class);

    private final AndroidDriver driver;
    private final UserDataManager userDataManager;

    public LoginFlow(AndroidDriver driver) {
        this.driver = driver;
        this.userDataManager =
                new UserDataManager(EnvironmentManager.getEnvironment());
    }

    public DashboardPage loginAsValidUser() {

        LOGGER.info("Starting valid user login flow");

        LoginPage loginPage = new LoginPage(driver);

        loginPage
                .enterEmail(userDataManager.getValidUserEmail())
                .enterPassword(userDataManager.getValidUserPassword())
                .clickSignIn();

        LOGGER.info("Valid user login flow completed");

        return new DashboardPage(driver);
    }

    public LoginPage loginAsInvalidUser() {

        LOGGER.info("Starting invalid user login flow");

        LoginPage loginPage = new LoginPage(driver);

        loginPage
                .enterEmail(userDataManager.getInvalidUserEmail())
                .enterPassword(userDataManager.getInvalidUserPassword())
                .clickSignIn();

        LOGGER.info("Invalid user login flow completed");

        return loginPage;
    }
}