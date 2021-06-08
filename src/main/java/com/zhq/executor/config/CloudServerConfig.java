package com.zhq.executor.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author: zhenghq
 * @date: 2021/3/4
 * @version: 1.0.0
 */
@Component
public class CloudServerConfig {
	
	@Value("${executor.server.center.host:}")
	private String serverHost;
	
	@Value("${executor.client.port:20789}")
	private int localPort;
	
	@Value("${executor.client.serviceId:}")
	private String localServiceId;
	
	
	public String getServerHost() {
		return serverHost;
	}
	
	public void setServerHost(String serverHost) {
		this.serverHost = serverHost;
	}
	
	public int getLocalPort() {
		return localPort;
	}
	
	public void setLocalPort(int localPort) {
		this.localPort = localPort;
	}
	
	public String getLocalServiceId() {
		return localServiceId;
	}
	
	public void setLocalServiceId(String localServiceId) {
		this.localServiceId = localServiceId;
	}
	
	@Override
	public String toString() {
		return "CloudServerConfig{" +
				"serverHost='" + serverHost + '\'' +
				", localPort=" + localPort +
				", localServiceId='" + localServiceId + '\'' +
				'}';
	}
}
