package com.automation.mobile.tests.android;

import com.automation.mobile.base.BaseTest;
import com.automation.mobile.flows.LoginFlow;
import com.automation.mobile.pages.android.DashboardPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class DashboardTest extends BaseTest {

    @Test
    public void verifyDashboardTabs() {

        // Login
        LoginFlow loginFlow = new LoginFlow(getDriver());

        DashboardPage dashboardPage = loginFlow.loginAsValidUser();

        // Verify Dashboard
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