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
     

# 项目上如何使用
    1、maven: 添加依赖包
    <dependency>
      <groupId>com.zhq.executor</groupId>
      <artifactId>async-executor</artifactId>
      <version>1.0.1</version>
    </dependency>
    
    2、springboot 上在Application.java上增加该路径的扫描
    @ComponentScan(basePackages = {...., ...., "com.zhq.executor"})
    
    3、springMVC 上则在xml配置文件中添加扫描路径
    <context:component-scan base-package="com.zhq.executor"></context:component-scan>
    
    通过以上方式即可以直接在您的代码上使用该异步处理器的功能
     
    
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
