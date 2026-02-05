package com.bases;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Properties;

import com.log.logTest;
import com.utility.PropertiesFile;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.ThreadContext;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.testng.annotations.*;

public class multipleThread_baseSetup {

        // Set ThreadLocal webdriver list to run parallel
        private static final ThreadLocal<WebDriver> driver_parallel = new ThreadLocal<>();
        protected JavascriptExecutor js;

        static String driverPath = PropertiesFile.getPropValue("driverPath");
        public static WebDriver getDriver() {
            return driver_parallel.get();
        }

        public String getBrowserName() {
            return ((org.openqa.selenium.remote.RemoteWebDriver) getDriver()).getCapabilities().getBrowserName();
        }

        //Hàm này để tùy chọn Browser. Cho chạy trước khi gọi class này (BeforeClass)
        private void setDriver(String browserType, String appURL) {
            WebDriver driver;
            switch (browserType) {
                case "chrome":
                    driver = initChromeDriver(appURL);
                    break;
                case "firefox":
                    driver = initFirefoxDriver(appURL);
                    break;
                case "msedge":
                    driver = initEdgeDriver(appURL);
                    break;
                default:
                    logTest.info("Browser: " + browserType + " is invalid, Launching Chrome as browser of choice...");
                    driver = initChromeDriver(appURL);
            }
            // Load the driver thread for parallel execution
            driver_parallel.set(driver);
        }

        private void setupBrowser(WebDriver driver, String appURL) {
            driver.manage().window().maximize();
            driver.navigate().to(appURL);
            js = (JavascriptExecutor) driver ;
            js.executeScript("document.body.style.zoom='70%'");
            driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
            driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(30));
        }

        //Khởi tạo cấu hình của các Browser để đưa vào Switch Case
        private  WebDriver initChromeDriver(String appURL) {
            logTest.info("Launching Chrome browser...");
            //Using offline chrome driver
            System.setProperty("webdriver.chrome.driver", driverPath + "chromedriver.exe");
            //WebDriverManager.chromedriver().setup();

            ChromeOptions options = new ChromeOptions();

            options.addArguments("--disable-gpu"); // accelerate hardware
            options.addArguments("--disable-notifications"); // disable notifications

            /* option run
            options.addArguments("--headless=new"); // run in headless mode
            options.setAcceptInsecureCerts(true); // accept insecure certs: ssl,...

            File file = new.File(".crx filepath");
            options.addExtensions(file); // add extension
             */

            WebDriver driver = new ChromeDriver(options);
            setupBrowser(driver, appURL);
            return driver;
        }

        private  WebDriver initFirefoxDriver(String appURL) {
            logTest.info("Launching Firefox browser...");
            //Using offline firefox driver
            System.setProperty("webdriver.gecko.driver", driverPath + "geckodriver.exe");
            //WebDriverManager.firefoxdriver().setup();

            FirefoxOptions options = new FirefoxOptions();

            // disable notifycation
            options.addPreference("dom.webnotifications.enabled", false);
            options.addPreference("dom.push.enabled", false);
            // disable hardware acceleration
            options.addPreference("layers.acceleration.disabled", true);

            options.setBinary("C:\\Program Files\\Mozilla Firefox\\firefox.exe");

            WebDriver driver = new FirefoxDriver(options);
            setupBrowser(driver, appURL);
            return driver;

        }

    private WebDriver initEdgeDriver(String appURL) {
        logTest.info("Launching Edge browser...");

        // 1. Set property for Edge driver
        System.setProperty("webdriver.edge.driver", driverPath + "msedgedriver.exe");

        // WebDriverManager.edgedriver().setup(); // run online driver.

        EdgeOptions options = new EdgeOptions();

        // 2. Turn off Pop-up and Notification
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-geolocation");

        // 3. Khởi tạo ĐÚNG class EdgeDriver
        WebDriver driver = new EdgeDriver(options);

        setupBrowser(driver, appURL);
        return driver;
    }


        // Chạy hàm initializeTestBaseSetup trước hết khi class này được gọi
        @Parameters({ "browserType", "appURL" })
        @BeforeClass(alwaysRun = true)
        public void initializeTestBaseSetup(@Optional("") String browserType,@Optional("") String appURL) {
            try {

                // Kiểm tra browserType: Nếu XML rỗng thì lấy từ file Properties
                String finalBrowser = (browserType != null && !browserType.isEmpty())
                        ? browserType
                        : PropertiesFile.getPropValue("browser");

                // Kiểm tra appURL: Nếu XML rỗng thì lấy từ file Properties
                String finalURL = (appURL != null && !appURL.isEmpty())
                        ? appURL
                        : PropertiesFile.getPropValue("url");// i t

                // Name browers to log.
                ThreadContext.put("browser", browserType.toUpperCase());

                setDriver(finalBrowser, finalURL);

            } catch (Exception e) {
                logTest.error("[FAIL] ..." + e.getMessage());
            }
        }

        @AfterClass(alwaysRun = true)
        public void tearDown() throws Exception {
            if (getDriver() != null) {
                Thread.sleep(2000);
                // Close driver
                getDriver().quit();
                // Close thread local all driver.
                driver_parallel.remove();
                logTest.info("Driver closed. Test completed.");
            }
        }

}