package com.automation.mobile.pages.android;

import com.automation.mobile.base.BasePage;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.WebElement;

public class LeadPage extends BasePage {

    @AndroidFindBy(
            accessibility = "home_appbar_title_link"
    )
    private WebElement leadListingTitle;

    public LeadPage(AndroidDriver driver) {
        super(driver);
    }

    // ==========================================
    // Lead Listing
    // ==========================================

    public boolean isLeadListingDisplayed() {
        return isDisplayed(leadListingTitle);
    }

    // ==========================================
    // Lead Details
    // ==========================================

    // Add verified Lead detail locators here

    // ==========================================
    // Lead CRUD Operations
    // ==========================================

    // Add later as screens become available
}