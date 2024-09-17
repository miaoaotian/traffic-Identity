package com.miaoaotian.smallcar.controller;

import com.miaoaotian.smallcar.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mqtt")
public class mqttController {
    @Autowired
    private MessageHandler mqttOutbound;

    @PostMapping("/camera_start")
    public Result<String> sendMessage(@RequestBody String message) {
        Message<String> mqttMessage = MessageBuilder.withPayload(message).build();
        mqttOutbound.handleMessage(mqttMessage);
        return Result.success("启动成功");
    }
}
