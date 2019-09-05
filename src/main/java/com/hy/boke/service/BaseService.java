package com.hy.boke.service;


import com.hy.boke.po.Msg;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class BaseService {
    public boolean checkNotNull(Object... str){
        for (int i = 0; i < str.length; i++) {
            if (null == str[i]){
                return false;
            }
        }
        return true;
    }
    public String getFormatterTimeFromLong(Long time){
        DateTimeFormatter ftf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return ftf.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault()));
    }
    public Msg<Object> returnError(){
        Msg<Object> msg = new Msg<>();
        msg.setSuccess(false);
        msg.setName("参数错误");
        return msg;
    }
}
