package testcases;

import com.bases.multipleThread_baseSetup;
import com.log.logTest;
import com.utility.CustomSoftAssert;
import com.utility.Helpers.ValidateHelper;
import com.utility.PropertiesFile;
import io.qameta.allure.Allure;
import org.openqa.selenium.JavascriptExecutor;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import pages.CartPage;
import pages.LoginPage;

public class CartTest extends multipleThread_baseSetup {
    private ValidateHelper validateHelper;
    private String emailXml;
    private String passXml;
    private String browserXml;

    @Parameters({"email", "password","browserType"})
    @BeforeMethod
    public void setLoginPage(@Optional("") String email,
                             @Optional("") String password,
                             @Optional("") String browser) throws Exception {

        validateHelper = new ValidateHelper(getDriver());
        JavascriptExecutor js = (JavascriptExecutor) getDriver();
        this.emailXml = (email != null && !email.isEmpty()) ? email : PropertiesFile.getPropValue("username");
        this.passXml = (password != null && !password.isEmpty()) ? password : PropertiesFile.getPropValue("password");
        this.browserXml = (browserXml != null && !browserXml.isEmpty()) ? browser : PropertiesFile.getPropValue("browser");
    }

    @Test(priority = 0)
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
    }

    @Test(priority = 1)
    public void CartTest_VerifyDeleteAllCart() throws Exception {

        // Overite testcase name display on Allure report by testcode and description.
        Allure.getLifecycle().updateTestCase(result -> result.setName("TC2: Verify delete all items in cart"));

        CustomSoftAssert softAssert = new CustomSoftAssert(getDriver());
        LoginPage loginPage = new LoginPage(getDriver());
        validateHelper.clickElement(loginPage.getAcceptCookie());

        logTest.info("Test case: Verify delete all items in cart on browser: " + browserXml);

        // --- SMART LOGIN LOGIC ---
        if (!loginPage.isLoggedIn()) {
            logTest.info("Session not available, proceed Login for: " + emailXml);
            loginPage.login_user(emailXml, passXml);
        } else {
            logTest.info("(Session reused), skip logged in step.");
        }
        // -------------------------

        CartPage cartPage = loginPage.quickGoToCart();

        cartPage.deleteAllItemInCart();
        Assert.assertTrue(cartPage.verifyCartIsEmpty(),"Cart is not empty");
        logTest.info("[PASS] Cart is empty");
    }

    @Test(priority = 2)
    public void CartTest_VerifyCalculationItemCart() throws Exception {

        // Overite testcase name display on Allure report by testcode and description.
        Allure.getLifecycle().updateTestCase(result -> result.setName("TC3: Verify calculation subtotal and pre-total price all items in cart"));

        CustomSoftAssert softAssert = new CustomSoftAssert(getDriver());
        LoginPage loginPage = new LoginPage(getDriver());
        validateHelper.clickElement(loginPage.getAcceptCookie());

        logTest.info("Test case: Verify calculation subtotal and pre-total price all items in cart on browser: " + browserXml);

        // --- SMART LOGIN LOGIC ---
        if (!loginPage.isLoggedIn()) {
            logTest.info("Session not available, proceed Login for: " + emailXml);
            loginPage.login_user(emailXml, passXml);
        } else {
            logTest.info("(Session reused), skip logged in step.");
        }
        // -------------------------

        CartPage cartPage = loginPage.quickGoToCart();

        Assert.assertTrue(cartPage.verifyCalculationPriceAllItemInCart(),"Calculation item is not correct");
        logTest.info("[PASS] Calculation subtotal price each items is correct");
        Assert.assertEquals(cartPage.getPreCalculatePriceAllItemInCart(),cartPage.getTotalPriceAllItemsInCart(),"Total price is not correct");
        logTest.info("[PASS] Total price all items is correct");
    }

    @Test(priority = 3)
    public void CartTest_QuantityUpdateItemCart() throws Exception {

        // Overite testcase name display on Allure report by testcode and description.
        Allure.getLifecycle().updateTestCase(result -> result.setName("TC4: Verify quantity update 1 items in cart"));

        CustomSoftAssert softAssert = new CustomSoftAssert(getDriver());
        LoginPage loginPage = new LoginPage(getDriver());
        validateHelper.clickElement(loginPage.getAcceptCookie());

        logTest.info("Test case: Verify quantity update 1 items in cart on browser: " + browserXml);

        // --- SMART LOGIN LOGIC ---
        if (!loginPage.isLoggedIn()) {
            logTest.info("Session not available, proceed Login for: " + emailXml);
            loginPage.login_user(emailXml, passXml);
        } else {
            logTest.info("(Session reused), skip logged in step.");
        }
        // -------------------------

        CartPage cartPage = loginPage.quickGoToCart();

        cartPage.verifyIncreaseQuantityOfItemInCart();
        Assert.assertTrue(cartPage.verifyCalculationPriceAllItemInCart(),"Calculation item is not correct");
        logTest.info("[PASS] Calculation subtotal price items is correct");

    }


}



