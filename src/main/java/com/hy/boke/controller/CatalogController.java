package com.hy.boke.controller;

import com.hy.boke.po.Catalog;
import com.hy.boke.service.CatalogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CatalogController {
    @Autowired
    private CatalogService catalogService = null;

    @GetMapping("/getCatalogs")
    public List<Catalog> getCatalogs(){
        return catalogService.getCatalogs();
    }
}
