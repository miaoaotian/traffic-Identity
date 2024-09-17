package com.miaoaotian.smallcar.common;

import com.miaoaotian.smallcar.pojo.AllActionVO;
import com.miaoaotian.smallcar.pojo.AllSignalVO;
import com.miaoaotian.smallcar.pojo.DriveTypeActionVO;
import com.miaoaotian.smallcar.service.AllService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.util.List;

@Component
public class WebSocketEventListener {
    @Autowired
    private final SimpMessagingTemplate messagingTemplate;
    @Autowired
    private final AllService allService;

    public WebSocketEventListener(SimpMessagingTemplate messagingTemplate, AllService allService) {
        this.messagingTemplate = messagingTemplate;
        this.allService = allService;
    }

    // 当有新的WebSocket连接时触发
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        System.out.println("Received a new web socket connection");
    }

    // 当客户端订阅地址时触发
    @EventListener
    public void handleWebSocketSubscribeListener(SessionSubscribeEvent event) {
        SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.wrap(event.getMessage());
        String destination = headers.getDestination();

        if ("/topic/driveActions".equals(destination)) {
            List<DriveTypeActionVO> actions = allService.getDriveTypeActions();
            messagingTemplate.convertAndSend(destination, actions);
        } else if ("/topic/Actions".equals(destination)) {
            List<AllActionVO> actions = allService.getAllActions();
            messagingTemplate.convertAndSend(destination, actions);
        } else if ("/topic/trafficSignal".equals(destination)) {
            List<AllSignalVO> actions = allService.getAllSignals();
            messagingTemplate.convertAndSend(destination, actions);
        }
    }
}

