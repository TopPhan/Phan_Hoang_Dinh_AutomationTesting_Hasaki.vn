package testcases;

import com.bases.multipleThread_baseSetup;
import com.log.logTest;
import com.utility.CustomSoftAssert;
import com.utility.Helpers.ValidateHelper;
import com.utility.PropertiesFile;
import io.qameta.allure.Allure;
import org.openqa.selenium.JavascriptExecutor;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import pages.CartPage;
import pages.LoginPage;
import pages.ProductDetailPage;


public class CartTest extends multipleThread_baseSetup {
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
        //driver = getDriver();
        validateHelper = new ValidateHelper(getDriver());
        js = (JavascriptExecutor) getDriver() ;
        this.emailXml = (email != null && !email.isEmpty()) ? email : PropertiesFile.getPropValue("username");
        this.passXml = (password != null && !password.isEmpty()) ? password : PropertiesFile.getPropValue("password");
        this.browserXml = (browserXml != null && !browserXml.isEmpty()) ? browser : PropertiesFile.getPropValue("browser");
    }

    @Test
    public void CartTest_VerifyUI() throws Exception {

        // Overite testcase name display on Allure report by testcode and description.
        Allure.getLifecycle().updateTestCase(result -> result.setName("TC1: Quick verify Cart page UI"));

        CustomSoftAssert softAssert = new CustomSoftAssert(getDriver());
        LoginPage loginPage = new LoginPage(getDriver());
        validateHelper.clickElement(loginPage.getAcceptCookie());

        logTest.info("Test case: Verify Cart page UI on browser: " + browserXml);

        // --- SMART LOGIN LOGIC ---
        if (!loginPage.isLoggedIn()) {
            logTest.info("Session not available, proceed Login for: " + emailXml);
            loginPage.login_user(emailXml, passXml);
        } else {
            logTest.info("(Session reused), skip logged in step.");
        }
        // -------------------------

        CartPage cartPage = loginPage.quickGoToCart();

        softAssert.assertTrue(cartPage.verify_ProductPage_Url(),"Cart page URL is not correct");
        softAssert.assertTrue(cartPage.verify_cartTitle(),"Cart page title is not display");
        softAssert.assertAll();
        cartPage.verifyCalculationPriceAllItemInCart();
    }


}



