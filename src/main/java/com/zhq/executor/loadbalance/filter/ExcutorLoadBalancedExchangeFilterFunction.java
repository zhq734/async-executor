package com.zhq.executor.loadbalance.filter;

import com.zhq.executor.loadbalance.ExcutorServiceInstanceListSuppler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerUriTools;
import org.springframework.cloud.client.loadbalancer.reactive.LoadBalancedExchangeFilterFunction;
import org.springframework.cloud.loadbalancer.core.ReactorServiceInstanceLoadBalancer;
import org.springframework.cloud.loadbalancer.core.RoundRobinLoadBalancer;
import org.springframework.cloud.loadbalancer.support.SimpleObjectProvider;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

import java.net.URI;

/**
 * @author: zhenghq
 * @date: 2021/2/7
 * @version: 1.0.0
 */
@Slf4j
public class ExcutorLoadBalancedExchangeFilterFunction implements LoadBalancedExchangeFilterFunction {
	
	public Mono<ClientResponse> filter(ClientRequest clientRequest, ExchangeFunction next) {
		URI originalUrl = clientRequest.url();
		String serviceId = originalUrl.getHost();
		log.info("serviceId: {}", serviceId);
		if (serviceId == null) {
			String message = String.format("Request URI does not contain a valid hostname: %s", originalUrl.toString());
			log.warn(message);
			
			return Mono.just(ClientResponse.create(HttpStatus.BAD_REQUEST).body(message).build());
		} else {
			
			ExcutorServiceInstanceListSuppler suppler = new ExcutorServiceInstanceListSuppler(serviceId);
			SimpleObjectProvider simpleObjectProvider = new SimpleObjectProvider(suppler);
//			ReactorServiceInstanceLoadBalancer randomLoadBalancer = new RandomLoadBalancer(simpleObjectProvider, serviceId);
			ReactorServiceInstanceLoadBalancer randomLoadBalancer = new RoundRobinLoadBalancer(simpleObjectProvider, serviceId);
			
			return randomLoadBalancer.choose().flatMap((lbResponse) -> {
				ServiceInstance instance = lbResponse.getServer();
				
				if (instance != null) {
					ClientRequest newRequest = buildClientRequest(clientRequest, instance, null, false);
					
					return next.exchange(newRequest);
				} else {
					return next.exchange(clientRequest);
				}
			});
		}
		
	}
	
	
	private static ClientRequest buildClientRequest(ClientRequest request, ServiceInstance serviceInstance, String instanceIdCookieName, boolean addServiceInstanceCookie) {
		URI originalUrl = request.url();
		return ClientRequest.create(request.method(), LoadBalancerUriTools.reconstructURI(serviceInstance, originalUrl)).headers((headers) -> {
			headers.addAll(request.headers());
		}).cookies((cookies) -> {
			cookies.addAll(request.cookies());
			if (instanceIdCookieName != null && instanceIdCookieName.length() != 0 && addServiceInstanceCookie) {
				cookies.add(instanceIdCookieName, serviceInstance.getInstanceId());
			}
			
		}).attributes((attributes) -> {
			attributes.putAll(request.attributes());
		}).body(request.body()).build();
	}
	
}
