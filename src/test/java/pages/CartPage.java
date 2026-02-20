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
import pojoClass.ProductModel;

import java.time.Duration;
import java.util.ArrayList;
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
    private By cartTotalPrice = By.xpath("//div[contains(text(),'Tạm tính:')]//span");
    private By checkoutBtn = By.xpath("//div[contains(text(),'Tạm tính:')]/following-sibling::button");

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
    String onlyByOneItems = "//tbody//tr[%d]//p[.='Sản phẩm chỉ được mua tối đa là 1']";

    // ---- Action Dynamic ----
    @Step("Execute: Remove item from cart at row index {0}")
    public void deleteItemsInCartByIndex(int index) {
        By itemDeleteBtnLocator = By.xpath(String.format(itemDeleteBtn, index));
        WebElement itemDelete = driver.findElement(itemDeleteBtnLocator);
        validateHelper.scrollToTopPage_js();
        validateHelper.clickElement(itemDeleteBtnLocator);
        wait.until(ExpectedConditions.stalenessOf(itemDelete));
    }

    @Step("Retrieve Brand name of product at row {0}")
    public String getBrandItemsInCartByIndex(int index) {
        By itemBrandLocator = By.xpath(String.format(itemBrand, index));
        return validateHelper.getTextElement(itemBrandLocator);
    }

    @Step("Retrieve Product name at row {0}")
    public String getNameItemsInCartByIndex(int index) {
        By itemNameLocator = By.xpath(String.format(itemName, index));
        return validateHelper.getTextElement(itemNameLocator);
    }

    @Step("Retrieve current Quantity at row {0}")
    public long getQuantityItemsInCartByIndex(int index) {
        By itemQuantityLocator = By.xpath(String.format(itemQuantity, index));
        return validateHelper.parseCurrencyToLong(validateHelper.getElementAttribute(itemQuantityLocator, "value"));
    }

    @Step("Retrieve Unit Price at row {0}")
    public long getUnitPriceItemsInCartByIndex(int index) {
        By itemUnitPriceLocator = By.xpath(String.format(itemUnitPrice, index));
        return validateHelper.parseCurrencyToLong(validateHelper.getTextElement(itemUnitPriceLocator));
    }

    @Step("Retrieve Subtotal Price (UI) at row {0}")
    public long getTotalPriceItemsInCartByIndex(int index) {
        By itemTotalPriceLocator = By.xpath(String.format(itemTotalPrice, index));
        return validateHelper.parseCurrencyToLong(validateHelper.getTextElement(itemTotalPriceLocator));
    }

    @Step("Action: Increase quantity at row {0} by {1} times")
    public void increaseItemsInCartByIndex(int index, int times) {
        By itemIncreaseLocator = By.xpath(String.format(increaseQuantityBtn, index));
        By itemQuantityLocator = By.xpath(String.format(itemQuantity, index));
        for (int i =0 ; i<times; i++) {
           validateHelper.clickElement(itemIncreaseLocator);
           validateHelper.waitForElementVisible(itemQuantityLocator,1);
        }
        logTest.info("Increased quantity at row " + index + " by " + times);
    }

    @Step("Action: Decrease quantity at row {0} by {1} times")
    public void DecreaseItemsInCartByIndex(int index, int times) {
        By itemDecreaseLocator = By.xpath(String.format(decreaseQuantityBtn, index));
        By itemQuantityLocator = By.xpath(String.format(itemQuantity, index));
        for (int i =0 ; i<times; i++) {
            validateHelper.clickElement(itemDecreaseLocator);
            validateHelper.waitForElementVisible(itemQuantityLocator,1);
        }
        logTest.info("Decreased quantity at row " + index + " by " + times);
    }

    @Step("Validate calculation logic for item at row {0}")
    public boolean verifyTotalPriceItemsInCartByIndex(int index) throws InterruptedException {
        long expectedTotalPrice = getQuantityItemsInCartByIndex(index) * getUnitPriceItemsInCartByIndex(index);
        validateHelper.Delay(500);
        long actualTotalPrice = getTotalPriceItemsInCartByIndex(index);
        logTest.info(String.format("Verification at row %d - Expected: %d, Actual UI: %d", index, expectedTotalPrice, actualTotalPrice));
        return expectedTotalPrice == actualTotalPrice;
    }

    @Step("Retrieve calculated Subtotal for row {0} (Qty * UnitPrice)")
    public long getSubTotalPriceItemsInCartByIndex(int index) {
        return getQuantityItemsInCartByIndex(index) * getUnitPriceItemsInCartByIndex(index);
    }

    @Step("Retrieve Grand Total Price from UI")
    public long getTotalPriceAllItemsInCart() {
        return validateHelper.parseCurrencyToLong(validateHelper.getTextElement(cartTotalPrice));
    }

    @Step("Check if Cart is empty")
    public boolean verifyCartIsEmpty() {
        boolean isEmpty = validateHelper.verifyElementIsExist(cartEmptyText);
        logTest.info("Cart empty status: " + isEmpty);
        return isEmpty;
    }


    // ---------- VERIFY PAGE --------------
    @Step("Validate Cart Page URL")
    public boolean verify_ProductPage_Url() {
        try {
            boolean urlContains = validateHelper.verifyUrl("hasaki.vn/checkout/cart");
            if (urlContains){
                logTest.info("[PASS] Url: hasaki.vn/checkout/cart is display");
                return true;
            }
        } catch (Exception e) {
            logTest.error("[FAIL] Cart URL mismatch: " + driver.getCurrentUrl());
            return false;
        }
        return false;
    }

    @Step("Validate Cart Title 'Giỏ hàng' is displayed")
    public boolean verify_cartTitle() {
        try {
            boolean isTitleDisplay = validateHelper.verifyElementIsDisplay(cartTitle);
            if (isTitleDisplay){
                logTest.info("[PASS] Title 'Giỏ hàng' is display");
                return true;
            }
        } catch (Exception e) {
            logTest.error("[FAIL] Title 'Giỏ hàng' is not display");
            return false;
        }
        return false;
    }

    // ---- ACTION LOOP ALL ITEMS IN CART ----
    @Step("Execute: Clear all items from cart")
    public void deleteAllItemInCart() {
        validateHelper.waitForElementVisible(allItems,3);
        List<WebElement> listItems = driver.findElements(allItems);
        if (!listItems.isEmpty()) {
           while (validateHelper.verifyElementIsExist(By.xpath("//button[contains(text(),'Xóa')]"))) {
                try {
                    deleteItemsInCartByIndex(1);
                } catch (Exception e) {
                    logTest.warn("Small error during deletion, retrying... " + e.getMessage());
                    break;
                }
            }
        } else if (validateHelper.verifyElementIsDisplay(cartEmptyText)) {
            logTest.info("Nothing to delete, cart is already empty.");
        }
        validateHelper.waitForElementVisible(cartTitle,1);
    }

    @Step("Execute: Validate pricing calculation for ALL items in cart")
    public boolean verifyCalculationPriceAllItemInCart() throws InterruptedException {
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(allItems));
        if (validateHelper.verifyElementIsExist(allItems)){
            List<WebElement> listItems = driver.findElements(allItems);
            for (int i = listItems.size()-1; i > 0; i--) {
                // By pass if item is a gift
                By isUnitPriceExist = By.xpath(String.format(itemUnitPrice, i));
                if (!validateHelper.verifyElementIsExist(isUnitPriceExist)) {
                    logTest.info("Row " + i + " is a Gift (No price). Skipping...");
                    continue;
                }
                boolean isCalculateTrue = verifyTotalPriceItemsInCartByIndex(i);
                if (!isCalculateTrue) {
                    logTest.error("[FAIL] Calculation item at position: " + i);
                    return false;
                }
            }
            return true;
        } else if (validateHelper.verifyElementIsDisplay(cartEmptyText)) {
            logTest.info("Nothing to delete, cart is already empty.");
        }
        return false;
    }

    @Step("Calculate expected Grand Total by summing all item subtotals")
    public long getPreCalculatePriceAllItemInCart() throws InterruptedException {
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(allItems));
        if (validateHelper.verifyElementIsExist(allItems)){
            List<WebElement> listItems = driver.findElements(allItems);
            long totalPrice = 0;
            for (int i = listItems.size()-1; i > 0; i--) {
                // By pass if item is a gift
                By isUnitPriceExist = By.xpath(String.format(itemUnitPrice, i));
                if (!validateHelper.verifyElementIsExist(isUnitPriceExist)) {
                    logTest.info("Row " + i + " is a Gift (No price). Skipping...");
                    continue;
                }
                validateHelper.Delay(500);
                totalPrice += getSubTotalPriceItemsInCartByIndex(i);
            }
            logTest.info("Calculated sum of all items: " + totalPrice);
            return totalPrice;
        } else if (validateHelper.verifyElementIsDisplay(cartEmptyText)) {
            logTest.info("Nothing to delete, cart is already empty.");
            return 0;
        }
        return 0;
    }

    @Step("Execute: Increase quantity for the valid item by {0}")
    public void verifyIncreaseQuantityOfItemInCart(int quantity) {
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(allItems));
        if (validateHelper.verifyElementIsExist(allItems)){
            List<WebElement> listItems = driver.findElements(allItems);
            for (int i = listItems.size(); i > 0; i--) {
                // By pass if item is a gift
                By isUnitPriceExist = By.xpath(String.format(itemUnitPrice, i));
                if (!validateHelper.verifyElementIsExist(isUnitPriceExist)) {
                    logTest.info("Row " + i + " is a Gift (No price). Skipping...");
                    continue;
                }
                // Increase quantity of item by 3 = 4
                increaseItemsInCartByIndex(i,quantity);
                By isOnlyByOneItems = By.xpath(String.format(onlyByOneItems,i));
                if (validateHelper.verifyElementIsExist(isOnlyByOneItems)) {
                    logTest.info("Row " + i + " has purchase limit of 1. Reverting...");
                    DecreaseItemsInCartByIndex(i,3);
                    continue;
                }
                break;
            }
        } else if (validateHelper.verifyElementIsDisplay(cartEmptyText)) {
            logTest.info("Nothing to delete, cart is already empty.");
        }
    }

    @Step("Retrieve all product details from cart as a list")
    public List<ProductModel> getDetailedListProductInCart() {
        List<ProductModel> products = new ArrayList<>();
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(allItems));
        if (validateHelper.verifyElementIsExist(allItems)) {
            List<WebElement> rows = driver.findElements(allItems);
            for (int i = 1; i < rows.size(); i++) {
                String brand = getBrandItemsInCartByIndex(i);
                String name = getNameItemsInCartByIndex(i);
                long price = getUnitPriceItemsInCartByIndex(i);
                long qty = getQuantityItemsInCartByIndex(i);
                products.add(new ProductModel(brand, name, price, qty));
                logTest.info(products.get(i-1).toString());
            }
            return products;
        } else if (validateHelper.verifyElementIsDisplay(cartEmptyText)) {
            logTest.info("Cart page empty");
        }
        return products;
    }

    // -------- Linking page -----------
    @Step("Proceed to Checkout page - Linking CheckoutPage")
    public CheckoutPage quickCheckOutProduct() {
        try {
            validateHelper.clickElement(checkoutBtn);
            logTest.info("Clicked Checkout button. Linking to CheckoutPage...");
            return new CheckoutPage(driver);
        } catch (Exception e) {
            logTest.error("[FAIL] Can't checkout product");
            throw new RuntimeException(e);
        }
    }

}
