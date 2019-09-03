package com.hy.boke.config;

import com.hy.boke.po.Common;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQSender {
    @Autowired
    private AmqpTemplate rabbitTemplate;
    public void send(Common common){
        rabbitTemplate.convertAndSend("common", common);
    }
}
