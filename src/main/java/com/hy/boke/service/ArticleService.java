package com.hy.boke.service;

import com.hy.boke.dao.CommonRepository;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
//文章关注redis  hash表  表名：文章名   k：用户名   v：time
//用户关注redis  hash表   表名：人名    k：文章_id  v:time
@Service
public class ArticleService extends BaseService {
    @Autowired
    private ArticleRepository articleRepository = null;

    @Autowired
    private MongoTemplate mongoTemplate = null;

    @Autowired
    private CommonRepository commonRepository;

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
        content.forEach(x -> x.setCount(commonRepository.countAllByArticleId(x.get_id())));
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
    /**
     * 判断该文章自己是否关注
     */
    public Msg<Boolean> checkStored(String _id, String userName){
        Msg<Boolean> msg = new Msg<>();
        if (checkNotNull(_id, userName)){
            Boolean aBoolean = redisTemplate.opsForHash().hasKey(_id, userName);
            msg.setSuccess(aBoolean);
            msg.setName("不存在");
            return msg;
        }
        msg.setSuccess(false);
        msg.setName("参数不完整");
        return msg;
    }
    /**
     * 收藏文章
     */
    public Msg<Boolean> store(String _id, String userName){
        Msg<Boolean> msg = new Msg<>();
        if (checkNotNull(_id, userName)){
            Boolean aBoolean = redisTemplate.opsForHash().putIfAbsent(_id, userName, getFormatterTimeFromLong(System.currentTimeMillis()));
            Boolean bBoolean = redisTemplate.opsForHash().putIfAbsent(userName, _id, getFormatterTimeFromLong(System.currentTimeMillis()));
            msg.setSuccess(aBoolean && bBoolean);
            msg.setName("关注失败");
            return msg;
        }
        msg.setSuccess(false);
        msg.setName("参数不完整");
        return msg;
    }
    /**
     * 取消关注文章
     */
    public Msg<Boolean> unStore(String _id, String userName){
        Msg<Boolean> msg = new Msg<>();
        if (checkNotNull(_id, userName)){
            Long delete1 = redisTemplate.opsForHash().delete(_id, userName);
            Long delete2 = redisTemplate.opsForHash().delete(userName, _id);
            msg.setSuccess(delete1 > 0 && delete2 > 0);
            msg.setName("取消关注失败");
            return msg;
        }
        msg.setSuccess(false);
        msg.setName("参数不完整");
        return msg;
    }
    /**
     * 获取关注的文章
     */
    public Msg<List<Article>> showMyFollowArticles(String userName){
        Msg<List<Article>> msg = new Msg<>();
        if (null != userName){
            Set<Object> keys = redisTemplate.opsForHash().keys(userName);
            List<Article> list = new ArrayList<>();
            for (Object temp: keys) {
                Article by_id = articleRepository.findBy_id(String.valueOf(temp));
                if (null != by_id){
                    list.add(by_id);
                }
            }
            msg.setSuccess(true);
            msg.setData(list);
            return msg;
        }
        msg.setSuccess(false);
        msg.setName("参数空");
        return msg;
    }
}
