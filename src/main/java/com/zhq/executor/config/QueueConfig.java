package com.zhq.executor.config;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @author: zhenghq
 * @date: 2019/9/6
 * @version: 1.0.0
 */
@Configuration
@ConfigurationProperties(prefix = "queue.config", ignoreInvalidFields = true)
@Data
@Component
@ToString
public class QueueConfig {
	
	/**
	 * 线程池的线程数量
	 */
	private int threadCount = 4;
	
	/**
	 * 默认最大的等待队列的长度
	 */
	private int maxWaitLineCount = 36000;
	
	/**
	 * 最大的超时时间， 10s
	 */
	private int maxTimeout = 10000;
	
}
