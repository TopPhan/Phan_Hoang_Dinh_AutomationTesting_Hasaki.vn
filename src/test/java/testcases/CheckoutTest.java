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
            priority = 0,
            groups = {"smoke", "regression"}
    )
    @Story("Checkout UI Integrity")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Verify all primary UI elements on Checkout page are visible and enabled.")
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
            priority = 1,
            groups = {"regression","smoke"}
    )
    @Story("Address Information")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that the checkout page correctly pulls the default address from the user profile.")
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
        getDriver().navigate().to("https://hasaki.vn/");
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
            priority = 2,
            groups = {"regression"}
    )
    @Story("E2E Data Integrity")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Ensure all products in the cart are correctly carried over to the Checkout page.")
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
            getDriver().get("https://hasaki.vn/");
        }

        CartPage cartPage = loginPage.quickGoToCart();
        List<ProductModel> cartList = cartPage.getDetailedListProductInCart();
        CheckoutPage checkoutPage = cartPage.quickCheckOutProduct();
        List<ProductModel> checkoutList = checkoutPage.getDetailedListProductInCheckout();

        checkoutPage.compareProductLists(cartList, checkoutList);
        logTest.info("[PASS] Product lists are identical.");
    }

    @Test(
            dataProvider = "multiSearchData",
            dataProviderClass = DataProviders.class,
            priority = 3,
            groups = {"regression"}
    )
    @Story("E2E Price Integrity")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Verify that the total price calculated in the cart matches the total price on the Checkout page.")
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
            getDriver().get("https://hasaki.vn/");
        }

        CartPage cartPage = loginPage.quickGoToCart();
        long totalPriceInCart = cartPage.getTotalPriceAllItemsInCart();
        CheckoutPage checkoutPage = cartPage.quickCheckOutProduct();
        long totalPriceInCheckout = checkoutPage.getTotalPrice();
        logTest.info(String.format("Comparison - Cart: %d, Checkout: %d", totalPriceInCart, totalPriceInCheckout));
        Assert.assertEquals(totalPriceInCart, totalPriceInCheckout, "Total price synchronization failed!");
        logTest.info("[PASS] Total price is synchronization between Cart and Checkout page.");
    }

}
