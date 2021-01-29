package com.zhq.executor.core;

import com.zhq.executor.consumer.IConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @author: zhenghq
 * @date: 2019/7/24
 * @version: 1.0.0
 */
@Component
@Slf4j
public class QueueContainer {
	
	@Autowired
	List<IConsumer> consumerList;
	
	@PostConstruct
	public void init() {
		log.info("QueueContainer start init......");
		consumerList.stream().forEach(consumer -> {
			log.info("init consumer: {}", consumer.getClass().getSimpleName());
			consumer.init();
		});
	}
	
}
