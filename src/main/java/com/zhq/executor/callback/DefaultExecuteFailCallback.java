package com.zhq.executor.callback;

import com.zhq.executor.core.ExecuteFailCallback;
import com.zhq.executor.core.QueueData;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: zhenghq
 * @date: 2019/9/6
 * @version: 1.0.0
 */
@Slf4j
public class DefaultExecuteFailCallback implements ExecuteFailCallback {
	@Override
	public void callback(QueueData queueData, Exception e) {
		log.error("DefaultExecuteFailCallback callback : queueData={}, msg={}, ", queueData, e.getMessage(), e);
	}
}
