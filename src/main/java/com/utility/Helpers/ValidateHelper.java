package com.utility.Helpers;

import com.log.logTest;
import com.utility.PropertiesFile;
import io.qameta.allure.Step;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

public class ValidateHelper {

    private WebDriver driver;
    private Actions action;
    private WebDriverWait wait;
    private JavascriptExecutor js;

    public ValidateHelper(WebDriver driver){
        this.driver = driver;


            try{//Get Config File Timeout and interval for explicit wait
            long timeout = Long.parseLong(PropertiesFile.getPropValue("timeout"));
            long interval = Long.parseLong(PropertiesFile.getPropValue("refreshInterval"));

            this.wait = new WebDriverWait(driver,
                    Duration.ofSeconds(timeout),
                    Duration.ofMillis(interval));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        this.js = (JavascriptExecutor) driver ;
        this.action = new Actions(driver);
    }

    @Step("Wait for element invinsible")
    public void waitForElementInvisible(By locator) {
        try{
            wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Step("Verify isPaginationButtonDisabled is enabled / disabled")
    public boolean isPaginationButtonDisabled(WebElement element) {
        // Lấy giá trị của thuộc tính class
        String classValue = element.getAttribute("class");
        // Kiểm tra xem trong class có chứa từ khóa khóa nút hay không
        return classValue.contains("cursor-not-allowed") || classValue.contains("disabled");
    }

    @Step("Set slider '{0}' to X axis: '{1}' and Y axis: '{2}'")
    public void setSlider(By locator, int x, int y){
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            action.dragAndDropBy(driver.findElement(locator), x, y).build().perform();
        } catch (Exception e) {
            logTest.warn("[WARN] Can't not set slider "+ locator);
            Assert.fail("Could not interact with the slider.");
        }
    }

    @Step("Get shadow element with shadow host locator: '{0}' and inner css selector: '{1}'")
    public WebElement getShadowElement(By ShadowHostLocator, String innerCssSelector) {
        try {
            // 1. Find Shadow Host (tag include shadow-root)
            WebElement shadowHost = driver.findElement(ShadowHostLocator);
            // 2. Use JavaScript to through Shadow Root and find inner CSS selector
            JavascriptExecutor js = (JavascriptExecutor) driver;
            WebElement shadowElement = (WebElement) js.executeScript(
                    "return arguments[0].shadowRoot.querySelector(arguments[1])",
                    shadowHost, innerCssSelector);

            logTest.info("Found shadow element with CSS: " + innerCssSelector);
            return shadowElement;
        } catch (Exception e) {
            logTest.warn("[WARN] Cannot find shadow element: " + innerCssSelector);
            return null;
        }
    }

    @Step("Verify value of placeholder")
    public boolean getTextPlaceholder(By locator, String expect){
        try {

            wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            return Objects.requireNonNull(driver.findElement(locator).getAttribute("placeholder")).trim().contains((expect));
        } catch (Exception e) {
            logTest.error("[FAIL] Can't not get value of placeholder "+ locator);
            return false;
        }
    }

    @Step("Set text '{1}' into element '{0}'")
    public void setTextByActions(By locator, String text) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        WebElement element = driver.findElement(locator);
        action.moveToElement(element)
                .click()
                .keyDown(Keys.CONTROL).sendKeys("a").keyUp(Keys.CONTROL)
                .sendKeys(Keys.BACK_SPACE)
                .sendKeys(text)
                .perform();
    }


    @Step("Set text '{1}' into locator '{0}'")
    public void setText(By locator, String text){

        try {

        wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        ScrollToElement_js(locator);
        driver.findElement(locator).click();
        driver.findElement(locator).clear();
        driver.findElement(locator).sendKeys(text);

        } catch (Exception e) {
            logTest.warn("[WARN] Selenium setText fail, try setText by JavaScript: " + locator.toString());
            try {
                WebElement element = driver.findElement(locator);

                String script = "arguments[0].value='" + text + "';" +
                        "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));" +
                        "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));";
                js.executeScript(script, element);
                logTest.info("[WARN] Set text '" + text + "' sucessful by JavaScript.");
            } catch (Exception jsException) {
                logTest.error("[FAIL] Both Selenium and JS fail: " + locator.toString());
            }
        }
    }

    @Step("Click nornal on locator '{0}'")
    public void clickElement(By locator){
        try {

            WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
            js.executeScript("arguments[0].scrollIntoView(true);", element);
            element.click();

        } catch (Exception e) {
            logTest.warn("[WARN] Fail to click with locator, try js click : " + locator.toString());
            clickElement_js(locator);
        }
    }

    @Step("Click js on locator '{0}'")
    public void clickElement_js(By locator){
        try {

            WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
            js.executeScript("arguments[0].scrollIntoView(true);", element);
            js.executeScript("arguments[0].click();", element);

        } catch (Exception e) {
            logTest.warn("[WARN] Fail to click JS with locator: " + locator.toString());
        }
    }



    @Step("Click nornal on element '{0}'")
    public void clickElement(WebElement element){
        try {

            wait.until(ExpectedConditions.elementToBeClickable(element));
            js.executeScript("arguments[0].scrollIntoView(true);", element);
            element.click();

        } catch (Exception e) {
            logTest.warn("[WARN] Fail to click with element, try js click : " + element.toString());
            clickElement_js(element);
        }
    }

    @Step("Click js on element '{0}'")
    public void clickElement_js(WebElement element){
        try {

            wait.until(ExpectedConditions.elementToBeClickable(element));
            js.executeScript("arguments[0].scrollIntoView(true);", element);
            js.executeScript("arguments[0].click();", element);

        } catch (Exception e) {
            logTest.warn("[WARN] Fail to click JS with element: " + element.toString());
        }
    }

    @Step("Scroll to element '{0}' by js")
    public void ScrollToElement_js(WebElement element){
        try {

            wait.until(ExpectedConditions.visibilityOf(element));
            js.executeScript("arguments[0].scrollIntoView(true);", element);

        } catch (Exception e) {
            logTest.warn("[WARN] Can't scroll js to element with locator: " + element.toString());
        }
    }

    @Step("Scroll to locator '{0}' by js")
    public void ScrollToElement_js(By locator){
        try {

            WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
            js.executeScript("arguments[0].scrollIntoView(true);", element);

        } catch (Exception e) {
            logTest.warn("[WARN] Can't scroll js to element with locator: " + locator.toString());
        }
    }

    @Step("Scroll to top of the page")
    public void scrollToTopPage_js() {
        try {
            js.executeScript("window.scrollTo(0, 0);");
            logTest.info("Scrolled to top of the page");
        } catch (Exception e) {
            logTest.error("[ERROR] Failed to scroll to top: " + e.getMessage());
        }
    }

    @Step("Scroll to bottom of the page")
    public void scrollToBottomPage_js() {
        try {
            js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
            logTest.info("Scrolled to bottom of the page");
        } catch (Exception e) {
            logTest.error("[ERROR] Failed to scroll to bottom: " + e.getMessage());
        }
    }

    @Step("Scroll to middle of the page")
    public void scrollToMiddlePage_js() {
        try {
            js.executeScript("window.scrollTo(0, document.body.scrollHeight / 2);");
            logTest.info("Scrolled to middle of the page");
        } catch (Exception e) {
            logTest.error("[ERROR] Failed to scroll to middle: " + e.getMessage());
        }
    }

    @Step("Get text of locator '{0}'")
    public String getTextElement(By locator) {
        try {
            String strErrorMsg = null;
            wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            strErrorMsg = driver.findElement(locator).getText().trim();
            return strErrorMsg;
        } catch (Exception e) {
            logTest.warn("[WARN] Fail to getText with locator: " + locator.toString());
        }
        return "";
    }

    @Step("Verify url match with: '{0}'")
    public boolean verifyUrl(String expectedUrl) {
        try {
            wait.until(ExpectedConditions.urlContains(expectedUrl));
            return Objects.requireNonNull(driver.getCurrentUrl()).contains(expectedUrl);
        } catch (Exception e) {
            logTest.error("[FAIL] Url is not match with: " + expectedUrl );
        }
        return false;
    }

    @Step("Verify element '{0}' is enabled")
    public boolean verifyElementEnabled(By locator) {
        try {

            wait.until(ExpectedConditions.elementToBeClickable(locator));
            boolean isEnabled = driver.findElement(locator).isEnabled();
            if (isEnabled) {
                logTest.info("[PASS] Element " + locator.toString() + " is enabled.");
            }
            return isEnabled;
        } catch (Exception e) {
            logTest.error("[FAIL] Element " + locator.toString() + " is NOT enabled or not clickable after timeout.");
            return false;
        }
    }

    @Step("Verify element '{0}' is existed")
    public boolean verifyElementIsExist(By locator) {
        try {
            List<WebElement> elements = driver.findElements(locator);
            int total = elements.size();
            return total > 0;

        } catch (Exception e) {
            logTest.error("[FAIL] to verifyElementIsExist with locator: " + locator.toString());
        }
        return false;
    }

    @Step("Verify element '{0}' is display")
    public boolean verifyElementIsDisplay(By locator) {
        try{
            wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            logTest.info("Element with locator: " + locator.toString() + " is displayed.");
            return true;
        } catch (Exception e) {
            logTest.error("[FAIL] Element is NOT displayed: " + locator.toString());
            return false;
        }
    }

    @Step("Select dropdown by locator '{0}' and index '{1}'")
    public void SelectDropdownByIndex(By locator, int value) {
        try{
            wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            Select select = new Select(driver.findElement(locator));
            select.selectByIndex(value);
        }catch (Exception e){
            logTest.warn("[WARN] Can't select dropdown to locator: " + locator.toString());
        }
    }

    @Step("Select dropdown by locator '{0}' and value '{1}'")
    public void SelectDropdownByValue(By locator, String value) {
        try{
            wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            Select select = new Select(driver.findElement(locator));
            select.selectByValue(value);
        }catch (Exception e){
            logTest.warn("[WARN] select dropdown to locator:: " + locator.toString());
        }
    }

    @Step("Select dropdown by locator '{0}' and text '{1}'")
    public void SelectDropdownByVisibleText(By locator, String value) {
        try{
            wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            Select select = new Select(driver.findElement(locator));
            select.selectByVisibleText(value);
        }catch (Exception e){
            logTest.warn("[WARN] select dropdown to locator: " + locator.toString());
        }
    }

    @Step("Action - Enter")
    public void action_Enter() {
            action.sendKeys(Keys.ENTER).build().perform();
    }

    @Step("Action - Refresh")
    public void action_Refresh() {
        action.sendKeys(Keys.CONTROL).sendKeys(Keys.F5).build().perform();
    }

    @Step("Action - Double Click to '{0}'")
    public void action_DoubleClick(By locator) {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(locator));
            action.doubleClick(driver.findElement(locator)).build().perform();
        } catch (Exception e) {
            logTest.warn("[WARN] doubleclick with locator: " + locator.toString());
        }
    }

    @Step("Action - Right Click to '{0}'")
    public void action_ContextClick(By locator) {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(locator));
            action.contextClick(driver.findElement(locator)).build().perform();
        } catch (Exception e) {
            logTest.warn("[WARN] contextclick with locator: " + locator.toString());
        }
    }

    @Step("Action - Move to locator '{0}'")
    public void action_MovetoElement(By locator) {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(locator));
            action.moveToElement(driver.findElement(locator)).build().perform();
        } catch (Exception e) {
            logTest.warn("[WARN] MoveToElement with locator: " + locator.toString());
        }
    }

    @Step("Action - Drap from locator '{0}' to locator '{1}'")
    public void action_DrapAndDrop(By locator, By locator1) {
        try {

            wait.until(ExpectedConditions.elementToBeClickable(locator));
            wait.until(ExpectedConditions.elementToBeClickable(locator1));
            action.dragAndDrop(driver.findElement(locator), driver.findElement(locator1)).build().perform();
        } catch (Exception e) {
            logTest.warn("[WARN] Can't DrapAndDrop with locator or locator2: " + locator.toString() +" / "+ locator1.toString());
        }
    }

    @Step("Action - Click and Hold '{0}'")
    public void action_ClickAndHold(By locator) {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(locator));
            action.clickAndHold(driver.findElement(locator)).build().perform();
        } catch (Exception e) {
            logTest.warn("[WARN] Can't ClickAndHold with locator: " + locator.toString());
        }
    }

    @Step("Action - KeyDown '{1}' at locator '{0}'")
    public void action_KeyDown(By locator, Keys key) {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(locator));
            action.keyDown(driver.findElement(locator), key).build().perform();
        } catch (Exception e) {
            logTest.warn("[WARN] Can't KeyDown with locator: " + locator.toString());
        }
    }

    @Step("Action - KeyUp '{1}' at locator '{0}'")
    public void action_KeyUp(By locator, Keys key) {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(locator));
            action.keyUp(driver.findElement(locator), key).build().perform();
        } catch (Exception e) {
            logTest.warn("[WARN] Can't KeyUp with locator: " + locator.toString());
        }
    }

    @Step("Action - Crtr+  '{1}'")
    public void action_Ctrt(String key) {
        try {
            action.keyDown(Keys.CONTROL).sendKeys(key).keyUp(Keys.CONTROL).build().perform();
        } catch (Exception e) {
            logTest.warn("[WARN] Ctrt+ with key: " + key);
        }
    }

    @Step("Switch to frame by locator '{0}'")
    public void switchToFrame(By locator) {
        try {
            // Đợi cho iframe sẵn sàng rồi mới nhảy vào
            wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(locator));
            logTest.info("Iframe switch is success: " + locator);
        } catch (Exception e) {
            logTest.warn("[WARN] Iframe can't select Iframe: " + locator);
        }
    }

    @Step("Switch to default content frame")
    public void switchToDefaultContent() {
        try {
            driver.switchTo().defaultContent();
            logTest.info("Switch to default content is success");
        } catch (Exception e) {
            logTest.warn("[WARN] Can't switch to default Iframe: " + e.getMessage());
        }
    }

    @Step("Delay in '{0}' miliseconds")
    public void Delay(int time) throws InterruptedException {Thread.sleep(time);           }



   /* @Step("Wait for page loaded ()")
    public void waitForPageLoaded() {
        ExpectedCondition<Boolean> jsload = new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                return ((JavascriptExecutor) driver).executeScript("return document.readyState").toString()
                        .equals("complete");
            }
        };
        ExpectedCondition<Boolean> jquery = new ExpectedCondition<Boolean>() {
                @Override
                public Boolean apply(WebDriver driver) {
                    try{
                        return ((long)((JavascriptExecutor) driver).executeScript("return jQuery.active")==0);
                    } catch (Exception e){
                        return true;
                    }
            }
        };

        try {
            this.wait.until(jsload);
            // wait jquery disable by default
            //wait.until(jquery);
        } catch (Throwable error) {
            logTest.error("Page Load Request Timeout");
            logTest.warn("Timeout waiting for Page Load Request to complete.");
        }
    }*/
}