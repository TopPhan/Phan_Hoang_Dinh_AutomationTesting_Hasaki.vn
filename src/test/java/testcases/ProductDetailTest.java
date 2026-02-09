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
import org.testng.SkipException;
import org.testng.annotations.*;
import pages.*;

@Epic("Web Ecommerce Hasaki.vn")
@Feature("Product Detail Functionality")
@Owner("Hoàng Đỉnh Automation")
public class ProductDetailTest extends multipleThread_baseSetup {
    private ValidateHelper validateHelper;
    private JavascriptExecutor js;
    private String emailXml;
    private String passXml;
    private String browserXml;

    @Parameters({"email", "password","browserType"})
    @BeforeMethod(alwaysRun = true)
    public void setProductDetail(@Optional("") String email,
                             @Optional("") String password,
                             @Optional("") String browser) throws Exception {
        //driver = getDriver();
        validateHelper = new ValidateHelper(getDriver());
        js = (JavascriptExecutor) getDriver() ;
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
            dataProvider = "searchData",
            dataProviderClass = DataProviders.class,
            groups = {"smoke", "regression"}
    )
    @Story("UI Verification")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Verify Product detail page UI elements, Name display and Add to Cart button status.")
    public void ProductDetail_VerifyUI(String keyword,String brand,String quantity) throws Exception {

        // Overite testcase name display on Allure report by testcode and description.
        Allure.getLifecycle().updateTestCase(result -> result.setName(
                String.format("TC1: Verify UI for product '%s %s'", keyword, brand)));

        CustomSoftAssert softAssert = new CustomSoftAssert(getDriver());
        LoginPage loginPage = new LoginPage(getDriver());

        logTest.info("Test case: Verify Address UI on browser: " + browserXml);

        // --- Smart login ---
        handleSmartLogin(loginPage, validateHelper);

        SearchPage searchPage = new SearchPage(getDriver());

        ProductDetailPage productDetailPage = searchPage.searchAndReturnFirstProduct(keyword+" "+brand);

        softAssert.assertTrue(productDetailPage.verify_ProductPage_Url(),"Product URL is incorrect!");
        softAssert.assertTrue(productDetailPage.isProductNameDisplay(keyword+" "+brand),"Product name is not contains keyword:" + keyword);
        softAssert.assertTrue(productDetailPage.isAddCartEnable(),"Add to Cart button should be enabled!");
        softAssert.assertAll();

        //reload for next test
        getDriver().navigate().refresh();
    }

    @Test(
            dataProvider = "searchData",
            dataProviderClass = DataProviders.class,
            groups = {"regression", "smoke"}
    )
    @Story("Cart Functionality")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify adding a 1 unit of a product to the cart and checking the mini-cart badge update.")
    public void ProductDetail_addSingleUnit(String keyword,String brand,String quantity) throws Exception {

        // Overite testcase name display on Allure report by testcode and description.
        Allure.getLifecycle().updateTestCase(result -> result.setName(
                String.format("TC2: Add Single Product '%s' to Cart", keyword)));

        CustomSoftAssert softAssert = new CustomSoftAssert(getDriver());
        LoginPage loginPage = new LoginPage(getDriver());

        logTest.info("Test case: Add single product to cart on browser" + browserXml);

        // --- Smart login ---
        handleSmartLogin(loginPage, validateHelper);

        SearchPage searchPage = new SearchPage(getDriver());

        ProductDetailPage productDetailPage = searchPage.searchAndReturnFirstProduct(keyword+" "+brand);

        Assert.assertTrue(productDetailPage.isAddCartEnable(),"Product is out of stock or button disabled!");

        // Get current quantity in cart
        int currentItems = productDetailPage.getCartQuantity();

        // Add single product to cart
        productDetailPage.addProductToCart();
        Assert.assertTrue(productDetailPage.isPopupAddToCartDisplay(),"Success popup did not appear!");
        productDetailPage.closeSuccessPopup();

        // Get quantity in cart after add
        int afterAddItems = productDetailPage.getCartQuantity();
        Assert.assertEquals(afterAddItems,currentItems+1,"Cart quantity did not increase by 1!");
    }

    @Test(
            dataProvider = "searchData",
            dataProviderClass = DataProviders.class,
            groups = {"regression"}
    )
    @Story("Cart Functionality")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify adding multiple units of a product and checking for purchase limits.")
    public void ProductDetail_addMultipleUnits(String keyword,String brand, String quantity) throws Exception {

        // Overite testcase name display on Allure report by testcode and description.
        Allure.getLifecycle().updateTestCase(result -> result.setName(
                String.format("TC3: Add %s units of '%s' to Cart", quantity, keyword)));

        CustomSoftAssert softAssert = new CustomSoftAssert(getDriver());
        LoginPage loginPage = new LoginPage(getDriver());

        logTest.info("Test case: Add multiple product to cart on browser" + browserXml);

        // --- Smart login ---
        handleSmartLogin(loginPage, validateHelper);

        SearchPage searchPage = new SearchPage(getDriver());

        ProductDetailPage productDetailPage = searchPage.searchAndReturnFirstProduct(keyword+" "+brand);

        Assert.assertTrue(productDetailPage.isAddCartEnable(),"Add to cart button is not enable");

        // Get current quantity in cart
        int currentItems = productDetailPage.getCartQuantity();
        int qtyInput = Integer.parseInt(quantity);

        // Add  product to cart
        productDetailPage.setProductQuantity(quantity);
        productDetailPage.addProductToCart();

        boolean isProductAllowOnlyBuyOne = productDetailPage.isProductAllowOnlyBuyOne();
        if (isProductAllowOnlyBuyOne) {
            logTest.info("Product has a purchase limit of 1. Skipping quantity verification.");
            //productDetailPage.closeOnlyBuyOnePopup();
            throw new SkipException("Skipped: Product limit reached (Only 1 allowed)");
        }

        // Get quantity in cart after add
        int afterAddItems = productDetailPage.getCartQuantity();
        Assert.assertEquals(afterAddItems, currentItems + qtyInput, "Cart quantity mismatch after adding multiple items!");
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
