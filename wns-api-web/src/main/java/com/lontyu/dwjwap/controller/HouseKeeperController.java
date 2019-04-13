package com.lontyu.dwjwap.controller;

import com.alibaba.fastjson.JSONObject;
import com.lontyu.dwjwap.config.GlobalsConfig;
import com.lontyu.dwjwap.constants.WXMessageConstant;
import com.lontyu.dwjwap.service.WxAdvService;
import com.lontyu.dwjwap.utils.MessageFormatUtil;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Map;

/**
 * 功能说明: 微信请求域名验证控制器
 */
@Controller
@RequestMapping("/wx")
public class HouseKeeperController {

    private static final Logger logger = LoggerFactory.getLogger(HouseKeeperController.class);

    @Autowired
    private GlobalsConfig globalsConfig;

    @Autowired
    private WxAdvService wxAdvService;

    /**
     *  微信验证
     * @param request
     * @param response
     */
    @GetMapping("/housekeeper")
    @ResponseBody
    public String weixinCheck(HttpServletRequest request, HttpServletResponse response) {
        try {
            logger.info("接收到微信端发来的验证请求....");
            // 开发者提交信息后，微信服务器将发送GET请求到填写的服务器地址URL上，GET请求携带参数
            String signature = request.getParameter("signature");// 微信加密签名（token、timestamp、nonce。）
            String timestamp = request.getParameter("timestamp");// 时间戳
            String nonce = request.getParameter("nonce");// 随机数
            String echostr = request.getParameter("echostr");// 随机字符串
            // 将token、timestamp、nonce三个参数进行字典序排序
            String[] params = new String[]{globalsConfig.getAuthToken(), timestamp, nonce};
            Arrays.sort(params);
            // 将三个参数字符串拼接成一个字符串进行sha1加密
            String clearText = params[0] + params[1] + params[2];
            String algorithm = "SHA-1";
            String sign = new String(
                    Hex.encodeHex(MessageDigest.getInstance(algorithm).digest((clearText).getBytes()), true));
            // 开发者获得加密后的字符串可与signature对比，标识该请求来源于微信
            logger.info("响应微信端发来的验证请求，请求参数：signature={},timestamp={},nonce={},echostr={},返回的签名值：sign={}", signature, timestamp, nonce, echostr, sign);
            if (signature.equals(sign)) {
               return echostr;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     *  处理微信消息
     * @param request
     * @param response
     * @return
     */
    @PostMapping("/housekeeper")
    @ResponseBody
    public String dealMessage(HttpServletRequest request,HttpServletResponse response){
        String message = null;
        try{
            Map<String, String> map = MessageFormatUtil.xmlToMap(request);
            String fromUserName = map.get("FromUserName");//公众号
            String toUserName = map.get("ToUserName");//粉丝号
            String msgType = map.get("MsgType");//发送的消息类型[比如 文字,图片,语音。。。]
            String content = map.get("Content");//发送的消息内容
            logger.info("接收到微信用户：userName={}发来的消息，消息内容：content={}", toUserName, content);

            if(WXMessageConstant.MESSAGE_TEXT.equals(msgType)){ // 文本消息
                message = MessageFormatUtil.initText(toUserName,fromUserName,globalsConfig.getReplyContent());
            }else if (WXMessageConstant.MESSAGE_EVENT.equals(msgType)){ // 关注、取消事件
                String eventType = map.get("Event");//获取是关注还是取消
                if(WXMessageConstant.MESSAGE_SUBSCRIBE.equals(eventType)){ // 关注事件
                    String eventBody = JSONObject.toJSONString(map);
                    wxAdvService.appendToQueue(eventBody);
                    message = MessageFormatUtil.initText(toUserName,fromUserName,globalsConfig.getSubscrbeContent());
                }
            }else{ // 其他
                message = MessageFormatUtil.initText(toUserName,fromUserName,globalsConfig.getReplyContent());
            }

        } catch (Exception e){
            logger.error("解析微信消息出错，错误信息：msg={}",e.getMessage());
        }

        return message;
    }

}
