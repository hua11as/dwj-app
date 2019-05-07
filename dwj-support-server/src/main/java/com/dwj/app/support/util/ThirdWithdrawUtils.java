package com.dwj.app.support.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.DigestUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author as
 * @desc
 * @sinse 2019/3/28
 */
public class ThirdWithdrawUtils {

    public static String thirdWithdrawTest() {
        String source = "https://www.baidu.com";
        Map<String, Object> postData = new HashMap<>(10);
//        postData.put("mid", "1328");
        postData.put("jine", "1");
        postData.put("openid", "oFl4V07FQx3itqKjwvmTI-HstEHA");
        postData.put("tixianid", "10000000000");
        String s = "1328" + "1" + "oFl4V07FQx3itqKjwvmTI-HstEHA" + "oweisawkjjhdskjkbxzbfgdsf";
        String mkey = DigestUtils.md5DigestAsHex(s.getBytes());
        postData.put("lx", "999");
        postData.put("mkey", mkey);

        String str = "";
        try {
            URI uri = new URI("http://jfcms12.com/jieru.php");

            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(uri);
//            httpPost.setHeader("content-type", ContentType.MULTIPART_FORM_DATA.getMimeType());
//            ObjectMapper objectMapper = new ObjectMapper();
//            HttpEntity entity = new StringEntity(objectMapper.writeValueAsString(postData), "UTF-8");
//            httpPost.setEntity(entity);
            CloseableHttpResponse response = httpclient.execute(httpPost);
            str = EntityUtils.toString(response.getEntity(), "utf-8");
            System.out.println(str);
            System.out.println(unicodeToString(str));
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        return str;
    }

    private static Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");

    public static String unicodeToString(String str) {

        Matcher matcher = pattern.matcher(str);
        char ch;
        while (matcher.find()) {
            //group 6728
            String group = matcher.group(2);
            //ch:'木' 26408
            ch = (char) Integer.parseInt(group, 16);
            //group1 \u6728
            String group1 = matcher.group(1);
            str = str.replace(group1, ch + "");
        }
        return str;
    }

    public static void main(String[] args) {
        thirdWithdrawTest();
//        System.out.println(ContentType.MULTIPART_FORM_DATA.getMimeType());
    }
}
