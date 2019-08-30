package com.hy.boke.service;

import com.hy.boke.po.Event;
import com.hy.boke.po.Msg;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventService extends BaseService{
    @Autowired
    private Dao dao;
    /**
     * 根据用户名获取未查看消息
     */
    public Msg<List<Event>> getEvent(String userName){
        Msg<List<Event>> msg = new Msg<>();
        if (checkNotNull(userName)){
            List<Event> query = dao.query(Event.class, Cnd.where("toUserName", "=", userName).and("status", "=", 1));
            query.forEach(x -> {
                Long createTime = Long.valueOf(x.getTime());
                x.setTime(getFormatterTimeFromLong(createTime));
            });
            msg.setSuccess(true);
            msg.setData(query);
            return msg;
        } else {
            msg.setSuccess(false);
            msg.setName("当前未登陆");
            return msg;
        }
    }
    /**
     * 根据用户名更新未查看消息的状态为已查看
     */
    public Msg<List<Event>> updateEventStatus(String userName){
        Msg<List<Event>> msg = new Msg<>();
        if (checkNotNull(userName)){
            int update = dao.update(Event.class, Chain.make("status", 0), Cnd.where("toUserName", "=", userName));
            if (update > 0){
                msg.setSuccess(true);
                return msg;
            } else {
                msg.setSuccess(false);
                msg.setName("更新失败");
                return msg;
            }
        } else {
            msg.setSuccess(false);
            msg.setName("当前未登陆");
            return msg;
        }
    }
    /**
     * 根据用户名获取所有消息
     */
    public Msg<List<Event>> getAllEvent(String userName){
        Msg<List<Event>> msg = new Msg<>();
        if (checkNotNull(userName)){
            List<Event> query = dao.query(Event.class, Cnd.where("toUserName", "=", userName));
            query.forEach(x -> {
                Long createTime = Long.valueOf(x.getTime());
                x.setTime(getFormatterTimeFromLong(createTime));
            });
            msg.setData(query);
            msg.setSuccess(true);
            return msg;
        } else {
            msg.setSuccess(false);
            msg.setName("当前未登陆");
            return msg;
        }
    }
    /**
     * 根据用户名获取所发表的消息，通过时间排序desc,分页
     */

}
