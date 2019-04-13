package com.lontyu.dwjwap.socket;

import com.alibaba.fastjson.JSONObject;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import com.lontyu.dwjwap.dto.BaseResponse;
import com.lontyu.dwjwap.dto.CurrentInfoVo;
import com.lontyu.dwjwap.dto.req.CurrentInfoReq;
import com.lontyu.dwjwap.service.BjlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.UUID;

@Component
public class MessageEventHandler {
    private static Logger log = LoggerFactory.getLogger(MessageEventHandler.class);
    public static SocketIOServer socketIoServer;
    static ArrayList<UUID> listClient = new ArrayList<>();
    static final int limitSeconds = 60;
    public static final String PRIZE_EVENT="prize_event";
    public static final String  SOCKET_LOG_DESC ="socket io log  ";

    @Autowired
    private BjlService bjlService;

    @Autowired
    public MessageEventHandler(SocketIOServer server) {
        this.socketIoServer = server;
    }

    @OnConnect
    public void onConnect(SocketIOClient client) {
        listClient.add(client.getSessionId());
        log.info(SOCKET_LOG_DESC+"当前连接数："+listClient.size() +" 客户端:" + client.getSessionId() + "已连接");
        BaseResponse<CurrentInfoVo> info = bjlService.currentInfo();
        log.info(SOCKET_LOG_DESC+"开始推送视频信息到 "+client.getSessionId()+ " ： info:"+ JSONObject.toJSONString(info));
        socketIoServer.getClient(client.getSessionId()).sendEvent(PRIZE_EVENT,info);
        log.info(SOCKET_LOG_DESC+"开始推送视频信息到 "+client.getSessionId()+"结束： info:"+ JSONObject.toJSONString(info));
    }

    @OnDisconnect
    public void onDisconnect(SocketIOClient client) {
        log.info(SOCKET_LOG_DESC+"当前连接数："+listClient.size() +" 客户端:" + client.getSessionId() + "已断开连接");
    }

    @OnEvent(value = PRIZE_EVENT)
    public void onEvent(SocketIOClient client, AckRequest request, CurrentInfoReq data) {
        log.info(SOCKET_LOG_DESC+ client.getSessionId()+" 发来消息 用户ID:"+ data.getUserId());
        BaseResponse<CurrentInfoVo> info = bjlService.currentInfo();
        socketIoServer.getClient(client.getSessionId()).sendEvent(PRIZE_EVENT, info);
    }

//    public static void sendBuyLogEvent() {
//        //这里就是向客户端推消息了
//        String dateTime = "123 4546";
//        for (UUID clientId : listClient) {
//            if (socketIoServer.getClient(clientId) == null) continue;
//            MessageInfo rM = new MessageInfo();
//            rM.setMessage("server send message:"+"  begin order" );
//            rM.setUserName(clientId.toString());
//            socketIoServer.getClient(clientId).sendEvent("chatevent", rM);
//        }
//    }

    /**
     * 给所有客户端推送视频信息
     * @param
     */
    public  void broadcastPrizeEvent( ) {
        BaseResponse<CurrentInfoVo> info = bjlService.currentInfo();
        log.info("开始推送视频信息到前端： info:"+ JSONObject.toJSONString(info));
        socketIoServer.getBroadcastOperations().sendEvent(PRIZE_EVENT,info);
        log.info("开始推送视频信息到结束： info:"+ JSONObject.toJSONString(info));
    }

}