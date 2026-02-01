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
        // 1. Chỉ đường dẫn tới file Excel của bạn
        String path = PropertiesFile.getPropValue("excelDataPath");

        // 2. Hốt hết dữ liệu vào cái "Giỏ" List<Map> bằng hàm xịn bạn vừa viết
        List<Map<String, String>> dataList = excel.getDataList(path, "LoginData");

        // 3. Chuẩn bị cái "Băng chuyền" Object[][] để TestNG bốc đi
        Object[][] dataObj = new Object[dataList.size()][1];

        for (int i = 0; i < dataList.size(); i++) {
            // 4. Tạo cái "Khung" POJO và đổ dữ liệu từ Map vào từng cái nhãn tương ứng
            LoginModel loginModel = new LoginModel();

            // Lấy từ Map bằng Key (tên cột) rồi set vào POJO
            loginModel.setTestcode(dataList.get(i).get("testcode"));
            loginModel.setDescriptions(dataList.get(i).get("descriptions"));
            loginModel.setEmail(dataList.get(i).get("email"));
            loginModel.setPassword(dataList.get(i).get("password"));
            loginModel.setExecute(dataList.get(i).get("execute"));
            // 5. Đặt cái khung đã đầy dữ liệu lên băng chuyền
            dataObj[i][0] = loginModel;
        }

        return dataObj;
    }

    @DataProvider(name = "SearchDataFromExcel")
    public Object[][] getSearchDataFromExcel() throws Exception {
        ExcelHelpers excel = new ExcelHelpers();
        // 1. Chỉ đường dẫn tới file Excel của bạn
        String path = PropertiesFile.getPropValue("excelDataPath");

        // 2. Hốt hết dữ liệu vào cái "Giỏ" List<Map> bằng hàm xịn bạn vừa viết
        List<Map<String, String>> dataList = excel.getDataList(path, "SearchData");

        // 3. Chuẩn bị cái "Băng chuyền" Object[][] để TestNG bốc đi
        Object[][] dataObj = new Object[dataList.size()][1];

        for (int i = 0; i < dataList.size(); i++) {
            // 4. Tạo cái "Khung" POJO và đổ dữ liệu từ Map vào từng cái nhãn tương ứng
            SearchModel searchModel = new SearchModel();

            // Lấy từ Map bằng Key (tên cột) rồi set vào POJO
            searchModel.setTestcase(dataList.get(i).get("testcase"));
            searchModel.setDescriptions(dataList.get(i).get("descriptions"));
            searchModel.setKeyword(dataList.get(i).get("keyword"));
            searchModel.setType(dataList.get(i).get("type"));
            searchModel.setExpectValued(dataList.get(i).get("expectValued"));

            // Convert String to int
            String rawRate = dataList.get(i).get("expectRated");
            int rate = (int) Double.parseDouble(rawRate);
            searchModel.setExpectRated(rate);

            searchModel.setExecuted(dataList.get(i).get("executed"));

            // 5. Đặt cái khung đã đầy dữ liệu lên băng chuyền
            dataObj[i][0] = searchModel;
        }

        return dataObj;
    }

    @DataProvider(name = "AddressDataFromExcel")
    public Object[][] getAddressDataFromExcel() throws Exception {
        ExcelHelpers excel = new ExcelHelpers();
        // 1. Chỉ đường dẫn tới file Excel của bạn
        String path = PropertiesFile.getPropValue("excelDataPath");

        // 2. Hốt hết dữ liệu vào cái "Giỏ" List<Map> bằng hàm xịn bạn vừa viết
        List<Map<String, String>> dataList = excel.getDataList(path, "AddressData");

        // 3. Chuẩn bị cái "Băng chuyền" Object[][] để TestNG bốc đi
        Object[][] dataObj = new Object[dataList.size()][1];

        for (int i = 0; i < dataList.size(); i++) {
            // 4. Tạo cái "Khung" POJO và đổ dữ liệu từ Map vào từng cái nhãn tương ứng
            AddressModel addressModel = new AddressModel();

            // Lấy từ Map bằng Key (tên cột) rồi set vào POJO
            addressModel.setPhoneNumber(dataList.get(i).get("phoneNumber"));
            addressModel.setFullName(dataList.get(i).get("fullName"));
            addressModel.setCity(dataList.get(i).get("city"));
            addressModel.setDistrict(dataList.get(i).get("district"));
            addressModel.setWard(dataList.get(i).get("ward"));
            addressModel.setAddress(dataList.get(i).get("address"));
            addressModel.setExecuted(dataList.get(i).get("executed"));

            // 5. Đặt cái khung đã đầy dữ liệu lên băng chuyền
            dataObj[i][0] = addressModel;
        }

        return dataObj;
    }




    //-------------------  Data Include Quick -----------------

    @DataProvider(name = "searchData")
    public Object[][] searchKeywords() {
        return new Object[][]{
                {"Chống nắng","sunplay","2"}, // Row 0, Column 0
                {"Nước hoa nữ","Narciso Rodriguez","3"},       // Row 1, Column 0
                {"Son thỏi","3CE","3"},        // Row 2, Column 0
                {"Sữa rửa mặt","cerave","4"}     // Row 3, Column 0
        };
    }

    // Test chay searchData
    /*@Test(dataProvider = "searchData")
    public void testSearch(String keyword) { // Cột 0 sẽ đổ vào biến keyword
        System.out.println("Đang search từ khóa: " + keyword);
        // Code search của bạn ở đây...
    }*/

    @DataProvider(name = "Search_By_price")
    public Object[][] search_By_price() {
        return new Object[][]{
                {"Kem chống nắng", "80000", "100000"}, // Row 0: Col 0 là keyword, Col 1 là expected
                {"Nước hoa", "100000", "200000"}         // Row 1: Col 0 là keyword, Col 1 là expect
        };
    }

    // Test chay search_expected_Data
    /*@Test(dataProvider = "search_expected_Data")
    public void testSearch(String keyword, String expectResulted) { // Cột 0 sẽ đổ vào biến keyword, cột 1 sẽ đổ bào biến expectResulted
        System.out.println("Đang search từ khóa: " + keyword);
        Syste.out.println(" Kết quả mong đợi: " + expectResulted);
        // Code search của bạn ở đây...
    }*/

    @DataProvider(name = "searchDataQuick")
    public Object[][] searchData() {
        // Giả sử bạn có class SearchModel (POJO)
        return new Object[][]{
                { new SearchModel("TC1", "Verify accurate rate by search  brand","sữa rữa mặt","cerave","cerave",90,"Y") },
                { new SearchModel("TC2", "Verify broad match key word","kem chống nắng","","chống nắng",60,"Y") }
        };
    }

    // Test chay searchDataQuick
   /* @Test(dataProvider = "searchDataQuick")
    public void testSearchHasaki(SearchModel data) {
        // Thay vì dùng nhiều biến, bạn gọi dữ liệu từ đối tượng data
        System.out.println("Tìm kiếm: " + data.getName());
        System.out.println("Loại sản phẩm: " + data.getType());

        // Ví dụ áp dụng vào Hasaki
        validateHelper.setText(inputSearch, data.getName());
        // ... code tiếp theo
    }*/




    //-------------------  Data From Json -----------------

    @DataProvider(name = "searchDataFromJson", parallel = false)
    public Object[][] getSearchData() {
        // Truyền thêm SearchModel.class để hàm biết đường mà map dữ liệu
        List<SearchModel> dataList = JsonHelper.getListData("src/main/resources/data/SearchData.json", SearchModel.class);

        if (dataList == null) return new Object[0][0];

        Object[][] data = new Object[dataList.size()][1];
        for (int i = 0; i < dataList.size(); i++) {
            data[i][0] = dataList.get(i);
        }
        return data;
    }

    // Ví dụ sau này bạn làm cho LoginTest bằng JSON, bạn chỉ cần copy lại y hệt
    /*@DataProvider(name = "loginDataFromJson")
    public Object[][] getLoginDataJson() {
        List<LoginModel> dataList = JsonHelper.getListData("src/main/resources/data/LoginData.json", LoginModel.class);
        // ... code đổ vào Object[][] tương tự như trên
        return data;
    }*/

    // Code @Test từ dataprovider json
    /*@Test(dataProviderClass = DataProviders.class, dataProvider = "searchDataFromJson")
    public void testSearchHasaki(SearchModel searchData) {
        System.out.println("--- Bắt đầu Test Case SearchTest ---");

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
