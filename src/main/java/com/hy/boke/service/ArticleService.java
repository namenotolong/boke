package com.hy.boke.service;

import com.hy.boke.dao.articleDao.ArticleRepository;
import com.hy.boke.po.Article;
import com.hy.boke.po.Msg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

@Service
public class ArticleService {
    @Autowired
    private ArticleRepository articleRepository = null;

    @Autowired
    private MongoTemplate mongoTemplate = null;

    @Autowired
    private StringRedisTemplate redisTemplate = null;

    /**
     * 根据_id获取文章
     * @param _id
     * @return
     */
    public Msg<Article> getArticleById(String _id){
        Msg<Article> msg = new Msg<>();
        if (_id == null){
            msg.setSuccess(false);
            msg.setName("文章id为空");
            return msg;
        }
        Query id = Query.query(Criteria.where("_id").is(_id));
        Article one = mongoTemplate.findOne(id, Article.class);
        if (one == null){
            msg.setSuccess(false);
            msg.setName("文章不存在");
            return msg;
        }
        msg.setSuccess(true);
        msg.setData(one);
        return msg;
    }
    /**
     *获取文章列表——首页展示
     */
    public Msg<List<Article>> getArticles(Integer count, String name){
        if (count < 0){
            count = 0;
        }
        List<Article> content;
        if (null == name){
            content = articleRepository.findAll(PageRequest.of(count, 6)).getContent();
        } else {
            content = articleRepository.findArticlesByCatalogName(name, PageRequest.of(count, 6)).getContent();
        }
        Msg<List<Article>> msg = new Msg<>();
        if (content.size() == 0){
            msg.setSuccess(false);
            msg.setName("已经到底啦！");
        } else {
            msg.setSuccess(true);
            msg.setData(content);
        }
        return msg;
    }

    /**
     * 发布文章
     */
    public Msg<Article> publishArticle(Article article){
        Msg<Article> msg = new Msg<>();
        if (null == article){
            msg.setName("发布失败，输入内容不完整");
            msg.setSuccess(false);
            return msg;
        }
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy年MM月dd日HH时mm分ss秒"));
        article.setCreateTime(now);
        article.setModifiedTime(now);
        Article save = articleRepository.save(article);
        msg.setSuccess(true);
        msg.setName("发布成功");
        msg.setData(save);
        return msg;
    }

    /**
     * 根据name，获取前六篇文章
     * @param userName
     * @return
     */
    public Msg<List<Article>> getArticlesByName(String userName){
        Msg<List<Article>> msg = new Msg<>();
        if (userName == null || userName.equals("")){
            msg.setName("作者名字为空");
            msg.setSuccess(false);
            return msg;
        }
        List<Article> articlesByUserName = articleRepository.findAllArticlesByUserName(userName, PageRequest.of(0, 6));
        msg.setData(articlesByUserName);
        msg.setSuccess(true);
        return msg;
    }

    /**
     * 查看自己文章
     */
    public Msg<List<Article>> getOwnArticles(String name, int count){
        Msg<List<Article>> msg = new Msg<>();
        if (null == name || name.equals("")){
            msg.setSuccess(false);
            msg.setName("参数有误");
            return msg;
        }
        if (count < 0){
            count = 0;
        }
        msg.setData(articleRepository.findAllArticlesByUserName(name, PageRequest.of(count, 6)));
        msg.setSuccess(true);
        return msg;
    }

    /**
     * 根据人名获取关注人的文章
     */
    public Msg<List<Article>> getArticlesOfFollowers(String name, int count){
        Msg<List<Article>> msg = new Msg<>();
        if (null == name || name.equals("")){
            msg.setSuccess(false);
            msg.setName("参数为空");
            return msg;
        }
        //获取关注人的列表
        Set<Object> keys = redisTemplate.opsForHash().keys(name + "2");
        if (keys.size() == 0){
            msg.setSuccess(false);
            msg.setName("没有关注任何人");
            return msg;
        }
        if (count < 0){
            count = 0;
        }
        List<Article> articlesByUserNameExists = articleRepository.findArticlesByUserNameIn(keys, PageRequest.of(count, 6));
        msg.setSuccess(true);
        msg.setData(articlesByUserNameExists);
        return msg;
    }
}
