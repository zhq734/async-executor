package com.zhq.executor.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * @author: zhenghq
 * @date: 2021/3/4
 * @version: 1.0.0
 */
public class NetworkUtil {
	
	/**
	 * 获取当前服务ip
	 * @return
	 */
	public static String getLocalHost() {
		String ip = null;
		try {
			ip = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		return ip;
	}
	
	
	/***
	 * 测试主机Host的port端口是否被使用
	 * @param host
	 * @param port
	 * @throws UnknownHostException
	 */
	public static boolean isPortUsing(String host, int port) throws UnknownHostException{
		boolean flag = false;
		InetAddress address = InetAddress.getByName(host);
		try(Socket socket = new Socket(address, port);) {
			//建立一个Socket连接
			flag = true;
		} catch (IOException e) {
		
		}
		return flag;
	}
	
	/**
	 * 获取一个有效的端口
	 * @param port
	 * @param startPort
	 * @return
	 */
	public static int getVaildPort(String port, int startPort) {
		try {
			boolean isUsing = isPortUsing(port, startPort);
			
			while (isUsing) {
				isUsing = isPortUsing(port, ++startPort);
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		return startPort;
	}
	
}
