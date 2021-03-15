package com.zhq.executor.loadbalance.model;

import java.net.URI;
import java.util.Map;

/**
 * @author: zhenghq
 * @date: 2021/3/11
 * @version: 1.0.0
 */
public interface ServiceInstance {
	
	default String getInstanceId() {
		return null;
	}
	
	String getServiceId();
	
	String getHost();
	
	int getPort();
	
	boolean isSecure();
	
	URI getUri();
	
	Map<String, String> getMetadata();
	
	default String getScheme() {
		return null;
	}
	
}
