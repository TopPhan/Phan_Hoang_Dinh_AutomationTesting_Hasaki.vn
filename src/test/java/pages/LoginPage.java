package pages;

import com.log.logTest;
import com.utility.Helpers.ValidateHelper;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage {

    private final WebDriver driver;
    private final ValidateHelper validateHelper;

    private By hasakiLogo = By.xpath("//a[@class='logo']//img[@class='loading']");
    private By popup_login = By.xpath("//a[@href='#'][@class='icon_header']");
    private By popup_login_button = By.xpath("//a[@id='hskLoginButton']");
    private By emailInput = By.xpath("//input[@id='username']");
    private By passwordInput = By.xpath("//input[@id='password']");
    private By signinBtn = By.xpath("//button[contains(text(),'Đăng nhập')]");
    private By signUpLinkBtn = By.xpath("//a[contains(text(),'Đăng ký ngay')]/preceding::form[@id='form-head-LoginTest']");
    private By forgotPasswordLinkBtn = By.xpath("//a[contains(text(),'Quên mật khẩu')]");
    private By rememberPasswordCheckbox = By.xpath("//label[contains(.,'Nhớ mật khẩu')]//input[@type='checkbox']");
    private By headerUsername = By.xpath("//span[@class='header_username']");
    private By headerUsernameAddress = By.xpath("//span[normalize-space()='Chào']");
    private By errorMessage = By.xpath("//div[@class='alert alert-danger']");
    private By cartBtn = By.xpath("//a[@href='https://hasaki.vn/checkout/cart']");


    //---- Label Group ---
    private By labelLogin = By.xpath("//form[@id='form-head-login']//div[contains(.,'Hoặc đăng nhập với Hasaki.vn')]");
    private By labelRememberPassword = By.xpath("//label[contains(.,'Nhớ mật khẩu')]");
    private By labelYouNotHaveAccount = By.xpath("//form[@id='form-head-login']");

    //--- Start popup ---
    public By acceptCookie = By.xpath("//button[@id='acceptCookies']");
    public By getAcceptCookie() {
        return acceptCookie;
    }


    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.validateHelper  = new ValidateHelper(driver);
    }

    // ---- Verify Page ----
    @Step("Check if user is successfully logged in")
    public boolean isLoggedIn() {
        validateHelper.waitForElementVisible(headerUsername,5);
        validateHelper.waitForElementVisible(headerUsernameAddress,5);
        return validateHelper.verifyElementIsExist(headerUsername) || validateHelper.verifyElementIsExist(headerUsernameAddress) ;
    }

    @Step("Validate Login Page Title")
    public boolean verify_SignInPage_Title() {
        try {
            validateHelper.verifyElementIsDisplay(hasakiLogo);
            String expectedTitle = "Hasaki.vn | Mỹ Phẩm & Clinic";
            if(driver.getTitle().trim().equals(expectedTitle)){
                logTest.info("[PASS] sign in page title match");
            };
            return driver.getTitle().trim().equals(expectedTitle);
        } catch (Exception e) {
            logTest.error("[FAIL] sign in page title doesn't match");
        }
        return false;
    }

    @Step("Verify Sign In page url")
    public boolean verify_SignInPage_Url(){
        try {
            validateHelper.verifyElementIsDisplay(hasakiLogo);
            boolean isUrlDisplay = validateHelper.verifyUrl("hasaki.vn");
            if(isUrlDisplay){
                logTest.info("[PASS] sign in page url match");
                return true;
            };
        } catch (Exception e) {
            logTest.error("[FAIL] sign in page url doesn't match");
        }
        return false;
    }

    @Step("Check if Email/Phone input has placeholder: '{0}'")
    public boolean verify_EmailPhone_placeholder(String expectText) {
        try{
            if(validateHelper.getTextPlaceholder(emailInput,expectText)){
                logTest.info("[PASS] Email/Phone placeholder match");
            };
            return validateHelper.getTextPlaceholder(emailInput,expectText);
        } catch (Exception e) {
            logTest.error("[FAIL] Email/Phone placeholder doesn't match");
        }
        return false;
    }

    @Step("Check if Password input has placeholder: '{0}'")
    public boolean verify_Password_placeholder(String expectText) {
        try{
            if(validateHelper.getTextPlaceholder(passwordInput,expectText)){
                logTest.info("[PASS] Password placeholder match");
            };
            return validateHelper.getTextPlaceholder(passwordInput,expectText);
        } catch (Exception e) {
            logTest.error("[FAIL] Password placeholder doesn't match");
        }
        return false;
    }

    @Step("Verify label LoginTest,Remember password,You not have account")
    public boolean verify_Label() {
        try {
            if(validateHelper.verifyElementIsDisplay(labelLogin)
                    && validateHelper.verifyElementIsDisplay(labelRememberPassword)
                    && validateHelper.verifyElementIsDisplay(labelYouNotHaveAccount)){
                logTest.info("[PASS] Label LoginTest,Remember password,You not have account display");
            }
            return validateHelper.verifyElementIsDisplay(labelLogin)
                    && validateHelper.verifyElementIsDisplay(labelRememberPassword)
                    && validateHelper.verifyElementIsDisplay(labelYouNotHaveAccount);
        } catch (Exception e) {
            logTest.info("[FAIL] Label LoginTest,Remember password,You not have account, doesn't display");
        }
        return false;
    }


    // ---- Get page element ----
    @Step("Get text error message")
    public String getTextErrorMessage(){
        return validateHelper.getTextElement(errorMessage);
    }

    @Step("Get user header name after login")
    public String getTextHeaderUser(){
        return validateHelper.getTextElement(headerUsername);
    }

    // ---- Action ----
    @Step("Login to Hasaki.vn with email: '{0}' - Linking MyAccountPage")
    public MyAccountPage login_user(String username, String password) throws Exception {

        // Check page has asked cookie,
        try{
            validateHelper.waitForElementVisible(acceptCookie,5);
            validateHelper.verifyElementIsExist(acceptCookie);
            validateHelper.clickElement(acceptCookie);
        } catch (Exception e) {
            logTest.error("[FAIL] Cookie banner did not appear");
        }

        validateHelper.action_MovetoElement(popup_login);
        validateHelper.clickElement(popup_login_button);
        validateHelper.setText(emailInput, username);
        validateHelper.setText(passwordInput, password);
        validateHelper.clickElement(signinBtn);

        return new MyAccountPage(driver);
    }

    @Step("Perform login and enable 'Remember Me' for user: {0} - Linking MyAccountPage")
    public MyAccountPage login_remember_user(String username, String password) throws Exception {

        validateHelper.Delay(2000);

        // Check page has asked cookie,
        try{
            validateHelper.waitForElementVisible(acceptCookie,5);
            validateHelper.verifyElementIsExist(acceptCookie);
            validateHelper.clickElement(acceptCookie);
        } catch (Exception e) {
            logTest.error("[FAIL] Cookie banner did not appear");
        }

        validateHelper.action_MovetoElement(popup_login);
        validateHelper.clickElement(popup_login_button);
        validateHelper.setText(emailInput, username);
        validateHelper.setText(passwordInput, password);
        validateHelper.clickElement(rememberPasswordCheckbox);
        validateHelper.clickElement(signinBtn);

        return new MyAccountPage(driver);
    }

    @Step("Close cookie banner")
    public void bypass_Cookie() throws Exception {

        // Check page has asked cookie,
        try{
            validateHelper.waitForElementVisible(acceptCookie,5);
            validateHelper.verifyElementIsExist(acceptCookie);
            validateHelper.clickElement(acceptCookie);
        } catch (Exception e) {
            logTest.error("[FAIL] Cookie banner did not appear");
        }

        validateHelper.action_MovetoElement(popup_login);
        validateHelper.clickElement(popup_login_button);
    }

    @Step("Click on Cart icon redirect to Cart Page - Linking CartPage")
    public CartPage quickGoToCart() {
        try {
            validateHelper.clickElement(cartBtn);
            logTest.info("[PASS] Go to cart");
            return new CartPage(driver);
        } catch (Exception e) {
            logTest.error("[FAIL] Can't go tocart");
            throw new RuntimeException(e);
        }
    }
}
