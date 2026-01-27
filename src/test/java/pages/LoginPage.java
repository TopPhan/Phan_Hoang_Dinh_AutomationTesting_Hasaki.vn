package pages;

import com.log.logTest;
import com.utility.Helpers.ValidateHelper;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage {

    private WebDriver driver;
    private ValidateHelper validateHelper;

    // Hasaki logo
    private By hasakiLogo = By.xpath("//a[@class='logo']//img[@class='loading']");
    // Popup LoginTest panel
    private By popup_login = By.xpath("//a[@href='#'][@class='icon_header']");
    // Popup LoginTest button
    private By popup_login_button = By.xpath("//a[@id='hskLoginButton']");
    // Email input textbox
    private By emailInput = By.xpath("//input[@id='username']");
    // Password input textbox
    private By passwordInput = By.xpath("//input[@id='password']");
    // Sign-in button
    private By signinBtn = By.xpath("//button[contains(text(),'Đăng nhập')]");
    // Sign-up link
    private By signUpLinkBtn = By.xpath("//a[contains(text(),'Đăng ký ngay')]/preceding::form[@id='form-head-LoginTest']");
    // Forgot password link
    private By forgotPasswordLinkBtn = By.xpath("//a[contains(text(),'Quên mật khẩu')]");
    // Remember password checkbox
    private By rememberPasswordCheckbox = By.xpath("//label[contains(.,'Nhớ mật khẩu')]//input[@type='checkbox']");
    // Header username
    private By headerUsername = By.xpath("//span[@class='header_username']");
    private By headerUsernameAddress = By.xpath("//span[normalize-space()='Chào']");
    // Show error message
    private By errorMessage = By.xpath("//div[@class='alert alert-danger']");



    //---- Label Group ---
    // LoginTest with hasaki.vn
    private By labelLogin = By.xpath("//form[@id='form-head-LoginTest']//div[contains(.,'Hoặc đăng nhập với Hasaki.vn')]");
    // remember password label
    private By labelRememberPassword = By.xpath("//label[contains(.,'Nhớ mật khẩu')]");
    // you not have account
    private By labelYouNotHaveAccount = By.xpath("//form[@id='form-head-LoginTest']");

    //--- Start popup ---
    public By acceptCookie = By.xpath("//button[@id='acceptCookies']");

    public By getAcceptCookie() {
        return acceptCookie;
    }




    // Constructor need to create at each test in test class
    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.validateHelper  = new ValidateHelper(driver);
    }

    @Step("Verify is logged in")
    public boolean isLoggedIn() {
        return validateHelper.verifyElementIsExist(headerUsername) || validateHelper.verifyElementIsExist(headerUsernameAddress) ;
    }

    @Step("Verify Sign In page title")
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
            if(validateHelper.verifyUrl("hasaki.vn")){
                logTest.info("[PASS] sign in page url match");
                return validateHelper.verifyUrl("hasaki.vn");
            };
        } catch (Exception e) {
            logTest.error("[FAIL] sign in page url doesn't match");
        }
        return false;
    }

    @Step("Verify Email/Phone placeholder")
    public boolean verify_EmailPhone_placeholder() {
        try{
            if(validateHelper.getTextPlaceholder(emailInput,"Nhập email hoặc số điện thoại")){
                logTest.info("[PASS] Email/Phone placeholder match");
            };
            return validateHelper.getTextPlaceholder(emailInput,"Nhập email hoặc số điện thoại");
        } catch (Exception e) {
            logTest.error("[FAIL] Email/Phone placeholder doesn't match");
        }
        return false;
    }

    @Step("Verify password placeholder")
    public boolean verify_Password_placeholder() {
        try{
            if(validateHelper.getTextPlaceholder(passwordInput,"Nhập password")){
                logTest.info("[PASS] Password placeholder match");
            };
            return validateHelper.getTextPlaceholder(passwordInput,"Nhập password");
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

    @Step("Get text error message")
    public String getTextErrorMessage(){
        return validateHelper.getTextElement(errorMessage);
    }

    @Step("Get user header name after login")
    public String getTextHeaderUser(){
        return validateHelper.getTextElement(headerUsername);
    }

    @Step("Create MyAccountPage class for linking by login (Open My Account Page)")
    public MyAccountPage login_user(String username, String password) throws Exception {

        validateHelper.Delay(2000);

        // Check page has asked cookie,
        try{
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

    @Step("Create MyAccountPage class for linking by login and remember password (Open My Account Page)")
    public MyAccountPage login_remember_user(String username, String password) throws Exception {

        validateHelper.Delay(2000);

        // Check page has asked cookie,
        try{
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

    @Step("By pass accept cookie pop-up")
    public void bypass_Cookie() throws Exception {

        validateHelper.Delay(2000);

        // Check page has asked cookie,
        try{
            validateHelper.verifyElementIsExist(acceptCookie);
            validateHelper.clickElement(acceptCookie);
        } catch (Exception e) {
            logTest.error("[FAIL] Cookie banner did not appear");
        }

        validateHelper.action_MovetoElement(popup_login);
        validateHelper.clickElement(popup_login_button);

    }



}
