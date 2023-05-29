package com.qconfig.client.support;

import com.google.common.collect.Lists;
import com.qconfig.client.util.QConfigInjector;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Properties;

/**
 * @Description:
 * @author: liukairong1
 * @date: 2023/05/23/15:22
 */
@Slf4j
public abstract class  AbstractConfigRepository implements ConfigRepository {

    private List<RepositoryChangeListener> changeListeners = Lists.newCopyOnWriteArrayList();

    protected PropertiesFactory propertiesFactory = QConfigInjector.getInstance(PropertiesFactory.class);

    protected boolean trySync() {
        try {
            log.info("{} trySync execute !!!", this.getClass().getName());
            sync();
            return true;
        } catch (Exception e) {
            log.error("AbstractConfigRepository trySync has error!", e);
        }
        return false;
    }

    protected abstract void sync();

    @Override
    public void addChangeListener(RepositoryChangeListener listener) {
        if (!changeListeners.contains(listener)) {
            changeListeners.add(listener);
        }
    }

    @Override
    public void removeChangeListener(RepositoryChangeListener listener) {
        changeListeners.remove(listener);
    }

    protected void fireRepositoryChange(String namespace, Properties properties) {
        for (RepositoryChangeListener changeListener : changeListeners) {
            try {
                changeListener.onRepositoryChange(namespace, properties);
            } catch (Exception e) {
                log.error("changeListener has error! changeListener : {}, namespace: {}", changeListener.getClass(), namespace, e);
            }
        }
    }
}
