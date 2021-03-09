package com.zhq.executor.register;

import com.alibaba.fastjson.JSON;
import com.zhq.executor.config.CloudServerConfig;
import com.zhq.executor.consumer.DataConsumer;
import com.zhq.executor.consumer.InnerCommand;
import com.zhq.executor.core.QueueData;
import com.zhq.executor.loadbalance.ServiceInstanceConfig;
import com.zhq.executor.register.model.ResultDto;
import com.zhq.executor.register.model.ServiceInfo;
import com.zhq.executor.register.service.WebClientService;
import com.zhq.executor.util.ExpiryMap;
import com.zhq.executor.util.NetworkUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author: zhenghq
 * @date: 2021/2/5
 * @version: 1.0.0
 */
@Slf4j
@Component
public class ServiceDiscovery {
	
	@Resource
	private CloudServerConfig cloudServerConfig;
	
	private static WebClientService webClientService;
	
	@Resource
	public void setWebClientService(WebClientService webClientService) {
		ServiceDiscovery.webClientService = webClientService;
	}
	
	private static ExpiryMap<String, ExpiryMap<String, String>> topicServiceMap = new ExpiryMap<>();
	
	private int localPort;
	
	/**
	 *
	 */
	@PostConstruct
	public void initServer() {
		
		localPort = NetworkUtil.getVaildPort(NetworkUtil.getLocalHost(), cloudServerConfig.getLocalPort());
		serverHandle();
		heartbeat();
	}
	
	private void serverHandle() {
		
		VirtualHttpServer server = new VirtualHttpServer(localPort);
		server.start(new ServerInBoundHandler() {
			public void handle(RequestHook requestHook, ResponseHook responseHook) {
				String uri = requestHook.getUri();
				Map<String, String> params = requestHook.getRequestParams();
				/**
				 * 处理心跳
				 */
				if (uri.equals("/executor/client/heartbeat")) {
					log.debug("handle heartbeat msg: {}", JSON.toJSONString(params));
					ServiceInfo serviceInfo = JSON.parseObject(JSON.toJSONString(params), ServiceInfo.class);
				
					/**
					 * 动态注册路由
					 */
					ServiceInstanceConfig.add(serviceInfo.getServiceId(), serviceInfo.getHost(), serviceInfo.getPort());
					
					
					putData(serviceInfo.getTopicList(), serviceInfo.getServiceId());
					responseHook.setBody(ResultDto.success()).response();
					return;
				}
				
				/**
				 * 处理任务
				 */
				if (uri.equals("/executor/client/handleTask")) {
					log.debug("handle handleTask msg: {}", JSON.toJSONString(params));
					String topic = params.get("topic");
					String data = params.get("data");
					
					InnerCommand innerCommand = InnerCommand.getInstance(topic);
					if (innerCommand != null) {
						QueueData queueData = new QueueData();
						queueData.setData(data);
						innerCommand.addQueueData(queueData);
					}
					
					responseHook.setBody(ResultDto.success()).response();
					return;
				}
				
				responseHook.setBody(ResultDto.success()).response();
			}
		});
	}
	
	
	private void putData(List<String> topicList, String serviceId) {
		
		topicList.stream().forEach(topic -> {
			ExpiryMap<String, String> currentData = topicServiceMap.getOrDefault(topic, new ExpiryMap<>());
			currentData.put(serviceId, serviceId);
			
			topicServiceMap.put(topic, currentData);
		});
	}
	
	private static List<String> getServiceIdListByTopic(String topic) {
		
		ExpiryMap<String, String> currentData = topicServiceMap.getOrDefault(topic, new ExpiryMap<>());
		
		return currentData.keySet().stream().collect(Collectors.toList());
	}
	
	
	/**
	 * 发送心跳
	 */
	private void heartbeat() {
		
		if (StringUtils.isBlank(cloudServerConfig.getServerHost())) {
			log.info("serverHost is not config");
			return;
		}
		/**
		 * 如果配置了serverHost, 则每隔一定时间上报心跳数据
		 * host
		 * port
		 * serviceId
		 * topic
		 */
		ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
		
		log.warn("execute {}/executor/client/heartbeat", cloudServerConfig.getServerHost());
		/**
		 * 30秒调用一次
		 */
		executorService.scheduleAtFixedRate(() -> {
			
			String api = "/executor/client/heartbeat";
			MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
			params.add("topic", StringUtils.join(InnerCommand.getTopicList(), ","));
			params.add("host", NetworkUtil.getLocalHost());
			params.add("port", localPort + "");
			params.add("serviceId", cloudServerConfig.getLocalServiceId());
			
			String url = cloudServerConfig.getServerHost() + api;
			try {
				Mono<String> result = WebClient.builder()
						.build().post()
						.uri(url)
						.bodyValue(params)
						.retrieve().bodyToMono(String.class);
				
				log.debug("heartbeat result: {}", result.block());
			} catch (Exception e) {
				log.error("requestInvoke error: {}", e.getMessage());
			}
		}, 0, 30, TimeUnit.SECONDS);
		
		
	
	}
	
	/**
	 * 调用远程服务
	 * @param topic
	 * @param data
	 */
	public static void handleTask(String topic, Object data) {
		
		if (StringUtils.isBlank(topic)) {
			return;
		}
		
		/**
		 * 根据topic 获取服务列表
		 */
		List<String> serviceIdList = getServiceIdListByTopic(topic);
		
		if (CollectionUtils.isEmpty(serviceIdList)) {
			log.info("current topic not bind service: {}", topic);
			return;
		}
		
		serviceIdList.stream().forEach(serviceId -> {
			DataConsumer.addQueueData(new QueueData(), (p) -> {
				String url = "http://" + serviceId;
				
				/**
				 * 循环遍历，异步调用
				 */
				String api = "/executor/client/handleTask";
				MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
				params.add("topic", topic);
				params.add("data", JSON.toJSONString(data));
				
				webClientService.requestInvoke(url + api, params);
			});
		});
		
		
	}
	
}
