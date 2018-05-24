package helper;

import net.sourceforge.tess4j.TesseractException;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.message.BufferedHeader;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;


/**
 * Created by zhangyang33 on 2018/3/12.
 */
public class Login {
    Map<String, String> pageParam = new HashMap<>();

    public Login() throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet method = new HttpGet("https://passport.jd.com/new/login.aspx");
        String responseContent = EntityUtils.toString(httpClient.execute(method).getEntity());

        Document doc = Jsoup.parse(responseContent);//解析HTML字符串返回一个Document实现
        pageParam.put("uuid", doc.getElementById("uuid").val());
        pageParam.put("eid", doc.getElementById("eid").val());
        pageParam.put("fp", doc.getElementById("sessionId").val());
        pageParam.put("_t", doc.getElementById("token").val());
        pageParam.put("loginType", doc.getElementById("loginType").val());
        pageParam.put("pubKey", doc.getElementById("pubKey").val());
        pageParam.put("sa_token", doc.getElementById("sa_token").val());
    }

    public String getVerifyCode() throws IOException, TesseractException {
        String imgUrl = String.format("https://authcode.jd.com/verify/image?a=1&acid=%s&uid=%s&yys=%s",
                pageParam.get("uuid"),
                pageParam.get("uuid"),
                String.valueOf(new Date().getTime()));

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet method = new HttpGet(imgUrl);
        method.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.84 Safari/537.36");
        method.setHeader("Host", "authcode.jd.com");
        method.setHeader("Accept", "image/webp,image/apng,image/*,*/*;q=0.8");
        method.setHeader("Accept-Language", "zh-CN,zh;q=0.9");
        method.setHeader("Accept-Encoding", "gzip, deflate, br");
        method.setHeader("Connection", "keep-alive");
        method.setHeader("Referer", "https://passport.jd.com/new/login.aspx");
        HttpResponse response = httpClient.execute(method);

        byte[] data = new byte[1024];
        int len = 0;
        InputStream inputStream = response.getEntity().getContent();
        FileOutputStream fileOutputStream = new FileOutputStream("src/main/java/httpTestData/verifyCode.jpeg");

        while ((len = inputStream.read(data)) != -1) {
            fileOutputStream.write(data, 0, len);
        }
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                throw e;
            }
        }
        if (fileOutputStream != null) {
            try {
                fileOutputStream.close();
            } catch (IOException e) {
                throw e;
            }
        }
//        File imageFile = new File("src/main/java/httpTestData/verifyCode.jpeg");
//        Tesseract tesseract = new Tesseract();
//        tesseract.setDatapath("D:/tessdata");
//        tesseract.setLanguage("fontyp");
//        try {
//            Thread.sleep(1500);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        String verifyCode = tesseract.doOCR(imageFile);
        String verifyCode = "";
        return verifyCode;
    }

    public void getCookie() throws JSONException, IOException, TesseractException {
        HttpClientContext context = HttpClientContext.create();
        String loginUrl = "https://passport.jd.com/uc/loginService?uuid=" + pageParam.get("uuid") + "&r=0.6335209684602039&version=2015";
        CookieStore cookieStore = new BasicCookieStore();
        CloseableHttpClient httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
//        HttpClient httpClient = new HttpClient();
//        DefaultHttpClient defaultHttpClient = new DefaultHttpClient();

        HttpPost method = new HttpPost(loginUrl);
//        StringEntity entity = new StringEntity(body.toString(), "utf-8");
//        method.setEntity(entity);
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("uuid", pageParam.get("uuid")));
        nvps.add(new BasicNameValuePair("eid", pageParam.get("eid")));
        nvps.add(new BasicNameValuePair("fp", pageParam.get("fp")));
        nvps.add(new BasicNameValuePair("_t", pageParam.get("_t")));
        nvps.add(new BasicNameValuePair("loginType", pageParam.get("loginType")));
        nvps.add(new BasicNameValuePair("sa_token", pageParam.get("sa_token")));
        nvps.add(new BasicNameValuePair("pubKey", pageParam.get("pubKey")));
        nvps.add(new BasicNameValuePair("loginname", "13366169583"));
        nvps.add(new BasicNameValuePair("nloginpwd", "zhangyang@163.com"));
        nvps.add(new BasicNameValuePair("chkRememberMe", ""));
//        nvps.add(new BasicNameValuePair("authcode", getVerifyCode()));
//        nvps.add(new BasicNameValuePair("authcode", "FVEU"));
        System.out.println("输入验证码");
        Scanner in = new Scanner(System.in);
        String code = in.next();
        nvps.add(new BasicNameValuePair("authcode", code));
        method.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
        method.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.84 Safari/537.36");
        method.setHeader("Host","authcode.jd.com");
        method.setHeader("X-Requested-With","XMLHttpRequest");
        method.setHeader("Connection","keep-alive");
        method.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");


        HttpResponse response = httpClient.execute(method);

        BufferedHeader locationHeader =  (BufferedHeader) response.getFirstHeader("Location");

        String result = EntityUtils.toString(response.getEntity());

//        CookieStore cookieStore= httpClient.getCookieStore();
        String JSESSIONID = null;
        String cookie_user = null;
        cookieStore = context.getCookieStore();
        List<Cookie> cookies = cookieStore.getCookies();
        for (int i = 0; i < cookies.size(); i++) {
            if (cookies.get(i).getName().equals("JSESSIONID")) {
                JSESSIONID = cookies.get(i).getValue();
            }
            if (cookies.get(i).getName().equals("cookie_user")) {
                cookie_user = cookies.get(i).getValue();
            }
        }
        if (cookie_user != null) {
//            String result = JSESSIONID;
        }

        int i=0;
    }

    public static void main(String[] args) throws IOException, TesseractException, JSONException {
            Login login = new Login();
            login.getVerifyCode();
            login.getCookie();
    }
}
