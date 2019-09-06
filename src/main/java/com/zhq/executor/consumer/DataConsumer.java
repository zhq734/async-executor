package com.zhq.executor.consumer;


import com.google.common.primitives.UnsignedInteger;
import com.zhq.executor.config.QueueConfig;
import com.zhq.executor.core.QueueContainer;
import com.zhq.executor.core.QueueContext;
import com.zhq.executor.core.QueueContextManager;
import com.zhq.executor.core.QueueData;
import com.zhq.executor.core.QueueExecutor;
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
	
	@Resource
	private QueueConfig queueConfig;
	
	private static BlockingQueue blockingQueue = new LinkedBlockingQueue();
	
	private ExecutorService executorService;
	
	@Override
	public void setQueueContainer(QueueContainer faceQueue) {
		executorService = Executors.newFixedThreadPool(queueConfig.getThreadCount());
	}
	
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
						if (queueData.getCurrentRetryCount() > 0) {
							Thread.sleep(queueContext.getQueueData().getCurrentRetryCount() * 1000);
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
