package httpUtil;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.base.Joiner;
import excelUtil.RecordHandler;
import helper.GetJsonKeys;
import org.apache.commons.lang3.RandomUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import net.sf.json.JSONObject;

/**
 * Class that operate http request
 * Created by zhangyang33 on 2017/9/20.
 */
public class HTTPReqGenerate {

    /**
     * Perform all the request
     * @param request        Request parameter
     * @return               HttpResponse
     */
    public static HttpResponse performRequest(RecordHandler request, Map<String,String> dependResponse) throws JSONException {
        String url = request.get("URL").trim();
        String cookie = request.get("Cookie");
        String callType = request.get("Method").toLowerCase();
        String body = request.get("Request_params");
        String contentType = request.get("ContenType");

        GetJsonKeys getJsonKeys = new GetJsonKeys();
        if (!request.get("DependentId").equals("")){
            String dependentParams = request.get("DependentParams");
            String[] dependentParam = dependentParams.split(",");

            List<Map<String, String>> jsonKeys;
            Map<String, String> paramMap = new HashMap<>();

            for(String param : dependentParam){
                if (param.contains("=")){
                    JSONObject jsonObject = new JSONObject(dependResponse.get(request.get("DependentId")));
                    jsonKeys = getJsonKeys.getKeyListByName(param.split("=")[0], jsonObject);
                    paramMap.put(param.split("=")[0], jsonKeys.get(Integer.parseInt(param.split("=")[1])-1).get(param.split("=")[0]));
                }
                else {
                    JSONObject jsonObject = new JSONObject(dependResponse.get(request.get("DependentId")));
                    jsonKeys = getJsonKeys.getKeyListByName(param, jsonObject);
                    int randomParam = RandomUtils.nextInt(0, jsonKeys.size());
                    paramMap.put(param, jsonKeys.get(randomParam).get(param));
                }
            }
            JSONObject originBody = new JSONObject(body);
            for (Map.Entry<String, String> entry : paramMap.entrySet()){
                originBody = getJsonKeys.setKeys(originBody, entry.getKey(), entry.getValue());
            }
            switch (body = originBody.toString()) {
            }
        }

        if (callType.equals("get") && !body.equals("{}")) {
            Map<String, String> paramMap = net.sf.json.JSONObject.fromObject(body);

            for(Map.Entry<String, String> mm : paramMap.entrySet()){
                try {
                    if (mm.getKey().equals("data")){
                        String jsonString = JSON.toJSONString(mm.getValue(), SerializerFeature.PrettyFormat);

                        String encodeValue = URLEncoder.encode(jsonString, "utf-8");
                        paramMap.put(mm.getKey(), encodeValue);
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            String commonParameter = Joiner.on("&").withKeyValueSeparator("=").join(paramMap);
            url = String.format("%s?%s", url, commonParameter);
        }

        HttpResponse result = null;

        switch (callType) {
            case "get":
                result = httpGet(url, cookie);
                break;
            case "post":
                result = httpPost(url, cookie, new JSONObject(body), contentType);
                break;
            case "put":
                result = httpPut(url, cookie, new JSONObject(body));
                break;
            case "delete":
                result = httpDelete(url, cookie);
                break;
            default:
                break;
        }
        return result;
    }

    /**
     * Perform http post request
     * @param url          The url to request
     * @param cookie       Cookie to request
     * @param jsonParam    Json param to request
     * @return             HttpResponse
     */
    public static HttpResponse httpPost(String url, String cookie, JSONObject jsonParam, String contentType) {
        HttpResponse result = null;

        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpPost method = new HttpPost(url);

            method.setHeader("Accept", "application/json");
            if (null != cookie) {
                method.setHeader("Cookie", cookie);
            }

            if (!contentType.equals(""))
            {
                List<org.apache.http.NameValuePair> nvps = new ArrayList<>();
                nvps.add(new BasicNameValuePair("data",jsonParam.toString()));
                method.setHeader("Content-Type", "application/x-www-form-urlencoded");
                method.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
            }
            else
            {
                StringEntity entity = new StringEntity(jsonParam.toString(), "utf-8");
                entity.setContentEncoding("UTF-8");
                method.setEntity(entity);
                method.addHeader("X-Requested-With", "XMLHttpRequest");
                entity.setContentType("application/json");
            }

            result = httpClient.execute(method);
        } catch (IOException e) {
            e.printStackTrace();
            //logger.error("post�����쳣:" + url, e);
        }

        return result;
    }

    /**
     * Perform http get request
     * @param url      The url to request
     * @param cookie   Cookie to request
     * @return         HttpResponse
     */
    public static HttpResponse httpGet(String url, String cookie) {
        HttpResponse response = null;
        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();

            HttpGet method = new HttpGet(url);

            if (null != cookie) {
                method.setHeader("Cookie", cookie);
            }

            response = httpClient.execute(method);

        } catch (IOException e) {
            //logger.error("get�����쳣:" + url, e);
        }

        return response;
    }

    /**
     * Perform http put request
     * @param url          The url to request
     * @param cookie       Cookie to request
     * @param jsonParam    Json param to request
     * @return             HttpResponse
     */
    public static HttpResponse httpPut(String url, String cookie, JSONObject jsonParam){
        HttpResponse result = null;
        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpPut method = new HttpPut(url);

            StringEntity entity = new StringEntity(jsonParam.toString(), "utf-8");
            entity.setContentEncoding("UTF-8");
            entity.setContentType("application/json");
            method.setEntity(entity);
            method.addHeader("X-Requested-With", "XMLHttpRequest");
            method.setHeader("Accept", "json");

            if (null != cookie) {
                method.setHeader("Cookie", cookie);
            }

            result = httpClient.execute(method);
        } catch (IOException e) {
            e.printStackTrace();
            //logger.error("put�����쳣:" + url, e);
        }
        return result;
    }

    /**
     * Perform http delete request
     * @param url       The url to request
     * @param cookie    Cookie to request
     * @return          HttpResponse
     */
    public static HttpResponse httpDelete(String url, String cookie){
        HttpResponse result = null;

        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpDelete method = new HttpDelete(url);

            method.addHeader("X-Requested-With", "XMLHttpRequest");
            method.setHeader("Accept", "json");

            if (null != cookie) {
                method.setHeader("Cookie", cookie);
            }

            result = httpClient.execute(method);
        } catch (IOException e) {
            e.printStackTrace();
            //logger.error("delete�����쳣:" + url, e);
        }
        return result;
    }

}
