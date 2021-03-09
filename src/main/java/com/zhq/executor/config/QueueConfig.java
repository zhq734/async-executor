package com.zhq.executor.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author: zhenghq
 * @date: 2019/9/6
 * @version: 1.0.0
 */
@Data
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
	
}
