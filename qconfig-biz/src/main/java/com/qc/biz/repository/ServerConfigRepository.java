package com.qc.biz.repository;

import com.qc.biz.entity.ServerConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import javax.persistence.Table;

/**
 * @Description:
 * @author: liukairong1
 * @date: 2023/04/30/09:17
 */
@Table(name = "ServerConfig")
@Repository
public interface ServerConfigRepository extends JpaRepository<ServerConfig, Long> {
}
