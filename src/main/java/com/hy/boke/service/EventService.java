package com.hy.boke.service;

import com.hy.boke.po.Event;
import com.hy.boke.po.Msg;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.pager.Pager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventService extends BaseService{
    @Autowired
    private Dao dao;
    /**
     * 根据用户名获取未读消息消息的数量
     */
    public Msg<Integer> getUnReadCount(String userName){
        return getCount(userName, 1);
    }
    /**
     * 根据用户名获取已读消息消息的数量
     */
    public Msg<Integer> getReadCount(String userName){
        return getCount(userName, 0);
    }
    public Msg<Integer> getCount(String userName, int method){
        Msg<Integer> msg = new Msg<>();
        if (checkNotNull(userName)){

            List<Event> query = dao.query(Event.class, Cnd.where("toUserName", "=", userName)
                    .and("status", "=", method));
            msg.setSuccess(true);
            msg.setData(query.size());
            return msg;
        } else {
            msg.setSuccess(false);
            msg.setName("当前未登陆");
            return msg;
        }
    }


    /**
     * 根据用户名获取未查看消息
     */
    public Msg<Pager> getEvent(String userName, Integer pageCount){
        Msg<Pager> msg = new Msg<>();
        if (checkNotNull(userName)){
            Pager pager = dao.createPager(pageCount, 5);
            List<Event> query = dao.query(Event.class, Cnd.where("toUserName", "=", userName)
                    .and("status", "=", 1).desc("createTime"), pager);
            query.forEach(x -> {
                Long createTime = Long.valueOf(x.getTime());
                x.setTime(getFormatterTimeFromLong(createTime));
            });
            pager.setRecordCount(getUnReadCount(userName).getData());
            msg.setSuccess(true);
            msg.setData(pager);
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
     * 根据用户名获取所有收到的消息
     */
    public Msg<Pager> getAllPullEvent(String userName, Integer pageCount){
        return getAllEvent(userName, pageCount, "toUserName");
    }
    /**
     * 根据用户名获取所发表的消息，通过时间排序desc,分页
     */
    public Msg<Pager> getAllPushEvent(String userName, Integer pageCount){
        return getAllEvent(userName, pageCount, "fromUserName");
    }
    public Msg<Pager> getAllEvent(String userName, Integer pageCount, String method){
        Msg<Pager> msg = new Msg<>();
        if (pageCount < 0){
            pageCount = 0;
        }
        if (checkNotNull(userName)){
            Pager pager = dao.createPager(pageCount, 5);
            List<Event> query = dao.query(Event.class, Cnd.where(method, "=", userName)
                    .desc("createTime"), pager);

            query.forEach(x -> {
                Long createTime = Long.valueOf(x.getTime());
                x.setTime(getFormatterTimeFromLong(createTime));
            });
            pager.setRecordCount(dao.query(Event.class, Cnd.where(method, "=", userName)
                    .desc("createTime")).size());
            msg.setData(pager);
            msg.setSuccess(true);
            return msg;
        } else {
            msg.setSuccess(false);
            msg.setName("当前未登陆");
            return msg;
        }
    }
}
