package com.automation.mobile.base;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class BasePage {

    protected final AndroidDriver driver;
    protected final WebDriverWait wait;

    private static final Logger LOGGER = LogManager.getLogger(BasePage.class);

    private static final Duration DEFAULT_WAIT = Duration.ofSeconds(15);

    public BasePage(AndroidDriver driver) {
        this.driver = driver;

        this.wait = new WebDriverWait(driver, DEFAULT_WAIT);

        LOGGER.debug("Initializing page: {}", getClass().getSimpleName());

        PageFactory.initElements(
                new AppiumFieldDecorator(
                        driver,
                        DEFAULT_WAIT
                ),
                this
        );

        LOGGER.debug(
                "Page initialized successfully: {}",
                getClass().getSimpleName()
        );
    }

    protected void click(WebElement element) {

        LOGGER.debug("Waiting for element to be clickable");

        wait.until(
                ExpectedConditions.elementToBeClickable(element)
        );

        LOGGER.debug("Clicking element");

        element.click();
    }

    protected void enterText(WebElement element, String text) {

        LOGGER.debug("Waiting for element to be visible");

        wait.until(
                ExpectedConditions.visibilityOf(element)
        );

        LOGGER.debug("Entering text into element");

        element.click();
        element.clear();
        element.sendKeys(text);
    }

    protected void clearAndEnterText(WebElement element, String text) {

        LOGGER.debug("Waiting for element to be visible");

        wait.until(
                ExpectedConditions.visibilityOf(element)
        );

        LOGGER.debug("Clearing and entering text into element");

        element.clear();
        element.sendKeys(text);
    }

    protected String getText(WebElement element) {

        LOGGER.debug("Waiting for element text");

        wait.until(ExpectedConditions.visibilityOf(element));

        return element.getText();
    }

    protected boolean isDisplayed(WebElement element) {

        LOGGER.debug("Checking element visibility");

        try {
            return wait.until(
                    ExpectedConditions.visibilityOf(element)
            ).isDisplayed();

        } catch (Exception e) {

            LOGGER.debug("Element was not displayed");

            return false;
        }
    }
}