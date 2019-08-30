package com.hy.boke.controller;

import com.hy.boke.po.Common;
import com.hy.boke.po.Msg;
import com.hy.boke.service.CommonService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/common")
public class CommonController extends BaseController {
    @Autowired
    private CommonService commonService;

    @ApiOperation("获取文章的评论")
    @GetMapping("/getCommonsOfArticle")
    public Msg<List<Common>> getCommonsOfArticle(String _id, Integer count){
        return commonService.getCommonsOfArticle(_id, count);
    }
    @ApiOperation("评论文章")
    @PostMapping("/doCommonOfArticle")
    public Msg<Common> doCommonOfArticle(@RequestBody Common common){
        return commonService.doCommonOfArticle(common);
    }

    @ApiOperation("获取评论的回复")
    @GetMapping("/getCommonsOfCommon")
    public Msg<List<Common>> getCommonsOfCommon(String commonId, Integer count){
        return  commonService.getCommonsOfCommon(commonId, count);
    }

    @ApiOperation("回复评论")
    @PostMapping("/doCommonOfCommon")
    public Msg<Common> doCommonOfCommon(@RequestBody Common common){
        return commonService.doCommonOfCommon(common);
    }

    @ApiOperation("删除评论")
    @DeleteMapping("/delete")
    public Msg delete(Long id){
        return commonService.delete(id);
    }
}
