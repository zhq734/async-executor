package com.zhq.executor.core;

/**
 * @author: zhenghq
 * @date: 2019/9/6
 * @version: 1.0.0
 */
public interface ExecuteFailCallback {
	
	/**
	 * 执行回调
	 * @param queueData
	 * @param e
	 */
	void callback(QueueData queueData, Exception e);
	
}
