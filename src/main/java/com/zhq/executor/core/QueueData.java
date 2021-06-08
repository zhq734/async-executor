package com.zhq.executor.core;

import java.util.Date;

/**
 * @author: zhenghq
 * @date: 2019/9/5
 * @version: 1.0.0
 */
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
	 * 订阅主题
	 */
	private String tipoc;
	
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
	
	
	public boolean isHasRetry() {
		return hasRetry;
	}
	
	public void setHasRetry(boolean hasRetry) {
		this.hasRetry = hasRetry;
	}
	
	public int getRetryCount() {
		return retryCount;
	}
	
	public void setRetryCount(int retryCount) {
		this.retryCount = retryCount;
	}
	
	public int getCurrentRetryCount() {
		return currentRetryCount;
	}
	
	public void setCurrentRetryCount(int currentRetryCount) {
		this.currentRetryCount = currentRetryCount;
	}
	
	public Date getExecuteDate() {
		return executeDate;
	}
	
	public void setExecuteDate(Date executeDate) {
		this.executeDate = executeDate;
	}
	
	public String getCacheKey() {
		return cacheKey;
	}
	
	public void setCacheKey(String cacheKey) {
		this.cacheKey = cacheKey;
	}
	
	public Long getExpireTime() {
		return expireTime;
	}
	
	public void setExpireTime(Long expireTime) {
		this.expireTime = expireTime;
	}
	
	public T getData() {
		return data;
	}
	
	public void setData(T data) {
		this.data = data;
	}
	
	public String getTipoc() {
		return tipoc;
	}
	
	public void setTipoc(String tipoc) {
		this.tipoc = tipoc;
	}
	
	public ExecuteFailCallback getFailCallback() {
		return failCallback;
	}
	
	public void setFailCallback(ExecuteFailCallback failCallback) {
		this.failCallback = failCallback;
	}
	
	@Override
	public String toString() {
		return "QueueData{" +
				"hasRetry=" + hasRetry +
				", retryCount=" + retryCount +
				", currentRetryCount=" + currentRetryCount +
				", executeDate=" + executeDate +
				", cacheKey='" + cacheKey + '\'' +
				", expireTime=" + expireTime +
				", data=" + data +
				", tipoc='" + tipoc + '\'' +
				", failCallback=" + failCallback +
				'}';
	}
}
