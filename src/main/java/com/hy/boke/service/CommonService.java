package com.hy.boke.service;

import com.hy.boke.config.RabbitMQSender;
import com.hy.boke.dao.CommonRepository;
import com.hy.boke.po.Common;
import com.hy.boke.po.Msg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 评论的业务
 */
@Service
public class CommonService extends BaseService{
    @Autowired
    private CommonRepository commonRepository;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private RabbitMQSender sender;

    private Common initPublishCommon(Common common){
        if (null == common){
            return null;
        }
        common.setType(0);
        common.setCount(0L);
        common.setCreateTime(String.valueOf(System.currentTimeMillis()));
        common.setReplied(false);
        return common;
    }
    private Common initPublishCommonReplied(Common common){
        if (null == common){
            return null;
        }
        common.setType(1);
        common.setCreateTime(String.valueOf(System.currentTimeMillis()));
        common.setReplied(false);
        String commonId = common.getCommonId();
        Common byId = commonRepository.findBy_id(commonId);
        //rootId——链表
        if (null != byId){
            //设置rootId
            //让评论数+1
            Query query = new Query();
            if (byId.getType() == 0){
                query.addCriteria(Criteria.where("_id").is(byId.get_id()));
                common.setRootId(byId.get_id());
            } else {
                query.addCriteria(Criteria.where("_id").is(byId.getRootId()));
                common.setRootId(byId.getRootId());
            }
            Long temp = null == byId.getCount() ? 0 : byId.getCount();
            mongoTemplate.updateFirst(query, Update.update("count", temp + 1), Common.class);
        }
        if (null != commonId){
            //更新评论的状态，让它的replied为true(文章的评论)
            if (byId != null && byId.getType() == 0){
                Query query = new Query();
                query.addCriteria(Criteria.where("_id").is(commonId));
                mongoTemplate.updateFirst(query, Update.update("replied", "true"), Common.class);
            }
        }
        return common;
    }

    /**
     * 获取文章的评论
     * @param _id
     * @param count
     * @return
     */
    public Msg<List<Common>> getCommonsOfArticle(String _id, Integer count){
        Msg<List<Common>> msg = new Msg<>();
        if (checkNotNull(_id)){
            List<Common> allByArticleIdAndTypeOrderByCreateTimeAsc = null;
            if (null == count){
                allByArticleIdAndTypeOrderByCreateTimeAsc = commonRepository.findAllByArticleIdAndTypeOrderByCreateTimeAsc(_id, 0);
            } else {
                count = count > 0 ? count : 0;
                allByArticleIdAndTypeOrderByCreateTimeAsc = commonRepository.findAllByArticleIdAndTypeOrderByCreateTimeAsc(_id, 0, PageRequest.of(count, 10));
            }
            allByArticleIdAndTypeOrderByCreateTimeAsc.forEach(x -> {
                Long createTime = Long.valueOf(x.getCreateTime());
                x.setCreateTime(getFormatterTimeFromLong(createTime));
            });
            msg.setData(allByArticleIdAndTypeOrderByCreateTimeAsc);
            msg.setSuccess(true);
            return msg;
        }
        msg.setSuccess(false);
        return msg;
    }

    /**
     * 评论文章  common: articleId, userName, content, replyUser
     * @return
     */
    public Msg<Common> doCommonOfArticle(Common common){
        Msg<Common> msg = new Msg<>();
        if (null != common){
            Common common1 = initPublishCommon(common);
            Common save = commonRepository.save(common1);
            msg.setSuccess(true);
            msg.setData(save);
            //使用rabbitMQ发送消息
            sender.send(save);
            return msg;
        }
        msg.setSuccess(false);
        return msg;
    }
    /**
     *获取评论的回复
     */
    public Msg<List<Common>> getCommonsOfCommon(String commonId, Integer count){
        Msg<List<Common>> msg = new Msg<>();
        if (null == commonId){
            msg.setSuccess(false);
            return msg;
        }
        List<Common> commons = null;
        if (null == count){
            commons = commonRepository.findAllByRootIdOrderByCreateTimeAsc(commonId);
        } else {
            count = count > 0 ? count : 0;
            commons = commonRepository.findAllByRootIdOrderByCreateTimeAsc(commonId, PageRequest.of(count, 10));
        }
        commons.forEach(x -> {
            Long createTime = Long.valueOf(x.getCreateTime());
            x.setCreateTime(getFormatterTimeFromLong(createTime));
        });
        msg.setSuccess(true);
        msg.setData(commons);
        return msg;
    }

    /**
     * 回复评论
     * @param common:articleId, userName, commonId, content, replyUser
     * @return
     */
    public Msg<Common> doCommonOfCommon(Common common){
        Msg<Common> msg = new Msg<>();
        if (null != common){
            Common common1 = initPublishCommonReplied(common);
            if (null != common){
                Common save = commonRepository.save(common1);
                sender.send(save);
                msg.setSuccess(true);
                msg.setData(common1);
                return msg;
            }
        }
        msg.setSuccess(false);
        return msg;

    }

    /**
     * 删除评论
     * @param id
     * @return
     */
    public Msg delete(Long id){
        Msg msg = new Msg<>();
        if (null != id){
            Optional<Common> byId = commonRepository.findById(id);
            if (byId.isPresent()){
                //如果是文章的评论，则删除该评论的所有回复
                if (byId.get().getType() == 0){
                    commonRepository.deleteAllByRootId(id);
                } else {
                    //如果是评论的回复，只删除这一条
                    commonRepository.deleteById(id);
                }
                msg.setSuccess(true);
                return msg;
            }
        }
        msg.setSuccess(false);
        return msg;
    }

}
