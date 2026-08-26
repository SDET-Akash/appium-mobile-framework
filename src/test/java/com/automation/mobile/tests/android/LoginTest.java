package com.automation.mobile.tests.android;

import com.automation.mobile.base.BaseTest;

import com.automation.mobile.config.UserDataManager;
import com.automation.mobile.pages.android.DashboardPage;
import com.automation.mobile.pages.android.LoginPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LoginTest extends BaseTest {

    @Test
    public void verifyUserCanLoginSuccessfully() {

        UserDataManager userDataManager = new UserDataManager();

        LoginPage loginPage = new LoginPage(getDriver());

        loginPage
                .enterEmail(userDataManager.getValidUserEmail())
                .enterPassword(userDataManager.getValidUserPassword())
                .clickSignIn();

        DashboardPage dashboardPage = new DashboardPage(getDriver());

        Assert.assertTrue(
                dashboardPage.isDashboardDisplayed(),
                "Dashboard should be displayed after successful login"
        );
    }

    @Test
    public void verifyUserCannotLoginWithInvalidCredentials() {

        LoginPage loginPage = new LoginPage(getDriver());

        loginPage
                .enterEmail("wrong@yopmail.com")
                .enterPassword("WrongPassword")
                .clickSignIn();

        Assert.assertTrue(
                loginPage.isInvalidLoginMessageDisplayed(),
                "Invalid login error message should be displayed"
        );
    }
}