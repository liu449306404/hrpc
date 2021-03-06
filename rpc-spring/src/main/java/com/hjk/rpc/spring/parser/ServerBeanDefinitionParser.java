package com.hjk.rpc.spring.parser;

import com.hjk.rpc.spring.bean.ServerBean;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

import java.util.Iterator;
import java.util.List;

/**
 * Created by hanjk on 16/9/8.
 */
public class ServerBeanDefinitionParser extends AbstractBeanDefinitionParser {


    public ServerBeanDefinitionParser() {
    }

    @Override
    protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(ServerBean.class);
        List childElements = DomUtils.getChildElementsByTagName(element, "service");
        if(childElements != null && childElements.size() > 0) {
            this.parseChildren(childElements,builder, parserContext);
        }

        String name = element.getAttribute("name");
        builder.addPropertyValue("name", name);
        String port = element.getAttribute("port");
        builder.addPropertyValue("port",port);
        return builder.getBeanDefinition();
    }

    /**
     * 注册service服务
     */
    private void parseChildren(List<Element> childElements, BeanDefinitionBuilder builder, ParserContext parserContext) {
        ManagedList children = new ManagedList(childElements.size());
        ServiceBeanDefinitionParser parser = new ServiceBeanDefinitionParser();
        Iterator var5 = childElements.iterator();
        while(var5.hasNext()) {
            Element element = (Element)var5.next();
            children.add(parser.parse(element, parserContext));
        }
        builder.addPropertyValue("services",children);

    }

    protected boolean shouldGenerateId() {
        return true;
    }
}
