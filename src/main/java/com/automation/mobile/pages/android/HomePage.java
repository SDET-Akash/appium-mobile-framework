package com.automation.mobile.pages.android;

import com.automation.mobile.base.BasePage;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.WebElement;

public class HomePage extends BasePage {

    @AndroidFindBy(
            accessibility = "home_appbar_hamburger_icon"
    )
    private WebElement hamburgerMenu;

    @AndroidFindBy(
            uiAutomator = "new UiSelector().resourceIdMatches(\".*:id/nav_drawer_nav_dashboard_list_item\")"
    )
    private WebElement dashboardMenu;

    @AndroidFindBy(
            uiAutomator = "new UiSelector().resourceIdMatches(\".*:id/nav_drawer_nav_lead_list_item\")"
    )
    private WebElement leadsMenu;

    @AndroidFindBy(
            uiAutomator = "new UiSelector().resourceIdMatches(\".*:id/nav_drawer_nav_contact_list_item\")"
    )
    private WebElement contactsMenu;

    @AndroidFindBy(
            uiAutomator = "new UiSelector().resourceIdMatches(\".*:id/nav_drawer_nav_deal_list_item\")"
    )
    private WebElement dealsMenu;

    @AndroidFindBy(
            uiAutomator = "new UiSelector().resourceIdMatches(\".*:id/nav_drawer_nav_company_list_item\")"
    )
    private WebElement companiesMenu;

    @AndroidFindBy(
            uiAutomator = "new UiSelector().resourceIdMatches(\".*:id/nav_drawer_nav_quotation_list_item\")"
    )
    private WebElement quotationsMenu;

    @AndroidFindBy(
            uiAutomator = "new UiSelector().resourceIdMatches(\".*:id/nav_drawer_nav_task_list_item\")"
    )
    private WebElement tasksMenu;

    @AndroidFindBy(
            uiAutomator = "new UiSelector().resourceIdMatches(\".*:id/nav_drawer_nav_meeting_list_item\")"
    )
    private WebElement meetingsMenu;

    public HomePage(AndroidDriver driver) {
        super(driver);
    }

    public void openNavigationDrawer() {
        click(hamburgerMenu);
    }

    public void navigateToDashboard() {
        openNavigationDrawer();
        click(dashboardMenu);
    }

    public boolean isDashboardMenuDisplayed() {
        return isDisplayed(dashboardMenu);
    }

    public LeadPage navigateToLeads() {
        openNavigationDrawer();
        click(leadsMenu);
        return new LeadPage(driver);
    }

    public void navigateToContacts() {
        openNavigationDrawer();
        click(contactsMenu);
    }

    public void navigateToDeals() {
        openNavigationDrawer();
        click(dealsMenu);
    }

    public void navigateToCompanies() {
        openNavigationDrawer();
        click(companiesMenu);
    }

    public void navigateToQuotations() {
        openNavigationDrawer();
        click(quotationsMenu);
    }

    public void navigateToTasks() {
        openNavigationDrawer();
        click(tasksMenu);
    }

    public void navigateToMeetings() {
        openNavigationDrawer();
        click(meetingsMenu);
    }

    public boolean isLeadsMenuDisplayed() {
        return isDisplayed(leadsMenu);
    }

    public boolean isContactsMenuDisplayed() {
        return isDisplayed(contactsMenu);
    }

    public boolean isDealsMenuDisplayed() {
        return isDisplayed(dealsMenu);
    }

    public boolean isCompaniesMenuDisplayed() {
        return isDisplayed(companiesMenu);
    }

    public boolean isQuotationsMenuDisplayed() {
        return isDisplayed(quotationsMenu);
    }

    public boolean isTasksMenuDisplayed() {
        return isDisplayed(tasksMenu);
    }

    public boolean isMeetingsMenuDisplayed() {
        return isDisplayed(meetingsMenu);
    }
}