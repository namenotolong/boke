package com.hy.boke.config;

import org.nutz.dao.Dao;
import org.nutz.dao.impl.NutDao;
import org.nutz.integration.spring.SpringDaoRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class JDBCConfig {

    @Bean
    public Dao getDao(SpringDaoRunner springDaoRunner, DataSource dataSource){
        NutDao dao = new NutDao(dataSource);
        dao.setRunner(springDaoRunner);
        return dao;
    }
    @Bean
    public SpringDaoRunner springDaoRunner(){
        return new SpringDaoRunner();
    }
    @Bean
    public PlatformTransactionManager txManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
