package com.hy.boke.po;

import lombok.Data;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Timestamp;

@Data
public class BasePo {
    @Id
    private Long id;
    @Column
    private Timestamp createTime;
    @Column
    private Timestamp modifiedTime;
}
