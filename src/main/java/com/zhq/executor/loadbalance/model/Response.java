package com.zhq.executor.loadbalance.model;

/**
 * @author: zhenghq
 * @date: 2021/3/11
 * @version: 1.0.0
 */
public interface Response<T> {
	boolean hasServer();
	
	T getServer();
}
