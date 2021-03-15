package com.zhq.executor.consumer;


import com.google.common.primitives.UnsignedInteger;
import com.zhq.executor.config.MetricsConfig;
import com.zhq.executor.config.QueueConfig;
import com.zhq.executor.core.QueueContext;
import com.zhq.executor.core.QueueContextManager;
import com.zhq.executor.core.QueueData;
import com.zhq.executor.core.QueueExecutor;
import com.zhq.executor.util.ExecutorThreadFactory;
import com.zhq.executor.util.ExpiryMap;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

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
	private static QueueConfig queueConfig;
	
	@Resource
	public void setQueueConfig(QueueConfig queueConfig) {
		DataConsumer.queueConfig = queueConfig;
	}
	
	private static final String _DEFAULT_TOPiC = "_default";
	
	/**
	 * 清除的时候，进行加锁操作
	 */
	private static AtomicBoolean hasLock = new AtomicBoolean(false);
	
	/**
	 * 判断是否已经在执行
	 */
	private static ConcurrentMap<String, AtomicBoolean> runningMap = new ConcurrentHashMap<>();
	
	/**
	 * 阻塞队列
	 */
	private static BlockingQueue blockingQueue = new LinkedBlockingQueue();
	
	/**
	 * 线程池的管理服务
	 */
	private static ExecutorService executorService;
	
	/**
	 * 缓存数据记录（用于处理执行器的执行频率的）
	 */
	private static ExpiryMap<String, Byte> expiryMap = new ExpiryMap<>();
	
	@Override
	public void init() {
		LinkedBlockingQueue linkedBlockingQueue = new LinkedBlockingQueue<Runnable>(queueConfig.getMaxWaitLineCount());
		executorService = new ThreadPoolExecutor(queueConfig.getThreadCount(), queueConfig.getThreadCount(),
				0L, TimeUnit.MILLISECONDS,
				linkedBlockingQueue, new ExecutorThreadFactory(this.getClass().getSimpleName()),
				(Runnable r, ThreadPoolExecutor executor) -> {
					hasLock.set(true);
					/**
					 * 如果队列满了，则清理队列
					 */
					try {
						int queueSize = linkedBlockingQueue.size() + 1;
						log.warn("linkedBlockingQueue is full, will clear: {}", queueSize);
						
						linkedBlockingQueue.clear();
						QueueContextManager.getInstance().clearContext();
						MetricsConfig.outQueueCounter.labels(MetricsConfig.CLEAR_LABEL).inc(queueSize);
					} finally {
						hasLock.set(false);
					}
					
				});
	}
	
	/**
	 * 添加数据到执行队列
	 * @param queueData
	 * @param queueCallback
	 */
	public static void addQueueData(QueueData queueData, QueueExecutor queueCallback) {
		/**
		 * 如果加锁了，则延迟1秒
		 */
		if (hasLock.get()) {
			log.warn("current hasLock will sleep 1000 ms");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		UnsignedInteger seqNum = QueueContextManager.getInstance().addContext(queueData, queueCallback);
		try {
			runningMap.put(_DEFAULT_TOPiC, runningMap.getOrDefault(_DEFAULT_TOPiC, new AtomicBoolean(false)));
			blockingQueue.put(seqNum);
			execute(_DEFAULT_TOPiC, blockingQueue);
		} catch (InterruptedException e) {
			log.error("DataConsumer addQueueData error: {}", e.getMessage(), e);
		}
		
	}
	
	/**
	 * 添加同步队列
	 * @param queueData
	 * @param queueCallback
	 */
	public static void addSyncQueueData(QueueData queueData, QueueExecutor queueCallback) {
		
		if (queueData == null) {
			queueData = new QueueData();
		}
		
		synchronized (queueData) {
			try {
				addQueueData(queueData, queueCallback);
				queueData.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	
	/**
	 * 添加指定订阅的数据到执行队列
	 * @param queueData
	 * @param queueCallback
	 */
	protected static void addTopicQueueData(String topic, QueueData queueData, QueueExecutor queueCallback) {
		/**
		 * 如果加锁了，则延迟1秒
		 */
		if (hasLock.get()) {
			log.warn("current hasLock will sleep 1000 ms");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		try {
			BlockingQueue blockingQueue = InnerCommand.getTopicQueue(topic);
			if (blockingQueue == null) {
				log.error("current topic not find Queue: {}", topic);
				addQueueData(queueData, queueCallback);
				return;
			}
			if (queueData == null) {
				queueData = QueueData.getInstance();
			}
			
			runningMap.put(topic, runningMap.getOrDefault(topic, new AtomicBoolean(false)));
			queueData.setTipoc(topic);
			UnsignedInteger seqNum = QueueContextManager.getInstance().addContext(queueData, queueCallback);
			
			blockingQueue.put(seqNum);
			execute(topic, blockingQueue);
		} catch (InterruptedException e) {
			log.error("DataConsumer addTopicQueueData error: {}", e.getMessage(), e);
		}
		
	}
	
	
	/**
	 * 执行代码
	 */
	private static void execute(String topic, BlockingQueue blockingQueue) {
		AtomicBoolean hasRunning = runningMap.get(topic);
		if (hasRunning == null) {
			log.info("invild request execute!! topic={}", topic);
			return;
		}
		if (!hasRunning.compareAndSet(false, true)) {
			return;
		}
		
		//只要阻塞队列中有任务，就一直去消费
		while(blockingQueue.peek() != null) {
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
							MetricsConfig.outQueueCounter.labels(MetricsConfig.NOHANDLE_LABEL).inc();
							return 0;
						}
						queueData = queueContext.getQueueData();
						if (Objects.nonNull(queueData)) {
							Long expireTime = queueData.getExpireTime();
							String cacheKey = queueData.getCacheKey();
							
							/**
							 * 如果在缓存中，则进行处理
							 */
							if (Objects.nonNull(cacheKey) && expiryMap.get(cacheKey) != null
									&& queueData.getCurrentRetryCount() == 0) {
								log.info("The current operation time is still within the expiration time！no handle: {}", queueData);
								MetricsConfig.outQueueCounter.labels(MetricsConfig.NOHANDLE_LABEL).inc();
								return 0;
							}
							
							if ( queueData.getCurrentRetryCount() > 0) {
								Thread.sleep(queueContext.getQueueData().getCurrentRetryCount() * 1000);
							}
							
							/**
							 * 如果该数据带了过期时间和缓存key, 则加入缓存, 没配置时间则默认2分钟执行一次
							 */
							if (Objects.nonNull(cacheKey) && cacheKey.length() > 0
									&& Objects.nonNull(expireTime) && expireTime > 0) {
								expiryMap.put(cacheKey, Byte.parseByte("0"), expireTime);
							}
						}
						
						QueueContextManager.executeContext(queueContext);
					} catch (Exception e) {
						log.error("execute DataConsumer error: {}, {}", e.getMessage(), e);
						if (Objects.nonNull(queueData) && queueData.isHasRetry()) {
							try {
								boolean addSuccess = queueData.addFailCount();
								if (addSuccess) {
									String currentTopic = queueData.getTipoc();
									if (Objects.isNull(currentTopic)) {
										addQueueData(queueData, queueContext.getQueueCallback());
									} else {
										addTopicQueueData(currentTopic, queueData, queueContext.getQueueCallback());
									}
								} else {
									if (Objects.nonNull(queueData.getFailCallback())) {
										queueData.getFailCallback().callback(queueData, new RuntimeException(e.getMessage()));
									}
								}
							} catch (Exception e1) {
								log.error("execute fail callback error: {}, {}", e1.getMessage(), e1);
							}
						}
					} finally {
						MDC.clear();
						if (queueData != null) {
							synchronized (queueData) {
								queueData.notifyAll();
							}
						}
					}
					return 1;
				}, executorService);
			} catch (InterruptedException e) {
				log.error("DataConsumer execute error: {}, {}", e.getMessage(), e);
			}
			
		}
		hasRunning.set(false);
		runningMap.put(topic, hasRunning);
	}
}
