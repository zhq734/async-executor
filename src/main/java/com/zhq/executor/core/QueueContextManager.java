package com.zhq.executor.core;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalListener;
import com.google.common.primitives.UnsignedInteger;
import com.zhq.executor.config.MetricsConfig;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
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
	/**
	 * 记录logback日志中的MDC信息
	 */
	private Map<UnsignedInteger, Map<String, String>> MDCInfoMap = new HashMap<>();
	
	
	/**
	 * 采用枚举类实现单例对象初始化
	 */
	private enum InnerContext {
		INSTANCE;
		private QueueContextManager instance;
		
		InnerContext() {
			this.instance = new QueueContextManager();
		}
		
		private QueueContextManager getSingleton() {
			return instance;
		}
		
	}
	
	public static QueueContextManager getInstance() {
		return QueueContextManager.InnerContext.INSTANCE.getSingleton();
	}
	
	private QueueContextManager() {
		initCache();
	}
	
	
//	private volatile static QueueContextManager singleton = null;
//
//
//	private QueueContextManager(int concurrencyLevel, int initialCapacity, long maximumSize, long duration) {
//		this.concurrencyLevel = concurrencyLevel;
//		this.initialCapacity = initialCapacity;
//		this.maximumSize = maximumSize;
//		this.duration = duration;
//
//		initCache();
//	}
//
//	public static QueueContextManager getInstance() {
//		return getInstance(4, 1000, 100000, 30);
//	}
//
//	public static QueueContextManager getInstance(int concurrencyLevel, int initialCapacity, long maximumSize, long duration) {
//		if (singleton == null) {
//			synchronized (QueueContextManager.class) {
//				if (singleton == null) {
//					singleton = new QueueContextManager(concurrencyLevel, initialCapacity, maximumSize, duration);
//				}
//			}
//		}
//
//		return singleton;
//	}
	
	private Cache<UnsignedInteger, QueueContext> contextCache = null;
	
	/**
	 * 初始化contextCache
	 */
	private void initCache() {
		log.info("QueueContextManager initCache......");
		contextCache = Caffeine.newBuilder()
				.initialCapacity(initialCapacity)
				.maximumSize(maximumSize)
				.expireAfterWrite(duration, TimeUnit.SECONDS)
				.removalListener(contextRemovalListener)
				.build();
	}
	
	/**
	 * contextCache 缓存移除监听
	 */
	private RemovalListener<UnsignedInteger, QueueContext> contextRemovalListener = (key, value, cause) -> {
		
		try {
			log.info("request reqNum: {} is {}", key, cause);
			switch (cause) {
				case EXPLICIT:   // 手动删除
					break;
				case REPLACED:    // 手动替换
					break;
				case COLLECTED:    // 被垃圾回收
					if (Objects.nonNull(value)) {
						addContext(value.getQueueData(), value.getQueueCallback());
					}
					break;
				case EXPIRED:      // 超时过期
					/**
					 * 这边后续通过策略方式走
					 * 判断是否是抛弃还是执行
					 */
					MetricsConfig.outQueueCounter.labels(MetricsConfig.EXPIRE_LABEL).inc();
					executeContext(value);
					break;
				case SIZE:         // 由于缓存大小限制
					log.warn("context cache is full!!!");
					if (Objects.nonNull(value)) {
						addContext(value.getQueueData(), value.getQueueCallback());
					}
					break;
				default: break;
				
			}
		} catch (Exception e) {
			log.error("contextRemovalListener error: {}", e.getMessage(), e);
		}
	};
	
	/**
	 * 往缓存中添加数据
	 * @param seqNum
	 * @param queueContext
	 */
	private void pushContext(UnsignedInteger seqNum, QueueContext queueContext) {
		if (Objects.nonNull(queueContext)) {
			if (Objects.isNull(contextCache)) {
				initCache();
			}
			contextCache.put(seqNum, queueContext);
			Map<String, String> currentMDCMap = MDC.getCopyOfContextMap();
			MDCInfoMap.put(seqNum, currentMDCMap == null ? Collections.emptyMap() : currentMDCMap);
		}
	}
	
	/**
	 * 往缓存中移除数据
	 * @param seqNum
	 * @return
	 */
	private synchronized QueueContext popContext(UnsignedInteger seqNum) {
		QueueContext queueContext = contextCache.getIfPresent(seqNum);
		contextCache.invalidate(seqNum);
		MDC.setContextMap(MDCInfoMap.remove(seqNum));
		
		return queueContext;
	}
	
	/**
	 * 往缓存中添加数据
	 * @param queueData
	 * @param queueCallback
	 * @return
	 */
	public UnsignedInteger addContext(QueueData queueData, QueueExecutor queueCallback) {
		UnsignedInteger seqNum = QueueContextSeqNumUtil.getSeqNum();
		
		QueueContext queueContext = new QueueContext(queueData, queueCallback);
		pushContext(seqNum, queueContext);
		
		if (Objects.nonNull(queueData)) {
			if (Objects.nonNull(queueData.getCacheKey())) {
				MetricsConfig.inQueueCounter.labels(queueData.getCacheKey()).inc();
			} else {
				MetricsConfig.inQueueCounter.labels(MetricsConfig.DEFAULT_LABEL).inc();
			}
		} else {
			MetricsConfig.inQueueCounter.labels(MetricsConfig.DEFAULT_LABEL).inc();
		}
		
		return seqNum;
	}
	
	/**
	 * 清除缓存数据
	 */
	public void clearContext() {
		contextCache.invalidateAll();
		MDCInfoMap.clear();
	}
	
	/**
	 * 获取指定序列号的缓存数据，并将其移除
	 * @param seqNum
	 * @return
	 */
	public QueueContext getContext(UnsignedInteger seqNum) {
		QueueContext queueContext = popContext(seqNum);
		
		return queueContext;
	}
	
	/**
	 * 调用执行
	 * @param queueContext
	 */
	public static void executeContext(QueueContext queueContext) {
		
		if (Objects.nonNull(queueContext)) {
			QueueExecutor queueCallback = queueContext.getQueueCallback();
			
			if (Objects.nonNull(queueCallback)) {
				QueueData queueData = queueContext.getQueueData();
				if (Objects.nonNull(queueData)) {
					queueData.setExecuteDate(new Date());
					
					String cacheKey = queueData.getCacheKey();
					if (Objects.nonNull(cacheKey)) {
						MetricsConfig.outQueueCounter.labels(cacheKey).inc();
					} else {
						MetricsConfig.outQueueCounter.labels(MetricsConfig.DEFAULT_LABEL).inc();
					}
				} else {
					MetricsConfig.outQueueCounter.labels(MetricsConfig.DEFAULT_LABEL).inc();
				}
				
				/**
				 * 执行回调
				 */
				queueCallback.execute(queueData);
			} else {
				MetricsConfig.outQueueCounter.labels(MetricsConfig.NOHANDLE_LABEL).inc();
			}
		} else {
			MetricsConfig.outQueueCounter.labels(MetricsConfig.NOHANDLE_LABEL).inc();
		}
		
	}



}
