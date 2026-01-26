package testcases;

import com.bases.multipleThread_baseSetup;
import com.log.logTest;
import com.utility.CustomSoftAssert;
import com.utility.Helpers.ValidateHelper;
import com.utility.PropertiesFile;
import io.qameta.allure.Allure;
import org.openqa.selenium.JavascriptExecutor;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pages.*;

public class ProductDetailTest extends multipleThread_baseSetup {
    private ValidateHelper validateHelper;
    private JavascriptExecutor js;
    private CustomSoftAssert softAssert;

    public LoginPage loginPage;
    public SearchPage searchPage;
    public ProductDetailPage productDetailPage;



    @BeforeClass
    public void setLoginPage() throws Exception {
        //driver = getDriver();
        validateHelper = new ValidateHelper(getDriver());
        js = (JavascriptExecutor) getDriver() ;
        softAssert = new CustomSoftAssert(getDriver());

        loginPage = new LoginPage(getDriver());
        validateHelper.clickElement(loginPage.getAcceptCookie());

        logTest.info("Test case: Verify Address UI");
        logTest.info("Default Username: " + PropertiesFile.getPropValue("username"));
        logTest.info("Default Password: " + PropertiesFile.getPropValue("password"));

        loginPage.login_user(PropertiesFile.getPropValue("username"), PropertiesFile.getPropValue("password"));
        searchPage = new SearchPage(getDriver());
    }

    @Test
    public void ProductDetail_VerifyUI() throws InterruptedException {

        // Overite testcase name display on Allure report by testcode and description.
        Allure.getLifecycle().updateTestCase(result -> result.setName("TC1: Quick verify Product detailed page UI"));

        productDetailPage = searchPage.searchAndReturnFirstProduct("Sữa rửa mặt cerave");
        logTest.info(productDetailPage.verify_ProductPage_Url());
        logTest.info(productDetailPage.isProductNameDisplay("Sữa rửa mặt cerave"));


    }

}
