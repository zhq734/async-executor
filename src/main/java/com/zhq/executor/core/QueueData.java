package com.zhq.executor.core;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author: zhenghq
 * @date: 2019/9/5
 * @version: 1.0.0
 */
@Data
@NoArgsConstructor
public class QueueData<T> {
	
	public static QueueData getInstance() {
		return new QueueData();
	}
	
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
	 * 数据标识：用于设置调用频率限制的
	 */
	private String cacheKey;
	
	/**
	 * 数据执行的有效期（即多少时间内只能执行一次）
	 */
	private Long expireTime;
	
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
