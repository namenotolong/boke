package com.hy.boke.po;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

/**
 * 评论
 */
@Data
@Document(collection = "common")
public class Common implements Serializable {
    //mongoDB中的主键id
    private String _id;
    //评论人
    @Field("user_name")
    private String userName;
    //内容
    @Field("content")
    private String content;
    //时间,时间戳
    @Field("create_time")
    private String createTime;
    //时间,时间戳
    @Field("modified_time")
    private String modifiedTime;
    //评论的类型  0代表文章评论  1代表评论的回复
    @Field("type")
    private int type;
    //所评论的文章id
    @Field("article_id")
    private String articleId;
    //回复的评论id
    @Field("common_id")
    private String commonId;
    //回复的用户
    @Field("reply_user")
    private String replyUser;
    //该评论是否含有回复
    @Field("replied")
    private Boolean replied;
    //如果是回复的话，这里是该对应的文章评论id
    @Field("root_id")
    private String rootId;
    @Field("count")
    private Long count;
}