package com.hy.boke.service;

import com.hy.boke.dao.CommonRepository;
import com.hy.boke.dao.articleDao.ArticleRepository;
import com.hy.boke.po.Common;
import com.hy.boke.po.Event;
import com.hy.boke.po.Msg;
import org.nutz.dao.*;
import org.nutz.dao.pager.Pager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EventService extends BaseService{
    @Autowired
    private Dao dao;
    @Autowired
    private CommonRepository commonRepository;
    @Autowired
    private ArticleRepository articleRepository;
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
    public Msg<QueryResult> getEvent(String userName, Integer pageCount){
        Msg<QueryResult> msg = new Msg<>();
        if (checkNotNull(userName)){
            Pager pager = dao.createPager(pageCount, 5);
            List<Event> query = dao.query(Event.class, Cnd.where("toUserName", "=", userName)
                    .and("status", "=", 1).desc("time"), pager);
            query.forEach(x -> {
                Long createTime = Long.valueOf(x.getTime());
                x.setTime(getFormatterTimeFromLong(createTime));
                Common by_id = commonRepository.findBy_id(x.getCommonId());
                by_id.setArticleUserName(articleRepository.findBy_id(by_id.getArticleId()).getUserName());
                x.setCommon(by_id);
            });
            pager.setRecordCount(getUnReadCount(userName).getData());
            msg.setSuccess(true);
            msg.setData(new QueryResult(query, pager));
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
    public Msg<QueryResult> getAllPullEvent(String userName, Integer pageCount){
        return getAllEvent(userName, pageCount, "toUserName");
    }
    /**
     * 根据用户名获取所发表的消息，通过时间排序desc,分页
     */
    public Msg<QueryResult> getAllPushEvent(String userName, Integer pageCount){
        return getAllEvent(userName, pageCount, "fromUserName");
    }
    public Msg<QueryResult> getAllEvent(String userName, Integer pageCount, String method){
        Msg<QueryResult> msg = new Msg<>();
        if (pageCount < 0){
            pageCount = 0;
        }
        if (checkNotNull(userName)){
            Pager pager = dao.createPager(pageCount, 5);
            List<Event> query = dao.query(Event.class,
                    Cnd.where(method, "=", userName).desc("time"), pager);
            //未读的消息  用于改变状态
            List<Event> temp = new ArrayList<>();
            query.forEach(x -> {
                Long createTime = Long.valueOf(x.getTime());
                x.setTime(getFormatterTimeFromLong(createTime));
                Common by_id = commonRepository.findBy_id(x.getCommonId());
                by_id.setArticleUserName(articleRepository.findBy_id(by_id.getArticleId()).getUserName());
                x.setCommon(by_id);
                if (method.equals("toUserName") &&  x.getStatus() == 1){
                    //通过克隆获取查询的事件的原型，防止因为更新操作让返回值也改变
                    temp.add((Event) x.clone());
                }
            });
            pager.setRecordCount(dao.query(Event.class, Cnd.where(method, "=", userName)
                    .desc("time")).size());
            msg.setData(new QueryResult(query, pager));
            //让消息改变状态
            if (method.equals("toUserName")){
                temp.forEach(x -> x.setStatus(0));
                dao.update(temp, "^status$");
            }
            msg.setSuccess(true);
            return msg;
        } else {
            msg.setSuccess(false);
            msg.setName("当前未登陆");
            return msg;
        }
    }

    /**
     * 拉取事件、动态
     * @param userName
     * @param type
     * @param pageCount
     * @return
     */
    public Msg<QueryResult> getEvents(String userName, Integer type, Integer pageCount){
        if (!checkNotNull(userName, type, pageCount)){
            return null;
        }
        String method = null;
        if (type == 1){
           return getAllPullEvent(userName, pageCount);
        } else {
            return getAllPushEvent(userName, pageCount);
        }
    }
}
