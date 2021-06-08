package com.zhq.executor.loadbalance;

import com.zhq.executor.loadbalance.model.ServiceInstance;
import com.zhq.executor.loadbalance.model.ServiceInstanceListSupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * @author: zhenghq
 * @date: 2021/2/7
 * @version: 1.0.0
 */
public class ExcutorServiceInstanceListSuppler implements ServiceInstanceListSupplier {
	
	private static final Logger log = LoggerFactory.getLogger(ExcutorServiceInstanceListSuppler.class);

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
