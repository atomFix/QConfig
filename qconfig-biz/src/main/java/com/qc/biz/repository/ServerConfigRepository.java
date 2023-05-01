package com.qc.biz.repository;

import com.qc.biz.entity.ServerConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.Table;

/**
 * @Description:
 * @author: liukairong1
 * @date: 2023/04/30/09:17
 */
@Table(name = "ServerConfig")
public interface ServerConfigRepository extends JpaRepository<ServerConfig, Long> {
}
