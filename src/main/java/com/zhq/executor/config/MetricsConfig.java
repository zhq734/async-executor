package com.zhq.executor.config;

import io.prometheus.client.Counter;

/**
 * @author: zhenghq
 * @date: 2020/4/28
 * @version: 1.0.0
 */
public interface MetricsConfig {
	
	String DEFAULT_LABEL = "_default";
	String NOHANDLE_LABEL = "_nohandle";
	String EXPIRE_LABEL = "_expire";
	String CLEAR_LABEL = "_clear";
	String RETRY_LABEL = "_retry";
	
	Counter inQueueCounter = Counter.build().name("in_queue_count").help("当前进队列数据").labelNames("in").register();
	Counter outQueueCounter = Counter.build().name("out_queue_count").help("当前执行数").labelNames("out").register();
	
}
