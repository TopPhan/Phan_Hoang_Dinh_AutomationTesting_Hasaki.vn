package testcases;

import com.bases.multipleThread_baseSetup;
import com.log.logTest;
import com.utility.CustomSoftAssert;
import com.utility.Helpers.ValidateHelper;
import io.qameta.allure.*;
import org.openqa.selenium.JavascriptExecutor;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.SkipException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.LoginPage;
import pages.MyAccountPage;
import DataProviders.DataProviders;
import pojoClass.LoginModel;

@Epic("Web Ecommerce")
@Feature("Login Functionality")
@Owner("Hoàng Đỉnh Automation")
public class LoginTest extends multipleThread_baseSetup {

    @BeforeMethod
    public void createHelper() {
        ValidateHelper validateHelper = new ValidateHelper(getDriver());
        JavascriptExecutor js = (JavascriptExecutor) getDriver();
    }

    @Test(
            groups = {"regression", "data_driven"},
            dataProvider = "dataLogin",
            dataProviderClass = DataProviders.class,
            priority = 0
    )
    @Feature("Login")
    @Story("Login Data Driven")
    @Owner("Hoàng Đỉnh Automation")
    @Description("Comprehensive Authentication Test Suite: \n" +
            "1. Valid credentials (TC1)\n" +
            "2. Empty fields handling (TC2, TC3, TC4)\n" +
            "3. Invalid email formats (TC5)\n" +
            "4. Boundary testing with spaces (TC6)\n" +
            "5. Security check with wrong passwords (TC7)")
    @Severity(SeverityLevel.BLOCKER)
    public void login_verifyMultipleAuthenticationScenarios(LoginModel loginModel) throws Exception {

        // Checking execute column ( Y/N )
        if (loginModel.getExecute().equalsIgnoreCase("N")) {
            // throw new SkipException is testNG method to skip the test
            throw new SkipException("Skip test case: " + loginModel.getTestcode());
        }
        // Overite testcase name display on Allure report by testcode and description.
        Allure.getLifecycle().updateTestCase(result -> result.setName(loginModel.getTestcode() + ": " + loginModel.getDescriptions()));

        // Output each data row in the console
        logTest.info("Test case: " +loginModel.getTestcode()+" description: " + loginModel.getDescriptions());
        logTest.info("Username: " + loginModel.getEmail());
        logTest.info("Password: " + loginModel.getPassword());
        Allure.addAttachment("Input Data", "Executing scenario: " + loginModel.getDescriptions() +
                "\nEmail: " + loginModel.getEmail());

        LoginPage loginPage = new LoginPage(getDriver());
        CustomSoftAssert softAssert = new CustomSoftAssert(getDriver());

        Assert.assertTrue(loginPage.verify_SignInPage_Title(), "Sign In page title doesn't match");
        Assert.assertTrue(loginPage.verify_SignInPage_Url(),"Sign In page url doesn't match");

        MyAccountPage myAccountPage = loginPage.login_user(loginModel.getEmail(),loginModel.getPassword());

        // 3. Switch Case for each TestCode
        switch (loginModel.getTestcode().trim().toUpperCase()) {
            case "TC1": // LoginTest success by default
                softAssert.assertEquals(loginPage.getTextHeaderUser(), "HoangDinh");
                break;
            case "TC2": // Leave the email, password blank
                softAssert.assertEquals(loginPage.getTextErrorMessage(), "Vui lòng nhập tên đăng nhập");
                break;
            case "TC3": // Enter valid email, leave password blank
                softAssert.assertEquals(loginPage.getTextErrorMessage(), "Vui lòng nhập mật khẩu");
                break;
            case "TC4": // Leave the email blank, enter valid password
                softAssert.assertEquals(loginPage.getTextErrorMessage(), "Vui lòng nhập tên đăng nhập");
                break;
            case "TC5": // Enter invalid data without symbol "@" in email
                softAssert.assertEquals(loginPage.getTextErrorMessage(), "Tên đăng nhập hoặc mật khẩu không khớp !");
                break;
            case "TC6": // Enter valid data in email with a space " " at the end.
                softAssert.assertEquals(loginPage.getTextErrorMessage(), "Tên đăng nhập hoặc mật khẩu không khớp !");
                break;
            case "TC7": // Enter valid email, invalid password
                softAssert.assertEquals(loginPage.getTextErrorMessage(), "Tên đăng nhập hoặc mật khẩu không khớp !");
                break;
            default:
                logTest.warn("Undefine testcode: " + loginModel.getTestcode());
        }
        softAssert.assertAll();
    }

    @Test(
            groups = {"smoke", "gui"},
            priority = 1
    )
    @Feature("Login")
    @Story("Login Page UI")
    @Owner("Hoàng Đỉnh Automation")
    @Description("Verify UI elements like placeholders, labels and cookies on Login Page")
    @Severity(SeverityLevel.NORMAL)
    public void login_verifyUILayoutAndPlaceholders() throws Exception {

        // Overite testcase name display on Allure report by testcode and description.
        Allure.getLifecycle().updateTestCase(result -> result.setName("Verify LoginTest UI"));

        LoginPage loginPage = new LoginPage(getDriver());
        CustomSoftAssert softAssert = new CustomSoftAssert(getDriver());

        loginPage.bypass_Cookie();

        // Verify placeholder
        logTest.info("Verify Email/phone placeholder");
        softAssert.assertTrue(loginPage.verify_EmailPhone_placeholder("Nhập email hoặc số điện thoại"),"Email/Phone placeholder is not correct");
        logTest.info("Verify password placeholder");
        softAssert.assertTrue(loginPage.verify_Password_placeholder("Nhập password"),"Password placeholder is not correct");

        // Verify all main label is display correctly
        logTest.info("Verify all main label is display correctly");
        softAssert.assertTrue(loginPage.verify_Label());

        // do all softAssert
        softAssert.assertAll();
    }

    @AfterMethod
    public void tearDown(ITestResult result){
        try {
            logTest.info("Cleaning up after row: " + result.getName());
            getDriver().manage().deleteAllCookies();
            if (!result.isSuccess()) {
                ((org.openqa.selenium.JavascriptExecutor) getDriver()).executeScript("window.stop();");
            }
            getDriver().navigate().to("https://hasaki.vn/");
        } catch (Exception e) {
            logTest.error("Error while cleaning up after row: " + result.getName());
        }
    }
}