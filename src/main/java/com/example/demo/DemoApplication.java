package com.example.demo;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpVersion;
import java.net.InetSocketAddress;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		//SpringApplication.run(DemoApplication.class, args);
		start();
	}
	
	public static void start() {
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap().group(group)
                    .channel(NioSocketChannel.class)
					.remoteAddress(new InetSocketAddress("localhost", 8080))
					.handler(new ClientAdapterInitializer());		
            ChannelFuture channelFuture = bootstrap.connect().sync();
			// Prepare the HTTP request.
              HttpRequest request = new DefaultFullHttpRequest(
                      HttpVersion.HTTP_1_1, HttpMethod.GET, "/resources/configuration/all");
              request.headers().set(HttpHeaderNames.HOST, "localhost");
              request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
              request.headers().set(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.GZIP);
  
			Channel channel = channelFuture.channel();
			channel.writeAndFlush(request);
			System.out.println("I write");
			channel.closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }	
	
}
