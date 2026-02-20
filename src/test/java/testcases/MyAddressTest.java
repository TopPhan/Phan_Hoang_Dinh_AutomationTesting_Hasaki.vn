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
    public void setupMyAddress(@Optional("") String email,
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
            groups = {"smoke", "regression"}
    )
    @Story("UI Verification")
    @Severity(SeverityLevel.BLOCKER)
    @Description("""
        ### [UI] Address Page Verification
        **Objective:** Ensure the Address Management page loads correctly with valid URL and Title.
        
        **Test Steps:**
        1. **Smart Login:** Reuse session or login to access the account.
        2. **Navigation:** Navigate to the "My Address" tab.
        3. **UI Check:** Verify the page URL and the presence of the Address Tab title.
        
        **Expected Result:** User is on the correct Address Management page without UI broken.
        """)
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
            groups = {"regression"}
    )
    @Story("Add Address Validation")
    @Severity(SeverityLevel.CRITICAL)
    @Description("""
        ### [Function] Add New Delivery Address
        **Objective:** Validate that a user can successfully create and save a new shipping address.
        
        **Test Steps:**
        1. **Smart Login:** Ensure the session is active.
        2. **Input Data:** Fill in Name, Phone, and select Province/District/Ward from Excel.
        3. **Submission:** Click save to store the new address.
        4. **Verification:** Verify the saved information matches the input data exactly.
        
        **Expected Result:** New address is saved and displayed correctly in the address list.
        """)
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
            groups = {"cleanup"}
    )
    @Story("Delete Address Validation")
    @Severity(SeverityLevel.CRITICAL)
    @Description("""
        ### [Cleanup] Delete Existing Address
        **Objective:** Ensure the system can remove an address from the user's account.
        
        **Test Steps:**
        1. **Locate Target:** Identify the address to be deleted by Name and Address string.
        2. **Execution:** Click delete and confirm the removal.
        3. **Post-check:** Verify the address no longer exists in the list.
        
        **Expected Result:** The targeted address is completely removed from the UI and system.
        """)
    public void MyAddress_verifyDeleteAddress(AddressModel addressModel) throws Exception {

        // Checking execute column ( Y/N )
        if (addressModel.getExecuted().equalsIgnoreCase("N")) {
            // throw new SkipException is testNG method to skip the test
            throw new SkipException("Skip test case !");
        }

        // Overite testcase name display on Allure report by testcode and description.
        Allure.getLifecycle().updateTestCase(result -> result.setName(String.format("Delete Address of '%s'", addressModel.getFullName())));

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
            groups = {"negative", "regression"}
    )
    @Story("Negative Validation")
    @Severity(SeverityLevel.MINOR)
    @Description("""
        ### [Negative] Missing Phone Number Validation
        **Objective:** Verify that the system prevents saving an address when the phone number is missing.
        
        **Test Steps:**
        1. **Incomplete Input:** Fill in all address details but leave the Phone Number field empty.
        2. **Submit:** Attempt to save the address.
        3. **Validation:** Capture and check the specific error message.
        
        **Expected Result:** System blocks the submission and displays a clear "Required" warning for the phone field.
        """)
    public void MyAddress_validationLeavePhoneNumberBlank(AddressModel addressModel) throws Exception {

        // Checking execute column ( Y/N )
        if (addressModel.getExecuted().equalsIgnoreCase("N")) {
            // throw new SkipException is testNG method to skip the test
            throw new SkipException("Skip test case !");
        }

        // Overite testcase name display on Allure report by testcode and description.
        Allure.getLifecycle().updateTestCase(result -> result.setName("TC3: Validate error message when phone is blank"));

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