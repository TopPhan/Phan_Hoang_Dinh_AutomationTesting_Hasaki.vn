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
    public void setupCartTestPage(@Optional("") String email,
                             @Optional("") String password,
                             @Optional("") String browser) throws Exception {

        validateHelper = new ValidateHelper(getDriver());
        JavascriptExecutor js = (JavascriptExecutor) getDriver();
        this.emailXml = (email != null && !email.isEmpty()) ? email : PropertiesFile.getPropValue("username");
        this.passXml = (password != null && !password.isEmpty()) ? password : PropertiesFile.getPropValue("password");
        this.browserXml = (browserXml != null && !browserXml.isEmpty()) ? browser : PropertiesFile.getPropValue("browser");
    }

    @Step("Handle Smart Login for Cart section")
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
    @Description("""
        ### [UI] Cart Page Accessibility & State Verification
        **Objective:** Verify that the Cart page loads correctly and the Page Object is properly initialized.
        
        **Test Steps:**
        1. **Smart Authentication:** Reuse existing session or login via `handleSmartLogin`.
        2. **Navigation:** Navigate directly to the Cart page via `quickGoToCart()`.
        3. **UI Check:** Verify the page URL and ensure the Cart Title element is displayed.
        
        **Target:** Confirm the cart environment is ready for functional and logic testing.
        """)
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
    @Description("""
        ### [Logic] Calculation Accuracy & Pricing Audit
        **Objective:** Validate that the system calculates subtotals and the grand total without any discrepancies.
        
        **Test Steps:**
        1. **Data Collection:** Scan all items currently present in the cart.
        2. **Subtotal Audit:** Calculate (Price * Quantity) for each product and compare with the system's subtotal.
        3. **Grand Total Check:** Cross-check the sum of subtotals against the displayed Grand Total Price.
        
        **Target:** Ensure 100% pricing accuracy and catch potential rounding errors.
        """)
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
    @Description("""
        ### [Function] Quantity Update & Price Synchronization
        **Objective:** Verify that increasing item quantities triggers correct price recalculations.
        
        **Test Steps:**
        1. **Action:** Increase the quantity of a specific product in the cart.
        2. **Sync Check:** Wait for the system to process the update and reflect new values.
        3. **Recalculation:** Re-verify the subtotal and grand total based on the updated quantity.
        
        **Target:** Ensure the dynamic cart update logic is synchronized with the pricing engine.
        """)
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
    @Description("""
        ### [Cleanup] Cart empty & Final State Check
        **Objective:** Validate the ability to clear the entire cart and reset the shopping session.
        
        **Test Steps:**
        1. **Action:** Execute the "Delete All" function to remove all items from the cart.
        2. **Status Check:** Verify that no product elements remain in the cart list.
        3. **UI Verification:** Confirm the "Empty Cart" message or placeholder is displayed.
        
        **Target:** Ensure the cart can be fully cleared for a clean test environment.
        """)
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



