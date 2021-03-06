package com.hjk.rpc.spring;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import com.hjk.rpc.spring.parser.ClientBeanDefinitionParser;
import com.hjk.rpc.spring.parser.InterfaceBeanDefinitionParser;
import com.hjk.rpc.spring.parser.ServerBeanDefinitionParser;
import com.hjk.rpc.spring.parser.ServiceBeanDefinitionParser;
import com.hjk.rpc.spring.parser.ZookeeperBeanDefinitionParser;

/**
 * Created by hanjk on 16/9/8.
 */
public class RpcNameSpaceHandler extends NamespaceHandlerSupport {
    public RpcNameSpaceHandler() {
    }

    @Override
    public void init() {
        this.registerBeanDefinitionParser("zookeeper", new ZookeeperBeanDefinitionParser());
        this.registerBeanDefinitionParser("server", new ServerBeanDefinitionParser());
        this.registerBeanDefinitionParser("client", new ClientBeanDefinitionParser());
        this.registerBeanDefinitionParser("service", new ServiceBeanDefinitionParser());
        this.registerBeanDefinitionParser("interface", new InterfaceBeanDefinitionParser());
    }

    public BeanDefinition parse(Element element, ParserContext parserContext) {
        parserContext.getRegistry().registerBeanDefinition(RpcApplicationContext.class.getName(),
                BeanDefinitionBuilder.rootBeanDefinition(RpcApplicationContext.class).getBeanDefinition());
        return super.parse(element, parserContext);
    }

}
