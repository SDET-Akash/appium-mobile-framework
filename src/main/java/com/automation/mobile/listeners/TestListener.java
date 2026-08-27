package com.automation.mobile.listeners;
import com.automation.mobile.utils.screenshot.ScreenshotUtil;
import com.automation.mobile.driver.DriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestListener implements ITestListener {

    private static final Logger LOGGER = LogManager.getLogger(TestListener.class);

    @Override
    public void onTestStart(ITestResult result) {
        LOGGER.info(
                "========== TEST STARTED: {} ==========",
                result.getName()
        );
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        LOGGER.info(
                "========== TEST PASSED: {} ==========",
                result.getName()
        );
    }

    @Override
    public void onTestFailure(ITestResult result) {

        LOGGER.error(
                "========== TEST FAILED: {} ==========",
                result.getName()
        );

        LOGGER.error(
                "Failure reason: {}",
                result.getThrowable() != null
                        ? result.getThrowable().getMessage()
                        : "Unknown failure"
        );

        try {

            if (DriverManager.getDriver() != null) {

                String screenshotPath =
                        ScreenshotUtil.takeScreenshot(
                                DriverManager.getDriver(),
                                result.getName()
                        );

                LOGGER.error(
                        "Failure screenshot: {}",
                        screenshotPath
                );
            }

        } catch (Exception e) {

            LOGGER.error(
                    "Unable to capture failure screenshot",
                    e
            );
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        LOGGER.warn(
                "========== TEST SKIPPED: {} ==========",
                result.getName()
        );
    }
}