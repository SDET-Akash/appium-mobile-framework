package com.automation.mobile.tests.android;

import com.automation.mobile.base.BaseTest;
import com.automation.mobile.config.UserDataManager;
import com.automation.mobile.pages.android.DashboardPage;
import com.automation.mobile.pages.android.LoginPage;
import org.testng.Assert;
import org.testng.annotations.Test;

import static com.automation.mobile.driver.DriverManager.getDriver;

public class DashboardTest extends BaseTest {

    @Test
    public void verifyDashboardTabs() {

        // Login
        UserDataManager userDataManager = new UserDataManager();

        LoginPage loginPage = new LoginPage(getDriver());

        loginPage
                .enterEmail(userDataManager.getValidUserEmail())
                .enterPassword(userDataManager.getValidUserPassword())
                .clickSignIn();

        // Dashboard
        DashboardPage dashboardPage = new DashboardPage(getDriver());

        Assert.assertTrue(
                dashboardPage.isDashboardDisplayed(),
                "Dashboard should be displayed after login"
        );

        // Verify Dashboard tab
        dashboardPage.clickDashboardTab();

        Assert.assertTrue(
                dashboardPage.isDashboardTabSelected(),
                "Dashboard tab should be selected"
        );

        // Verify Upcoming Meetings tab
        dashboardPage.clickUpcomingMeetings();

        Assert.assertTrue(
                dashboardPage.isUpcomingMeetingsTabSelected(),
                "Upcoming Meetings tab should be selected"
        );

        // Verify Upcoming Tasks tab
        dashboardPage.clickUpcomingTasks();

        Assert.assertTrue(
                dashboardPage.isUpcomingTasksTabSelected(),
                "Upcoming Tasks tab should be selected"
        );
    }
}