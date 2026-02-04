package testcases;

import DataProviders.DataProviders;
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
import pages.*;
import pojoClass.ProductModel;
import pojoClass.SearchModel;

import java.util.List;

public class CheckoutTest extends multipleThread_baseSetup {
    private ValidateHelper validateHelper;
    private JavascriptExecutor js;
    private String emailXml;
    private String passXml;
    private String browserXml;

    @Parameters({"email","password","browserType"})
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

    @Test(priority = 0)
    public void CheckoutTest_VerifyUI() throws Exception {

        // Overite testcase name display on Allure report by testcode and description.
        Allure.getLifecycle().updateTestCase(result -> result.setName("TC1: Quick verify Checkout page UI"));

        CustomSoftAssert softAssert = new CustomSoftAssert(getDriver());
        LoginPage loginPage = new LoginPage(getDriver());
        validateHelper.clickElement(loginPage.getAcceptCookie());

        logTest.info("Test case: Verify Checkout page UI on browser: " + browserXml);

        // --- SMART LOGIN LOGIC ---
        if (!loginPage.isLoggedIn()) {
            logTest.info("Session not available, proceed Login for: " + emailXml);
            loginPage.login_user(emailXml, passXml);
        } else {
            logTest.info("(Session reused), skip logged in step.");
        }
        // -------------------------

        CartPage cartPage = loginPage.quickGoToCart();
        CheckoutPage checkoutPage = cartPage.quickCheckOutProduct();

        softAssert.assertTrue(checkoutPage.verify_CheckoutPage_Url(),"Checkout page URL is not correct");
        softAssert.assertTrue(checkoutPage.verify_CheckoutTitle(),"Checkout page title is not display");
        softAssert.assertTrue(checkoutPage.verify_addressTitle(),"'Địa chỉ nhận hàng' title not display");
        softAssert.assertTrue(checkoutPage.verify_paymentMethodTitle(),"'Hình thức thanh toán' title not display");
        softAssert.assertTrue(checkoutPage.verify_paymentBtnIsEnable(),"'Đặt hàng' button is not enable'");
        softAssert.assertAll();
    }

    @Test(priority = 1)
    public void CheckoutTest_VerifyDefaultAddress() throws Exception {

        // Overite testcase name display on Allure report by testcode and description.
        Allure.getLifecycle().updateTestCase(result -> result.setName("TC2: Verify default address on checkout page"));

        CustomSoftAssert softAssert = new CustomSoftAssert(getDriver());
        LoginPage loginPage = new LoginPage(getDriver());
        validateHelper.clickElement(loginPage.getAcceptCookie());

        logTest.info("Test case: Verify Checkout page default address on browser: " + browserXml);

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

    @Test(dataProvider = "multiSearchData",dataProviderClass = DataProviders.class,priority = 1)
    public void CheckoutTest_VerifyItemListMatchCart(String[][] items) throws Exception {

        // Overite testcase name display on Allure report by testcode and description.
        Allure.getLifecycle().updateTestCase(result -> result.setName("TC2: Verify E2E Flow - Shopping Cart & Checkout Data Integrity"));
        CustomSoftAssert softAssert = new CustomSoftAssert(getDriver());
        LoginPage loginPage = new LoginPage(getDriver());
        validateHelper.clickElement(loginPage.getAcceptCookie());

        logTest.info("Test case: Add single product to cart on browser" + browserXml);

        // --- SMART LOGIN LOGIC ---
        if (!loginPage.isLoggedIn()) {
            logTest.info("Session not available, proceed Login for: " + emailXml);
            loginPage.login_user(emailXml, passXml);
        } else {
            logTest.info("(Session reused), skip logged in step.");
        }
        // -------------------------

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

    }

}
