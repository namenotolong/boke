package com.hy.boke.controller;

import com.hy.boke.po.Article;
import com.hy.boke.po.Catalog;
import com.hy.boke.po.User;
import com.hy.boke.utils.WebUtils;
import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
@Slf4j
public class BaseController {
    @Autowired
    private Dao dao = null;

    @Autowired
    protected BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * 获取用户所拥有的权限列表
     * @return
     */
    public List<String> getAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        List<String> list = new ArrayList<>();
        for (GrantedAuthority grantedAuthority : authorities) {
            log.info("权限列表：{}", grantedAuthority.getAuthority());
            list.add(grantedAuthority.getAuthority());
        }
        return list;
    }



    public void insert(){
        String format = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String[] arr = {"java", "大数据", "人工智能", "情感生活", "本地特色", "python", "c#","区块链", "安全", "运维", "其他"};
        for (int i = 0; i < arr.length; i++) {
            Catalog catalog = new Catalog();
            catalog.setName(arr[i]);
            catalog.setCreateTime(new Timestamp(System.currentTimeMillis()));
            catalog.setModifiedTime(new Timestamp(System.currentTimeMillis()));
            dao.insert(catalog);
        }
    }

}
