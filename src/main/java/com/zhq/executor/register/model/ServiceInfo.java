package com.zhq.executor.register.model;

import lombok.Data;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author: zhenghq
 * @date: 2021/3/4
 * @version: 1.0.0
 */
@Data
public class ServiceInfo {
	
	private String serviceId;
	
	private String host;
	
	private int port;
	
	private String topic;
	
	public List<String> getTopicList() {
		if (Objects.isNull(topic)) return Collections.emptyList();
		
		return Stream.of(topic.split(",")).collect(Collectors.toList());
	}
	
	public String getInstanceId() {
		return host + ":" + port;
	}
	
	
}
