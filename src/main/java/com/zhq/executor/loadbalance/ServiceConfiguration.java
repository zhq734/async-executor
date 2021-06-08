package com.zhq.executor.loadbalance;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: zhenghq
 * @date: 2021/2/7
 * @version: 1.0.0
 */
@Configuration
@ConfigurationProperties(prefix = "spring.cloud.service", ignoreInvalidFields = true)
public class ServiceConfiguration {
	
	private List<ServiceInstanceInfo> instances = new ArrayList<>();
	
	@PostConstruct
	public void initSupplier() {
		instances.stream().forEach(p -> {
			ServiceInstanceConfig.add(p.getName(), p.getServers());
		});
	}
	
	public static class ServiceInstanceInfo {
		
		private String name;
		
		private String servers;
		
		public String getName() {
			return name;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		
		public String getServers() {
			return servers;
		}
		
		public void setServers(String servers) {
			this.servers = servers;
		}
	}
	
	public List<ServiceInstanceInfo> getInstances() {
		return instances;
	}
	
	public void setInstances(List<ServiceInstanceInfo> instances) {
		this.instances = instances;
	}
}
