package com.zhq.executor.loadbalance.model.impl;

import com.zhq.executor.loadbalance.model.ServiceInstance;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author: zhenghq
 * @date: 2021/3/11
 * @version: 1.0.0
 */
public class DefaultServiceInstance implements ServiceInstance {
	private String instanceId;
	private String serviceId;
	private String host;
	private int port;
	private boolean secure;
	private Map<String, String> metadata;
	private URI uri;
	
	public DefaultServiceInstance(String instanceId, String serviceId, String host, int port, boolean secure, Map<String, String> metadata) {
		this.metadata = new LinkedHashMap();
		this.instanceId = instanceId;
		this.serviceId = serviceId;
		this.host = host;
		this.port = port;
		this.secure = secure;
		this.metadata = metadata;
	}
	
	public DefaultServiceInstance(String instanceId, String serviceId, String host, int port, boolean secure) {
		this(instanceId, serviceId, host, port, secure, new LinkedHashMap());
	}
	
	public DefaultServiceInstance() {
		this.metadata = new LinkedHashMap();
	}
	
	public static URI getUri(ServiceInstance instance) {
		String scheme = instance.isSecure() ? "https" : "http";
		String uri = String.format("%s://%s:%s", scheme, instance.getHost(), instance.getPort());
		return URI.create(uri);
	}
	
	@Override
	public URI getUri() {
		return getUri(this);
	}
	
	@Override
	public Map<String, String> getMetadata() {
		return this.metadata;
	}
	
	@Override
	public String getInstanceId() {
		return this.instanceId;
	}
	
	@Override
	public String getServiceId() {
		return this.serviceId;
	}
	
	@Override
	public String getHost() {
		return this.host;
	}
	
	@Override
	public int getPort() {
		return this.port;
	}
	
	@Override
	public boolean isSecure() {
		return this.secure;
	}
	
	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}
	
	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}
	
	public void setHost(String host) {
		this.host = host;
	}
	
	public void setPort(int port) {
		this.port = port;
	}
	
	public void setUri(URI uri) {
		this.uri = uri;
		this.host = this.uri.getHost();
		this.port = this.uri.getPort();
		String scheme = this.uri.getScheme();
		if ("https".equals(scheme)) {
			this.secure = true;
		}
		
	}
	
	@Override
	public String toString() {
		return "DefaultServiceInstance{instanceId='" + this.instanceId + '\'' + ", serviceId='" + this.serviceId + '\'' + ", host='" + this.host + '\'' + ", port=" + this.port + ", secure=" + this.secure + ", metadata=" + this.metadata + '}';
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (o != null && this.getClass() == o.getClass()) {
			DefaultServiceInstance that = (DefaultServiceInstance)o;
			return this.port == that.port && this.secure == that.secure && Objects.equals(this.instanceId, that.instanceId) && Objects.equals(this.serviceId, that.serviceId) && Objects.equals(this.host, that.host) && Objects.equals(this.metadata, that.metadata);
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(new Object[]{this.instanceId, this.serviceId, this.host, this.port, this.secure, this.metadata});
	}
}
