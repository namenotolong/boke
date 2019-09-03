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
public class Event {
    @Id
    private Long id;
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
}
