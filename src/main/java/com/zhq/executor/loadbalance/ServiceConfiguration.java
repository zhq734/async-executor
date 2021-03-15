package com.zhq.executor.loadbalance;

import lombok.Data;
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
@Data
public class ServiceConfiguration {
	
	private List<ServiceInstanceInfo> instances = new ArrayList<>();
	
	@PostConstruct
	public void initSupplier() {
		instances.stream().forEach(p -> {
			ServiceInstanceConfig.add(p.getName(), p.getServers());
		});
	}
	
	@Data
	public static class ServiceInstanceInfo {
		
		private String name;
		
		private String servers;
		
	}


}
