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
import org.testng.Assert;
import pojoClass.ProductModel;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class CheckoutPage {

    private WebDriver driver;
    private ValidateHelper validateHelper;
    private JavascriptExecutor js;
    private WebDriverWait wait;
    private CustomSoftAssert softAssert;

    public CheckoutPage(WebDriver driver) {
        this.driver = driver;
        validateHelper = new ValidateHelper(driver);
        this.js = (JavascriptExecutor) driver ;
        this.softAssert = new CustomSoftAssert(driver);
        try{
            this.wait = new WebDriverWait(driver,
                    Duration.ofSeconds(10),
                    Duration.ofMillis(500));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // --- PAGE ELEMENT ---
    private By cartTitle = By.xpath("//h2[normalize-space()='Thanh toán']");
    private By allItems = By.xpath("//div//article[@class='mt-2.5']");
    private By TotalPrice = By.xpath("//p[contains(.,'Thành tiền (Đã VAT)')]/following-sibling::span");
    private By checkoutBtn = By.xpath("//button[@aria-label='Đặt hàng']");
    private By nameReceive = By.xpath("//h2[contains(.,'Địa chỉ nhận hàng')]/parent::section//span[@class='font-sans text-sm font-bold']");
    private By fullAddress = By.xpath("//h2[contains(.,'Địa chỉ nhận hàng')]/parent::section//span[contains(@class,'font-sans text-sm font-normal')]");
    private By addressText = By.xpath("//h2[contains(text(),'Địa chỉ nhận hàng')]");
    private By paymentText = By.xpath("//h2[contains(text(),'Hình thức thanh toán')]");

    // --- Dynamic xpath ---
    String itemBrand = "//div//article[@class='mt-2.5'][%d]//h2";
    String itemName = "//div//article[@class='mt-2.5'][%d]//h3";
    String itemUnitPrice ="//div//article[@class='mt-2.5'][%d]//span[@class='font-bold']";
    String itemUnitDiscoutPrice = "//div//article[@class='mt-2.5'][%d]//p[contains(.,'Giá sau voucher Hasaki')]/following-sibling::span";
    String itemQuantity = "//div//article[@class='mt-2.5'][%d]//div//span[1]";

    // ---- Action Dynamic ----
    @Step("Get brand items on checkout page by index")
    public String getBrandItemsInCheckoutByIndex(int index) {
        By itemBrandLocator = By.xpath(String.format(itemBrand, index));
        return validateHelper.getTextElement(itemBrandLocator);
    }

    @Step("Get name items on checkout page by index")
    public String getNameItemsInCheckoutByIndex(int index) {
        By itemNameLocator = By.xpath(String.format(itemName, index));
        return validateHelper.getTextElement(itemNameLocator);
    }

    @Step("Get quantity items on checkout page by index")
    public long getQuantityItemsInCheckoutByIndex(int index) {
        By itemQuantityLocator = By.xpath(String.format(itemQuantity, index));
        return validateHelper.parseCurrencyToLong(validateHelper.getTextElement(itemQuantityLocator));
    }

    @Step("Get unit price of items on checkout page by index")
    public long getUnitPriceItemsInCheckoutByIndex(int index) {
        By itemUnitPriceLocator = By.xpath(String.format(itemUnitPrice, index));
        return validateHelper.parseCurrencyToLong(validateHelper.getTextElement(itemUnitPriceLocator));
    }

    @Step("Get unit discount price of items on checkout page by index")
    public long getUnitDiscountPriceItemsInCheckoutByIndex(int index) {
        By itemUnitDiscountPriceLocator = By.xpath(String.format(itemUnitDiscoutPrice, index));
        return validateHelper.parseCurrencyToLong(validateHelper.getTextElement(itemUnitDiscountPriceLocator));
    }


    // ---- Get Page Element ----
    @Step("Get name address")
    public String getNameReceive() {
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(nameReceive));
        return validateHelper.getTextElement(nameReceive);
    }

    @Step("Get full address")
    public String getFullAddress() {
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(fullAddress));
        return validateHelper.getTextElement(fullAddress);
    }

    @Step("Get total price")
    public long getTotalPrice() {
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(TotalPrice));
        return validateHelper.parseCurrencyToLong(validateHelper.getTextElement(TotalPrice));
    }

    // ---- Verify Page ----
    @Step("Verify product page url contains: /checkout ")
    public boolean verify_CheckoutPage_Url() {
        try {
            boolean urlContains = validateHelper.verifyUrl("hasaki.vn/checkout");
            if (urlContains){
                logTest.info("[PASS] Url: hasaki.vn/checkout is display");
                return validateHelper.verifyUrl("hasaki.vn/checkout");
            }
        } catch (Exception e) {
            logTest.error("[FAIL] actual Url is: " + driver.getCurrentUrl());
            return false;
        }
        return false;
    }

    @Step("Verify checkout page title is display")
    public boolean verify_CheckoutTitle() {
        try {
            validateHelper.scrollToTopPage_js();
            boolean isTitleDisplay = validateHelper.verifyElementIsDisplay(cartTitle);
            if (isTitleDisplay){
                logTest.info("[PASS] Title 'Thanh Toán' is display");
                return true;
            }
        } catch (Exception e) {
            logTest.error("[FAIL] Title 'Thanh Toán' is not display");
            return false;
        }
        return false;
    }

    @Step("Verify address title is display")
    public boolean verify_addressTitle() {
        try {
            boolean isTitleDisplay = validateHelper.verifyElementIsDisplay(addressText);
            if (isTitleDisplay){
                logTest.info("[PASS] address title 'Địa chỉ nhận hàng' is display");
                return true;
            }
        } catch (Exception e) {
            logTest.error("[FAIL] address title 'Địa chỉ nhận hàng' is not display");
            return false;
        }
        return false;
    }

    @Step("Verify payment method title is display")
    public boolean verify_paymentMethodTitle() {
        try {
            boolean isTitleDisplay = validateHelper.verifyElementIsDisplay(paymentText);
            if (isTitleDisplay){
                logTest.info("[PASS] payment method title 'Hình thức thanh toán' is display");
                return true;
            }
        } catch (Exception e) {
            logTest.error("[FAIL] payment method title 'Hình thức thanh toán' is not display");
            return false;
        }
        return false;
    }

    @Step("Verify 'Đặt Hàng' button is enable and display")
    public boolean verify_paymentBtnIsEnable() {
        try {
            boolean isCheckoutBtnIsEnable = validateHelper.verifyElementEnabled(checkoutBtn);
            boolean isCheckoutBtnIsDisplay = validateHelper.verifyElementIsDisplay(checkoutBtn);
            if (isCheckoutBtnIsEnable && isCheckoutBtnIsDisplay){
                logTest.info("[PASS] 'Đặt Hàng' button is enable and display");
                return true;
            }
        } catch (Exception e) {
            logTest.error("[FAIL] 'Đặt Hàng' button is not enable and display");
            return false;
        }
        return false;
    }

    // ---- Get all product in checkout page ----
    @Step("Get detailed List product in checkout")
    public List<ProductModel> getDetailedListProductInCheckout() {
        List<ProductModel> products = new ArrayList<>();
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(allItems));
        if (validateHelper.verifyElementIsExist(allItems)) {
            List<WebElement> rows = driver.findElements(allItems);
            for (int i = 1; i <= rows.size(); i++) {
                String brand = getBrandItemsInCheckoutByIndex(i);
                String name = getNameItemsInCheckoutByIndex(i);

                long price = 0;
                if (getUnitDiscountPriceItemsInCheckoutByIndex(i) != 0){
                    price = getUnitDiscountPriceItemsInCheckoutByIndex(i);
                } else {
                    price = getUnitPriceItemsInCheckoutByIndex(i);
                }

                long qty = getQuantityItemsInCheckoutByIndex(i);
                products.add(new ProductModel(brand, name, price, qty));
                logTest.info(products.get(i-1).toString());
            }
            return products;
        } else {
            logTest.info("Checkout page empty");
        }
        return products;
    }

    public void compareProductLists(List<ProductModel> cartList, List<ProductModel> checkoutList) {
        // 1. Compare quantity between Cart and Checkout page
        softAssert.assertEquals(checkoutList.size(), cartList.size(),
                "Số lượng loại sản phẩm không khớp! Cart: " + cartList.size() + " | Checkout: " + checkoutList.size());

        // 2. Check each product in Cart is in Checkout page
        for (ProductModel cartItem : cartList) {
            boolean found = false;
            for (ProductModel checkoutItem : checkoutList) {
                if (cartItem.equals(checkoutItem)) {
                    found = true;
                    logTest.info("Items equals: " + cartItem.getName());
                    break;
                }
            }
            // If not found
            if (!found) {
                logTest.error("Items not existed in checkout page: " + cartItem.getName());
                // Try to check if error is because of quantity/price
                for (ProductModel checkoutItem : checkoutList) {
                    if (cartItem.getName().equalsIgnoreCase(checkoutItem.getName())) {
                        if (cartItem.getPrice() != checkoutItem.getPrice()) {
                            logTest.error(String.format("   -> Wrong Price: Cart [%d] vs Checkout [%d]",
                                    cartItem.getPrice(), checkoutItem.getPrice()));
                        }
                        if (cartItem.getQuantity() != checkoutItem.getQuantity()) {
                            logTest.error(String.format("   -> Wrong Quantity: Cart [%d] vs Checkout [%d]",
                                    cartItem.getQuantity(), checkoutItem.getQuantity()));
                        }
                    }
                }
                softAssert.fail("Error data items: " + cartItem.getName());
            }
        }
        softAssert.assertAll();
    }




}

