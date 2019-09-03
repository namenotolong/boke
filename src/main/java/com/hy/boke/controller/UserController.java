package com.hy.boke.controller;

import com.hy.boke.constant.ConstantKey;
import com.hy.boke.exception.UsernameIsExitedException;
import com.hy.boke.po.Msg;
import com.hy.boke.po.User;
import com.hy.boke.service.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Objects;


@RestController
@Api(value = "注册管理", tags = "注册管理")
@Slf4j
@RequestMapping("/users")
public class UserController extends BaseController{

    @Autowired
    private Dao dao = null;

    @Autowired
    private UserService userService = null;

    @ApiOperation(value = "注册用户")
    @PostMapping("/signup")
    public Msg<User> signup(@RequestBody User user) {
        return userService.register(user);
    }

    @ApiOperation(value = "自定义登录")
    @PostMapping(value = "/login")
    public Msg<User> login(@RequestBody User user, HttpServletRequest request, HttpServletResponse response) {
        return userService.login(user, request, response);
    }

    @ApiOperation("自动登陆验证token")
    @GetMapping("/validate")
    public Boolean validateToken(){
        return true;
    }

    @ApiOperation("判断是否关注")
    @GetMapping("/getIsFollowed")
    public Boolean getIsFollowed(String name, String byFollowedName){
        return userService.followed(name, byFollowedName);
    }

    @ApiOperation("关注用户")
    @GetMapping("/follow")
    public Boolean follow(String name, String byFollowedName){
        return userService.follow(name, byFollowedName);
    }

    @ApiOperation("取消用户")
    @GetMapping("/cancelFollow")
    public Boolean cancelFollow(String name, String byFollowedName){
        return userService.cancelFollow(name, byFollowedName);
    }
    @ApiOperation("获取关注人数")
    @GetMapping("/getFollowsNums")
    public Long getFollowsNums(String name){
        return userService.getFollowNums(name);
    }
    @ApiOperation("获取粉丝数目")
    @GetMapping("/getFansNums")
    public Long getFansNums(String name){
        return userService.getFansNums(name);
    }

}
