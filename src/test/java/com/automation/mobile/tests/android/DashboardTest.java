package com.automation.mobile.tests.android;

import com.automation.mobile.base.AuthenticatedBaseTest;
import com.automation.mobile.pages.android.DashboardPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class DashboardTest extends AuthenticatedBaseTest {

    @Test
    public void verifyDashboardTabs() {

        DashboardPage dashboardPage =
                new DashboardPage(getDriver());

        // Verify Dashboard
        Assert.assertTrue(
                dashboardPage.isDashboardDisplayed(),
                "Dashboard should be displayed after authentication"
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