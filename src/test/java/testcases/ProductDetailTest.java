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
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.*;

public class ProductDetailTest extends multipleThread_baseSetup {
    private ValidateHelper validateHelper;
    private JavascriptExecutor js;

    @BeforeMethod
    public void setLoginPage() throws Exception {
        //driver = getDriver();
        validateHelper = new ValidateHelper(getDriver());
        js = (JavascriptExecutor) getDriver() ;

    }

    @Test(dataProvider = "searchData",dataProviderClass = DataProviders.class, priority = 0)
    public void ProductDetail_VerifyUI(String keyword) throws Exception {

        // Overite testcase name display on Allure report by testcode and description.
        Allure.getLifecycle().updateTestCase(result -> result.setName("TC1: Quick verify Product detailed page UI"));

        CustomSoftAssert softAssert = new CustomSoftAssert(getDriver());
        LoginPage loginPage = new LoginPage(getDriver());
        validateHelper.clickElement(loginPage.getAcceptCookie());

        logTest.info("Test case: Verify Address UI");
        logTest.info("Default Username: " + PropertiesFile.getPropValue("username"));
        logTest.info("Default Password: " + PropertiesFile.getPropValue("password"));

        loginPage.login_user(PropertiesFile.getPropValue("username"), PropertiesFile.getPropValue("password"));
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

        logTest.info("Test case: Verify Address UI");
        logTest.info("Default Username: " + PropertiesFile.getPropValue("username"));
        logTest.info("Default Password: " + PropertiesFile.getPropValue("password"));

        loginPage.login_user(PropertiesFile.getPropValue("username"), PropertiesFile.getPropValue("password"));
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

}
