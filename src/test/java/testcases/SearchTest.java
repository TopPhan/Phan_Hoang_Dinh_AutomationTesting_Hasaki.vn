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
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.LoginPage;
import pages.ProductDetailPage;
import pages.SearchPage;
import pojoClass.SearchModel;


@Epic("Web Ecommerce Hasaki.vn")
@Feature("Search Functionality")
@Owner("Hoàng Đỉnh Automation")
public class SearchTest extends multipleThread_baseSetup {
    private ValidateHelper validateHelper;
    private JavascriptExecutor js;

    @BeforeMethod(alwaysRun = true)
    public void setupSearch() {
        validateHelper = new ValidateHelper(getDriver());
        js = (JavascriptExecutor) getDriver();
    }

    @Test(
            dataProvider = "SearchDataFromExcel",
            dataProviderClass = DataProviders.class,
            groups = {"regression"}
    )
    @Story("Search Accuracy Validation")
    @Severity(SeverityLevel.BLOCKER)
    @Description("""
        ### [Data-Driven] Search Accuracy & Matching Rate
        **Objective:** Validate search engine quality by calculating the matching rate of results against expected keywords.
        
        **Test Steps:**
        1. **Dynamic Search:** Execute search using keywords and types from Excel.
        2. **First Page Audit:** Calculate matching percentage for the initial result set.
        3. **Deep Pagination:** Traverse through all available pages to compute the "Global Accuracy Rate".
        4. **Threshold Validation:** Compare final rate against the defined expected percentage.
        
        **Target:** Ensure search results remain relevant across multiple pages.
        """)
    public void search_verifyResultsAccuracyWithMultipleKeywords(SearchModel searchModel) throws Exception {

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

    @Test(
            dataProvider = "Search_By_price",
            dataProviderClass = DataProviders.class,
            groups = {"regression"}
    )
    @Story("Price Filter Validation")
    @Severity(SeverityLevel.NORMAL)
    @Description("""
        ### [Filter] Price Range Validation
        **Objective:** Verify that the price filter correctly restricts products within the specified min-max range.
        
        **Test Steps:**
        1. **Search & Filter:** Search for a product and apply a price range filter.
        2. **Multi-page Verification:** Loop through all result pages.
        3. **Price Comparison:** Parse and verify every single product price against the filter boundary.
        
        **Expected Result:** No product price falls outside the [Min - Max] range across all pages.
        """)
    public void search_verifyPriceFilterFunctionality(String name, String minPrice,String maxPrice) throws Exception {

        // Overite testcase name display on Allure report by testcode and description.
        Allure.getLifecycle().updateTestCase(result -> result.setName(
                String.format("TC5 filter price: [%s] | Range: %s - %s", name, minPrice, maxPrice)
        ));

        LoginPage loginPage = new LoginPage(getDriver());
        validateHelper.clickElement(loginPage.getAcceptCookie());

        SearchPage searchPage = new SearchPage(getDriver());
        CustomSoftAssert softAssert = new CustomSoftAssert(getDriver());

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

    @Test(
            groups = {"negative","regression"}
    )
    @Story("Negative Search Scenarios")
    @Severity(SeverityLevel.NORMAL)
    @Description("""
        ### [Negative] Invalid Keyword Handling
        **Objective:** Ensure the system displays a proper "No Results Found" message for invalid/random strings.
        
        **Test Steps:**
        1. **Input:** Search with a non-existent/random keyword.
        2. **Verification:** Check for the presence of the "No Product Found" placeholder/message.
        
        **Expected Result:** System handles invalid input gracefully without crashing or showing blank pages.
        """)
    public void search_verifyNoResultsForInvalidKeyword() {

        String keyword = "ádefsDFFFsdf";
        // Overite testcase name display on Allure report by testcode and description.
        Allure.getLifecycle().updateTestCase(result -> result.setName("TC6 Negative Search: '" + keyword + "'"));

        LoginPage loginPage = new LoginPage(getDriver());
        validateHelper.clickElement(loginPage.getAcceptCookie());

        SearchPage searchPage = new SearchPage(getDriver());
        CustomSoftAssert softAssert = new CustomSoftAssert(getDriver());

        searchPage.searchProduct(keyword);
        Assert.assertTrue(searchPage.verifyNoProductFound(),"[FAIL]Error message is not display after search noneless keyword");
    }

    @Test(
            groups = {"smoke", "regression"}
    )
    @Feature("Search Functionality")
    @Story("Product Navigation")
    @Severity(SeverityLevel.BLOCKER)
    @Description("""
        ### [Navigation] Search to Product Detail
        **Objective:** Ensure the user can reach the correct Product Detail Page (PDP) from the search results.
        
        **Test Steps:**
        1. **Search:** Input specific keyword and brand.
        2. **Selection:** Click on the first product in the results list.
        3. **Data Sync:** Verify that the PDP displays the correct Product Name and Brand.
        
        **Expected Result:** Smooth transition from Search to PDP with 100% data consistency.
        """)
    public void search_verifyNavigationToProductDetail() throws InterruptedException {

        String keyword = "rửa mặt";
        String brand = "cerave";
        // Overite testcase name display on Allure report by testcode and description.
        Allure.getLifecycle().updateTestCase(result -> result.setName(String.format("TC7 Search & Navigate: [%s] [%s]", keyword,brand)));

        LoginPage loginPage = new LoginPage(getDriver());
        validateHelper.clickElement(loginPage.getAcceptCookie());

        SearchPage searchPage = new SearchPage(getDriver());
        CustomSoftAssert softAssert = new CustomSoftAssert(getDriver());

        ProductDetailPage productDetailPage = searchPage.searchAndReturnFirstProduct(keyword+" "+brand);
        Assert.assertTrue(productDetailPage.getProductBrand().trim().equalsIgnoreCase(brand), "Product brand is not correct");
        Assert.assertTrue(productDetailPage.getProductName().trim().toLowerCase().contains(keyword), "Product name is not correct");
    }

    @AfterMethod(alwaysRun = true)
    public void closeSearch(ITestResult result) {
        try {
            logTest.info("Cleaning up after row: " + result.getName());
            //getDriver().manage().deleteAllCookies();
            if (!result.isSuccess()) {
                ((org.openqa.selenium.JavascriptExecutor) getDriver()).executeScript("window.stop();");
            }
            getDriver().navigate().to(PropertiesFile.getPropValue("url"));
        } catch (Exception e) {
            logTest.error("Error while cleaning up after row: " + result.getName());
        }
    }

}
