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
import org.testng.ITestResult;
import org.testng.annotations.*;
import pages.*;

public class ProductDetailTest extends multipleThread_baseSetup {
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

    @Test(dataProvider = "searchData",dataProviderClass = DataProviders.class, priority = 0)
    public void ProductDetail_VerifyUI(String keyword) throws Exception {

        // Overite testcase name display on Allure report by testcode and description.
        Allure.getLifecycle().updateTestCase(result -> result.setName("TC1: Quick verify Product detailed page UI"));

        CustomSoftAssert softAssert = new CustomSoftAssert(getDriver());
        LoginPage loginPage = new LoginPage(getDriver());
        validateHelper.clickElement(loginPage.getAcceptCookie());

        logTest.info("Test case: Verify Address UI on browser: " + browserXml);

        // --- SMART LOGIN LOGIC ---
        if (!loginPage.isLoggedIn()) {
            logTest.info("Session not available, proceed Login for: " + emailXml);
            loginPage.login_user(emailXml, passXml);
        } else {
            logTest.info("(Session reused), skip logged in step.");
        }
        // -------------------------

        SearchPage searchPage = new SearchPage(getDriver());

        ProductDetailPage productDetailPage = searchPage.searchAndReturnFirstProduct(keyword);

        softAssert.assertTrue(productDetailPage.verify_ProductPage_Url(),"Product detailed page URL is not correct");
        softAssert.assertTrue(productDetailPage.isProductNameDisplay(keyword),"Product name is not contains keyword:" +keyword);
        softAssert.assertTrue(productDetailPage.isAddCartEnable(),"Add to cart button is not enable");
        softAssert.assertAll();

        //reload for next test
        getDriver().navigate().refresh();
        getDriver().navigate().to("https://hasaki.vn/");
    }

    @Test(dataProvider = "searchData",dataProviderClass = DataProviders.class)
    public void ProductDetail_AddSingleToCart(String keyword) throws Exception {

        // Overite testcase name display on Allure report by testcode and description.
        Allure.getLifecycle().updateTestCase(result -> result.setName("TC2: Add single product"));
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

        ProductDetailPage productDetailPage = searchPage.searchAndReturnFirstProduct(keyword);

        Assert.assertTrue(productDetailPage.isAddCartEnable(),"Add to cart button is not enable");

        // Get current quantity in cart
        int currentItems = productDetailPage.getCartQuantity();

        // Add single product to cart
        productDetailPage.addSingleProductToCart();
        Assert.assertTrue(productDetailPage.isPopupAddToCartDisplay(),"Popup add to cart is not display");
        productDetailPage.closeSuccessPopup();

        // Get quantity in cart after add
        int afterAddItems = productDetailPage.getCartQuantity();
        Assert.assertEquals(afterAddItems,currentItems+1,"Quantity in cart is not correct");
    }

    @AfterMethod
    public void tearDown(ITestResult result){
        try {
            logTest.info("Finished row: " + result.getName());
            if (!result.isSuccess()) {
                logTest.error("Test failed, clearing cookies and stopping window...");
                getDriver().manage().deleteAllCookies();
                ((org.openqa.selenium.JavascriptExecutor) getDriver()).executeScript("window.stop();");
            }
            getDriver().navigate().to("https://hasaki.vn/");
        } catch (Exception e) {
            logTest.error("Error while cleaning up after row: " + result.getName());
        }
    }

}
