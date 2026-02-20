package DataProviders;

import com.utility.Helpers.ExcelHelpers;
import com.utility.Helpers.JsonHelper;
import com.utility.PropertiesFile;
import org.testng.annotations.DataProvider;
import pojoClass.AddressModel;
import pojoClass.LoginModel;
import pojoClass.SearchModel;

import java.util.List;
import java.util.Map;

public class DataProviders {

    //-------------------  Data From Excel -----------------

    @DataProvider(name = "dataLogin")
    public Object[][] getLoginData() throws Exception {
        ExcelHelpers excel = new ExcelHelpers();

        String path = PropertiesFile.getPropValue("excelDataPath");

        List<Map<String, String>> dataList = excel.getDataList(path, "LoginData");

        Object[][] dataObj = new Object[dataList.size()][1];

        for (int i = 0; i < dataList.size(); i++) {

            LoginModel loginModel = new LoginModel();

            loginModel.setTestcode(dataList.get(i).get("testcode"));
            loginModel.setDescriptions(dataList.get(i).get("descriptions"));
            loginModel.setEmail(dataList.get(i).get("email"));
            loginModel.setPassword(dataList.get(i).get("password"));
            loginModel.setExecute(dataList.get(i).get("execute"));

            dataObj[i][0] = loginModel;
        }
        return dataObj;
    }

    @DataProvider(name = "SearchDataFromExcel")
    public Object[][] getSearchDataFromExcel() throws Exception {
        ExcelHelpers excel = new ExcelHelpers();

        String path = PropertiesFile.getPropValue("excelDataPath");

        List<Map<String, String>> dataList = excel.getDataList(path, "SearchData");

        Object[][] dataObj = new Object[dataList.size()][1];

        for (int i = 0; i < dataList.size(); i++) {

            SearchModel searchModel = new SearchModel();

            searchModel.setTestcase(dataList.get(i).get("testcase"));
            searchModel.setDescriptions(dataList.get(i).get("descriptions"));
            searchModel.setKeyword(dataList.get(i).get("keyword"));
            searchModel.setType(dataList.get(i).get("type"));
            searchModel.setExpectValued(dataList.get(i).get("expectValued"));

            String rawRate = dataList.get(i).get("expectRated");
            int rate = (int) Double.parseDouble(rawRate);
            searchModel.setExpectRated(rate);

            searchModel.setExecuted(dataList.get(i).get("executed"));

            dataObj[i][0] = searchModel;
        }
        return dataObj;
    }

    @DataProvider(name = "AddressDataFromExcel")
    public Object[][] getAddressDataFromExcel() throws Exception {
        ExcelHelpers excel = new ExcelHelpers();

        String path = PropertiesFile.getPropValue("excelDataPath");

        List<Map<String, String>> dataList = excel.getDataList(path, "AddressData");

        Object[][] dataObj = new Object[dataList.size()][1];

        for (int i = 0; i < dataList.size(); i++) {

            AddressModel addressModel = new AddressModel();

            addressModel.setPhoneNumber(dataList.get(i).get("phoneNumber"));
            addressModel.setFullName(dataList.get(i).get("fullName"));
            addressModel.setCity(dataList.get(i).get("city"));
            addressModel.setDistrict(dataList.get(i).get("district"));
            addressModel.setWard(dataList.get(i).get("ward"));
            addressModel.setAddress(dataList.get(i).get("address"));
            addressModel.setExecuted(dataList.get(i).get("executed"));

            dataObj[i][0] = addressModel;
        }
        return dataObj;
    }




    //-------------------  Data Search Scenario -----------------

    @DataProvider(name = "searchData")
    public Object[][] searchKeywords() {
        return new Object[][]{
                {"Chống nắng","sunplay","2"}, // Row 0, Column 0,1,2
                {"Nước hoa nữ","Narciso Rodriguez","3"},       // Row 1, Column 0,1,2
                {"Son thỏi","LIP ON LIP","3"},        // Row 2, Column 0,1,2
                {"Sữa rửa mặt","cerave","4"}     // Row 3, Column 0,1,2
        };
    }


    @DataProvider(name = "Search_By_price")
    public Object[][] search_By_price() {
        return new Object[][]{
                {"Kem chống nắng", "80000", "100000"}, // Row 0: Col 0 is keyword, Col 1,2 is price range (min - max)
                {"Nước hoa", "100000", "200000"}       // Row 1: Col 0 is keyword, Col 1,2 is price range (min - max)
        };
    }


    @DataProvider(name = "searchDataQuick")
    public Object[][] searchData() {
        // Class SearchModel (POJO)
        return new Object[][]{
                { new SearchModel("TC1", "Verify accurate rate by search  brand","sữa rữa mặt","cerave","cerave",90,"Y") },
                { new SearchModel("TC2", "Verify broad match key word","kem chống nắng","","chống nắng",60,"Y") }
        };
    }

    // Tutorial run searchDataQuick
       /* @Test(dataProvider = "searchDataQuick")
        public void testSearchHasaki(SearchModel data) {
            System.out.println("Search product: " + data.getName());
            System.out.println("Type product: " + data.getType());

            validateHelper.setText(inputSearch, data.getName());
        }*/

    @DataProvider(name = "multiSearchData")
    public Object[][] multiSearchData() {
        String[][] allItems = {
                {"Chống nắng", "sunplay", "2"},
                {"Nước hoa nữ", "Narciso Rodriguez", "3"},
                {"Son thỏi", "LIP ON LIP", "3"},
                {"Sữa rửa mặt", "cerave", "4"}
        };

        return new Object[][]{
                { allItems }
        };
    }



    //-------------------  Data From Json -----------------

    @DataProvider(name = "searchDataFromJson", parallel = false)
    public Object[][] getSearchData() {

        List<SearchModel> dataList = JsonHelper.getListData("src/main/resources/data/SearchData.json", SearchModel.class);

        if (dataList == null) return new Object[0][0];

        Object[][] data = new Object[dataList.size()][1];
        for (int i = 0; i < dataList.size(); i++) {
            data[i][0] = dataList.get(i);
        }
        return data;
    }

    // ----------  Tutorial run json ---------------
        /*@DataProvider(name = "loginDataFromJson")
        public Object[][] getLoginDataJson() {
            List<LoginModel> dataList = JsonHelper.getListData("src/main/resources/data/LoginData.json", LoginModel.class);
            // ... code đổ vào Object[][] tương tự như trên
            return data;
        }*/

        // Code @Test from dataprovider json
        /*@Test(dataProviderClass = DataProviders.class, dataProvider = "searchDataFromJson")
        public void testSearchHasaki(SearchModel searchData) {

            // 1. Lấy dữ liệu từ POJO
            String keyword = searchData.getName();
            String category = searchData.getType();

            System.out.println("Từ khóa: " + keyword);
            System.out.println("Danh mục: " + category);

            // 2. Thực hiện thao tác trên trình duyệt (Ví dụ với Hasaki)
            // Giả sử bạn đã có validateHelper và các locator
            validateHelper.setText(inputSearch, keyword);
            validateHelper.pressEnter(inputSearch);

            // 3. Kiểm tra kết quả (Assertion)
            // Ví dụ kiểm tra xem tiêu đề trang có chứa từ khóa không
            Assert.assertTrue(driver.getTitle().contains(keyword), "Tiêu đề trang không chứa từ khóa tìm kiếm!");
        }*/



}
