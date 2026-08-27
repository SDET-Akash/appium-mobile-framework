package com.automation.mobile.pages.android;

import com.automation.mobile.base.BasePage;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebElement;

public class LoginPage extends BasePage {

    private static final Logger LOGGER =  LogManager.getLogger(LoginPage.class);



    @AndroidFindBy(id = "login_email_field")
    private WebElement emailField;

    @AndroidFindBy(id = "login_password_password_field")
    private WebElement passwordField;

    @AndroidFindBy(id = "login_sign_in_button")
    private WebElement signInButton;

    @AndroidFindBy(id = "login_forgot_password_button")
    private WebElement forgotPasswordButton;

    @AndroidFindBy(accessibility = "Uhoh! Looks like the details don't match. Please check and try again.")
    private WebElement invalidLoginMessage;


    public LoginPage(AndroidDriver driver) {
        super(driver);
    }


    public LoginPage enterEmail(String email) {
        LOGGER.debug("Entering email address");
        enterText(emailField, email);
        return this;
    }


    public LoginPage enterPassword(String password) {
        LOGGER.debug("Entering password");
        enterText(passwordField, password);
        return this;
    }


    public void clickSignIn() {
        LOGGER.info("Clicking Sign In button");
        click(signInButton);
    }


    public void clickForgotPassword() {
        LOGGER.info("Clicking Forgot Password button");
        click(forgotPasswordButton);
    }


    public boolean isInvalidLoginMessageDisplayed() {
        return isDisplayed(invalidLoginMessage);
    }
}