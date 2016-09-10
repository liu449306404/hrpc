package com.hjk.rpc.core.client;

import com.hjk.rpc.common.bean.RpcRequest;
import com.hjk.rpc.common.bean.RpcResponse;
import com.hjk.rpc.common.bean.ServiceObject;
import com.hjk.rpc.common.exception.NotFoundServiceException;
import com.hjk.rpc.common.utils.StringUtil;
import com.hjk.rpc.common.utils.UUIDUtil;
import com.hjk.rpc.registry.discovery.ServiceDiscovery;
import com.hjk.rpc.registry.zookeeper.ZookeeperServiceDiscovery;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * 创建代理bean
 * Created by hanjk on 16/9/8.
 */
public class RpcCglibProxy{

    private static final Logger logger = LoggerFactory.getLogger(RpcCglibProxy.class);

    public static Object getPoxyObject(final ServiceObject serviceObject) throws
            ClassNotFoundException {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(Class.forName(serviceObject.getServiceName()));
        enhancer.setCallback(new MethodInterceptor() {
            public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                if(method.getDeclaringClass() == Object.class){
                    return method.invoke(this, objects);
                }else{
                    //封装 request
                    RpcRequest request = new RpcRequest();
                    request.setServiceName(method.getDeclaringClass().getName());
                    request.setMethodName(method.getName());
                    request.setRequestId(UUIDUtil.getUUID());
                    request.setParameterTypesClass(method.getParameterTypes());
                    request.setParameters(objects);
                    //发送请求
                    ServiceDiscovery serviceDiscovery = ZookeeperServiceDiscovery.getInstance();
                    String serverAddress ;
                    try {
                        serverAddress = serviceDiscovery.discovery(serviceObject.getAppServer(),serviceObject.getServiceName());
                    }catch (NotFoundServiceException var){
                        logger.error("",var);
                        return null;
                    }
                    if(StringUtil.isEmpty(serverAddress)){
                        return null;
                    }
                    String[] address = serverAddress.split(":");
                    RpcClient client = new RpcClient(address[0],Integer.parseInt(address[1]));
                    logger.info("client send server:[{}] requestId:{} data:{}",serverAddress,request.getRequestId(),request);
                    long beginTime = System.currentTimeMillis();
                    RpcResponse response = client.send(request);
                    long endTime = System.currentTimeMillis();
                    logger.info("client received return requestId:{} data:{}",response.getRequestId(),response);
                    logger.info("client request cost time:{}",endTime - beginTime);
                    return response.getResult();
                }
            }
        });
        return enhancer.create();
    }
}