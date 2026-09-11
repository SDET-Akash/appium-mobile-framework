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
        return waitForTabSelected(dashboardTab);
    }

    public DashboardPage clickUpcomingMeetings() {
        click(upcomingMeetingsTab);
        return this;
    }

    public boolean isUpcomingMeetingsTabSelected() {
        return waitForTabSelected(upcomingMeetingsTab);
    }

    public DashboardPage clickUpcomingTasks() {
        click(upcomingTasksTab);
        return this;
    }

    public boolean isUpcomingTasksTabSelected() {
        return waitForTabSelected(upcomingTasksTab);
    }

    public boolean isOwnerDisplayed() {
        return isDisplayed(ownerText);
    }

    /**
     * Waits for a bottom-nav tab to report {@code selected=true}.
     *
     * <p>The preceding tab switch can still be loading/rendering its own
     * content when the next tab is tapped, and the tap is occasionally
     * dropped as a result (confirmed via page-source captures showing the
     * previous tab still selected and the new tab's content never loaded).
     * A single retry click, driven by the same "selected" condition rather
     * than a fixed delay, recovers from that dropped tap deterministically.</p>
     */
    private boolean waitForTabSelected(WebElement tab) {
        try {
            return waitForAttribute(tab, "selected", "true");
        } catch (RuntimeException firstAttemptFailed) {
            click(tab);
            return waitForAttribute(tab, "selected", "true");
        }
    }
}
