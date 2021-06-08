package com.zhq.executor.executor;

import com.zhq.executor.core.QueueData;
import com.zhq.executor.core.QueueExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: zhenghq
 * @date: 2019/9/6
 * @version: 1.0.0
 */
public class DefaultQueueExecutor implements QueueExecutor {
	
	private static final Logger log = LoggerFactory.getLogger(DefaultQueueExecutor.class);
	
	@Override
	public void execute(QueueData queueData) {
		log.info("DefaultQueueExecutor executor: queueData={}", queueData);
		
	}
}
