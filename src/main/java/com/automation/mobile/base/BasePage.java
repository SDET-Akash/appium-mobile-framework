package com.automation.mobile.base;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

import java.time.Duration;

public class BasePage  {

    protected final AndroidDriver driver;

    public BasePage(AndroidDriver driver) {
        this.driver = driver;

        PageFactory.initElements(
                new AppiumFieldDecorator(
                        driver,
                        Duration.ofSeconds(15)
                ),
                this
        );
    }

    protected void click(WebElement element) {
        element.click();
    }

    protected void enterText(WebElement element, String text) {
        element.click();
        element.clear();
        element.sendKeys(text);
    }

    protected void clearAndEnterText(WebElement element, String text) {
        element.clear();
        element.sendKeys(text);
    }

    protected String getText(WebElement element) {
        return element.getText();
    }

    protected boolean isDisplayed(WebElement element) {
        return element.isDisplayed();
    }
}