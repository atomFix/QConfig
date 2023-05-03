package com.qc.biz.entity;

import lombok.*;

import javax.persistence.*;

/**
 * @Description:
 * @author: liukairong1
 * @date: 2023/04/30/11:27
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = ServerConfig.TABLE_NAME, schema = "QConfig")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class ServerConfig {
    public static final String TABLE_NAME = "`server_config`";
    public static final String COLUMN_ID_NAME = "`key`";
    public static final String COLUMN_VALUE_NAME = "value";
    public static final String COLUMN_CLUSTER_NAME = "cluster";
    public static final String COLUMN_COMMENT_NAME = "comment";
    public static final String COLUMN_COMMENT_VERSION = "version";


    @Id
    @Column(name = COLUMN_ID_NAME, nullable = false, length = 50)
    private String key;

    @Column(name = COLUMN_VALUE_NAME, length = 300)
    private String value;

    @Column(name = COLUMN_CLUSTER_NAME, nullable = false, length = 20)
    private String cluster;

    @Column(name = COLUMN_COMMENT_NAME, length = 50)
    private String comment;

    @Column(name = COLUMN_COMMENT_VERSION)
    private Integer version;

}