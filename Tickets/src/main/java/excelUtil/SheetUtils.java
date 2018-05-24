package excelUtil;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Class that operate XSSF sheet from Workbook
 * Created by zhangyang33 on 2017/9/18.
 */
public class SheetUtils {

    /**
     * Remove sheet form workbook
     * @param wb        Given excel workbook
     * @param sheetName Given excel sheet name to remove
     */
    public static void removeSheetByName(XSSFWorkbook wb, String sheetName){
        try{
            wb.removeSheetAt(wb.getSheetIndex(sheetName));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Create sheet for workbook
     * @param wb        Given excel workbook
     * @param sheetName Given excel sheet name to create
     * @return  XSSFSheet  Return created sheet
     */
    public static XSSFSheet createSheetByName(XSSFWorkbook wb, String sheetName){
        XSSFSheet sheet = wb.createSheet(sheetName);
        XSSFRow latestRow = sheet.createRow(0);

        try{
            switch (sheetName){
                case "Output":
                    latestRow.createCell(0).setCellValue("ID");
                    latestRow.createCell(1).setCellValue("TestCase");
                    latestRow.createCell(2).setCellValue("Response");
                    break;
                case "Comparison":
                    latestRow.createCell(0).setCellValue("ID");
                    latestRow.createCell(1).setCellValue("TestCase");
                    latestRow.createCell(2).setCellValue("Assert");
                    latestRow.createCell(3).setCellValue("Failure field:Value");
                    break;
                case "Result":
                    latestRow.createCell(0).setCellValue("ID");
                    latestRow.createCell(1).setCellValue("TestCase");
                    latestRow.createCell(2).setCellValue("Result");
                    break;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return sheet;
    }
}
