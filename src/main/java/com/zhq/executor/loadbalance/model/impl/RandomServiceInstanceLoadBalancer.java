package com.zhq.executor.loadbalance.model.impl;

import com.zhq.executor.loadbalance.model.Response;
import com.zhq.executor.loadbalance.model.ServiceInstance;
import com.zhq.executor.loadbalance.model.ServiceInstanceListSupplier;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: zhenghq
 * @date: 2021/3/11
 * @version: 1.0.0
 */
@Slf4j
public class RandomServiceInstanceLoadBalancer {
	
	private ServiceInstanceListSupplier supplier;
	
	private final AtomicInteger position;
	
	public RandomServiceInstanceLoadBalancer(ServiceInstanceListSupplier supplier) {
		this.supplier = supplier;
		this.position = new AtomicInteger((new Random()).nextInt(1000));
	}
	
	public Mono<Response<ServiceInstance>> choose() {
		return supplier.get().next().map((serviceInstances) -> {
			return this.processInstanceResponse(supplier, serviceInstances);
		});
	}
	
	
	private Response<ServiceInstance> getInstanceResponse(List<ServiceInstance> instances) {
		if (instances.isEmpty()) {
			return new EmptyResponse();
		} else {
			int pos = Math.abs(this.position.incrementAndGet());
			ServiceInstance instance = (ServiceInstance)instances.get(pos % instances.size());
			return new DefaultResponse(instance);
		}
	}
	
	private Response<ServiceInstance> processInstanceResponse(ServiceInstanceListSupplier supplier, List<ServiceInstance> serviceInstances) {
		Response<ServiceInstance> serviceInstanceResponse = this.getInstanceResponse(serviceInstances);
		
		return serviceInstanceResponse;
	}

}
