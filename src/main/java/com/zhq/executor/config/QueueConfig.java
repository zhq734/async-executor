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
	
}
