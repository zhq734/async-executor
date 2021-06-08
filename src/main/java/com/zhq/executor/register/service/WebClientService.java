package com.zhq.executor.register.service;

import com.alibaba.fastjson.JSON;
import com.zhq.executor.loadbalance.filter.ExcutorLoadBalancedExchangeFilterFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * @author: zhenghq
 * @date: 2021/3/5
 * @version: 1.0.0
 */
@Service
public class WebClientService {
	
	private static final Logger log = LoggerFactory.getLogger(WebClientService.class);
	
	
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
