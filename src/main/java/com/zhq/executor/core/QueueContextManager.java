package com.zhq.executor.core;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.primitives.UnsignedInteger;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author: zhenghq
 * @date: 2019/9/5
 * @version: 1.0.0
 */
@Slf4j
public class QueueContextManager {
	
	private int concurrencyLevel = 4;
	private int initialCapacity = 1000;
	private long maximumSize = 100000;
	private long duration = 30;
	
	private static QueueContextManager singleton = null;
	
	
	private QueueContextManager() {
	
	}
	
	private QueueContextManager(int concurrencyLevel, int initialCapacity, long maximumSize, long duration) {
		this.concurrencyLevel = concurrencyLevel;
		this.initialCapacity = initialCapacity;
		this.maximumSize = maximumSize;
		this.duration = duration;
	}
	
	public static QueueContextManager getInstance() {
		return getInstance(4, 1000, 100000, 30);
	}
	
	public static QueueContextManager getInstance(int concurrencyLevel, int initialCapacity, long maximumSize, long duration) {
		if (singleton == null) {
			synchronized (QueueContextManager.class) {
				if (singleton == null) {
					singleton = new QueueContextManager(concurrencyLevel, initialCapacity, maximumSize, duration);
					singleton.initCache();
				}
			}
		}
		
		return singleton;
	}
	
	private Cache<UnsignedInteger, QueueContext> contextCache = null;

	private void initCache() {
		contextCache = CacheBuilder.newBuilder()
				.concurrencyLevel(concurrencyLevel)
				.initialCapacity(initialCapacity)
				.maximumSize(maximumSize)
				.expireAfterWrite(duration, TimeUnit.SECONDS)
				.removalListener(contextRemovalListener)
				.build();
		
	}
	
	private RemovalListener<UnsignedInteger, QueueContext> contextRemovalListener = (removalNotification) -> {
		
		try {
			log.info("request reqNum: {} is {}", removalNotification.getKey(), removalNotification.getCause());
			switch (removalNotification.getCause()) {
				case EXPLICIT:   // 手动删除
					log.error("request reqNum: {} is deleted", removalNotification.getKey());
					break;
				case REPLACED:    // 手动替换
					log.error("request reqNum: {} is replaced", removalNotification.getKey());
					break;
				case COLLECTED:    // 被垃圾回收
					pushContext(removalNotification.getKey(), removalNotification.getValue());
					break;
				case EXPIRED:      // 超时过期
					QueueContext queueContext = removalNotification.getValue();
					if (Objects.nonNull(queueContext)) {
						QueueExecutor queueCallback = queueContext.getQueueCallback();
						if (Objects.nonNull(queueCallback)) {
							queueCallback.execute(queueContext.getQueueData());
						}
					}
					break;
				case SIZE:         // 由于缓存大小限制
					log.error("context cache is full!!!");
					pushContext(removalNotification.getKey(), removalNotification.getValue());
					break;
				default: break;
				
			}
		} catch (Exception e) {
			log.error("contextRemovalListener error: {}", e.getMessage(), e);
		}
	};
	
	
	private void pushContext(UnsignedInteger seqNum, QueueContext queueContext) {
		if (Objects.nonNull(queueContext)) {
			contextCache.put(seqNum, queueContext);
		}
	}
	
	private synchronized QueueContext popContext(UnsignedInteger seqNum) {
		QueueContext queueContext = contextCache.getIfPresent(seqNum);
		contextCache.invalidate(seqNum);
		
		return queueContext;
	}
	
	public UnsignedInteger addContext(QueueData queueData, QueueExecutor queueCallback) {
		UnsignedInteger seqNum = QueueContextSeqNumUtil.getSeqNum();
		
		QueueContext queueContext = new QueueContext(queueData, queueCallback);
		pushContext(seqNum, queueContext);
		
		return seqNum;
	}
	
	
	public QueueContext getContext(UnsignedInteger seqNum) {
		QueueContext queueContext = popContext(seqNum);
		
		return queueContext;
	}
	
	public static void executeContext(QueueContext queueContext) {
		
		if (Objects.nonNull(queueContext)) {
			QueueExecutor queueCallback = queueContext.getQueueCallback();
			
			if (Objects.nonNull(queueCallback)) {
				queueContext.getQueueData().setExecuteDate(new Date());
				queueCallback.execute(queueContext.getQueueData());
			}
		}
		
	}



}
