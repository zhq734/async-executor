package com.zhq.executor.core;

/**
 * @author: zhenghq
 * @date: 2019/9/5
 * @version: 1.0.0
 */
public class QueueContext {
	
	public static QueueContext getInstance() {
		return new QueueContext();
	}
	
	public QueueContext() {
	
	}
	
	public QueueContext(QueueData queueData, QueueExecutor queueCallback) {
		this.queueData = queueData;
		this.queueCallback = queueCallback;
	}
	
	private QueueData queueData;
	
	private QueueExecutor queueCallback;
	
	public QueueData getQueueData() {
		return queueData;
	}
	
	public void setQueueData(QueueData queueData) {
		this.queueData = queueData;
	}
	
	public QueueExecutor getQueueCallback() {
		return queueCallback;
	}
	
	public void setQueueCallback(QueueExecutor queueCallback) {
		this.queueCallback = queueCallback;
	}
	
	@Override
	public String toString() {
		return "QueueContext{" +
				"queueData=" + queueData +
				", queueCallback=" + queueCallback +
				'}';
	}
}
