package com.zhq.executor.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author: zhenghq
 * @date: 2019/9/6
 * @version: 1.0.0
 */
@Component
public class QueueConfig {
	
	/**
	 * 线程池的线程数量
	 */
	@Value("${queue.config.threadCount:40}")
	private int threadCount = 40;
	
	/**
	 * 默认最大的等待队列的长度
	 */
	@Value("${queue.config.maxWaitLineCount:36000}")
	private int maxWaitLineCount = 36000;
	
	/**
	 * 最大的超时时间， 10s
	 */
	@Value("${queue.config.maxTimeout:10000}")
	private int maxTimeout = 10000;
	
	
	public int getThreadCount() {
		return threadCount;
	}
	
	public void setThreadCount(int threadCount) {
		this.threadCount = threadCount;
	}
	
	public int getMaxWaitLineCount() {
		return maxWaitLineCount;
	}
	
	public void setMaxWaitLineCount(int maxWaitLineCount) {
		this.maxWaitLineCount = maxWaitLineCount;
	}
	
	public int getMaxTimeout() {
		return maxTimeout;
	}
	
	public void setMaxTimeout(int maxTimeout) {
		this.maxTimeout = maxTimeout;
	}
	
	@Override
	public String toString() {
		return "QueueConfig{" +
				"threadCount=" + threadCount +
				", maxWaitLineCount=" + maxWaitLineCount +
				", maxTimeout=" + maxTimeout +
				'}';
	}
}
