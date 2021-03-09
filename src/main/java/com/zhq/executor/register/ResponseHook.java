package com.zhq.executor.register;

import com.alibaba.fastjson.JSON;
import com.zhq.executor.register.model.ResultDto;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

import java.nio.charset.Charset;

/**
 * @author: zhenghq
 * @date: 2021/2/5
 * @version: 1.0.0
 */
public class ResponseHook {
	
	ChannelHandlerContext ctx;
	DefaultFullHttpResponse response;
	
	public ResponseHook(ChannelHandlerContext ctx) {
		this.response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
		this.ctx = ctx;
	}
	
	public ResponseHook setStatus(HttpResponseStatus status) {
		this.response.setStatus(status);
		return this;
	}
	
	public ResponseHook getContentType(String contentType) {
		return this.setHeader("Content-Type", contentType);
	}
	
	public ResponseHook setHeader(String name, String value) {
		this.response.headers().set(name, value);
		return this;
	}
	
	public ResponseHook setBody(ResultDto resultDto) {
		return this.setBody(Unpooled.wrappedBuffer(JSON.toJSONString(resultDto).getBytes()));
	}
	
	public ResponseHook setBody(String body) {
		return this.setBody(Unpooled.wrappedBuffer(body.getBytes()));
	}
	
	public ResponseHook setBody(String body, Charset charset) {
		return this.setBody(Unpooled.wrappedBuffer(body.getBytes(charset)));
	}
	
	public ResponseHook setBody(byte[] body) {
		return this.setBody(Unpooled.wrappedBuffer(body));
	}
	
	public ResponseHook setBody(ByteBuf body) {
		this.response = (DefaultFullHttpResponse) this.response.replace(body);
		return this;
	}
	
	public void response() {
		this.response.headers().set("Content-Length", this.response.content().readableBytes());
		this.ctx.write(this.response);
		this.ctx.flush();
	}
	
}
