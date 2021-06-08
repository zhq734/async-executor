package com.zhq.executor.loadbalance.model;

import java.net.URI;
import java.util.Map;

/**
 * @author: zhenghq
 * @date: 2021/3/11
 * @version: 1.0.0
 */
public interface ServiceInstance {
	
	/**
	 * 获取实例id
	 * @return
	 */
	default String getInstanceId() {
		return null;
	}
	
	/**
	 * 获取服务id
	 * @return
	 */
	String getServiceId();
	
	/**
	 * 获取host
	 * @return
	 */
	String getHost();
	
	/**
	 * 获取端口
	 * @return
	 */
	int getPort();
	
	/**
	 * 是否是安全的
	 * @return
	 */
	boolean isSecure();
	
	/**
	 * 获取uri
	 * @return
	 */
	URI getUri();
	
	/**
	 * 获取元数据
	 * @return
	 */
	Map<String, String> getMetadata();
	
	/**
	 * 获取Scheme
	 * @return
	 */
	default String getScheme() {
		return null;
	}
	
}
