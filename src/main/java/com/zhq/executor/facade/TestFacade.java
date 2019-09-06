package com.zhq.executor.facade;

import com.zhq.executor.callback.DefaultExecuteFailCallback;
import com.zhq.executor.consumer.DataConsumer;
import com.zhq.executor.core.QueueData;
import com.zhq.executor.core.QueueExecutor;
import com.zhq.executor.executor.DefaultQueueExecutor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

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
		queueData.setFailCallback(new DefaultExecuteFailCallback());
		
		DataConsumer.addQueueData(queueData, new QueueExecutor() {
			@Override
			public void execute(QueueData queueData) {
				System.out.println(queueData);
				throw new RuntimeException("hahahahahahahahahaha");
			}
		});
		
		return "aaaaa";
	}
	
}
