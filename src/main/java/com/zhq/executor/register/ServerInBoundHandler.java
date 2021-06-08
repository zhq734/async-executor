package com.zhq.executor.register;

/**
 * @author: zhenghq
 * @date: 2021/2/5
 * @version: 1.0.0
 */
public interface ServerInBoundHandler {
	
	/**
	 * 执行
	 * @param var1
	 * @param var2
	 */
	void handle(RequestHook var1, ResponseHook var2);
}
