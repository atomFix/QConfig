package com.qc.biz.repository;

import com.qc.biz.entity.ServerConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import javax.persistence.Table;
import javax.transaction.Transactional;

/**
 * @Description:
 * @author: liukairong1
 * @date: 2023/04/30/09:17
 */
@Table(name = "server_config")
@Repository
public interface ServerConfigRepository extends JpaRepository<ServerConfig, String> {

    ServerConfig findFirstByKeyAndCluster(String key, String cluster);

    // 根据key和version更新ServerConfig对象的value属性
    @Modifying
    @Transactional
    @Query("UPDATE ServerConfig sc SET sc.value = :value, sc.version = :version + 1 WHERE sc.key = :key AND sc.version = :version")
    int updateValueByKeyAndVersion(@Param("key") String key, @Param("version") Integer version, @Param("value") String value);
}
