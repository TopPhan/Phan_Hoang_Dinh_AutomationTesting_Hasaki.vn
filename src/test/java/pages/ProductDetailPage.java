package pages;

import com.log.logTest;
import com.utility.CustomSoftAssert;
import com.utility.Helpers.ValidateHelper;
import io.qameta.allure.Step;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class ProductDetailPage {
    private WebDriver driver;
    private ValidateHelper validateHelper;
    private CustomSoftAssert softAssert;
    private JavascriptExecutor js;
    private WebDriverWait wait;
    private Actions action;

    public ProductDetailPage(WebDriver driver) {
        this.driver = driver;
        validateHelper = new ValidateHelper(driver);
        softAssert = new CustomSoftAssert(driver);
        this.js = (JavascriptExecutor) driver ;
        this.action = new Actions(driver);
        try{
            this.wait = new WebDriverWait(driver,
                    Duration.ofSeconds(10),
                    Duration.ofMillis(500));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    // --- Products Element ---
    private By productName = By.xpath("//h1[@class='text-lg font-medium leading-[1.25]']");
    private By productPrice = By.xpath(
            "//span[contains(text(),'Đã bao gồm VAT')]/preceding-sibling::span | //span[@class='text-orange text-base font-bold leading-[22px] cursor-pointer']");
    private By productAdd = By.xpath("//div[contains(text(),'Giỏ hàng')]/parent::button");
    private By productCheckout = By.xpath("//button//div[contains(text(),'MUA NGAY NOWFREE 2H')]");
    private By productByNow = By.xpath("//div[contains(text(),'MUA NGAY')]/parent::button");
    private By descreseProduct = By.xpath("//button[@aria-label='Descrease btn']");
    private By increaseProduct = By.xpath("//button[@aria-label='Increase btn']");
    private By productQuantity = By.xpath("//input[@name='qty']");
    private By productAvailable = By.xpath("//span//b");
    private By cartButton = By.xpath("//a[@aria-label='Cart Nav']");
    private By numberOfItems = By.xpath("//span[contains(text(),'Cart Icon')]/following-sibling::span");
    private By productBrand = By.xpath("//a[@aria-label='now free 2h']/following-sibling::a");

    // --- Test Element ---
    private By onlyBuyOne = By.xpath("//div[contains(text(),'Sản phẩm chỉ được mua tối đa là 1')]");
    private By closePopupBuyOne = By.xpath("//button/preceding::div[contains(text(),'Sản phẩm chỉ được mua tối đa là 1')]");
    private By logoIsDisplay = By.xpath("//a[@aria-label='Homepage']");
    private By successPopup = By.xpath("//div[contains(text(),'Sản Phẩm đã được thêm vào giỏ hàng thành công')]");
    private By closePopup = By.xpath("//div[@class='grid gap-1']/following-sibling::button");


    // ---------- VERIFY PAGE --------------
    @Step("Verify that product page URL contains '/san-pham/'")
    public boolean verify_ProductPage_Url() {
        try {
            boolean urlContains = validateHelper.verifyUrl("hasaki.vn/san-pham/");
            if (urlContains){
                logTest.info("[PASS] Url: hasaki.vn/san-pham/... is display");
                return true;
            }
        } catch (Exception e) {
            logTest.error("[FAIL] actual Url is: " + driver.getCurrentUrl());
            return false;
        }
        return false;
    }

    @Step("Verify that product name displays correctly as: '{0}'")
    public boolean isProductNameDisplay(String name) {
        try {

            Boolean nameFound = validateHelper.verifyElementIsDisplay(productName);
            Boolean nameMatch = validateHelper.getTextElement(productName).trim().toLowerCase().contains(name.trim().toLowerCase());
            if(nameFound && nameMatch){
                logTest.info("[PASS] Product name: " + validateHelper.getTextElement(productName) + " is display");
            }
            return nameFound && nameMatch;
        } catch (Exception e) {
            logTest.error("[FAIL] Product name is not display");
            return false;
        }
    }

    @Step("Check if 'Added to Cart' button is displayed and enabled")
    public boolean isAddCartEnable() {
        try {

            Boolean isAddCartDisplay = validateHelper.verifyElementIsDisplay(productAdd);
            Boolean isAddCartEnable = validateHelper.verifyElementEnabled(productAdd);

            if(isAddCartDisplay && isAddCartEnable){
                logTest.info("[PASS] Add Cart button is display & enable");
            }
            return isAddCartDisplay && isAddCartEnable;
        } catch (Exception e) {
            logTest.error("[FAIL] Add Cart button isn't display or enable");
            return false;
        }
    }

    @Step("Verify that 'Added to Cart' success popup is visible")
    public boolean isPopupAddToCartDisplay() {
        try {
            Boolean popupFound = validateHelper.verifyElementIsDisplay(successPopup);
            Boolean popupMatch = validateHelper.getTextElement(successPopup).trim().contains("Sản Phẩm đã được thêm vào giỏ hàng thành công");
            if(popupFound && popupMatch){
                logTest.info("[PASS] Popup: " + validateHelper.getTextElement(successPopup) + " is display");
            }
            return popupFound && popupMatch;
        } catch (Exception e) {
            logTest.error("[FAIL] Popup is not display");
            return false;
        }
    }

    @Step("Check if product has a purchase limit of 1 (Only Buy One popup)")
    public boolean isProductAllowOnlyBuyOne() {
        try {
            validateHelper.waitForElementVisible(onlyBuyOne,3);
            Boolean popupFound = driver.findElement(onlyBuyOne).isDisplayed();
            Boolean popupMatch = driver.findElement(onlyBuyOne).getText().trim().contains("Sản phẩm chỉ được mua tối đa là 1");
            if(popupFound && popupMatch){
                logTest.info("[PASS] Popup: " + driver.findElement(onlyBuyOne).getText().trim() + " is display");
            }
            return popupFound && popupMatch;
        } catch (Exception e) {
            logTest.error("[FAIL] Popup is not display");
            return false;
        }
    }

    // --------------- ACTION -----------------
    @Step("Click on 'Add to Cart' button - Linking CartPage")
    public CartPage addProductToCart() {
        try {
            validateHelper.clickElement(productAdd);
            logTest.info("[PASS] Add product");
            return new CartPage(driver);
        } catch (Exception e) {
            logTest.error("[FAIL] Add product");
            throw new RuntimeException(e);
        }
    }

    @Step("Retrieve current product quantity from input field")
    public int getProductQuantity() {
        try {
            validateHelper.scrollToTopPage_js();
            String rawText = validateHelper.getTextElement(productQuantity).trim();
            logTest.info("Raw cart text: '" + rawText + "'");
            if (rawText.isEmpty() || !rawText.matches("\\d+")) {
                logTest.warn("[WARN] Product is empty or contains non-numeric text. Returning 0.");
                return 0;
            }
            return Integer.parseInt(rawText);
        } catch (Exception e) {
            logTest.error("[FAIL] Unexpected error getting product quantity: " + e.getMessage());
            return 0;
        }
    }

    @Step("Retrieve total items count from cart badge")
    public int getCartQuantity() {
        try {
            validateHelper.scrollToTopPage_js();
            wait.until(ExpectedConditions.elementToBeClickable(numberOfItems));
            String rawText = validateHelper.getTextElement(numberOfItems).trim();
            logTest.info("Raw cart text: '" + rawText + "'");
            if (rawText.isEmpty() || !rawText.matches("\\d+")) {
                logTest.warn("[WARN] Cart badge is empty. Returning 0.");
                return 0;
            }
            return Integer.parseInt(rawText);
        } catch (Exception e) {
            logTest.error("[FAIL] Unexpected error getting cart quantity: " + e.getMessage());
            return 0;
        }
    }

    @Step("Retrieve product brand name")
    public String getProductBrand() {
        try {
            validateHelper.scrollToTopPage_js();
            wait.until(ExpectedConditions.visibilityOfElementLocated(productBrand));
            String rawText = validateHelper.getTextElement(productBrand).trim();
            logTest.info("Prodcut brand text: '" + rawText + "'");
            if (rawText.isEmpty()) {
                logTest.warn("[WARN] Product brand is empty Returning ''");
                return "";
            }
            return rawText;
        } catch (Exception e) {
            logTest.error("[FAIL] Unexpected error getting product brand: " + e.getMessage());
            return "";
        }
    }

    @Step("Retrieve product name from header")
    public String getProductName() {
        try {
            validateHelper.scrollToTopPage_js();
            wait.until(ExpectedConditions.visibilityOfElementLocated(productName));
            String rawText = validateHelper.getTextElement(productName).trim();
            logTest.info("Product name text: '" + rawText + "'");
            if (rawText.isEmpty()) {
                logTest.warn("[WARN] Product name is empty Returning ''");
                return "";
            }
            return rawText;
        } catch (Exception e) {
            logTest.error("[FAIL] Unexpected error getting product name: " + e.getMessage());
            return "";
        }
    }

    @Step("Retrieve product price and convert to long")
    public long getProductPrice() {
        try {
            validateHelper.scrollToTopPage_js();
            validateHelper.waitForElementVisible(productPrice,2);
            String rawText = validateHelper.getTextElement(productPrice).trim();
            long price = validateHelper.parseCurrencyToLong(rawText);

            logTest.info("Product name price: '" + price + "'");
            if (price == 0) {
                logTest.warn("[WARN] Product price is empty Returning '0'");
                return 0;
            }
            return price;
        } catch (Exception e) {
            logTest.error("[FAIL] Unexpected error getting product price: " + e.getMessage());
            return 0;
        }
    }


    @Step("Close the success 'Added to Cart' popup")
    public void closeSuccessPopup() {
        validateHelper.clickElement(closePopup);
        validateHelper.waitForElementInvisible(closePopup);
    }

    @Step("Close the 'Purchase Limit' (Only Buy One) popup")
    public void closeOnlyBuyOnePopup() {
        validateHelper.clickElement(closePopupBuyOne);
        validateHelper.waitForElementInvisible(closePopupBuyOne);
    }

    @Step("Set product quantity to: {0} using 'Increase' button")
    public void setProductQuantity(String quantity) {
        try {
            int intQty = Integer.parseInt(quantity);
            for (int i = 0 ; i< intQty-1; i++){
                validateHelper.clickElement(increaseProduct);
            }
            logTest.info("[PASS] Success add " + quantity + " product to cart");
        } catch (Exception e) {
            logTest.error("[FAIL] to add " + quantity + " product to cart");
            throw new RuntimeException(e);
        }
    }



    // -------- Linking page -----------
    @Step("Click 'Checkout' and navigate to Checkout Page")
    public CheckoutPage quickCheckOutProduct() {
        try {
            validateHelper.clickElement(productCheckout);
            logTest.info("[PASS] Go to checkout product");
            return new CheckoutPage(driver);
        } catch (Exception e) {
            logTest.error("[FAIL] Can't checkout product");
            throw new RuntimeException(e);
        }
    }

    @Step("Click on cart icon and navigate to Cart Page")
    public CartPage quickGoToCart() {
        try {
            validateHelper.clickElement(cartButton);
            logTest.info("[PASS] Go to cart");
            return new CartPage(driver);
        } catch (Exception e) {
            logTest.error("[FAIL] Can't go tocart");
            throw new RuntimeException(e);
        }
    }

}
