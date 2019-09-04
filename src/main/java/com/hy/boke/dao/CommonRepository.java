package com.hy.boke.dao;

import com.hy.boke.po.Common;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommonRepository extends MongoRepository<Common, Long> {
    List<Common> findAllByArticleIdAndTypeOrderByCreateTimeAsc(String _id, int type, Pageable pageable);
    List<Common> findAllByRootIdOrderByCreateTimeAsc(String rootId,Pageable pageable);
    List<Common> findAllByRootIdOrderByCreateTimeAsc(String rootId);
    List<Common> findAllByArticleIdAndTypeOrderByCreateTimeAsc(String _id, int type);
    void deleteAllByRootId(Long rootId);
    Common findBy_id(String _id);
    Common findByUserNameAndCreateTime(@Param("userName") String userName,@Param("createTime") String createTime);
    Long countAllByArticleId(String _id);
}
