package com.zhq.executor.consumer;


import com.zhq.executor.core.QueueContainer;

/**
 * @author: zhenghq
 * @date: 2019/7/24
 * @version: 1.0.0
 */
public interface IConsumer extends Runnable {
	
	void setQueueContainer(QueueContainer faceQueue);
	
}
