package com.qconfig.client.model;

import com.qconfig.common.enums.PropertyChangeType;
import lombok.Data;

/**
 * @Description:
 * @author: liukairong1
 * @date: 2023/05/16/00:16
 */
@Data
public class ConfigChange {

    public ConfigChange(String namespace, String propertyName) {
        this.namespace = namespace;
        this.propertyName = propertyName;
    }

    public ConfigChange(String namespace, String propertyName, String oldKey, String newKey, PropertyChangeType changeType) {
        this.namespace = namespace;
        this.propertyName = propertyName;
        this.oldKey = oldKey;
        this.newKey = newKey;
        this.changeType = changeType;
    }

    private final String namespace;

    private final String propertyName;

    private String oldKey;

    private String newKey;

    private PropertyChangeType changeType;

}
