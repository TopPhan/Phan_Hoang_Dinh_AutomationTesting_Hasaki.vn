package testcases;

import com.bases.multipleThread_baseSetup;
import com.log.logTest;
import com.utility.CustomSoftAssert;
import com.utility.Helpers.ValidateHelper;
import com.utility.PropertiesFile;
import io.qameta.allure.*;
import org.openqa.selenium.JavascriptExecutor;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.*;
import pages.CartPage;
import pages.LoginPage;

@Epic("Web Ecommerce Hasaki.vn")
@Feature("Cart Management Functionality")
@Owner("Hoàng Đỉnh Automation")
public class CartTest extends multipleThread_baseSetup {
    private ValidateHelper validateHelper;
    private String emailXml;
    private String passXml;
    private String browserXml;

    @Parameters({"email", "password","browserType"})
    @BeforeMethod(alwaysRun = true)
    public void setLoginPage(@Optional("") String email,
                             @Optional("") String password,
                             @Optional("") String browser) throws Exception {

        validateHelper = new ValidateHelper(getDriver());
        JavascriptExecutor js = (JavascriptExecutor) getDriver();
        this.emailXml = (email != null && !email.isEmpty()) ? email : PropertiesFile.getPropValue("username");
        this.passXml = (password != null && !password.isEmpty()) ? password : PropertiesFile.getPropValue("password");
        this.browserXml = (browserXml != null && !browserXml.isEmpty()) ? browser : PropertiesFile.getPropValue("browser");
    }

    @Step("Handle Smart Login for Cart Test")
    private void handleSmartLogin(LoginPage loginPage, ValidateHelper validateHelper) throws Exception {
        validateHelper.clickElement(loginPage.getAcceptCookie());
        if (!loginPage.isLoggedIn()) {
            loginPage.login_user(emailXml, passXml);
        } else {
            logTest.info("(Session reused), skip login.");
        }
    }

    @Test(
            groups = {"smoke", "regression"}
    )
    @Story("Cart UI & Navigation")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that the Cart page loads correctly with valid URL and Title.")
    public void CartTest_VerifyUI() throws Exception {

        // Overite testcase name display on Allure report by testcode and description.
        Allure.getLifecycle().updateTestCase(result -> result.setName("TC1: Check cart page UI"));

        CustomSoftAssert softAssert = new CustomSoftAssert(getDriver());
        LoginPage loginPage = new LoginPage(getDriver());

        logTest.info("Test case: Verify Cart page UI on browser: " + browserXml);

        // --- Smart login ---
        handleSmartLogin(loginPage, validateHelper);

        CartPage cartPage = loginPage.quickGoToCart();

        softAssert.assertTrue(cartPage.verify_ProductPage_Url(),"Target URL mismatch!");
        softAssert.assertTrue(cartPage.verify_cartTitle(),"Cart title not found!");
        softAssert.assertAll();
    }

    @Test(
            groups = {"smoke","regression"}
    )
    @Story("Price Calculation Logic")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Verify that subtotal for each item and total grand price are calculated correctly.")
    public void CartTest_CalculationCheck() throws Exception {

        // Overite testcase name display on Allure report by testcode and description.
        Allure.getLifecycle().updateTestCase(result -> result.setName("TC2: Logic Check - Pricing Calculation"));

        CustomSoftAssert softAssert = new CustomSoftAssert(getDriver());
        LoginPage loginPage = new LoginPage(getDriver());

        logTest.info("Validating pricing calculated on: " + browserXml);

        // --- Smart login ---
        handleSmartLogin(loginPage, validateHelper);

        CartPage cartPage = loginPage.quickGoToCart();

        Assert.assertTrue(cartPage.verifyCalculationPriceAllItemInCart(),"Subtotal calculation error!");
        logTest.info("[PASS] Subtotal each item matches expectations.");
        Assert.assertEquals(cartPage.getPreCalculatePriceAllItemInCart(),cartPage.getTotalPriceAllItemsInCart(),"Grand total Price mismatch!");
        logTest.info("[PASS] Grand total is correct.");
    }

    @Test(
            groups = {"regression"}
    )
    @Story("Quantity Update Functionality")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that increasing an item's quantity updates the price correctly.")
    public void CartTest_UpdateQuantity() throws Exception {

        // Overite testcase name display on Allure report by testcode and description.
        Allure.getLifecycle().updateTestCase(result -> result.setName("TC3: Functional - Quantity Update & Recalculate"));

        CustomSoftAssert softAssert = new CustomSoftAssert(getDriver());
        LoginPage loginPage = new LoginPage(getDriver());

        logTest.info("Testing Quantity Update on: " + browserXml);

        // --- Smart login ---
        handleSmartLogin(loginPage, validateHelper);

        CartPage cartPage = loginPage.quickGoToCart();

        // --- Increase quantity of item in cart ---
        cartPage.verifyIncreaseQuantityOfItemInCart(3);

        Assert.assertTrue(cartPage.verifyCalculationPriceAllItemInCart(),"Price not updated after qty change!");
        logTest.info("[PASS] Quantity and Price synchronized.");

    }

    @Test(
            groups = {"cleanup"}
    )
    @Story("Clear Cart Functionality")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify the ability to delete all items from the cart and confirm it's empty.")
    public void CartTest_DeleteAllItems() throws Exception {

        // Overite testcase name display on Allure report by testcode and description.
        Allure.getLifecycle().updateTestCase(result -> result.setName("Clear All Items from Cart"));

        CustomSoftAssert softAssert = new CustomSoftAssert(getDriver());
        LoginPage loginPage = new LoginPage(getDriver());

        logTest.info("Executing 'Delete All' on on browser: " + browserXml);

        // --- Smart login ---
        handleSmartLogin(loginPage, validateHelper);

        CartPage cartPage = loginPage.quickGoToCart();

        cartPage.deleteAllItemInCart();
        Assert.assertTrue(cartPage.verifyCartIsEmpty(),"Cart still has items after deletion!");
        logTest.info("[PASS] Cart is empty as expected.");
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result){
        try {
            logTest.info("Finished row: " + result.getName());
            if (!result.isSuccess()) {
                logTest.error("Test failed, clearing cookies and stopping window...");
                //getDriver().manage().deleteAllCookies();
                ((org.openqa.selenium.JavascriptExecutor) getDriver()).executeScript("window.stop();");
            }
            getDriver().navigate().to(PropertiesFile.getPropValue("url"));
        } catch (Exception e) {
            logTest.error("Error while cleaning up after row: " + result.getName());
        }
    }




}



