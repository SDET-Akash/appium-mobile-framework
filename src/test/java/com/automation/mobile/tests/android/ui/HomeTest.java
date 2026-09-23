package com.automation.mobile.tests.android.ui;

import com.automation.mobile.base.AuthenticatedBaseTest;
import com.automation.mobile.pages.android.HomePage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class HomeTest extends AuthenticatedBaseTest {

    @Test
    public void verifyHomeNavigation() {

        HomePage homePage = new HomePage(getDriver());

        homePage.openNavigationDrawer();

        Assert.assertTrue(
                homePage.isDashboardMenuDisplayed(),
                "Dashboard menu should be displayed"
        );

        Assert.assertTrue(
                homePage.isLeadsMenuDisplayed(),
                "Leads menu should be displayed"
        );

        Assert.assertTrue(
                homePage.isContactsMenuDisplayed(),
                "Contacts menu should be displayed"
        );

        Assert.assertTrue(
                homePage.isDealsMenuDisplayed(),
                "Deals menu should be displayed"
        );

        Assert.assertTrue(
                homePage.isCompaniesMenuDisplayed(),
                "Companies menu should be displayed"
        );

        Assert.assertTrue(
                homePage.isQuotationsMenuDisplayed(),
                "Quotations menu should be displayed"
        );

        Assert.assertTrue(
                homePage.isTasksMenuDisplayed(),
                "Tasks menu should be displayed"
        );

        Assert.assertTrue(
                homePage.isMeetingsMenuDisplayed(),
                "Meetings menu should be displayed"
        );
    }
}