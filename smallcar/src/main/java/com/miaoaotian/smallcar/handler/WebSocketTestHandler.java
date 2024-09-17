package com.miaoaotian.smallcar.handler;

import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;


@ServerEndpoint("/websocket/a")
public class WebSocketTestHandler {
    @OnOpen
    public void onOpen(Session session){

    }


    @OnClose
    public void onClose(Session session){
        /*
            do something for onClose
            与当前客户端连接关闭时
         */
    }

    @OnError
    public void onError(Throwable error,Session session) {
    }


    @OnMessage
    public void onMsg(Session session,String message) throws IOException {

    }
}
