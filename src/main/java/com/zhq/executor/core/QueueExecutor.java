package com.zhq.executor.core;

/**
 * @author: zhenghq
 * @date: 2019/9/5
 * @version: 1.0.0
 */
public interface QueueExecutor {
	
	/**
	 * 执行
	 * @param queueData
	 */
	void execute(QueueData queueData);
	
}
