package com.example.demo;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpVersion;
import java.net.InetSocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@SpringBootApplication
public class DemoApplication {

	private int listenThreads = 2;
	private int workerThreads = 16;
	private int listenPort = 80;
	private NioEventLoopGroup bossGroup;
	private NioEventLoopGroup workerGroup;
	private ChannelFuture sync;

	public DemoApplication(final int listenPort,
			final int listenThreads,
			final int workerThreads) {
		this.listenThreads = listenThreads;
		this.workerThreads = workerThreads;
		this.listenPort = listenPort;
	}

	public static void main(String[] args) {
		//SpringApplication.run(DemoApplication.class, args);
		new DemoApplication(8181, 2, 16).openServer(new ClientToProxyInitializer());
	}

	public void openServer(ChannelHandler handler) {

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				shutdown();
			}
		});

		bossGroup = new NioEventLoopGroup(listenThreads);
		workerGroup = new NioEventLoopGroup(workerThreads);
		ServerBootstrap bootStrap = new ServerBootstrap();
		bootStrap
				.group(bossGroup, workerGroup)
				.channel(NioServerSocketChannel.class)
				.childHandler(handler)
				.option(ChannelOption.SO_BACKLOG, 128)
				.childOption(ChannelOption.SO_KEEPALIVE, true);
		try {
			sync = bootStrap.bind(listenPort).sync();
			sync.channel().closeFuture().sync();
		} catch (InterruptedException ex) {
			Logger.getLogger(DemoApplication.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}

	}

	public void shutdown() {
		workerGroup.shutdownGracefully();
		bossGroup.shutdownGracefully();

		try {
			sync.channel().closeFuture().sync();
		} catch (InterruptedException e) {
		}
	}

}
