package com.utility.Helpers;

import com.google.gson.Gson;
import java.io.FileReader;
import java.io.Reader;
import java.util.List;
import com.google.gson.internal.$Gson$Types;
import com.log.logTest;

public class JsonHelper {

    // Sử dụng <T> để đại diện cho bất kỳ Class nào bạn muốn (SearchModel, LoginModel,...)
    public static <T> List<T> getListData(String filePath, Class<T> clazz) {
        try (Reader reader = new FileReader(filePath)) {
            Gson gson = new Gson();

            // Cú pháp đặc biệt của Gson để hiểu List<T> mà không bị lỗi Type Erasure
            java.lang.reflect.Type listType = $Gson$Types.newParameterizedTypeWithOwner(null, List.class, clazz);

            return gson.fromJson(reader, listType);
        } catch (Exception e) {
            logTest.error("[FAIL] to read file JSON: " + e.getMessage());
            return null;
        }
    }
}
