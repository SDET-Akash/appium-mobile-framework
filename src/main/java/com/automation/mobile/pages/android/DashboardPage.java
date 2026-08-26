package com.automation.mobile.pages.android;

import com.automation.mobile.base.BasePage;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.WebElement;

public class DashboardPage extends BasePage {

    @AndroidFindBy(id = "dashboard_home_tab")
    private WebElement dashboardTab;

    @AndroidFindBy(id = "dashboard_meetings_tab")
    private WebElement upcomingMeetingsTab;

    @AndroidFindBy(id = "dashboard_tasks_tab")
    private WebElement upcomingTasksTab;

    @AndroidFindBy(accessibility = "Default Dashboard")
    private WebElement dashboardName;

    @AndroidFindBy(accessibility = "Owner")
    private WebElement ownerText;

    public DashboardPage(AndroidDriver driver) {
        super(driver);
    }

    public boolean isDashboardDisplayed() {
        return isDisplayed(dashboardName);
    }

    public DashboardPage clickDashboardTab() {
        click(dashboardTab);
        return this;
    }

    public boolean isDashboardTabSelected() {
        return "true".equals(dashboardTab.getAttribute("selected"));
    }

    public DashboardPage clickUpcomingMeetings() {
        click(upcomingMeetingsTab);
        return this;
    }

    public boolean isUpcomingMeetingsTabSelected() {
        return "true".equals(
                upcomingMeetingsTab.getAttribute("selected")
        );
    }

    public DashboardPage clickUpcomingTasks() {
        click(upcomingTasksTab);
        return this;
    }

    public boolean isUpcomingTasksTabSelected() {
        return "true".equals(
                upcomingTasksTab.getAttribute("selected")
        );
    }

    public boolean isOwnerDisplayed() {
        return isDisplayed(ownerText);
    }
}