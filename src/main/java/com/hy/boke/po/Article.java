package com.hy.boke.po;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;


@Data
@Document(collection = "article")
public class Article{
    private String _id;
    @Field("title")
    private String title;
    @Field("introduction")
    private String introduction;
    @Field("count")
    private Long count;
    @Field("userName")
    private String userName;
    @Field("content")
    private String content;
    @Field("catalogName")
    private String catalogName;
    //0代表公开，1代表私有
    @Field("personal")
    private Integer personal;
    @Field("createTime")
    private String createTime;
    @Field("modifiedTime")
    private String modifiedTime;
}
