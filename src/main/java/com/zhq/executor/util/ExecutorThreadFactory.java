package com.zhq.executor.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: zhenghq
 * @date: 2020/7/2
 * @version: 1.0.0
 */
public class ExecutorThreadFactory implements ThreadFactory {
	
	private final AtomicInteger poolNumber = new AtomicInteger(1);
	
	private final ThreadGroup threadGroup;
	
	private final AtomicInteger threadNumber = new AtomicInteger(1);
	
	public final String namePrefix;
	
	public ExecutorThreadFactory(String name) {
		SecurityManager s = System.getSecurityManager();
		threadGroup = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
		if (null==name || "".equals(name.trim())){
			name = "pool";
		}
		namePrefix = name + "-" + poolNumber.getAndIncrement() + "-thread-";
	}
	
	@Override
	public Thread newThread(Runnable r) {
		Thread t = new Thread(threadGroup, r, namePrefix + getAndIncrement(), 0);
		if (t.isDaemon())
			t.setDaemon(false);
		if (t.getPriority() != Thread.NORM_PRIORITY)
			t.setPriority(Thread.NORM_PRIORITY);
		
		return t;
	}
	
	private int getAndIncrement() {
		int currentNum = threadNumber.getAndIncrement();
		
		if (currentNum < 0) {
			threadNumber.set(1);
			return 1;
		}
		
		return currentNum;
	}
}
