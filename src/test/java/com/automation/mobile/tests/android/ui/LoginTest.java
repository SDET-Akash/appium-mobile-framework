package com.automation.mobile.tests.android.ui;

import com.automation.mobile.base.BaseTest;
import com.automation.mobile.flows.LoginFlow;
import com.automation.mobile.pages.android.DashboardPage;
import com.automation.mobile.pages.android.LoginPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LoginTest extends BaseTest {

    @Test
    public void verifyUserCanLoginSuccessfully() {

        LoginFlow loginFlow = new LoginFlow(getDriver());

        DashboardPage dashboardPage = loginFlow.loginAsValidUser();

        Assert.assertTrue(
                dashboardPage.isDashboardDisplayed(),
                "Dashboard should be displayed after successful login"
        );
    }

    @Test
    public void verifyUserCannotLoginWithInvalidCredentials() {

        LoginFlow loginFlow = new LoginFlow(getDriver());

        LoginPage loginPage = loginFlow.loginAsInvalidUser();

        Assert.assertTrue(
                loginPage.isInvalidLoginMessageDisplayed(),
                "Invalid login error message should be displayed"
        );
    }
}