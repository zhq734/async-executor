package com.zhq.executor.core;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

/**
 * @author: zhenghq
 * @date: 2019/9/5
 * @version: 1.0.0
 */
@Data
@NoArgsConstructor
@ToString
public class QueueData<T> {
	
	/**
	 * 是否进行重试
	 */
	private boolean hasRetry = true;
	
	/**
	 * 重试次数
	 */
	private int retryCount = 3;
	
	/**
	 * 当前重试的次数
	 */
	private int currentRetryCount = 0;
	
	/**
	 * 执行时间
	 */
	private Date executeDate;
	
	/**
	 * 请求的数据
	 */
	private T data;
	
	/**
	 * 失败回调
	 */
	private ExecuteFailCallback failCallback;
	
	public boolean addFailCount() {
		if (currentRetryCount >= retryCount) {
			return false;
		}
		currentRetryCount ++;
		return true;
	}
	
}
