package com.dwj.app.support.util;

import com.fasterxml.jackson.databind.ObjectMapper;
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
        postData.put("mid", "1328");
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

            SimpleClientHttpRequestFactory schr = new SimpleClientHttpRequestFactory();

            ClientHttpRequest chr = schr.createRequest(uri, HttpMethod.POST);
            ObjectMapper mapper = new ObjectMapper();
            chr.getBody().write(mapper.writeValueAsString(postData).getBytes());

            ClientHttpResponse res = chr.execute();

            InputStream is = res.getBody();

            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);

            while ((str = br.readLine()) != null) {
                System.out.println(str);
                System.out.println(unicodeToString(str));
            }

        } catch (URISyntaxException | IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        return str;
    }

    public static String unicodeToString(String str) {
        Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
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
}
