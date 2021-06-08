package com.zhq.executor.register.model;

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
	
	
	public String getServiceId() {
		return serviceId;
	}
	
	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}
	
	public String getHost() {
		return host;
	}
	
	public void setHost(String host) {
		this.host = host;
	}
	
	public int getPort() {
		return port;
	}
	
	public void setPort(int port) {
		this.port = port;
	}
	
	public String getTopic() {
		return topic;
	}
	
	public void setTopic(String topic) {
		this.topic = topic;
	}
	
	@Override
	public String toString() {
		return "ServiceInfo{" +
				"serviceId='" + serviceId + '\'' +
				", host='" + host + '\'' +
				", port=" + port +
				", topic='" + topic + '\'' +
				'}';
	}
}
