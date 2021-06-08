package com.zhq.executor.callback;

import com.zhq.executor.core.ExecuteFailCallback;
import com.zhq.executor.core.QueueData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: zhenghq
 * @date: 2019/9/6
 * @version: 1.0.0
 */
public class DefaultExecuteFailCallback implements ExecuteFailCallback {
	
	private static final Logger log = LoggerFactory.getLogger(DefaultExecuteFailCallback.class);
	
	@Override
	public void callback(QueueData queueData, Exception e) {
		log.error("DefaultExecuteFailCallback callback : queueData={}, msg={}, ", queueData, e.getMessage(), e);
	}
}
