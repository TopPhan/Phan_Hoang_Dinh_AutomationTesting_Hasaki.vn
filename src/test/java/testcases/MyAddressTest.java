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
import pages.LoginPage;
import pages.MyAccountPage;
import pages.MyAddressPage;
import pojoClass.AddressModel;

@Epic("Web Ecommerce Hasaki.vn")
@Feature("MyAddress Functionality")
@Owner("Hoàng Đỉnh Automation")
public class MyAddressTest extends multipleThread_baseSetup {

    private ValidateHelper validateHelper;
    private JavascriptExecutor js;
    private String emailXml;
    private String passXml;
    private String browserXml;

    @Parameters({"email", "password","browserType"})
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

    @Test(
            priority = 0,
            groups = {"smoke", "regression"}
    )
    @Story("UI Verification")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Verify Address page UI elements, correct URL navigation and Page Title.")
    public void MyAddress_verifyAddressUI() throws Exception {

        // Overite testcase name display on Allure report by testcode and description.
        Allure.getLifecycle().updateTestCase(result -> result.setName("TC1: Quick verify address page UI & URL"));
        CustomSoftAssert softAssert = new CustomSoftAssert(getDriver());
        LoginPage loginPage = new LoginPage(getDriver());
        validateHelper.clickElement(loginPage.getAcceptCookie());

        // --- SMART LOGIN LOGIC ---
        MyAccountPage myAccountPage;
        MyAddressPage myAddressPage ;
        if (!loginPage.isLoggedIn()) {
            logTest.info("Session not available, proceed Login for: " + emailXml);
            myAccountPage = loginPage.login_user(emailXml, passXml);
            myAddressPage = myAccountPage.goToMyAddressTab();
        } else {
            logTest.info("(Session reused), skip logged in step.");
            myAddressPage = new MyAddressPage(getDriver());
            //getDriver().navigate().to("https://hasaki.vn/customer/address/");
        }
        Assert.assertNotNull(myAddressPage, "MyAddressPage was not initialized!");
        // -------------------------

        Assert.assertTrue(myAddressPage.verify_AddressTab_Url(),"Address tab url doesn't match");
        Assert.assertTrue(myAddressPage.isTitleAddressTab(),"Address tab title doesn't exist");
    }

    @Test(
            dataProvider = "AddressDataFromExcel",
            dataProviderClass = DataProviders.class,
            priority = 1,
            groups = {"regression"}
    )
    @Story("Add Address Validation")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Validate that a user can successfully add a new delivery address with full details.")
    public void MyAddress_verifyAddNewAddress(AddressModel addressModel) throws Exception {

        // Checking execute column ( Y/N )
        if (addressModel.getExecuted().equalsIgnoreCase("N")) {
            // throw new SkipException is testNG method to skip the test
            throw new SkipException("Skip test case !");
        }

        // Overite testcase name display on Allure report by testcode and description.
        Allure.getLifecycle().updateTestCase(result -> result.setName(String.format("TC2: Add Address for '%s'", addressModel.getFullName())));

        CustomSoftAssert softAssert = new CustomSoftAssert(getDriver());
        LoginPage loginPage = new LoginPage(getDriver());

        // --- SMART LOGIN LOGIC ---
        MyAccountPage myAccountPage;
        MyAddressPage myAddressPage ;
        if (!loginPage.isLoggedIn()) {
            logTest.info("Session not available, proceed Login for: " + emailXml);
            validateHelper.clickElement(loginPage.getAcceptCookie());
            myAccountPage = loginPage.login_user(emailXml, passXml);
            myAddressPage = myAccountPage.goToMyAddressTab();
        } else {
            logTest.info("(Session reused), skip logged in step.");
            myAddressPage = new MyAddressPage(getDriver());
            //getDriver().navigate().to("https://hasaki.vn/customer/address/");
        }
        Assert.assertNotNull(myAddressPage, "MyAddressPage was not initialized!");
        // -------------------------

        String fullname = addressModel.getFullName();
        String fullAddress = addressModel.getAddress() +", "+addressModel.getWard()+", "+addressModel.getDistrict()+", "+addressModel.getCity();

        logTest.info("---- Add new address ----");
        logTest.info("Expect add full name: "+fullname);
        logTest.info("Expect add full address : "+fullAddress);

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
                addressModel.getAddress()),"[FAIL] Address is not saved: "+ fullAddress);

        softAssert.assertAll();

    }

    @Test(
            dataProvider = "AddressDataFromExcel",
            dataProviderClass = DataProviders.class,
            priority = 99,
            groups = {"cleanup"}
    )
    @Story("Delete Address Validation")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Validate that a user can successfully delete an existing address from the list.")
    public void MyAddress_verifyDeleteAddress(AddressModel addressModel) throws Exception {

        // Checking execute column ( Y/N )
        if (addressModel.getExecuted().equalsIgnoreCase("N")) {
            // throw new SkipException is testNG method to skip the test
            throw new SkipException("Skip test case !");
        }

        // Overite testcase name display on Allure report by testcode and description.
        Allure.getLifecycle().updateTestCase(result -> result.setName(String.format("TC3: Delete Address of '%s'", addressModel.getFullName())));

        CustomSoftAssert softAssert = new CustomSoftAssert(getDriver());
        LoginPage loginPage = new LoginPage(getDriver());


        // --- SMART LOGIN LOGIC ---
        MyAccountPage myAccountPage;
        MyAddressPage myAddressPage ;
        if (!loginPage.isLoggedIn()) {
            logTest.info("Session not available, proceed Login for: " + emailXml);
            validateHelper.clickElement(loginPage.getAcceptCookie());
            myAccountPage = loginPage.login_user(emailXml, passXml);
            myAddressPage = myAccountPage.goToMyAddressTab();
        } else {
            logTest.info("(Session reused), skip logged in step.");
            myAddressPage = new MyAddressPage(getDriver());
            //getDriver().navigate().to("https://hasaki.vn/customer/address/");
        }
        Assert.assertNotNull(myAddressPage, "MyAddressPage was not initialized!");
        // -------------------------

        String fullname = addressModel.getFullName();
        String fullAddress = addressModel.getAddress() +", "+addressModel.getWard()+", "+addressModel.getDistrict()+", "+addressModel.getCity();

        logTest.info("---- Deleted new address ----");
        logTest.info("Expect delete full name: "+fullname);
        logTest.info("Expect delete full address : "+fullAddress);


        myAddressPage.deleteAddressByNameAndAddressString(
                addressModel.getFullName(),
                addressModel.getCity(),
                addressModel.getDistrict(),
                addressModel.getWard(),
                addressModel.getAddress());

        softAssert.assertTrue(myAddressPage.verifyAddressIsDeleted(
                addressModel.getFullName(),
                addressModel.getCity(),
                addressModel.getDistrict(),
                addressModel.getWard(),
                addressModel.getAddress()),"[FAIL] Address isn't deleted");

        softAssert.assertAll();
    }

    @Test(
            dataProvider = "AddressDataFromExcel",
            dataProviderClass = DataProviders.class,
            priority = 3,
            groups = {"negative", "regression"}
    )
    @Story("Negative Validation")
    @Severity(SeverityLevel.MINOR)
    @Description("Verify that the system shows an error message when attempting to add an address without a phone number.")
    public void MyAddress_validationLeavePhoneNumberBlank(AddressModel addressModel) throws Exception {

        // Checking execute column ( Y/N )
        if (addressModel.getExecuted().equalsIgnoreCase("N")) {
            // throw new SkipException is testNG method to skip the test
            throw new SkipException("Skip test case !");
        }

        // Overite testcase name display on Allure report by testcode and description.
        Allure.getLifecycle().updateTestCase(result -> result.setName("TC4: Validate error message when phone is blank"));

        CustomSoftAssert softAssert = new CustomSoftAssert(getDriver());
        LoginPage loginPage = new LoginPage(getDriver());

        // --- SMART LOGIN LOGIC ---
        MyAccountPage myAccountPage;
        MyAddressPage myAddressPage ;
        if (!loginPage.isLoggedIn()) {
            logTest.info("Session not available, proceed Login for: " + emailXml);
            validateHelper.clickElement(loginPage.getAcceptCookie());
            myAccountPage = loginPage.login_user(emailXml, passXml);
            myAddressPage = myAccountPage.goToMyAddressTab();
        } else {
            logTest.info("(Session reused), skip logged in step.");
            myAddressPage = new MyAddressPage(getDriver());
            //getDriver().navigate().to("https://hasaki.vn/customer/address/");
        }
        Assert.assertNotNull(myAddressPage, "MyAddressPage was not initialized!");
        // -------------------------

        softAssert.assertTrue(myAddressPage.addAddressWithoutPhoneNumber(
                addressModel.getFullName(),
                addressModel.getCity(),
                addressModel.getDistrict(),
                addressModel.getWard(),
                addressModel.getAddress()),"[FAIL] error test when address without phone number doesn't display");

        softAssert.assertAll();
    }
    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result){
        try {
            logTest.info("Finish Test: " + result.getName());
            getDriver().navigate().to(PropertiesFile.getPropValue("address_url"));
        } catch (Exception e) {
            logTest.error("[FAIL]Error while reload address page.");
        }
    }
}