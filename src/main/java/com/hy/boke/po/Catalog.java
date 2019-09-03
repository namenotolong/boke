package com.hy.boke.po;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Table;

import java.sql.Timestamp;

@Data
@Table
public class Catalog extends BasePo{
    @Id
    private Long id;
    @Name
    private String name;
    @Column(hump = true)
    private Timestamp createTime;
    @Column(hump = true)
    private Timestamp modifiedTime;
}
