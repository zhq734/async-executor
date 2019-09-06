package com.zhq.executor.executor;

import com.zhq.executor.core.QueueData;
import com.zhq.executor.core.QueueExecutor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: zhenghq
 * @date: 2019/9/6
 * @version: 1.0.0
 */
@Slf4j
public class DefaultQueueExecutor implements QueueExecutor {
	@Override
	public void execute(QueueData queueData) {
		log.info("DefaultQueueExecutor executor: queueData={}", queueData);
		
		
		
		
	}
}
