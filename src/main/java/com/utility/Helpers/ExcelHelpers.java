package com.utility.Helpers;

import org.apache.poi.ss.usermodel.*;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelHelpers {
    private Workbook wb;
    private Sheet sh;

    // 1. Hàm mở file Excel
    public void setExcelFile(String excelPath, String sheetName) throws Exception {
        FileInputStream fis = new FileInputStream(excelPath);
        wb = WorkbookFactory.create(fis);
        sh = wb.getSheet(sheetName);
    }

    // 2. Hàm đọc 1 ô bất kỳ (Giữ lại từ code cũ của bạn)
    public String getCellData(int rownum, int colnum) {
        try {
            Cell cell = sh.getRow(rownum).getCell(colnum);
            if (cell == null || cell.getCellType() == CellType.BLANK) return "";
            return cell.toString(); // Đơn giản hóa việc lấy data
        } catch (Exception e) {
            return "";
        }
    }

    // 3. HÀM QUAN TRỌNG NHẤT: Đọc cả bảng Excel vào List<Map>
    public List<Map<String, String>> getDataList(String filePath, String sheetName) throws Exception {
        setExcelFile(filePath, sheetName);
        List<Map<String, String>> dataList = new ArrayList<>();

        // Lấy dòng 0 làm tiêu đề (Key)
        Row headerRow = sh.getRow(0);

        // Chạy từ dòng 1 đến hết
        for (int i = 1; i <= sh.getLastRowNum(); i++) {
            Map<String, String> rowMap = new HashMap<>();

            // Chạy từng cột của dòng hiện tại
            for (int j = 0; j < headerRow.getLastCellNum(); j++) {
                String columnName = headerRow.getCell(j).getStringCellValue();
                String cellValue = getCellData(i, j);

                // Tự động khớp: Tên Cột -> Giá trị ô
                rowMap.put(columnName, cellValue);
            }
            dataList.add(rowMap);
        }
        return dataList;
    }
}
