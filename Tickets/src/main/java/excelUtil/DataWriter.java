package excelUtil;

import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.*;
import org.skyscreamer.jsonassert.JSONCompareResult;

import java.text.DecimalFormat;

/**
 * Class that write data to XSSF sheet
 * Created by zhangyang33 on 2017/9/18.
 */
public class DataWriter {

    /**
     * This method used to write data to sheet by id and responseResult
     * @param sheet             Given excel sheet, main used to write output sheet
     * @param responseResult    response content to excel Response column
     * @param id                test case id to excel ID column
     * @param testCase          test case name to excel TestCase column
     */
    public static void writeData(XSSFSheet sheet, String responseResult, String id, String testCase){
        XSSFRow latestRow = sheet.createRow(sheet.getLastRowNum()+1);
        latestRow.createCell(0).setCellValue(id);
        latestRow.createCell(1).setCellValue(testCase);
        latestRow.createCell(2).setCellValue(responseResult);
    }

    /**
     * This method used to write data to sheet by id and responseResult
     * @param sheet             Given excel sheet, main used to write comparison sheet
     * @param exceptedResult    excepted result in Baseline sheet
     * @param actualResult      responsed actual result
     * @param id                test case id to excel ID column
     * @param testCase          test case name to excel TestCase column
     * @param isException       if is exception, only write exception message, else write expected and actual result
     */
    public static void writeData(XSSFSheet sheet, String exceptedResult, String actualResult, String id, String testCase, boolean isException){
        XSSFRow latestRow = sheet.createRow(sheet.getLastRowNum()+1);
        latestRow.createCell(0).setCellValue(id);
        latestRow.createCell(1).setCellValue(testCase);
        if (isException){
            latestRow.createCell(2).setCellValue("Message");
            latestRow.createCell(3).setCellValue(actualResult);
        }else {
            latestRow.createCell(2).setCellValue("Actual");
            latestRow.createCell(3).setCellValue(actualResult);

            latestRow = sheet.createRow(sheet.getLastRowNum()+1);
            latestRow.createCell(2).setCellValue("Expect");
            latestRow.createCell(3).setCellValue(exceptedResult);
        }

    }

    /**
     * This method used to write data to sheet by id and responseResult
     * @param sheet             Given excel sheet, main used to write output sheet
     * @param jsonCompareResult json conmare result to excel Response column
     * @param id                test case id to excel ID column
     * @param testCase          test case name to excel TestCase column
     */
    public static void writeData(XSSFSheet sheet, JSONCompareResult jsonCompareResult, String id, String testCase){
        XSSFRow latestRow = sheet.createRow(sheet.getLastRowNum()+1);
        latestRow.createCell(0).setCellValue(id);
        latestRow.createCell(1).setCellValue(testCase);
        latestRow.createCell(2).setCellValue("Message");
        latestRow.createCell(3).setCellValue(jsonCompareResult.getMessage());
    }

    /**
     * This method used to write data to sheet by id and responseResult
     * @param wb                Given excel workbook
     * @param sheet             Given excel sheet
     * @param result            result to excel
     * @param id                test case id to excel ID column
     * @param testCase          test case name to excel TestCase column
     */
    public static void writeData(XSSFWorkbook wb, XSSFSheet sheet, String result, String id, String testCase){
        XSSFRow latestRow = sheet.createRow(sheet.getLastRowNum()+1);
        latestRow.createCell(0).setCellValue(id);
        latestRow.createCell(1).setCellValue(testCase);
        XSSFCell resultCell= null;
        XSSFCellStyle style =  wb.createCellStyle();
        style.setFillForegroundColor(IndexedColors.RED.getIndex());

        switch (result){
            case "false":
                resultCell = latestRow.createCell(2);
                resultCell.setCellValue("Failed");
                resultCell.setCellStyle(style);
                break;
            case "true":
                resultCell = latestRow.createCell(2);
                resultCell.setCellValue("Passed");
                break;
            default:
                resultCell = latestRow.createCell(2);
                resultCell.setCellValue(result);
        }
    }

    /**
     * This method used to write data to sheet by id and responseResult
     * @param sheet             Given excel sheet, main used to write Result sheet
     * @param totalcase         all the test cases to run
     * @param failedcase        failed test cases after run
     * @param startTime         time started to run test cases
     * @param endTime           time after run the last test cases
     */
    public static void writeData(XSSFSheet sheet, double totalcase, double failedcase, String startTime, String endTime){
        DecimalFormat failRateFormat = new DecimalFormat("##.00%");

        XSSFRow latestRow = sheet.createRow(sheet.getLastRowNum()+1);
        latestRow.createCell(1).setCellValue("Pass Percentage:");
        latestRow.createCell(2).setCellValue("'"+failRateFormat.format((totalcase-failedcase)/totalcase));

        latestRow = sheet.createRow(sheet.getLastRowNum()+1);
        latestRow.createCell(1).setCellValue("Start Time:");
        latestRow.createCell(2).setCellValue(startTime);

        latestRow = sheet.createRow(sheet.getLastRowNum()+1);
        latestRow.createCell(1).setCellValue("End Time:");
        latestRow.createCell(2).setCellValue(endTime);
    }

    /**
     * This method used to write data to sheet by id and responseResult
     * @param wb                Given excel workbook, main used to write output sheet
     * @param sheet             Given excel sheet,  main used to write output sheet
     */
    public static void writeData(XSSFWorkbook wb, XSSFSheet sheet){
        XSSFCellStyle style = wb.createCellStyle();
        style.setFillForegroundColor(IndexedColors.RED.getIndex());
        XSSFCell colorCell = sheet.getRow(sheet.getLastRowNum()).getCell(2);
        colorCell.setCellStyle(style);
    }
}
