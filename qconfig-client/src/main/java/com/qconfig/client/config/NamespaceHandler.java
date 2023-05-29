package com.qconfig.client.config;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;
import org.springframework.core.Ordered;
import org.springframework.util.SystemPropertyUtils;
import org.w3c.dom.Element;

/**
 * @Description:
 * @author: liukairong1
 * @date: 2023/05/22/18:10
 */
public class NamespaceHandler extends NamespaceHandlerSupport {

    private static final Splitter SPLITTER = Splitter.on(",").omitEmptyStrings();

    @Override
    public void init() {
        registerBeanDefinitionParser("config", new BeanParser());
    }

    static class BeanParser extends AbstractSingleBeanDefinitionParser {

        @Override
        protected Class<?> getBeanClass(Element element) {
            return ConfigPropertySourcesProcessor.class;
        }

        @Override
        protected boolean shouldGenerateId() {
            return true;
        }

        @Override
        protected void doParse(Element element, BeanDefinitionBuilder builder) {
            String namespaces = this.getNamespace(element);

            int order = Ordered.LOWEST_PRECEDENCE;
            String orderAttribute = element.getAttribute("order");

            if (!Strings.isNullOrEmpty(orderAttribute)) {
                try {
                    order = Integer.parseInt(orderAttribute);
                } catch (Throwable ex) {
                    throw new IllegalArgumentException(
                            String.format("Invalid order: %s for namespaces: %s", orderAttribute, namespaces));
                }
            }
            PropertySourcesPostProcessor.addNamespaces(order, SPLITTER.splitToList(namespaces));
        }

        private String getNamespace(Element element) {
            String namespace = element.getAttribute("namespaces");
            if (Strings.isNullOrEmpty(namespace)) {
                return "application";
            }
            return SystemPropertyUtils.resolvePlaceholders(namespace);
        }
    }

}
