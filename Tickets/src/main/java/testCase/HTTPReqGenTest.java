package testCase;

import excelUtil.*;
import httpUtil.HTTPReqGenerate;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONCompare;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.JSONCompareResult;
import org.testng.Assert;
import org.testng.ITest;
import org.testng.ITestContext;
import org.testng.annotations.*;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Class that perform test cases
 * Created by zhangyang33 on 2017/9/18.
 */
public class HTTPReqGenTest implements ITest {
    private HttpResponse response;
    private DataReader myInputData;
    private DataReader myBaselineData;
    private Map<String,String> responseList = new HashMap<String, String>();

    public String getTestName() {
        return "API Test";
    }

    String filePath = "";
    XSSFWorkbook wb = null;
    XSSFSheet inputSheet = null;
    XSSFSheet baselineSheet = null;
    XSSFSheet outputSheet = null;
    XSSFSheet comparsionSheet = null;
    XSSFSheet resultSheet = null;

    private double totalcase = 0;
    private double failedcase = 0;
    private String startTime = "";
    private String endTime = "";

    /**
     * Run this method before every test case. This method is used to collect test data from excel, excel path is
     * configured in testng xml file.
     * @param path  Configured excel path in testng xml file
     */
    @BeforeTest
    @Parameters("workBook")
    public void setup(String path) {
        filePath = path;
        try {
            FileInputStream testData = new FileInputStream(filePath);
            wb = new XSSFWorkbook(testData);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        inputSheet = wb.getSheet("Input");
        baselineSheet = wb.getSheet("Baseline");

        SheetUtils.removeSheetByName(wb, "Output");
        SheetUtils.removeSheetByName(wb, "Comparison");
        SheetUtils.removeSheetByName(wb, "Result");
        outputSheet = SheetUtils.createSheetByName(wb, "Output");
        comparsionSheet = SheetUtils.createSheetByName(wb, "Comparison");
        resultSheet = SheetUtils.createSheetByName(wb, "Result");

        try{
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            wb.write(fileOutputStream);
            fileOutputStream.close();
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }

        SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        startTime = sf.format(new Date());
    }

    /**
     * Switch map to [][] type as data provider to test
     * @param context
     * @return    [][] test data
     */
    @DataProvider(name = "WorkBookData")
    protected Iterator<Object[]> testProvider(ITestContext context) {

        List<Object[]> test_IDs = new ArrayList<>();

        myInputData = new DataReader(inputSheet, true, true, 0);
        Map<String, RecordHandler> myInput = myInputData.get_map();

        // sort map in order so that test cases ran in a fixed order
        List<Map.Entry<String, RecordHandler>> sortmap = Utils.sortmap(myInput);
        for (Map.Entry<String, RecordHandler> entry : sortmap) {

            String test_ID =  entry.getKey();
            String test_case = entry.getValue().get("Sub_module");
            if (!test_ID.equals("") && !test_case.equals("") && entry.getValue().get("IsRun").equals("Y")) {
                test_IDs.add(new Object[] { test_ID, test_case });
            }
            totalcase++;
        }

        myBaselineData = new DataReader(baselineSheet, true, true, 0);
        return test_IDs.iterator();
    }

    /**
     * Run test case and get test result
     * @param ID                Test case id
     * @param test_case         Test case name
     * @throws IOException      Test case exception
     */
    @Test(dataProvider = "WorkBookData", description = "ReqGenTest")
    public void api_test(String ID, String test_case) throws IOException, InterruptedException {

        HTTPReqGenerate httpReqGenerate = new HTTPReqGenerate();

        try {
            response = httpReqGenerate.performRequest(myInputData.get_record(ID),responseList);
        } catch (Exception e) {
            DataWriter.writeData(outputSheet, e.getMessage(), ID, test_case);
            Assert.fail("Problem using HTTPRequestGenerator to generate response: " + e.getMessage());
        }

        String responseContent = EntityUtils.toString(response.getEntity());
        String baseline_operation = myBaselineData.get_record(ID).get("Operation");
        String baseline_message = myBaselineData.get_record(ID).get("Response");

        responseList.put(ID,responseContent);

        if (Integer.parseInt(myInputData.get_record(ID).get("WaitTime(s)")) != 0){
            Thread.sleep(Integer.parseInt(myInputData.get_record(ID).get("WaitTime(s)")+"000"));
        }

        if (response.getStatusLine().getStatusCode() == 200)
            try {
                DataWriter.writeData(outputSheet, responseContent, ID, test_case);
                boolean containResult = false;
                if(baseline_operation.equals("contain")){
                    if (baseline_message.contains("*")){
                        String[] expectedAll = baseline_message.split("\\*");
                        for (String expected : expectedAll){
                            if (responseContent.contains(expected)) {
                                containResult = true;
                            } else {
                                containResult = false;
                                break;
                            }
                        }
                    } else {
                        containResult = responseContent.contains(baseline_message);
                    }

                    if (!containResult) {
                        DataWriter.writeData(comparsionSheet, baseline_message, responseContent, ID, test_case, false);
                        DataWriter.writeData(wb, resultSheet, "false", ID, test_case);
                        DataWriter.writeData(wb, outputSheet);
                        failedcase++;
                        Assert.fail("The response doesn't contain your expected result.");
                    } else {
                        DataWriter.writeData(wb, resultSheet, "true", ID, test_case);
                        Assert.assertTrue(true);
                    }
                }
                else if(baseline_operation.equals("equal")){
                    JSONCompareResult result = JSONCompare.compareJSON(baseline_message, responseContent, JSONCompareMode.NON_EXTENSIBLE);
                    if (!result.passed()) {
                        DataWriter.writeData(comparsionSheet, result, ID, test_case);
                        DataWriter.writeData(wb, resultSheet, "false", ID, test_case);
                        DataWriter.writeData(wb, outputSheet);
                        failedcase++;
                        Assert.fail("The response content doesn't equal to your expected result.");
                    } else {
                        DataWriter.writeData(wb, resultSheet, "true", ID, test_case);
                        Assert.assertTrue(true);
                    }
                }
                else {
                    DataWriter.writeData(wb, outputSheet);
                    Assert.fail("The assert key is doesn't exist,please contact admin to add this key.");
                }
            } catch (JSONException e) {
                DataWriter.writeData(comparsionSheet, "", "Problem to assert Response and baseline messages: "+e.getMessage(), ID, test_case, true);
                DataWriter.writeData(wb, resultSheet, "error", ID, test_case);
                failedcase++;
                Assert.fail(String.format("Problem to assert Response and baseline messages: %s", e.getMessage()));
            }
        else {
            DataWriter.writeData(outputSheet, responseContent, ID, test_case);

            if(baseline_operation.equals("contain")){
                if (responseContent.contains(baseline_message)) {
                    DataWriter.writeData(wb, resultSheet, "true", ID, test_case);
                    Assert.assertTrue(true);
                } else {
                    DataWriter.writeData(comparsionSheet, baseline_message, responseContent, ID, test_case, false);
                    DataWriter.writeData(wb, resultSheet, "false", ID, test_case);
                    DataWriter.writeData(wb, outputSheet);
                    failedcase++;
                    Assert.fail("The response doesn't contain your expected result.");
                }
            }else if(baseline_operation.equals("equal")){
                if (baseline_message.equals(responseContent)) {
                    DataWriter.writeData(wb, resultSheet, "true", ID, test_case);
                    Assert.assertTrue(true);
                } else {
                    DataWriter.writeData(comparsionSheet, baseline_message, responseContent, ID, test_case, false);
                    DataWriter.writeData(wb, resultSheet, "false", ID, test_case);
                    DataWriter.writeData(wb, outputSheet);
                    failedcase++;
                    Assert.fail("The response content doesn't equal to your expected result.");
                }
            }
            else {
                DataWriter.writeData(wb, resultSheet, "false", ID, test_case);
                Assert.fail("The assert key is doesn't exist,please contact admin to add this key.");
            }
        }
    }

    /**
     * Run this method after run every test case, write all the test result to excel.
     * @throws FileNotFoundException
     */
    @AfterTest
    public void teardown() {
        SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        endTime = sf.format(new Date());
        DataWriter.writeData(resultSheet, totalcase, failedcase, startTime, endTime);

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            wb.write(fileOutputStream);
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // result.renameTo(new File(String.format("test-output-%s", String.valueOf(System.currentTimeMillis()))));

        File result = new File("test-output");
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd HHmmss");
        String dateString = formatter.format(currentTime);
        result.renameTo(new File(String.format("test-output-%s", dateString)));

        File resultJenkins = new File("target/surefire-reports");
        resultJenkins.renameTo(new File(String.format("target/surefire-reports-%s", dateString)));
    }
}
