# async-executor
异步执行器 采用队列的方式，异步执行数据，提高接口的执行速度

# demo
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
