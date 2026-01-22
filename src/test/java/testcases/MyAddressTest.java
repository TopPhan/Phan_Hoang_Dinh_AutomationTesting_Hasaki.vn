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
import org.testng.SkipException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.LoginPage;
import pages.MyAccountPage;
import pages.MyAddressPage;
import pojoClass.AddressModel;

public class MyAddressTest extends multipleThread_baseSetup {

    private ValidateHelper validateHelper;
    private JavascriptExecutor js;
    private CustomSoftAssert softAssert;

    public LoginPage loginPage;
    public MyAccountPage myAccountPage;
    public MyAddressPage myAddressPage;


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

        myAccountPage = loginPage.login_user(PropertiesFile.getPropValue("username"), PropertiesFile.getPropValue("password"));
        myAddressPage = myAccountPage.goToMyAddressTab();
    }

    @Test(priority = 0)
    public void MyAddress_verifyAddressUI() throws Exception {

        // Overite testcase name display on Allure report by testcode and description.
        Allure.getLifecycle().updateTestCase(result -> result.setName("TC1: Quick verify address page UI"));

        Assert.assertTrue(myAddressPage.verify_AddressTab_Url(),"Address tab url doesn't match");
        Assert.assertTrue(myAddressPage.isTitleAddressTab(),"Address tab title doesn't exist");
    }



    @Test(dataProvider = "AddressDataFromExcel",dataProviderClass = DataProviders.class, dependsOnMethods = "MyAddress_verifyAddressUI", priority = 1)
    public void MyAddress_verifyAddNewAddress(AddressModel addressModel) throws Exception {

        // Checking execute column ( Y/N )
        if (addressModel.getExecuted().equalsIgnoreCase("N")) {
            // throw new SkipException is testNG method to skip the test
            throw new SkipException("Skip test case !");
        }

        // Overite testcase name display on Allure report by testcode and description.
        Allure.getLifecycle().updateTestCase(result -> result.setName("TC2: Verify add address"));

        myAddressPage.addNewAddress(addressModel.getPhoneNumber(),
                addressModel.getFullName(),
                addressModel.getCity(),
                addressModel.getDistrict(),
                addressModel.getWard(),
                addressModel.getAddress());

        softAssert.assertTrue(myAddressPage.verifyAddressIsSaved(addressModel.getFullName(),
                addressModel.getCity(),
                addressModel.getDistrict(),
                addressModel.getWard(),
                addressModel.getAddress()),"[FAIL] Address is not saved");

        softAssert.assertAll();
    }

    @AfterMethod
    public void tearDown(ITestResult result){
        try {
            logTest.info("Finish Test: " + result.getName());
            getDriver().navigate().to(PropertiesFile.getPropValue("address_url"));

        } catch (Exception e) {
            logTest.error("[FAIL]Error while reload address page.");
        }

    }

}