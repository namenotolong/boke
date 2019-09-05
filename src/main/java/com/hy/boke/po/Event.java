package com.hy.boke.po;

import lombok.Data;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

/**
 * 消息事件
 */
@Data
@Table
public class Event implements Cloneable {
    @Id
    private Long id;
    //评论的消息内容对应的id
    @Column(hump = true)
    private String commonId;
    @Column(hump = true)
    private String fromUserName;
    @Column(hump = true)
    private String toUserName;
    @Column
    private String content;
    @Column
    private String time;
    @Column
    //1为未读，0为已读
    private int status;
    private Common common;

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }
}
