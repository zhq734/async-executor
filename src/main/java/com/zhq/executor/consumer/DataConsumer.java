package com.zhq.executor.consumer;


import com.google.common.primitives.UnsignedInteger;
import com.zhq.executor.config.QueueConfig;
import com.zhq.executor.core.QueueContainer;
import com.zhq.executor.core.QueueContext;
import com.zhq.executor.core.QueueContextManager;
import com.zhq.executor.core.QueueData;
import com.zhq.executor.core.QueueExecutor;
import com.zhq.executor.util.ExpiryMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author: zhenghq
 * @date: 2019/9/5
 * @version: 1.0.0
 */
@Slf4j
@Component
public class DataConsumer implements IConsumer {
	
	/**
	 * 队列配置
	 */
	@Resource
	private QueueConfig queueConfig;
	
	/**
	 * 阻塞队列
	 */
	private static BlockingQueue blockingQueue = new LinkedBlockingQueue();
	
	/**
	 * 线程池的管理服务
	 */
	private ExecutorService executorService;
	
	/**
	 * 缓存数据记录（用于处理执行器的执行频率的）
	 */
	private static ExpiryMap<String, Byte> expiryMap = new ExpiryMap<>();
	
	@Override
	public void setQueueContainer(QueueContainer faceQueue) {
		executorService = Executors.newFixedThreadPool(queueConfig.getThreadCount());
	}
	
	/**
	 * 添加数据到执行队列
	 * @param queueData
	 * @param queueCallback
	 */
	public static void addQueueData(QueueData queueData, QueueExecutor queueCallback) {
		UnsignedInteger seqNum = QueueContextManager.getInstance().addContext(queueData, queueCallback);
		try {
			blockingQueue.put(seqNum);
		} catch (InterruptedException e) {
			log.error("DataConsumer addQueueData error: {}", e.getMessage(), e);
		}
		
	}
	
	@Override
	public void run() {
		//只要阻塞队列中有任务，就一直去消费
		while(true){
			UnsignedInteger seqNum = null;
			try {
				seqNum = (UnsignedInteger) blockingQueue.take();
				
				CompletableFuture.completedFuture(seqNum).thenApplyAsync(seqNumInner -> {
					log.info("正在消费DataConsumer: {}", seqNumInner);
					QueueContext queueContext = null;
					QueueData queueData = null;
					try {
						queueContext = QueueContextManager.getInstance().getContext(seqNumInner);
						if (Objects.isNull(queueContext)) {
							return 0;
						}
						queueData = queueContext.getQueueData();
						if (Objects.nonNull(queueData)) {
							String cacheKey = queueData.getCacheKey();
							Long expireTime = queueData.getExpireTime();
							
							/**
							 * 如果在缓存中，则进行处理
							 */
							if (Objects.nonNull(cacheKey) && expiryMap.get(cacheKey) != null
									&& queueData.getCurrentRetryCount() == 0) {
								log.info("The current operation time is still within the expiration time！no handle: {}", queueData);
								return 0;
							}
							
							if (queueData.getCurrentRetryCount() > 0) {
								Thread.sleep(queueContext.getQueueData().getCurrentRetryCount() * 1000);
							}
							
							/**
							 * 如果该数据带了过期时间和缓存key, 则加入缓存, 没配置时间则默认2分钟执行一次
							 */
							if (Objects.nonNull(cacheKey) && cacheKey.length() > 0) {
								if (Objects.nonNull(expireTime) && expireTime > 0) {
									expiryMap.put(cacheKey, Byte.parseByte("0"), expireTime);
								} else {
									expiryMap.put(cacheKey, Byte.parseByte("0"));
								}
							}
						}
						
						QueueContextManager.executeContext(queueContext);
					} catch (Exception e) {
						log.error("execute DataConsumer error: {}, {}", e.getMessage(), e);
						if (Objects.nonNull(queueData) && queueData.isHasRetry()) {
							try {
								boolean addSuccess = queueData.addFailCount();
								if (addSuccess) {
									addQueueData(queueData, queueContext.getQueueCallback());
								} else {
									if (Objects.nonNull(queueData.getFailCallback())) {
										queueData.getFailCallback().callback(queueData, new RuntimeException(e.getMessage()));
									}
								}
							} catch (Exception e1) {
								log.error("execute fail callback error: {}, {}", e1.getMessage(), e1);
							}
						}
					}
					return 1;
				}, executorService);
				
			} catch (InterruptedException e) {
				log.error("DataConsumer execute error: {}, {}", e.getMessage(), e);
			}
			
		}
	}
}
