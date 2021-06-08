package com.zhq.executor.loadbalance.model.impl;

import com.zhq.executor.loadbalance.model.Response;
import com.zhq.executor.loadbalance.model.ServiceInstance;

/**
 * @author: zhenghq
 * @date: 2021/3/11
 * @version: 1.0.0
 */
public class EmptyResponse implements Response<ServiceInstance> {
	public EmptyResponse() {
	}
	
	@Override
	public boolean hasServer() {
		return false;
	}
	
	@Override
	public ServiceInstance getServer() {
		return null;
	}
}