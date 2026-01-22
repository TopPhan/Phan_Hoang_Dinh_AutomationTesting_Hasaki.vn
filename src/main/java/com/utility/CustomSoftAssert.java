package com.utility;

import io.qameta.allure.Attachment;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.asserts.IAssert;
import org.testng.asserts.SoftAssert;
import com.log.logTest;

public class CustomSoftAssert extends SoftAssert {
    private WebDriver driver;

    public CustomSoftAssert(WebDriver driver) {
        this.driver = driver;
    }

    @Override
    public void onAssertFailure(IAssert<?> assertCommand, AssertionError ex) {
        logTest.error("❌ Soft Assert Failed: " + ex.getMessage());
        saveScreenshotToAllure();
    }

    @Attachment(value = "Soft Assert Failure Screenshot", type = "image/png")
    public byte[] saveScreenshotToAllure() {
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
    }
}
