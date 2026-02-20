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
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import pages.*;
import pojoClass.ProductModel;

import java.util.List;

@Epic("Web Ecommerce Hasaki.vn")
@Feature("End-to-End Purchase Flow")
@Owner("Hoàng Đỉnh Automation")
public class PurchaseEndToEndTest extends multipleThread_baseSetup {
    private ValidateHelper validateHelper;
    private JavascriptExecutor js;
    private String emailXml;
    private String passXml;
    private String browserXml;

    @Parameters({"email","password","browserType"})
    @BeforeMethod(alwaysRun = true)
    public void setupEndToEnd(@Optional("") String email,
                                  @Optional("") String password,
                                  @Optional("") String browser) throws Exception {
        validateHelper = new ValidateHelper(getDriver());
        js = (JavascriptExecutor) getDriver() ;
        this.emailXml = (email != null && !email.isEmpty()) ? email : PropertiesFile.getPropValue("username");
        this.passXml = (password != null && !password.isEmpty()) ? password : PropertiesFile.getPropValue("password");
        this.browserXml = (browserXml != null && !browserXml.isEmpty()) ? browser : PropertiesFile.getPropValue("browser");
    }

    @Test(
            dataProvider = "multiSearchData",
            dataProviderClass = DataProviders.class,
            groups = {"regression"}
    )
    @Story("Complete User Purchase Flow")
    @Severity(SeverityLevel.CRITICAL)
    @Description("""
        ### [E2E] Full Purchase Flow & Data Integrity Check
        **Objective:** Validate the entire e-commerce flow from product search to the final checkout stage, ensuring no data loss or price mismatches occur during transitions.
        
        **Test Workflow:**
        1. **Session Management:** Execute Smart Login to maintain authentication throughout the flow.
        2. **Product Acquisition:** Dynamically search and add multiple products into the shopping cart.
        3. **Cart Validation:** Perform an audit on subtotals and verify data mapping into Product Models (POJOs).
        4. **Checkout Integration:** Transfer all cart data to the Checkout page.
        5. **Final Integrity Check:** Execute a **Deep Comparison** between the Cart data and Checkout data (Matching Name, Price, and SKU).
        
        **Business Goal:** Ensure a seamless and error-free, smoothly process for the customer.
        """)
    public void endToEndPurchaseFlow(String[][] items) throws Exception {

        // Overite testcase name display on Allure report by testcode and description.
        // Dynamic test case name update for Allure
        Allure.getLifecycle().updateTestCase(result -> result.setName("E2E Purchase Flow: Integrity Check (" + browserXml + ")"));

        LoginPage loginPage = new LoginPage(getDriver());
        SearchPage searchPage = new SearchPage(getDriver());

        logTest.info("E2E Purchase Flow on browser: " + browserXml);

        // --- STEP 1: SMART LOGIN ---
        executeSmartLogin(loginPage);

        // --- STEP 2: SEARCH & ADD MULTIPLE ITEMS ---
        addProductsToCart(searchPage, items);

        // --- STEP 3: CART VERIFICATION ---
        CartPage cartPage = loginPage.quickGoToCart();
        List<ProductModel> cartList = verifyCartCalculations(cartPage);

        // --- STEP 4: CHECKOUT & COMPARE ---
        verifyCheckoutDataIntegrity(cartPage, cartList);
    }

    @Step("[E2E Stage 1] Establish authenticated user session")
    private void executeSmartLogin(LoginPage loginPage) throws Exception {
        validateHelper.clickElement(loginPage.getAcceptCookie());
        if (!loginPage.isLoggedIn()) {
            loginPage.login_user(emailXml, passXml);
            logTest.info("New login session established.");
        } else {
            logTest.info("Existing session detected. Skipping login.");
        }
        Assert.assertTrue(loginPage.isLoggedIn(), "Login failed! Terminalizing test.");
    }

    @Step("[E2E Stage 2] Add shopping cart with multiple products")
    private void addProductsToCart(SearchPage searchPage, String[][] items) throws Exception {
        for (String[] item : items) {
            String fullKeyword = item[0] + " " + item[1];
            logTest.info("Searching and adding item: " + fullKeyword);

            ProductDetailPage detailPage = searchPage.searchAndReturnFirstProduct(fullKeyword);
            Assert.assertTrue(detailPage.isAddCartEnable(), "Add to Cart button is NOT enabled for: " + fullKeyword);

            detailPage.addProductToCart();
            detailPage.closeSuccessPopup();
            getDriver().navigate().to(PropertiesFile.getPropValue("url"));
        }
    }

    @Step("[E2E Stage 3] Audit cart calculations and capture product metadata")
    private List<ProductModel> verifyCartCalculations(CartPage cartPage) throws InterruptedException {
        Assert.assertTrue(cartPage.verifyCalculationPriceAllItemInCart(),
                "Cart grand total calculation mismatch!");
        logTest.info("Cart calculations verified successfully.");
        return cartPage.getDetailedListProductInCart();
    }

    @Step("[E2E Stage 4] Finalize checkout and verify data synchronization")
    private void verifyCheckoutDataIntegrity(CartPage cartPage, List<ProductModel> expectedList) {
        CheckoutPage checkoutPage = cartPage.quickCheckOutProduct();
        List<ProductModel> actualList = checkoutPage.getDetailedListProductInCheckout();

        // Deep compare two product lists (using POJO ProductModel)
        checkoutPage.compareProductLists(expectedList, actualList);

        // Final check for payment button
        Assert.assertTrue(checkoutPage.verify_paymentBtnIsEnable(), "Payment button is disabled on Checkout page!");
        logTest.info("Data Integrity Check: Cart and Checkout lists are perfectly matched.");
    }

}
