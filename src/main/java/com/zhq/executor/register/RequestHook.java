package com.zhq.executor.register;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author: zhenghq
 * @date: 2021/2/5
 * @version: 1.0.0
 */
public class RequestHook {
	
	HttpRequest request;
	HttpPostRequestDecoder decoder;
	private HttpHeaders headers;
	private Map<String, List<String>> queryParams;
	ByteBuf httpContentBuf = Unpooled.buffer();
	
	public RequestHook(HttpRequest request) {
		this.request = request;
		
		try {
			this.decoder = new HttpPostRequestDecoder(new DefaultHttpDataFactory(16384L), request);
		} catch (Exception var3) {
			;
		}
		
	}
	
	public HttpMethod getMethod() {
		return this.request.method();
	}
	
	public String getUri() {
		return this.request.uri().split("\\?")[0];
	}
	
	private HttpHeaders getHeaders() {
		if (this.headers != null) {
			return this.headers;
		} else {
			this.headers = this.request.headers();
			return this.headers;
		}
	}
	
	public Set<String> getHeaderNames() {
		return this.getHeaders().names();
	}
	
	public String getHeader(String headerName) {
		return this.getHeaders().get(headerName);
	}
	
	private Map<String, List<String>> getQueryParams() {
		if (this.queryParams != null) {
			return this.queryParams;
		} else {
			this.queryParams = (new QueryStringDecoder(this.request.uri(), Charset.defaultCharset())).parameters();
			return this.queryParams;
		}
	}
	
	public Set<String> getQueryParamNames() {
		getQueryParams();
		return this.queryParams.keySet();
	}
	
	public List<String> getParams(String name) {
		return (List)this.queryParams.get(name);
	}
	
	public String getParam(String name) {
		List<String> values = this.getParams(name);
		return values.size() >= 1 ? (String)values.get(0) : null;
	}
	
	void consume(HttpContent httpContent) {
		if (this.decoder != null) {
			this.decoder.offer(httpContent);
		}
		
		httpContent.content().resetReaderIndex();
		this.httpContentBuf.writeBytes(httpContent.content());
	}
	
	public ByteBuf getBody() {
		return this.httpContentBuf;
	}
	
	public String getBodyAsString(Charset charset) {
		return this.httpContentBuf.toString(charset);
	}
	
	public List<InterfaceHttpData> getBodyAsPostParams() {
		return this.decoder.getBodyHttpDatas();
	}
	
	public Map<String, String> getBodyAsStringedPostParams() {
		Map<String, String> params = new HashMap();
		Iterator iterator = this.decoder.getBodyHttpDatas().iterator();
		
		while(iterator.hasNext()) {
			InterfaceHttpData data = (InterfaceHttpData)iterator.next();
			if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute) {
				Attribute attr = (Attribute)data;
				
				try {
					params.put(attr.getName(), attr.getValue());
				} catch (IOException var6) {
					var6.printStackTrace();
				}
			}
		}
		
		return params;
	}
	
	public Map<String, String> getRequestParams() {
		Map<String, String> params = getBodyAsStringedPostParams();
		
		Set<String> queryNames = getQueryParamNames();
		queryNames.stream().forEach(p -> {
			params.put(p, getParam(p));
		});
		
		return params;
	}
	
	public void releaseAll() {
		if (this.queryParams != null) {
			this.queryParams.clear();
		}
		
		if (this.httpContentBuf != null) {
			this.httpContentBuf.release();
		}
		
		if (this.headers != null) {
			this.headers.clear();
		}
		
	}
	
}
