package com.miaoaotian.smallcar.config;
import com.fasterxml.jackson.core.type.TypeReference;
import com.miaoaotian.smallcar.mapper.AllMapper;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import java.util.HashMap;
import java.util.Map;
@Configuration
@IntegrationComponentScan
public class MqttConfig {
    @Bean
    public DefaultMqttPahoClientFactory mqttClientFactory() {
        // 创建Paho MQTT连接选项
        MqttConnectOptions connectOptions = new MqttConnectOptions();
        connectOptions.setServerURIs(new String[] { "tcp://118.178.235.155:1883" });
        connectOptions.setUserName("admin");
        connectOptions.setPassword("qixinkai123".toCharArray());
        connectOptions.setCleanSession(false);
        connectOptions.setKeepAliveInterval(60); // 60秒

        // 创建客户端工厂并设置连接选项
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        factory.setConnectionOptions(connectOptions);

        // 设置持久化存储，这里使用内存持久化
        factory.setPersistence(new MemoryPersistence());
        return factory;
    }

    // 发送消息的通道
    @Bean
    public MessageChannel mqttOutboundChannel() {
        return new DirectChannel();
    }

    // 接收消息的通道
    @Bean
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }

    // 消息处理器，用于将消息发送到MQTT
    @Bean
    @ServiceActivator(inputChannel = "mqttOutboundChannel")
    public MessageHandler mqttOutbound() {
        MqttPahoMessageHandler messageHandler =
                new MqttPahoMessageHandler("mqtt_asdjifqn", mqttClientFactory());
        messageHandler.setAsync(true);
        messageHandler.setDefaultTopic("/start/stop");
        return messageHandler;
    }

    // 驱动消息适配器，用于从MQTT接收消息
    @Bean
    public MqttPahoMessageDrivenChannelAdapter inbound() {
        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter("mqtt_asdjifq2", mqttClientFactory(),
                        "/send","/start/stop");
        adapter.setOutputChannel(mqttInputChannel());
        return adapter;
    }
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public void processMessage(Message<?> message) {
        String topic = message.getHeaders().get("mqtt_receivedTopic", String.class);
        switch (topic) {
            case "/start/stop":
                handleStartStop(message);
                break;
            case "/send":
                handleSend(message);
                break;
            default:
                handleDefault(message);
                break;
        }
    }

    private void handleStartStop(Message<?> message) {
        // 处理来自 "/start/stop" 主题的消息
        System.out.println("处理 /start/stop 主题的消息: " + message.getPayload());
    }
    @Autowired
    private AllMapper allMapper;
    private final Map<String, Integer> signalCounters = new HashMap<>();

    private void handleSend(Message<?> message) {
        String payload = message.getPayload().toString();
        System.out.println(payload);
        Map<String, Integer> currentSignals = extractSignalsFromPayload(payload);
        System.out.println(111111111);
        signalCounters.keySet().retainAll(currentSignals.keySet());
        System.out.println(currentSignals);
        currentSignals.forEach((signal, value) -> {
            signalCounters.put(signal, signalCounters.getOrDefault(signal, 0) + 1);

            if (signalCounters.get(signal) == 1) {
                saveSignalToDatabase(signal, value);
                System.out.println("New traffic signal processed: " + signal + " with value: " + value);
            }
        });

        new HashMap<>(signalCounters).forEach((signal, count) -> {
            if (!currentSignals.containsKey(signal)) {
                signalCounters.put(signal, 0);
            }
        });
    }
    private Map<String, Integer> extractSignalsFromPayload(String payload) {
        return convertJsonToMap(payload);
    }
    private void saveSignalToDatabase(String signal, Integer value) {
        System.out.println("Saving signal to database: " + signal + " with value: " + value);
        switch (signal) {
            case "red", "stop":
                allMapper.insertDriveType(0);
                allMapper.insertObject(4);
                break;
            case "yellow":
                allMapper.insertObject(3);
                break;
            case "green":
                allMapper.insertObject(0);
                allMapper.insertDriveType(1);
                break;
            case "turn_left":
                allMapper.insertObject(1);
                break;
            case "turn_right":
                allMapper.insertObject(2);
            default:
                break;
        }
        allMapper.insertTrafficSignal(signal);
    }
    private Map<String, Integer> convertJsonToMap(String json) {
        Map<String, Integer> map = new HashMap<>();
        String stripped = json.trim().substring(1, json.length() - 1);
        String[] keyValuePairs = stripped.split(",");
        for (String pair : keyValuePairs) {
            String[] entry = pair.split(":");
            String key = entry[0].trim().replace("\"", "");
            Integer value = Integer.parseInt(entry[1].trim());
            map.put(key, value);
        }
        return map;
    }


    private void handleDefault(Message<?> message) {
        // 处理其他未指定主题的消息
        System.out.println("处理其他主题的消息: " + message.getPayload());
    }


}
