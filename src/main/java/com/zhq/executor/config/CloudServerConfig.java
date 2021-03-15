package com.zhq.executor.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author: zhenghq
 * @date: 2021/3/4
 * @version: 1.0.0
 */
@Component
@Data
public class CloudServerConfig {
	
	@Value("${executor.server.center.host:}")
	private String serverHost;
	
	@Value("${executor.client.port:20789}")
	private int localPort;
	
	@Value("${executor.client.serviceId:}")
	private String localServiceId;
	
	
}
