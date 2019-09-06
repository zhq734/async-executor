package com.zhq.executor.core;

import com.google.common.primitives.UnsignedInteger;

/**
 * @author: zhenghq
 * @date: 2019/9/5
 * @version: 1.0.0
 */
public class QueueContextSeqNumUtil {
	
	private static UnsignedInteger seqNum = UnsignedInteger.ZERO;
	
	public static synchronized UnsignedInteger getSeqNum() {
		UnsignedInteger temp = seqNum;
		seqNum = seqNum.plus(UnsignedInteger.ONE);
		
		return temp;
	}
	
	
}
