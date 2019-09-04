package com.hy.boke.config;

import com.hy.boke.po.Common;
import com.hy.boke.po.Event;
import org.nutz.dao.Dao;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * 处理产生的评论的消息通知
 */
@Component
@RabbitListener(queues = "common")
public class RabbiMQReceiver {
    @Autowired
    private Dao dao;
    @RabbitHandler
    public void process(Common common){
        Event event = new Event();
        event.setTime(common.getCreateTime());
        event.setContent(common.getContent());
        event.setFromUserName(common.getUserName());
        event.setToUserName(common.getReplyUser());
        event.setCommonId(common.get_id());
        event.setStatus(1);
        dao.insert(event);
    }
}
