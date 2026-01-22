package pages;

import com.log.logTest;
import com.utility.CustomSoftAssert;
import com.utility.Helpers.ValidateHelper;
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

    // My account panel
    private By myAccountPanel = By.xpath("//div[@class='item_header item_login user-info-group ']");
    // My account
    private By myaccount = By.xpath("//a[contains(text(),'Tài khoản của bạn')]");
    // Order manage
    private By orderManage = By.xpath("//a[contains(text(),'Quản lý đơn hàng')]");
    // My wishlist
    private By myWishlist = By.xpath("//a[contains(text(),'Sản phẩm yêu thích')]");
    // My address
    private By myAddress = By.xpath("//a[contains(text(),'Địa chỉ giao hàng')]");
    // Log-out button
    private By logoutBtn = By.xpath("//a[normalize-space()='Thoát']");
    // Label LoginTest
    private By labelLogin = By.xpath("//a[@class='popup-login txt_header_right']");
    // Label Register
    private By labelRegister = By.xpath("//a[@class='popup-register txt_header_right']");



    public MyAccountPage(WebDriver driver) {
        this.driver = driver;
        validateHelper = new ValidateHelper(driver);
        softAssert = new CustomSoftAssert(driver);
    }

    public void verifyUI_MyAccountPanel() {

    }

    @Step("Log out the account")
    public void LogOut() throws InterruptedException {
        validateHelper.verifyElementIsDisplay(myAccountPanel);
        validateHelper.action_MovetoElement(myAccountPanel);
        validateHelper.clickElement(logoutBtn);
    }

    @Step("Verify logout is successfully")
    public boolean verify_LogoutSuccess() {
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

        return isLoginDisplayed && isRegisterDisplayed;
    }

    @Step("Create MyAddressPage class for linking page ")
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
