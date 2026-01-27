package testcases;

import DataProviders.DataProviders;
import com.bases.multipleThread_baseSetup;
import com.log.logTest;
import com.utility.CustomSoftAssert;
import com.utility.Helpers.ValidateHelper;
import io.qameta.allure.*;
import org.openqa.selenium.JavascriptExecutor;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.SkipException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.LoginPage;
import pages.SearchPage;
import pojoClass.SearchModel;

public class SearchTest extends multipleThread_baseSetup {

    private ValidateHelper validateHelper;
    private JavascriptExecutor js;

    @BeforeMethod
    public void createHelper() {
        validateHelper = new ValidateHelper(getDriver());
        js = (JavascriptExecutor) getDriver() ;
    }

    @Test(dataProvider = "SearchDataFromExcel",dataProviderClass = DataProviders.class, priority = 0)
    @Feature("SearchTest")
    @Story("SearchTest with various keyword")
    @Severity(SeverityLevel.BLOCKER)
    public void Search_testFunctionality(SearchModel searchModel) throws Exception {

        // Checking execute column ( Y/N )
        if (searchModel.getExecuted().equalsIgnoreCase("N")) {
            // throw new SkipException is testNG method to skip the test
            throw new SkipException("Skip test case: "+ searchModel.getDescriptions() +" "+ searchModel.getDescriptions());
        }

        // Overite testcase name display on Allure report by testcode and description.
        Allure.getLifecycle().updateTestCase(result -> result.setName(searchModel.getTestcase() + " " + searchModel.getDescriptions()));

        LoginPage loginPage = new LoginPage(getDriver());
        validateHelper.clickElement(loginPage.getAcceptCookie());

        SearchPage searchPage = new SearchPage(getDriver());
        CustomSoftAssert softAssert = new CustomSoftAssert(getDriver());

        logTest.info(searchModel.getTestcase() + " " + searchModel.getDescriptions());

        // Set global total items, total matched keyword items
        int globalTotal = 0;
        int globalMatch = 0;

        // 1. Find element in first page.
        // SearchTest items
        searchPage.searchProduct(searchModel.getKeyword() +" "+ searchModel.getType());
        globalTotal += searchPage.getTotalProductsOnPage();
        globalMatch += searchPage.countMatchingProducts(searchModel.getExpectValued());

        // If item in first search return > 0 - > do test else assert fail
        Assert.assertTrue(globalTotal > 0, "FAIL: no item in search page " + searchModel.getTestcase());

            double accuracy = ((double) globalMatch / globalTotal) * 100;
            logTest.info("Total items in first page: " + globalTotal);
            logTest.info("Total items in first page which contains " + searchModel.getExpectValued() + " is :" + globalMatch);
            logTest.info("Accurate rate in first page: " + accuracy + "%");

            // 1. Decision Pass/Fail base on approximal expectRated% in first page
            Assert.assertTrue(accuracy >= searchModel.getExpectRated(), "Accurate rate in first page lower 60% (Real rate: " + accuracy + "%)");

            // 2. Find element to next page until goToNextPage() = false (mean next button is disabled)
            // int countPage = 1;
            // This count to stop / while (searchPage.goToNextPage() && countPage < 3) - > limit 3 pages
            while (searchPage.goToNextPage()) {
                globalTotal += searchPage.getTotalProductsOnPage();
                globalMatch += searchPage.countMatchingProducts(searchModel.getExpectValued());

                // countPage++;
            }

            // 3. Calculate Accurate rate
            accuracy = ((double) globalMatch / globalTotal) * 100;

            logTest.info("Total items: " + globalTotal);
            logTest.info("Total items which contains " + searchModel.getExpectValued() + " is :" + globalMatch);
            logTest.info("Accurate rate: " + accuracy + "%");

            // 4. Decision Pass/Fail base on approximal expectValued% in all page
            softAssert.assertTrue(accuracy >= searchModel.getExpectRated(), "Accurate rate lower 40% (Real rate: " + accuracy + "%)");

            // Collect all assertions
            softAssert.assertAll();
    }

    @Test(dataProvider = "Search_By_price",dataProviderClass = DataProviders.class, priority = 1)
    @Feature("SearchTest")
    @Story("SearchTest with min and max price")
    @Severity(SeverityLevel.NORMAL)
    public void Search_testFilerMinMaxPrice(String name, String minPrice,String maxPrice) throws Exception {


        // Overite testcase name display on Allure report by testcode and description.
        Allure.getLifecycle().updateTestCase(result -> result.setName("TC5 Verify search by price"));

        LoginPage loginPage = new LoginPage(getDriver());
        validateHelper.clickElement(loginPage.getAcceptCookie());

        SearchPage searchPage = new SearchPage(getDriver());
        CustomSoftAssert softAssert = new CustomSoftAssert(getDriver());

        logTest.info("TC5 Verify search by price");

        // 1. Find element in first page.
        // SearchTest items
        searchPage.searchProduct(name);
        // Apply filler
        searchPage.filterByPriceRange(minPrice, maxPrice);
        // Assert all price between min and max
        Assert.assertTrue(searchPage.verifyPriceRange(minPrice, maxPrice), "FAIL: Price range of First is not correct");

        int globalTotal = searchPage.getTotalProductsOnPage();

        // If item in first search return > 0 - > do test else assert fail
        Assert.assertTrue(globalTotal > 0, "FAIL: no item in search page ");

        logTest.info("First page of price list is accurate between: " + minPrice + " and " + maxPrice);
        int count = 2;
        while (searchPage.goToNextPage()) {
            Assert.assertTrue(searchPage.verifyPriceRange(minPrice, maxPrice),"FAIL: Price range of "+ count + " is not correct");
            logTest.info("Page " + count + " of price list is accurate between: " + minPrice + " and " + maxPrice);
            count++;
        }
    }

    @Test(priority = 2)
    @Feature("SearchTest")
    @Story("SearchTest with none less keyword")
    @Severity(SeverityLevel.NORMAL)
    public void Search_Negative_TestWithNoneLessKeyword() {

        // Overite testcase name display on Allure report by testcode and description.
        Allure.getLifecycle().updateTestCase(result -> result.setName("TC6 Negative Test: SearchTest with none less keyword"));

        LoginPage loginPage = new LoginPage(getDriver());
        validateHelper.clickElement(loginPage.getAcceptCookie());

        SearchPage searchPage = new SearchPage(getDriver());
        CustomSoftAssert softAssert = new CustomSoftAssert(getDriver());

        logTest.info("TC6 Negative Test: SearchTest with none less keyword");

        String keyword = "ádefsDFFFsdf";
        searchPage.searchProduct(keyword);

        Assert.assertTrue(searchPage.verifyNoProductFound(),"[FAIL]Error message is not display after search noneless keyword");

    }

    @Test(priority = 3)
    @Feature("SearchTest")
    @Story("Search and click first items")
    @Severity(SeverityLevel.NORMAL)
    public void Search_ClickFirstItems() throws InterruptedException {

        // Overite testcase name display on Allure report by testcode and description.
        Allure.getLifecycle().updateTestCase(result -> result.setName("TC6 Negative Test: SearchTest with none less keyword"));

        LoginPage loginPage = new LoginPage(getDriver());
        validateHelper.clickElement(loginPage.getAcceptCookie());

        SearchPage searchPage = new SearchPage(getDriver());
        CustomSoftAssert softAssert = new CustomSoftAssert(getDriver());

        logTest.info("TC7 SearchTest and click firsh items");

        String keyword = "Sữa rửa mặt cerave";
        searchPage.searchAndReturnFirstProduct(keyword);

    }

    @AfterMethod
    public void closeSearch(ITestResult result) {
        try {
            logTest.info("Cleaning up after row: " + result.getName());
            getDriver().manage().deleteAllCookies();

            if (!result.isSuccess()) {
                ((org.openqa.selenium.JavascriptExecutor) getDriver()).executeScript("window.stop();");
            }

            getDriver().navigate().to("https://hasaki.vn/");

        } catch (Exception e) {
            logTest.error("Error while cleaning up after row: " + result.getName());
        }
    }


}
