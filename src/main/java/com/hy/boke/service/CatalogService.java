package com.hy.boke.service;

import com.hy.boke.po.Catalog;
import org.nutz.dao.Dao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CatalogService {
    @Autowired
    private Dao dao = null;
    /**
     * 获取所有目录
     */
    public List<Catalog> getCatalogs(){
        List<Catalog> query = dao.query(Catalog.class, null);
        return query;
    }
}
