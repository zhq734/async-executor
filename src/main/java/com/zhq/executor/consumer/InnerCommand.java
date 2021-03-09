package com.zhq.executor.consumer;

import com.zhq.executor.core.QueueData;
import com.zhq.executor.util.ExecutorSpringUtil;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.encoder.org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

/**
 * @author: zhenghq
 * @date: 2021/2/2
 * @version: 1.0.0
 */
@Slf4j
public abstract class InnerCommand {
	
	private static final Map<String, Class> topicClassMap = new HashMap<>();
	private static final Map<String, BlockingQueue> topicQueueMap = new HashMap<>();
	
	private String topic;
	
	public InnerCommand(String topic, Class clz) {
		if (StringUtils.isBlank(topic) || clz == null) return;
		
		topic = topic.toUpperCase();
		if (!topicClassMap.containsKey(topic)) {
			if (clz.isInstance(this)) {
				log.info("register innerCommand : msgType={}, clz={}", topic, clz.getSimpleName());
				topicClassMap.put(topic, clz);
				topicQueueMap.put(topic, new LinkedBlockingQueue());
				this.topic = topic;
			} else {
				throw new RuntimeException(String.format("this class: %s is not InnerCommand instance, mstType: %s",
						clz.getSimpleName(), topic));
			}
		} else {
			throw new RuntimeException(String.format("current topic [%s] has exist in class [%s], not repeat", topic,
					topicClassMap.get(topic).getSimpleName()));
		}
	}
	
	public abstract Object process(QueueData queueData);
	
	public void addQueueData(QueueData queueData) {
		DataConsumer.addTopicQueueData(this.topic, queueData, (p) -> {
			process(p);
		});
	}
	
	/**
	 * 获取注册的topic 列表
	 * @return
	 */
	public static List<String> getTopicList() {
		return topicClassMap.keySet().stream().collect(Collectors.toList());
	}
	
	/**
	 * 获取topic对应的队列
	 * @param topic
	 * @return
	 */
	public static BlockingQueue getTopicQueue(String topic) {
		return topicQueueMap.get(topic);
	}
	
	/**
	 * 获取实例对象
	 * @param topic
	 * @return
	 */
	public static InnerCommand getInstance(String topic) {
		if (StringUtils.isBlank(topic)) return null;
		
		topic = topic.toUpperCase();
		if (topicClassMap.containsKey(topic)) {
			// 这边要从Spring容器中获取对象才行，不然Spring注入的对象无法成功注入
			return (InnerCommand) ExecutorSpringUtil.getBean(topicClassMap.get(topic));
		}
		
		return null;
	}
	
	
	
}
