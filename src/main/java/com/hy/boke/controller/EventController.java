package com.hy.boke.controller;

import com.hy.boke.po.Msg;
import com.hy.boke.service.EventService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
}
