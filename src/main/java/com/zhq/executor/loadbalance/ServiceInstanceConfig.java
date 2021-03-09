package com.zhq.executor.loadbalance;

import com.zhq.executor.util.ExpiryMap;
import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author: zhenghq
 * @date: 2021/2/7
 * @version: 1.0.0
 */
public class ServiceInstanceConfig {
	
	private static ExpiryMap<String, List<ServiceInstance>> serviceInstanceMap = new ExpiryMap<>();
	
	public static Map getServiceInstanceMap() {
		return serviceInstanceMap;
	}
	
	public static void add(String serviceId, String host, int port) {
		
		String instanceId = String.format("%s:%s", host, port);
		List<ServiceInstance> serviceInstanceList = serviceInstanceMap.getOrDefault(serviceId, new ArrayList<>());
		serviceInstanceList.add(new DefaultServiceInstance(instanceId, serviceId, host, port, false));
		
		serviceInstanceMap.put(serviceId, serviceInstanceList);
		
	}
	
	public static void add(String serviceId, String servers) {
		
		if (Objects.isNull(servers)) return;
		
		for (String server : servers.split(",")) {
			String[] hostPort = server.split("\\:");
			String host = hostPort[0];
			Integer port = 80;
			if (hostPort.length == 2) {
				port = Integer.parseInt(hostPort[1]);
			}
			add(serviceId, host, port);
		}
	}
	
	public static void add(DefaultServiceInstance serviceInstance) {
		if (serviceInstance == null) {
			return;
		}
		
		String serviceId = serviceInstance.getServiceId();
		List<ServiceInstance> serviceInstanceList = serviceInstanceMap.getOrDefault(serviceId, new ArrayList<>());
		serviceInstanceList.add(serviceInstance);
		
		serviceInstanceMap.put(serviceId, serviceInstanceList);
	}
	
	public static void addList(List<DefaultServiceInstance> serviceInstanceList) {
		if (CollectionUtils.isEmpty(serviceInstanceList)) {
			return;
		}
		
		Map<String, List<ServiceInstance>> serviceInstanceGroup = serviceInstanceList.stream()
				.collect(Collectors.groupingBy(p -> p.getServiceId()));
		
		serviceInstanceMap.putAll(serviceInstanceGroup);
	}
	
	
	public static List<ServiceInstance> getServiceInstanceList(String serviceId) {
		return serviceInstanceMap.getOrDefault(serviceId, Collections.emptyList());
	}
	
}
