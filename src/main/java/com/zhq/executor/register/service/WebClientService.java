package com.zhq.executor.register.service;

import com.alibaba.fastjson.JSON;
import com.zhq.executor.loadbalance.filter.ExcutorLoadBalancedExchangeFilterFunction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * @author: zhenghq
 * @date: 2021/3/5
 * @version: 1.0.0
 */
@Slf4j
@Service
public class WebClientService {
	
	
	public Mono<String> requestInvoke(String url, MultiValueMap<String, String> params) {
		log.debug("WebClientService requestInvoke params: url={}, params={}", url, JSON.toJSONString(params));
		
		
		Mono<String> result = null;
		
		try {
			result = WebClient.builder().filter(new ExcutorLoadBalancedExchangeFilterFunction())
					.build().post()
					.uri(url)
					.bodyValue(params)
					.retrieve().bodyToMono(String.class);
			
			log.debug("requestInvoke result: {}", result.block());
		} catch (Exception e) {
			log.error("requestInvoke error: {}", e.getMessage(), e);
		}
		
		return result;
	}
	
	
	
}
