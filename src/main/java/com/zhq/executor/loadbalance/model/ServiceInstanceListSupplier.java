package com.zhq.executor.loadbalance.model;

import reactor.core.publisher.Flux;

import java.util.List;
import java.util.function.Supplier;

/**
 * @author: zhenghq
 * @date: 2021/3/11
 * @version: 1.0.0
 */
public interface ServiceInstanceListSupplier extends Supplier<Flux<List<ServiceInstance>>> {
		String getServiceId();

		default Flux<List<ServiceInstance>> get() {
			return (Flux)this.get();
		}
}
