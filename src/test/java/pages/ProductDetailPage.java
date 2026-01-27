package pages;

import com.log.logTest;
import com.utility.CustomSoftAssert;
import com.utility.Helpers.ValidateHelper;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class ProductDetailPage {
    private WebDriver driver;
    private ValidateHelper validateHelper;
    private CustomSoftAssert softAssert;
    private JavascriptExecutor js;
    private WebDriverWait wait;

    public ProductDetailPage(WebDriver driver) {
        this.driver = driver;
        validateHelper = new ValidateHelper(driver);
        softAssert = new CustomSoftAssert(driver);
        this.js = (JavascriptExecutor) driver ;
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
    private By productPrice = By.xpath("//span[@class='text-orange text-base font-bold leading-[22px] cursor-pointer']");
    private By productAdd = By.xpath("//div[contains(text(),'Giỏ hàng')]/parent::button");
    private By productByNow = By.xpath("//div[contains(text(),'MUA NGAY')]/parent::button");
    private By descreseProduct = By.xpath("//button[@aria-label='Descrease btn']");
    private By increaseProduct = By.xpath("//button[@aria-label='Increase btn']");
    private By productQuantity = By.xpath("//input[@name='qty']");
    private By productAvailable = By.xpath("//span//b");
    private By cartButton = By.xpath("//a[@aria-label='Cart Nav']");
    private By numberOfItems = By.xpath("//span[contains(text(),'Cart Icon')]/following-sibling::span");

    // ---
    private By logoIsDisplay = By.xpath("//a[@aria-label='Homepage']");
    private By successPopup = By.xpath("//div[contains(text(),'Sản Phẩm đã được thêm vào giỏ hàng thành công')]");
    private By closePopup = By.xpath("//div[@class='grid gap-1']/following-sibling::button");


    // ---------- VERIFY PAGE --------------
    @Step("Verify product page url contains: /san-pham ")
    public boolean verify_ProductPage_Url() {
        try {
            boolean urlContains = validateHelper.verifyUrl("hasaki.vn/san-pham/");
            if (urlContains){
                logTest.info("[PASS] Url: hasaki.vn/san-pham/... is display");
                return validateHelper.verifyUrl("hasaki.vn/san-pham/");
            }
        } catch (Exception e) {
            logTest.error("[FAIL] actual Url is: " + driver.getCurrentUrl());
            return false;
        }
        return false;
    }

    @Step("Verify product name is display correctly")
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

    @Step("Verify product name is display correctly")
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

    @Step("Get cart quantity")
    public int getCartQuantity() {
        try {

            String rawText = validateHelper.getTextElement(numberOfItems).trim();
            logTest.info("Raw cart text: '" + rawText + "'");
            if (rawText.isEmpty() || !rawText.matches("\\d+")) {
                logTest.warn("[WARN] Cart is empty or contains non-numeric text. Returning 0.");
                return 0;
            }
            return Integer.parseInt(rawText);
        } catch (Exception e) {
            logTest.error("[FAIL] Unexpected error getting cart quantity: " + e.getMessage());
            return 0;
        }
    }

    @Step("Verify popup add to cart is display")
    public boolean isPopupAddToCartDisplay() {
        try {
            Boolean popupFound = validateHelper.verifyElementIsDisplay(successPopup);
            Boolean popupMatch = validateHelper.getTextElement(successPopup).trim().contains("Sản Phẩm đã được thêm vào giỏ hàng thành công");
            if(popupFound && popupMatch){
                logTest.info("[PASS] Popup: " + validateHelper.getTextElement(productName) + " is display");
            }
            return popupFound && popupMatch;
        } catch (Exception e) {
            logTest.error("[FAIL] Popup is not display");
            return false;
        }
    }

    @Step("Close success popup")
    public void closeSuccessPopup() {
        validateHelper.waitForElementInvisible(successPopup);
    }

    // --------------- ACTION -----------------
    @Step("Add single product to cart")
    public void addSingleProductToCart() {
        try {
            validateHelper.clickElement(productAdd);
            logTest.info("[PASS] Add single product");
        } catch (Exception e) {
            logTest.error("[FAIL] Add single product");
        }
    }






}
