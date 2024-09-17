package com.miaoaotian.smallcar.task;

import com.miaoaotian.smallcar.pojo.AllActionVO;
import com.miaoaotian.smallcar.pojo.AllSignalVO;
import com.miaoaotian.smallcar.pojo.DriveTypeActionVO;
import com.miaoaotian.smallcar.service.AllService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SchedulePushTask {
    @Autowired
    private AllService allService;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @RabbitListener(queues = "drive_type_queue")
    public void sendDriveTypeActions() {
        List<DriveTypeActionVO> actions = allService.getDriveTypeActions();
        System.out.println(actions);
        messagingTemplate.convertAndSend("/topic/driveActions", actions);
    }
    @RabbitListener(queues = "objects_queue")
    public void broadcastActions() {
        List<AllActionVO> actions = allService.getAllActions();
        System.out.println(actions);
        messagingTemplate.convertAndSend("/topic/Actions", actions);
    }
    @RabbitListener(queues = "traffic_signal_queue")
    public void sendSignals() {
        List<AllSignalVO> actions = allService.getAllSignals();
        messagingTemplate.convertAndSend("/topic/trafficSignal", actions);
    }
//    @Scheduled(fixedRate = 1000)
}
