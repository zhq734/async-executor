# async-executor
异步执行器 采用队列的方式，异步执行数据，提高接口的执行速度

# desc
     
     代码提供默认的执行器：DefaultQueueExecutor 和 默认的异常回调函数：DefaultExecuteFailCallback
     用户可以自定义扩展：
     具体参考以下代码：
      
     @Slf4j
     public class DefaultQueueExecutor implements QueueExecutor {
	@Override
	public void execute(QueueData queueData) {
	    log.info("DefaultQueueExecutor executor: queueData={}", queueData);

	}
     }
     
     
     @Slf4j
     public class DefaultExecuteFailCallback implements ExecuteFailCallback {
	@Override
	public void callback(QueueData queueData, Exception e) {
	    log.error("DefaultExecuteFailCallback callback : queueData={}, msg={}, ", queueData, e.getMessage(), e);
	}
     }
     
     
    
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
