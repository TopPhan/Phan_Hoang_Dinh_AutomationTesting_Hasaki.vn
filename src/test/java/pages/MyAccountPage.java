package pages;

import com.log.logTest;
import com.utility.CustomSoftAssert;
import com.utility.Helpers.ValidateHelper;
import com.utility.PropertiesFile;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.asserts.SoftAssert;

public class MyAccountPage {
    private WebDriver driver;
    private ValidateHelper validateHelper;
    private CustomSoftAssert softAssert;
    public MyAddressPage myAddressPage;

    // --- Locators ---
    private By myAccountPanel = By.xpath("//div[@class='item_header item_login user-info-group ']");
    private By myaccount = By.xpath("//a[contains(text(),'Tài khoản của bạn')]");
    private By myWishlist = By.xpath("//a[contains(text(),'Sản phẩm yêu thích')]");
    private By myAddress = By.xpath("//a[contains(text(),'Địa chỉ giao hàng')]");
    private By logoutBtn = By.xpath("//a[normalize-space()='Thoát']");
    private By labelLogin = By.xpath("//a[@class='popup-login txt_header_right']");
    private By labelRegister = By.xpath("//a[@class='popup-register txt_header_right']");


    public MyAccountPage(WebDriver driver) {
        this.driver = driver;
        validateHelper = new ValidateHelper(driver);
        softAssert = new CustomSoftAssert(driver);
    }

    @Step("Perform Logout action from Account menu")
    public void LogOut() throws InterruptedException {
        validateHelper.verifyElementIsDisplay(myAccountPanel);
        validateHelper.action_MovetoElement(myAccountPanel);
        validateHelper.clickElement(logoutBtn);
        //driver.get("https://hasaki.vn/customer/account/logout/");
    }

    @Step("Verify Logout status by checking Login/Register labels")
    public boolean verify_LogoutSuccess() throws InterruptedException {
        logTest.info("--- Checking LoginTest & Register labels after LogoutTest ---");

        boolean isLoginDisplayed = validateHelper.verifyElementIsDisplay(labelLogin);
        boolean isRegisterDisplayed = validateHelper.verifyElementIsDisplay(labelRegister);

        softAssert.assertTrue(isLoginDisplayed, "Label 'Đăng Nhập' not display");
        softAssert.assertTrue(isRegisterDisplayed, "Label 'Đăng Ký' not display");

        if (isLoginDisplayed && isRegisterDisplayed) {
            logTest.info("[PASS] Label 'Đăng Nhập' & 'Đăng Ký' is display. Log out success");
        } else {
            logTest.error("[FAIL] Label 'Đăng Nhập' & 'Đăng Ký' is not display. Log out fail");
        }
        softAssert.assertAll();
        return isLoginDisplayed && isRegisterDisplayed;
    }

    @Step("Navigate to 'My Address' tab - Linking MyAddressPage")
    public MyAddressPage goToMyAddressTab() {
        try {
            validateHelper.action_MovetoElement(myAccountPanel);
            validateHelper.clickElement(myAddress);
            return new MyAddressPage(driver);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
