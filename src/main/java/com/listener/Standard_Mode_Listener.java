package com.listener;

import com.bases.multipleThread_baseSetup;
import com.log.logTest;
import com.utility.Capture.CaptureScreenshot;
import com.utility.Capture.CaptureVideo;
import io.qameta.allure.Attachment;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class Standard_Mode_Listener implements ITestListener {

    private CaptureScreenshot captureScreenshot;

    public String getTestName(ITestResult result) {
        return result.getTestName() != null ? result.getTestName()
                : result.getMethod().getConstructorOrMethod().getName();
    }

    public String getTestDescription(ITestResult result) {
        return result.getMethod().getDescription() != null ? result.getMethod().getDescription() : getTestName(result);
    }

    //Text attachments for Allure
    @Attachment(value = "{0}", type = "text/plain")
    public static String saveTextLog(String message) {
        return message;
    }

    //HTML attachments for Allure
    @Attachment(value = "{0}", type = "text/html")
    public static String attachHtml(String html) {
        return html;
    }

    //Text attachments for Allure
    @Attachment(value = "Page screenshot", type = "image/png")
    public byte[] saveScreenshotPNG(WebDriver driver) {
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
    }

    private void stopRecordVideo() {
        try {
            CaptureVideo.stopRecord();
        } catch (Exception e) {
            logTest.error("⚠️ Can't stop record video: " + e.getMessage());
        }
    }


    @Override
    public void onFinish(ITestContext result) {

        // Create file enviroment.properties for reports
            try {
                Properties props = new Properties();
                props.setProperty("Tester", "Phan Hoang Dinh");
                props.setProperty("Browser", "Chrome & Edge (Parallel)");
                props.setProperty("Java Version", System.getProperty("java.version"));
                props.setProperty("OS", System.getProperty("os.name"));
                props.setProperty("Project", "Hasaki.vn Automation");

                // File environment path
                File allureResultsDir = new File("allure-results/");
                if (!allureResultsDir.exists()) {
                    allureResultsDir.mkdirs(); // Tạo thư mục nếu chưa có
                }

                // Create file environment.properties
                File envFile = new File(allureResultsDir, "environment.properties");
                FileOutputStream fos = new FileOutputStream(envFile);
                props.store(fos, "Allure Environment Properties");
                fos.close();

                // Log file successfully created
                System.out.println("Allure Environment file created at: " + envFile.getAbsolutePath());
            } catch (Exception e) {
                logTest.error("[FAIL] to create Allure Environment file: " + e.getMessage());
            }

    }

    @Override
    public void onStart(ITestContext result) {

    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {

    }

    @Override
    public void onTestStart(ITestResult result) {
        Object instance = result.getInstance();
        if (instance instanceof multipleThread_baseSetup) {
            multipleThread_baseSetup baseSetup = (multipleThread_baseSetup) instance;
            String browserName = baseSetup.getBrowserName();
            try {
                String testName = result.getName();
                String timestamp = new SimpleDateFormat("HH-mm-ss").format(new Date());
                String videoName = browserName + "_" + testName + "_" + timestamp;

                result.setAttribute("videoName", videoName);

                // Luồng nào sẽ bắt đầu ghi video cho luồng đó
                CaptureVideo.startRecord(videoName);
                logTest.info("🎥 [THREAD-" + Thread.currentThread().getId() + "] Start recording: " + videoName);
            } catch (Exception e) {
                logTest.error("❌ Can't open screen record: " + e.getMessage());
            }
        }

    }

    @Override
    public void onTestSuccess(ITestResult result) {

        logTest.info("This test case is success : " + result.getName());

        // Stop Record video
        stopRecordVideo();

        // Get currently Browser name
        multipleThread_baseSetup baseSetup = (multipleThread_baseSetup) result.getInstance();
        String browserName = baseSetup.getBrowserName();

        //Get video attribute in test
        String videoName = (String) result.getAttribute("videoName");

        // Delete video if test passed
        CaptureVideo.deleteVideo(videoName);

    }

    @Override
    public void onTestFailure(ITestResult result) {
        logTest.error("[FAIL] This test case is failed: " + result.getName());
        // Stop Record video
        stopRecordVideo();

        // Get driver of current class on running test.
        Object currentClass = result.getInstance();
        WebDriver driver = ((multipleThread_baseSetup) currentClass).getDriver();

        if (driver!=null) {// Take screenshot if test is failed.
            try {

                if (captureScreenshot == null) {
                    captureScreenshot = new CaptureScreenshot();
                }

                captureScreenshot.takeScreenshot(driver, result.getName()+"_FAIL");

                //Allure Screenshot custom
                saveScreenshotPNG(driver);
                //Save a log on Allure report.
                saveTextLog(result.getName()+ " failed and screenshot taken!");

            } catch (InterruptedException e) {
                logTest.error("[FAIL] Error while taking screenshot: " + e.getMessage());
            }
        }

        // Get (Object[])  DataProvider currently load to test
        Object[] parameters = result.getParameters();
        if (parameters.length > 0) {
            // Log error
            logTest.error("Data causing failure: " + parameters[0]);
            saveTextLog("Data causing failure: " + parameters[0]);
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        logTest.info("This test case is skip: " + result.getName());

        // Stop Record video
        stopRecordVideo();

    }

}
