package pages;

import com.log.logTest;
import com.utility.CustomSoftAssert;
import com.utility.Helpers.ValidateHelper;
import io.qameta.allure.Step;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.swing.*;
import java.time.Duration;
import java.util.List;

public class SearchPage {

    private WebDriver driver;
    private ValidateHelper validateHelper;
    private JavascriptExecutor js;
    private CustomSoftAssert softAssert;
    private WebDriverWait wait;
    private Actions action;

    public SearchPage(WebDriver driver) {
        this.driver = driver;
        this.validateHelper = new ValidateHelper(driver);
        this.js = (JavascriptExecutor) driver;
        this.softAssert = new CustomSoftAssert(driver);
        this.wait = new WebDriverWait(driver,
                Duration.ofSeconds(10),
                Duration.ofMillis(500));
        this.action = new Actions(driver);
    }

    // SearchTest textbox
    private By searchBar = By.id("search");
    // SearchTest button
    private By searchButton = By.xpath("//button[@title='Search']");
    // List items in page
    private By listItems = By.xpath("//div[@class='grid grid-cols-4 gap-2.5 px-2.5 mt-5']//div/a");
    // Text not found item in search
    private By testNotFoundItem = By.xpath("//p[contains(text(),'không tìm thấy sản phẩm từ')]");
    // Items not existed
    private By itemsNotExisted = By.xpath("//span[@class='text-orange font-bold']");



    // --- Filer ---
    // Start price
    private By startPrice = By.xpath("//input[@name='priceFrom']");
    // End price
    private By endPrice = By.xpath("//input[@name='priceTo']");
    // Apply price
    private By applyPrice = By.xpath("//button[contains(text(),'Áp dụng')]");
    // --- List price of item
    private By listPrice = By.xpath("//div[@class='grid grid-cols-4 gap-2.5 px-2.5 mt-5']//a//span[@class='text-orange font-bold text-sm']");

    // --- Search on product detailed page ---
    private By textboxProductDetail = By.xpath("//input[@placeholder='Tìm sản phẩm, thương hiệu bạn mong muốn...']");
    private By searchButtonProduct = By.xpath("//button[@aria-label='Search Button']");


    @Step("SearchTest product with keyword: '{0}'")
    public void searchProduct(String product) {
        validateHelper.setText(searchBar,product);
        validateHelper.clickElement(searchButton);
    }

    @Step("SearchTest product with keyword: '{0}'")
    public void searchOnProductDetailedPage(String product) {
        validateHelper.setText(textboxProductDetail,product);
        validateHelper.clickElement(searchButtonProduct);
    }

    @Step("Search and click first product with keyword: '{0}' (Open Product Detail Page)")
    public ProductDetailPage searchAndReturnFirstProduct(String product) throws InterruptedException {
        try {
            validateHelper.setText(searchBar, product);
            validateHelper.clickElement(searchButton);
            wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(listItems));
            WebElement firstItem = driver.findElements(listItems).getFirst();
            if (!driver.findElements(listItems).isEmpty()) {
                validateHelper.clickElement(firstItem);
                return new ProductDetailPage(driver);
            } else {
                logTest.info("[FAIL] No product found");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Step("Go to next page")
    public Boolean goToNextPage() throws InterruptedException {
        // Wait pagination panel display
        validateHelper.verifyElementIsDisplay(By.xpath("//div[@class='flex justify-center mt-5']"));

        // Check if pagination panel display
        List<WebElement> pagination = driver.findElements(By.xpath("//ul[@class='flex list-none p-2.5 gap-2.5']"));
        if (pagination.size() == 0) {
            logTest.info("Only one page. Continues test");
            return false;
        }

        // Get element of next page button
        WebElement nextPage = driver.findElement(By.xpath("//ul[@class='flex list-none p-2.5 gap-2.5']//li[last()]"));

        // Condition to stop loop
        if (validateHelper.isPaginationButtonDisabled(nextPage)) {
            return false; // End of pagination, stop.
        }

        validateHelper.clickElement(nextPage);
        logTest.info("Click Next.");

        // Wait for pagination panel display
        softAssert.assertTrue(validateHelper.verifyElementIsDisplay(By.xpath("//div[@class='flex justify-center mt-5']")),"Pagination panel not display");
        softAssert.assertAll();
        // delay 0.5s for stable dom.
        validateHelper.Delay(500);
        return true; // Go to next page if not last page.
    }

    // Check keyword is matching with product name
    @Step("Count product match with keyword: '{0}'")
    public int countMatchingProducts(String keyword) {
        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(listItems));
        List<WebElement> items = driver.findElements(listItems);
        int count = 0;
        for (WebElement item : items) {
            if (item.getText().toLowerCase().contains(keyword.toLowerCase())) {
                count++;
            }
        }
        return count;
    }

    // Get total products on page
    @Step("Get total products on page")
    public int getTotalProductsOnPage() {
        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(listItems));
        return driver.findElements(listItems).size();
    }

    @Step("Set price range of products from '{0}' to '{1}'")
    public void filterByPriceRange(String min, String max) throws InterruptedException {

        // Wait for price display
        validateHelper.verifyElementIsDisplay(applyPrice);
        // Scroll to price filter
        validateHelper.ScrollToElement_js(applyPrice);
        // Get 1 items price after filter
        WebElement oldPriceElement = driver.findElement(listPrice);

        // Get element by locator
        WebElement minInput = driver.findElement(startPrice);
        WebElement maxInput = driver.findElement(endPrice);
        WebElement btnApply = driver.findElement(applyPrice);

        // 1. Set Min price and perform Tab to jump to Max price.
        action.moveToElement(minInput)
                .click()
                .keyDown(Keys.CONTROL).sendKeys("a").keyUp(Keys.CONTROL) // Ctrl+A để chọn hết
                .sendKeys(Keys.BACK_SPACE) // Xóa sạch
                .sendKeys(min)
                .sendKeys(Keys.TAB) // Nhấn Tab để Framework nhận diện sự kiện Blur/Change
                .perform();

        // 2. Set Max price and perform Enter to apply filter.
        action.moveToElement(maxInput)
                .click()
                .keyDown(Keys.CONTROL).sendKeys("a").keyUp(Keys.CONTROL)
                .sendKeys(Keys.BACK_SPACE)
                .sendKeys(max)
                .sendKeys(Keys.ENTER) // Giả lập phím Enter để kích hoạt bộ lọc
                .perform();

        // 3. Click apply button
        validateHelper.clickElement(applyPrice);

        // Wait for price stale -> web refresh data
        try {
            wait.until(ExpectedConditions.stalenessOf(oldPriceElement));
            logTest.info("List old price is refresh, wait for new apply price...");
        } catch (Exception e) {
            logTest.error("[FAIL] List old price is not refresh, wait for new apply price...");
            validateHelper.Delay(1000);
        }
    }

    @Step("Verify price range list of search products from '{0}' to '{1}'")
    public boolean verifyPriceRange(String min, String max) {
        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(listPrice));
        List<WebElement> priceElements = driver.findElements(listPrice);

        // 1. Convert min/max từ String to int
        int minVal = (int) Double.parseDouble(min);
        int maxVal = (int) Double.parseDouble(max);

        for (WebElement e : priceElements) {
            String rawPrice = e.getText().replaceAll("[^0-9]", "");
            if (!rawPrice.isEmpty()) {
                // Convert "357.000 ₫" to int 357000
                int currentPrice = Integer.parseInt(rawPrice);
                if (currentPrice < minVal || currentPrice > maxVal) {
                    logTest.error("[FAIL] item price  " + currentPrice + " out of range " + min + "-" + max);
                    return false;
                }
            }
        }
        logTest.info("[PASS] all items price are between " + min + "-" + max);
        return true;
    }

    @Step("Negative test: Page will message 'Rất tiếc, không tìm thấy sản phẩm...' when search noneless keyword")
    public boolean verifyNoProductFound() {
        try {

            String errorText = validateHelper.getTextElement(testNotFoundItem);
            String errorKeyword = validateHelper.getTextElement(itemsNotExisted);

            Boolean Message = validateHelper.verifyElementIsDisplay(testNotFoundItem);
            Boolean itemFound = validateHelper.verifyElementIsDisplay(itemsNotExisted);

            logTest.info("[PASS] Message: "+ errorText +" is display after search noneless keyword");
            return Message && itemFound;

        } catch (Exception e) {
            logTest.error("[FAIL] Message 'Rất tiếc, không tìm thấy sản phẩm...' is not display after search noneless keyword");
            return false;
        }
    }

}
