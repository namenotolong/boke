package com.hy.boke.dao.articleDao;

import com.hy.boke.po.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ArticleRepository extends MongoRepository<Article, Long> {
    Page<Article> findArticlesByCatalogName(String name, Pageable pageable);
    List<Article> findAllArticlesByUserName(String name);
    List<Article> findAllArticlesByUserName(String name, Pageable pageable);
    List<Article> findArticlesByUserNameIn(Set<Object> name, Pageable pageable);
}
