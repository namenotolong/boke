package com.hy.boke.controller;

import com.hy.boke.po.Msg;
import com.hy.boke.service.EventService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.nutz.dao.QueryResult;
import org.nutz.dao.pager.Pager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Api("消息事件")
@RestController
@RequestMapping("/event")
public class EventController {
    @Autowired
    private EventService eventService;

    @ApiOperation("获取未读消息的数目")
    @GetMapping("/getUnReadCount")
    public Msg<Integer> getUnReadCount(String userName){
        return eventService.getUnReadCount(userName);
    }

    @ApiOperation("拉取事件消息")
    @GetMapping("/getEvents")
    public Msg<QueryResult> getEvents(@ApiParam("用户名") String userName,
                                      @ApiParam("1代表自己收到的0～") Integer type, @ApiParam("页数") Integer pageCount){
        return eventService.getEvents(userName, type, pageCount);
    }
}
