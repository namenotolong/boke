package com.hy.boke;

import com.hy.boke.config.RabbitMQSender;
import com.hy.boke.constant.ConstantKey;
import com.hy.boke.controller.BaseController;
import com.hy.boke.dao.articleDao.ArticleRepository;
import com.hy.boke.po.Article;
import com.hy.boke.po.Catalog;
import com.hy.boke.po.Common;
import com.hy.boke.po.User;
import com.hy.boke.service.ArticleService;
import com.hy.boke.utils.JwtHelper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nutz.dao.Dao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BokeApplicationTests {
    @Autowired
    private Dao dao = null;

    @Autowired
    private ArticleRepository articleRepository = null;

    @Autowired
    private MongoTemplate mongoTemplate = null;

    @Autowired
    private BaseController baseController = null;

    @Autowired
    private StringRedisTemplate stringRedisTemplate = null;

    @Autowired
    private JwtHelper jwtHelper = null;

    @Autowired
    protected BCryptPasswordEncoder bCryptPasswordEncoder;
    @Value("${jwt.expiration}")
    private String time;
    @Autowired
    private ArticleService articleService = null;

    @Autowired
    private RabbitMQSender sender;

    @Test
    public void contextLoads(){
        Common common = new Common();
        common.setCount(1L);
        sender.send(common);
    }

}
