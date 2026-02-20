package com.bases;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

import com.log.logTest;
import com.utility.PropertiesFile;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.ThreadContext;
import org.openqa.selenium.Dimension;
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

        // Set flag config in properties file
        private final boolean isHeadless = Boolean.parseBoolean(PropertiesFile.getPropValue("browser.headless"));
        private final boolean isIncognito = Boolean.parseBoolean(PropertiesFile.getPropValue("browser.incognito"));
        private final boolean isMaximize = Boolean.parseBoolean(PropertiesFile.getPropValue("browser.maximize"));
        private final String windowSize = PropertiesFile.getPropValue("browser.window.size"); // e.g., 1920x1080

        static String driverPath = PropertiesFile.getPropValue("driverPath");
        public static WebDriver getDriver() {
            return driver_parallel.get();
        }

        public String getBrowserName() {
            return ((org.openqa.selenium.remote.RemoteWebDriver) getDriver()).getCapabilities().getBrowserName();
        }

        //Optional Browser. run in @BeforeClass
        private void setDriver(String browserType, String appURL) {
            WebDriver driver;
            switch (browserType) {
                case "chrome":
                    driver = initChromeDriver(appURL);
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
            if (isMaximize) driver.manage().window().maximize();
            driver.navigate().to(appURL);
            js = (JavascriptExecutor) driver ;
            driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
            driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(30));
        }

        //Config Browser input to Switch Case
        private  WebDriver initChromeDriver(String appURL) {
            logTest.info("Launching Chrome browser...");
            //Using offline chrome driver
            System.setProperty("webdriver.chrome.driver", driverPath + "chromedriver.exe");
            //WebDriverManager.chromedriver().setup();

            ChromeOptions options = new ChromeOptions();

            // Get config from Properties
            if (isHeadless) {
                options.addArguments("--headless=new");
                options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.36");
            }
            if (isIncognito) options.addArguments("--incognito");
            if (windowSize != null) options.addArguments("--window-size=" + windowSize);

            options.addArguments("--disable-gpu"); // Disable GPU hardware acceleration
            options.addArguments("--no-sandbox"); // Bypass OS security (CI/CD environments)
            options.addArguments("--disable-dev-shm-usage"); // Overcome limited resource problems
            options.addArguments("--disable-notifications"); // disable notifications

            /* option run
            options.setAcceptInsecureCerts(true); // accept insecure certs: ssl,...

            File file = new.File(".crx filepath");
            options.addExtensions(file); // add extension
             */

            WebDriver driver = new ChromeDriver(options);
            setupBrowser(driver, appURL);
            return driver;
        }

    private WebDriver initEdgeDriver(String appURL) {
        logTest.info("Launching Edge browser...");

        // 1. Set property for Edge driver
        System.setProperty("webdriver.edge.driver", driverPath + "msedgedriver.exe");

        // WebDriverManager.edgedriver().setup(); // run online driver.

        EdgeOptions options = new EdgeOptions();

        // 2. Get config from Properties
        if (isHeadless) {
            options.addArguments("--headless=new");
            options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/133.0.0.0 Safari/537.36 Edg/133.0.0.0");
        }
        if (isIncognito) options.addArguments("-inprivate"); // Edge use -inprivate
        if (windowSize != null) options.addArguments("--window-size=" + windowSize);

        // 3. Turn off Pop-up and Notification
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-geolocation");
        options.addArguments("--disable-gpu");

        // 4. Init Edge driver
        WebDriver driver = new EdgeDriver(options);
        setupBrowser(driver, appURL);
        return driver;
    }


        // Primary initializeTestBaseSetup before class test call.
        @Parameters({ "browserType", "appURL" })
        @BeforeClass(alwaysRun = true)
        public void initializeTestBaseSetup(@Optional("") String browserType,@Optional("") String appURL) {
            try {

                //Check browserType: If XML empty then get data from Properties file.
                String finalBrowser = (browserType != null && !browserType.isEmpty())
                        ? browserType
                        : PropertiesFile.getPropValue("browser");

                //Check appUrl: If XML empty then get data from Properties file.
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