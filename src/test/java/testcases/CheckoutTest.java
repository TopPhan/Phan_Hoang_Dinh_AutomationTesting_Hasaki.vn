package testcases;

import DataProviders.DataProviders;
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
import pages.*;
import pojoClass.ProductModel;
import pojoClass.SearchModel;

import java.util.List;

@Epic("Web Ecommerce Hasaki.vn")
@Feature("Checkout & Payment Process")
@Owner("Hoàng Đỉnh Automation")
public class CheckoutTest extends multipleThread_baseSetup {
    private ValidateHelper validateHelper;
    private JavascriptExecutor js;
    private String emailXml;
    private String passXml;
    private String browserXml;

    @Parameters({"email","password","browserType"})
    @BeforeMethod(alwaysRun = true)
    public void setupCheckOutPage(@Optional("") String email,
                             @Optional("") String password,
                             @Optional("") String browser) throws Exception {
        //driver = getDriver();
        validateHelper = new ValidateHelper(getDriver());
        js = (JavascriptExecutor) getDriver() ;
        this.emailXml = (email != null && !email.isEmpty()) ? email : PropertiesFile.getPropValue("username");
        this.passXml = (password != null && !password.isEmpty()) ? password : PropertiesFile.getPropValue("password");
        this.browserXml = (browserXml != null && !browserXml.isEmpty()) ? browser : PropertiesFile.getPropValue("browser");
    }

    @Step("Handle Smart Login for Checkout session")
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
    @Story("Checkout UI Integrity")
    @Severity(SeverityLevel.BLOCKER)
    @Description("""
        ### [UI] Checkout Page Integrity & Critical Components
        **Objective:** Verify that the Checkout page is fully rendered with all necessary functional components for payment.
        
        **Test Steps:**
        1. **Smart Authentication:** Reuse current session via `handleSmartLogin`.
        2. **Navigation Flow:** Transition from Homepage -> Cart -> Checkout page.
        3. **Component Validation:** Audit the presence of key UI anchors: Shipping Address, Payment Methods, and the 'Order' button.
        
        **Target:** Ensure the checkout interface is stable and all mandatory elements are enabled for user interaction.
        """)
    public void CheckoutTest_verifyCheckoutUIComponents() throws Exception {

        // Overite testcase name display on Allure report by testcode and description.
        Allure.getLifecycle().updateTestCase(result -> result.setName("TC1: Integrity Check - Checkout Page UI"));

        CustomSoftAssert softAssert = new CustomSoftAssert(getDriver());
        LoginPage loginPage = new LoginPage(getDriver());

        logTest.info("Test case: Verify Checkout page UI on browser: " + browserXml);

        // --- Smart login ---
        handleSmartLogin(loginPage, validateHelper);

        CartPage cartPage = loginPage.quickGoToCart();
        CheckoutPage checkoutPage = cartPage.quickCheckOutProduct();

        softAssert.assertTrue(checkoutPage.verify_CheckoutPage_Url(),"Checkout page URL is not correct");
        softAssert.assertTrue(checkoutPage.verify_CheckoutTitle(),"Checkout page title is not display");
        softAssert.assertTrue(checkoutPage.verify_addressTitle(),"'Địa chỉ nhận hàng' title not display");
        softAssert.assertTrue(checkoutPage.verify_paymentMethodTitle(),"'Hình thức thanh toán' title not display");
        softAssert.assertTrue(checkoutPage.verify_paymentBtnIsEnable(),"'Đặt hàng' button is not enable'");
        softAssert.assertAll();
    }

    @Test(
            groups = {"regression","smoke"}
    )
    @Story("Address Information")
    @Severity(SeverityLevel.CRITICAL)
    @Description("""
        ### [Data-Sync] Profile Address & Checkout Consistency
        **Objective:** Validate that the system correctly pulls and synchronizes the default shipping address from the user profile to the Checkout page.
        
        **Test Steps:**
        1. **Profile Audit:** Access 'My Address' tab to extract the default Name and Address string.
        2. **Data Comparison:** Navigate to Checkout and capture the automatically populated address info.
        3. **Verification:** Perform a string-match comparison between the Profile Data and Checkout Data.
        
        **Target:** Ensure seamless data synchronization across the account management and checkout modules.
        """)
    public void CheckoutTest_verifyAddressSyncFromProfile() throws Exception {

        // Overite testcase name display on Allure report by testcode and description.
        Allure.getLifecycle().updateTestCase(result -> result.setName("TC2: Data Sync - Default Address Validation"));

        CustomSoftAssert softAssert = new CustomSoftAssert(getDriver());
        LoginPage loginPage = new LoginPage(getDriver());

        logTest.info("Test case: Verifying profile address matches checkout address on browser: " + browserXml);

        MyAccountPage myAccountPage = loginPage.login_user(emailXml, passXml);
        MyAddressPage myAddressPage = myAccountPage.goToMyAddressTab();
        String DefaultName = myAddressPage.getFullNameIndex(1).split("-")[0].trim();
        String DefaultPlace = myAddressPage.getFullAddressIndex(1).split("-")[0].trim();

        logTest.info(String.format("Default Name: %s, Default Place: %s", DefaultName, DefaultPlace));
        getDriver().navigate().to(PropertiesFile.getPropValue("url"));
        CartPage cartPage = loginPage.quickGoToCart();
        CheckoutPage checkoutPage = cartPage.quickCheckOutProduct();

        String ActualName = checkoutPage.getNameReceive().split("-")[0].trim();
        String ActualPlace = checkoutPage.getFullAddress().split("-")[0].trim();
        logTest.info(String.format("Actual Name: %s, Actual Place: %s", ActualName, ActualPlace));

        softAssert.assertEquals(ActualName, DefaultName, "Name is not match");
        softAssert.assertEquals(ActualPlace, DefaultPlace, "Place is not match");

        softAssert.assertAll();
    }

    @Test(
            dataProvider = "multiSearchData",
            dataProviderClass = DataProviders.class,
            groups = {"regression"}
    )
    @Story("E2E Data Integrity")
    @Severity(SeverityLevel.CRITICAL)
    @Description("""
        ### [E2E] Item List Migration & Integrity (Cart to Checkout)
        **Objective:** Ensure that all products added to the cart are accurately carried over to the Checkout stage without data loss.
        
        **Test Steps:**
        1. **Data Acquisition:** Dynamically search and add multiple items from Data provider to the cart.
        2. **Cart Snapshot:** Extract a detailed list of products (Name, SKU, Price) using the `ProductModel` POJO.
        3. **Deep Comparison:** Transition to Checkout and perform a 1-on-1 comparison between the Cart List and Checkout List.
        
        **Target:** Guarantee 100% data integrity for product metadata throughout the purchase journey.
        """)
    public void CheckoutTest_verifyItemListIntegrityAfterCheckout(String[][] items) throws Exception {

        // Overite testcase name display on Allure report by testcode and description.
        Allure.getLifecycle().updateTestCase(result -> result.setName("TC3: E2E Flow - Item List Integrity (Cart -> Checkout)"));
        CustomSoftAssert softAssert = new CustomSoftAssert(getDriver());
        LoginPage loginPage = new LoginPage(getDriver());

        logTest.info("Test case: Validating item list migration for" + items.length +" items."+ browserXml);

        // --- Smart login ---
        handleSmartLogin(loginPage, validateHelper);

        SearchPage searchPage = new SearchPage(getDriver());
        ProductDetailPage productDetailPage = new ProductDetailPage(getDriver());
        // ---- Add list product to cart ----
        for (String[] item : items) {
            String keyword = item[0];
            String brand = item[1];
            int quantity = Integer.parseInt(item[2]);

            productDetailPage = searchPage.searchAndReturnFirstProduct(keyword + " " + brand);
            Assert.assertTrue(productDetailPage.isAddCartEnable(), "Add to cart button is not enable");

            // Add single product to cart
            productDetailPage.addProductToCart();
            productDetailPage.closeSuccessPopup();
            getDriver().navigate().to(PropertiesFile.getPropValue("url"));
        }

        CartPage cartPage = loginPage.quickGoToCart();
        List<ProductModel> cartList = cartPage.getDetailedListProductInCart();
        CheckoutPage checkoutPage = cartPage.quickCheckOutProduct();
        List<ProductModel> checkoutList = checkoutPage.getDetailedListProductInCheckout();

        // ---- Compare list product in cart and checkout ----
        checkoutPage.compareProductLists(cartList, checkoutList);

    }

    @Test(
            dataProvider = "multiSearchData",
            dataProviderClass = DataProviders.class,
            groups = {"regression"}
    )
    @Story("E2E Price Integrity")
    @Severity(SeverityLevel.BLOCKER)
    @Description("""
        ### [E2E] Financial Synchronization (Price Integrity)
        **Objective:** Verify that the final total price calculated in the Cart matches exactly with the value on the Checkout page.
        
        **Test Steps:**
        1. **Bulk Purchase:** Add a variety of items to the cart to create a complex pricing scenario.
        2. **Cart Pricing:** Capture the `totalPriceInCart` as the source of truth.
        3. **Checkout Verification:** Compare the source price against the `totalPriceInCheckout` after the page transition.
        
        **Target:** Prevent any pricing discrepancies errors during the final steps of the flow.
        """)
    public void CheckoutTest_verifyGrandTotalMatchWithCart(String[][] items) throws Exception {

        // Overite testcase name display on Allure report by testcode and description.
        Allure.getLifecycle().updateTestCase(result -> result.setName("TC4: Verify E2E Flow - Shopping Cart & Checkout Total Price"));
        CustomSoftAssert softAssert = new CustomSoftAssert(getDriver());
        LoginPage loginPage = new LoginPage(getDriver());

        logTest.info("Test case: Validating total price between Cart and Checkout. on browser" + browserXml);

        // --- Smart login ---
        handleSmartLogin(loginPage, validateHelper);

        SearchPage searchPage = new SearchPage(getDriver());
        ProductDetailPage productDetailPage = new ProductDetailPage(getDriver());
        // ---- Add list product to cart ----
        for (String[] item : items) {
            String keyword = item[0];
            String brand = item[1];
            int quantity = Integer.parseInt(item[2]);

            productDetailPage = searchPage.searchAndReturnFirstProduct(keyword + " " + brand);
            Assert.assertTrue(productDetailPage.isAddCartEnable(), "Add to cart button is not enable");

            // Add single product to cart
            productDetailPage.addProductToCart();
            productDetailPage.closeSuccessPopup();
            getDriver().navigate().to(PropertiesFile.getPropValue("url"));
        }

        CartPage cartPage = loginPage.quickGoToCart();
        long totalPriceInCart = cartPage.getTotalPriceAllItemsInCart();
        CheckoutPage checkoutPage = cartPage.quickCheckOutProduct();
        long totalPriceInCheckout = checkoutPage.getTotalPrice();
        logTest.info(String.format("Comparison - Cart: %d, Checkout: %d", totalPriceInCart, totalPriceInCheckout));
        Assert.assertEquals(totalPriceInCart, totalPriceInCheckout, "Total price synchronization failed!");
        logTest.info("[PASS] Total price is synchronization between Cart and Checkout page.");
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
