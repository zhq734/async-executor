package com.zhq.executor.loadbalance.model.impl;

import com.zhq.executor.loadbalance.model.Response;
import com.zhq.executor.loadbalance.model.ServiceInstance;
import org.springframework.core.style.ToStringCreator;

import java.util.Objects;

/**
 * @author: zhenghq
 * @date: 2021/3/11
 * @version: 1.0.0
 */
public class DefaultResponse implements Response<ServiceInstance> {
	private final ServiceInstance serviceInstance;
	
	public DefaultResponse(ServiceInstance serviceInstance) {
		this.serviceInstance = serviceInstance;
	}
	
	@Override
	public boolean hasServer() {
		return this.serviceInstance != null;
	}
	
	@Override
	public ServiceInstance getServer() {
		return this.serviceInstance;
	}
	
	@Override
	public String toString() {
		ToStringCreator to = new ToStringCreator(this);
		to.append("serviceInstance", this.serviceInstance);
		return to.toString();
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (!(o instanceof DefaultResponse)) {
			return false;
		} else {
			DefaultResponse that = (DefaultResponse)o;
			return Objects.equals(this.serviceInstance, that.serviceInstance);
		}
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(new Object[]{this.serviceInstance});
	}
}
