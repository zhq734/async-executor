package com.zhq.executor.hook;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author: zhenghq
 * @date: 2019/9/9
 * @version: 1.0.0
 */
public class TestShutdownHook extends Thread {
	
	private static final Logger logger = LoggerFactory.getLogger(TestShutdownHook.class);
	private static final TestShutdownHook testShutdownHook = new TestShutdownHook("TestShutdownHook");
	private final AtomicBoolean registered = new AtomicBoolean(false);
	private final AtomicBoolean destroyed = new AtomicBoolean(false);
//	private Set<ShutdownCallbackFunc> shutdownCallbackFuncList = new ConcurrentSkipListSet();
	private Set<ShutdownCallbackFunc> shutdownCallbackFuncList = new HashSet<>();
	
	private TestShutdownHook(String name) {
		super(name);
	}
	
	public static TestShutdownHook getTestShutdownHook() {
		return testShutdownHook;
	}
	
	public void run() {
		if (logger.isInfoEnabled()) {
			logger.info("Run shutdown hook now.");
		}
		
		this.doDestroy();
	}
	
	
	
	public void register() {
		if (!registered.get() && registered.compareAndSet(false, true)) {
			Runtime.getRuntime().addShutdownHook(getTestShutdownHook());
		}
	}
	
	public void unregister() {
		if (registered.get() && registered.compareAndSet(true, false)) {
			Runtime.getRuntime().removeShutdownHook(getTestShutdownHook());
		}
	}
	
	public void doDestroy() {
		if (destroyed.compareAndSet(false, true)) {
			shutdownCallbackFuncList.forEach((item) -> {
				item.call();
			});
		}
	}
	
	public void addDestroyCallback(TestShutdownHook.ShutdownCallbackFunc func) {
		shutdownCallbackFuncList.add(func);
	}
	
	public interface ShutdownCallbackFunc {
		void call();
	}
}
