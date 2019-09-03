package com.hy.boke.controller;

import com.hy.boke.dao.articleDao.ArticleRepository;
import com.hy.boke.po.Article;
import com.hy.boke.po.Msg;
import com.hy.boke.service.ArticleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/article")
@Api(value = "文章接口")
public class ArticleController {
    @Autowired
    private ArticleService articleService = null;
    @Autowired
    private ArticleRepository articleRepository = null;

    @GetMapping("/getArticleById")
    @ApiOperation(value = "根据文章_id获取文章")
    public Msg<Article> getArticleById(@ApiParam("文章_id") String id){
        return articleService.getArticleById(id);
    }

    @GetMapping("/getArticlesByName")
    @ApiOperation(value = "根据作者名称获取文章")
    public Msg<List<Article>> getArticlesByName(@ApiParam("作者名称") String userName){
        return articleService.getArticlesByName(userName);
    }

    @ApiOperation(value = "根据分类获取文章")
    @GetMapping("/getArticles")
    public Msg<List<Article>> getArticles(Integer count,@ApiParam("文章分类") String name){
        return articleService.getArticles(count, name);
    }
    @ApiOperation(value = "发布文章")
    @PostMapping("/publishArticle")
    public Msg<Article> publishArticle(@RequestBody Article article){
        return articleService.publishArticle(article);
    }

    @ApiOperation("获取关注的文章")
    @GetMapping("/getArticlesOfFollowers")
    public Msg<List<Article>> getArticlesOfFollowers(@ApiParam("人名") String name,@ApiParam("第几页") int count){
        return articleService.getArticlesOfFollowers(name, count);
    }
    @ApiOperation("获取自己的文章")
    @GetMapping("/getOwnArticles")
    public Msg<List<Article>> getOwnArticles(@ApiParam("人名")String name,@ApiParam("第几页") int count){
        return articleService.getOwnArticles(name, count);
    }
}
