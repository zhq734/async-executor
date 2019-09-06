package com.zhq.executor.core;

import com.zhq.executor.consumer.IConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author: zhenghq
 * @date: 2019/7/24
 * @version: 1.0.0
 */
@Component
@Slf4j
public class QueueContainer {
	
	private ExecutorService executorService = Executors.newCachedThreadPool();
	@Autowired
	List<IConsumer> consumerList;
	
	@PostConstruct
	public void init() {
		log.info("QueueContainer start init......");
		consumerList.stream().forEach(consumer -> {
			log.info("add consumer: {}", consumer.getClass().getSimpleName());
			consumer.setQueueContainer(this);
			Thread tc= new Thread(consumer, consumer.getClass().getSimpleName());
			tc.start();
		});
	}
	
	public ExecutorService getExecutorService() {
		return executorService;
	}
}
