package com.zhq.executor.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * 带有效期map 简单实现 实现了基本的方法
 *
 * @author: zhenghq
 * @date: 2020/2/26
 * @version: 1.0.0
 */
public class ExpiryMap<K, V> extends HashMap<K, V> {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * default expiry time 2m
	 */
	private long expiry = 1000 * 60 * 2;
	
	private HashMap<K, Long> expiryMap = new HashMap<>();
	
	public ExpiryMap() {
		super();
	}
	
	public ExpiryMap(long defaultExpiryTime) {
		this(1 << 4, defaultExpiryTime);
	}
	
	public ExpiryMap(int initialCapacity, long defaultExpiryTime) {
		super(initialCapacity);
		this.expiry = defaultExpiryTime;
	}
	
	@Override
	public V put(K key, V value) {
		expiryMap.put(key, System.currentTimeMillis() + expiry);
		return super.put(key, value);
	}
	
	@Override
	public boolean containsKey(Object key) {
		return !checkExpiry(key, true) && super.containsKey(key);
	}
	
	/**
	 * @param key
	 * @param value
	 * @param expiryTime 键值对有效期 毫秒
	 * @return
	 */
	public V put(K key, V value, long expiryTime) {
		expiryMap.put(key, System.currentTimeMillis() + expiryTime);
		return super.put(key, value);
	}
	
	@Override
	public int size() {
		return entrySet().size();
	}
	
	@Override
	public boolean isEmpty() {
		return entrySet().size() == 0;
	}
	
	@Override
	public boolean containsValue(Object value) {
		if (value == null) return Boolean.FALSE;
		Set<Entry<K, V>> set = super.entrySet();
		Iterator<Entry<K, V>> iterator = set.iterator();
		while (iterator.hasNext()) {
			Entry<K, V> entry = iterator.next();
			if (value.equals(entry.getValue())) {
				if (checkExpiry(entry.getKey(), false)) {
					iterator.remove();
					return Boolean.FALSE;
				} else return Boolean.TRUE;
			}
		}
		return Boolean.FALSE;
	}
	
	@Override
	public Collection<V> values() {
		
		Collection<V> values = super.values();
		
		if (values == null || values.size() < 1) return values;
		
		Iterator<V> iterator = values.iterator();
		
		while (iterator.hasNext()) {
			V next = iterator.next();
			if (!containsValue(next)) iterator.remove();
		}
		return values;
	}
	
	@Override
	public V get(Object key) {
		if (key == null)
			return null;
		if (checkExpiry(key, true))
			return null;
		return super.get(key);
	}
	
	/**
	 * @param key
	 * @return null:不存在或key为null -1:过期  存在且没过期返回value 因为过期的不是实时删除，所以稍微有点作用
	 * @Description: 是否过期
	 */
	public Object isInvalid(Object key) {
		if (key == null)
			return null;
		if (!expiryMap.containsKey(key)) {
			return null;
		}
		long expiryTime = expiryMap.get(key);
		
		boolean flag = System.currentTimeMillis() > expiryTime;
		
		if (flag) {
			super.remove(key);
			expiryMap.remove(key);
			return -1;
		}
		return super.get(key);
	}
	
	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		for (Entry<? extends K, ? extends V> e : m.entrySet())
			expiryMap.put(e.getKey(), System.currentTimeMillis() + expiry);
		super.putAll(m);
	}
	
	@Override
	public Set<Entry<K, V>> entrySet() {
		Set<Entry<K, V>> set = super.entrySet();
		Iterator<Entry<K, V>> iterator = set.iterator();
		while (iterator.hasNext()) {
			Entry<K, V> entry = iterator.next();
			if (checkExpiry(entry.getKey(), false)) iterator.remove();
		}
		
		return set;
	}
	
	/**
	 * @param key
	 * @param isRemoveSuper true super删除
	 * @return
	 * @Description: 是否过期
	 */
	private boolean checkExpiry(Object key, boolean isRemoveSuper) {
		
		if (!expiryMap.containsKey(key)) {
			return Boolean.FALSE;
		}
		long expiryTime = expiryMap.get(key);
		
		boolean flag = System.currentTimeMillis() > expiryTime;
		
		if (flag) {
			if (isRemoveSuper)
				super.remove(key);
			expiryMap.remove(key);
		}
		return flag;
	}
	
	public static void main(String[] args) throws InterruptedException {
		
		ExpiryMap<String, String> map = new ExpiryMap<>(10);
		
		map.put("test", "aaaaaaa");
		map.put("test1", "bbbbbb", 3000);
		
		System.out.println(map.size());
		System.out.println("test: " + map.get("test"));
		System.out.println("test1: " + map.get("test1"));
		
		Thread.sleep(1000);
		
		System.out.println("=================");
		System.out.println(map.size());
		System.out.println("test: " + map.get("test"));
		System.out.println("test1: " + map.get("test1"));
		
		Thread.sleep(3000);
		
		System.out.println("=================");
		System.out.println(map.size());
		System.out.println("test: " + map.get("test"));
		System.out.println("test1: " + map.get("test1"));
		
	}
	
	
}
