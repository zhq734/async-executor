package com.zhq.executor.facade;

import com.zhq.executor.callback.DefaultExecuteFailCallback;
import com.zhq.executor.consumer.DataConsumer;
import com.zhq.executor.core.QueueData;
import com.zhq.executor.core.QueueExecutor;
import com.zhq.executor.executor.DefaultQueueExecutor;
import com.zhq.executor.util.TextFormat;
import io.prometheus.client.CollectorRegistry;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author: zhenghq
 * @date: 2019/9/6
 * @version: 1.0.0
 */
@RequestMapping
@RestController
public class TestFacade {
	
	
	@ResponseBody
	@RequestMapping("/test")
	public Object test() {
		
		
		QueueData queueData = new QueueData();
		
		Map<String, String> data = new HashMap<>();
		data.put("aaa", "bbbbb");
		data.put("11111", "22222");
		
		queueData.setData(data);
		
		
		DataConsumer.addQueueData(queueData, new DefaultQueueExecutor());
		
		
		queueData = new QueueData();
		
		data = new HashMap<>();
		data.put("ccccc", "ddddd");
		data.put("22222", "44444");
		
		queueData.setData(data);
		
		queueData.setCacheKey("test001");
		queueData.setExpireTime(60 * 1000L);
		queueData.setFailCallback(new DefaultExecuteFailCallback());
		
		DataConsumer.addQueueData(queueData, new QueueExecutor() {
			@Override
			public void execute(QueueData queueData) {
				System.out.println(queueData);
				throw new RuntimeException("hahahahahahahahahaha");
			}
		});
		
		
		/*QueueData finalQueueData = queueData;
		
		TestShutdownHook.getTestShutdownHook().addDestroyCallback(new TestShutdownHook.ShutdownCallbackFunc() {
			
			@Override
			public void call() {
				System.out.println("ababa巴巴爸爸爸爸");
				System.out.println(finalQueueData);
			}
		});
		
		TestShutdownHook.getTestShutdownHook().addDestroyCallback(() -> {
			System.out.println("啦啦啦啦啦啦啦啦");
			System.out.println(finalQueueData);
			
			
		});
		
		
		System.out.println(TestShutdownHook.currentThread().getName());
		System.out.println(Thread.currentThread().getName());*/
		
		
		
		
//		TestShutdownHook.getTestShutdownHook().register();
		
		
		// 注入钩子线程
//		Runtime.getRuntime().addShutdownHook(new Thread(){
//
//			@Override
//			public void run(){
//
//				System.out.println("The hook thread 1 is running ....");
//
//				try {
//					TimeUnit.SECONDS.sleep(1);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//
//
//				System.out.println("The hook thread 1 is exit  ....");
//
//			}
//
//
//		});
//
//
//
//		// 钩子线程可以注册多个
//		Runtime.getRuntime().addShutdownHook(new Thread(){
//
//			@Override
//			public void run(){
//
//				System.out.println("The hook thread 2 is running ....");
//
//				try {
//					TimeUnit.SECONDS.sleep(10);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//
//
//				System.out.println("The hook thread 2 is exit  ....");
//
//			}
//
//
//		});

		
		return "aaaaa";
	}
	
	
	private CollectorRegistry registry = CollectorRegistry.defaultRegistry;
	@RequestMapping("/metrics")
	public void metrics(HttpServletResponse response, HttpServletRequest request) throws IOException {
		response.setStatus(200);
		response.setContentType("text/plain; version=0.0.4; charset=utf-8");
		PrintWriter writer = response.getWriter();
		try {
			TextFormat.write004(writer, this.registry.filteredMetricFamilySamples(this.parse(request)));
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			writer.close();
		}
	}
	private Set<String> parse(HttpServletRequest req) {
		String[] includedParam = req.getParameterValues("name[]");
		return (Set) (includedParam == null ? Collections.emptySet() : new HashSet(Arrays.asList(includedParam)));
	}
	
}
