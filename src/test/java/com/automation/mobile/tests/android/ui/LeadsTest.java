package com.automation.mobile.tests.android.ui;

import com.automation.mobile.base.AuthenticatedBaseTest;
import com.automation.mobile.pages.android.HomePage;
import com.automation.mobile.pages.android.LeadPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LeadsTest extends AuthenticatedBaseTest {

    @Test
    public void verifyLeadListing() {

        HomePage homePage = new HomePage(getDriver());

        LeadPage leadPage = homePage.navigateToLeads();

        Assert.assertTrue(
                leadPage.isLeadListingDisplayed(),
                "Lead listing screen is not displayed"
        );
    }
}