package com.zhq.executor.register;

import com.zhq.executor.register.model.ResultDto;
import com.zhq.executor.util.NetworkUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.LastHttpContent;


/**
 * @author: zhenghq
 * @date: 2021/2/5
 * @version: 1.0.0
 */
public class VirtualHttpServer {
	
	EventLoopGroup bossGroup;
	EventLoopGroup workerGroup;
	int port;
	
	public VirtualHttpServer(int port) {
		this.port = port;
	}
	
	public void start(final ServerInBoundHandler serverInBoundHandler) {
		this.bossGroup = new NioEventLoopGroup();
		this.workerGroup = new NioEventLoopGroup();
		ServerBootstrap b = new ServerBootstrap();
		b.group(this.bossGroup, this.workerGroup);
		b.channel(NioServerSocketChannel.class);
		((ServerBootstrap)b.childHandler(new ChannelInitializer<SocketChannel>() {
			protected void initChannel(SocketChannel ch) throws Exception {
				ChannelPipeline pipeline = ch.pipeline();
				pipeline.addLast(new ChannelHandler[]{new HttpResponseEncoder()});
				pipeline.addLast(new ChannelHandler[]{new HttpRequestDecoder()});
				pipeline.addLast(new ChannelHandler[]{new ChannelInboundHandlerAdapter() {
					RequestHook requestHook;
					
					public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
						if (msg instanceof HttpRequest) {
							this.requestHook = new RequestHook((HttpRequest)msg);
						} else {
							if (msg instanceof HttpContent) {
								HttpContent chunk = (HttpContent)msg;
								this.requestHook.consume(chunk);
								if (chunk instanceof LastHttpContent) {
									serverInBoundHandler.handle(this.requestHook, new ResponseHook(ctx));
									this.requestHook.releaseAll();
									ctx.close();
								}
							}
							
						}
					}
					
					public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
						ctx.flush();
					}
					
					public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
						cause.printStackTrace();
						ctx.close();
					}
				}});
			}
		}).option(ChannelOption.SO_BACKLOG, 128)).childOption(ChannelOption.SO_KEEPALIVE, true);
		
		try {
			b.bind(this.port).sync();
		} catch (InterruptedException var4) {
			var4.printStackTrace();
		}
		
	}
	
	public void stop() {
		this.bossGroup.shutdownGracefully();
		this.workerGroup.shutdownGracefully();
	}
	
	public static void main(String[] args) {
		final int[] count = new int[]{0};
		
//		int port = NetworkUtil.getVaildPort("localhost", 20789);
		int port = NetworkUtil.getVaildPort("localhost", 20790);
		VirtualHttpServer server = new VirtualHttpServer(port);
		server.start(new ServerInBoundHandler() {
			public void handle(RequestHook requestHook, ResponseHook responseHook) {
				System.out.println(requestHook.getMethod());
				System.out.println(requestHook.getUri());
				System.out.println(requestHook.getRequestParams());
				
				responseHook.setBody(ResultDto.success()).response();
				count[0] = ++count[0];
				System.out.println("--------------------------------->" + count[0]);
			}
		});
		System.out.println("started!");
	}
	
	
}
