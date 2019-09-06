package com.zhq.executor.core;

/**
 * @author: zhenghq
 * @date: 2019/9/6
 * @version: 1.0.0
 */
public interface ExecuteFailCallback {
	
	void callback(QueueData queueData, Exception e);
	
}
