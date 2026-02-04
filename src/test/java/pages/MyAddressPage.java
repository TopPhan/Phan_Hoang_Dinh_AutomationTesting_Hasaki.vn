package pages;
import com.log.logTest;
import com.utility.CustomSoftAssert;
import com.utility.Helpers.ValidateHelper;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class MyAddressPage {
    private WebDriver driver;
    private ValidateHelper validateHelper;
    private CustomSoftAssert softAssert;
    private JavascriptExecutor js;
    private WebDriverWait wait;

    // My account panel
    private By myAccountPanel = By.xpath("//div[@class='item_header item_login user-info-group ']");
    private By myAddress = By.xpath("//a[contains(text(),'Địa chỉ giao hàng')]");
    // Log-out button

    // --- Add address ---
    // Address title
    private By addressTitle = By.xpath("//h2[contains(text(),'Sổ địa chỉ')]");
    // Add new address
    private By addNewAddress = By.xpath("//button[@aria-label='add address button']");
    // input Phone number
    private By inputPhoneNumber = By.xpath("//input[@placeholder='Số điện thoại']");
    // input Full name
    private By inputFullName = By.xpath("//input[@placeholder='Họ và tên']");
    // Dropdown list input City / Province
    private By inputCityProvinceBtn = By.xpath("//button[contains(.,'Chọn Tỉnh/ TP, Quận/ Huyện')]");
    // Textbox input City / Province
    private By inputCityProvinceTxt = By.xpath("//input[@placeholder='Tìm kiếm Tỉnh/ TP, Quận/ Huyện ...']");
    // Dropdown list input Ward
    private By inputWardBtn = By.xpath("//button[contains(.,'Chọn Phường/ Xã')]");
    // Textbox input Ward
    private By inputWardTxt = By.xpath("//input[@placeholder='Tìm kiếm Chọn Phường/ Xã ...']");
    // Button number house address and street
    private By inputHouseNumberBtn = By.xpath("//button[@name='address']");
    // Textbox input House number and street
    private By inputHouseNumberTxt = By.xpath("//input[@placeholder='Nhập vị trí của bạn']");
    // Button continue
    private By btnContinue = By.xpath("//button[@type='button'][contains(text(),'Tiếp tục')]");
    // Button save
    private By btnSave = By.xpath("//button[contains(text(),'Tiếp tục')]");


    // --- Verify Address ---
    // Update button
    private By modifyBtn = By.xpath("//button[@aria-label='update button']");
    // Receive name
    private By receiveName = By.xpath("//p[@class='text-sm font-bold']");
    // Address text
    private By addressTxt = By.xpath("//p[@class='text-muted-foreground text-sm leading-normal']");
    // Close success update pop-up
    private By closeSuccessUpdatePopUp = By.xpath("//div[@class='grid gap-1']/following-sibling::button");
    // List of address
    private By listAddress = By.xpath("//div[@class='grid gap-5 grid-cols-2 w-full']/div");
    // Default address
    private By defaultAddress = By.xpath("//div[contains(text(),'Địa chỉ mặc định')]");
    // Confirm delete address
    private By confirmDeleteAddress = By.xpath("//button[contains(text(),'Xác nhận')]");

    // --- Error message ---
    // errorPhoneNumber
    private By errorPhoneNumber = By.xpath("//p[contains(text(),'Vui lòng điền số điện thoại')]");
    // close error popup
    private By closeAddrressPopUp = By.xpath("//button[contains(text(),'Hủy')]");

    // ---- Dynamic xpath ----
    String fullNameIndex = "//div[@class='grid gap-5 grid-cols-2 w-full']//div[@class='w-full border rounded-[10px]'][%d]//p[@class='text-sm font-bold']";
    String fullAddressIndex = "//div[@class='grid gap-5 grid-cols-2 w-full']//div[@class='w-full border rounded-[10px]'][%d]//p[@class='text-muted-foreground text-sm leading-normal']";
    String removeBtnIndex = "//div[@class='grid gap-5 grid-cols-2 w-full']//div[@class='w-full border rounded-[10px]'][%d]//button[@aria-label='delete button']";



    public MyAddressPage(WebDriver driver) {
        this.driver = driver;
        validateHelper = new ValidateHelper(driver);
        softAssert = new CustomSoftAssert(driver);
        this.js = (JavascriptExecutor) driver ;
        try{
            this.wait = new WebDriverWait(driver,
                    Duration.ofSeconds(10),
                    Duration.ofMillis(500));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // ---- Get Page Element ----
    @Step("Get fullName by index")
    public String getFullNameIndex(int index) {
        By fullNameLocator = By.xpath(String.format(fullNameIndex, index));
        wait.until(ExpectedConditions.visibilityOfElementLocated(fullNameLocator));
        return validateHelper.getTextElement(fullNameLocator);
    }

    @Step("Get fullAddress by index")
    public String getFullAddressIndex(int index) {
        By fullAddressLocator = By.xpath(String.format(fullAddressIndex, index));
        wait.until(ExpectedConditions.visibilityOfElementLocated(fullAddressLocator));
        return validateHelper.getTextElement(fullAddressLocator);
    }

    @Step("Get remove button by index")
    public String getRemoveBtnIndex(int index) {
        By removeBtnLocator = By.xpath(String.format(removeBtnIndex, index));
        wait.until(ExpectedConditions.visibilityOfElementLocated(removeBtnLocator));
        return validateHelper.getTextElement(removeBtnLocator);
    }

    // ---- Verify Page ----
    @Step("Verify address tab url")
    public boolean verify_AddressTab_Url() {
        try {
            validateHelper.verifyElementIsDisplay(addressTitle);
            if(validateHelper.verifyUrl("hasaki.vn/customer/address")) {
                logTest.info("[PASS] Url: hasaki.vn/customer/address is display");
                return true ;
            }
        } catch (Exception e) {
            logTest.error("[FAIL] actual Url is: " + driver.getCurrentUrl());
            return false;
        }
        return false;
    }

    @Step("Verify title on Address Tab ")
    public boolean isTitleAddressTab() {
        try {

            Boolean addressTitleFound = validateHelper.verifyElementIsDisplay(addressTitle);
            Boolean titleMatch = validateHelper.getTextElement(addressTitle).trim().toLowerCase().contains("sổ địa chỉ");
            if(addressTitleFound && titleMatch){
                logTest.info("[PASS] Title: " + validateHelper.getTextElement(addressTitle) + " is display");
            }
            return addressTitleFound && titleMatch;
        } catch (Exception e) {
            logTest.error("[FAIL] Title 'Sổ địa chỉ' is not display");
            return false;
        }
    }


    // ---- Page Action ----
    @Step("Add new address on MyAccount page")
    public void addNewAddress(String phoneNumber, String fullname, String city, String district, String ward, String address) throws InterruptedException {

        // Wait for address page load
        validateHelper.verifyElementIsDisplay(addressTitle);
        // Click on Add new address button
        validateHelper.clickElement(addNewAddress);

        // ---- Fill address form ----
        // Fill phone number
        validateHelper.setTextByActions(inputPhoneNumber, phoneNumber);
        // Fill full name
        validateHelper.setTextByActions(inputFullName,fullname);

        // Select dropdown list city/province
        validateHelper.clickElement(inputCityProvinceBtn);
        // Enter city/province
        validateHelper.setText(inputCityProvinceTxt, city+" - "+district);
        validateHelper.action_Enter();
        // validateHelper.Delay(200); // delay 200ms for stable dom

        // Select dropdown list ward
        validateHelper.clickElement(inputWardBtn);
        // Enter ward
        validateHelper.setText(inputWardTxt, ward);
        validateHelper.action_Enter();
        // validateHelper.Delay(200); // delay 200ms for stable dom

        // Select textbox address
        validateHelper.clickElement(inputHouseNumberBtn);
        // Fill the address
        validateHelper.setText(inputHouseNumberTxt, address);
        validateHelper.action_Enter();
        // validateHelper.Delay(200); // delay 200ms for stable dom

        // Click on Continues button
        js.executeScript("document.body.style.zoom='70%'");
        validateHelper.clickElement(btnContinue);

        // Click on save address button
        js.executeScript("document.body.style.zoom='70%'");
        validateHelper.clickElement(btnSave);

        // Close success update popup
        try {
            validateHelper.clickElement(closeSuccessUpdatePopUp);
            wait.until(ExpectedConditions.invisibilityOfElementLocated(closeSuccessUpdatePopUp));
        } catch (Exception e) {
            logTest.error("[FAIL] to close success update popup");
        }
    }

    @Step("Verify address is saved")
    public boolean verifyAddressIsSaved(String fullname, String city, String district, String ward, String address) {

        // Get all address on page
        wait.until(ExpectedConditions.refreshed(
                ExpectedConditions.visibilityOfElementLocated(listAddress)));

        List<WebElement> list_Address = driver.findElements(listAddress);
        String fullAddress = address +", "+ward+", "+district+", "+city;

        // Set logic
        Boolean isFullNameSaved = false;
        Boolean isFullAddressSaved = false;

        for (int i=1;i<=list_Address.size();i++){

            //Get fullname
            String getFullName = getFullNameIndex(i);
            //Get full address
            String getFullAddress = getFullAddressIndex(i);

            // If address is found return true
            if (getFullName.trim().contains(fullname) && getFullAddress.trim().contains(fullAddress)) {
                logTest.info("Actual fullname: "+getFullName);
                logTest.info("Actual address: "+getFullAddress);
                logTest.info("[PASS] Found matching address at position: " + i);
                return true;
            }
        }
            logTest.error("[FAIL] Full name and full address aren't exist");
            return false;
    }


    @Step("Delete address by fullName and fullAddress")
    public void deleteAddressByNameAndAddressString(String fullname, String city, String district, String ward, String address) {

        // Get all address on page
        wait.until(ExpectedConditions.refreshed(
                ExpectedConditions.visibilityOfAllElementsLocatedBy(listAddress)));
        List<WebElement> list_Address = driver.findElements(listAddress);
        String fullAddress = address +", "+ward+", "+district+", "+city;

        // Set logic
        Boolean isFullNameSaved = false;
        Boolean isFullAddressSaved = false;

        for (int i=1;i<=list_Address.size();i++){

            //Get fullname
            String getFullName = getFullNameIndex(i);
            //Get full address
            String getFullAddress = getFullAddressIndex(i);
            //Get Remove address button
            By removeAddressButtonLocator = By.xpath(String.format(removeBtnIndex,i));

            // If address is found return true
            if (getFullName.trim().contains(fullname) && getFullAddress.trim().contains(fullAddress)) {
                try{
                    validateHelper.clickElement(removeAddressButtonLocator);
                    validateHelper.clickElement(confirmDeleteAddress);
                    validateHelper.waitForElementInvisible(confirmDeleteAddress);
                    logTest.info("Deleted fullname: "+getFullName);
                    logTest.info("Deleted address: "+getFullAddress);
                    logTest.info("[PASS] Deleted address at position: " + i);
                } catch (Exception e) {
                    logTest.info("[FAIL] Not found matching address");
                }
            }
        }

    }

    @Step("Verify address is deleted")
    public boolean verifyAddressIsDeleted(String fullname, String city, String district, String ward, String address) {

        // Get all address on page
        wait.until(ExpectedConditions.refreshed(
                ExpectedConditions.visibilityOfElementLocated(listAddress)));

        List<WebElement> list_Address = driver.findElements(listAddress);
        String fullAddress = address +", "+ward+", "+district+", "+city;

        // Set logic
        Boolean isFullNameSaved = false;
        Boolean isFullAddressSaved = false;

        for (int i=1;i<=list_Address.size();i++){
            //Get fullname
            String getFullName = getFullNameIndex(i);
            //Get full address
            String getFullAddress = getFullAddressIndex(i);

            // If address is found return true
            if (!(getFullName.trim().contains(fullname) && getFullAddress.trim().contains(fullAddress))) {
                logTest.info("[PASS] Full name and full address aren't exist after deleted");
                return true;
            }
        }
        logTest.error("[FAIL] Full name and full address still exist");
        return false;
    }

    @Step("Negative Test: Validata add address without phone number")
    public boolean addAddressWithoutPhoneNumber(String fullname, String city, String district, String ward, String address) throws InterruptedException {

        // Wait for address page load
        validateHelper.verifyElementIsDisplay(addressTitle);
        // Click on Add new address button
        validateHelper.clickElement(addNewAddress);

        // ---- Fill address form ----
        // *** Leave phone number blank for negative test
        validateHelper.setTextByActions(inputPhoneNumber, "");

        // Fill full name
        validateHelper.setTextByActions(inputFullName,fullname);

        // Select dropdown list city/province
        validateHelper.clickElement(inputCityProvinceBtn);
        // Enter city/province
        validateHelper.setText(inputCityProvinceTxt, city+" - "+district);
        validateHelper.action_Enter();
        // validateHelper.Delay(200); // delay 200ms for stable dom

        // Select dropdown list ward
        validateHelper.clickElement(inputWardBtn);
        // Enter ward
        validateHelper.setText(inputWardTxt, ward);
        validateHelper.action_Enter();
        // validateHelper.Delay(200); // delay 200ms for stable dom

        // Select textbox address
        validateHelper.clickElement(inputHouseNumberBtn);
        // Fill the address
        validateHelper.setText(inputHouseNumberTxt, address);
        validateHelper.action_Enter();
        // validateHelper.Delay(200); // delay 200ms for stable dom

        // Click on Continues button
        js.executeScript("document.body.style.zoom='70%'");
        validateHelper.clickElement(btnContinue);

        // Click on save address button
        js.executeScript("document.body.style.zoom='70%'");

        WebElement button = driver.findElement(btnSave);
        Actions actions = new Actions(driver);
        actions.moveToElement(button).click().perform();
        validateHelper.clickElement(btnSave);

       // Close success update popup
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(errorPhoneNumber));
            String isErrorDisplay = driver.findElement(errorPhoneNumber).getText().trim();
            logTest.info("Acctual error message: " + isErrorDisplay);
            if (isErrorDisplay.contains("Vui lòng điền số điện thoại")) {
                logTest.info("[PASS] error message is display when leave phoneNumber blank");
                validateHelper.clickElement(closeAddrressPopUp);
                validateHelper.waitForElementInvisible(closeAddrressPopUp);
                return true;
            } else {
                validateHelper.clickElement(closeAddrressPopUp);
                validateHelper.waitForElementInvisible(closeAddrressPopUp);
                return false;
            }

        } catch (Exception e) {
            logTest.error("[FAIL] can't validation error message when leave phone number blank");
            return false;
        }

    }

}
