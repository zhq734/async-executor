package com.zhq.executor.loadbalance;

import com.zhq.executor.loadbalance.model.ServiceInstance;
import com.zhq.executor.loadbalance.model.ServiceInstanceListSupplier;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * @author: zhenghq
 * @date: 2021/2/7
 * @version: 1.0.0
 */
@Slf4j
public class ExcutorServiceInstanceListSuppler implements ServiceInstanceListSupplier {

	private final String serviceId;

	public ExcutorServiceInstanceListSuppler(String serviceId) {
		this.serviceId = serviceId;
	}

	@Override
	public String getServiceId() {
		return serviceId;
	}

	@Override
	public Flux<List<ServiceInstance>> get() {
		log.info("SayHelloServiceInstanceListSuppler get: serviceId={}", serviceId);

		return Flux.just(ServiceInstanceConfig.getServiceInstanceList(serviceId));
	}

}
