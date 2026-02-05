package testcases;

import com.bases.multipleThread_baseSetup;
import com.log.logTest;
import com.utility.CustomSoftAssert;
import com.utility.Helpers.ValidateHelper;
import com.utility.PropertiesFile;
import io.qameta.allure.*;
import org.openqa.selenium.JavascriptExecutor;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import pages.LoginPage;
import pages.MyAccountPage;

@Epic("Web Ecommerce")
@Feature("Logout Functionality")
@Owner("Hoàng Đỉnh Automation")
public class LogoutTest extends multipleThread_baseSetup {

    private ValidateHelper validateHelper;
    private JavascriptExecutor js;
    private String emailXml;
    private String passXml;
    private String browserXml;

    @Parameters({"email", "password","browserType"})
    @BeforeMethod
    public void setLoginPage(@Optional("") String email,
                             @Optional("") String password,
                             @Optional("") String browser) throws Exception {
        validateHelper = new ValidateHelper(getDriver());
        js = (JavascriptExecutor) getDriver() ;
        this.emailXml = (email != null && !email.isEmpty()) ? email : PropertiesFile.getPropValue("username");
        this.passXml = (password != null && !password.isEmpty()) ? password : PropertiesFile.getPropValue("password");
        this.browserXml = (browserXml != null && !browserXml.isEmpty()) ? browser : PropertiesFile.getPropValue("browser");
    }

    @Test(
            priority = 0,
            groups = {"smoke", "regression"}
    )
    @Feature("Logout")
    @Story("Log out from system")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that a logged-in user can log out successfully.")
    public void logout_verifyUserCanLogoutSuccessfully() throws Exception {

        LoginPage loginPage = new LoginPage(getDriver());
        Allure.getLifecycle().updateTestCase(result -> result.setName("Logout Test: Verify session termination for " + emailXml));

        logTest.info("--- Test log-out flow ---");
        logTest.info("Email: " + emailXml);
        //logTest.info("Password: "+ passXml);

        MyAccountPage myAccountPage = loginPage.login_user(emailXml, passXml);
        myAccountPage.LogOut();
        Assert.assertTrue(myAccountPage.verify_LogoutSuccess());

        Allure.addAttachment("Logout Status", "Account " + emailXml + " logged out successfully.");
    }

}
