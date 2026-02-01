package pages;

import com.log.logTest;
import com.utility.CustomSoftAssert;
import com.utility.Helpers.ValidateHelper;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;

public class CartPage {
    private WebDriver driver;
    private ValidateHelper validateHelper;
    private JavascriptExecutor js;
    private WebDriverWait wait;

    public CartPage(WebDriver driver) {
        this.driver = driver;
        validateHelper = new ValidateHelper(driver);
        this.js = (JavascriptExecutor) driver ;
        try{
            this.wait = new WebDriverWait(driver,
                    Duration.ofSeconds(10),
                    Duration.ofMillis(500));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // --- PAGE ELEMENT ---
    private By cartTitle = By.xpath("//div[contains(text(),'Giỏ hàng')]");
    private By allItems = By.xpath("//tbody//tr");
    private By cartEmptyText = By.xpath("//p[contains(text(),'Bạn chưa chọn sản phẩm')]");

    // --- Dynamic xpath ---
    String itemBrand = "//tbody//tr[%d]//a[@aria-label='Go brand page']";
    String itemName = "//tbody//tr[%d]//a[@aria-label='Go brand page']/parent::div/following-sibling::a";
    String wishListBtn = "//tbody//tr[%d]//button[@aria-label='Wishlist Button']";
    String itemDeleteBtn = "//tbody/tr[%d]//button[contains(text(),'Xóa')]";
    String itemUnitPrice ="//tbody/tr[%d]//td[2]//div[@class='font-bold']";
    String itemQuantity = "//tbody/tr[%d]//td[3]//input";
    String increaseQuantityBtn ="//tbody/tr[%d]//button[@aria-label='Increase btn']";
    String decreaseQuantityBtn ="//tbody/tr[%d]//button[@aria-label='Descrease btn']";
    String itemTotalPrice ="//tbody/tr[%d]//td[4]//div[@class='font-bold']";

    // ---- Action Dynamic ----
    @Step("Delete items on cart page by index")
    public void deleteItemsInCartByIndex(int index) {
        By itemDeleteBtnLocator = By.xpath(String.format(itemDeleteBtn, index));
        validateHelper.clickElement(itemDeleteBtnLocator);
    }

    @Step("Get brand items on cart page by index")
    public String getBrandItemsInCartByIndex(int index) {
        By itemBrandLocator = By.xpath(String.format(itemBrand, index));
        return validateHelper.getTextElement(itemBrandLocator);
    }

    @Step("Get name items on cart page by index")
    public String getNameItemsInCartByIndex(int index) {
        By itemNameLocator = By.xpath(String.format(itemName, index));
        return validateHelper.getTextElement(itemNameLocator);
    }

    @Step("Get quantity items on cart page by index")
    public long getQuantityItemsInCartByIndex(int index) {
        By itemQuantityLocator = By.xpath(String.format(itemQuantity, index));
        return validateHelper.parseCurrencyToLong(validateHelper.getElementAttribute(itemQuantityLocator, "value"));
    }

    @Step("Get unit price of items on cart page by index")
    public long getUnitPriceItemsInCartByIndex(int index) {
        By itemUnitPriceLocator = By.xpath(String.format(itemUnitPrice, index));
        return validateHelper.parseCurrencyToLong(validateHelper.getTextElement(itemUnitPriceLocator));
    }

    @Step("Get total price of items on cart page by index")
    public long getTotalPriceItemsInCartByIndex(int index) {
        By itemTotalPriceLocator = By.xpath(String.format(itemTotalPrice, index));
        return validateHelper.parseCurrencyToLong(validateHelper.getTextElement(itemTotalPriceLocator));
    }

    @Step("Increase items on cart page by index")
    public void increaseItemsInCartByIndex(int index) {
        By itemIncreaseLocator = By.xpath(String.format(increaseQuantityBtn, index));
        validateHelper.clickElement(itemIncreaseLocator);
    }

    @Step("Decrease items on cart page by index")
    public void DecreaseItemsInCartByIndex(int index) {
        By itemDecreaseLocator = By.xpath(String.format(decreaseQuantityBtn, index));
        validateHelper.clickElement(itemDecreaseLocator);
    }

    @Step("Verify total price items on cart page by index")
    public boolean verifyTotalPriceItemsInCartByIndex(int index) {
        long expectedTotalPrice = getQuantityItemsInCartByIndex(index) * getUnitPriceItemsInCartByIndex(index);
        long actualTotalPrice = getTotalPriceItemsInCartByIndex(index);
        return expectedTotalPrice == actualTotalPrice;
    }


    // ---------- VERIFY PAGE --------------
    @Step("Verify product page url contains: /checkout/cart ")
    public boolean verify_ProductPage_Url() {
        try {
            boolean urlContains = validateHelper.verifyUrl("hasaki.vn/checkout/cart");
            if (urlContains){
                logTest.info("[PASS] Url: hasaki.vn/checkout/cart is display");
                return validateHelper.verifyUrl("hasaki.vn/checkout/cart");
            }
        } catch (Exception e) {
            logTest.error("[FAIL] actual Url is: " + driver.getCurrentUrl());
            return false;
        }
        return false;
    }

    @Step("Verify cart page title is display")
    public boolean verify_cartTitle() {
        try {
            boolean isTitleDisplay = validateHelper.verifyElementIsDisplay(cartTitle);
            if (isTitleDisplay){
                logTest.info("[PASS] Title 'Giỏ hàng' is display");
                return isTitleDisplay;
            }
        } catch (Exception e) {
            logTest.error("[FAIL] Title 'Giỏ hàng' is not display");
            return false;
        }
        return false;
    }

    // ---- ACTION LOOP ALL ITEMS IN CART ----
    @Step("Delele all item in cart")
    public void deleteAllItemInCart() {

        if (validateHelper.verifyElementIsExist(allItems)){
            List<WebElement> listItems = driver.findElements(allItems);
            for (int i = listItems.size(); i > 0; i--) {

                // By pass if item is a gift
                By isUnitPriceExist = By.xpath(String.format(itemUnitPrice, i));
                if (!validateHelper.verifyElementIsExist(isUnitPriceExist)) {
                    logTest.info("Row " + i + " is a Gift item. Skipping...");
                    continue;
                }

                deleteItemsInCartByIndex(i);
                logTest.info("[PASS] Deleted item at position: " + i);
            }
        } else if (validateHelper.verifyElementIsDisplay(cartEmptyText)) {
            logTest.info("Cart page empty");
        }
    }

    @Step("Verify all item calculation price in cart")
    public void verifyCalculationPriceAllItemInCart() {
        if (validateHelper.verifyElementIsExist(allItems)){
            List<WebElement> listItems = driver.findElements(allItems);
            for (int i = listItems.size(); i > 0; i--) {

                // By pass if item is a gift
                By isUnitPriceExist = By.xpath(String.format(itemUnitPrice, i));
                if (!validateHelper.verifyElementIsExist(isUnitPriceExist)) {
                    logTest.info("Row " + i + " is a Gift item. Skipping...");
                    continue;
                }

                boolean isCalculateTrue = verifyTotalPriceItemsInCartByIndex(i);
                logTest.info(getQuantityItemsInCartByIndex(i));
                logTest.info(getUnitPriceItemsInCartByIndex(i));
                logTest.info(getTotalPriceItemsInCartByIndex(i));
                if (isCalculateTrue) {
                    logTest.info("[PASS] Calculation item at position: " + i);
                } else {
                    logTest.error("[FAIL] Calculation item at position: " + i);
                }
            }
        } else if (validateHelper.verifyElementIsDisplay(cartEmptyText)) {
            logTest.info("Cart page empty");
        }
    }

}
