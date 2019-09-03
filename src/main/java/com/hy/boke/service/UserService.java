package com.hy.boke.service;

import com.hy.boke.constant.ConstantKey;
import com.hy.boke.po.Msg;
import com.hy.boke.po.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

/**
 * redis中粉丝表为name1，关注表为name2，其中name为key，time为value
 */
@Service
public class UserService {
    @Autowired
    private Dao dao = null;

    @Value("${jwt.expiration}")
    private String time;

    @Autowired
    private StringRedisTemplate redisTemplate = null;

    /**
     * 验证用户密码
     */
    public Boolean validateUser(String name, String password){
        return dao.fetch(User.class, Cnd.where("name", "=", name).and("password", "=", password)) != null;
    }
    /**
     * 关注用户
     */
    public Boolean follow(String name, String byFollowedName){
        try {
            if (null == name || null == byFollowedName || name.equals("") || byFollowedName.equals("")){
                return false;
            }
            String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy年MM月dd日HH时mm分ss秒"));
            //将name添加到byFollowedName的粉丝表
            redisTemplate.opsForHash().put(byFollowedName + '1', name, now);
            //将byFollowedName添加到name的关注表
            redisTemplate.opsForHash().put(name + '2', byFollowedName, now);
        }catch (Exception e){
            return false;
        }
        return true;
    }
    /**
     * 取消关注
     */
    public Boolean cancelFollow(String name, String byFollowedName){
        try {
            if (null == name || null == byFollowedName || name.equals("") || byFollowedName.equals("")){
                return false;
            }
            redisTemplate.opsForHash().delete(byFollowedName + '1', name);
            redisTemplate.opsForHash().delete(name + '2', byFollowedName);
        }catch (Exception e){
            return false;
        }
        return true;
    }

    /**
     * 判读是否关注
     */

    public Boolean followed(String name, String byFollowedName){
        if (null == name || null == byFollowedName || name.equals("") || byFollowedName.equals("")){
            return false;
        }
        return redisTemplate.opsForHash().hasKey(name + "2", byFollowedName);
    }
    /**
     * 获取粉丝数
     */
    public Long getFansNums(String name){
        if (null == name || name.equals("")){
            return 0L;
        }
        return redisTemplate.opsForHash().size(name + "1");
    }
    /**
     * 获取关注人数
     */
    public Long getFollowNums(String name){
        if (null == name || name.equals("")){
            return 0L;
        }
        return redisTemplate.opsForHash().size(name + "2");
    }
    /**
     * 获取所有粉丝
     */
    public Set<Object> getAllFans(String name){
        if (null == name || name.equals("")){
            return null;
        }
        return redisTemplate.opsForHash().keys(name + "1");
    }
    /**
     * 获取所有关注
     */
    public Set<Object> getAllFollow(String name){
        if (null == name || name.equals("")){
            return null;
        }
        return redisTemplate.opsForHash().keys(name + "2");
    }
    /**
     * 用户注册
     */
    public Msg<User> register(User user){
        Msg<User> msg = new Msg<>();
        if (null == user || user.getName() == "" || null == user.getName() || null == user.getPassword() || user.getPassword() == ""){
            msg.setSuccess(false);
            msg.setName("用户信息不完整");
            return msg;
        }
        User name = dao.fetch(User.class, Cnd.where("name", "=", user.getName()));
        if(null != name){
            msg.setSuccess(false);
            msg.setName("用户名已存在");
            return msg;
        }
        user.setCreateTime(Timestamp.valueOf(LocalDateTime.now()));
        user.setModifiedTime(Timestamp.valueOf(LocalDateTime.now()));
        User insert = dao.insert(user);
        msg.setSuccess(insert != null);
        msg.setData(insert);
        return msg;
    }
    /**
     * 用户登陆
     */
    public Msg<User> login(User user, HttpServletRequest request, HttpServletResponse response){
        Msg<User> msg = new Msg<>();
        if (null == user){
            msg.setSuccess(false);
            msg.setName("账户或密码为空！");
            return msg;
        }
        User userVo = dao.fetch(user);
        if (userVo != null) {
            if (!Objects.equals(userVo.getPassword(), user.getPassword())){
                msg.setSuccess(false);
                msg.setName("密码错误！");
                return msg;
            }
            //自定义生成Token，因为使用了自定义登录，就不会执行JWTLoginFilter了，所以需要字段调用生成token并返给前端
            // 这里可以根据用户信息查询对应的角色信息，这里为了简单，我直接设置个空list
            // List roleList = new ArrayList<>();
            String subject = userVo.getName() + "-" + userVo.getPassword()/* + "-" + roleList*/;
            String token = Jwts.builder()
                    .setSubject(subject)
                    .setExpiration(new Date(System.currentTimeMillis() + Integer.parseInt(time))) // 设置过期时间 365 * 24 * 60 * 60秒(这里为了方便测试，所以设置了1年的过期时间，实际项目需要根据自己的情况修改)
                    .signWith(SignatureAlgorithm.HS512, ConstantKey.SIGNING_KEY) //采用什么算法是可以自己选择的，不一定非要采用HS512
                    .compact();
            // 登录成功后，返回token到header里面
            response.addHeader("Authorization", "Bearer " + token);
            msg.setSuccess(true);
            msg.setName("登陆成功！");
            msg.setData(user);
            return msg;
        } else {
            msg.setSuccess(false);
            msg.setName("账户不存在！");
            return msg;
        }
    }

}
