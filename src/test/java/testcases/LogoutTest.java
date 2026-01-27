package testcases;

import com.bases.multipleThread_baseSetup;
import com.log.logTest;
import com.utility.CustomSoftAssert;
import com.utility.Helpers.ValidateHelper;
import com.utility.PropertiesFile;
import org.openqa.selenium.JavascriptExecutor;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.LoginPage;
import pages.MyAccountPage;

public class LogoutTest extends multipleThread_baseSetup {

    private ValidateHelper validateHelper;
    private JavascriptExecutor js;

    @BeforeMethod
    public void createHelper() {
        validateHelper = new ValidateHelper(getDriver());
        js = (JavascriptExecutor) getDriver();
    }

    @Test(priority = 0)
    public void Logout_testFunctionality() throws Exception {

        LoginPage loginPage = new LoginPage(getDriver());
        logTest.info("--- Test log-out flow ---");
        logTest.info("Email: "+ PropertiesFile.getPropValue("username"));
        logTest.info("Password: "+ PropertiesFile.getPropValue("password"));

        MyAccountPage myAccountPage = loginPage.login_user(PropertiesFile.getPropValue("username"),PropertiesFile.getPropValue("password"));
        myAccountPage.LogOut();
        Assert.assertTrue(myAccountPage.verify_LogoutSuccess());

    }

}
