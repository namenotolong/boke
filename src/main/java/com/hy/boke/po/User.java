package com.hy.boke.po;

import lombok.Data;
import org.nutz.dao.entity.annotation.*;


@Table
@Data
public class User extends BasePo{
    @Name
    private String name;

    @Column
    private String password;

    @Column(hump = true)
    private String infoUrl;
}
