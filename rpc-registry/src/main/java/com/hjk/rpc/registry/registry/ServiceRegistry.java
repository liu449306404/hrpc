package com.hjk.rpc.registry.registry;

import com.hjk.rpc.common.bean.ServiceObject;

/**
 * 服务注册接口
 * Created by hanjk on 16/9/7.
 */
public interface ServiceRegistry {

    /**
     * 注册服务
     */
    void register(ServiceObject serviceObject);
}
