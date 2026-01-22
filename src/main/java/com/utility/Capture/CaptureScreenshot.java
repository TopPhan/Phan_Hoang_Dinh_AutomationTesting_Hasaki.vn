package com.utility.Capture;

import com.log.logTest;
import com.utility.PropertiesFile;
import com.utility.Helpers.ValidateHelper;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.io.FileHandler;
import org.testng.Reporter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CaptureScreenshot {
    private ValidateHelper validateHelper;
    //Lấy đường dẫn đến project hiện tại
    static String projectPath = System.getProperty("user.dir") + "/";
    //Tạo format ngày giờ để xíu gắn dô cái name của screenshot hoặc record video
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH-mm-ss");

    public void takeScreenshot(WebDriver driver,String screenName) throws InterruptedException {

        logTest.info("Driver for Screenshot: " + driver);

            try {
                // Tạo tham chiếu của TakesScreenshot
                TakesScreenshot ts = (TakesScreenshot) driver;
                // Gọi hàm capture screenshot - getScreenshotAs
                File source = ts.getScreenshotAs(OutputType.FILE);
                //Kiểm tra folder tồn tại. Nêu không thì tạo mới folder
                String screenshotPath = PropertiesFile.getPropValue("screenshotPath");
                File theDir = new File(projectPath + screenshotPath);
                if (!theDir.exists()) {
                    theDir.mkdirs();
                }

                String fileName = screenName + "_" + dateFormat.format(new Date()) + ".png";
                File targetFile = new File(theDir.getAbsolutePath() + File.separator + fileName);
                FileHandler.copy(source,targetFile);

                logTest.info("Screenshot taken: " + screenName);
                logTest.info("Screenshot saved at: " + targetFile.getAbsolutePath());

            } catch (Exception e) {
                logTest.error("[FAIL] Exception while taking screenshot " + e.getMessage());
            }
        }
    }




