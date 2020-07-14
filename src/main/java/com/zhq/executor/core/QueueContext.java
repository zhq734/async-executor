package com.zhq.executor.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: zhenghq
 * @date: 2019/9/5
 * @version: 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueueContext {
	
	public static QueueContext getInstance() {
		return new QueueContext();
	}
	
	private QueueData queueData;
	
	private QueueExecutor queueCallback;
	
}
